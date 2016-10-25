package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Purchase;

/**
 * Created by Shira on 18/12/2015.
 */
public class CheckOutActivity extends Activity
{

    Backend store= BackendFactory.getInstance();
    String this_client_id;
    ArrayList<Purchase> cartB = new ArrayList<Purchase>();
    ArrayList<Purchase> actualCart = new ArrayList<Purchase>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            Intent fromClientSystem = getIntent();
            this_client_id = fromClientSystem.getStringExtra("clientID");
            cartB = (ArrayList<Purchase>)store.getClient(this_client_id).getAdeddToCart();

            setContentView(R.layout.check_out_window);

            ListView listview = (ListView) findViewById(R.id.cart);
            final TextView sum_price = (TextView) findViewById(R.id.editTextSumPrice);
            Button buy = (Button) findViewById(R.id.buttonPurchase);

            ArrayAdapter<Purchase> purchs_adapter = new ArrayAdapter<Purchase>(this, R.layout.row_cart_item, cartB)
            {
                public View getView(final int position, final View contentView, final ViewGroup parent)
                {
                    final View row = View.inflate(CheckOutActivity.this, R.layout.row_cart_item, null);
                    try {
                        Purchase p = cartB.get(position);

                        String title_name=p.getBooks().getTitle().getTitle_name().toString();
                        final TextView title = (TextView) row.findViewById(R.id.textViewResultName);
                        title.setText(title_name);

                        final TextView vendor = (TextView) row.findViewById(R.id.textViewResultVendor);
                        vendor.setText(p.getVendor_id());

                        final TextView amount = (TextView) row.findViewById(R.id.textViewResultAmount);
                        amount.setText(String.valueOf(p.getCopies_amount()));

                        final TextView price = (TextView) row.findViewById(R.id.textViewResultPrice);
                        price.setText(String.valueOf(p.getFinal_price()));

                        final ImageView cover= (ImageView) row.findViewById(R.id.imageViewResult);
                        try
                        {
                            Bitmap b=store.getTitleCover(title_name);
                            if(b==null)
                                throw new Exception();
                            cover.setImageBitmap(b);
                        }
                        catch (Exception ex)
                        {
                            cover.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.blankcover));
                        }

                        final CheckBox chose = (CheckBox) row.findViewById(R.id.choose);
                        final TextView chosen = (TextView) row.findViewById(R.id.chosen);
                        final ImageView remove = (ImageView) row.findViewById(R.id.imageButtonRemove);
                        chosen.setText("0"); // default!!
                        chose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                {
                                    chosen.setText("1");
                                    double x = Double.parseDouble(sum_price.getText().toString()) + Double.parseDouble(price.getText().toString());
                                    sum_price.setText(String.valueOf(x));
                                    actualCart.add(cartB.get(position));
                                } else {
                                    chosen.setText("0");
                                    double x = Double.parseDouble(sum_price.getText().toString()) - Double.parseDouble(price.getText().toString());
                                    sum_price.setText(String.valueOf(x));
                                    actualCart.remove(cartB.get(position));
                                }
                            }
                        });
                        remove.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                try
                                {
                                    store.addToCart(cartB.get(position).getClient_id(), false , cartB.get(position));
                                    Toast.makeText(CheckOutActivity.this, "item removed. please refresh to update", Toast.LENGTH_LONG).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(CheckOutActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }

                    catch (Exception ex)
                    {
                        Toast.makeText(CheckOutActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    return row;
                }
            };

            listview.setAdapter(purchs_adapter);

            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        for (Purchase p : actualCart) {
                            try {
                                store.execute_purchase(p);
                            } catch (Exception ex) {
                                try {
                                    store.addToCart(p.getClient_id(), false, p);
                                } catch (Exception e) {
                                    Toast.makeText(CheckOutActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        //Intent returnsToClientSystem = new Intent();
                        //returnsToClientSystem.putExtra("cart", (ArrayList<Purchase>)cartB);
                        //setResult(RESULT_OK, returnsToClientSystem);
                        Toast.makeText(CheckOutActivity.this, "The acquisition was successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
            });
        }
        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(CheckOutActivity.this, ClientSystemActivity.class);
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
        Intent intent = new Intent(CheckOutActivity.this, CheckOutActivity.class);
        intent.putExtra("clientID", this_client_id);
        Toast.makeText(CheckOutActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        */
        Intent intent = new Intent(CheckOutActivity.this, ClientSystemActivity.class);
        intent.putExtra("clientID", this_client_id);
        startActivity(intent);
    }

}

