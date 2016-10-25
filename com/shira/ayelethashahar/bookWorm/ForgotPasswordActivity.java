package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Client;
import storeComponents.Enums;
import storeComponents.User;
import storeComponents.Vendor;

/**
 * Created by AyeletHaShachar on 10/12/2015.
 */
public class ForgotPasswordActivity extends Activity {

    Backend store = BackendFactory.getInstance();
    Intent fromLoginActivity;

    public void onCreate(Bundle savedInstanceState) {

        fromLoginActivity = getIntent();
        super.onCreate(savedInstanceState);
        //connect the xml file to its activity
        setContentView(R.layout.forgot_password_window);

        final EditText user_name = (EditText) findViewById(R.id.editTextUserName);
        final EditText user_mail =  (EditText) findViewById(R.id.editTextMail);
        final Button send = (Button) findViewById(R.id.buttonSend);
        //final TextView user_em = (TextView) findViewById(R.id.textViewEm);
        //user_em.setVisibility(View.INVISIBLE);
        //final TextView user_email = (TextView) findViewById(R.id.textViewEmail);
        //user_email.setVisibility(View.INVISIBLE);
        //final Button restore = (Button) findViewById(R.id.buttonRestorePass);
        //restore.setVisibility(View.INVISIBLE);
        /*next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            *//**
                                             * if this user is not signed in the book-store-system
                                             *//*
                                            *//*if (!(store.isVendor(user_name.getText().toString())) || (store.find_index_ven(user_name.getText().toString()) == -1)) {
                                                Toast.makeText(ForgotPasswordActivity.this, "make sure you insert the right username", Toast.LENGTH_LONG).show();
                                            } else {*//*
                                                switch (fromLoginActivity.getStringExtra("permission")) {
                                                    case ("CLIENT"):
                                                        Client c = store.getClient(user_name.getText().toString());
                                                        if (c == null)
                                                            Toast.makeText(ForgotPasswordActivity.this, "this client doesn't exist", Toast.LENGTH_SHORT).show();
                                                        else {
                                                            user_email.setText(c.getEmail());
                                                            user_em.setVisibility(View.VISIBLE);
                                                            user_email.setVisibility(View.VISIBLE);
                                                            restore.setVisibility(View.VISIBLE);
                                                        }
                                                        break;
                                                    case ("VENDOR"):
                                                        Vendor v2 = store.getVendor(user_name.getText().toString());
                                                        if (v2 == null)
                                                            Toast.makeText(ForgotPasswordActivity.this, "this vendor doesn't exist", Toast.LENGTH_SHORT).show();
                                                        else {
                                                            user_email.setText(v2.getEmail());
                                                            user_em.setVisibility(View.VISIBLE);
                                                            user_email.setVisibility(View.VISIBLE);
                                                            restore.setVisibility(View.VISIBLE);
                                                        }
                                                        break;
                                                    default:
                                                }
                                            //}
                                        } catch (Exception ex) {
                                            Toast.makeText(ForgotPasswordActivity.this, ex.getMessage(), Toast.LENGTH_SHORT);
                                        }
                                    }
                                });*/


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                // TODO: 25/09/2016 send mail from app 
                switch (fromLoginActivity.getStringExtra("permission"))
                {
                    case ("CLIENT"):
                        if(store.client_mail_valid(user_name.getText().toString(), user_mail.getText().toString()))
                        {
                            intent.putExtra("permission",Enums.PERMISSION.CLIENT.toString());
                            startActivity(intent);
                            Toast.makeText(ForgotPasswordActivity.this, "Your password was sent to your email.\nGood luck!", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(ForgotPasswordActivity.this, "wrong mail", Toast.LENGTH_SHORT);
                        break;
                    case ("VENDOR"):
                        if(store.vendor_mail_valid(user_name.getText().toString(), user_mail.getText().toString()))
                        {
                        intent.putExtra("permission",Enums.PERMISSION.VENDOR.toString());
                        startActivity(intent);
                        Toast.makeText(ForgotPasswordActivity.this, "Your password was sent to your email.\nGood luck!", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(ForgotPasswordActivity.this, "wrong mail", Toast.LENGTH_SHORT);
                        break;
                    default:
                }


            }
        });
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            switch (fromLoginActivity.getStringExtra("permission")) {
                case ("CLIENT"):
                    intent.putExtra("permission",Enums.PERMISSION.CLIENT.toString());
                    break;
                case ("VENDOR"):
                    intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                    break;
                default:
            }
            startActivity(intent);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    public void onBackPressed()
    {
        /*
        Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordActivity.class);
        switch (fromLoginActivity.getStringExtra("permission")) {
            case ("CLIENT"):
                intent.putExtra("permission",Enums.PERMISSION.CLIENT.toString());
                break;
            case ("VENDOR"):
                intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                break;
            default:
        }

        Toast.makeText(ForgotPasswordActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        */
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        switch (fromLoginActivity.getStringExtra("permission")) {
            case ("CLIENT"):
                intent.putExtra("permission",Enums.PERMISSION.CLIENT.toString());
                break;
            case ("VENDOR"):
                intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                break;
            default:
        }
        startActivity(intent);
    }
}
