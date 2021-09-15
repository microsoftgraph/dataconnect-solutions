using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using System.Linq;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using Dapper;
using GDC.Models.Input;
using GDC.Commands;
using GDC.Models;

//meaningless comment
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

            string contactsReturn = null; 

            try
            {
                List<GDCContact> contacts = new List<GDCContact>();


                using (IDbConnection db = new SqlConnection(sqlConnectionString))
                {
                    contacts = db.Query<GDCContact>(@"SELECT * FROM dbo.v_ConversationSentiment_CRM", 300).ToList();                    
                }

                if(gdcInput.returnFormat == "csv")
                {

                    contactsReturn = CSVCreator.CreateCSVTextFile(contacts);

                   return new OkObjectResult(contactsReturn);
                   
                }
                else
                {        
                   
                   contactsReturn = JsonConvert.SerializeObject(contacts, Formatting.Indented);

                   return new OkObjectResult(contactsReturn);
                }
                //Stupid worthless comment

            }
            catch (System.Exception ex)
            {
                return new BadRequestObjectResult($"An error occured getting contacts from the SQL Database. {ex.ToString()}");
            }

        }

    }
}