package com.example.booklibraryapp;

import android.util.Log;
import java.sql.DriverManager;
import java.util.Objects;

public class DatabaseConnectorClass
{
    protected static String database = "library_schema";
    protected static String ip = "library-app.cpmmsuog2ibh.us-east-2.rds.amazonaws.com";
    protected static String port = "3306";
    protected static String username = "admin";
    protected static String password = "UxEePxbk1LgBzO9UDSJc";

    public java.sql.Connection Connector() {
        java.sql.Connection myConnection = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+database, username, password);

        }catch (Exception e){
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
        return myConnection;
    }
}