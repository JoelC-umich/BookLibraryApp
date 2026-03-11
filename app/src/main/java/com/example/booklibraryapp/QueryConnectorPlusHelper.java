package com.example.booklibraryapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class QueryConnectorPlusHelper {

    protected static String database = "library_schema";
    protected static String ip = "library-app.cpmmsuog2ibh.us-east-2.rds.amazonaws.com";
    protected static String port = "3306";
    protected static String username = "admin";
    protected static String password = "UxEePxbk1LgBzO9UDSJc";

    public static java.sql.Connection Connector() {
        java.sql.Connection myConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, username, password);

        } catch (Exception e) {
            Log.e("CONNECTION_ERROR", "Connector could not connect to database");
        }
        return myConnection;
    }

    public static void checkConnection() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String connectionStatus = null;
            Connection connection = null;
            try {
                connection = Connector();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                if (connection == null) {
                    connectionStatus = "Connection Failed";
                } else {
                    connectionStatus = "Connection Succeeded";
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (connectionStatus != null) {
                Log.i("CONNECTION_STATUS", connectionStatus);
            }

            try {
                if (connection.isClosed() == false) {
                    connection.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void insertQuery(String query) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection connection = Connector();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                    statement.close(); //MUST CLOSE IN ORDER FOR APP TO RUN
                    connection.close();
                } else {
                    Log.d("INSERT_QUERY_FAILED", "Insert query failed due to Connector");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                executorService.shutdown();
            }
        });
    }

    public static String getLastIDQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> maxIDIncoming = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT MAX(ID) + 1 FROM USERS");
            setResult.next();
            String maxID = setResult.getString("MAX(ID) + 1");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return maxID;
        });
        try {
            return maxIDIncoming.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getUsernamesQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuture = executorService.submit(() -> {
            List<String> usernameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_NAME FROM USERS");
            while (setResult.next()) {
                usernameList.add(setResult.getString("USER_NAME"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return usernameList;
        });
        try {
            return listFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getUsernameIDQuery(String username) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureUsernameID = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM USERS WHERE USER_NAME = '"+username+"'");
            setResult.next();
            String usernameID = setResult.getString("ID");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return usernameID;
        });
        try {
            return futureUsernameID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getPasswordFromID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futurePassword = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_PASSWORD FROM USERS WHERE ID = "+ID);
            setResult.next();
            String password = setResult.getString("USER_PASSWORD");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return password;
        });
        try {
            return futurePassword.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getUserTypeFromID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureUserType = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_TYPE FROM USERS WHERE ID = "+ID);
            setResult.next();
            String userType = setResult.getString("USER_TYPE");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return userType;
        });
        try {
            return futureUserType.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

}