/*
 *  Copyright (c) Microsoft Corporation. All rights reserved. Licensed under the MIT license.
 *  See LICENSE in the project root for license information.
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using Newtonsoft.Json.Linq;
using System.Configuration;
using System.IO;
using System.Threading.Tasks;

namespace EmailMetrics.Controllers
{
    public class EmailMetricsController : Controller
    {
        CloudStorageAccount _storageAccount;
        CloudBlobClient _storageClient;
        CloudBlobContainer _storageContainer;

        // GET: EmailMetrics
        public ActionResult Index()
        {
            return View();
        }

        [HttpPost, ActionName("ShowMetrics")]
        [ValidateAntiForgeryToken]
        public ActionResult ShowMetrics()
        {
            var emailMetrics = ProcessBlobFiles();

            return View(emailMetrics);
        }

        private void ProcessBlobEmails(List<Models.EmailMetric> emailMetrics, CloudBlob emailBlob)
        {
            using (var reader = new StreamReader(emailBlob.OpenRead()))
            {
                string line;
                while ((line = reader.ReadLine()) != null)
                {
                    var jsonObj = JObject.Parse(line);

                    // extract sender
                    var sender = jsonObj.SelectToken("Sender.EmailAddress.Address")?.ToString();

                    // extract and count up recipients
                    var totalRecipients = 0;
                    totalRecipients += jsonObj.SelectToken("ToRecipients").Children().Count();
                    totalRecipients += jsonObj.SelectToken("CcRecipients").Children().Count();
                    totalRecipients += jsonObj.SelectToken("BccRecipients").Children().Count();

                    var emailMetric = new Models.EmailMetric();
                    emailMetric.Email = sender;
                    emailMetric.RecipientsToEmail = totalRecipients;

                    // if already have this sender... 
                    var existingMetric = emailMetrics.FirstOrDefault(metric => metric.Email == emailMetric.Email);
                    if (existingMetric != null)
                    {
                        existingMetric.RecipientsToEmail += emailMetric.RecipientsToEmail;
                    }
                    else
                    {
                        emailMetrics.Add(emailMetric);
                    }
                }
            }
        }

        private List<Models.EmailMetric> ProcessBlobFiles()
        {
            var emailMetrics = new List<Models.EmailMetric>();

            // connect to the storage account
            _storageAccount = CloudStorageAccount.Parse(ConfigurationManager.AppSettings["AzureStorageConnectionString-1"]);
            _storageClient = _storageAccount.CreateCloudBlobClient();

            // connect to the container
            _storageContainer = _storageClient.GetContainerReference("maildump");

            // get a list of all emails
            var blobResults = _storageContainer.ListBlobs();

            // process each email
            foreach (IListBlobItem blob in blobResults)
            {
                if (blob.GetType() == typeof(CloudBlockBlob))
                {
                    var cloudBlob = (CloudBlockBlob)blob;
                    var blockBlob = _storageContainer.GetBlobReference(cloudBlob.Name);

                    ProcessBlobEmails(emailMetrics, blockBlob);
                }
            }

            return emailMetrics;
        }
    }
}