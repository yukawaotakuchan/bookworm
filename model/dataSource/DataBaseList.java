package model.dataSource;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.backend.Backend;
import storeComponents.Client;
import storeComponents.Copies;
import storeComponents.Enums;
import storeComponents.Purchase;
import storeComponents.Title;
import storeComponents.User;
import storeComponents.Vendor;

/**
 * Created by Shira on 02/12/2015.
 */
public class DataBaseList implements Backend {
    public User admin;
    public List<Client> clients;
    public List<Vendor> vendors;
    public List<Title> titles;

    public DataBaseList() throws Exception
    {
        admin= new User("1111", "admin", "0000", "00-0000000", "AAA", "a@store.jct.ac.il", Enums.GENDER.FEMALE, "01/01/1990", Enums.PERMISSION.ADMIN);
        clients=new LinkedList<Client>();
        vendors=new LinkedList<Vendor>();
        titles=new LinkedList<Title>();
    }


    public boolean isVendor(String id) {return (find_index_ven(id)!=-1);}
    public boolean isClient(String id) {return (find_index_client(id)!=-1);}
    public boolean client_mail_valid(String id, String email){return email.equals(getClient(id).getEmail());}
    public boolean vendor_mail_valid(String id, String email){return email.equals(getVendor(id).getEmail());}


    public void add_vendor(Vendor v) throws Exception//adds vendor to the list
    {
        if (find_index_ven(v.getId()) == -1)
            vendors.add(v);
        else
            throw new Exception("vendor already exist");
    }
    public void add_client(Client c) throws Exception
    {
        if (find_index_client(c.getId()) == -1)
            clients.add(clients.size(), c);
        else
            throw new Exception("client already exist");
    }
    public void add_title(Title t) throws Exception
    {
        if (find_index_title(t.getTitle_name()) == -1)
            titles.add(t);
        else
            throw new Exception("title already exist");
    }
    public void add_title_to_vendor (String ven_id, Title t, int amount, double price) throws Exception
    {
        int v_index = find_index_ven(ven_id);
        int t_index = find_index_title(t.getTitle_name());
        if(v_index != -1) //if the vendor exists
        {
            if(t_index == -1) //if the wanted title doesn't exist in the store
                add_title(t);

            vendors.get(v_index).add_copies_to_stock(t, amount, price);
        }
        else
            throw new Exception("vendor not exists!");
    }

    public void update_ven_list(Vendor v /*, Enums.PERMISSION p*/) throws Exception//only a vendor is allowed to edit his own details
    {
        if (find_index_ven(v.getId()) != -1)
            vendors.get(find_index_ven(v.getId())).edit_details(v);
        else
            throw new Exception("this vendor is not exist, OR that user is not a vendor");
    }
    public void update_clients_list (Client c) throws Exception
    {
        if (find_index_client(c.getId()) != -1)
            clients.get(find_index_client(c.getId())).edit_details(c);
        else
            throw new Exception("this client is not exist, OR that user is not a client");
    }
    public void update_titles_list (Title t) throws Exception
    {
        if (find_index_title(t.getTitle_name()) != -1)
            titles.get(find_index_ven(t.getTitle_name())).edit_details(t);
        else
            throw new Exception("sorry, this title isn't exist in our store");
    }
    public void update_copy_price (String ven_id, String title_name, double price) throws Exception
    {
        int index_ven=find_index_ven(ven_id);
        if (index_ven != -1)
            vendors.get(index_ven).edit_copy_price(title_name, price);
        else
            throw new Exception("vendor does not exist");
    }

    public void delete_ven (String ven_id) throws Exception
    {
        if (find_index_ven(ven_id) != -1)
            vendors.remove(find_index_ven(ven_id));
        else
            throw new Exception("this vendor doesn't exist");
    }
    public void delete_client (String client_id) throws Exception
    {
        if(find_index_client(client_id)!= -1)
            clients.remove(find_index_client(client_id));
        else
            throw new Exception("this client doesn't exist");

    }
    public void delete_title (String title_name) throws Exception
    {
        if (find_index_title(title_name)!= -1)
            clients.remove(find_index_title(title_name));
        else
            throw new Exception("this title doesn't exist");
    }

