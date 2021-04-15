package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.models.business.Opportunity
import com.microsoft.graphdataconnect.skillsfinder.models.response.ResponseMessage
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

import scala.collection.mutable

@Controller
@RequestMapping(Array("/gdc"))
class OpportunityController() {

  // TODO replace these with usage of data stored in DB
  var currentId: Long = 1L
  val defaultOpportunity = Opportunity(currentId, "Kaleidoscope", "Jeff Aliston", 2340000, "")
  val opportunities = mutable.Map[Long, Opportunity]((currentId, defaultOpportunity))

  @PostMapping(Array("/opportunities"))
  def createOpportunity(@RequestBody opportunity: Opportunity): ResponseEntity[Opportunity] = {
    currentId += 1
    val storedOpportunity = opportunity.copy(currentId)
    opportunities += (currentId -> storedOpportunity)
    ResponseEntity.status(HttpStatus.OK).body(storedOpportunity)
  }

  @GetMapping(Array("/opportunities"))
  def getOpportunities(): ResponseEntity[List[Opportunity]] = {
    ResponseEntity.status(HttpStatus.OK).body(opportunities.values.toList)
  }

  @GetMapping(Array("/opportunities/{opportunityId}"))
  def getOpportunity(@PathVariable opportunityId: Long): ResponseEntity[Opportunity] = {
    ResponseEntity.status(HttpStatus.OK).body(opportunities(opportunityId))
  }

  @PutMapping(Array("/opportunities/{opportunityId}"))
  def updateOpportunity(@PathVariable opportunityId: Long, @RequestBody opportunity: Opportunity): ResponseEntity[ResponseMessage] = {
    opportunities += (opportunityId -> opportunity)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Opportunity updated"))
  }

  @DeleteMapping(Array("/opportunities/{opportunityId}"))
  def deleteOpportunity(@PathVariable opportunityId: Long): ResponseEntity[ResponseMessage] = {
    opportunities -= opportunityId
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Opportunity deleted"))
  }
}
