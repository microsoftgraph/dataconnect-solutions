package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings

import java.time.OffsetDateTime

import com.fasterxml.jackson.annotation.JsonProperty
import com.microsoft.graphdataconnect.skillsfinder.models.SearchSettingsRequestResponse
import com.vladmihalcea.hibernate.`type`.json.JsonStringType
import javax.persistence.{Column, Entity, Id, Table}
import javax.validation.constraints.{Email, NotBlank}
import org.hibernate.annotations.{Type, TypeDef}

object SearchSettings {

  def apply(searchSettingsRequest: SearchSettingsRequestResponse, userEmail: String): SearchSettings = {
    val searchSettings = new SearchSettings
    searchSettings.userEmail = userEmail
    searchSettings.dataSources = searchSettingsRequest.dataSourceSettings.toDataSources()
    searchSettings.searchCriteria = searchSettingsRequest.searchCriteria
    searchSettings.searchResultsFilters = searchSettingsRequest.searchResultsFilters
    searchSettings.useReceivedEmailsContent = searchSettingsRequest.useReceivedEmailsContent
    searchSettings.freshness = searchSettingsRequest.freshness
    searchSettings.freshnessEnabled = searchSettingsRequest.freshnessEnabled
    searchSettings.volume = searchSettingsRequest.volume
    searchSettings.volumeEnabled = searchSettingsRequest.volumeEnabled
    searchSettings.relevanceScore = searchSettingsRequest.relevanceScore
    searchSettings.relevanceScoreEnabled = searchSettingsRequest.relevanceScoreEnabled
    searchSettings.freshnessBeginDate = searchSettingsRequest.freshnessBeginDate
    searchSettings.freshnessBeginDateEnabled = searchSettingsRequest.freshnessBeginDateEnabled
    searchSettings.includedEmailDomains = searchSettingsRequest.includedEmailDomains
    searchSettings.includedEmailDomainsEnabled = searchSettingsRequest.includedEmailDomainsEnabled
    searchSettings.excludedEmailDomains = searchSettingsRequest.excludedEmailDomains
    searchSettings.excludedEmailDomainsEnabled = searchSettingsRequest.excludedEmailDomainsEnabled
    searchSettings
  }

}

@Table(name = "search_settings")
@Entity
@TypeDef(name = "json", typeClass = classOf[JsonStringType])
class SearchSettings {

  @Id
  @JsonProperty("userEmail")
  @Column(name = "user_email")
  @NotBlank
  @Email
  var userEmail: String = _

  @Type(`type` = "json")
  @Column(name = "data_sources", columnDefinition = "json")
  var dataSources: List[DataSource] = _

  @Type(`type` = "json")
  @Column(name = "search_criteria", columnDefinition = "json")
  var searchCriteria: List[SearchCriterion] = _

  @Type(`type` = "json")
  @Column(name = "search_results_filters", columnDefinition = "json")
  var searchResultsFilters: List[SearchFiltersByDataSource] = _

  @JsonProperty("useReceivedEmailsContent")
  @Column(name = "use_received_emails_content")
  var useReceivedEmailsContent: Boolean = _

  @JsonProperty("freshness")
  @Column(name = "freshness")
  var freshness: Integer = _

  @JsonProperty("freshnessEnabled")
  @Column(name = "freshness_enabled")
  var freshnessEnabled: Boolean = _

  @JsonProperty("volume")
  @Column(name = "volume")
  var volume: Integer = _

  @JsonProperty("volumeEnabled")
  @Column(name = "volume_enabled")
  var volumeEnabled: Boolean = _

  @JsonProperty("relevanceScore")
  @Column(name = "relevance_score")
  var relevanceScore: Integer = _

  @JsonProperty("relevanceScoreEnabled")
  @Column(name = "relevance_score_enabled")
  var relevanceScoreEnabled: Boolean = _

  @JsonProperty("freshnessBeginDate")
  @Column(name = "freshness_begin_date")
  var freshnessBeginDate: OffsetDateTime = _

  @JsonProperty("freshnessBeginDateEnabled")
  @Column(name = "freshness_begin_date_enabled")
  var freshnessBeginDateEnabled: Boolean = _

  @Type(`type` = "json")
  @Column(name = "included_email_domains", columnDefinition = "json")
  var includedEmailDomains: List[String] = _

  @JsonProperty("includedEmailDomainsEnabled")
  @Column(name = "included_email_domains_enabled")
  var includedEmailDomainsEnabled: Boolean = _

  @Type(`type` = "json")
  @Column(name = "excluded_email_domains", columnDefinition = "json")
  var excludedEmailDomains: List[String] = _

  @JsonProperty("excludedEmailDomainsEnabled")
  @Column(name = "excluded_email_domains_enabled")
  var excludedEmailDomainsEnabled: Boolean = _
}