    public int find_index_ven (String ven_id)
    {
        for (int i = 0; i < vendors.size(); i++)
            if(vendors.get(i).getId().equals(ven_id))
                return i;
        return -1;
    }
    public int find_index_client (String client_id)
    {
        for (int i = 0; i < clients.size(); i++)
            if(clients.get(i).getId().equals(client_id))
                return i;
        return -1;
    }
    public int find_index_title (String title_name)
    {
        for (int i = 0; i < titles.size(); i++)
            if(titles.get(i).getTitle_name().equals(title_name))
                return i;
        return -1;
    }

    public List<Client> get_clients(){return clients;}
    public List<Vendor> get_vendors(){return vendors;}
    public List<Title> get_titles(){return titles;}

    public void authentication_vendor (String ven_id)
    {
        vendors.get(find_index_ven(ven_id)).setAdmin_authen(!vendors.get(find_index_ven(ven_id)).getAdmin_authen());
    }

    public List<Copies> getVendorStock(String ven_id)
    {
        int vendorIndex=find_index_ven(ven_id);
        if(vendorIndex!=-1)
            return vendors.get(vendorIndex).getStock();
        return null;
    }
    public ArrayList<Copies> find_avilable_copies ()
    {
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (int i = 0; i < vendors.size(); i++)
            if (vendors.get(i).getAdmin_authen())
            {
                for(int j=0; j<vendors.get(i).getStock().size(); j++)
                    if (vendors.get(i).getStock().get(j).getAmount() > 0)
                        result.add(vendors.get(i).getStock().get(j));
            }
        return result;
    }
    public ArrayList<Copies> find_titles_copies (String t_name)
    {
        ArrayList<Copies> result = new ArrayList<Copies>();
        if(t_name.equals("") || t_name.equals("search title"))
            return find_avilable_copies();
        for (Copies copy : find_avilable_copies()) {
            boolean b=copy.getTitle().getTitle_name().contains(t_name);
            if (b)
                result.add(copy);
        }
        return result;
    }
    public ArrayList<Copies> find_copies_within_range (int min, int max, List<Copies> l)
    {
        if (l.size()==0)
            l = find_avilable_copies();
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (Copies copy : l)
            if (copy.getInitial_price()>=min && copy.getInitial_price()<=max)
                result.add(copy);
        return result;
    }
    public ArrayList<Copies> find_copies_of_genere (String genere, List<Copies> l)
    {
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (Copies copy : l)
            if (copy.getTitle().getGenre1().toString()==genere || copy.getTitle().getGenre2().toString()==genere )
                result.add(copy);
        return result;
    }
    public ArrayList<Copies> find_copies_of_category (String category, List<Copies> l)
    {
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (Copies copy : l)
            if (copy.getTitle().getCategory().toString()==category )
                result.add(copy);
        return result;
    }
    public ArrayList<Copies> find_copies_by_author (String author, List<Copies> l)
    {
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (Copies copy : l)
            if (!copy.getTitle().getAuthor().equals(author))
                result.add(copy);
        return result;
    }
    public ArrayList<Copies> find_copies_by_vendor (String vendor, List<Copies> l) {
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (Copies copy : l)
            if (copy.getVen_id().equals(vendor))
                result.add(copy);
        return result;
    }

    public ArrayList<Copies> getSearchCopies(List<Copies> l)
    {
        ArrayList<Copies> result = new ArrayList<>();
        result.addAll(l);
        return result;
    }

    public void execute_purchase (Purchase p) throws Exception//at this function we already verified that the wanted book exists in the store, and the wanted vendor already chosen.
    {
        vendors.get(find_index_ven(p.getVendor_id())).add_sell(p);
        clients.get(find_index_client(p.getClient_id())).add_new_purchase(p);
    }
    public boolean checkImpossibilityOfPurchase(Purchase p)
    {
        int v_index = find_index_ven(p.getVendor_id());
        int t_index_in_v_stock = vendors.get(v_index).find_title(p.getBooks().getTitle().getTitle_name());
        return (vendors.get(v_index).getStock().get(t_index_in_v_stock).getAmount() < p.getCopies_amount());
    }

    public boolean client_is_valid (String id, String pass)
    {
        if(find_index_client(id) == -1)
            return false;

        Client c = clients.get(find_index_client(id));

        if(!c.getPassword().equals(pass))
            return false;

        return true;
    }
    public boolean vendor_is_valid (String id, String pass)
    {
        if(find_index_ven(id) == -1)
            return false;

        Vendor v = vendors.get(find_index_ven(id));

        if(!v.getPassword().equals(pass))
            return false;

        return true;
    }

