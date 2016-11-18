package com.epatec.epatecmovil.dataAccess.DBTables_Classes;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Purchased_Item {

    //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

    public int _PURCHASEDITEM_ID;
    public int _Price;
    public int _Quantity;
    public int _INVOICE_ID;
    public int _PRODUCT_ID;




    public Purchased_Item(int pID,int pPrice,int pQuantity,int pInvoiceID,int pProductID){
        _PURCHASEDITEM_ID = pID;
        _Price = pPrice;
        _Quantity = pQuantity;
        _INVOICE_ID = pInvoiceID;
        _PRODUCT_ID = pProductID;
    }
}