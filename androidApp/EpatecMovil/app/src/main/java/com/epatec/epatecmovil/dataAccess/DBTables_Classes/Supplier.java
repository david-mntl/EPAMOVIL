package com.epatec.epatecmovil.dataAccess.DBTables_Classes;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Supplier {

    //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

    public int _SUPPLIER_ID;
    public boolean _Active;
    public String _Name;
    public String _Country;
    public String _Phone;




    public Supplier(int pID, boolean pActive,String pName,String pCountry,String pPhone){
        _SUPPLIER_ID = pID;
        _Active = pActive;
        _Name = pName;
        _Country = pCountry;
        _Phone = pPhone;
    }
}