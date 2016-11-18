package com.epatec.epatecmovil.dataAccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import com.epatec.epatecmovil.MainActivity;
import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Category;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Customer;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Order_Check;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Product;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Purchased_Item;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Seller;
import com.epatec.epatecmovil.dataAccess.DBTables_Classes.Supplier;
import android.content.SharedPreferences;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SyncroDBHandler {

    AsyncTaskConnector _connector;
    AsyncTaskPublisher _publisher;
    Context _context;

    static SyncroDBHandler _instance;
    JSONArray productsInfo = null;
    String currentWebServiceCall = "";
    boolean ready = false;

    public SyncroDBHandler(){

    }

    public void syncronizeDatabase(Context pContext){
        _context = pContext;
        _connector = new AsyncTaskConnector();
        _connector.execute("init");
    }

    public void publishDatabase(Context pContext){
        _context = pContext;
        _publisher = new AsyncTaskPublisher();
        _publisher.execute("init");

    }


    private class AsyncTaskConnector extends AsyncTask<String, String, String> {


        ArrayList<Product> listaInfoProductLocals = new ArrayList<>();
        ArrayList<Seller> listaInfoSellers = new ArrayList<>();
        ArrayList<Supplier> listaInfoSuppliers = new ArrayList<>();
        ArrayList<Customer> listaInfoCustomers = new ArrayList<>();
        ArrayList<Order_Check> listaInfoOrders = new ArrayList<>();
        ArrayList<Category> listaInfoCategory = new ArrayList<>();
        ArrayList<Purchased_Item> listaInfoPurchase = new ArrayList<>();


        // convert inputstream to String
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        public String convertStandardJSONString(String data_json) {
            data_json = data_json.replaceAll("\\\\r\\\\n", "");
            data_json = data_json.replace("\\", "");
            return data_json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {

            String[] webServices = {"GetProducts?params=all","GetAllEmployee/","GetAllSupplier/","GetAllCustomer/","GetAllOrder/","GetAllCategory/","GetAllPurchase/","END"};
            SharedPreferences settings  = _context.getSharedPreferences("settingsFile",0);

            String localMode = settings.getString("localMode", "");
            String localIP = settings.getString("localIP", "");

            String serverURL = "";

            if(localMode.compareTo("true") == 0) {
                serverURL = localIP;
            }
            else{
                serverURL=_context.getString(R.string.webservice_url);
            }


            InputStream inputStream;
            String result = "";

            for(int i = 0; i < webServices.length; i++) {
                ready = false;
                try {

                    // create HttpClient
                    HttpClient httpclient = new DefaultHttpClient();

                    // make GET request to the given URL
                    //HttpResponse httpResponse = httpclient.execute(new HttpGet("http://cewebserver.azurewebsites.net/Service1.svc/GetProducts?params=all"));
                    HttpResponse httpResponse = httpclient.execute(new HttpGet(serverURL + "/Service1.svc/" + webServices[i]));
                    // receive response as inputStream
                    inputStream = httpResponse.getEntity().getContent();

                    // convert inputstream to string
                    if (inputStream != null)
                        result = convertInputStreamToString(inputStream);
                    else
                        result = "error";

                } catch (Exception e) {
                    Log.e("ERRSyn", e.toString());
                    // publishProgress(e.toString());
                }

                currentWebServiceCall = webServices[i];
                publishProgress(result);
                while(!ready){
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            if(progress[0].compareTo("error") != 0) {
                if(currentWebServiceCall.compareTo("GetProducts?params=all") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Productos");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pProductID;
                            int pBOffice;
                            String pDetails;
                            boolean pActive;
                            boolean pTaxFree;
                            int pStock;
                            String pName;
                            int pPrice;
                            int pSUPPLIER_ID;
                            int pCATEGORY_ID;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pProductID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Product"));
                                pBOffice = Integer.parseInt(productsInfo.getJSONObject(x).getString("BOffice"));
                                pDetails = productsInfo.getJSONObject(x).getString("Details");
                                pActive = Boolean.parseBoolean(productsInfo.getJSONObject(x).getString("Active"));
                                pTaxFree = Boolean.parseBoolean(productsInfo.getJSONObject(x).getString("TaxFree"));
                                pStock = Integer.parseInt(productsInfo.getJSONObject(x).getString("Stock"));
                                pName = productsInfo.getJSONObject(x).getString("Name");
                                pPrice = Integer.parseInt(productsInfo.getJSONObject(x).getString("Price"));
                                pSUPPLIER_ID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Supplier"));
                                pCATEGORY_ID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Category"));


                                Product newProduct = new Product(pProductID, pBOffice, pDetails, pActive, pTaxFree, pStock, pName, pPrice, pSUPPLIER_ID, pCATEGORY_ID);
                                listaInfoProductLocals.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoProductLocals.size(); x++) {
                            Product inf = listaInfoProductLocals.get(x);

                            try {
                                db.execSQL("INSERT INTO Product(PRODUCT_ID,BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID) " +
                                        "VALUES(" +
                                        "\'" + inf._ProductID + "\'" + "," +
                                        "\'" + inf._BOffice + "\'" + "," +
                                        "\'" + inf._Details + "\'" + "," +
                                        "\'" + inf._Active + "\'" + "," +
                                        "\'" + inf._TaxFree + "\'" + "," +
                                        "\'" + inf._Stock + "\'" + "," +
                                        "\'" + inf._Name + "\'" + "," +
                                        "\'" + inf._Price + "\'" + "," +
                                        "\'" + inf._SUPPLIER_ID + "\'" + "," +
                                        "\'" + inf._CATEGORY_ID + "\'" + ")");
                                Log.i("GDD", "Producto " + inf._ProductID + " ingresado");
                            } catch (SQLiteConstraintException e) {
                                Log.i("GDD", "Producto actualizado");
                                db.execSQL("UPDATE Product SET " +
                                        "BOffice=" + "\'" + inf._BOffice + "\'" + "," +
                                        "Details=" + "\'" + inf._Details + "\'" + "," +
                                        "Active=" + "\'" + inf._Active + "\'" + "," +
                                        "TaxFree=" + "\'" + inf._TaxFree + "\'" + "," +
                                        "Stock=" + "\'" + inf._Stock + "\'" + "," +
                                        "Name=" + "\'" + inf._Name + "\'" + "," +
                                        "Price=" + "\'" + inf._Price + "\'" + "," +
                                        "SUPPLIER_ID=" + "\'" + inf._SUPPLIER_ID + "\'" + "," +
                                        "CATEGORY_ID=" + "\'" + inf._CATEGORY_ID + "\'" +
                                        "WHERE PRODUCT_ID=" + "\'" + inf._ProductID + "\'");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Productos)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("GetAllEmployee/") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Vendedores");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pSellerID;
                            String pName;
                            String pLastName1;
                            String pLastName2;
                            String pResidence;
                            String pNickname;
                            String pPassword;
                            String pBDate;
                            String pPhone;
                            String pEmail;
                            boolean pActive;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pSellerID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Employee"));
                                pName = productsInfo.getJSONObject(x).getString("Name");
                                pLastName1 = productsInfo.getJSONObject(x).getString("LastName1");
                                pLastName2 = productsInfo.getJSONObject(x).getString("LastName2");
                                pResidence = productsInfo.getJSONObject(x).getString("Residence");
                                pNickname = productsInfo.getJSONObject(x).getString("Nickname");
                                pPassword = productsInfo.getJSONObject(x).getString("Secure_Pass");
                                pBDate = productsInfo.getJSONObject(x).getString("BDate");
                                pPhone = productsInfo.getJSONObject(x).getString("Phone");
                                pEmail = productsInfo.getJSONObject(x).getString("Email");
                                pActive = Boolean.parseBoolean(productsInfo.getJSONObject(x).getString("Active"));


                                Seller newProduct = new Seller(pSellerID,pName,pLastName1,pLastName2,pResidence,pNickname,pPassword,pBDate,pPhone,pEmail,pActive);
                                listaInfoSellers.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoSellers.size(); x++) {
                            Seller inf = listaInfoSellers.get(x);

                            try {
                                db.execSQL("INSERT INTO Seller(SELLER_ID,Name,LastName1,LastName2,Residence,Nickname,Password,BDate,Phone,Email,Active) " +
                                        "VALUES(" +
                                        "\'" + inf._SellerID + "\'" + "," +
                                        "\'" + inf._Name + "\'" + "," +
                                        "\'" + inf._LastName1 + "\'" + "," +
                                        "\'" + inf._LastName2 + "\'" + "," +
                                        "\'" + inf._Residence + "\'" + "," +
                                        "\'" + inf._Nickname + "\'" + "," +
                                        "\'" + inf._Password + "\'" + "," +
                                        "\'" + inf._BDate + "\'" + "," +
                                        "\'" + inf._Phone + "\'" + "," +
                                        "\'" + inf._Email + "\'" + "," +
                                        "\'" + inf._Active + "\'" + ")");
                                Log.i("GDD", "Vendedor " + inf._SellerID + " ingresado");
                            } catch (SQLiteConstraintException e) {
                                db.execSQL("UPDATE Seller SET " +
                                        "Name=" + "\'" + inf._Name + "\'" + "," +
                                        "LastName1=" + "\'" + inf._LastName1 + "\'" + "," +
                                        "LastName2=" + "\'" + inf._LastName2 + "\'" + "," +
                                        "Residence=" + "\'" + inf._Residence + "\'" + "," +
                                        "Nickname=" + "\'" + inf._Nickname + "\'" + "," +
                                        "Password=" + "\'" + inf._Password + "\'" + "," +
                                        "BDate=" + "\'" + inf._BDate + "\'" + "," +
                                        "Phone=" + "\'" + inf._Phone + "\'" + "," +
                                        "Email=" + "\'" + inf._Email + "\'" + "," +
                                        "Active=" + "\'" + inf._Active + "\'" +
                                        "WHERE SELLER_ID=" + "\'" + inf._SellerID + "\'");
                                Log.i("GDD", "Vendedor actualizado");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Vendedores)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("GetAllSupplier/") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Proveedores");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pSupplierID;
                            boolean pActive;
                            String pName;
                            String pCountry;
                            String pPhone;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pSupplierID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Supplier"));
                                pActive = Boolean.parseBoolean(productsInfo.getJSONObject(x).getString("Active"));
                                pName = productsInfo.getJSONObject(x).getString("Name");
                                pCountry = productsInfo.getJSONObject(x).getString("Country");
                                pPhone = productsInfo.getJSONObject(x).getString("Phone");



                                Supplier newProduct = new Supplier(pSupplierID,pActive,pName,pCountry,pPhone);
                                listaInfoSuppliers.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoSuppliers.size(); x++) {
                            Supplier inf = listaInfoSuppliers.get(x);

                            try {
                                db.execSQL("INSERT INTO Supplier(SUPPLIER_ID,Active,Name,Country,Phone) " +
                                        "VALUES(" +
                                        "\'" + inf._SUPPLIER_ID + "\'" + "," +
                                        "\'" + inf._Active + "\'" + "," +
                                        "\'" + inf._Name + "\'" + "," +
                                        "\'" + inf._Country + "\'" + "," +
                                        "\'" + inf._Phone + "\'" + ")");
                                Log.i("GDD", "Proveedor " + inf._SUPPLIER_ID + " ingresado");
                            } catch (SQLiteConstraintException e) {
                                Log.i("GDD", "Proveedor actualizado");
                                db.execSQL("UPDATE Supplier SET " +
                                        "Active=" + "\'" + inf._Active + "\'" + "," +
                                        "Name=" + "\'" + inf._Name + "\'" + "," +
                                        "Country=" + "\'" + inf._Country + "\'" + "," +
                                        "Phone=" + "\'" + inf._Phone + "\'" +
                                        "WHERE SUPPLIER_ID=" + "\'" + inf._SUPPLIER_ID + "\'");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        ready= true;
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Proveedores)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("GetAllCustomer/") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Clientes");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pCustomerID;
                            String pName;
                            String pLastName1;
                            String pLastName2;
                            String pResidence;
                            String pNickname;
                            String pPassword;
                            String pBDate;
                            String pPhone;
                            String pEmail;
                            boolean pActive;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pCustomerID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Card"));
                                pName = productsInfo.getJSONObject(x).getString("Name");
                                pLastName1 = productsInfo.getJSONObject(x).getString("LastName1");
                                pLastName2 = productsInfo.getJSONObject(x).getString("LastName2");
                                pResidence = productsInfo.getJSONObject(x).getString("Residence");
                                pNickname = productsInfo.getJSONObject(x).getString("Nickname");
                                pPassword = productsInfo.getJSONObject(x).getString("Secure_Pass");
                                pBDate = productsInfo.getJSONObject(x).getString("BDate");
                                pPhone = productsInfo.getJSONObject(x).getString("Phone");
                                pEmail = productsInfo.getJSONObject(x).getString("Email");
                                pActive = Boolean.parseBoolean(productsInfo.getJSONObject(x).getString("Active"));


                                Customer newProduct = new Customer(pCustomerID,pName,pLastName1,pLastName2,pResidence,pNickname,pPassword,pBDate,pPhone,pEmail,pActive);
                                listaInfoCustomers.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoCustomers.size(); x++) {
                            Customer inf = listaInfoCustomers.get(x);

                            try {
                                db.execSQL("INSERT INTO Customer(CUSTOMER_ID,Name,LastName1,LastName2,Residence,Nickname,Password,BDate,Phone,Email,Active) " +
                                        "VALUES(" +
                                        "\'" + inf._CUSTOMER_ID + "\'" + "," +
                                        "\'" + inf._Name + "\'" + "," +
                                        "\'" + inf._LastName1 + "\'" + "," +
                                        "\'" + inf._LastName2 + "\'" + "," +
                                        "\'" + inf._Residence + "\'" + "," +
                                        "\'" + inf._Nickname + "\'" + "," +
                                        "\'" + inf._Password + "\'" + "," +
                                        "\'" + inf._BDate + "\'" + "," +
                                        "\'" + inf._Phone + "\'" + "," +
                                        "\'" + inf._Email + "\'" + "," +
                                        "\'" + inf._Active + "\'" + ")");
                                Log.i("GDD", "Cliente " + inf._CUSTOMER_ID + " ingresado");
                            } catch (SQLiteConstraintException e) {
                                db.execSQL("UPDATE Customer SET " +
                                        "Name=" + "\'" + inf._Name + "\'" + "," +
                                        "LastName1=" + "\'" + inf._LastName1 + "\'" + "," +
                                        "LastName2=" + "\'" + inf._LastName2 + "\'" + "," +
                                        "Residence=" + "\'" + inf._Residence + "\'" + "," +
                                        "Nickname=" + "\'" + inf._Nickname + "\'" + "," +
                                        "Password=" + "\'" + inf._Password + "\'" + "," +
                                        "BDate=" + "\'" + inf._BDate + "\'" + "," +
                                        "Phone=" + "\'" + inf._Phone + "\'" + "," +
                                        "Email=" + "\'" + inf._Email + "\'" + "," +
                                        "Active=" + "\'" + inf._Active + "\'" +
                                        "WHERE CUSTOMER_ID=" + "\'" + inf._CUSTOMER_ID + "\'");
                                Log.i("GDD", "Cliente actualizado");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Clientes)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("GetAllOrder/") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Órdenes");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pInvoiceID;
                            int pBOffice;
                            String pDate;
                            String pStatus;
                            boolean pActive;
                            int pCustomerID;
                            int pEmployeeID;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pInvoiceID = Integer.parseInt(productsInfo.getJSONObject(x).getString("Invoice_ID"));
                                pBOffice = Integer.parseInt(productsInfo.getJSONObject(x).getString("BOffice"));
                                pDate = productsInfo.getJSONObject(x).getString("Date_Time");
                                pStatus = productsInfo.getJSONObject(x).getString("Order_Status");
                                pActive = Boolean.parseBoolean(productsInfo.getJSONObject(x).getString("Active"));
                                pCustomerID = Integer.parseInt(productsInfo.getJSONObject(x).getString("Customer_ID"));
                                pEmployeeID = Integer.parseInt(productsInfo.getJSONObject(x).getString("Employee_ID"));



                                Order_Check newProduct = new Order_Check(pInvoiceID,pBOffice,pDate,pStatus,pActive,pCustomerID,pEmployeeID);
                                listaInfoOrders.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoOrders.size(); x++) {
                            Order_Check inf = listaInfoOrders.get(x);

                            try {
                                db.execSQL("INSERT INTO Order_Check(INVOICE_ID,BOffice,Date_Time,Order_Status,Active,CUSTOMER_ID,EMPLOYEE_ID) " +
                                        "VALUES(" +
                                        "\'" + inf._INVOICE_ID + "\'" + "," +
                                        "\'" + inf._BOffice + "\'" + "," +
                                        "\'" + inf._Date_Time + "\'" + "," +
                                        "\'" + inf._Order_Status + "\'" + "," +
                                        "\'" + inf._Active + "\'" + "," +
                                        "\'" + inf._CUSTOMER_ID + "\'" + "," +
                                        "\'" + inf._EMPLOYEE_ID + "\'" + ")");
                                Log.i("GDD", "Orden " + inf._INVOICE_ID + " ingresado");
                            } catch (SQLiteConstraintException e) {
                                Log.i("GDD", "Orden actualizado");
                                db.execSQL("UPDATE Order_Check SET " +
                                        "BOffice=" + "\'" + inf._BOffice + "\'" + "," +
                                        "Date_Time=" + "\'" + inf._Date_Time + "\'" + "," +
                                        "Order_Status=" + "\'" + inf._Order_Status + "\'" + "," +
                                        "Active=" + "\'" + inf._Active + "\'" + "," +
                                        "CUSTOMER_ID=" + "\'" + inf._CUSTOMER_ID + "\'" + "," +
                                        "EMPLOYEE_ID=" + "\'" + inf._EMPLOYEE_ID + "\'" +
                                        "WHERE INVOICE_ID=" + "\'" + inf._INVOICE_ID + "\'");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        ready= true;
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Órdenes)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("GetAllCategory/") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Categorías");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pCategoryID;
                            boolean pActive;
                            String pName;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pCategoryID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Category"));
                                pName = productsInfo.getJSONObject(x).getString("Details");

                                //pActive = productsInfo.getJSONObject(x).getString("Name");


                                Category newProduct = new Category(pCategoryID,true,pName);
                                listaInfoCategory.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoCategory.size(); x++) {
                            Category inf = listaInfoCategory.get(x);

                            try {
                                db.execSQL("INSERT INTO Category(CATEGORY_ID,Name,Active) " +
                                        "VALUES(" +
                                        "\'" + inf._CATEGORYID + "\'" + "," +
                                        "\'" + inf._Name + "\'" + "," +
                                        "\'" + inf._Active + "\'" + ")");
                                Log.i("GDD", "Categoria " + inf._CATEGORYID + " ingresada");
                            } catch (SQLiteConstraintException e) {
                                Log.i("GDD", "Categoria actualizada");
                                db.execSQL("UPDATE Category SET " +
                                        "Name=" + "\'" + inf._Name + "\'" + "," +
                                        "Active=" + "\'" + inf._Active + "\'" +
                                        "WHERE CATEGORY_ID=" + "\'" + inf._CATEGORYID + "\'");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        ready= true;
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Categorías)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("GetAllPurchase/") == 0) {
                    MainActivity.dbupdate_alert.setContentText("Sincronizando Compras");
                    try {
                        String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
                        result = convertStandardJSONString(result);

                        try {
                            productsInfo = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < productsInfo.length(); x++) {

                            int pPurcharseID;
                            int pPrice;
                            int pQuantity;
                            int pInvoiceID;
                            int pProductID;

                            try {
                                //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

                                pPurcharseID = Integer.parseInt(productsInfo.getJSONObject(x).getString("PurchasedItem_ID"));
                                pPrice = Integer.parseInt(productsInfo.getJSONObject(x).getString("Price"));
                                pQuantity = Integer.parseInt(productsInfo.getJSONObject(x).getString("Quantity"));
                                pInvoiceID = Integer.parseInt(productsInfo.getJSONObject(x).getString("Invoice_ID"));
                                pProductID = Integer.parseInt(productsInfo.getJSONObject(x).getString("Product_ID"));



                                Purchased_Item newProduct = new Purchased_Item(pPurcharseID,pPrice,pQuantity,pInvoiceID,pProductID);
                                listaInfoPurchase.add(newProduct);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SQLite user = new SQLite(_context, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();

                        for (int x = 0; x < listaInfoPurchase.size(); x++) {
                            Purchased_Item inf = listaInfoPurchase.get(x);

                            try {
                                db.execSQL("INSERT INTO Purchased_Item(PURCHASEDITEM_ID,Price,Quantity,INVOICE_ID,PRODUCT_ID) " +
                                        "VALUES(" +
                                        "\'" + inf._PURCHASEDITEM_ID + "\'" + "," +
                                        "\'" + inf._Price + "\'" + "," +
                                        "\'" + inf._Quantity + "\'" + "," +
                                        "\'" + inf._INVOICE_ID + "\'" + "," +
                                        "\'" + inf._PRODUCT_ID + "\'" + ")");
                                Log.i("GDD", "Compra " + inf._PURCHASEDITEM_ID + " ingresada");
                            } catch (SQLiteConstraintException e) {
                                Log.i("GDD", "Compra actualizado");
                                db.execSQL("UPDATE Purchased_Item SET " +
                                        "Price=" + "\'" + inf._Price + "\'" + "," +
                                        "Quantity=" + "\'" + inf._Quantity + "\'" + "," +
                                        "INVOICE_ID=" + "\'" + inf._INVOICE_ID + "\'" + "," +
                                        "PRODUCT_ID=" + "\'" + inf._PRODUCT_ID + "\'" +
                                        "WHERE PURCHASEDITEM_ID=" + "\'" + inf._PURCHASEDITEM_ID + "\'");
                            }
                        }

                        //TODO CORREGIR
                        //MainActivity.dbupdate_alert.dismissWithAnimation();
                        ready = true;
                    } catch (Exception e) {
                        ready= true;
                        MainActivity.dbupdate_alert.dismissWithAnimation();
                        new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Error de conexión con el servidor (Compras)")
                                .show();
                    }
                }

                else if(currentWebServiceCall.compareTo("END") == 0){
                    ready= true;
                    MainActivity.dbupdate_alert.dismissWithAnimation();
                }
            }
            else{
                new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Error de conexión")
                        .show();
            }

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }



    private class AsyncTaskPublisher extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {

            /*****************************************************************/
            //LISTAS DE INFORMACIÓN

            ArrayList<Category> listaInfoCategory = new ArrayList<>();
            ArrayList<Seller> listaInfoSellers = new ArrayList<>();

            /*****************************************************************/

            String[] webServices = {"/PostNEWCategory","/PostNEWEmployee"};
            SQLite user = new SQLite(_context, "DBClientes", null, 1);
            SQLiteDatabase dbRead = user.getReadableDatabase();

            SharedPreferences settings  = _context.getSharedPreferences("settingsFile", 0);
            String localMode = settings.getString("localMode", "");
            String localIP = settings.getString("localIP", "");
            String serverURL = "";

            if(localMode.compareTo("true") == 0)
                serverURL = localIP;
            else
                serverURL=_context.getString(R.string.webservice_url);



            /*****************************************************************/
            //CATEGORIAS

            Cursor c1 = dbRead.rawQuery("SELECT * FROM Category", null);
            if (c1.moveToFirst()) {
                do {
                    int pID = Integer.parseInt(c1.getString(0));
                    String pName = c1.getString(1);
                    boolean pActive = Boolean.parseBoolean(c1.getString(2));

                    Category newCat = new Category(pID,pActive,pName);
                    listaInfoCategory.add(newCat);

                } while(c1.moveToNext());
            }

            Cursor c2 = dbRead.rawQuery("SELECT * FROM Seller", null);
            if (c2.moveToFirst()) {
                do {
                    int pSellerID = Integer.parseInt(c2.getString(0));
                    String pName = c2.getString(1);
                    String pLastName1 = c2.getString(2);
                    String pLastName2 = c2.getString(3);
                    String pResidence = c2.getString(4);
                    String pNickname = c2.getString(5);
                    String pPassword = c2.getString(6);
                    String pDate = c2.getString(7);
                    String pPhone = c2.getString(8);
                    String pEmail = c2.getString(9);
                    boolean pActive = Boolean.parseBoolean(c2.getString(10));

                    Seller newCat = new Seller(pSellerID,pName,pLastName1,pLastName2,pResidence,pNickname,pPassword,pDate,pPhone,pEmail,pActive);
                    listaInfoSellers.add(newCat);

                } while(c2.moveToNext());
            }


            /**************************************************************/
            // CATEGORIAS
            for(int i = 0; i < listaInfoCategory.size(); i++) {

                Category inf = listaInfoCategory.get(i);
                String message = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    URL url = new URL(serverURL + "/Service1.svc" + webServices[0]);
                    jsonObject.put("ID_Category", inf._CATEGORYID);
                    jsonObject.put("Details",inf._Name);
                    message = jsonObject.toString();

                    publishProgress("Categorías");
                    doOnlineRequest(url,message);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            //{ "ID_Card": 39999, "Name": "Percebe3 super feo","LastName1":"mu3y","LastName2":"F3eo",
            // "Residence": "CR", "Nickname": "perci3", "Secure_Pass": "123456", "BDate": "04/04/1992",
            // "Phone": 87864174, "Email": "tvlenin@gmai3l.com", "PriorityLevel": 1 }

            //VENDEDORES
            for(int i = 0; i < listaInfoSellers.size(); i++) {

                Seller inf = listaInfoSellers.get(i);
                String message = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    URL url = new URL(serverURL + "/Service1.svc" + webServices[1]);
                    jsonObject.put("ID_Card", inf._SellerID);
                    jsonObject.put("Name", inf._Name);
                    jsonObject.put("LastName1", inf._LastName1);
                    jsonObject.put("LastName2", inf._LastName2);
                    jsonObject.put("Residence", inf._Residence);
                    jsonObject.put("Nickname", inf._Nickname);
                    jsonObject.put("Secure_Pass", inf._Password);
                    jsonObject.put("BDate", inf._BDate);
                    jsonObject.put("Phone", inf._Phone);
                    jsonObject.put("Email", inf._Email);
                    jsonObject.put("PriorityLevel", inf._Active);
                    message = jsonObject.toString();

                    publishProgress("Vendedores");
                    doOnlineRequest(url,message);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            /****************************************************************/

            return "";
        }

        public void doOnlineRequest(URL url, String message){

            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;

            try {
                //constants



                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);

                //make some HTTP header nicety
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //open
                conn.connect();

                //setup send
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                //clean up
                os.flush();

                //do somehting with response
                is = conn.getInputStream();

                StringBuffer response;


                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                publishProgress(response.toString());


            } catch (Exception e) {
                publishProgress(e.toString());
            } finally {
                //clean up
                try {
                    os.close();
                    is.close();
                } catch (IOException e) {
                    publishProgress(e.toString());
                }

                conn.disconnect();
            }
        }


        @Override
        protected void onProgressUpdate(String... progress) {
            //char[] response = progress[0].toCharArray();
            //Log.e("RESPO",progress[0]);
            MainActivity.dbupload_alert.setContentText("Subiendo información: "+ progress[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MainActivity.dbupload_alert.dismissWithAnimation();
        }
    }
}
