package com.shira.ayelethashahar.bookWorm;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Copies;

public class VendorShowStockFragment extends Fragment {

    Backend store= BackendFactory.getInstance();
    String this_ven_id;
    List<Copies> stock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        this_ven_id = getArguments().getString("vendorID");
        stock = store.getVendorStock(this_ven_id);

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_vendor_show_stock, null);

        try
        {
            final SearchView search = (SearchView) rootView.findViewById(R.id.search);
            search.setQueryHint("search title");
            search.setQuery("", false);
            final TextView num = (TextView) rootView.findViewById(R.id.textViewXFounds);
            num.setText(stock.size() + " results were found");
            final ListView listview = (ListView) rootView.findViewById(R.id.stockList);
            final StockAdapter stock_adapter = new StockAdapter(getActivity(), this_ven_id, stock);
            listview.setAdapter(stock_adapter);

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    List<Copies> partial_stock = new ArrayList<Copies>();

                    for (Copies c : stock)
                        if (c.getTitle().getTitle_name().contains(search.getQuery()))
                            partial_stock.add(c);

                    final StockAdapter partial_stock_adapter = new StockAdapter(getActivity(), this_ven_id, partial_stock);
                    listview.setAdapter(partial_stock_adapter);

                    num.setText(partial_stock.size() + " results were found");

                    search.setQuery("", false);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

}