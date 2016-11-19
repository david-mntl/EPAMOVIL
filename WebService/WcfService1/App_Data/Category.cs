using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WcfService1{
    [DataContract]
    public class Category{
        [DataMember]
        public int ID_Category { get; set; }
        [DataMember]
        public string Details { get; set; }
    }
}