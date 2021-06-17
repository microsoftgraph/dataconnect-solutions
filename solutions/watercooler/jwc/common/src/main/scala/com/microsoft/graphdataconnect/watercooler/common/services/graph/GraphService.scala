/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.graph

import java.io.InputStream
import java.util

import com.azure.identity.{ClientSecretCredential, ClientSecretCredentialBuilder}
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.{Gson, GsonBuilder, JsonObject}
import com.microsoft.graph.authentication.TokenCredentialAuthProvider
import com.microsoft.graph.callrecords.models.CallRecord
import com.microsoft.graph.models.{Call, CallDirection, ChatInfo, Event, Identity, IdentitySet, MediaConfig, MediaInfo, MeetingInfo, Modality, OnlineMeeting, OrganizerMeetingInfo, ServiceHostedMediaConfig, User}
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.graphdataconnect.watercooler.common.dto.UserDetailsDTO
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import okhttp3.Request

import collection.JavaConverters._

class GraphService(val graphClient: GraphServiceClient[Request]) {

  private val DEFAULT_MAX_PICTURE_SIZE: String = "120x120"

  val builder = new GsonBuilder()
  builder.addDeserializationExclusionStrategy(new SuperclassExclusionStrategy)
  builder.addSerializationExclusionStrategy(new SuperclassExclusionStrategy)
  val gson: Gson = builder.create()

  def getUserId(userMail: String): String = {
    graphClient.users(userMail).buildRequest().get().id
  }

  def getCallRecord(id: String): CallRecord = {
    graphClient.communications.callRecords(id).buildRequest().expand("sessions($expand=segments)").get()
  }

  def getMeetingDetails(meetingId: String, meetingOrganizerId: String): Unit = {
//    graphClient.customRequest(s"/users/$meetingOrganizerId/onlineMeetings/$meetingId").buildRequest().get()


    val call: Call = new Call();
    call.direction = CallDirection.OUTGOING
    call.callbackUri = "https://bot.contoso.com/callback"

    val modalities: util.List[Modality] = new util.LinkedList[Modality]()
    modalities.add(Modality.AUDIO)
    call.requestedModalities = modalities

    val mediaConfig: ServiceHostedMediaConfig = new ServiceHostedMediaConfig()
    val preFetchMedias: util.List[MediaInfo] = new util.LinkedList[MediaInfo]()
    val preFetchMedia: MediaInfo = new MediaInfo()
//    preFetchMedia.resourceId
//    preFetchMedias.add(preFetchMedia)
    mediaConfig.preFetchMedia = preFetchMedias
    call.mediaConfig = mediaConfig

    val chatInfo: ChatInfo = new ChatInfo()
    chatInfo.threadId = "19:meeting_MmM5NTFiZWUtYzljOS00MmVlLTk0N2ItMjk3OGMxODk1NWE0@thread.v2"
    chatInfo.messageId = "0"
    call.chatInfo = chatInfo

    val meetingInfo: OrganizerMeetingInfo = new OrganizerMeetingInfo()
    val identitySet: IdentitySet = new IdentitySet
    val identity: Identity = new Identity
    identity.id = meetingOrganizerId
    identity.displayName = "Watercooler"
    identitySet.user = identity

    meetingInfo.organizer =  identitySet
    call.meetingInfo = meetingInfo

    call.tenantId = "d26bf63a-a52f-436a-bf3b-531b1e378694"


    graphClient.customRequest(s"/communications/calls", classOf[Call]).buildRequest().post(call)
  }

  def getUsers: List[User] = {
    var users = List.empty[User]
    val req = graphClient.users().buildRequest().get
    var currentPage = req.getCurrentPage
    while(currentPage != null) {

      users = currentPage.asScala.toList ++ users
      val nextPage = req.getNextPage
      if(null != nextPage) {
        currentPage = nextPage.buildRequest().get().getCurrentPage
      } else {
        currentPage = null
      }
    }
    users
  }

  def readUserProfilePicture(mail: String): Array[Byte] = {
    val sizedProfilePicture: Array[Byte] = getSizedProfilePicture(mail)
    if (sizedProfilePicture.nonEmpty) {
      sizedProfilePicture
    } else {
      getDefaultSizedProfilePicture(mail)
    }
  }

  def getUserInfo(id: String): UserDetailsDTO = {
    val userBasicInformation = graphClient.customRequest("/users/" + id + "/", classOf[LinkedTreeMap[String, Object]]).buildRequest().get()

    val displayName = getUtfUnquoted(userBasicInformation.get("displayName").asInstanceOf[String])
    val aboutMe: String = getUtfUnquoted(getUserProperty(id, "aboutMe"))
    val jobTitle = getUtfUnquoted(userBasicInformation.get("jobTitle").asInstanceOf[String])
    val companyName: String = getUtfUnquoted(getUserProperty(id, "companyName"))
    val department: String = getUtfUnquoted(getUserProperty(id, "department"))
    val country: String = getUtfUnquoted(getUserProperty(id, "country"))
    val officeLocation = getUtfUnquoted(userBasicInformation.get("officeLocation").asInstanceOf[String])
    val city: String = getUtfUnquoted(getUserProperty(id, "city"))
    val state: String = getUtfUnquoted(getUserProperty(id, "state"))

    val reportsTo: String = getUtfUnquoted(getUserProperty(id, "manager", "displayName"))

    val skills: String = getSkills(id)
    val responsibilities: String = getResponsibilities(id)

    UserDetailsDTO(
      displayName = displayName,
      aboutMe = aboutMe,
      jobTitle = jobTitle,
      companyName = companyName,
      department = department,
      country = country,
      officeLocation = officeLocation,
      city = city,
      state = state,
      skills = skills,
      responsibilities = responsibilities,
      engagement = reportsTo
    )
  }

