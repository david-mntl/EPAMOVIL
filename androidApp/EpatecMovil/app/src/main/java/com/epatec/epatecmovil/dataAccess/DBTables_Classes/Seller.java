package com.epatec.epatecmovil.dataAccess.DBTables_Classes;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Seller {

    //(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID)

    public int _SellerID;
    public String _Name;
    public String _LastName1;
    public String _LastName2;
    public String _Residence;
    public String _Nickname;
    public String _Password;
    public String _BDate;
    public String _Phone;
    public String _Email;
    public boolean _Active;




    public Seller(int pSellerID,String pName,String pLastName1,String pLastName2,String pResidence,
                  String pNickname,String pPassword,String pBDate,String pPhone,String pEmail,boolean pActive){

        _SellerID = pSellerID;
        _Name = pName;
        _LastName1 = pLastName1;
        _LastName2 = pLastName2;
        _Residence = pResidence;
        _Nickname = pNickname;
        _Password = pPassword;
        _BDate = pBDate;
        _Phone = pPhone;
        _Email = pEmail;
        _Active = pActive;
    }
}