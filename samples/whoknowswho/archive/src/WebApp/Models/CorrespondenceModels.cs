using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;
namespace WhoKnowWho.Models
{
    public class Correspondence
    {
        [Required(ErrorMessage = "Email cannot be empty.")]
        public string Email { get; set; }
        public float Closeness { get; set; }

        public Correspondence()
        {
        }

        public Correspondence(string email, float closeness)
        {
            this.Email = email;
            this.Closeness = closeness;
        }
    }
}