using System;
using System.Net.Http;
using System.Threading.Tasks;

namespace GDC.Commands
{
    public class HttpGetStringResultCommand : ICommandAsync<string>
    {
        private readonly HttpClient _httpClient;
        private readonly string _url;

        public HttpGetStringResultCommand(HttpClient httpClient, string url) => (_httpClient, _url) = (httpClient, url);

        public async Task<string> ExecuteAsync()
        {
            var result = await _httpClient.GetAsync(_url);

            if(result.IsSuccessStatusCode) {
                return await result.Content.ReadAsStringAsync();
            }
            else {
                throw new Exception($"Error occurred while executing an HTTP GET request. Url: {_url}; Error: {result.StatusCode} - {result.ReasonPhrase}");
            }
        }
    }
}