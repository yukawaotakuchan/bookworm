package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Copies;
import storeComponents.Enums;
import storeComponents.Purchase;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;

/**
 * Created by Shira on 30/11/2015.
 */
public class ClientSystemActivity extends Activity {
    Backend store = BackendFactory.getInstance();

    List<Copies> results = new ArrayList<Copies>();
    ArrayList<Purchase> cartB = new ArrayList<Purchase>();
    ListView list;
    String f_genre = "";
    String f_category = "";
    List<String> r_names = new ArrayList<>();
    List<String> r_authors = new ArrayList<>();
    List<String> r_prices = new ArrayList<>();
    List<Bitmap> r_pictures = new ArrayList<>();
    List<String> r_seller = new ArrayList<>();
    String this_client_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent fromClientLogin = getIntent();
        this_client_id = fromClientLogin.getStringExtra("clientID");
        setContentView(R.layout.client_system_window);

        final EditText from = (EditText) findViewById(R.id.editTextFrom);
        from.setText("from");
        final EditText to = (EditText) findViewById(R.id.editTextTo);
        to.setText("to");
        final EditText seller = (EditText) findViewById(R.id.editTextSeller);
        seller.setHint("seller:");
        final EditText author = (EditText) findViewById(R.id.editTextAuthor);
        author.setHint("author:");
        final Button filter = (Button) findViewById(R.id.buttonGo);
        final TextView hello = (TextView) findViewById(R.id.hello_user);
        final TextView numResults = (TextView) findViewById(R.id.textViewNumResults);
        final Spinner spinnerG = (Spinner) findViewById(R.id.spinnerGenre);
        final Spinner spinnerC = (Spinner) findViewById(R.id.spinnerCategory);
        hello.setText("Hello " + this_client_id + "  :)");
        list = (ListView) findViewById(R.id.listViewResults);
        final SearchView search = (SearchView) findViewById(R.id.search);
        search.setQueryHint("search title");
        search.setQuery("", false);
        final ImageButton cart = (ImageButton) findViewById(R.id.imageButtonCart);
        ImageButton user_d = (ImageButton) findViewById(R.id.imageButtonUser);
        registerForContextMenu(user_d);

