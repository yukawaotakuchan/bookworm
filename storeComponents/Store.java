/*
package storeComponents;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

*/
/**
 * Created by AyeletHaShachar on 09/11/2015.
 *//*

public class Store  {

    //Scanner in = new Scanner(System.in);

    public User admin;
    public List<Client> clients;
    public List<Vendor> vendors;
    public List<Title> titles;

    public Store(*/
/**List<User> admin, List<Client> clients, List<Vendor> vendors, List<Title> titles*//*
) throws Exception
    {
        */
/** this.admin = admin;
         this.clients = clients;
         this.vendors = vendors;
         this.titles = titles; *//*

        admin= new User("1111", "admin", "0000", "00-0000000", "AAA", "a@store.jct.ac.il", Enums.GENDER.FEMALE, "01/01/1990", Enums.PERMISSION.ADMIN);
        clients=new LinkedList<Client>();
        vendors=new LinkedList<Vendor>();
        titles=new LinkedList<Title>();
    }

    public void add_vendor(Vendor v) throws Exception//adds vendor to the list
    {
        if (find_index_ven(v.getId()) == -1)
            vendors.add(v);
        else
            throw new Exception("vendor already exist");
    }

    public void add_client(Client c) throws Exception {
        if (find_index_client(c.getId()) == -1)
            clients.add(c);
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

            vendors.get(v_index).add_copies_to_stock(titles.get(t_index), amount, price);
        }
        else
            throw new Exception("vendor not exists!");
    }

    public void update_ven_list(Vendor v */
/*, Enums.PERMISSION p*//*
) throws Exception//only a vendor is allowed to edit his own details
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
            if(vendors.get(i).getId() == ven_id)
                return i;
        return -1;
    }
    public int find_index_client (String client_id)
    {
        for (int i = 0; i < clients.size(); i++)
            if(clients.get(i).getId() == client_id)
                return i;
        return -1;
    }
    public int find_index_title (String title_name)
    {
        for (int i = 0; i < titles.size(); i++)
            if(titles.get(i).getTitle_name() == title_name)
                return i;
        return -1;
    }

    public void authentication_vendor (String ven_id)
    {
        vendors.get(find_index_ven(ven_id)).setAdmin_authen(!vendors.get(find_index_ven(ven_id)).getAdmin_authen());
    }

    public ArrayList<Copies> find_title_vendors_copies (String t_name)
    {
        ArrayList<Copies> result = new ArrayList<Copies>();
        for (int i = 0; i < vendors.size(); i++)
            if (vendors.get(i).getAdmin_authen())
            {
                int c_index = vendors.get(i).find_title(t_name);
                if (c_index != -1 && vendors.get(i).getStock().get(c_index).getAmount() > 0)
                    result.add(vendors.get(i).getStock().get(c_index));
            }

        return result;
    }

    public void execute_purchase (Copies c, String c_id, int amount) throws Exception//at this function we already verified that the wanted book exists in the store, and the wanted vendor already chosen.
    {
        int credit= clients.get(find_index_client(c_id)).getDiscount();
        double final_price = c.getInitial_price() * (1 - credit/100) * amount;
        DateFormat dateFormat = new SimpleDateFormat("dd/mm,yyyy HH:mm:ss");
        Date date = new Date();
        Purchase new_p = new Purchase(c_id, c.getVen_id(), c, date , final_price, amount);

        vendors.get(find_index_ven(c.getVen_id())).add_sell(new_p);

        clients.get(find_index_client(c_id)).add_new_purchase(new_p);
    }

    public boolean client_is_valid (String id, String pass)
    {
        Client c = clients.get(find_index_client(id));
        if(c == null || !c.getPassword().equals(pass))
            return false;
        return true;
    }
    public boolean vendor_is_valid (String id, String pass)
    {
        Vendor v = vendors.get(find_index_ven(id));
        if (v == null || !v.getPassword().equals(pass))
            return false;
        return true;
    }

}
*/
