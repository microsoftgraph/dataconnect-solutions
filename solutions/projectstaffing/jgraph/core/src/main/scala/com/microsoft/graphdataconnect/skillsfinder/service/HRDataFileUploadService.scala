package com.microsoft.graphdataconnect.skillsfinder.service

import java.time.OffsetDateTime

import com.azure.core.credential.{AccessToken, TokenCredential, TokenRequestContext}
import com.azure.storage.blob.models.ListBlobsOptions
import com.azure.storage.blob.{BlobClient, BlobContainerClient, BlobServiceClient, BlobServiceClientBuilder}
import com.microsoft.graphdataconnect.model.admin.IngestionMode
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.UserToken
import com.microsoft.graphdataconnect.skillsfinder.service.adf.AzureApiRequestRetry
import com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode.ModeSwitchStateService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono

import scala.collection.JavaConverters._
import scala.compat.java8.FunctionConverters._

@Service
class HRDataFileUploadService {

  @Autowired
  private var modeSwitchStateService: ModeSwitchStateService = _

  @Value("${azbs.storageAccount.name.demoData}")
  private var demoDataStorageAccountName: String = _

  private val logger: Logger = LoggerFactory.getLogger(classOf[HRDataFileUploadService])


  @AzureApiRequestRetry
  def writeHrData(multipartFile: MultipartFile, userToken: UserToken): Unit = {
    val blobContainerClient: BlobContainerClient = getHrDataBlobContainerClient(userToken.accessToken)

    //TODO Centralize the output folder path in jgraph-common
    val targetFolder = "hr_data/"

    val targetFolderUrl = blobContainerClient.getBlobContainerUrl + "/" + targetFolder
    logger.info(s"Clearing contents of HR Data folder $targetFolderUrl before writing new HR Data")
    val listOptions = new ListBlobsOptions()
    listOptions.setPrefix(targetFolder)
    blobContainerClient.listBlobs(listOptions, null).iterator().asScala
      .foreach(blobItem => {
        val blob = blobContainerClient.getBlobClient(blobItem.getName)
        blob.delete()
      })

    val targetFileName = "hr_data.csv"
    val targetFileUrl = targetFolderUrl + targetFileName
    logger.info(s"Uploading new HR Data file to $targetFileUrl")
    val csvBlobClient: BlobClient = blobContainerClient.getBlobClient(targetFolder + targetFileName)
    csvBlobClient.upload(multipartFile.getInputStream, multipartFile.getSize, true)
    logger.info(s"New HR Data file uploaded successfully")
  }

  private def getHrDataBlobContainerClient(accessToken: String): BlobContainerClient = {
    val blobServiceClient: BlobServiceClient = new BlobServiceClientBuilder()
      .endpoint(s"https://${demoDataStorageAccountName}.blob.core.windows.net")
      .credential(new TokenCredential {

        override def getToken(tokenRequestContext: TokenRequestContext): Mono[AccessToken] = {
          Mono.just(new AccessToken(accessToken, OffsetDateTime.MAX))
            .onErrorMap(
              ((e: Throwable) => {
                logger.error("Received an error when accessing  AccessToken Mono for creating BlobServiceClient.", e)
                e
              }).asJava)
        }

      }).buildClient()

    val latestIngestionModeOpt: Option[IngestionMode.Value] = modeSwitchStateService.getIngestionMode

    //TODO Centralize the container names in jgraph-common
    val containerName: String = latestIngestionModeOpt.map(_.toString) match {
      case Some("simulated_mode") => "simulated-data"
      case Some("sample_mode") => "sample-data"
      case Some("production_mode") => "production-data"
      case None => "simulated-data"
    }

    blobServiceClient.getBlobContainerClient(containerName)
  }

}


