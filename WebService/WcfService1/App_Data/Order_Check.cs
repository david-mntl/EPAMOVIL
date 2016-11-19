using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WcfService1{
    [DataContract]
    public class Order_Check{
        
        [DataMember]
        public int BOffice { get; set; }
        [DataMember]
        public string Date_Time{ get; set; }
        [DataMember]
        public string Order_Status { get; set; }
        [DataMember]
        public int Active { get; set; }
        [DataMember]
        public int Customer_ID { get; set; }
        [DataMember]
        public int Employee_ID { get; set; }
        [DataMember]
        public int Invoice_ID { get; set; }
    }
}