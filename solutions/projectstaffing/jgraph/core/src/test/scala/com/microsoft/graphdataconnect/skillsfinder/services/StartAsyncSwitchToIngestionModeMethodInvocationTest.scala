/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.services

import java.time.{ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.model.admin.{IngestionMode, IngestionModeSwitchPhase}
import com.microsoft.graphdataconnect.skillsfinder.Runner
import com.microsoft.graphdataconnect.skillsfinder.config.ApplicationLifecycleEventListener
import com.microsoft.graphdataconnect.skillsfinder.controllers.{GDCRestClient, GdcController}
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{IngestionModeSwitchRejectedException, InvalidRequestException}
import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{IngestionModeSwitchState, UserToken}
import com.microsoft.graphdataconnect.skillsfinder.service.adf.ADFService
import com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode.{IngestionModeSwitchService, ModeSwitchStateService}
import com.microsoft.graphdataconnect.skillsfinder.service.{ConfigurationService, WebSocketService}
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.{verify, when}
import org.mockito.{ArgumentMatchers, Mockito}
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.{MockBean, SpyBean}
import org.springframework.test.context.junit4.SpringRunner

@RunWith(classOf[SpringRunner])
@SpringBootTest(
  properties = Array("spring.profiles.active=test"),
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = Array(classOf[Runner]))
class StartAsyncSwitchToIngestionModeMethodInvocationTest {

  @MockBean
  var adfService: ADFService = _
  @MockBean
  var webSocketService: WebSocketService = _
  @MockBean
  var modeSwitchStateService: ModeSwitchStateService = _
  @MockBean
  var configurationService: ConfigurationService = _
  @SpyBean
  var ingestionModeSwitchService: IngestionModeSwitchService = _

  //These beans are commented so not to be instantiated by Spring because they are not relevant for these tests
  @MockBean
  var GDCRestClient: GDCRestClient = _
  @MockBean
  var applicationEventListener: ApplicationLifecycleEventListener = _
  @MockBean
  var gdcController: GdcController = _

  //Tests for IngestionModeSwitchService.updateIngestionMode

  @Test
  def whenUpdateIngestionModeSwitchIsInvoked_AndPreviousIngestionModeSwitchFinished_StartAsyncSwitchIsCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.Completed),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.updateIngestionMode(IngestionMode.Sample, requester)
    } catch {
      case _: Throwable =>
        assert(assertion = false, "No exception should be thrown")
    }

    // ASSERT
    verify(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

  @Test
  def whenUpdateIngestionModeSwitchIsInvoked_AndIngestionModeSwitchIsInProgress_StartAsyncSwitchIsNotCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.RunningTriggerRunsStopped),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.updateIngestionMode(IngestionMode.Sample, requester)
    } catch {
      case e: Throwable =>
        assert(e.isInstanceOf[InvalidRequestException], "InvalidRequestException has to be thrown if ingestion mode switch is in progress.")
    }

    // ASSERT
    verify(ingestionModeSwitchService, Mockito.never()).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

  @Test
  def whenUpdateIngestionModeSwitchIsInvoked_AndIngestionModeRequestedIsAlreadySet_StartAsyncSwitchIsNotCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.Completed),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.updateIngestionMode(IngestionMode.Simulated, requester)
    } catch {
      case e: Throwable =>
        assert(e.isInstanceOf[IngestionModeSwitchRejectedException], "IngestionModeSwitchRejectedException has to be thrown if ingestion mode switch is already done.")
    }

    // ASSERT
    verify(ingestionModeSwitchService, Mockito.never()).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

  //Tests for IngestionModeSwitchService.retryIngestionModeSwitch

  @Test
  def whenRetryIngestionModeSwitchIsInvoked_AndIngestionModeSwitchIsInProgress_StartAsyncSwitchIsNotCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.IngestionModeUpdatedInAdf),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.retryIngestionModeSwitch(requester)
    } catch {
      case e: Throwable =>
        assert(e.isInstanceOf[InvalidRequestException], "IngestionModeSwitchRejectedException has to be thrown if ingestion mode switch is already done.")
    }

    // ASSERT
    verify(ingestionModeSwitchService, Mockito.never()).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

  @Test
  def whenRetryIngestionModeSwitchIsInvoked_AndPreviousIngestionModeSwitchFailed_StartAsyncSwitchIsCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.Error),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.retryIngestionModeSwitch(requester)
    } catch {
      case _: Throwable =>
        assert(assertion = false, "No exception should be thrown.")
    }

    // ASSERT
    verify(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

  //Tests for IngestionModeSwitchService.resumeIngestionModeSwitch

  @Test
  def whenResumeIngestionModeSwitchIsInvoked_AndPreviousIngestionModeSwitchFailed_StartAsyncSwitchIsNotCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.Error),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.resumeIngestionModeSwitch(requester)
    } catch {
      case e: Throwable =>
        assert(e.isInstanceOf[InvalidRequestException], "InvalidRequestException has to be thrown if resume is attempted and ingestion mode switch is in Error state.")
    }

    // ASSERT
    verify(ingestionModeSwitchService, Mockito.never()).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

  @Test
  def whenResumeIngestionModeSwitchIsInvoked_AndIngestionModeSwitchIsInProgress_StartAsyncSwitchIsNotCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.CleanupPipelineStarted),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.resumeIngestionModeSwitch(requester)
    } catch {
      case e: Throwable =>
        assert(e.isInstanceOf[InvalidRequestException], "InvalidRequestException has to be thrown if resume is attempted and ingestion mode switch is in progress.")
    }

    // ASSERT
    verify(ingestionModeSwitchService, Mockito.never()).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }


  @Test
  def whenResumeIngestionModeSwitchIsInvoked_AndIngestionModeSwitchIsPaused_StartAsyncSwitchIsCalled(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.CleanupPipelineStarted),
      modeSwitchPaused = true,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))
    Mockito.doNothing().when(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())

    // ACT
    try {
      ingestionModeSwitchService.resumeIngestionModeSwitch(requester)
    } catch {
      case _: Throwable =>
        assert(assertion = false, "No exception should be thrown")
    }

    // ASSERT
    verify(ingestionModeSwitchService).startAsyncSwitchToIngestionMode(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any())
  }

}
