using System;
using System.Configuration;
using System.IO;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Microsoft.IdentityModel.Clients.ActiveDirectory;
using WhoKnowWho.Models;
using Microsoft.Rest;
using Microsoft.Rest.Azure.Authentication;
using Microsoft.Azure.Management.DataLake.Store;

namespace WhoKnowWho.Controllers
{
    /// <summary>
    /// Searching people who have correspondence with a particular e-mail address 
    /// </summary>
    [Authorize]
    public class CorrespondenceController : Controller
    {
        /// <summary>
        /// GET: Correspondence/Search 
        /// </summary>
        /// <returns>The search screen</returns>
        public async Task<ActionResult> Search()
        {
            IEnumerable<string> samples = await ContactMap.GetSampleUsers();

            if (samples != null && samples.Count() > 0)
            {
                string sample = samples.ElementAt((int) (DateTimeOffset.Now.Ticks % samples.Count()));
                ViewBag.SearchSuggestions += string.Format("Try {0}", sample);
                ViewBag.SampleUser = sample;
            }
            else
            {
                ViewBag.SearchSuggestions = "Data extraction pipeline is running... Please wait!";
                ViewBag.SampleUser = null ;
            }

            return View();
        }

        /// <summary>
        /// POST: correspondence/Search 
        /// </summary>
        /// <param name="associate">The e-mail address or domain you want to search</param>
        /// <returns>The search results</returns>
        [HttpPost, ActionName("Search")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Search([Bind(Include = "Email,Closeness")] Correspondence associate)
        {
            string eMail = associate.Email.Trim().TrimStart('@');

            IEnumerable<Correspondence> result = await GetResults(eMail);

            ViewBag.SearchEmail = (eMail.Contains('@')? "" : "people in ") + eMail;

            if (result == null || result.Count() == 0)
            {
                return View("NoResults");
            }
            else
            {
                return View("SearchResults", result);
            }
        }

        /// <summary>
        /// Get the e-mail address and degree of interaction of all people who interact with a given e-mail address 
        /// </summary>
        /// <param name="eMail">The e-mail you can to find who are interacting with</param>
        /// <returns>e-mail and degree of interaction for the people who interact with the given e-mail</returns>
        protected async Task<IEnumerable<Correspondence>> GetResults(string eMail)
        {
            Dictionary<string, List<UserScore>> map = await ContactMap.GetMap();

            if(map == null)
            {
                return null;
            }

            List<UserScore> userScoreList = null;
            map.TryGetValue(eMail, out userScoreList);

            if(userScoreList == null)
            {
                return null;
            }

            IEnumerable<Correspondence> result =
                (from e in userScoreList
                 select new Correspondence(e.User, e.Score)).OrderByDescending(o => o.Closeness).Take(5);

            return result;
        }
    }
}
