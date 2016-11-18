using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WcfService1
{   [DataContract]
    public class LoginEmployee{
        [DataMember]
        public string Nickname { get; set; }
        [DataMember]
        public string Secure_Pass { get; set; }
    
    }
}
//connectionString="Server=tcp:epatec.database.windows.net,1433;Database=DBCore;User ID=master;Password=sudoPass36;Encrypt=True;TrustServerCertificate=False;Connection Timeout=30;" />
//connectionString="Server=ec2-54-221-246-85.compute-1.amazonaws.com,5432;Database=dbqekda7uhijva;User ID=kahakkmxmkiatf;Password=HL0jQmL7nyK97F2PqRa2zhcszZ;Encrypt=True;TrustServerCertificate=False;Connection Timeout=30;" />