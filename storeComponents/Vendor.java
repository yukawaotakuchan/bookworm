package storeComponents;

/**
 * Created by AyeletHaShachar on 09/11/2015.
 */

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Vendor extends User {
    /**
     * attributes + their getters & setters
     *
     * @return
     */
    public String getPaypal_account_mail() {
        return paypal_account_mail;
    }
    private void setPaypal_account_mail(String paypal_account_mail) throws Exception {
        if (!paypal_account_mail.contains("@"))
            throw new Exception("that string can not be a email address!");
        this.paypal_account_mail = paypal_account_mail;
    }
    private String paypal_account_mail;

    public List<Copies> getStock() {
        return stock;
    }
    private List<Copies> stock;

    public List<Purchase> getPurchs() {
        return purchs;
    }
    private List<Purchase> purchs;

    public Boolean getAdmin_authen() {
        return admin_authen;
    }
    /* no one can change this field, besides the store admin*/
    public void setAdmin_authen(Boolean admin_authen)
    {
        this.admin_authen = admin_authen;
    }
    private Boolean admin_authen;

    /**
     * constructor
     *
     * @param id
     * @param name
     * @param pass
     * @param ph
     * @param addr
     * @param em
     * @param gen
     * @param birth
     * @param paypal_account_mail
     * @throws Exception
     */
    public Vendor(String id, String name, String pass, String ph, String addr, String em, Enums.GENDER gen, String birth, String paypal_account_mail) throws Exception {
        super(id, name, pass, ph, addr, em, gen, birth, Enums.PERMISSION.VENDOR);
        setPaypal_account_mail(paypal_account_mail);
        stock= new LinkedList<>();
        purchs=new LinkedList<>();
        setAdmin_authen(false);
    }

    public Vendor(){}
    /**
     * toString
     */
    @Override
    public String toString() {
        return super.toString() + '\'' +
                ", paypal_account_mail='" + paypal_account_mail + '\'' +
                ", stock=" + stock + '\'' +
                //", rating=" + rating + '\'' +
                ", purchs=" + purchs + '\'' +
                ", admin_authen=" + admin_authen;
    }

    /**
     * does not allow to edit here purchs, id, gender, birthdate, admin_authen and stock
     * @param v
     * @throws Exception
     */
    /*protected void edit_details(Vendor v) throws Exception
    {
        super.edit_details((User) v);

        setPaypal_account_mail(v.getPaypal_account_mail());
    }*/

    public void add_copies_to_stock(Title t, int amount, double price) throws Exception // we assume that at this function - the title already exists it the store
    {
        int t_index = find_title(t.getTitle_name());
        if(t_index == -1)  //if this title is not in the vendor's stock yet
        {
            Copies new_book = new Copies(t, amount, price, getId());//, getName());
            stock.add(new_book);
        }
        else //if already exists copies of this title at the vendor's stock
            stock.get(t_index).setAmount(stock.get(t_index).getAmount()+ amount);
    }
    public void delete_copies_from_stock(Title t, int amount) throws Exception
    {
        if (amount < 0)
            throw new Exception("amount of copies must be at list 0");

        int t_index = find_title(t.getTitle_name());
        if(t_index == -1)  //if this title is not in the vendor's stock
            throw new Exception("no such a copy in the stock");
        else //if already exists copies of this title at the vendor's stock
        {
            int new_amount = stock.get(t_index).getAmount() - amount;
            if (new_amount <= 0)
                stock.get(t_index).setAmount(0);
            else
                stock.get(t_index).setAmount(new_amount);
        }

    }

    public void add_sell(Purchase p) throws Exception
    {
        int c_index = find_title(p.getBooks().getTitle().getTitle_name());
        if (stock.get(c_index).getAmount() < p.getCopies_amount())
            throw new Exception("vendor doesn't have enough copies of " + p.getBooks().getTitle().getTitle_name());
        purchs.add(p);
        delete_copies_from_stock(p.getBooks().getTitle(), p.getCopies_amount());
    }


    public int find_title(String title)
    {
        for(int i=0; i<stock.size(); i++)
            if(stock.get(i).getTitle().getTitle_name().equals(title))
                return i;
        return -1;
    }
    public void edit_copy_price(String title, double price)throws Exception
    {
        int t_index = find_title(title);
        if(t_index == -1)  //if this title is not in the vendor's stock
            throw new Exception("title doesn't exist in stock");
        else //if already exists copies of this title at the vendor's stock
            stock.get(t_index).setInitial_price(price);
    }
}