    public User returnAdmin() {return admin;}

    public void fill()throws Exception
    {
        Client c1 = new Client("shiri", "shira", "123", "0001111", "aaa", "a@com", Enums.GENDER.FEMALE, "15/08/1993");
        add_client(c1);
        Client c2 = new Client("lulushka", "ayelet", "456", "0001111", "aaa", "a@com", Enums.GENDER.FEMALE, "15/08/1993");
        add_client(c2);
        Vendor v = new Vendor("booking", "moshe", "000", "5555555", "qqq", "booking@com", Enums.GENDER.MALE, "01/01/1990", "bPay@com");
        add_vendor(v);
        Title t1 = new Title("Harry Potter", "J K Rolling", 1990, Enums.GENRES.FANTASY, Enums.GENRES.NONE, "opus", Enums.CATEGORY.KIDS, 560, 89);
        Title t2 = new Title("Ella Enchanted", "Gail Karson Levign", 1991, Enums.GENRES.FANTASY, Enums.GENRES.ROMANCE, "marganit", Enums.CATEGORY.KIDS, 300, 70);
        Title t3 = new Title("The Alchemist", "Paulo Cohelo", 1990, Enums.GENRES.PHILOSOPHY, Enums.GENRES.NONE,"Don'tKnow" ,Enums.CATEGORY.ADULTS, 150, 60);
        authentication_vendor("booking");
        add_copies_to_vendor("booking", t1, 5, 90);
        add_title(t2);
        add_copies_to_vendor("booking", t3, 10, 50);
        Purchase p = new Purchase(c2.getId(), v.getId(), new Copies(t1, 10, 90, v.getId()), new Date().toString(), 100, 1);
        c2.addToCart(p);
        execute_purchase(p);
    }

    public void add_copies_to_vendor (String ven_id, Title t, int amount, double price) throws Exception
    {
        int v_index = find_index_ven(ven_id);
        int t_index = find_index_title(t.getTitle_name());
        if(v_index != -1) //if the vendor exists
        {
            if(t_index == -1) //if the wanted title doesn't exist in the store
                add_title(t);

            vendors.get(v_index).add_copies_to_stock(t, amount, price);
        }
        else
            throw new Exception("vendor not exists!");
    }
    public void delete_copies_from_vendor (String ven_id, Title t, int amount) throws Exception
    {
        int v_index = find_index_ven(ven_id);
        int t_index = find_index_title(t.getTitle_name());
        if(v_index != -1) //if the vendor exists
        {
            if(t_index == -1) //if the wanted title doesn't exist in the store
                throw new Exception("the required title doesn't exist in stock");

            vendors.get(v_index).delete_copies_from_stock(t, amount);
        }
        else
            throw new Exception("vendor not exists!");
    }

    public Vendor getVendor(String ven_id)
    {
        for(Vendor v :vendors)
            if(v.getId().equals(ven_id))
                return v;
        return null;
    }
    public Client getClient(String c_id)
    {
        for(Client c :clients)
            if(c.getId().equals(c_id))
                return c;
        return null;
    }
    public Copies getCopy (String t_name, String v_id)
    {
        int ven_index=find_index_ven(v_id);
        for(Copies c : vendors.get(ven_index).getStock())
            if(c.getTitle().getTitle_name().equals(t_name))
                return c;
        return null;
    }
    public Title getTitle(String t_name)
    {
        for(Title t :titles)
            if(t.getTitle_name().equals(t_name))
                return t;
        return null;
    }

    public List<Purchase> get_vendor_purchases(String ven_id)
    {
        for(Vendor v :vendors)
            if(v.getId().equals(ven_id))
                return v.getPurchs();
        return null;
    }
    public List<Purchase> get_client_purchases(String client_id)
    {
        for(Client c :clients)
            if(c.getId().equals(client_id))
                return c.getPrev_purchs();
        return null;
    }

    public void addToCart(String c_id, boolean op, Purchase p)throws Exception//0 for remove from cart, 1 for add to cart
    {
        int index= find_index_client(c_id);
        if(index==-1)
            throw new Exception("client doesn't exist");
        if(op)
            clients.get(index).addToCart(p);
        else
            clients.get(index).removeFromCart(p);
    }

    public Bitmap getTitleCover (String t_name) throws Exception
    {
        return titles.get(find_index_title(t_name)).getCover();
    }
}
