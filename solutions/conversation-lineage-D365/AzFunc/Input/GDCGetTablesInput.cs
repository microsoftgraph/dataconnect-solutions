namespace GDC.Models.Input
{

    public class GDCGetTablesInput
    {
        public string keyVaultURI { get; set; }

        public string sqlConnectionStringSecret { get; set; }
    

        public GDCGetTablesInput()
        {
        }
    }

}