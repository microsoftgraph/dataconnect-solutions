package com.microsoft.graphdataconnect.skillsfinder.model.email.enron

case class MimeEmail(messageId: String,
                     date: String,
                     from: String,
                     to: Seq[String] = List(),
                     subject: String,
                     cc: Seq[String] = List(),
                     mimeVersion: String,
                     contentType: String,
                     contentTransferEncoding: String,
                     bcc: Seq[String] = List(),
                     xFrom: String,
                     xTo: Seq[String] = List(),
                     xCc: Seq[String] = List(),
                     xBcc: Seq[String] = List(),
                     xFolder: String,
                     xOrigin: String,
                     xFieldName: String,
                     body: String
                    )
