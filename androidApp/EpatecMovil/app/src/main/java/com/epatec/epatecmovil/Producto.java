package com.epatec.epatecmovil;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Producto{
    int _ProductID;
    int _Quantity;
    int _Price;
    String _Name;
    public Producto(int id, int quantity,int price,String name){
        _ProductID = id;
        _Quantity = quantity;
        _Price = price;
        _Name = name;
    }
}