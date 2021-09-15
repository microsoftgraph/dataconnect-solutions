using System;

namespace GDC.Models
{
    public class GDCContact
    {

        public string Owner { get; set; }

        public string city { get; set; }

        public string State { get; set; }

        public string client { get; set; }

        public string website { get; set; }

        public int numberEmployees { get; set; }

        public decimal revenue { get; set; }

        public string clientContact { get; set; }

        public string number { get; set; }

        public string clientCity { get; set; }

        public string clientState { get; set; }

        public string clientDept { get; set; }

        public string clientTitle { get; set; }

        public string OverallSentiment { get; set; }

        public decimal SentimentScore { get; set; }

        public decimal PositiveScore { get; set; }

        public decimal NeutralScore { get; set; }

        public decimal NegativeScore { get; set; }

        public int InteractionCount { get; set; }

        public string source_system { get; set; }
        
        

    }
}
