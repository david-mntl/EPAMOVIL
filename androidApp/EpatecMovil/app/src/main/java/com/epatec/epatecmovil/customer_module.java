package com.epatec.epatecmovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.epatec.epatecmovil.EditActivities.EditCustomer;
import com.epatec.epatecmovil.RegisterActivities.RegisterCustomer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class customer_module extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_module);

        final ImageButton usButton = (ImageButton) findViewById(R.id.user_button);
        usButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent shopIntent = new Intent(customer_module.this, RegisterCustomer.class);
                startActivity(shopIntent);
            }
        });

        final ImageButton usButton2 = (ImageButton) findViewById(R.id.edit_user_button);
        usButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText pID = (EditText)findViewById(R.id.txt_clientID);
                if(pID.getText().toString().compareTo("") != 0) {
                    Intent editIntent = new Intent(customer_module.this, EditCustomer.class);
                    Bundle b = new Bundle();
                    b.putString("userID", pID.getText().toString()); //Your id
                    editIntent.putExtras(b); //Put your id to your next Intent
                    startActivity(editIntent);
                }
                else{
                    new SweetAlertDialog(customer_module.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Por favor digite el ID del cliente")
                            .show();
                }

            }
        });

    }

}
