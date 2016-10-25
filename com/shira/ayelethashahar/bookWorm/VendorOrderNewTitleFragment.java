package com.shira.ayelethashahar.bookWorm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.backend.Backend;
import model.backend.BackendFactory;
import storeComponents.Enums;
import storeComponents.Title;
import storeComponents.Vendor;


public class VendorOrderNewTitleFragment extends Fragment {

    Backend store = BackendFactory.getInstance();
    String this_ven_id;
    String chosen_g1 = "";
    String chosen_g2 = "";
    String chosen_c = "";
    ImageView imgView;
    Bitmap cover;
    String imgDecodableString;
    String t_name;
    public static final String UPLOAD_URL = "http://bookworm.net23.net/uploadImage.php";
    public static final String UPLOAD_IMAGE = "image";
    public static final String UPLOAD_TITLE = "title";
    BitmapFactory.Options options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_vendor_order_new_title, container, false);

        try
        {
            this_ven_id = getArguments().getString("vendorID");
            final Vendor this_ven = store.getVendor(this_ven_id);

            final EditText tName = (EditText) rootView.findViewById(R.id.editTextTitleName);
            final TextView go = (TextView) rootView.findViewById(R.id.textViewGo);
            final EditText amount = (EditText) rootView.findViewById(R.id.editTextAmount);
            final EditText price = (EditText) rootView.findViewById(R.id.editTextPrice);
            final EditText author = (EditText) rootView.findViewById(R.id.editTextAuthor);
            final EditText pub_year = (EditText) rootView.findViewById(R.id.editTextPubYear);
            final EditText pub_house = (EditText) rootView.findViewById(R.id.editTextPubHouse);
            final EditText length = (EditText) rootView.findViewById(R.id.editTextLength);
            final ImageButton order = (ImageButton) rootView.findViewById(R.id.buttonOrder);
            final Spinner spinnerG1 = (Spinner) rootView.findViewById(R.id.spinnerGenre1);
            final Spinner spinnerG2 = (Spinner) rootView.findViewById(R.id.spinnerGenre2);
            final Spinner spinnerC = (Spinner) rootView.findViewById(R.id.spinnerCategory);
            // Spinner click listener
            spinnerG1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosen_g1 = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
                ///
            });
            spinnerG2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosen_g2 = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
                ///
            });
            spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosen_c = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            // Spinner Drop down elements
            List<String> genres = new ArrayList<String>();
            for (Enums.GENRES gen : Enums.GENRES.values())
                genres.add(gen.toString());
            List<String> categories = new ArrayList<String>();
            for (Enums.CATEGORY cat : Enums.CATEGORY.values())
                categories.add(cat.toString());
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapterG = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, genres);
            ArrayAdapter<String> dataAdapterC = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
            // Drop down layout style - list view with radio button
            dataAdapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinnerG1.setAdapter(dataAdapterG);
            spinnerG2.setAdapter(dataAdapterG);
            spinnerC.setAdapter(dataAdapterC);
            imgView = (ImageView) rootView.findViewById(R.id.imageViewCover);
            final Button addCover = (Button) rootView.findViewById(R.id.buttonImage);

            addCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    loadImagefromGallery(rootView);
                }
            });
            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * this method check (by the title's name) whether that title exists in the store's stock.
                     * if so -> doe's not require additional details for the wanted title.
                     * if not -> discovers the other edit texts, and require to fill them in order to create later a new title and adding it to the store's stock.
                     */
                    try {
                        t_name=tName.getText().toString();
                        //if that vendor already has that title in his stock
                        if (this_ven.find_title(t_name) != -1)
                            throw new Exception("you already have copies of this title, you can add copies via the 'show stock button'");

                        if (t_name.equals(""))
                            throw new Exception("title name must be filled");

                        amount.setVisibility(View.VISIBLE);
                        price.setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                        order.setVisibility(View.VISIBLE);
                        if (store.find_index_title(t_name) == -1)//if that title does not exist in the store's stock yet
                        {
                            author.setVisibility(View.VISIBLE);
                            pub_year.setVisibility(View.VISIBLE);
                            pub_house.setVisibility(View.VISIBLE);
                            length.setVisibility(View.VISIBLE);
                            spinnerG1.setVisibility(View.VISIBLE);
                            spinnerG2.setVisibility(View.VISIBLE);
                            spinnerC.setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.textView5).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.textView6).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.textView8).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.textView9).setVisibility(View.VISIBLE);
                            addCover.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });


            order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //if that title does not exist in the store's stock yet
                        if (store.find_index_title(t_name)== -1)
                        {
                            //if not *all* the fields are filled
                            if (t_name.equals("") || author.getText().toString().equals("") || pub_house.getText().toString().equals("") || pub_year.getText().toString().equals("") || chosen_c.equals("") || (chosen_g1.equals("") && chosen_g2.equals("")) || price.getText().toString().equals("") || amount.getText().toString().equals("") || Integer.parseInt(amount.getText().toString()) <= 0 || Integer.parseInt(length.getText().toString()) <= 0 || Double.parseDouble(price.getText().toString()) < 0 || Integer.parseInt(pub_year.getText().toString()) < 1500)
                                throw new Exception("please make sure you complete *all* the fields correctly");

                            if(cover==null)
                                cover = BitmapFactory.decodeResource(getActivity().getResources(),
                                        R.drawable.blankcover);
                            //otherwise
                            try
                            {
                                Title t = new Title(t_name, author.getText().toString(), Integer.parseInt(pub_year.getText().toString()), (Enums.GENRES.valueOf(chosen_g1)), (Enums.GENRES.valueOf(chosen_g2)), pub_house.getText().toString(), (Enums.CATEGORY.valueOf(chosen_c)), Integer.parseInt(length.getText().toString()), Double.parseDouble(price.getText().toString()), cover);
                                store.add_title_to_vendor(this_ven_id, t, Integer.parseInt(amount.getText().toString()), Double.parseDouble(price.getText().toString()));
                            }
                            catch (Exception ex) {Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show(); return;}
                        }
                        //if that title **exists** in the store's stock (but that vendor does not have that title in his stock yet)
                        else {
                            //if not all the required fields (title_name, price and amount) are filled
                            if (t_name.equals("") || price.getText().toString().equals("") || amount.getText().toString().equals("") || Integer.parseInt(amount.getText().toString()) <= 0 || Integer.parseInt(price.getText().toString()) < 0)
                                throw new Exception("please make sure you complete *all* the fields correctly");
                            //otherwise
                            try {
                                Title t = store.getTitle(t_name);
                                store.add_title_to_vendor(this_ven_id, t, Integer.parseInt(amount.getText().toString()), Double.parseDouble(price.getText().toString()));
                            } catch (Exception ex) {Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show(); return;}
                        }

                        uploadImage();
                        ((VendorSystemActivity)getActivity()).onFragmentBackPressed("title's ordering was executed successfully:)");
                    }
                    catch (Exception ex) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, 1);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == getActivity().RESULT_OK && null != data)
            {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String

                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap image = BitmapFactory.decodeFile(imgDecodableString, options);
                //Bitmap image=BitmapFactory.decodeFile(imgDecodableString);
                imgView.setImageBitmap(image);
                cover=image;
            }
            else
            {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void uploadImage() {
        final String image = getStringImage(cover);
        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Please wait...", "uploading", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadHandler rh = new UploadHandler();
                HashMap<String, String> param = new HashMap<String, String>();
                param.put(UPLOAD_TITLE, t_name);
                param.put(UPLOAD_IMAGE, image);
                String result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode==1)
            if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Start the Intent
                    startActivityForResult(galleryIntent, 1);

                } else {
                    Toast.makeText(getActivity(), "can't upload image without permission", Toast.LENGTH_SHORT);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
}