  def getSkills(id: String): String = {
    val skills: util.ArrayList[String] = graphClient.customRequest("/users/" + id + "/skills", classOf[LinkedTreeMap[String, Object]])
      .buildRequest().get()
      .get("value").asInstanceOf[util.ArrayList[String]]
    skills.asScala.toList.mkString(",")
  }

  def getResponsibilities(id: String): String = {
    val responsibilities: util.ArrayList[String] = graphClient.customRequest("/users/" + id + "/responsibilities", classOf[LinkedTreeMap[String, Object]])
      .buildRequest().get()
      .get("value").asInstanceOf[util.ArrayList[String]]
    responsibilities.asScala.toList.mkString(",")
  }

  private def getDefaultSizedProfilePicture(mail: String): Array[Byte] = {
    try {
      val stream: InputStream = graphClient.users(mail).photo().content().buildRequest().get()
      if (null == stream) {
        Array.emptyByteArray
      } else {
        Stream.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray
      }
    } catch {
      case _: Exception =>
        Array.emptyByteArray
    }
  }

  private def getSizedProfilePicture(mail: String, profilePictureSize: String = DEFAULT_MAX_PICTURE_SIZE): Array[Byte] = {
    try {
      val stream: InputStream = graphClient.users(mail).photos(profilePictureSize).content().buildRequest().get()
      if (null == stream) {
        Array.emptyByteArray
      } else {
        Stream.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray
      }
    } catch {
      case _: Exception =>
        Array.emptyByteArray
    }
  }

  def getUtfUnquoted(input: String): String = {
    if (null == input) {
      ""
    } else {
      new String(input.getBytes("UTF-8"), "UTF-8")
        .replace("'", "''") // 2x single quote: escaping for SQL queries
        .trim
        .replaceAll("\\s{2,}", " ") // 2+ white spaces becomes one space
    }
  }

  def getUserProperty(userId: String, fieldName: String, propertyName: String = "value"): String = {
    try {
      val value = graphClient.customRequest("/users/" + userId + "/" + fieldName, classOf[util.LinkedHashMap[String, Object]]).buildRequest().get()
        .get(propertyName)
      if (null != value) {
        value.asInstanceOf[String]
      } else {
        ""
      }
    } catch {
      case e: Exception =>
        ""
    }
  }

  /*
   * startDay is inclusive - 'greater or equal' is used
   * endDay is exclusive - 'less than' is used
   */
  def getUserCalendarData(userId: String, startDay: String, endDay: String): Tuple2[List[LinkedTreeMap[String, Object]], String] = {
    //    val urlFilterParam = URLEncoder.encode(s"startDateTime=$startDay&endDateTime=$endDay", StandardCharsets.UTF_8.toString)
    try {
      val data = graphClient.customRequest(s"/users/$userId/calendar/calendarView?startDateTime=$startDay&endDateTime=$endDay", classOf[LinkedTreeMap[String, Object]]).buildRequest().get()
      val value = data.get("value").asInstanceOf[java.util.ArrayList[LinkedTreeMap[String, Object]]]
      val nextUrl = getUtfUnquoted(data.get("@odata.nextLink").asInstanceOf[String])

      if (nextUrl.nonEmpty) {
        (value.asScala.toList, "/" + nextUrl.substring("https://graph.microsoft.com/v1.0/".length))
      } else {
        (value.asScala.toList, "")
      }
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        (List.empty, "")
    }
  }

  def getUserCalendarDataUrl(url: String): Tuple2[List[LinkedTreeMap[String, Object]], String] = {
    try {
      val data = graphClient.customRequest(url, classOf[LinkedTreeMap[String, Object]]).buildRequest().get()
      val value = data.get("value").asInstanceOf[java.util.ArrayList[LinkedTreeMap[String, Object]]]
      val nextUrl = getUtfUnquoted(data.get("@odata.nextLink").asInstanceOf[String])

      if (nextUrl.nonEmpty) {
        (value.asScala.toList, "/" + nextUrl.substring("https://graph.microsoft.com/v1.0/".length))
      } else {
        (value.asScala.toList, "")
      }
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        (List.empty, "")
    }
  }

  def createEvent(meetingOrganizerId: String, eventPayload: Event): Unit = {
    val eventGsonObj = gson.toJsonTree(eventPayload).asInstanceOf[JsonObject]
    graphClient.customRequest(s"/users/$meetingOrganizerId/calendar/events").buildRequest().post(eventGsonObj)
  }

  def createTeamsEvent(meetingOrganizerId: String, payloadMeeting: OnlineMeeting): OnlineMeeting = {
    graphClient.customRequest(s"/users/$meetingOrganizerId/onlineMeetings", classOf[OnlineMeeting]).buildRequest().post(payloadMeeting)
  }

}

object GraphService {
  def apply(graphApiAppAccessInfo: GraphApiAppAccessInfo): GraphService = {
    val clientSecretCredential: ClientSecretCredential = new ClientSecretCredentialBuilder()
      .clientId(graphApiAppAccessInfo.clientId)
      .clientSecret(graphApiAppAccessInfo.clientSecret)
      .tenantId(graphApiAppAccessInfo.directoryId).build();

    val tokenCredentialAuthProvider: TokenCredentialAuthProvider = new TokenCredentialAuthProvider(graphApiAppAccessInfo.scope, clientSecretCredential)
    val graphClient: GraphServiceClient[Request] = GraphServiceClient.builder.authenticationProvider(tokenCredentialAuthProvider).buildClient()

    new GraphService(graphClient)
  }
}
