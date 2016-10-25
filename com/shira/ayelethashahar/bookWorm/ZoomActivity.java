package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Copies;
import storeComponents.Purchase;

/**
 * Created by Shira on 12/12/2015.
 */
public class ZoomActivity extends Activity
{
    Backend store = BackendFactory.getInstance();
    String this_ven_id;
    String this_client_id;
    String this_title_name;
    int value;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent fromClientSystemActivity = getIntent();
        setContentView(R.layout.zoom_item_window);
        this_client_id=fromClientSystemActivity.getStringExtra("clientID");
        this_ven_id = fromClientSystemActivity.getStringExtra("vendorID");
        this_title_name = fromClientSystemActivity.getStringExtra("titleName");

        TextView tName = (TextView) findViewById(R.id.textViewTitle);
        TextView tAuthor = (TextView) findViewById(R.id.textViewAuthor);
        TextView tGenre1 = (TextView) findViewById(R.id.textViewGenre1);
        TextView tGenre2 = (TextView) findViewById(R.id.textViewGenre2);
        TextView tCategory = (TextView) findViewById(R.id.textViewCategory);
        TextView tPages = (TextView) findViewById(R.id.textViewPages);
        Button add_to_cart = (Button) findViewById(R.id.buttonAddToCart);
        ImageView cover = (ImageView) findViewById(R.id.imageCover);
        NumberPicker num = (NumberPicker) findViewById(R.id.numberPicker);
        value = 0;
        num.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal>0)
                    value = newVal;
            }
        });
        TextView avail = (TextView) findViewById(R.id.textViewAvilable);

        try {
            Copies this_c = store.getCopy(this_title_name, this_ven_id);
            num.setMinValue(0);
            num.setMaxValue(this_c.getAmount());
            tName.setText(this_c.getTitle().getTitle_name());
            tAuthor.setText(this_c.getTitle().getAuthor().toString());
            tGenre1.setText(this_c.getTitle().getGenre1().toString()+ "   ");
            tGenre2.setText(this_c.getTitle().getGenre2().toString()+ "   ");
            tCategory.setText(this_c.getTitle().getCategory().toString());
            tPages.setText(String.valueOf(this_c.getTitle().getLength()) + " pages");
            try
            {
                Bitmap b=store.getTitleCover(this_title_name);
                if(b==null)
                    throw new Exception();
                cover.setImageBitmap(b);
            }
            catch (Exception ex)
            {
                cover.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.blankcover));
            }
            String x = String.valueOf(this_c.getAmount()).concat(" copies of ").concat(this_title_name).concat(" are available in ").concat(this_ven_id).concat("'s stock");
            avail.setText(x);

            add_to_cart.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (value > 0) {
                        try {
                            Copies c = store.getCopy(this_title_name, this_ven_id);
                            int discount = store.getClient(this_client_id).getDiscount();
                            double final_price = c.getInitial_price() * (1.0 - discount / 100.0) * (value * 1.0);
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            String d=dateFormat.format(date);
                            Purchase cartItem = new Purchase(this_client_id, this_ven_id, c, d, final_price, value);
                            store.addToCart(this_client_id, true, cartItem);
                            Toast.makeText(ZoomActivity.this, "item added to cart", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ZoomActivity.this, ClientSystemActivity.class);
                            intent.putExtra("clientID", this_client_id);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(ZoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }catch (Exception x)
        {
            Toast.makeText(ZoomActivity.this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ZoomActivity.this, ClientSystemActivity.class);
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
        Intent intent = new Intent(ZoomActivity.this, ZoomActivity.class);
        intent.putExtra("clientID", this_client_id);
        intent.putExtra("vendorID", this_ven_id);
        intent.putExtra("titleName", this_title_name);
        Toast.makeText(ZoomActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        */
        Intent intent = new Intent(ZoomActivity.this, ClientSystemActivity.class);
        intent.putExtra("clientID", this_client_id);
        startActivity(intent);
    }
}
