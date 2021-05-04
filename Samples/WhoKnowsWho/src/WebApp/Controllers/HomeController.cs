using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using WhoKnowWho.Models;

namespace WhoKnowWho.Controllers
{
    [Authorize]
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult About()
        {
            ViewBag.CacheSize = ContactMap.GetMap().Result.Count();
            ViewBag.LastRefreshtime = ContactMap.GetLastRefreshTime().Result.ToLocalTime();

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Please contact us.";

            return View();
        }
    }
}