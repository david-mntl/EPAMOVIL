
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;


namespace WcfService1{
    [DataContract]
    public class GenericQuery{
        [DataMember]
        public string DataG { get; set; }
        
    
    }
}

