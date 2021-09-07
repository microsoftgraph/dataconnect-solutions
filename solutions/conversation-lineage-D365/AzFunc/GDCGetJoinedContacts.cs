using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using System.Text.RegularExpressions;
using System.Linq;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using Dapper;
using GDC.Models.Input;
using GDC.Commands;
using GDC.Models;





namespace GDC.Functions
{
    public static class GDCGetJoinedContacts
    {
        [FunctionName("GDCGetJoinedContacts")]

        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "get", "Post", Route = null)] HttpRequest req,
            ILogger log)
        {
            GDCGetTablesInput gdcInput;
            string sqlConnectionString;

            try
            {
                var body = await new StreamReader(req.Body).ReadToEndAsync();
                gdcInput = JsonConvert.DeserializeObject<GDCGetTablesInput>(body);
            }
            catch (System.Exception ex)
            {
                return new BadRequestObjectResult($"An error occurred while deserializing input: {ex.ToString()}");
            }

            try
            {
                sqlConnectionString = await new RetrieveSecretCommand(gdcInput.keyVaultURI, gdcInput.sqlConnectionStringSecret).ExecuteAsync();

            }
            catch (System.Exception ex)
            {
                return new BadRequestObjectResult($"An error occurred while pulling secrets from Key Vault: {ex.ToString()}");
            }

            string contactsJson = null;


            try
            {
                List<GDCContact> contacts = new List<GDCContact>();
                using (IDbConnection db = new SqlConnection(sqlConnectionString))
                {
                    contacts = db.Query<GDCContact>(@"SELECT * FROM dbo.crm_contact").ToList();
                }

                contactsJson = JsonConvert.SerializeObject(contacts, Formatting.Indented);

            }
            catch (System.Exception ex)
            {
                return new BadRequestObjectResult($"An error occured getting contacts from the SQL Database. {ex.ToString()}");
            }

                return new OkObjectResult(contactsJson);
        }

    }
}