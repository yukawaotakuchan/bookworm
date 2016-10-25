package storeComponents;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by AyeletHaShachar on 09/11/2015.
 */
public class Purchase implements Serializable{

    private String client_id;
    public String vendor_id;
    private Copies books;
    private Date date;
    private double final_price;
    private int copies_amount;

    public Purchase(){}
    public Purchase(String c_id, String v_id, Copies b, String d, double p, int am) throws Exception{
        setClient_id(c_id);
        setVendor_id(v_id);
        setBooks(b);
        setDate(d);
        setFinal_price(p);
        setCopies_amount(am);
    }

    public String getClient_id() {
        return client_id;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getVendor_id() {
        return vendor_id;
    }
    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    //each purchase is allowed to have copies of only one title and from only one vendor
    public Copies getBooks() {
        return books;
    }
    public void setBooks(Copies b) {
        this.books = b;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(String d)throws Exception
    {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        String newStringDate = myFormat.format(fromUser.parse(d));
        this.date= myFormat.parse(newStringDate);
    }

    public double getFinal_price() {
        return final_price;
    }
    public void setFinal_price(double final_price){
        this.final_price = final_price;
    }

    public int getCopies_amount() {
        return copies_amount;
    }
    public void setCopies_amount(int copies_amount) throws Exception{
        if(copies_amount > 0)
            this.copies_amount = copies_amount;
        else
            throw new Exception("invalid amount");
    }

    @Override
    /**
     * toString - returns a string which contain all the attributes of a Purchase object
     */
    public String toString() {
        return "Purchase{" +
                "client_id='" + client_id + '\'' +
                ", vendor_id='" + vendor_id + '\'' +
                ", books=" + books +
                ", date=" + date +
                ", final_price=" + final_price +
                ", copies_amount=" + copies_amount +
                '}';
    }
}
