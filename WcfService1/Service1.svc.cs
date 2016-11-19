using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
//using Google.Protobuf.Examples.MessageProtocol;
using System.IO;
using Newtonsoft.Json;
using Npgsql;
using System.Text.RegularExpressions;
//using System.Net.HttpWebResponse;

namespace WcfService1
{
        
    /*
     * Cuerpo de los metodos que son llamados desde el IService.cs
     * Los metodos reciben un objeto del tipo requerido, por ejemplo si se desea ingresar un nuevo 
     * Customer se envia un json con la informacion y luego se castea a la clase y se ejecuta el query.
     * 
     * Si los metodos son de tipo GET se devuelve la información en formato Json
     * en algunos casos se devuelve un string normal.
     * Si los metodos son de tipo POST se le pasa un json y este luego es pasado a objetos  
     * para que luego la información sea pasada como parametros en los querys
     * 
     */
    public class Service1 : IService1
    {
        string conn;
        SqlConnection connection;




        public string connect()
        {
            try
            {
               conn =ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
               connection = new SqlConnection(conn);
               connection.Open();
               connection.CreateCommand();
                return "ok";
            }
            catch (Exception ee)
            {
                return ee.Message;
            }
            try
            {
                conn = ConfigurationManager.ConnectionStrings["MyConnection2"].ConnectionString;
                connection = new SqlConnection(conn);
                connection.Open();
                connection.CreateCommand();
                return "ok";
            }
            catch (Exception ee)
            {
                return ee.Message;
            }
        }

        

        //Recibe un objeto de tipo Customer y lo registra en la base de datos
        public string PostCustomer(Customer str) {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            string query = "INSERT INTO CUSTOMER VALUES(@ID_Card,@Name,@LastName1,@LastName2,@Residence,@Nickname, @Secure_Pass,@BDate,@Phone,@Email,@PriorityLevel,1)";
            //string query = "INSERT INTO CUSTOMER VALUES(12423, 'Leni4cn', 'Torre4s', 'Valv4erde', 'c4r', 'tvleni4n3', '122435', 03/04/ 16, 88848, 'tvl4lll', 1, 1)";
            string msg = "all good";
            try
            {
                msg += " intenta";
                //connection.Open();
                this.connect();
                msg += " conecta";
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@ID_Card", str.ID_Card);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@LastName1",str.LastName1);
                sqlcmd.Parameters.AddWithValue("@LastName2", str.LastName2);
                sqlcmd.Parameters.AddWithValue("@Residence", str.Residence);
                sqlcmd.Parameters.AddWithValue("@Nickname", str.Nickname);
                sqlcmd.Parameters.AddWithValue("@Secure_Pass", str.Secure_Pass);
                sqlcmd.Parameters.AddWithValue("@BDate", Convert.ToDateTime(str.BDate));
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);
                sqlcmd.Parameters.AddWithValue("@Email", str.Email);
                sqlcmd.Parameters.AddWithValue("@PriorityLevel", str.PriorityLevel);
                //sqlcmd.Parameters.AddWithValue("@Active", str.Active);

                msg += " va a ejecutar"+ str.ID_Card+str.Name+ Convert.ToDateTime(str.BDate);
                sqlcmd.CommandType = CommandType.Text;
                sqlcmd.ExecuteNonQuery();
                msg += " No ejecutar";
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += "Error" + query;
            }
            finally
            {
                connection.Close();
           }

