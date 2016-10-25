package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Client;
import storeComponents.Enums;
import storeComponents.User;
import storeComponents.Vendor;

/**
 * Created by Shira on 13/12/2015.
 */
public class UserEditDetailsActivity extends Activity
{
    Backend store= BackendFactory.getInstance();
    String user_permission;
    String this_client_id;
    String this_ven_id;

    protected void onCreate(Bundle savedInstanceState)
    {
        final Intent editDetails = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_details);

        try {
            user_permission = editDetails.getStringExtra("permission");

            final EditText name = (EditText) findViewById(R.id.editTextVName);
            final EditText pass = (EditText) findViewById(R.id.editTextVPass);
            final EditText phone = (EditText) findViewById(R.id.editTextVPhone);
            final EditText addr = (EditText) findViewById(R.id.editTextVAddr);
            final EditText email = (EditText) findViewById(R.id.editTextVEmail);
            final EditText paypal = (EditText) findViewById(R.id.editTextVPay);
            final TextView payPal = (TextView) findViewById(R.id.TextViewPaypal);
            Button edit = (Button) findViewById(R.id.buttonEditVendorDetails);

            if (user_permission.equals("CLIENT"))
            {
                this_client_id = editDetails.getStringExtra("clientID");
                final Client c = store.getClient(this_client_id);

                name.setText(c.getName());
                pass.setText(c.getPassword());
                phone.setText(c.getPhone());
                addr.setText(c.getAddress());
                email.setText(c.getEmail());
                paypal.setVisibility(View.INVISIBLE);
                payPal.setVisibility(View.INVISIBLE);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            try {
                                Client tmp = new Client(c.getId(), name.getText().toString(), pass.getText().toString(), phone.getText().toString(), addr.getText().toString(), email.getText().toString(), c.getGender(), new SimpleDateFormat("yyyy-MM-dd").format(c.getBirthdate()), c.getAccumulative_payment(), c.getDiscount());
                                store.update_clients_list(tmp);
                                //store.getClient(this_client_id).edit_details(tmp);
                            }
                            catch (Exception ex){
                                Toast.makeText(UserEditDetailsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                                return; // beacause we don't want that if the details are invail - it will execute "finish()"
                            }

                            /**
                             * if every thing was ok - means if all the new details were valid!
                             */
                            throw new Exception("your personal details were edited successfully:)");
                        }
                        catch (Exception ex) {
                            Toast.makeText(UserEditDetailsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }

            else if (user_permission.equals("VENDOR"))
            {
                this_ven_id = editDetails.getStringExtra("vendorID");
                final Vendor ven = store.getVendor(this_ven_id);

                name.setText(ven.getName());
                pass.setText(ven.getPassword());
                phone.setText(ven.getPhone());
                addr.setText(ven.getAddress());
                email.setText(ven.getEmail());
                paypal.setVisibility(View.VISIBLE);
                payPal.setVisibility(View.VISIBLE);
                paypal.setText(ven.getPaypal_account_mail());

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            try {
                                Vendor tmp = new Vendor(ven.getId(), name.getText().toString(), pass.getText().toString(), phone.getText().toString(), addr.getText().toString(), email.getText().toString(), ven.getGender(), new SimpleDateFormat("yyyy-MM-dd").format(ven.getBirthdate()), paypal.getText().toString());
                                store.update_ven_list(tmp);
                                //store.getVendor(this_ven_id).edit_details(tmp);
                            }
                            catch (Exception ex){
                                Toast.makeText(UserEditDetailsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }

                            /**
                             * if every thing was ok - means if all the new details were valid!
                             */
                            throw new Exception("your personal details were edited successfully:)");

                        } catch (Exception ex) {
                            Toast.makeText(UserEditDetailsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(UserEditDetailsActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (user_permission) {
                case ("CLIENT"):
                    Intent intent = new Intent(UserEditDetailsActivity.this, ClientSystemActivity.class);
                    intent.putExtra("clientID", this_client_id);
                    startActivity(intent);
                    break;
                case ("VENDOR"):
                    Intent intent2 = new Intent(UserEditDetailsActivity.this, VendorSystemActivity.class);
                    intent2.putExtra("vendorID", this_ven_id);
                    startActivity(intent2);
                    break;
                default:
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed()
    {
        /*
        Intent intent = new Intent(UserEditDetailsActivity.this, UserEditDetailsActivity.class);
        switch (user_permission) {
            case ("CLIENT"):
                intent.putExtra("clientID", this_client_id);
                intent.putExtra("permission", Enums.PERMISSION.CLIENT.toString());
                break;
            case ("VENDOR"):
                intent.putExtra("vendorID", this_ven_id);
                intent.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                break;
            default:
        }

        Toast.makeText(UserEditDetailsActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        */
        switch (user_permission) {
            case ("CLIENT"):
                Intent intent = new Intent(UserEditDetailsActivity.this, ClientSystemActivity.class);
                intent.putExtra("clientID", this_client_id);
                startActivity(intent);
                break;
            case ("VENDOR"):
                Intent intent2 = new Intent(UserEditDetailsActivity.this, VendorSystemActivity.class);
                intent2.putExtra("vendorID", this_ven_id);
                startActivity(intent2);
                break;
            default:
        }
    }

}
