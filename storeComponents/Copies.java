package storeComponents;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by AyeletHaShachar on 09/11/2015.
 */
public class Copies implements Serializable {

    private Title title;;
    private int amount;
    private double initial_price;
    private String ven_id;

    public Copies(Title title, int amount, double initial_price, String v_id) throws Exception{
        setTitle(title);
        setAmount(amount);
        setInitial_price(initial_price);
        setVen_id(v_id);
    }
    public Copies()
    {
        title=null;
    }

    public Title getTitle() {
        return title;
    }
    public void setTitle(Title title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) throws Exception{
        if (amount < 0)
            throw new Exception("invalid amount");
        this.amount = amount;
    }

    public double getInitial_price() {
        return initial_price;
    }
    public void setInitial_price(double initial_price) {
        this.initial_price = initial_price;
    }

    public String getVen_id() {
        return ven_id;
    }
    public void setVen_id(String ven_id) {
        this.ven_id = ven_id;
    }

    @Override
    public String toString() {
        return "Copies{" +
                "title=" + title.toString() + "\n" +
                ", amount=" + amount +
                ", initial_price=" + initial_price +
                '}';
    }

    protected void edit(Copies c) throws Exception{
        setAmount(amount);
        setInitial_price(initial_price);


    }
}
