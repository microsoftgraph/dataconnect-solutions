using System;
using System.Threading.Tasks;
using GDC.Commands;
using Azure.Identity;
using Azure.Security.KeyVault.Secrets;

namespace GDC.Commands
{
    public class RetrieveSecretCommand : ICommandAsync<string>
    {
        private readonly SecretClient _keyVaultClient;
        private readonly string _secretName;

        public RetrieveSecretCommand(string keyVaultUri, string secretName)
        {
            _keyVaultClient = new SecretClient(new Uri(keyVaultUri), new DefaultAzureCredential(new DefaultAzureCredentialOptions{ SharedTokenCacheTenantId = "d26bf63a-a52f-436a-bf3b-531b1e378694", VisualStudioTenantId = "d26bf63a-a52f-436a-bf3b-531b1e378694" }));        
            _secretName = secretName;
        }

        public async Task<string> ExecuteAsync()
        {
            KeyVaultSecret secret = await _keyVaultClient.GetSecretAsync(_secretName);
            return secret.Value;
        }
    }
}