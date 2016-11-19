package com.epatec.epatecmovil.dataAccess.DBTables_Classes;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Category {

    //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

    public int _CATEGORYID;
    public boolean _Active;
    public String _Name;

    public Category(int pID, boolean pActive, String pName){
        _CATEGORYID = pID;
        _Active = pActive;
        _Name = pName;
    }
}