package com.epatec.epatecmovil.dataAccess.DBTables_Classes;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Product {

    //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

    public int _ProductID;
    public int _BOffice;
    public String _Details;
    public boolean _Active;
    public boolean _TaxFree;
    public int _Stock;
    public String _Name;
    public int _Price;
    public int _SUPPLIER_ID;
    public int _CATEGORY_ID;



    public Product(int pID,int pBOffice,String pDetails,boolean pActive,boolean pTaxFree,int pStock, String pName, int pPrice, int pSupplierID, int pCategoryID){
        _ProductID = pID;
        _BOffice = pBOffice;
        _Details = pDetails;
        _Active = pActive;
        _TaxFree = pTaxFree;
        _Stock = pStock;
        _Name = pName;
        _Price = pPrice;
        _SUPPLIER_ID = pSupplierID;
        _CATEGORY_ID = pCategoryID;
    }
}