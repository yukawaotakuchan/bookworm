package com.shira.ayelethashahar.bookWorm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import storeComponents.Enums;

public class VendorSystemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
String this_ven_id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_system_window);

        this_ven_id = getIntent().getStringExtra("vendorID");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ImageButton edit = (ImageButton) findViewById(R.id.buttonEditDetails);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editDetails= new Intent(VendorSystemActivity.this, UserEditDetailsActivity.class);
                editDetails.putExtra("vendorID", this_ven_id);
                editDetails.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                startActivity(editDetails);
            }
        });

        TextView hello = (TextView) findViewById(R.id.hello_user);
        hello.setText("Hello " + this_ven_id + "  :)");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
//// TODO: 20/09/2016 back pressed not working
    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            Intent intent= new Intent(VendorSystemActivity.this, VendorSystemActivity.class);
            intent.putExtra("vendorID", this_ven_id);
            startActivity(intent);
        }
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent (VendorSystemActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        switch (id)
        {
            case R.id.stock:
                fragment = new VendorShowStockFragment();

                break;
            case R.id.add_title:
                fragment = new VendorOrderNewTitleFragment();
                break;
            case R.id.purchases:
                fragment = new VendorShowPurchsFragment();
                break;
            default:
                break;
        }
        if (fragment != null)
        {
            Bundle args = new Bundle();
            args.putString("vendorID", this_ven_id);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentBackPressed(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        /**
         * when we get back-press from a fragment of this activity -
         * we want to return the focus to that activity.
         * but, notice that we must send the vendor's id with it...
         */
        Intent intent = new Intent(VendorSystemActivity.this, VendorSystemActivity.class);
        intent.putExtra("vendorID", this_ven_id);
        startActivity(intent);
        finish();
    }
}
