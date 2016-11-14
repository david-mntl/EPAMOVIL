package com.epatec.epatecmovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.epatec.epatecmovil.EditActivities.EditCategory;
import com.epatec.epatecmovil.EditActivities.EditProduct;
import com.epatec.epatecmovil.EditActivities.EditSupplier;
import com.epatec.epatecmovil.RegisterActivities.RegisterCategory;
import com.epatec.epatecmovil.RegisterActivities.RegisterProduct;
import com.epatec.epatecmovil.RegisterActivities.RegisterSupplier;

public class supplier_module extends ActionBarActivity {

    boolean editFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_module);

        final ImageButton products_but = (ImageButton) findViewById(R.id.products_button);
        products_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editFlag == false) {
                    Intent intentA = new Intent(supplier_module.this, RegisterProduct.class);
                    startActivity(intentA);
                } else {
                    Intent intentB = new Intent(supplier_module.this, EditProduct.class);
                    startActivity(intentB);
                }
            }
        });

        final ImageButton supplier_but = (ImageButton) findViewById(R.id.supplier_button);
        supplier_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editFlag == false) {
                    Intent intentA = new Intent(supplier_module.this, RegisterSupplier.class);
                    startActivity(intentA);
                } else {
                    Intent intentB = new Intent(supplier_module.this, EditSupplier.class);
                    startActivity(intentB);
                }
            }
        });

        final ImageButton category_but = (ImageButton) findViewById(R.id.category_button);
        category_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editFlag == false) {
                    Intent intentA = new Intent(supplier_module.this, RegisterCategory.class);
                    startActivity(intentA);
                } else {
                    Intent intentB = new Intent(supplier_module.this, EditCategory.class);
                    startActivity(intentB);
                };
            }
        });

        final CheckBox  editCheck = (CheckBox)findViewById(R.id.edit_checkbox);
        editCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editFlag = !editFlag;
                if(editFlag == false){
                    products_but.setBackgroundResource(R.drawable.box);
                    supplier_but.setBackgroundResource(R.drawable.supplier);
                    category_but.setBackgroundResource(R.drawable.category);
                }
                else{
                    products_but.setBackgroundResource(R.drawable.box_blue);
                    supplier_but.setBackgroundResource(R.drawable.supplier_blue);
                    category_but.setBackgroundResource(R.drawable.category_blue);
                }

            }
        });
    }

}
