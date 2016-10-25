package com.shira.ayelethashahar.bookWorm;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Purchase;

public class VendorShowPurchsFragment extends Fragment {

    Backend store= BackendFactory.getInstance();
    String this_ven_id;
    List<Purchase> purchases;
    Purchase p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_show_purchases, container, false);

        try
        {
            this_ven_id = getArguments().getString("vendorID");
            purchases = store.get_vendor_purchases(this_ven_id);

            ListView listview = (ListView) rootView.findViewById(R.id.purchsList);

            PurchasesAdapter purchs_adapter = new PurchasesAdapter(getActivity(), this_ven_id, purchases);
            listview.setAdapter(purchs_adapter);

        }
        catch (Exception ex)
        {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

}
