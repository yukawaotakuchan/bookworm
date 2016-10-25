package model.dataSource;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import model.backend.Backend;
import storeComponents.Client;
import storeComponents.Copies;
import storeComponents.Enums;
import storeComponents.Purchase;
import storeComponents.Title;
import storeComponents.Vendor;

/**
 * Created by AyeletHaShachar on 03/01/2016.
 */
public class DataBaseSQL implements Backend {
    final public String web_url = "http://bookworm.net23.net/";



    public List<Client> clients;
    public List<Vendor> vendors;
    public List<Title> titles;

    boolean t1, t2, finished, titlesLoaded, getP, fail, search;
    Map<String, Object> searchParams = new LinkedHashMap<>();
    Map<String, Object> purchParams = new LinkedHashMap<>();
    int status;
    Client c;
    Vendor v;
    Copies copy;

    Bitmap cover;

    public DataBaseSQL() throws Exception {
        clients= new LinkedList<>();
        vendors= new LinkedList<>();
        titles= new LinkedList<>();
        /*setClientsListFromMysqlDB2();
        setVendorsListFromMysqlDB2();*/
        setTitlesListFromMysqlDB2();

        clients.clear();
        vendors.clear();
    }


    /**
     * this method returns the output-string of the query in 'getclientslist'.
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String GET(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        StringBuffer response = new StringBuffer();


        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) //success
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
        }
        return response.toString();
    }
    /**
     * this method aims to change the
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    private static String POST(String url, Map<String, Object> params) throws IOException {
        //Convert 'Map<String,Object>' into 'key=value&key=value' pairs.
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(postData.toString().getBytes("UTF-8"));
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        StringBuffer response = new StringBuffer();
        System.out.println("POST Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK)//success
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);

            in.close();
        }
        return response.toString();
    }


    /**
     * this method initializes the clients list using the string which the 'GET' method returns.
     */
    public void setClientsListFromMysqlDB2() throws Exception {
        try {
            t1 = false;
            new AsyncTask<Void, Void, Integer>(){
                @Override
                protected Integer doInBackground(Void... voids){
                    try {
                        //'params' is used to post data to 'POST' method, in order to use this data in the qsl queries that in the php file.
                        Map<String, Object> params = new LinkedHashMap<>();
                        String get, post;
                        clients = new LinkedList<Client>();

                        get = GET(web_url + "getClientsList.php");
                        if (get.equals("0 results")) {
                            clients = new LinkedList<Client>();
                            return 0;
                        }
                        JSONArray result1 = new JSONObject(get).getJSONArray("clients");

                        Client tempC;
                        Copies tempC_tempP_copy;
                        Title tempC_tempP_copy_title;
                        Purchase tempC_tempP;
                        for (int i = 0; i < result1.length(); i++) //to get *all* the clients
                        {
                            try {
                                tempC = new Client(result1.getJSONObject(i).getString("id"), result1.getJSONObject(i).getString("name"), result1.getJSONObject(i).getString("password"), result1.getJSONObject(i).getString("phone"), result1.getJSONObject(i).getString("address"), result1.getJSONObject(i).getString("email"), Enums.GENDER.valueOf(result1.getJSONObject(i).getString("gender")), result1.getJSONObject(i).getString("birthdate").toString(), result1.getJSONObject(i).getDouble("accumulative_payment"), result1.getJSONObject(i).getInt("discount"));

                                params.clear();
                                params.put("client_id", result1.getJSONObject(i).getString("id"));
                                post = POST(web_url + "getClientPurchases.php", params);

                                if (!post.equals("0 results")) {
                                    JSONArray result2 = new JSONObject(post).getJSONArray("client_purchases");

                                    for (int j = 0; j < result2.length(); j++) //to get *all* the current client's purchases
                                    {
                                        try {
                                            //first, we need to get the title and the copy of that current purchase.
                                            params.clear();
                                            params.put("title_name", result2.getJSONObject(j).getString("title_name"));
                                            post = POST(web_url + "getTitle.php", params);
                                            if (post.equals("0 results"))
                                                throw new Exception("purchase must have a title");
                                            JSONArray result3 = new JSONObject(post).getJSONArray("title");
                                            tempC_tempP_copy_title = new Title(result3.getJSONObject(0).getString("title_name"), result3.getJSONObject(0).getString("author"), result3.getJSONObject(0).getInt("publishing_year"), Enums.GENRES.valueOf(result3.getJSONObject(0).getString("genre1")), Enums.GENRES.valueOf(result3.getJSONObject(0).getString("genre2")), result3.getJSONObject(0).getString("publishing_house"), Enums.CATEGORY.valueOf(result3.getJSONObject(0).getString("category")), result3.getJSONObject(0).getInt("length"), result3.getJSONObject(0).getDouble("recommended_price"), null);

                                            params.clear();
                                            params.put("vendor_id", result2.getJSONObject(j).getString("vendor_id"));
                                            params.put("title_name", result2.getJSONObject(j).getString("title_name"));
                                            post = POST(web_url + "getCopy.php", params);
                                            if (post.equals("0 results"))
                                                throw new Exception("purchase must have a copy");
                                            JSONArray result4 = new JSONObject(post).getJSONArray("copies");
                                            tempC_tempP_copy = new Copies(tempC_tempP_copy_title, result4.getJSONObject(0).getInt("amount"), result4.getJSONObject(0).getDouble("price"), result4.getJSONObject(0).getString("vendor_id"));

                                            //then, we can create a 'purchase' object.
                                            tempC_tempP = new Purchase(result2.getJSONObject(j).getString("client_id"), result2.getJSONObject(j).getString("vendor_id"), tempC_tempP_copy, result2.getJSONObject(j).getString("date"), result2.getJSONObject(j).getDouble("final_price"), result2.getJSONObject(j).getInt("amount"));

                                            /**
                                             * and then - to add it to the current client's list of purchases.
                                             */
                                            tempC.getPrev_purchs().add(tempC_tempP);
                                        }
                                        catch (Exception ex){ex.printStackTrace();}
                                    }
                                }

                                clients.add(tempC);
                            }
                            catch(Exception ex){ex.printStackTrace();}
                        }

                        } catch (Exception e) {
                        e.printStackTrace();
                    }
                    t1=true;
                    return 1;
                }

                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * this method initializes the vendors list using the string which the 'GET' method returns.
     */
    public void setVendorsListFromMysqlDB2() throws IOException {
        try {
            t2= false;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids){
                    try {
                        //'params' is used to post data to 'POST' method, in order to use this data in the qsl queries that in the php file.
                        Map<String, Object> params = new LinkedHashMap<>();
                        String get, post;
                        vendors = new LinkedList<Vendor>();

                        get = GET(web_url + "getVendorsList.php");
                        if (get.equals("0 results")) {
                            vendors = new LinkedList<Vendor>();
                            return null;
                        }

                        JSONArray result1 = new JSONObject(get).getJSONArray("vendors");

                        Vendor tempV;
                        Title tempV_tempP_copy_title;
                        Copies tempV_tempP_copy;
                        Purchase tempV_tempP;

                        for (int i = 0; i < result1.length(); i++) //to get *all* the vendors
                        {
                            try {
                                tempV = new Vendor(result1.getJSONObject(i).getString("id"),
                                        result1.getJSONObject(i).getString("name"),
                                        result1.getJSONObject(i).getString("password"),
                                        result1.getJSONObject(i).getString("phone"),
                                        result1.getJSONObject(i).getString("address"),
                                        result1.getJSONObject(i).getString("email"),
                                        Enums.GENDER.valueOf(result1.getJSONObject(i).getString("gender")),
                                        result1.getJSONObject(i).getString("birthdate"),
                                        result1.getJSONObject(i).getString("paypal_account_mail"));

                                if (result1.getJSONObject(i).getInt("admin_authen") == 1)
                                    tempV.setAdmin_authen(true);

                                params.clear();
                                params.put("vendor_id", tempV.getId());
                                post = POST(web_url + "getVendorPurchases.php", params);
                                if (!post.equals("0 results")) {
                                    JSONArray result2 = new JSONObject(post).getJSONArray("vendor_purchases");

                                    for (int j = 0; j < result2.length(); j++) //to get *all* the current vendor's purchases
                                    {
                                        try {
                                            //first, we need to get the title and the copy of that current purchase.
                                            params.clear();
                                            params.put("title_name", result2.getJSONObject(j).getString("title_name"));
                                            post = POST(web_url + "getTitle.php", params);
                                            if (post.equals("0 results")) // if the current purchase of the current client has no title - impossible case!!!
                                                throw new Exception("purchase must have a title");
                                            JSONArray result3 = new JSONObject(post).getJSONArray("title");
                                            try {
                                                tempV_tempP_copy_title = new Title(result3.getJSONObject(0).getString("title_name"), result3.getJSONObject(0).getString("author"), result3.getJSONObject(0).getInt("publishing_year"), Enums.GENRES.valueOf(result3.getJSONObject(0).getString("genre1")), Enums.GENRES.valueOf(result3.getJSONObject(0).getString("genre2")), result3.getJSONObject(0).getString("publishing_house"), Enums.CATEGORY.valueOf(result3.getJSONObject(0).getString("category")), result3.getJSONObject(0).getInt("length"), result3.getJSONObject(0).getDouble("recommended_price"), null);
                                            } catch (Exception ex) {
                                                break;
                                            }
                                            params.clear();
                                            params.put("vendor_id", tempV.getId());
                                            params.put("title_name", result2.getJSONObject(j).getString("title_name"));
                                            post = POST(web_url + "getCopy.php", params);
                                            if (post.equals("0 results")) // if the current purchase of the current client has no copy - impossible case!!!
                                                throw new Exception("purchase must have a copy");
                                            JSONArray result4 = new JSONObject(post).getJSONArray("copies");
                                            tempV_tempP_copy = new Copies(tempV_tempP_copy_title, result4.getJSONObject(0).getInt("amount"), result4.getJSONObject(0).getDouble("price"), tempV.getId());

                                            //then we can create a 'purchase' object
                                            tempV_tempP = new Purchase(result2.getJSONObject(j).getString("client_id"), result2.getJSONObject(j).getString("vendor_id"), tempV_tempP_copy, result2.getJSONObject(j).getString("date"), result2.getJSONObject(j).getDouble("final_price"), result2.getJSONObject(j).getInt("amount"));

                                            //and then we can add in to the current vendor's list of purchases.
                                            tempV.getPurchs().add(tempV_tempP);
                                        }
                                        catch (Exception ex){ex.printStackTrace();}
                                    }
                                }

                                params.clear();
                                params.put("vendor_id", tempV.getId());
                                post = POST(web_url + "getVendorCopies.php", params);
                                if (!post.equals("0 results")) // if the vendor has any book in his stock
                                {
                                    JSONArray result5 = new JSONObject(post).getJSONArray("vendor_copies");

                                    for (int j = 0; j < result5.length(); j++) //to get *all* the current vendor's stock
                                    {
                                        try {
                                            //first, we need to get the title that current copy.
                                            params.clear();
                                            params.put("title_name", result5.getJSONObject(j).getString("title_name"));
                                            post = POST(web_url + "getTitle.php", params);
                                            if (post.equals("0 results")) // if the current copy of the current client has no title - impossible case!!!
                                                throw new Exception("copy must has a title");
                                            JSONArray result6 = new JSONObject(post).getJSONArray("title");
                                            Title tempV_tempCo_title = new Title(result6.getJSONObject(0).getString("title_name"), result6.getJSONObject(0).getString("author"), result6.getJSONObject(0).getInt("publishing_year"), Enums.GENRES.valueOf(result6.getJSONObject(0).getString("genre1")), Enums.GENRES.valueOf(result6.getJSONObject(0).getString("genre2")), result6.getJSONObject(0).getString("publishing_house"), Enums.CATEGORY.valueOf(result6.getJSONObject(0).getString("category")), result6.getJSONObject(0).getInt("length"), result6.getJSONObject(0).getDouble("recommended_price"), null);

                                            tempV_tempP_copy = new Copies(tempV_tempCo_title, result5.getJSONObject(j).getInt("amount"), result5.getJSONObject(j).getDouble("price"), tempV.getId());

                                            tempV.getStock().add(tempV_tempP_copy);
                                        }
                                        catch (Exception ex){ex.printStackTrace();}
                                    }
                                }

                                vendors.add(tempV);
                            }
                            catch (Exception ex){ex.printStackTrace();}
                        }
                    }
                    catch (Exception e)
                    {e.printStackTrace();}
                    t2=true;
                    return null;
                }

                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    t2 = true;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * this method initializes the titles list using the string which the 'GET' method returns.
     */
    public void setTitlesListFromMysqlDB2() throws Exception{
        try {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids){
                    try{
                        titlesLoaded=false;
                        String get;
                        titles = new LinkedList<>();

                        //gets all the store's books from database
                        get = GET(web_url + "getTitlesList.php");
                        if (get.equals("0 results")) {
                            titles = new LinkedList<>();
                            return null;
                        }

                        JSONArray result = new JSONObject(get).getJSONArray("titles");

                        //to get *all* the titles
                        for (int i = 0; i < result.length(); i++)
                            titles.add(new Title(   result.getJSONObject(i).getString("title_name"),
                                                    result.getJSONObject(i).getString("author"),
                                                    result.getJSONObject(i).getInt("publishing_year"),
                                                    Enums.GENRES.valueOf(result.getJSONObject(i).getString("genre1")),
                                                    Enums.GENRES.valueOf(result.getJSONObject(i).getString("genre2")),
                                                    result.getJSONObject(i).getString("publishing_house"),
                                                    Enums.CATEGORY.valueOf(result.getJSONObject(i).getString("category")),
                                                    result.getJSONObject(i).getInt("length"),
                                                    result.getJSONObject(i).getDouble("recommended_price"),
                                                    null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    titlesLoaded = true;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }///////////////////////////////////


    public boolean isVendor(final String id) throws Exception {return (getVendor(id)!=null);}////?//
    public boolean isClient(String id) {return (getClient(id)!=null);}/////////////?////////////////
    public boolean client_mail_valid(String id, String email){
        c=getClient(id);
        if(c==null)
            return false;
        return c.getEmail().equals(email);
    }//////////////////////////////////
    public boolean vendor_mail_valid(String id, String email){
    v=getVendor(id);
    if(v==null)
            return false;
    return v.getEmail().equals(email);
    }/////////////////////////////////



    //these 3 methods - adding, updating and removing a client. to the lists and to the DB.
    public void add_client(final Client c) throws Exception {
        status = -1;
            if (find_index_client(c.getId()) == -1) // if this client doesn't exist yet in the store's database
            {
                //add the current client to the store's clients list
                clients.add(c);

                //add the current client to the SQL-database
                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        try {
                            Map<String, Object> params = new LinkedHashMap<>();
                            params.put("address", c.getAddress());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String d = sdf.format(c.getBirthdate());
                            params.put("birthdate", d);
                            params.put("email", c.getEmail());
                            params.put("gender", c.getGender());
                            params.put("id", c.getId());
                            params.put("name", c.getName());
                            params.put("password", c.getPassword());
                            params.put("accumulative_payment", c.getAccumulative_payment());
                            params.put("discount", c.getDiscount());
                            //params.put("permission", c.getPermission());
                            params.put("phone", c.getPhone());

                            String post = POST(web_url + "addClient.php", params);

                            if (!post.equals("client added successfully")) {
                                fail=true;
                                return 0;
                            }
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                        status=0;
                        return 1;
                    }
                    @Override
                    protected void onPreExecute() {
                    }
                    @Override
                    protected void onPostExecute(Integer i) {}
                }.execute().get();
                while(status==-1)
                    Thread.sleep(100);
                if(status==1)
                    throw new Exception("username already in use");
            }
            else
                throw new Exception("username already in use");
    }///////////////////////////////////
    public void update_clients_list(final Client c) throws Exception {
        // Because in *updateClient.php file* we will delete the tuple of the relevant client (this client) - we don't need to do this *here*.
        // So actually, the update_client method is exactly like the add_client method, besides the calling to POST method.
        int c_index = find_index_client(c.getId());
        if (c_index != -1) // if this client doesn't exist yet in the store's database
        {
            //update the current client in the clients-list
            clients.get(c_index).edit_details(c);

            //update the current client in the SQL database
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();

                        params.put("address", c.getAddress());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String d = sdf.format(c.getBirthdate());
                        params.put("birthdate", d);
                        params.put("email", c.getEmail());
                        params.put("gender", c.getGender());
                        params.put("id", c.getId());
                        params.put("name", c.getName());
                        params.put("password", c.getPassword());
                        params.put("accumulative_payment", c.getAccumulative_payment());
                        params.put("discount", c.getDiscount());
                        //params.put("permission", c.getPermission());
                        params.put("phone", c.getPhone());

                        String post = POST(web_url + "updateClient.php", params);
                        if (!post.equals("client updated successfully"))
                            throw new Exception("updating the client failed");
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }
                @Override
                protected void onPreExecute() {
                }
                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();
        }
        else
            throw new Exception("client doesn't exist");
    }//////////////////////////
    public void delete_client(final String client_id) throws Exception {
        int c_index = find_index_client(client_id);
        if (c_index == -1)
            throw new Exception("client doesn't exist");
        else
        {
            //remove the current client from the store's clients list
            clients.remove(clients.get(c_index));

            //remove the current client from the SQL-database
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try
                    {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("client_id", client_id);

                        String post = POST(web_url + "deleteClient.php", params);
                        if (!post.equals("client deleted successfully"))
                            throw new Exception("delete the client failed");
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }
                @Override
                protected void onPreExecute() {
                }
                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();
        }
    }////////////////////////
    public Client getClient (final String c_id) {
        for (Client c : clients)
            if (c.getId().equals(c_id))
                return c;
        status=-1;
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    String post, url;
                    JSONArray result = null;
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.clear();
                        params.put("id", c_id);
                        url = web_url + "getClient.php";
                        post = (POST(url, params));
                        result = new JSONObject(post).getJSONArray("client");
                        c = new Client(result.getJSONObject(0).getString("id"),
                                result.getJSONObject(0).getString("name"),
                                result.getJSONObject(0).getString("password"),
                                result.getJSONObject(0).getString("phone"),
                                result.getJSONObject(0).getString("address"),
                                result.getJSONObject(0).getString("email"),
                                Enums.GENDER.valueOf(result.getJSONObject(0).getString("gender")),
                                result.getJSONObject(0).getString("birthdate").toString(),
                                result.getJSONObject(0).getDouble("accumulative_payment"),
                                result.getJSONObject(0).getInt("discount"));
                        clients.add(c);
                        status = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = 1;
                    }
                    return null;
                }
                @Override
                protected void onPreExecute() {
                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        while(status==-1)
             try { Thread.sleep(100);}
             catch (InterruptedException e) { e.printStackTrace();}
        if(status==0)
            return c;
        return null;
    }///////////////////////////////////////////////

    //these 3 methods - adding, updating and removing a vendor. to the lists and to the DB.
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void add_vendor(final Vendor v) throws Exception {
                //add the current client to the SQL-database
        new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        try {
                            Map<String, Object> params = new LinkedHashMap<>();
                            params.put("vendor_id", v.getId());
                            params.put("name", v.getName());
                            params.put("password", v.getPassword());
                            params.put("phone", v.getPhone());
                            params.put("address", v.getAddress());
                            params.put("email", v.getEmail());
                            params.put("gender", v.getGender());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String d = sdf.format(v.getBirthdate());
                            params.put("birthdate", d);
                            //params.put("permission", v.getPermission());
                            params.put("paypal_account_mail", v.getPaypal_account_mail());
                            String post = POST(web_url + "addVendor.php", params);
                            /*if(post.contains("Duplicate")|| )
                                throw new Exception("vendor already exist");*/
                            if (!post.equals("vendor added successfully"))
                                status=1;
                            }
                        catch (Exception x) {
                            x.printStackTrace();
                            status=1;
                        }
                        status=0;
                        return 1;
                    }
                    @Override
                    protected void onPreExecute() {
                    }
                    @Override
                    protected void onPostExecute(Integer i) {}
                }.execute().get();
        while(status==-1)
            Thread.sleep(100);
        if(status==1)
            throw new Exception("username already in use");
        }///////////////////////////////////

    public void update_ven_list(final Vendor v) throws Exception {
        int v_index = find_index_ven(v.getId());
        if (v_index != -1) {
            //update the current vendor in the vendors-list
            vendors.get(v_index).edit_details(v);

            //update the current vendor in the SQL database
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();

                        params.put("address", v.getAddress());
                        params.put("admin_authen", v.getAdmin_authen());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String d = sdf.format(v.getBirthdate());
                        params.put("birthdate", d);
                        params.put("email", v.getEmail());
                        params.put("gender", v.getGender());
                        params.put("id", v.getId());
                        params.put("name", v.getName());
                        params.put("password", v.getPassword());
                        params.put("paypal_account_mail", v.getPaypal_account_mail());
                        params.put("phone", v.getPhone());

                        String post = POST(web_url + "updateVendor.php", params);
                        if (!post.equals("vendor updated succesfuly"))
                            throw new Exception("adding the vendor failed");
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }
                @Override
                protected void onPreExecute() {
                }
                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();

        } else
            throw new Exception("vendor doesn't exist");
    }//////////////////////////////
    public void delete_ven(final String ven_id) throws Exception {
        int v_index = find_index_ven(ven_id);
        if (v_index == -1)
            throw new Exception("the vendor doesn't exist");
        else {
            //remove the current vendor from the store's vendors list
            vendors.remove(vendors.get(v_index));

            //remove the current vendor from the store's vendors list
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("vendor_id", ven_id);

                        String post = POST(web_url + "deleteVendor.php", params);
                        if (!post.equals("vendor deleted successfully"))
                            throw new Exception("delete vendor failed");
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }
                @Override
                protected void onPreExecute() {
                }
                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();
        }
    }//////////////////////////////
    public Vendor getVendor(final String ven_id){///////////////////////////////////////////////////
        for(Vendor v : vendors)
            if(v.getId().equals(ven_id))
                return v;
        status=-1;
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    String post, url;
                    JSONArray result = null;
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.clear();
                        params.put("id", ven_id);
                        url = web_url + "getVendor.php";
                        post = (POST(url, params));
                        result = new JSONObject(post).getJSONArray("vendor");
                        v = new Vendor(result.getJSONObject(0).getString("id"),
                                result.getJSONObject(0).getString("name"),
                                result.getJSONObject(0).getString("password"),
                                result.getJSONObject(0).getString("phone"),
                                result.getJSONObject(0).getString("address"),
                                result.getJSONObject(0).getString("email"),
                                Enums.GENDER.valueOf(result.getJSONObject(0).getString("gender")),
                                result.getJSONObject(0).getString("birthdate"),
                                result.getJSONObject(0).getString("paypal_account_mail"));
                        if (result.getJSONObject(0).getInt("admin_authen") == 1)
                            v.setAdmin_authen(true);
                        vendors.add(v);
                        status = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = 1;
                    }
                    return null;
                }
                @Override
                protected void onPreExecute() {
                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        while(status==-1)
            try { Thread.sleep(100);}
            catch (InterruptedException e) { e.printStackTrace();}
        if(status==0)
            return v;
        return null;
    }///////////////////////////////////////////////

