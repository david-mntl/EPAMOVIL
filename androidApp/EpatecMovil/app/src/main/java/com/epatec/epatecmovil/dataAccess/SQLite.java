package com.epatec.epatecmovil.dataAccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLite extends SQLiteOpenHelper {

    String customer_table = "CREATE TABLE Customer(" +
            "CUSTOMER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Name TEXT," +
            "LastName1 TEXT," +
            "LastName2 TEXT," +
            "Residence TEXT," +
            "Nickname TEXT UNIQUE," +
            "Password TEXT," +
            "BDate TEXT," +
            "Phone TEXT," +
            "Email TEXT," +
            "PriorityLevel INTEGER," +
            "Active NUMERIC"+
            ")";

    String product_table = "CREATE TABLE Product(" +
            "PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "BOffice INTEGER," +
            "Details TEXT," +
            "Active NUMERIC," +
            "TaxFree NUMERIC," +
            "Stock INTEGER," +
            "Name TEXT UNIQUE," +
            "Price INTEGER," +
            "SUPPLIER_ID INTEGER," +
            "CATEGORY_ID INTEGER" +
            ")";

    String ordercheck_table = "CREATE TABLE Order_Check(" +
            "INVOICE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "BOffice INTEGER," +
            "Date_Time TEXT," +
            "Order_Status INTEGER," +
            "Active NUMERIC," +
            "CUSTOMER_ID INTEGER," +
            "EMPLOYEE_ID INTEGER" +
            ")";

    String purchaseditem_table = "CREATE TABLE Purchased_Item(" +
            "PURCHASEDITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Price INTEGER," +
            "Quantity INTEGER," +
            "INVOICE_ID INTEGER," +
            "PRODUCT_ID INTEGER" +
            ")";

    String supplier_table = "CREATE TABLE Supplier(" +
            "SUPPLIER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Active NUMERIC," +
            "Name TEXT UNIQUE," +
            "Country TEXT," +
            "Phone TEXT" +
            ")";

    String category_table = "CREATE TABLE Category(" +
            "CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Name TEXT UNIQUE," +
            "Active NUMERIC NOT NULL" +
            ")";

    String seller_table = "CREATE TABLE Seller(" +
            "SELLER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Name TEXT," +
            "LastName1 TEXT," +
            "LastName2 TEXT," +
            "Residence TEXT," +
            "Nickname TEXT UNIQUE," +
            "Password TEXT," +
            "BDate TEXT," +
            "Phone TEXT," +
            "Email TEXT," +
            "Active NUMERIC" +
            ")";


    String increment1 = "INSERT INTO SQLITE_SEQUENCE VALUES('Customer',2500);";
    String increment2 = "INSERT INTO SQLITE_SEQUENCE VALUES('Product',2500);";
    String increment3 = "INSERT INTO SQLITE_SEQUENCE VALUES('Order_Check',2500);";
    String increment4 = "INSERT INTO SQLITE_SEQUENCE VALUES('Purchased_Item',2500);";
    String increment5 = "INSERT INTO SQLITE_SEQUENCE VALUES('Supplier',2500);";
    String increment6 = "INSERT INTO SQLITE_SEQUENCE VALUES('Category',2500);";
    String increment7 = "INSERT INTO SQLITE_SEQUENCE VALUES('Seller',2500);";


    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //It executes automatically when the database does not exist
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(customer_table);
        db.execSQL(product_table);
        db.execSQL(ordercheck_table);
        db.execSQL(purchaseditem_table);
        db.execSQL(supplier_table);
        db.execSQL(category_table);
        db.execSQL(seller_table);

        db.execSQL(increment1);
        db.execSQL(increment2);
        db.execSQL(increment3);
        db.execSQL(increment4);
        db.execSQL(increment5);
        db.execSQL(increment6);
        db.execSQL(increment7);
    }

    //When the database version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXITS Customer");
        db.execSQL(customer_table);
        db.execSQL("DROP TABLE IF EXITS Product");
        db.execSQL(customer_table);
        db.execSQL("DROP TABLE IF EXITS Order_Check");
        db.execSQL(ordercheck_table);
        db.execSQL("DROP TABLE IF EXITS Purchased_Item");
        db.execSQL(purchaseditem_table);
        db.execSQL("DROP TABLE IF EXITS Supplier");
        db.execSQL(supplier_table);
        db.execSQL("DROP TABLE IF EXITS Category");
        db.execSQL(category_table);
        db.execSQL("DROP TABLE IF EXITS Seller");
        db.execSQL(seller_table);
    }
}