using Microsoft.Owin;
using Owin;
using WhoKnowWho.Models;

[assembly: OwinStartupAttribute(typeof(WhoKnowWho.Startup))]
namespace WhoKnowWho
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
            ContactMap.Initialize();
        }
    }
}
