package com.epatec.epatecmovil.dataAccess.DBTables_Classes;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Order_Check {

    //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

    public int _INVOICE_ID;
    public int _BOffice;
    public String _Date_Time;
    public String _Order_Status;
    public boolean _Active;
    public int _CUSTOMER_ID;
    public int _EMPLOYEE_ID;




    public Order_Check(int pID,int pOffice,String pDate,String pStatus,boolean pActive,int pCustomerID,int pEmployeeID){
        _INVOICE_ID = pID;
        _BOffice = pOffice;
        _Date_Time = pDate;
        _Order_Status = pStatus;
        _Active = pActive;
        _CUSTOMER_ID = pCustomerID;
        _EMPLOYEE_ID = pEmployeeID;
    }
}