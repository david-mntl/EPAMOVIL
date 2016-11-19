using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WcfService1{
    [DataContract]
    public class Product{
        [DataMember]
        public int ID_Product { get; set; }
        [DataMember]
        public string Details { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public int Stock { get; set; }
        [DataMember]
        public int Price { get; set; }
        [DataMember]
        public int BOffice { get; set; }
        [DataMember]
        public bool TaxFree { get; set; }
        [DataMember]
        public int ID_Supplier { get; set; }
        [DataMember]
        public int ID_Category { get; set; }
        [DataMember]
        public bool Active { get; set; }
    }
}