            return msg;
        }
        //Recibe el id del Custumer y devuelve la informacion de este
        public string Get_Customer(string id) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            connection.Open();
            string queryStock = "SELECT * FROM [CUSTOMER] WHERE Nickname = " +"'"+ id + "'";
            SqlCommand sqlcmdNick = new SqlCommand(queryStock, connection);
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmdNick);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables[0]);
            //tring result = result1.Remove(result1.Length - 1).Remove(result1.Length - 2);
            connection.Close();
            return result1;

            //return resultOrder;
        }
        //Recibe un objeto de tipo Customer y lo Actualiza en la base de datos
        public string UpdateCustomer(Customer str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "UPDATE CUSTOMER SET Name = @Name ,Residence = @Residence,Nickname = @Nickname, Secure_Pass = @Pass, BDate = @BDate,Phone = @Phone, Email = @Email,PriorityLevel = @PriorityLevel WHERE ID_Card = @ID_Card";
           
            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@ID_Card", str.ID_Card);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Residence", str.Residence);
                sqlcmd.Parameters.AddWithValue("@Nickname", str.Nickname);
                sqlcmd.Parameters.AddWithValue("@Pass", str.Secure_Pass);
                sqlcmd.Parameters.AddWithValue("@BDate", Convert.ToDateTime(str.BDate));
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);
                sqlcmd.Parameters.AddWithValue("@Email", str.Email);
                sqlcmd.Parameters.AddWithValue("@PriorityLevel", str.PriorityLevel);


                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe el id del Custumer y lo elimina de la base de datos
        public string DeleteCustomer(string str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
        
            string query = "UPDATE CUSTOMER SET Active = 0 WHERE Nickname = "+"'"+str+"'";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
             

                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;

        }
        //Recibe un objeto de tipo Order_Check y lo registra en la base de datos
        public string PostOrder(Order_Check str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            DateTime dtvar = Convert.ToDateTime(str.Date_Time);
            string query = "INSERT INTO ORDER_CHECK VALUES(@Date_Time,@OrderStatus,@Active,@Customer_ID,Employee_ID,@BOffice)";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@Customer_ID", str.Customer_ID);
                sqlcmd.Parameters.AddWithValue("@Employee_ID", str.Employee_ID);
                sqlcmd.Parameters.AddWithValue("@BOffice", str.BOffice);
                sqlcmd.Parameters.AddWithValue("@Date_Time", dtvar);
                sqlcmd.Parameters.AddWithValue("@OrderStatus", str.Order_Status);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);

                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe un objeto de tipo Category y lo registra en la base de datos
        public string PostCategory(Category str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);

            string query = "INSERT INTO CATEGORY VALUES(@Details)";
            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@Details", str.Details);
                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe un objeto de tipo Login_Auth y pide los datos para loguear a un usuario
        public string PostLogin(LoginAuth str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT ID_Card FROM [CUSTOMER] WHERE Nickname = @nickname AND Secure_Pass = @pass AND Active = 1";
            string msg = "all good";

            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            sqlcmd.Parameters.AddWithValue("@nickname", str.Nickname);
            sqlcmd.Parameters.AddWithValue("@pass", str.Secure_Pass);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);

            connection.Close();

            if (dt.Tables[0].Rows.Count == 0)
            {
                return "responsefalse";

            }
            else
                return "responsetrue";
            
            
        }
        //Recibe un objeto de tipo LoginEmployee y loguea el empleado
        public string PostLoginEmployee(LoginEmployee str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT Position ,BOffice_ID FROM [EMPLOYEE] WHERE Nickname = @nickname AND Secure_Pass = @pass";
            string msg = "all good";

            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            sqlcmd.Parameters.AddWithValue("@nickname", str.Nickname);
            sqlcmd.Parameters.AddWithValue("@pass", str.Secure_Pass);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);

            string result = JsonConvert.SerializeObject(dt.Tables[0]);
            string result1 = result.Remove(result.Length - 2);



            connection.Close();

            if (dt.Tables[0].Rows.Count == 0)
            {
                return "responsefalse";

            }
            else
                return result1.Split(',')[0].Split(':')[1]+":"+ result1.Split(',')[1].Split(':')[1];


        }
        //Recibe un objeto de tipo Employee y lo registra en la base de datos
        public string PostEmployee(Employee str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "INSERT INTO EMPLOYEE VALUES(@ID_Employee,@Name,@LastName1,@LastName2,@Residence,@Nickname, @Secure_Pass,@BDate,@Phone,@Email,@Position,@Active,@BOffice_ID,@Role)";
            //DateTime bdate = Convert.ToDateTime(str.BDate);
            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@ID_Employee", str.ID_Employee);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@LastName1", str.LastName1);
                sqlcmd.Parameters.AddWithValue("@LastName2", str.LastName2);
                sqlcmd.Parameters.AddWithValue("@Residence", str.Residence);
                sqlcmd.Parameters.AddWithValue("@Nickname", str.Nickname);
                sqlcmd.Parameters.AddWithValue("@Secure_Pass", str.Secure_Pass);
                sqlcmd.Parameters.AddWithValue("@BDate", str.BDate);
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);
                sqlcmd.Parameters.AddWithValue("@Email", str.Email);
                sqlcmd.Parameters.AddWithValue("@Position", str.Position);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);
                sqlcmd.Parameters.AddWithValue("@BOffice_ID", str.BOffice_ID);
                sqlcmd.Parameters.AddWithValue("@Role", str.Role);
                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }

        //Recibe un objeto de tipo Producto y lo registra en la base de datos
        public string PostProduct(Product str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "INSERT INTO PRODUCT VALUES(@Details,@Stock,@Price,@TaxFree,@ID_Supplier,@ID_Category,@Active,@Name,@BOffice)";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Details", str.Details);
                sqlcmd.Parameters.AddWithValue("@Stock", str.Stock);
                sqlcmd.Parameters.AddWithValue("@Price", str.Price);
                sqlcmd.Parameters.AddWithValue("@BOffice", str.BOffice);
                sqlcmd.Parameters.AddWithValue("@TaxFree", str.TaxFree);
                sqlcmd.Parameters.AddWithValue("@ID_Supplier", str.ID_Supplier);
                sqlcmd.Parameters.AddWithValue("@ID_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);


                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe un objeto de tipo Producto y actualiza la base de datos
        public string PostUpdateProduct(Product str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "UPDATE PRODUCT SET Details = @Details,Stock = @Stock, Price = @Price,TaxFree = @TaxFree,ID_Supplier = @ID_Supplier,ID_Category = @ID_Category,Active = @Active,Name = @Name,BOffice = @BOffice WHERE ID_Product = @ID_Product";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Details", str.Details);
                sqlcmd.Parameters.AddWithValue("@Stock", str.Stock);
                sqlcmd.Parameters.AddWithValue("@Price", str.Price);
                sqlcmd.Parameters.AddWithValue("@BOffice", str.BOffice);
                sqlcmd.Parameters.AddWithValue("@TaxFree", str.TaxFree);
                sqlcmd.Parameters.AddWithValue("@ID_Supplier", str.ID_Supplier);
                sqlcmd.Parameters.AddWithValue("@ID_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);
                sqlcmd.Parameters.AddWithValue("@ID_Product", str.ID_Product);


                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        
       

        //Obtiene los productos segun el parametro recibido
        public string GetProducts(string str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [PRODUCT] WHERE Active = 1 ";
            string[] paramList = str.Split(',');

            if (str != "all")
            {

                for (int i = 0; i < paramList.Length; i++)
                {
                    if (i == paramList.Length - 1)
                    {
                        query += paramList[i];
                    }
                    else
                    {
                        query += paramList[i] + " AND ";
                    }
                }

            }
            else
            {
                query = "SELECT * FROM PRODUCT WHERE Active = 1";
            }

            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");

            return result2;
        }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public string GetAllCustomer()
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [CUSTOMER] WHERE Active = 1 ";
            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");

            return result2;
        }
        public string GetAllOrder()
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [ORDER_CHECK]";
            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");
            return result2;
        }
        public string GetAllEmployee()
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [EMPLOYEE] WHERE Active = 1";
            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");
            return result2;
        }
        public string GetAllCategory()
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [CATEGORY]";
            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");
            return result2;
        }
        public string GetAllPurchase()
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [PURCHASE_ITEM]";
            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");
            return result2;
        }
        public string GetAllSupplier()
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "SELECT * FROM [SUPPLIER]";
            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);
            string result2 = Regex.Unescape(result);
            result2 = result2.Replace("\\", " ");
            return result2;
        }
        /****************************************************************************************************************************/
        public string PostNEWCustomer(Customer str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            string query = "EXEC [dbo].[insertCustomer_Procedure] @ID_Card,@Name,@LastName1,@LastName2,@Residence,@Nickname, @Secure_Pass,@BDate,@Phone,@Email,@PriorityLevel,1";
            //string query = "INSERT INTO CUSTOMER VALUES(12423, 'Leni4cn', 'Torre4s', 'Valv4erde', 'c4r', 'tvleni4n3', '122435', 03/04/ 16, 88848, 'tvl4lll', 1, 1)";
            string msg = "all good";
            try
            {
                msg += " intenta";
                //connection.Open();
                this.connect();
                msg += " conecta";
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@ID_Card", str.ID_Card);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@LastName1", str.LastName1);
                sqlcmd.Parameters.AddWithValue("@LastName2", str.LastName2);
                sqlcmd.Parameters.AddWithValue("@Residence", str.Residence);
                sqlcmd.Parameters.AddWithValue("@Nickname", str.Nickname);
                sqlcmd.Parameters.AddWithValue("@Secure_Pass", str.Secure_Pass);
                sqlcmd.Parameters.AddWithValue("@BDate", Convert.ToDateTime(str.BDate));
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);
                sqlcmd.Parameters.AddWithValue("@Email", str.Email);
                sqlcmd.Parameters.AddWithValue("@PriorityLevel", str.PriorityLevel);
                //sqlcmd.Parameters.AddWithValue("@Active", str.Active);

                msg += " va a ejecutar" + str.ID_Card + str.Name + Convert.ToDateTime(str.BDate);
                sqlcmd.CommandType = CommandType.Text;
                sqlcmd.ExecuteNonQuery();
                msg += " No ejecutar";
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += "Error" + query;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        public string PostNEWCategory(Category str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);

            string query = "EXEC [dbo].[insertCategory_Procedure] @id_Category,@Details";
            string msg = "all good";
            try
            {
                //connection.Open();
                this.connect();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@id_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@Details", str.Details);
                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        public string PostNEWEmployee(Employee str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            string query = "EXEC [dbo].[insertEmployee_Procedure] @ID_Employee,@Name,@LastName1,@LastName2,@Residence,@Nickname, @Secure_Pass,@BDate,@Phone,@Email,@Position,@Active,@BOffice_ID,@Role";
            //DateTime bdate = Convert.ToDateTime(str.BDate);
            string msg = "all good";
            try
            {
                //connection.Open();
                this.connect();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@ID_Employee", str.ID_Employee);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@LastName1", str.LastName1);
                sqlcmd.Parameters.AddWithValue("@LastName2", str.LastName2);
                sqlcmd.Parameters.AddWithValue("@Residence", str.Residence);
                sqlcmd.Parameters.AddWithValue("@Nickname", str.Nickname);
                sqlcmd.Parameters.AddWithValue("@Secure_Pass", str.Secure_Pass);
                sqlcmd.Parameters.AddWithValue("@BDate", str.BDate);
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);
                sqlcmd.Parameters.AddWithValue("@Email", str.Email);
                sqlcmd.Parameters.AddWithValue("@Position", str.Position);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);
                sqlcmd.Parameters.AddWithValue("@BOffice_ID", str.BOffice_ID);
                sqlcmd.Parameters.AddWithValue("@Role", str.Role);
                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        public string PostNEWOrder(Order_Check str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            DateTime dtvar = Convert.ToDateTime(str.Date_Time);
            string query = "EXEC [dbo].[insertOrderCheck_Procedure] @InvoiceID,@Date_Time,@OrderStatus,@Active,@Customer_ID,@Employee_ID,@BOffice";

            string msg = "all good";
            try
            {
                //connection.Open();
                this.connect();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@InvoiceID", str.InvoiceID);
                sqlcmd.Parameters.AddWithValue("@Customer_ID", str.Customer_ID);
                sqlcmd.Parameters.AddWithValue("@Employee_ID", str.Employee_ID);
                sqlcmd.Parameters.AddWithValue("@BOffice", str.BOffice);
                sqlcmd.Parameters.AddWithValue("@Date_Time", dtvar);
                sqlcmd.Parameters.AddWithValue("@OrderStatus", str.Order_Status);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);

                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        public string PostNEWProduct(Product str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            string query = "EXEC [dbo].[insertProduct_Procedure] @ID_Product,@Details,@Stock,@Price,@TaxFree,@ID_Supplier,@ID_Category,@Active,@Name,@BOffice";

            string msg = "all goodas";
            try
            {
                //connection.Open();
                this.connect();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@ID_Product", str.ID_Product);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Details", str.Details);
                sqlcmd.Parameters.AddWithValue("@Stock", str.Stock);
                sqlcmd.Parameters.AddWithValue("@Price", str.Price);
                sqlcmd.Parameters.AddWithValue("@BOffice", str.BOffice);
                sqlcmd.Parameters.AddWithValue("@TaxFree", str.TaxFree);
                sqlcmd.Parameters.AddWithValue("@ID_Supplier", str.ID_Supplier);
                sqlcmd.Parameters.AddWithValue("@ID_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);


                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        public string PostNEWPurchase(Purchase_Item str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            string query = "EXEC [dbo].[insertPurchaseItem_Procedure] @PurchasedItem_ID,@Invoice_ID,@Quantity,@Price,@Product_ID";

            string msg = "all good";
            try
            {
                //connection.Open();
                this.connect();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@PurchasedItem_ID", str.PurchasedItem_ID);
                sqlcmd.Parameters.AddWithValue("@Invoice_ID", str.Invoice_ID);
                sqlcmd.Parameters.AddWithValue("@Quantity", str.Quantity);
                sqlcmd.Parameters.AddWithValue("@Price", str.Price);
                sqlcmd.Parameters.AddWithValue("@Product_ID", str.Product_ID);



                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        public string PostNEWSupplier(Supplier str)
        {
            //string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            //SqlConnection connection = new SqlConnection(conn);
            string query = "EXEC [dbo].[insertSupplier_Procedure] @ID_Supplier,@Active,@Name,@Country,@Phone";

            string msg = "all good";
            try
            {
                //connection.Open();
                this.connect();

                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Product", str.ID_Product);
                sqlcmd.Parameters.AddWithValue("@ID_Supplier", str.ID_Supplier);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Country", str.Country);
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);



                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Elimina un producto de la base de datos
        public string DeleteProduct(string id){
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "UPDATE PRODUCT SET Active = 0 WHERE ID_Product = " + Convert.ToInt32(id);

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
               



                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;

        }
        //Realiza un post de un item que pertenece a un OrderCheck
        public string Post_Purchase_Item(Purchase_Item str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "INSERT INTO PURCHASE_ITEM VALUES(@Invoice_ID,@Quantity,@Price,@Product_ID)";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Product", str.ID_Product);
                sqlcmd.Parameters.AddWithValue("@Invoice_ID", str.Invoice_ID);
                sqlcmd.Parameters.AddWithValue("@Quantity", str.Quantity);
                sqlcmd.Parameters.AddWithValue("@Price", str.Price);
                sqlcmd.Parameters.AddWithValue("@Product_ID", str.Product_ID);
           


                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe un objeto de tipo Supplier y lo registra en la base de datos
        public string Post_Supplier(Supplier str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "INSERT INTO SUPPLIER VALUES(@ID_Supplier,@Active,@Name,@Country,@Phone)";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Product", str.ID_Product);
                sqlcmd.Parameters.AddWithValue("@ID_Supplier", str.ID_Supplier);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Country", str.Country);
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);



                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe un objeto de tipo Supplier y actualiza la base de datos
        public string PostUpdateSupplier(Supplier str)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "INSERT SUPPLIER VALUES(@ID_Supplier,@Active,@Name,@Country,@Phone)";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Product", str.ID_Product);
                sqlcmd.Parameters.AddWithValue("@ID_Supplier", str.ID_Supplier);
                sqlcmd.Parameters.AddWithValue("@Active", str.Active);
                sqlcmd.Parameters.AddWithValue("@Name", str.Name);
                sqlcmd.Parameters.AddWithValue("@Country", str.Country);
                sqlcmd.Parameters.AddWithValue("@Phone", str.Phone);



                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        //Recibe un objeto de tipo Post_Shop y lo registra en la base de datos
        public string Post_Shop(GenericQuery str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string msg = "OK";
            string data = str.DataG;
            string[] datasplit = data.Split(',');
            connection.Open();

            for (int i = 1; i < datasplit.Length; i++) {
                int quantity = Convert.ToInt32(datasplit[i].Split(':')[1]);
                int id_Product = Convert.ToInt32(datasplit[i].Split(':')[0]);
                int check_Stock = Product_Quantity(id_Product);
                if (quantity > check_Stock) {

                    string id_product = datasplit[0].Split(':')[0];
                    string queryNickname = "SELECT Name FROM [Product] WHERE ID_Product = " + id_Product;
                    SqlCommand sqlcmdNick = new SqlCommand(queryNickname, connection);
                    SqlDataAdapter sda = new SqlDataAdapter(sqlcmdNick);
                    DataSet dt = new DataSet();
                    sda.Fill(dt);
                    string result1 = JsonConvert.SerializeObject(dt.Tables[0]);
                    string result = result1.Remove(result1.Length - 1).Remove(result1.Length - 2);

                    connection.Close();
                    return "NS:"+ result.Split(':')[1] +":"+ check_Stock ;
                }

            }


                try
            {
                
               
                
                string Nickname = datasplit[0].Split(':')[0];
                string queryNickname = "SELECT ID_Card FROM [CUSTOMER] WHERE Nickname = " + "'" + Nickname + "'";
                SqlCommand sqlcmdNick = new SqlCommand(queryNickname, connection);
                SqlDataAdapter sda = new SqlDataAdapter(sqlcmdNick);
                DataSet dt = new DataSet();
                sda.Fill(dt);
                string result1 = JsonConvert.SerializeObject(dt.Tables[0]);
                string result = result1.Remove(result1.Length - 1).Remove(result1.Length - 2);
                ////////////////////////////////////////////////////////////////////////////////////////////////////
                string boffice = datasplit[0].Split(':')[1];
                string queryBOffice = "SELECT BOffice_ID FROM [BOFFICE] WHERE Name = " + "'" + boffice + "'";
                SqlCommand sqlcmdOffice = new SqlCommand(queryBOffice, connection);
                SqlDataAdapter sdaOffice = new SqlDataAdapter(sqlcmdOffice);
                DataSet dtOffice = new DataSet();
                sdaOffice.Fill(dtOffice);
                string resultOffice = JsonConvert.SerializeObject(dtOffice.Tables[0]);
                string resultOfficeF = resultOffice.Remove(resultOffice.Length - 1).Remove(resultOffice.Length - 2);
                msg += resultOfficeF.Split(':')[1];
                ////////////////////////////////////////////////////////////////////////////////////////////////////
                string query1 = "INSERT INTO ORDER_CHECK VALUES(@Date_Time,@Order_Status,@Active,@Customer_ID,@BOffice)";
                SqlCommand sqlcmd = new SqlCommand(query1, connection);
                sqlcmd.Parameters.AddWithValue("@BOffice", resultOfficeF.Split(':')[1]);
                sqlcmd.Parameters.AddWithValue("@Date_Time", DateTime.Now);
                sqlcmd.Parameters.AddWithValue("@Order_Status", "Pendiente");
                sqlcmd.Parameters.AddWithValue("@Active", 1);
                sqlcmd.Parameters.AddWithValue("@Customer_ID", Convert.ToInt32(result.Split(':')[1]));
                sqlcmd.ExecuteNonQuery();




                ////////////////////////////////////////////
                //string Invoice = datasplit[0].Split(':')[0];
                string queryInvoice = "SELECT max(Invoice_ID) FROM [ORDER_CHECK] WHERE Customer_ID = " + Convert.ToInt32(result.Split(':')[1]);
                SqlCommand sqlcmdInv = new SqlCommand(queryInvoice, connection);
                SqlDataAdapter sda1 = new SqlDataAdapter(sqlcmdInv);
                DataSet dt1 = new DataSet();
                sda1.Fill(dt1);
                string result12 = JsonConvert.SerializeObject(dt1.Tables[0]);
                string result2 = result12.Remove(result12.Length - 1).Remove(result12.Length - 2);


                /////////////////////////////////////////



                //insert all the items in the purchase
                for (int i = 1; i < datasplit.Length; i++){
                    string query2 = "INSERT INTO PURCHASE_ITEM VALUES(@Invoice_ID,@Quantity,@Price,@Product_ID)";
                    SqlCommand sqlcmd1 = new SqlCommand(query2, connection);
                    sqlcmd1.Parameters.AddWithValue("@Invoice_ID", Convert.ToInt32(result2.Split(':')[1]));
                    sqlcmd1.Parameters.AddWithValue("@Quantity", Convert.ToInt32(datasplit[i].Split(':')[1]));
                    sqlcmd1.Parameters.AddWithValue("@Price", Convert.ToInt32(datasplit[i].Split(':')[2]));
                    sqlcmd1.Parameters.AddWithValue("@Product_ID", Convert.ToInt32(datasplit[i].Split(':')[0]));
                    sqlcmd1.ExecuteNonQuery();

                    string queryUpdate = "UPDATE PRODUCT SET Stock = Stock - @number WHERE ID_Product = @ID_Product";
                    SqlCommand sqlcmdUp = new SqlCommand(queryUpdate, connection);
                    sqlcmdUp.Parameters.AddWithValue("@number", Convert.ToInt32(datasplit[i].Split(':')[1]));
                    sqlcmdUp.Parameters.AddWithValue("@ID_Product", Convert.ToInt32(datasplit[i].Split(':')[0]));
                    sqlcmdUp.ExecuteNonQuery();
                }



            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }



        
            return  msg; 
        }
        //Obtiene las estadisticas segun los parametro requeridos
        public string Get_Stadistics(string str) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);

            string query = "";
            if (str == "bestseller"){
                query = "SELECT sum(QUANTITY) as Total, PRODUCT.Name FROM PURCHASE_ITEM FULL OUTER JOIN PRODUCT ON Product_ID = ID_Product group by (PRODUCT.name)";
            }
            else if (str == "salesperbranch"){
                query = "SELECT SUM(quantity) as total,BOffice.name from PURCHASE_ITEM left join ORDER_CHECK ON PURCHASE_ITEM.Invoice_ID= ORDER_CHECK.Invoice_ID left join BOFFICE  ON ORDER_CHECK.BOffice = BOffice.BOffice_ID group by (BOffice.name )";
            }
            else if (str == "bestsalesperbranch"){
                query = "SELECT SUM(quantity) as total,BOFFICE.Name,PRODUCT.Name AS nameP from PURCHASE_ITEM LEFT JOIN PRODUCT ON PURCHASE_ITEM.Product_ID = PRODUCT.ID_Product Left Join BOFFICE ON PRODUCT.BOffice = BOFFICE.BOffice_ID group by BOffice.Name,PRODUCT.Name";
            }

            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);


            return result;
        }
        //Obtiene las ordenes segun el usuario ingresado
        public string Get_Order(string Nickname) {
            //string Nickname = Nick.DataG;                 
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);

            connection.Open();
            string queryNickname = "SELECT ID_Card FROM [CUSTOMER] WHERE Nickname = " + "'"+Nickname+"'" ;
            SqlCommand sqlcmdNick = new SqlCommand(queryNickname, connection);
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmdNick);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables[0]);
            string result = result1.Remove(result1.Length - 1).Remove(result1.Length - 2);
            result = result.Split(':')[1];


            string queryOrder = "SELECT Date_Time,Order_Status,ORDER_CHECK.BOffice,PRODUCT.Name,ORDER_CHECK.Invoice_ID,PURCHASE_ITEM.Quantity,PURCHASE_ITEM.Price,(Quantity*PURCHASE_ITEM.Price) as TotalPrice FROM ORDER_CHECK FULL OUTER JOIN PURCHASE_ITEM ON ORDER_CHECK.Invoice_ID = PURCHASE_ITEM.Invoice_ID LEFT JOIN PRODUCT  ON Product_ID = ID_Product where Customer_ID = " + result;
            SqlCommand sqlcmdOrder = new SqlCommand(queryOrder, connection);
            SqlDataAdapter sdaOrder = new SqlDataAdapter(sqlcmdOrder);
            DataSet dtOrder = new DataSet();
            sdaOrder.Fill(dtOrder);
            string resultOrder = JsonConvert.SerializeObject(dtOrder.Tables[0]);
            

            connection.Close();
            return  resultOrder;
            //return "OK";
        }
        //Obtiene una sucursal especifica
        public string GetBoffice() {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);

           string query = "SELECT Name FROM BOFFICE ORDER BY BOffice_ID";
        

            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);


            return result;
        }
        //Obtiene un supplier especifico
        public string GetSupplier() {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);

            string query = "SELECT * FROM SUPPLIER WHERE Active = 1";


            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);


            return result;

        }
        //Obtiene todas las ordenes requeridas
        public string GetOrders(string str)
        {
            string query;
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            if (str == "all"){
                query = "SELECT * FROM ORDER_CHECK";
            }
            else { query = "SELECT * FROM ORDER_CHECK WHERE BOffice = " + Convert.ToInt32(str); }

            connection.Open();
            SqlCommand sqlcmd = new SqlCommand(query, connection);
            //sqlcmd.ExecuteNonQuery();
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmd);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables);
            string result = result1.Remove(result1.Length - 1).Remove(0, 1);


            return result;

        }
        //Elimina un supplier de la base de datos
        public string DeleteSupplier(string id) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            string query = "UPDATE SUPPLIER SET Active = 0 WHERE ID_Supplier = " + Convert.ToInt32(id);

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);




                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;

        }
        //Obtiene la cantidad de productos requerida
        public int Product_Quantity(int id) {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            connection.Open();
            string queryStock = "SELECT Stock FROM [PRODUCT] WHERE ID_Product = " + id;
            SqlCommand sqlcmdNick = new SqlCommand(queryStock, connection);
            SqlDataAdapter sda = new SqlDataAdapter(sqlcmdNick);
            DataSet dt = new DataSet();
            sda.Fill(dt);
            string result1 = JsonConvert.SerializeObject(dt.Tables[0]);
            string result = result1.Remove(result1.Length - 1).Remove(result1.Length - 2);
            connection.Close();
            return Convert.ToInt32(result.Split(':')[1]);
        }
        //Actualiza una orden segun los datos requeridos
        public string UpdateOrder(string id, string status )
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);
            
            string query = "UPDATE ORDER_CHECK SET Order_Status = @status WHERE Invoice_ID = @id ";

            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                sqlcmd.Parameters.AddWithValue("@id", id);
                sqlcmd.Parameters.AddWithValue("@status", status);

                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }

        //Recibe un objeto de tipo Category y lo registra en la base de datos
        public string PostBoffice(string Name)
        {
            string conn = ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString;
            SqlConnection connection = new SqlConnection(conn);

            string query = "INSERT INTO BOFFICE VALUES(@Name)";
            string msg = "all good";
            try
            {
                connection.Open();
                SqlCommand sqlcmd = new SqlCommand(query, connection);
                //sqlcmd.Parameters.AddWithValue("@ID_Category", str.ID_Category);
                sqlcmd.Parameters.AddWithValue("@Name", Name);
                sqlcmd.ExecuteNonQuery();
            }
            catch (System.Data.SqlClient.SqlException ex)
            {
                msg += " Insert Error:";
                msg += ex.Message;
            }
            finally
            {
                connection.Close();
            }

            return msg;
        }
        
    }
}
