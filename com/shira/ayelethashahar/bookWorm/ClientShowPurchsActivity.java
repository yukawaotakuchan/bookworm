package com.shira.ayelethashahar.bookWorm;

/**
 * Created by Shira on 13/12/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Client;
import storeComponents.Purchase;
import storeComponents.Vendor;


/**
 * Created by AyeletHaShachar on 06/12/2015.
 */
public class ClientShowPurchsActivity extends Activity {

    Backend store= BackendFactory.getInstance();
    String this_client_id;
    List<Purchase> purchases;
    Purchase p;

    @Override
    public void onCreate (Bundle savedInstanceState) {

        final Intent editClient = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_purchases);
        this_client_id = editClient.getStringExtra("clientID");

        try
        {
            purchases = store.get_client_purchases(this_client_id);

            ListView listview = (ListView) findViewById(R.id.purchsList);
            PurchasesAdapter purchs_adapter = new PurchasesAdapter(this, this_client_id, purchases);
            listview.setAdapter(purchs_adapter);
        }
        catch (Exception ex)
        {
            Toast.makeText(ClientShowPurchsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ClientShowPurchsActivity.this, ClientSystemActivity.class);
            intent.putExtra("clientID", this_client_id);
            startActivity(intent);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed()
    {
        /*
        Intent intent = new Intent(ClientShowPurchsActivity.this, ClientShowPurchsActivity.class);
        intent.putExtra("clientID", this_client_id);
        Toast.makeText(ClientShowPurchsActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        */
        Intent intent = new Intent(ClientShowPurchsActivity.this, ClientSystemActivity.class);
        intent.putExtra("clientID", this_client_id);
        startActivity(intent);
    }

}


