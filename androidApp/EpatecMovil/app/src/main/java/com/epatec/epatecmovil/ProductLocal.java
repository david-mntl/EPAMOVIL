package com.epatec.epatecmovil;

/**
 * Created by Fabian on 19/9/2016.
 */
public class ProductLocal {
    public int _ProductID;
    public int _Stock;
    public int _Price;
    public String _Name;
    public String _Details;
    public ProductLocal(int id, int stock, int price, String name, String details){
        _ProductID = id;
        _Stock = stock;
        _Price = price;
        _Name = name;
        _Details = details;
    }
}