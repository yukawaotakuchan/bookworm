package storeComponents;

/**
 * Created by AyeletHaShachar on 09/11/2015.
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Client extends User {
    /**
     * @param id
     * @param name
     * @param pass
     * @param ph
     * @param addr
     * @param em
     * @param gen
     * @param birth
     */

    private List<Purchase> prev_purchs;
    private double accumulative_payment;
    private int discount; // in percents
    private List<Purchase> adeddToCart;

    public Client(String id, String name, String pass, String ph, String addr, String em, Enums.GENDER gen, String birth) throws Exception {
        super(id, name, pass, ph, addr, em, gen, birth, Enums.PERMISSION.CLIENT);
        setAccumulative_payment(0);
        setDiscount(0);
        prev_purchs=new LinkedList<>();
        adeddToCart= new ArrayList<>();
    }
    public Client(String id, String name, String pass, String ph, String addr, String em, Enums.GENDER gen, String birth, Double accP,int dis) throws Exception {
        super(id, name, pass, ph, addr, em, gen, birth, Enums.PERMISSION.CLIENT);
        setAccumulative_payment(accP);
        setDiscount(dis);
        prev_purchs=new LinkedList<>();
        adeddToCart= new ArrayList<>();
    }

    public List<Purchase> getPrev_purchs() {
        return prev_purchs;
    }

    public double getAccumulative_payment() {
        return accumulative_payment;
    }
    public void setAccumulative_payment(double accumulative_payment) {
        this.accumulative_payment = accumulative_payment;
    }

    public int getDiscount() {
        return discount;
    }
    public void setDiscount(int discount) throws Exception {
        if (discount > 15)
            this.discount=15;
        else
            this.discount = discount;
    }

    //cart
    public List<Purchase> getAdeddToCart() {
        return adeddToCart;
    }
    public void addToCart(Purchase p) {adeddToCart.add(p);}
    public void removeFromCart (Purchase p) {adeddToCart.remove(p);}

    @Override
    public String toString() {
        return super.toString() + '\'' +
                ", prev_purchs=" + prev_purchs + '\'' +
                ", discount=" + discount;
    }
    public void add_new_purchase(Purchase p) throws Exception// client orders a book from a specific vendor
    {
        removeFromCart(p);
        prev_purchs.add(p);
        setAccumulative_payment(accumulative_payment + p.getFinal_price());
        setDiscount(discount + (int) p.getFinal_price() / 50); // every payment of 50 dollars - gives the client additional 1 percent discount, for next purchases.
    }

}
