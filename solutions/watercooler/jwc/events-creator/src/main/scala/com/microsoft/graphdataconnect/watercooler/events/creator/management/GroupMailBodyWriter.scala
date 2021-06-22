/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.management

import java.io.StringWriter

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDayCC
import org.thymeleaf.context.Context
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templateresolver.{ClassLoaderTemplateResolver, FileTemplateResolver}

class GroupMailBodyWriter() extends Serializable {

  def getMeetingBody(group: GroupPerDayCC, meetingUrl: String): String = {

    val templateEngine = new TemplateEngine
    val templateResolver = new ClassLoaderTemplateResolver()
    templateResolver.setTemplateMode("HTML")
    templateEngine.setTemplateResolver(templateResolver)

    val context = new Context
    val stringWriter = new StringWriter()
    context.setVariable("name", group.display_name)
    context.setVariable("teamsMeetingUrl", meetingUrl)

    templateEngine.process("templates/emailIndex.html", context, stringWriter)
    stringWriter.toString
  }
}
