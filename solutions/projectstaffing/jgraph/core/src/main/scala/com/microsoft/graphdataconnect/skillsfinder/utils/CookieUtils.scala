/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.utils

import java.util.Base64

import javax.servlet.http.{Cookie, HttpServletRequest, HttpServletResponse}
import org.springframework.util.SerializationUtils

object CookieUtils {

  def getCookie(request: HttpServletRequest, name: String): Option[Cookie] = {
    Option(request.getCookies).flatMap(cookies => cookies.find(_.getName.equals(name)))
  }

  def addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int): Unit = {
    val cookie: Cookie = new Cookie(name, value)
    cookie.setPath("/")
    cookie.setSecure(false)
    cookie.setMaxAge(maxAge)
    response.addCookie(cookie)
  }

  def deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String): Unit = {
    Option(request.getCookies).foreach(cookies => {
      cookies
        .filter(_.getName.equals(name))
        .foreach(cookie => {
          cookie.setValue("")
          cookie.setPath("/")
          cookie.setMaxAge(0)
          response.addCookie(cookie)
        })
    })
  }

  def serialize(anyObject: Any): String = Base64.getUrlEncoder.encodeToString(SerializationUtils.serialize(anyObject))

  def deserialize[T](cookie: Cookie, cls: Class[T]): T = {
    cls.cast(SerializationUtils
      .deserialize(Base64.getUrlDecoder.decode(cookie.getValue)))
  }

}
