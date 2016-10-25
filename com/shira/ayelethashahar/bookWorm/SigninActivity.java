package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Client;
import storeComponents.Enums;
import storeComponents.Vendor;

/**
 * Created by Shira on 30/11/2015.
 */
public class SigninActivity extends Activity
{
    Backend store;
    Intent fromLoginWindow;

    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            store = BackendFactory.getInstance();
        }
        catch (Exception x){return;}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_window);


        final EditText editTextUN = (EditText) findViewById(R.id.editTextUN);
        final EditText editTextName = (EditText) findViewById(R.id.editTextName);
        final EditText editTextPass = (EditText) findViewById(R.id.editTextPass);
        final EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        final EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        final EditText editTextAddr = (EditText) findViewById(R.id.editTextAddr);
        final EditText editTextBirthdate = (EditText) findViewById(R.id.editTextBirthdate);
        final RadioGroup radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);

        final EditText editTextPaypal = (EditText) findViewById(R.id.editTextPaypal);
        final TextView paypal = (TextView)findViewById(R.id.textView7);

        fromLoginWindow = getIntent();
        /**
         *  if and only if that user is going to be a vendor - he's supposed to fill the paypal account field.
         *  if not - that field even won't show.
         */
        if(fromLoginWindow.getStringExtra("permission").equals("VENDOR")) {
            paypal.setVisibility(View.VISIBLE);
            editTextPaypal.setVisibility(View.VISIBLE);
        }

        /**
         * the sending-details-for-registration button
         */
        Button buttonSignin = (Button) findViewById(R.id.buttonSigning);
        buttonSignin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                RadioButton checkedGender = (RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId());

                /**
                 * in case that the user didn't filled all the required fields
                 */
                if (editTextUN.getText().toString().equals("")||editTextName.getText().toString().equals("")||editTextPass.getText().toString().equals("")||editTextPhone.getText().toString().equals("")||editTextEmail.getText().toString().equals("") || editTextAddr.getText().toString().equals("")||checkedGender == null || editTextBirthdate.getText().toString().equals("") || (editTextPaypal.getVisibility()==View.VISIBLE && editTextPaypal.getText().toString().equals("")))
                    Toast.makeText(SigninActivity.this, "please make sure that you filled all the fields", Toast.LENGTH_LONG).show();

                try
                {
                    /**
                     * if the person who registers here - is a client
                     */
                    if (fromLoginWindow.getStringExtra("permission").equals("CLIENT"))
                    {
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
                        String d = fromUser.format(myFormat.parse(editTextBirthdate.getText().toString()));
                        Client newC = new Client(   editTextUN.getText().toString(),
                                editTextName.getText().toString(),
                                editTextPass.getText().toString(),
                                editTextPhone.getText().toString(),
                                editTextAddr.getText().toString(),
                                editTextEmail.getText().toString(),
                                Enums.GENDER.valueOf(checkedGender.getText().toString()),
                                d);
                        store.add_client(newC);
                        Toast.makeText(SigninActivity.this, "registration as 'BookWorm' client - complete successfully:)", Toast.LENGTH_LONG).show();

                        Intent openClientSystem = new Intent(SigninActivity.this, ClientSystemActivity.class);
                        openClientSystem.putExtra("clientID", newC.getId());
                        startActivity(openClientSystem);
                    }

                    /**
                     * if the person who registers here - is a vendor
                     */
                    if (fromLoginWindow.getStringExtra("permission").equals("VENDOR"))
                    {
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
                        String d = fromUser.format(myFormat.parse(editTextBirthdate.getText().toString()));
                        Vendor newV = new Vendor(   editTextUN.getText().toString(),
                                editTextName.getText().toString(),
                                editTextPass.getText().toString(),
                                editTextPhone.getText().toString(),
                                editTextAddr.getText().toString(),
                                editTextEmail.getText().toString(),
                                Enums.GENDER.valueOf(checkedGender.getText().toString()),
                                d,
                                editTextPaypal.getText().toString());

                        store.add_vendor(newV);
                        Toast.makeText(SigninActivity.this, "registration as 'BookWorm' vendor - complete successfully:)", Toast.LENGTH_LONG).show();

                        Intent openVendorSystem = new Intent(SigninActivity.this, VendorSystemActivity.class);
                        openVendorSystem.putExtra("vendorID", newV.getId());
                        startActivity(openVendorSystem);
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(SigninActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            try {
                Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                if (fromLoginWindow.getStringExtra("permission").equals("CLIENT"))
                    intent.putExtra("permission", Enums.PERMISSION.CLIENT.toString());
                else if (fromLoginWindow.getStringExtra("permission").equals("VENDOR"))
                    intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                else
                    throw new Exception("permission error from signin activity");

                startActivity(intent);
            }
            catch (Exception ex){Toast.makeText(SigninActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();}

            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    public void onBackPressed()
    {
        /*
        Intent intent = new Intent(SigninActivity.this, SigninActivity.class);
        switch (fromLoginWindow.getStringExtra("permission")) {
            case ("CLIENT"):
                intent.putExtra("permission", Enums.PERMISSION.CLIENT.toString());
                break;
            case ("VENDOR"):
                intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                break;
            default:
        }

        Toast.makeText(SigninActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        */
        Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
        if (fromLoginWindow.getStringExtra("permission").equals("CLIENT"))
            intent.putExtra("permission", Enums.PERMISSION.CLIENT.toString());
        else if (fromLoginWindow.getStringExtra("permission").equals("VENDOR"))
            intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
        else
            Toast.makeText(SigninActivity.this, "permission error from signin activity", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
