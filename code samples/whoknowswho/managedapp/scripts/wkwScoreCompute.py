import sys
from pyspark.sql import SparkSession
from pyspark.sql.types import *

def main():
    spark = SparkSession.builder.appName('wkw_scores').getOrCreate()

    messages_loc = sys.argv[1]
    messages = spark.read.json(messages_loc + "/[^m]*", mode='DROPMALFORMED')

    newUserComboPlusScore = {}
    for message in messages.rdd.collect():
        try:
            sender = message.sender.emailAddress.address
            recipients = []
            for toRecepient in message.toRecipients:
                recipients.append(toRecepient.emailAddress.address)
            for ccRecepient in message.ccRecipients:
                recipients.append(ccRecepient.emailAddress.address)
            for bccRecepient in message.bccRecipients:
                recipients.append(bccRecepient.emailAddress.address)
            for recipient in recipients:
                key = sender + '_' + recipient
                newUserComboPlusScore[key] = newUserComboPlusScore.get(key, 0) + 1
                key = recipient + '_' + sender
                newUserComboPlusScore[key] = newUserComboPlusScore.get(key, 0) + 2
        except:
            pass

    newWkwScores = {}
    for combo in newUserComboPlusScore:
        substrings = combo.split('_')
        if substrings[0] and substrings[1]:
            userScore = {'User': substrings[1], 'Score': newUserComboPlusScore[combo]}
            if substrings[0] in newWkwScores:
                newWkwScores[substrings[0]].append(userScore)
            else:
                newWkwScores[substrings[0]] = [userScore]

    newWkwScoresArray = []
    for user in newWkwScores:
        newWkwScoresArray.append({'User': user, 'WkwScore': newWkwScores[user]})

    schema = StructType([
        StructField('User', StringType(), True),
        StructField('WkwScore', ArrayType(
            StructType([
                StructField('User', StringType(), True),
                StructField('Score', IntegerType(), True)
            ])
        ))
    ])

    df = spark.createDataFrame(newWkwScoresArray, schema)
    df.coalesce(1).write.format('json').mode("overwrite").save(messages_loc + '/WkwScores/')

if __name__ == "__main__":
    main()