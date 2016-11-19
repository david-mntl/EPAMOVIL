using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WcfService1{
    [DataContract]
    public class LoginAuth{
        [DataMember]
        public string Nickname { get; set; }
        [DataMember]
        public string Secure_Pass { get; set; }
    }
}