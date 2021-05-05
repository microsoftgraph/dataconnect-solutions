/*
 *  Copyright (c) Microsoft Corporation. All rights reserved. Licensed under the MIT license.
 *  See LICENSE in the project root for license information.
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WhoKnowWho.Models
{
    public class Message
    {
        public string Sender { get; set; }
        public List<string> ToRecipients { get; set; }
        public List<string> CcRecipients { get; set; }
        public List<string> BccRecipients { get; set; }
    }
}