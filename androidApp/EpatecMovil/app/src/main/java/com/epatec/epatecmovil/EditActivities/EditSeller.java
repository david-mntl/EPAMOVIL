package com.epatec.epatecmovil.EditActivities;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.UserDataHolder;
import com.epatec.epatecmovil.dataAccess.SQLite;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditSeller extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seller);


        final EditText customerID = (EditText)findViewById(R.id.txt_id);
        customerID.setEnabled(false);

        final UserDataHolder x = UserDataHolder.getInstance();

        final EditText pID = (EditText)findViewById(R.id.txt_id);
        final EditText pName = (EditText)findViewById(R.id.txt_name);
        final EditText pLast1 = (EditText)findViewById(R.id.txt_lastname1);
        final EditText pLast2 = (EditText)findViewById(R.id.txt_lastname2);
        final EditText pResidence = (EditText)findViewById(R.id.txt_residence);
        final TextView pDate = (TextView)findViewById(R.id.txtViewDate);
        final EditText pPhone = (EditText)findViewById(R.id.txt_phone);
        final EditText pMail = (EditText)findViewById(R.id.txt_mail);
        final EditText pUser = (EditText)findViewById(R.id.txt_user);
        final EditText pPass = (EditText)findViewById(R.id.txt_password);

        SQLite user = new SQLite(EditSeller.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();

        Cursor c = dbRead.rawQuery("SELECT * FROM Seller WHERE SELLER_ID=" + "\'" + x.userID + "\'", null);

        Log.i("IDSeller",x.userID);

        if (c.moveToFirst()) {
            do {
                //String codigo= c.getString(0);
                pID.setText(c.getString(0));
                pName.setText(c.getString(1));
                pLast1.setText(c.getString(2));
                pLast2.setText(c.getString(3));
                pResidence.setText(c.getString(4));
                pDate.setText(c.getString(7));
                pPhone.setText(c.getString(8));
                pMail.setText(c.getString(9));
                pUser.setText(c.getString(5));
                pPass.setText(c.getString(6));
            } while (c.moveToNext());

        }
        else{
            new SweetAlertDialog(EditSeller.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops")
                    .setContentText("No existe un usuario con el ID ingresado")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            EditSeller.this.finish();
                        }
                    })
                    .show();
        }

        final CheckBox checkBox = (CheckBox)findViewById(R.id.pass_checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    pPass.setTransformationMethod(null);
                }
                else{
                    pPass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveToDatabase();
            }
        });

        final Button delButton = (Button) findViewById(R.id.delete_button);
        delButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteUserFromDatabase();
            }
        });

    }

    private void deleteUserFromDatabase(){
        new SweetAlertDialog(EditSeller.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¡Atención!")
                .setContentText("¿Está seguro que desea eliminar al usuario?")
                .setCancelText("Cancelar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        final String pID = ((EditText)findViewById(R.id.txt_id)).getText().toString();

                        SQLite user = new SQLite(EditSeller.this, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();
                        db.execSQL("UPDATE Seller SET Active=0 WHERE SELLER_ID=" + "\'" + pID +"\'");

                        sDialog.dismissWithAnimation();
                        EditSeller.this.finish();
                    }
                })
                .show();
    }

    private void saveToDatabase() {
        final String pID = ((EditText)findViewById(R.id.txt_id)).getText().toString();
        final String pName = ((EditText)findViewById(R.id.txt_name)).getText().toString();
        final String pLast1 = ((EditText)findViewById(R.id.txt_lastname1)).getText().toString();
        final String pLast2 = ((EditText)findViewById(R.id.txt_lastname2)).getText().toString();
        final String pResidence = ((EditText)findViewById(R.id.txt_residence)).getText().toString();
        final String pDate = ((TextView)findViewById(R.id.txtViewDate)).getText().toString();
        final String pPhone = ((EditText)findViewById(R.id.txt_phone)).getText().toString();
        final String pMail = ((EditText)findViewById(R.id.txt_mail)).getText().toString();
        final String pUser = ((EditText)findViewById(R.id.txt_user)).getText().toString();
        final String pPass = ((EditText)findViewById(R.id.txt_password)).getText().toString();

        String[] info = {pID,pName,pLast1,pLast2,pResidence,pDate,pPhone,pMail,pUser,pPass};
        String[] errorMsgs = {"Ingrese un ID","Ingrese un nombre","Ingrese un apellido","Ingrese un apellido","Ingrese una residencia",
                "Ingrese una fecha","Ingrese un teléfono","Ingrese un correo electrónico","Ingrese un usuario","Ingrese una contraseña"};

        for(int i = 0; i <= info.length; i++){
            if( i == info.length){
                try {
                    SQLite user = new SQLite(EditSeller.this, "DBClientes", null, 1);
                    SQLiteDatabase db = user.getWritableDatabase();
                    db.execSQL("DELETE FROM Seller WHERE SELLER_ID=" + "\'" + pID +"\'");
                    db.execSQL("INSERT INTO Seller(SELLER_ID,Name,LastName1,LastName2,Residence,Nickname,Password,BDate,Phone,Email,Active) " +
                            "VALUES(" + "\'" + info[0] + "\'" + "," + "\'" + info[1] + "\'" + "," + "\'" + info[2] + "\'" + "," + "\'" + info[3] + "\'" + "," + "\'" + info[4] + "\'" + "," + "\'" + info[8] + "\'"
                            + "," + "\'" + info[9] + "\'" + "," + "\'" + info[5] + "\'" + "," + "\'" + info[6] + "\'" + "," + "\'" + info[7] + "\'" + ","
                            + "\'" + "1" + "\'" + ")");

                    new SweetAlertDialog(EditSeller.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Completado!")
                            .setContentText("Usuario actualizado con éxito")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    EditSeller.this.finish();
                                }
                            })
                            .show();

                }
                catch (SQLiteConstraintException e){
                    new SweetAlertDialog(EditSeller.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("El nombre de usuario o ID ya existe")
                            .show();
                }
                break;
            }
            if(info[i].compareTo("") ==0 ){
                showMessage(errorMsgs[i]);
                break;
            }
        }
    }

    public void showMessage(String message){
        new SweetAlertDialog(EditSeller.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops")
                .setContentText(message)
                .show();
    }

}
