/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.admin

object ADFTrigger extends Enumeration {

  type ADFTrigger = ADFTriggerValue

  case class ADFTriggerValue(triggerName: String, numOfPipelinesStartedByTrigger: Int) extends Val(triggerName)

  //TODO keep the trigger names and the number of started pipelines in sync with the ones in ADF
  val EMAILS_PIPELINE_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("emails_pipeline_trigger", 1)
  val EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("emails_pipeline_backfill_past_week_trigger", 7)
  val EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("emails_pipeline_backfill_further_past_trigger", -1) // -1 stands for unknown
  val INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("inferred_roles_pipeline_backfill_trigger", 1)
  val INFERRED_ROLES_PIPELINE_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("inferred_roles_pipeline_trigger", 1)
  val EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("employee_profiles_pipeline_backfill_trigger", 1)
  val EMPLOYEE_PROFILES_PIPELINE_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("employee_profiles_pipeline_trigger", 1)
  val AIRTABLE_PIPELINE_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("airtable_pipeline_trigger", 1)
  val AIRTABLE_PIPELINE_BACKFILL_TRIGGER: ADFTrigger.ADFTrigger = ADFTriggerValue("airtable_pipeline_backfill_trigger", 1)

}
