package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Purchase;

/**
 * Created by AyeletHaShachar on 22/12/2015.
 */
public class PurchasesAdapter extends ArrayAdapter<Purchase> {

    protected Backend store = BackendFactory.getInstance();

    protected Activity context;
    protected String this_ven_id;
    protected List<Purchase> purchs;

    PurchasesAdapter(Activity con, String vid, List<Purchase> pu)
    {
        super(con, R.layout.fragment_show_purchases, pu);
        this.this_ven_id=vid;
        this.context=con;
        this.purchs = pu;
    }
    public View getView(final int position, View contentView, ViewGroup parent)
    {
        final View row = View.inflate(context, R.layout.row_purchase, null);
        try
        {
            Purchase p = purchs.get(position);

            final TextView date = (TextView) row.findViewById(R.id.textViewDate);
            date.setText(new SimpleDateFormat("dd/MM/yyyy").format(p.getDate()));

            final TextView client_name = (TextView) row.findViewById(R.id.textViewClient);
            client_name.setText(p.getClient_id().toString());

            final TextView title = (TextView) row.findViewById(R.id.textViewTitleName);
            title.setText(p.getBooks().getTitle().getTitle_name().toString());

            final TextView author = (TextView) row.findViewById(R.id.textViewAuthor);
            author.setText(p.getBooks().getTitle().getAuthor().toString());

            final TextView amount = (TextView) row.findViewById(R.id.textViewAmount);
            amount.setText(String.valueOf(String.valueOf(p.getCopies_amount())));

            final TextView price = (TextView) row.findViewById(R.id.textViewInitPrice);
            price.setText(String.valueOf(p.getBooks().getInitial_price()));

            final TextView fin_price = (TextView) row.findViewById(R.id.textViewFinPrice);
            fin_price.setText(String.valueOf(p.getFinal_price()));

        }
        catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return row;
    }

    @Override /*5)*/
    public long getItemId(int position) { return position;}
}

