package storeComponents;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by AyeletHaShachar on 09/11/2015.
 */
public class Title {

    private String title_name;
    private String author;
    private int publishing_year;
    private Enums.GENRES genre1;
    private Enums.GENRES genre2;
    private String publishing_house;
    private Enums.CATEGORY category;
    private int length;
    private double recommended_price;
    public Bitmap cover;

    public Title(String title_name, String author,int publishing_year, Enums.GENRES genre1, Enums.GENRES genre2, String publishing_house, Enums.CATEGORY category, int length, double recommended_price, Bitmap cover) throws  Exception{
        setTitle_name(title_name);
        setAuthor(author);
        setPublishing_year(publishing_year);
        setGenre1(genre1);
        setGenre2(genre2);
        setPublishing_house(publishing_house);
        setCategory(category);
        setLength(length);
        setRecommended_price(recommended_price);
        setCover(cover);
    }
    public Title(String title_name, String author,int publishing_year, Enums.GENRES genre1, Enums.GENRES genre2, String publishing_house, Enums.CATEGORY category, int length, double recommended_price) throws  Exception{
        setTitle_name(title_name);
        setAuthor(author);
        setPublishing_year(publishing_year);
        setGenre1(genre1);
        setGenre2(genre2);
        setPublishing_house(publishing_house);
        setCategory(category);
        setLength(length);
        setRecommended_price(recommended_price);
        cover=null;
    }
    public Title(){}

    public String getTitle_name() {
        return title_name;
    }
    public void setTitle_name(String title_name)throws Exception
    {
        if(title_name.length()<3)
            throw new Exception("title_name too short. must be at least 3 characters");
        if(title_name.length()>45)
            throw new Exception("title name too long. must be max 45 characters");
        this.title_name = title_name;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author)throws Exception
    {
        if(author.length()<3)
            throw new Exception("author name too short. must be at least 3 characters");
        if(author.length()>20)
            throw new Exception("author too long. must be max 20 characters");
        this.author = author;
    }

    public int getPublishing_year() {
        return publishing_year;
    }
    public void setPublishing_year(int publishing_year) throws Exception {
        if(publishing_year<1000 || publishing_year> Calendar.getInstance().get(Calendar.YEAR))
            throw new Exception("invalid year");
        this.publishing_year = publishing_year;
    }

    public Enums.GENRES getGenre2() {
        return genre2;
    }
    public void setGenre2(Enums.GENRES genre2) {
        this.genre2 = genre2;
    }

    public Enums.GENRES getGenre1() {
        return genre1;
    }
    public void setGenre1(Enums.GENRES genre1) {
        this.genre1 = genre1;
    }

    public String getPublishing_house() {
        return publishing_house;
    }
    public void setPublishing_house(String publishing_house) {
        this.publishing_house = publishing_house;
    }

    public Enums.CATEGORY getCategory() {
        return category;
    }
    public void setCategory(Enums.CATEGORY category) {
        this.category = category;
    }

    public int getLength() {
        return length;
    }
    public void setLength(int length) throws Exception
    {
        if(length<1)
            throw new Exception("book too short. must be at least 1 digit");
        if(length>999)
            throw new Exception("book too long. must be max 3 digits");
        this.length = length;
    }

    public double getRecommended_price() {
        return recommended_price;
    }
    public void setRecommended_price(double recommended_price) throws Exception{
        if (recommended_price <= 0)
            throw new Exception("Error. price lower then 0!");
        this.recommended_price = recommended_price;
    }

    public Bitmap getCover() {
        return cover;
    }
    public void setCover(Bitmap cover) {
        this.cover = cover;
    }



    @Override
    public String toString() {
        return "Title{" +
                "title_name='" + title_name + '\'' +
                ", author='" + author + '\'' +
                ", publishing_year=" + publishing_year +
                ", genre2=" + genre2 +
                ", genre1=" + genre1 +
                ", publishing_house='" + publishing_house + '\'' +
                ", category=" + category +
                ", length=" + length +
                ", recommended_price=" + recommended_price +
                '}';
    }
    public void edit_details(Title t) throws Exception
    {
        /**
         * only the recommended price can be changed! all other details are constant!
         */
        setRecommended_price(t.getRecommended_price());
    }
}
