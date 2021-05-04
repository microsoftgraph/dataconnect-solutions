import sys
import json

from pyspark import SparkContext, SparkConf
from pyspark.sql import SQLContext
from pyspark.sql.functions import explode
from pyspark.sql.functions import col
from pyspark.sql.functions import lit
from pyspark.sql.types import StringType

# constants
inputFilePath = "abfss://{}@{}.dfs.core.windows.net/{}/{}/????????\-*"
outputFilePath = "https://{}.dfs.core.windows.net/{}/{}"
metadataFilePath = "abfss://{}@{}.dfs.core.windows.net/{}/model.json"
dataSourceColumnName = "sourceOfOrigin"
dataSourceColumnValue = "Office365"

def writeDataframeToCdmFormat(dataframe, outputPath, cdmModelName, cdmEntityName, appID, appKey, tenantId):
    """
    This method write dataframe in CDM format to the outputPath

    :param dataframe: dataframe to write
    :type dataframe: object
    :param outputPath: output path to write data
    :type outputPath: string
    :param cdmModelName: model name in CDM metadata
    :type cdmModelName: string
    :param cdmEntityName: entity name in CDM metadata
    :type cdmEntityName: string
    :param appID: AAD application Id to access storage account
    :type appID: string
    :param appKey: AAD application key to access storage account
    :type appKey: string
    :param tenantId: AAD application tenant Id
    :type tenantId: string
    """
    dataframe.repartition(1).write.format("com.microsoft.cdm") \
      .option("entity", cdmEntityName) \
      .option("appId", appID) \
      .option("appKey", appKey) \
      .option("tenantId", tenantId) \
      .option("cdmFolder", outputPath) \
      .option("cdmModelName", cdmModelName) \
      .save()

def createAppointmentDataframe(eventDf):
    """
    This method create Appointment dataframe for CDM

    :param eventDf: event dataframe
    :type eventDf: object
    """
    appointmentColumns = [
      "body.content as body","createdDateTime", "end.datetime as end","hasAttachments",
      "iCalUId","id as appointmentId","importance","isAllDay","isCancelled","isReminderOn","lastModifiedDateTime",
      "onlineMeetingUrl","originalEndTimeZone","originalStart","originalStartTimeZone","reminderMinutesBeforeStart",
      "responseRequested","sensitivity","seriesMasterId","showAs", "start.datetime as start","subject","type"
      ]

    return eventDf.selectExpr(appointmentColumns).withColumn(dataSourceColumnName, lit(dataSourceColumnValue))

def createEmailDataframe(messageDf):
    """
    This method create Email dataframe for CDM

    :param messageDf: message dataframe
    :type messageDf: object
    """
    emailColumns = [
      "uniqueBody.content as body","conversationId","createdDateTime","hasAttachments","id as emailId","importance","inferenceClassification",
      "internetMessageId","isDeliveryReceiptRequested","isDraft","isRead","isReadReceiptRequested","lastModifiedDateTime",
      "parentFolderId","receivedDateTime","sentDateTime","subject"
      ]

    return messageDf.selectExpr(emailColumns).withColumn(dataSourceColumnName, lit(dataSourceColumnValue))

def createPersonDataframe(managerDf, userDf):
    """
    This method create Person dataframe for CDM

    :param managerDf: manager dataframe
    :type managerDf: object
    :param userDf: user dataframe
    :type userDf: object
    """
    managerColumns = [
      "id as managerId", "puser"
    ]
    userColumns = [
      "birthday","companyName","createdDateTime","department","displayName",
      "givenName","hireDate","id as personId","jobTitle","mail","officeLocation","preferredLanguage","preferredName","surname","usageLocation","userPrincipalName","userType"
    ]
	
    managerDf = managerDf.selectExpr(managerColumns)
    userDf = userDf.selectExpr(userColumns)
    
    # Append manager id to user data
    return userDf.join(managerDf, userDf.personId == managerDf.puser, how='left').drop("puser")

def createAppointmentParticipantsDataframe(eventDf):
    """
    This method create AppointmentParticipant dataframe for CDM

    :param eventDf: event dataframe
    :type eventDf: object
    """
	
    # Get appointment attendees
    attendeesDf = eventDf.select(eventDf.id.alias("appointmentId"),explode("attendees").alias("attendees"))
    if(not attendeesDf.rdd.isEmpty()):
      attendeesDf = attendeesDf.select("appointmentId","attendees.emailAddress.address","attendees.status.response").withColumn("isOrganizer", lit(False))

    # Get appointment organizer   
    organizerDf = eventDf.select(eventDf.id.alias("appointmentId"), "organizer.emailAddress.address").withColumn("response", lit(None).cast(StringType())).withColumn("isOrganizer", lit(True))

    # Merge the attendees and organizer
    participantsDf = organizerDf
    if(not attendeesDf.rdd.isEmpty()):
	    participantsDf = participantsDf.unionByName(attendeesDf)
    
    return participantsDf

