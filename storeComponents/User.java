package storeComponents;

/**
 * Created by AyeletHaShachar on 09/11/2015.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {

    /**
     * attributes + their getters & setters
     */
    private String id;
    private String name;
    private String password;
    private String phone;
    private String address;
    private String email;
    private Enums.GENDER gender;
    private Date birthdate;
    private Enums.PERMISSION permission;

    public User(String id, String name, String pass, String ph, String addr, String em, Enums.GENDER gen, String birth, Enums.PERMISSION per) throws  Exception{
        setId(id);
        setName(name);
        setPassword(pass);
        setPhone(ph);
        setAddress(addr);
        setEmail(em);
        setGender(gen);
        setBirthdate(birth);
        setPermission(per);
    }
    public User(){}

    public String getId() {
        return id;
    }
    private void setId(String id)throws Exception{
        if(id.length()<3)
            throw new Exception("username too short. must be at least 3 characters");
        if(id.length()>10)
            throw new Exception("username too long. must be max 10 characters");
        this.id = id;
    }


    public String getName() {
        return name;
    }
    private void setName(String name) throws Exception{
        if(name.length()<3)
            throw new Exception("name too short. must be at least 3 characters");
        if(name.length()>20)
            throw new Exception("name too long. must be max 10 characters");
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    private void setPassword(String password)throws Exception{
        if(password.length()<3)
            throw new Exception("password too short. must be at least 3 characters");
        if(password.length()>15)
            throw new Exception("password too long. must be max 15 characters");
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }
    private void setPhone(String phone)throws Exception{
        if(phone.length()>10)
            throw new Exception("phone number too long. must be max 10 characters");
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    private void setAddress(String address)throws Exception{
        if(address.length()<3)
            throw new Exception("address too short. must be at least 3 characters");
        if(id.length()>50)
            throw new Exception("address too long. must be max 50 characters");
        this.address = address;
    }

    public String getEmail() {
        return email;
    }
    private void setEmail(String email) throws Exception {
        if (!email.contains("@"))
            throw new Exception("that string can not be an email address!");
        this.email = email;
    }

    public Enums.GENDER getGender() {
        return gender;
    }
    private void setGender(Enums.GENDER gender) {
        this.gender = gender;
    }

    public Date getBirthdate() throws ParseException {
       return birthdate;
    }
    private void setBirthdate(String bday) throws Exception{
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        String newStringDate = myFormat.format(fromUser.parse(bday));
        Date tdate = myFormat.parse(newStringDate);
        Calendar current = new GregorianCalendar();// defining current date
        Calendar bdate = Calendar.getInstance(Locale.US);//parse birthdate into Calendar for comparing
        bdate.setTime(tdate);
        current.set(Calendar.DATE, 0);//initializing current to today date
        if (current.get(Calendar.YEAR) - 16 < bdate.get(Calendar.YEAR))
            throw new Exception("user too young");
        if (bdate.get(Calendar.YEAR) + 120 < current.get(Calendar.YEAR))
            throw new Exception("an user can not be older then 120 years old!");
        this.birthdate = tdate;
    }

    public Enums.PERMISSION getPermission() {
        return permission;
    }
    private void setPermission(Enums.PERMISSION permission) {
        this.permission = permission;
    }



    /**
     * toString
     * @return a string which contains all the details of the user
     */
    public String toString() {
        return  "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", birthdate=" + birthdate +
                ", permission=" + permission;
    }

    /**
     * our method - which allows user to edit his own details.
     * can't edit id, birthdate, gender and permission
     */
    public void edit_details(User u) throws Exception{
        setName(u.getName());
        setPassword(u.getPassword());
        setPhone(u.getPhone());
        setAddress(u.getAddress());
        setEmail(u.getEmail());
    }

}

