package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Copies;

/**
 * Created by AyeletHaShachar on 20/12/2015.
 */
public class StockAdapter extends ArrayAdapter<Copies> {

    protected Backend store = BackendFactory.getInstance();

    protected Activity context;
    protected String this_ven_id;
    protected List<Copies> stock;
    protected Copies c;

    StockAdapter(Activity con, String vid, List<Copies> s)
    {
        super(con, R.layout.fragment_vendor_show_stock, s);
        this.this_ven_id=vid;
        this.context=con;
        this.stock = s;

    }
    public View getView(final int position, View contentView, ViewGroup parent)
    {
        //connect between our design to row_book(.xml)
        final View row = View.inflate(context, R.layout.row_book, null);

        try {
            //this is the book we wish to present in that current step (as a part os the general aim: to present the whole stock in the fragment
            c = stock.get(position);

            //present the the book's details in the relevant textViews
            final TextView tName = (TextView) row.findViewById(R.id.textViewTname);
            tName.setText(c.getTitle().getTitle_name());

            final TextView author = (TextView) row.findViewById(R.id.textViewAuthor);
            author.setText(c.getTitle().getAuthor());

            final TextView amount = (TextView) row.findViewById(R.id.textViewAmount);
            amount.setText(String.valueOf(c.getAmount()));

            final TextView cur_price = (TextView) row.findViewById(R.id.textViewPrice);
            cur_price.setText(String.valueOf(c.getInitial_price()));
            final TextView rec_price = (TextView) row.findViewById(R.id.textViewRecPrice);
            rec_price.setText(String.valueOf(c.getTitle().getRecommended_price()));

            final NumberPicker new_amount = (NumberPicker) row.findViewById(R.id.numberPickerNewAmount);
            new_amount.setMinValue(0);
            new_amount.setMaxValue(50);

            final NumberPicker new_price = (NumberPicker) row.findViewById(R.id.numberPickerNewPrice);
            new_price.setMinValue(0);
            new_price.setMaxValue(150);

            final RadioGroup add_remove = (RadioGroup) row.findViewById(R.id.radioGroupAddRemove);

            Button save = (Button) row.findViewById(R.id.buttonSave);

            save.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    try {
                        c = stock.get(position);
                        /**
                         * if there is a new value for the amount
                         */
                        if ( (new_amount.getValue() > 0) && (((RadioButton) row.findViewById(add_remove.getCheckedRadioButtonId())) != null) ) {
                            if (((RadioButton) row.findViewById(add_remove.getCheckedRadioButtonId())).getText().equals("add"))
                                store.add_copies_to_vendor(this_ven_id, c.getTitle(), new_amount.getValue(), Double.parseDouble(String.valueOf(new_price.getValue())));
                            else if (((RadioButton) row.findViewById(add_remove.getCheckedRadioButtonId())).getText().equals("remove"))
                                store.delete_copies_from_vendor(this_ven_id, c.getTitle(), new_amount.getValue());
                        }
                        /**
                         * if there is a new value for the price
                         */
                        if (new_price.getValue() > 0)
                            store.update_copy_price(this_ven_id, c.getTitle().getTitle_name(), Double.parseDouble(String.valueOf(new_price.getValue())));

                        /**
                         * restart all the fields
                         */
                        new_amount.setValue(0);
                        new_price.setValue(0);
                        amount.setText(String.valueOf(c.getAmount()));
                        cur_price.setText(Double.toString(c.getInitial_price()));
                        if (((RadioButton) row.findViewById(add_remove.getCheckedRadioButtonId())) != null )
                            ((RadioButton) row.findViewById(add_remove.getCheckedRadioButtonId())).setChecked(false);

                        throw new Exception("amount/price updated successfully");
                    }
                    catch (Exception ex) {
                        ((VendorSystemActivity)context).onFragmentBackPressed(ex.getMessage());
                    }
                }
            });

        }catch (Exception ex) {Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();}

        return row;
    }

    @Override /*5)*/
    public long getItemId(int position) { return position;}
}
