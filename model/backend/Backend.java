package model.backend;

import android.graphics.Bitmap;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import storeComponents.Client;
import storeComponents.Copies;
import storeComponents.Purchase;
import storeComponents.Title;
import storeComponents.User;
import storeComponents.Vendor;
/**
 * Created by Shira on 02/12/2015.
 */
public interface Backend{


    boolean isVendor(String id) throws Exception;
    boolean isClient(String id);
    boolean client_mail_valid(String id, String email);
    boolean vendor_mail_valid(String id, String email);

    public void add_vendor(Vendor v) throws Exception;//adds vendor to the list
    public void add_client(Client c) throws Exception;
    public void add_title(Title t) throws Exception;
    public void add_title_to_vendor (String ven_id, Title t, int amount, double price) throws Exception;
    public void add_copies_to_vendor (String ven_id, Title t, int amount, double price) throws Exception;

    public void update_ven_list(Vendor v /*, Enums.PERMISSION p*/) throws Exception;//only a vendor is allowed to edit his own details
    public void update_clients_list (Client c) throws Exception;
    public void update_titles_list (Title t) throws Exception;
    public void update_copy_price (String ven_id, String title_name, double price) throws Exception;

    public void delete_ven (String ven_id) throws Exception;
    public void delete_client (String client_id) throws Exception;
    public void delete_title (String title_name) throws Exception;
    public void delete_copies_from_vendor (String ven_id, Title t, int amount) throws Exception;

    public int find_index_ven (String ven_id);
    public int find_index_client (String client_id);
    public int find_index_title (String title_name);

    public List<Client> get_clients();
    public List<Vendor> get_vendors();
    public List<Title> get_titles();

    public void authentication_vendor (String ven_id);

    public List<Copies> getVendorStock(String ven_id); //return the vendor's stock
    public ArrayList<Copies> find_avilable_copies ();// return all the available copies in the store
    public ArrayList<Copies> find_titles_copies (String t_name); //filter copies by title name
    public ArrayList<Copies> find_copies_within_range (int min, int max, List<Copies> l); //filter copies by price range
    public ArrayList<Copies> find_copies_of_genere (String genere, List<Copies> l); //filter copies by genere
    public ArrayList<Copies> find_copies_of_category (String category, List<Copies> l); //filter copies by category
    public ArrayList<Copies> find_copies_by_author (String author, List<Copies> l);// filter copies by author
    public ArrayList<Copies> find_copies_by_vendor (String vendor, List<Copies> l);// filter copies by vendor
    public ArrayList<Copies> getSearchCopies(List<Copies> l);

    public void execute_purchase (Purchase p) throws Exception;//at this function we already verified that the wanted book exists in the store, and the wanted vendor already chosen.
    public boolean checkImpossibilityOfPurchase(Purchase p);
    public boolean client_is_valid (String id, String pass) throws IOException, JSONException;
    public boolean vendor_is_valid (String id, String pass);

    public void fill()throws Exception;
    public Vendor getVendor(String ven_id)throws  Exception;
    public Client getClient(String c_id)throws  Exception;
    public Copies getCopy (String t_name, String name)throws  Exception;
    public Title getTitle(String t_name) throws  Exception;

    public List<Purchase> get_vendor_purchases(String ven_id);
    public List<Purchase> get_client_purchases(String client_id);
    public void addToCart(String c_id, boolean op, Purchase p)throws Exception;//0 for remove from cart, 1 for add to cart

    public Bitmap getTitleCover (String t_name) throws Exception;
}
