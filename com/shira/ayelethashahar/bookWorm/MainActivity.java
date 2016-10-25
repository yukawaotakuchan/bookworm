package com.shira.ayelethashahar.bookWorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.Exception;
import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Enums;


public class MainActivity extends Activity
{

   Backend store;// = BackendFactory.getInstance();
    /**
     * methods
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            store = BackendFactory.getInstance();

            setContentView(R.layout.main_window);
        }
        catch (Exception ex)
        {
           Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        final Intent openLogin = new Intent(MainActivity.this, LoginActivity.class);

        TextView c_login = (TextView) findViewById(R.id.Clogin);
        TextView v_login = (TextView) findViewById(R.id.Vlogin);

        c_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    openLogin.putExtra("permission", Enums.PERMISSION.CLIENT.toString());
                    startActivity(openLogin);
                }
                catch (Exception ex)
                {
                }
            }
        });
        v_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openLogin.putExtra("permission", Enums.PERMISSION.VENDOR.toString());
                startActivity(openLogin);
            }
        });

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
