package com.epatec.epatecmovil;

import java.util.ArrayList;

public class UserDataHolder {
    private static UserDataHolder instance = null;

    public String user = "root"; //TODO Cambiarlo a str vacio
    public String userID = "";
    public String pUserDate = "";
    public int update_frequency = 0;
    public ArrayList<Producto> shoppingcart;

    private UserDataHolder() {
        shoppingcart = new ArrayList<>();
    }
    public static UserDataHolder getInstance() {
        if(instance == null) {
            instance = new UserDataHolder();
        }
        return instance;
    }
    public void addProductToShoppingCart(int id,int quantity,int price,String name){
        boolean inserted = false;
        if(shoppingcart.size() == 0) {
            Producto nuevo = new Producto(id, quantity,price,name,"");
            shoppingcart.add(nuevo);
        }
        else{
            for(int i = 0; i < shoppingcart.size(); i++){
                if(shoppingcart.get(i)._ProductID == id){
                    shoppingcart.get(i)._Stock += quantity;
                    inserted = true;
                }
            }
            if(inserted == false){
                Producto nuevo = new Producto(id, quantity,price,name,"");
                shoppingcart.add(nuevo);
            }
        }
    }

    public void deleteFromCart(int pID){
        for(int i = 0; i < shoppingcart.size(); i++){
            if(shoppingcart.get(i)._ProductID == pID){
                shoppingcart.remove(i);
            }
        }
    }

    public int getTotal(){
        int result = 0;

        for(int i = 0; i < shoppingcart.size(); i++){
            Producto current = shoppingcart.get(i);
            result += (current._Price * current._Stock);
        }

        return result;
    }
}
