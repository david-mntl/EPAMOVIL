using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WcfService1{
    [DataContract]
    public class Employee{
        [DataMember]
        public int ID_Employee { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public string LastName1 { get; set; }
        [DataMember]
        public string LastName2 { get; set; }
        [DataMember]
        public string Residence { get; set; }
        [DataMember]
        public string Nickname { get; set; }
        [DataMember]
        public string Secure_Pass { get; set; }
        [DataMember]
        public string BDate { get; set; }
        [DataMember]
        public string Phone { get; set; }
        [DataMember]
        public string Email { get; set; }
        [DataMember]
        public bool Active { get; set; }
        [DataMember]
        public string Position { get; set; }
        [DataMember]
        public int BOffice_ID { get; set; }
        [DataMember]
        public int Role { get; set; }
    }
}