def createEmailParticipantsDataframe(messageDf):
    """
    This method create EmailParticipant dataframe for CDM

    :param messageDf: message dataframe
    :type messageDf: object
    """
	
    # Get to recipients in email
    toRecipientsDf = messageDf.select(messageDf.id.alias("emailId"),explode("toRecipients").alias("toRecipients"))
    if(not toRecipientsDf.rdd.isEmpty()):
      toRecipientsDf = toRecipientsDf.select("emailId","toRecipients.emailAddress.address").withColumn("isSender", lit(False)).withColumn("recipientType", lit("To"))

    # Get cc recipients in email
    ccRecipientsDf = messageDf.select(messageDf.id.alias("emailId"),explode("ccRecipients").alias("ccRecipients"))
    if(not ccRecipientsDf.rdd.isEmpty()):
      ccRecipientsDf = ccRecipientsDf.select("emailId","ccRecipients.emailAddress.address").withColumn("isSender", lit(False)).withColumn("recipientType", lit("Cc"))

    # Get bcc recipients in email
    bccRecipientsDf = messageDf.select(messageDf.id.alias("emailId"),explode("bccRecipients").alias("bccRecipients"))
    if(not bccRecipientsDf.rdd.isEmpty()):
      bccRecipientsDf = ccRecipientsDf.select("emailId","bccRecipients.emailAddress.address").withColumn("isSender", lit(False)).withColumn("recipientType", lit("Bcc"))

    # Get sender in email  
    senderDf = messageDf.select(messageDf.id.alias("emailId"),"sender.emailAddress.address").withColumn("isSender", lit(True)).withColumn("recipientType", lit(None).cast(StringType()))

    # Merge to, cc, bcc and sender
    participantsDf = senderDf
    if(not toRecipientsDf.rdd.isEmpty()):
	    participantsDf = participantsDf.unionByName(toRecipientsDf)
    if(not ccRecipientsDf.rdd.isEmpty()):
	    participantsDf = participantsDf.unionByName(ccRecipientsDf)
    if(not bccRecipientsDf.rdd.isEmpty()):
	    participantsDf =participantsDf.unionByName(bccRecipientsDf)
      
    return participantsDf
	
def createEmailAddressDataframe(personDf):
    """
    This method create EmailAddress dataframe for CDM

    :param personDf: person dataframe
    :type personDf: object
    """
    mailDf = personDf.select("personId", personDf.mail.alias("emailAddress")).where(personDf.mail.isNotNull())
    upnDf = personDf.select("personId", personDf.userPrincipalName.alias("emailAddress")).where(personDf.userPrincipalName.isNotNull())
    
    return mailDf.unionByName(upnDf).distinct()

def createPhoneNumberDataframe(userDf):
    """
    This method create PhoneNumber dataframe for CDM

    :param userDf: person dataframe
    :type userDf: object
    """
    phoneNumberType = "phoneNumberType"
    businessPhoneDf = userDf.selectExpr("id as personId", "businessPhones[0] as phoneNumber").where(userDf.businessPhones[0].isNotNull()).withColumn(phoneNumberType, lit("Business"))
    mobilePhoneDf = userDf.selectExpr("id as personId", "mobilePhone as phoneNumber").where(userDf.mobilePhone.isNotNull()).withColumn(phoneNumberType, lit("Mobile"))
    
    return businessPhoneDf.unionByName(mobilePhoneDf)
	
def createPhysicalAddressDataframe(userDf):
    """
    This method create PhoneNumber dataframe for CDM

    :param userDf: person dataframe
    :type userDf: object
    """
    addressColumns = [
      "id as personId","city","country","officeLocation","postalCode","state","streetAddress"
	  ]
    return userDf.selectExpr(addressColumns).where(userDf.country.isNotNull())

def appendExternalUsers(personDf, appointmentParticipantsDf, emailParticipantsDf, sqlContext):
    """
    This method create MailParticipants dataframe for CDM

    :param personDf: Person dataframe
    :type personDf: object
    :param appointmentParticipantsDf: AppointmentParticipants dataframe
    :type appointmentParticipantsDf: object
    :param emailParticipantsDf: MailParticipants dataframe
    :type emailParticipantsDf: object
    :param sqlContext: SQL Context
    :type sqlContext: object
    """
    emptyDf = sqlContext.createDataFrame([], personDf.schema).drop("personId")

    # Get all email address from appointments and emails
    externalUserDf = appointmentParticipantsDf.select(appointmentParticipantsDf.address.alias("mail")).distinct().unionByName(emailParticipantsDf.select(emailParticipantsDf.address.alias("mail")).distinct())

    # Remove known mail and userPrincipalName in AAD user data
    externalUserDf = externalUserDf.subtract(personDf.select("mail"))
    externalUserDf = externalUserDf.subtract(personDf.selectExpr("userPrincipalName as mail"))

    # Construct new pserson data for unknown email address
    externalUserDf = externalUserDf.join(emptyDf, "mail", how='left')
    externalUserDf = externalUserDf.withColumn("personId", externalUserDf.mail)
  
    return externalUserDf.unionByName(personDf)