        //#################################
        // Spinner click listener
        spinnerG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                f_genre = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
            ///
        });
        spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                f_category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Spinner Drop down elements
        final List<String> genres = new ArrayList<String>();
        for (Enums.GENRES gen : Enums.GENRES.values())
            genres.add(gen.toString());
        final List<String> categories = new ArrayList<String>();
        for (Enums.CATEGORY cat : Enums.CATEGORY.values())
            categories.add(cat.toString());

        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapterG = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genres);
        final ArrayAdapter<String> dataAdapterC = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerG.setAdapter(dataAdapterG);
        spinnerC.setAdapter(dataAdapterC);
        //##############################################

        from.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                from.setText("");
                return false;
            }
        });
        to.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                to.setText("");
                return false;
            }
        });
        seller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seller.setHint("");
                seller.setText("");
                return false;
            }
        });
        author.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                author.setHint("");
                author.setText("");
                return false;
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    results.clear();
                    r_names.clear();
                    r_authors.clear();
                    r_prices.clear();
                    r_pictures.clear();
                    r_seller.clear();

                    results = store.find_titles_copies(search.getQuery().toString());
                    if (!to.getText().toString().equals("") && !to.getText().toString().equals("to")) {
                        if (!from.getText().toString().equals("") && !from.getText().toString().equals("from"))
                            results = store.find_copies_within_range(Integer.parseInt(from.getText().toString()), Integer.parseInt(to.getText().toString()), results);// if both are changed (not their initial text)
                        else//if there is no value in 'from' field
                            results = store.find_copies_within_range(0, Integer.parseInt(to.getText().toString()), results);
                    }

                    if (!f_genre.equals("") && !f_genre.equals("NONE"))
                        results = store.find_copies_of_genere(f_genre, results);
                    if (!f_category.equals("") && !f_category.equals("NONE"))
                        results = store.find_copies_of_category(f_category, results);
                    if (!seller.getText().toString().equals(""))
                        results = store.find_copies_by_vendor(seller.getText().toString(), results);
                    if (!author.getText().toString().equals(""))
                        results = store.find_copies_by_author(author.getText().toString(), results);

                    results = store.getSearchCopies(results);
                    for (Copies copy : results) {
                        r_names.add(copy.getTitle().getTitle_name());
                        r_authors.add(copy.getTitle().getAuthor());
                        r_prices.add(String.valueOf(copy.getInitial_price()));
                        try
                        {
                            Bitmap b=store.getTitleCover(copy.getTitle().getTitle_name());
                            if(b==null)
                                throw new Exception();
                            r_pictures.add(b);
                        }
                        catch (Exception ex)
                        {
                            r_pictures.add(BitmapFactory.decodeResource(getResources(), R.drawable.blankcover));
                        }
                        r_seller.add(copy.getVen_id());
                    }
                    BookResultAdapter adapter = new BookResultAdapter(ClientSystemActivity.this, r_names, r_authors, r_prices, r_pictures, r_seller);
                    list.setAdapter(adapter);

                    numResults.setText(results.size() + " results were found");
                    numResults.setVisibility(View.VISIBLE);

                    //restart all the filter-fields
                    to.setText("to");
                    from.setText("from");
                    seller.setHint("seller:");
                    seller.setText("");
                    author.setHint("author:");
                    author.setText("");
                    spinnerC.setSelection(dataAdapterC.getPosition("NONE"));
                    spinnerG.setSelection(dataAdapterG.getPosition("NONE"));

                } catch (Exception ex) {
                    Toast.makeText(ClientSystemActivity.this, ex.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                results.clear();
                results = store.find_titles_copies(search.getQuery().toString());
                r_names.clear();
                r_authors.clear();
                r_prices.clear();
                r_pictures.clear();
                r_seller.clear();
                results = store.getSearchCopies(results);
                for (Copies copy : results)
                {
                    r_names.add(copy.getTitle().getTitle_name());
                    r_authors.add(copy.getTitle().getAuthor());
                    r_prices.add(String.valueOf(copy.getInitial_price()));
                    try
                    {
                        Bitmap b=store.getTitleCover(copy.getTitle().getTitle_name());
                        if(b==null)
                            throw new Exception();
                        r_pictures.add(b);
                    }
                    catch (Exception ex)
                    {
                        r_pictures.add(BitmapFactory.decodeResource(getResources(), R.drawable.blankcover));
                    }
                    r_seller.add(copy.getVen_id());
                }
                BookResultAdapter adapter = new BookResultAdapter(ClientSystemActivity.this, r_names, r_authors, r_prices, r_pictures, r_seller);
                list.setAdapter(adapter);
                numResults.setText(results.size() + " results were found");
                numResults.setVisibility(View.VISIBLE);

                search.setQuery("", false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCheckOut = new Intent(ClientSystemActivity.this, CheckOutActivity.class);
                openCheckOut.putExtra("clientID", this_client_id);
                Bundle b = new Bundle();
                b.putSerializable("cart", cartB);
                openCheckOut.putExtra("cart", b);
                startActivityForResult(openCheckOut, 2);
            }
        });
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("client options");
        menu.add(0, v.getId(), 0, "edit details");
        menu.add(0, v.getId(), 0, "view previous purchases");
    }

    public boolean onContextItemSelected(MenuItem item){
        try {
            if (item.getTitle().equals("edit details")) {
                Intent openEditDetails = new Intent(ClientSystemActivity.this, UserEditDetailsActivity.class);
                openEditDetails.putExtra("permission", store.getClient(this_client_id).getPermission().toString());
                openEditDetails.putExtra("clientID", this_client_id);
                startActivity(openEditDetails);
                return true;
            }
            if (item.getTitle().equals("view previous purchases")) {
                Intent openPrecPurchases = new Intent(ClientSystemActivity.this, ClientShowPurchsActivity.class);
                openPrecPurchases.putExtra("clientID", this_client_id);
                startActivity(openPrecPurchases);
                return true;
            }
        }catch (Exception ex) {
            Toast.makeText(ClientSystemActivity.this, ex.getMessage(), Toast.LENGTH_SHORT);
        }
        return  false;
    }

    public void goZoom(String t_name, String v_id) {
        Intent openZoomResult = new Intent(ClientSystemActivity.this, ZoomActivity.class);
        openZoomResult.putExtra("titleName", t_name);
        openZoomResult.putExtra("vendorID", v_id);
        openZoomResult.putExtra("clientID", this_client_id);
        startActivity(openZoomResult);
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ClientSystemActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    // TODO: 21/09/2016 back pressed. initialized to normal.
    public void onBackPressed()
    {
        Intent intent = new Intent(ClientSystemActivity.this, MainActivity.class);
        //ntent.putExtra("clientID", this_client_id);
        //Toast.makeText(ClientSystemActivity.this, "long back press is required", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}