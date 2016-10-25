package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;

import java.io.IOException;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Enums;

/**
 * Created by Shira on 23/11/2015.
 */

public class LoginActivity extends Activity
{

    Backend store= BackendFactory.getInstance();
    Intent fromMainActivity;


    protected void onCreate(Bundle savedInstanceState)
    {
        fromMainActivity = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_window);

        final EditText input_name = (EditText) findViewById(R.id.editTextName);
        final EditText input_pass = (EditText) findViewById(R.id.editTextPass);
        final TextView forgot_pass = (TextView) findViewById(R.id.forget_pass);
        Button open_signin = (Button) findViewById(R.id.buttonSignin);
        Button login = (Button) findViewById(R.id.buttonLogin);

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent openForgotPass = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                switch (fromMainActivity.getStringExtra("permission")) {
                    case ("CLIENT"):
                        openForgotPass.putExtra("permission",Enums.PERMISSION.CLIENT.toString());
                        break;
                    case ("VENDOR"):
                        openForgotPass.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                        break;
                    default:
                }
                startActivity(openForgotPass);
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (input_name.getText().toString().equals("")|| input_pass.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "please fill all the fields", Toast.LENGTH_LONG).show();
                    return;
                }
                switch (fromMainActivity.getStringExtra("permission")) {
                    case ("CLIENT"):
                        //if client with those id and password exist- send by extra the client/just his id and open clientSystem window.
                        // else toast a message.
                        try {
                            if(!store.client_is_valid(input_name.getText().toString(), input_pass.getText().toString()))
                            {
                                Toast.makeText(LoginActivity.this, "username or password are not correct", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent openClientSystem = new Intent(LoginActivity.this, ClientSystemActivity.class);
                        openClientSystem.putExtra("clientID",input_name.getText().toString());
                        startActivity(openClientSystem);
                        break;
                    case ("VENDOR"):
                        //if vendor with those id and password exist- send by extra the vendort/just his id and open vendorSystem window.
                        // else toast a message.
                        if(!store.vendor_is_valid(input_name.getText().toString(), input_pass.getText().toString()))
                        {
                            Toast.makeText(LoginActivity.this, "username or password are not correct", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Intent openVendorSystem = new Intent(LoginActivity.this, VendorSystemActivity.class);
                        openVendorSystem.putExtra("vendorID",input_name.getText().toString());
                        startActivity(openVendorSystem);
                        break;
                    default:
                }
            }
        });

        open_signin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent signin = new Intent(LoginActivity.this, SigninActivity.class);
                switch (fromMainActivity.getStringExtra("permission")) {
                    case ("CLIENT"):
                        signin.putExtra("permission", Enums.PERMISSION.CLIENT.toString());
                        startActivity(signin);
                        break;
                    case ("VENDOR"):
                        signin.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                        startActivity(signin);
                        break;
                    default: {
                        break;
                    }
                }
            }
        });
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        switch (fromMainActivity.getStringExtra("permission")) {
            case ("CLIENT"):
                intent.putExtra("permission",Enums.PERMISSION.CLIENT.toString());
                break;
            case ("VENDOR"):
                intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                break;
            default:
        }
        Toast.makeText(LoginActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();
        startActivity(intent);*/

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

}