package com.epatec.epatecmovil;

/**
 * Created by Fabian on 19/9/2016.
 */
public class Producto{
    int _ProductID;
    int _Stock;
    int _Price;
    String _Name;
    String _Details;
    public Producto(int id, int stock,int price,String name,String details){
        _ProductID = id;
        _Stock = stock;
        _Price = price;
        _Name = name;
        _Details = details;
    }
}