    //adding a new title to the store (in the lists and in the DB). only for titles which isn't in the store yet!!///
    //adding a new title to the store (in the lists and in the DB). only for titles which isn't in the store yet!!///
    public void add_title(final Title t) throws Exception {
        if (find_index_title(t.getTitle_name()) != -1)
            throw new Exception("title already exist");
        else
        {
            titles.add(t);
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();

                        params.put("title_name", t.getTitle_name());
                        params.put("author", t.getAuthor());
                        params.put("publishing_year", t.getPublishing_year());
                        params.put("publishing_house", t.getPublishing_house());
                        params.put("genre1", t.getGenre1());
                        params.put("genre2", t.getGenre2());
                        params.put("category", t.getCategory());
                        params.put("length", t.getLength());
                        params.put("recommended_price", t.getRecommended_price());

                        String post = POST(web_url + "addTitle.php", params);
                        if (!post.equals("New Title added successfully"))
                            throw new Exception("adding the title failed");
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }
                @Override
                protected void onPreExecute() {
                }
                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();
        }
    }/////////////////////////////////////
    //adding a new title to the vendor's stock (in the lists and in the DB). only for titles which isn't in his stock yet!!
    public void add_title_to_vendor(final String ven_id, final Title t, final int amount, final double price) throws Exception {
        int v_index = find_index_ven(ven_id);
        int t_index = find_index_title(t.getTitle_name());
        if (v_index != -1) //if the vendor exists
        {
            if(vendors.get(v_index).find_title(t.getTitle_name())!=-1) //if that vendor already has copies of that title
                throw new Exception("vendor already has that book in his stock");

            if (t_index == -1) //if the wanted title doesn't exist in the store
                add_title(t); //adding title to the store. to the lists and to the DB

            //adding the new title to the vendor's stock. only to the lists!
            vendors.get(v_index).add_copies_to_stock(t, amount, price);

            //adding the new title to the vendor's stock. only to the DB!
            add_copy_to_db(new Copies(t, amount, price, ven_id));
            /*

             */
        }
        else
            throw new Exception("vendor not exists!");
    }//
    //updating the store's list of titles (int the lists and in the DB). only for titles which exists in the store already
    public void update_titles_list(final Title t) throws Exception {
        int t_index = find_index_title(t.getTitle_name());
        if (t_index != -1) { // if the title exists

            //update the current title in the vendors-list
            titles.get(t_index).edit_details(t);

            //update the current title in the SQL database
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();

                        params.put("title_name", t.getTitle_name());
                        params.put("author", t.getAuthor());
                        params.put("publishing_year", t.getPublishing_year());
                        params.put("publishing_house", t.getPublishing_house());
                        params.put("genre1", t.getGenre1());
                        params.put("genre2", t.getGenre2());
                        params.put("category", t.getCategory());
                        params.put("length", t.getLength());
                        params.put("recommended_price", t.getRecommended_price());

                        String post = POST(web_url + "updateTitle.php", params);
                        if (!post.equals("title updated successfully"))
                            throw new Exception("updating the title failed");
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }
                @Override
                protected void onPreExecute() {
                }
                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();

        } else
            throw new Exception("sorry, this title doesn't exist in our store");
    }////////////////////////////
    //deleting a title from the store, in the lists and in the DB.(cascade all the vendor's copies).
    public void delete_title(final String title_name) throws Exception {
        {
            int t_index = find_index_title(title_name);
            if (t_index == -1)
                throw new Exception("the title doesn't exist");
            else {
                //remove the current vendor from the store's vendors list
                titles.remove(titles.get(t_index));

                //remove the current vendor from the store's vendors list
                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        try {
                            Map<String, Object> params = new LinkedHashMap<>();
                            params.put("title_name", title_name);

                            String post = POST(web_url + "deleteTitle.php", params);
                            if (!post.equals("title deleted successfully"))
                                throw new Exception("delete the title failed");
                        }
                        catch (Exception x) {
                            x.printStackTrace();
                        }
                        return 1;
                    }
                    @Override
                    protected void onPreExecute() {
                    }
                    @Override
                    protected void onPostExecute(Integer i) {
                    }
                }.execute().get();

                //after cascading the vendors' copies - we want to load the vendors-list again.
                setVendorsListFromMysqlDB2();
            }
        }
    }////////////////////////
    public Title getTitle(String t_name) {
        for(Title t : titles)
            if(t.getTitle_name().equals(t_name))
                return t;
        return null;
    }//////////////////////////////////////////////////////

    //these 3 methods - adding, updating and deleting a copy - are only to the DB, not from the lists
    private void add_copy_to_db (final Copies c) throws Exception {
        if(find_index_title(c.getTitle().getTitle_name()) == -1)//if the corresponding title doesn't exist in the store's titles
            throw new Exception("title doesn't exist. cannot add its copy");
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    Map<String, Object> params = new LinkedHashMap<>();

                    params.put("title_name", c.getTitle().getTitle_name());
                    params.put("vendor_id", c.getVen_id());
                    params.put("price", c.getInitial_price());
                    params.put("amount", c.getAmount());

                    String post = POST(web_url + "addCopy.php", params);
                    if (!post.equals("copies added successfully"))
                        throw new Exception("adding the copy failed");
                }
                catch (Exception x) {
                    x.printStackTrace();
                }
                return 1;
            }
            @Override
            protected void onPreExecute() {
            }
            @Override
            protected void onPostExecute(Integer i) {
            }
        }.execute().get();
    }/////////////////////////////

    private void update_copy_on_db(final Copies c) throws Exception {
        if (getCopy(c.getTitle().getTitle_name(), c.getVen_id()) == null)
            throw new Exception("copy doesn't exist. cannot update the copy");
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    Map<String, Object> params = new LinkedHashMap<>();

                    params.put("title_name", c.getTitle().getTitle_name());
                    params.put("vendor_id", c.getVen_id());
                    params.put("price", c.getInitial_price());
                    params.put("amount", c.getAmount());

                    String post = POST(web_url + "updateCopy.php", params);
                    if (!post.equals("copy updated successfully"))
                        throw new Exception("updating the copy failed");
                }
                catch (Exception x) {
                    x.printStackTrace();
                }
                return 1;
            }
            @Override
            protected void onPreExecute() {
            }
            @Override
            protected void onPostExecute(Integer i) {
            }
        }.execute().get();
    }///////////////////////////
    private void delete_copy_from_db (final String t_name, final String v_id) throws Exception {
       // if (getCopy(t_name, v_id) == null)
         //   throw new Exception("copy doesn't exist. cannot delete the copy");
        //else
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("title_name", t_name);
                    params.put("vendor_id", v_id);
                    String post = POST(web_url + "deleteCopy.php", params);
                    if (!post.equals("copies deleted successfully"))
                        throw new Exception("delete the copies failed");
                }
                catch (Exception x) {
                    x.printStackTrace();
                }
                return 1;
            }
            @Override
            protected void onPreExecute() {
            }
            @Override
            protected void onPostExecute(Integer i) {
            }
        }.execute().get();
    }

    // TODO: 09/10/2016 unify with update_copy_on_db 
    //updating vendor's copy's price. (in the lists and in the DB)
    public void update_copy_price(final String ven_id, final String title_name, final double price) throws Exception {
        for(Copies c : vendors.get(find_index_ven(ven_id)).getStock())
            if(c.getTitle().getTitle_name().equals(title_name))
                c.setInitial_price(price);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Map<String, Object> params = new LinkedHashMap<>();

                    params.put("vendor_id", ven_id);
                    params.put("title_name", title_name);
                    params.put("price", price);
                    String post = POST(web_url + "updateCopyPrice.php", params);
                    if (!post.equals("price updated"))
                        throw new Exception("update failed");
                }
                catch (Exception x) {
                    x.printStackTrace();
                }
            return null;
            }
            protected void onPostExecute() {
            }
        }.execute().get();
    }///

    public Copies getCopy (final String t_name, final String v_id) {
        Vendor v = getVendor(v_id);
/*        for (Copies c : v.getStock())
            if(c.getTitle().getTitle_name().equals(t_name))
                return c;*/
        copy=null;
        status=-1;
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    String post, url;
                    JSONArray result = null;
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.clear();
                        params.put("vendor_id", v_id);
                        params.put("title_name", t_name);
                        url = web_url + "getCopy.php";
                        post = (POST(url, params));
                        if(!post.equals("no result")) {
                            result = new JSONObject(post).getJSONArray("copy");
                            copy = new Copies(getTitle(t_name),
                                    result.getJSONObject(0).getInt("amount"),
                                    result.getJSONObject(0).getDouble("price"),
                                    v_id);
                        }
                        status = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = 1;
                    }
                    return null;
                }
                @Override
                protected void onPreExecute() {
                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        while(status==-1)
            try { Thread.sleep(100);}
            catch (InterruptedException e) { e.printStackTrace();}
        if(status==0)
            return copy;
        return null;
    }////////////////////////////

    public void execute_purchase(final Purchase p) throws Exception//at this function we already verified that the wanted book exists in the store, and the wanted vendor already chosen.
    {
        try {
            //update the relevant lists to the new purchase
            /*int v_index = find_index_ven(p.getVendor_id());
            vendors.get(v_index).add_sell(p);*/

            //adding the new purchase to the DB
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        //adding the purchase to the purchases-table
                        Map<String, Object> params = new LinkedHashMap<>();

                        params.put("title_name", p.getBooks().getTitle().getTitle_name());
                        params.put("vendor_id", p.getVendor_id());
                        params.put("client_id", p.getClient_id());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String d = sdf.format(p.getDate());
                        params.put("date", d);
                        params.put("final_price", p.getFinal_price());
                        params.put("amount", p.getCopies_amount());

                        String post = POST(web_url + "addPurchase.php", params);
                        if (!post.equals("New Purchase added successfully"))
                            throw new Exception("error accured. please try to refresh");
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                    return 1;
                }

                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(Integer i) {
                }
            }.execute().get();

            //updating the copies-table - in the DB
            Copies old_copy = p.getBooks();
            Copies new_copy = new Copies(old_copy.getTitle(), old_copy.getAmount() - p.getCopies_amount(), old_copy.getInitial_price(), p.getVendor_id());
            int c_index = find_index_client(p.getClient_id());
            clients.get(c_index).add_new_purchase(p);

            update_copy_on_db(new_copy);
            //updating the clients-table in the DB (in order to update the client's discount and accummulative-payment
            update_clients_list(getClient(p.getClient_id()));
        }
        catch (Exception ex){
           throw new Exception(ex.getMessage());}
    }///////////////////

    public boolean checkImpossibilityOfPurchase(Purchase p) {
        /*int v_index = find_index_ven(p.getVendor_id());
        int t_index_in_v_stock = vendors.get(v_index).find_title(p.getBooks().getTitle().getTitle_name());
        boolean rslt = vendors.get(v_index).getStock().get(t_index_in_v_stock).getAmount() >= p.getCopies_amount();
        return rslt;*/
        return true;
    }///////////////////////////////////

    //adding copies to a vendor, which **already** has such copies. (besides, the copies' title must be in the store already)
    public void add_copies_to_vendor (final String ven_id, final Title t, final int amount, final double price) throws Exception {
        double p=price;
        int v_index = find_index_ven(ven_id);
        int t_index = find_index_title(t.getTitle_name());
        if(v_index == -1)
            throw new Exception("vendor not exists!");
        if(t_index == -1) //if the wanted title doesn't exist in the store
            throw new Exception("such a title doesn't exist in the store yet!");
        final Copies old_c = getCopy(t.getTitle_name(), ven_id);
        if(price==0)
            p=old_c.getInitial_price();
        //adding the copies to the vendor's stock. only to the lists.
        vendors.get(v_index).add_copies_to_stock(t, amount, p);

        //update the copies-table in the SQL database
        update_copy_on_db(new Copies(t, old_c.getAmount(), p, ven_id));
    }//

    //removing copies to a vendor, which **already** has such copies. (besides, the copies' title must be in the store already)
    public void delete_copies_from_vendor (final String ven_id, final Title t, final int amount) throws Exception {
        int v_index = find_index_ven(ven_id);
        int t_index = find_index_title(t.getTitle_name());
        if(v_index == -1)
            throw new Exception("vendor not exists");
        if(t_index == -1)
            throw new Exception("such a title doesn't exist in the store!");
        //deleting the copies from that vendor's stock. only from the lists!
        vendors.get(v_index).delete_copies_from_stock(t, amount);
        //deleting the copies from that vendor's stock. only from the DB!
        final Copies old_c = getCopy(t.getTitle_name(), ven_id);
        update_copy_on_db(new Copies(t, old_c.getAmount(), old_c.getInitial_price(), ven_id));
    }//


    public List<Client> get_clients(){return clients;}//////////////////////////////////////////////
    public List<Vendor> get_vendors(){return vendors;}//////////////////////////////////////////////
    public List<Title> get_titles(){return titles;}/////////////////////////////////////////////////

    public int find_index_ven(String ven_id) {
        for (int i = 0; i < vendors.size(); i++)
            if (vendors.get(i).getId().equals(ven_id))
                return i;
        return -1;
    }//////////////////////////////////////////////////
    public int find_index_client(String client_id) {
        for (int i = 0; i < clients.size(); i++)
            if (clients.get(i).getId().toLowerCase().equals(client_id))
                return i;
        return -1;
    }////////////////////////////////////////////
    public int find_index_title(String title_name){
        for (int i = 0; i < titles.size(); i++)
            if (titles.get(i).getTitle_name().equals(title_name))
                return i;
        return -1;
    }/////////////////////////////////////////////

    public void addToCart(String c_id, boolean op, Purchase p)throws Exception//'op' false for remove from cart, 'op' true for add to cart//
    {
        int index= find_index_client(c_id);
        if(index==-1)
            throw new Exception("client doesn't exist");
        if(op)
            clients.get(index).addToCart(p);
        else
            clients.get(index).removeFromCart(p);
    }////////

    public void authentication_vendor (String ven_id) {
        Vendor old_ven = getVendor(ven_id);
        old_ven.setAdmin_authen(!old_ven.getAdmin_authen());
        try {
            update_ven_list(old_ven);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }/////////////////////////////////////////

    public List<Copies> getVendorStock(final String ven_id) {
        final int vendorIndex = find_index_ven(ven_id);
        if (vendorIndex != -1) {
            v=vendors.get(vendorIndex);
            if(v.getStock().isEmpty())
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Map<String, Object> params = new LinkedHashMap<>();
                            params.put("vendor_id", ven_id);
                            String post = POST(web_url + "getVendorCopies.php", params);
                            if (!post.equals("0 results")) // if the vendor has any book in his stock
                            {
                                JSONArray result = new JSONObject(post).getJSONArray("copies");
                                for (int j = 0; j < result.length(); j++) //to get *all* the current vendor's stock
                                {
                                    try {
                                        Copies copy = new Copies(getTitle(result.getJSONObject(j).getString("title_name")),
                                                                result.getJSONObject(j).getInt("amount"),
                                                                result.getJSONObject(j).getDouble("price"),
                                                                ven_id);

                                        vendors.get(vendorIndex).getStock().add(copy);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception ex) {
                        }
                        return null;
                    }
                    @Override
                    protected void onPreExecute() {
                    }
                }.execute();
        return vendors.get(vendorIndex).getStock();
        }
        return null;
    }///////////////////////////////////
    public List<Purchase> get_vendor_purchases(String ven_id) {
        purchParams.put("kind", "vendor");
        purchParams.put("id", ven_id);
        return getPurchasesList();
    }/////////////////////////////////
    public List<Purchase> get_client_purchases(String client_id) {
        purchParams.put("kind", "client");
        purchParams.put("id", client_id);
        return getPurchasesList();
    }//////////////////////////////

    public List<Purchase> getPurchasesList () {
        final ArrayList<Purchase> pList = new ArrayList<Purchase>();
        getP = false;
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids){
                try{
                    String post;

                    //gets all the store's books from database
                    post = POST(web_url + "getPurchasesList.php", purchParams);

                    JSONArray result = new JSONObject(post).getJSONArray("purchs");

                    //to get *all* the titles
                    for (int i = 0; i < result.length(); i++)
                        pList.add(new Purchase(result.getJSONObject(i).getString("client_id"),
                                result.getJSONObject(i).getString("vendor_id"),
                                new Copies( getTitle(result.getJSONObject(i).getString("title_name")),
                                        result.getJSONObject(i).getInt("amount"),
                                        result.getJSONObject(i).getDouble("price"),
                                        result.getJSONObject(i).getString("vendor_id")),
                                result.getJSONObject(i).getString("date"),
                                result.getJSONObject(i).getDouble("final_price"),
                                result.getJSONObject(i).getInt("quantity")
                                ));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                getP=true;
                return null;
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(Void aVoid) {}
        }.execute();

        while(!getP)
            try {Thread.sleep(100);}
            catch (InterruptedException e) { e.printStackTrace(); }
        return pList;
    }/////////////////////////////////////////////////

    public ArrayList<Copies> find_avilable_copies () {
        final ArrayList<Copies> copiesList = new ArrayList<Copies>();
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids){
                try{
                    String get;

                    //gets all the store's books from database
                    get = GET(web_url + "getAvailableCopies.php");
                    if (get.equals("0 results"))
                        return null;

                    JSONArray result = new JSONObject(get).getJSONArray("copies");

                    //to get *all* the titles
                    for (int i = 0; i < result.length(); i++)
                        copiesList.add(new Copies(getTitle(result.getJSONObject(i).getString("title_name")),
                                        result.getJSONObject(i).getInt("amount"),
                                        result.getJSONObject(i).getDouble("price"),
                                        result.getJSONObject(i).getString("vendor_id")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                titlesLoaded = true;
            }
        }.execute();

        /*for (int i = 0; i < vendors.size(); i++)
            if (vendors.get(i).getAdmin_authen()) {
                for(int j=0; j<vendors.get(i).getStock().size(); j++)
                    if (vendors.get(i).getStock().get(j).getAmount() > 0)
                        result.add(vendors.get(i).getStock().get(j));
            }*/
        return copiesList;
    }//////////////////////////////////////////

    //get list of titles
    public ArrayList<Copies> find_titles_copies (String t_name) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        /*ArrayList<Copies> available =find_avilable_copies();
        if(t_name.equals("") || t_name.equals("search title"))
            return available;
        for (Copies copy : available) {
            boolean b=copy.getTitle().getTitle_name().toLowerCase().contains(t_name.toLowerCase());
            if (b)
                result.add(copy);
        }*/
        searchParams.clear();
        searchParams.put("title", t_name);
        return result;
    }///////////////////////////////

    public ArrayList<Copies> find_copies_within_range (int min, int max, List<Copies> l) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        /*if (l.size()==0)
            l = find_avilable_copies();
        for (Copies copy : l)
            if (copy.getInitial_price()>=min && copy.getInitial_price()<=max)
                result.add(copy);*/
        searchParams.put("low", min);
        searchParams.put("high", max);
        return result;
    }//////

    public ArrayList<Copies> find_copies_of_genere (String genere, List<Copies> l) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        /*for (Copies copy : l)
            if (copy.getTitle().getGenre1().toString()==genere || copy.getTitle().getGenre2().toString()==genere )
                result.add(copy);*/
        searchParams.put("genre", genere);
        return result;
    }////////////

    public ArrayList<Copies> find_copies_of_category (String category, List<Copies> l) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        /*for (Copies copy : l)
            if (copy.getTitle().getCategory().toString()==category )
                result.add(copy);*/
        searchParams.put("category", category);
        return result;
    }////////

    public ArrayList<Copies> find_copies_by_author (String author, List<Copies> l) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        /*for (Copies copy : l)
            if (!copy.getTitle().getAuthor().equals(author))
                result.add(copy);*/
        searchParams.put("author", author);
        return result;
    }////////////

    public ArrayList<Copies> find_copies_by_vendor (String vendor, List<Copies> l) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        /*for (Copies copy : l)
            if (copy.getVen_id().equals(vendor))
                result.add(copy);*/
        searchParams.put("venodr" , vendor);
        return result;
    }////////////

    public ArrayList<Copies> getSearchCopies(List<Copies> l) {
        final ArrayList<Copies> copiesList = new ArrayList<Copies>();
        search=false;
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids){
                try{
                    String post;

                    //gets all the store's books from database
                    post = POST(web_url + "searchCopies.php", searchParams);

                    JSONArray result = new JSONObject(post).getJSONArray("copies");

                    //to get *all* the titles
                    for (int i = 0; i < result.length(); i++)
                        copiesList.add(new Copies(getTitle(result.getJSONObject(i).getString("title_name")),
                                result.getJSONObject(i).getInt("amount"),
                                result.getJSONObject(i).getDouble("price"),
                                result.getJSONObject(i).getString("vendor_id")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                search = true;
                return null;
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(Void aVoid) {}
        }.execute();

        while(!search)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return copiesList;
    }//////////////////////////////////

    public boolean client_is_valid (final String id, String pass) {
        c=getClient(id);
        if(c==null)
            return false;
        return c.getPassword().equals(pass);
    }/////////////////////////////
    public boolean vendor_is_valid (final String id, final String pass) {
        v=getVendor(id);
        if(v==null)
            return false;
        return v.getPassword().equals(pass);
    }///////////////////////

    public void fill()throws Exception{}

    // TODO: 23/10/2016  check if sync (without sleep) 
    public Bitmap downloadImage(final String t_name) throws Exception{
        finished=false;
        try
        {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... voids){
                    try {
                        cover=null;
                        Map<String, Object> params = new LinkedHashMap<>();
                        String post;
                        params.put("title", t_name);
                        //gets all the store's books from database
                        post = POST(web_url + "downloadImage.php", params);
                        if (post.equals("0 results"))
                            return null;
                        JSONArray result = new JSONObject(post).getJSONArray("cover");
                        String path = result.getJSONObject(0).getString("cover");
                        URL url = new URL(path);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        cover = BitmapFactory.decodeStream(input);
                        }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finished=true;
                    return null;
                }

                @Override
                protected void onPreExecute(){}
                @Override
                protected void onPostExecute(Void aVoid){}

            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        while(!finished)
            Thread.sleep(100);
        return cover;
    }//////////////////////////
    public Bitmap getTitleCover (String t_name) throws Exception {
        if(getTitle(t_name).getCover()==null)
        {
            Bitmap b=downloadImage(t_name);
            titles.get(find_index_title(t_name)).setCover(b);
            return b;
        }
        return getTitle(t_name).getCover();
    }//////////////////////////////

}