def updatePersonId(emailAddressDf, participantsDf):
    """
    This method update participants dataframe with PersonId for CDM

    :param emailAddressDf: emailAddressDf dataframe
    :type emailAddressDf: object
    :param participantsDf: Participants dataframe
    :type participantsDf: object
    """
    return participantsDf.join(emailAddressDf.select("emailAddress","personId"), participantsDf.address == emailAddressDf.emailAddress, how='left').drop("address","emailAddress")

if __name__ == "__main__":

  # create context with Spark configuration
  conf = SparkConf().setAppName("JsonToCdm")
  sc = SparkContext(conf=conf)
  sqlContext = SQLContext(sc)
  
  # read input parameters
  extrationFS = sys.argv[1]
  storageAccountName = sys.argv[2]
  appID = sys.argv[3]
  appKey = sys.argv[4]
  tenantId = sys.argv[5]
  cdmDataFS = sys.argv[6]
  cdmModelName = sys.argv[7]
  pipelineId = sys.argv[8]
  eventDataset = sys.argv[9]
  managerDataset = sys.argv[10]
  messageDataset = sys.argv[11]
  userDataset = sys.argv[12]

   # read the json file
  eventDf = sqlContext.read.json(inputFilePath.format(extrationFS, storageAccountName, pipelineId, eventDataset))
  managerDf = sqlContext.read.json(inputFilePath.format(extrationFS, storageAccountName, pipelineId, managerDataset))
  messageDf = sqlContext.read.json(inputFilePath.format(extrationFS, storageAccountName, pipelineId, messageDataset))
  userDf = sqlContext.read.json(inputFilePath.format(extrationFS, storageAccountName, pipelineId, userDataset))

  # schmea transformation for appointment
  appointmentDf = createAppointmentDataframe(eventDf)
  appointmentDf.show()
  
  # schmea transformation for email
  emailDf = createEmailDataframe(messageDf)
  emailDf.show()

  # schmea transformation for phoneNumber
  phoneNumberDf = createPhoneNumberDataframe(userDf)
  phoneNumberDf.show()
  
  # schmea transformation for physicalAddress
  physicalAddressDf = createPhysicalAddressDataframe(userDf)
  physicalAddressDf.show()

  # schmea transformation for person
  personDf = createPersonDataframe(managerDf, userDf)
  appointmentParticipantDf = createAppointmentParticipantsDataframe(eventDf)
  emailParticipantDf = createEmailParticipantsDataframe(messageDf)
  personDf = appendExternalUsers(personDf, appointmentParticipantDf, emailParticipantDf, sqlContext)
  personDf.show()
  
  # schmea transformation for emailAddress
  emailAddressDf = createEmailAddressDataframe(personDf)
  emailAddressDf.show()

  # schmea transformation for appointmentParticipant
 
  appointmentParticipantDf = updatePersonId(emailAddressDf, appointmentParticipantDf)
  appointmentParticipantDf.show()

  # schmea transformation for emailParticipant
  emailParticipantDf = updatePersonId(emailAddressDf, emailParticipantDf)
  emailParticipantDf.show()
  
  # Write transformed data into CDM format
  outputLocation = outputFilePath.format(storageAccountName, cdmDataFS, pipelineId)

  writeDataframeToCdmFormat(appointmentDf, outputLocation, cdmModelName, "Appointment", appID, appKey, tenantId)
  writeDataframeToCdmFormat(appointmentParticipantDf, outputLocation, cdmModelName, "AppointmentParticipant", appID, appKey, tenantId)
  writeDataframeToCdmFormat(personDf, outputLocation, cdmModelName, "Person", appID, appKey, tenantId)
  writeDataframeToCdmFormat(emailParticipantDf, outputLocation, cdmModelName, "EmailParticipant", appID, appKey, tenantId)
  writeDataframeToCdmFormat(emailDf, outputLocation, cdmModelName, "Email", appID, appKey, tenantId)
  writeDataframeToCdmFormat(phoneNumberDf, outputLocation, cdmModelName, "PhoneNumber", appID, appKey, tenantId)
  writeDataframeToCdmFormat(physicalAddressDf, outputLocation, cdmModelName, "PhysicalAddress", appID, appKey, tenantId)
  writeDataframeToCdmFormat(emailAddressDf, outputLocation, cdmModelName, "EmailAddress", appID, appKey, tenantId)