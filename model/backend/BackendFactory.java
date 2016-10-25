package model.backend;


import model.backend.Backend;
import model.dataSource.DataBaseList;
import model.dataSource.DataBaseSQL;

/**
 * Created by קרומביין on 23/11/2015.
 */
public class BackendFactory {
    static Backend instance = null;

    public static String mode = "mysql";

    public final static Backend getInstance(/*Context context*/)
    {
        if (mode == "lists")
        {
            if (instance == null) {
                try {
                    instance = new DataBaseList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return instance;
        }
        if (mode == "mysql")
        {
            if (instance == null)
            {
                try {
                    instance = new DataBaseSQL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return instance;
        }
        else return null;
    }
}