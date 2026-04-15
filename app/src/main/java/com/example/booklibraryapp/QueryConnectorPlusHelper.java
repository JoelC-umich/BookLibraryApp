package com.example.booklibraryapp;

import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class QueryConnectorPlusHelper {

    protected static String database = "defaultdb";
    protected static String ip = "libraryapp-library-app.b.aivencloud.com";
    protected static String port = "10606";
    protected static String username = "avnadmin";
    /* AES encrypted password */
    private static final String ENCRYPTED_PASSWORD_B64 = "QVZOU19WcWFTcGNTTWNxWjItLTY3R1lJ";
    private static final String SECRET_SALT = "AppInternalSalt_2024";

    public static String IDWhenLoggingIn;

    private static String getDecryptedPassword() {
        try {
            byte[] key = SECRET_SALT.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);

            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            byte[] encryptedBytes = Base64.decode(ENCRYPTED_PASSWORD_B64, Base64.DEFAULT);

            return new String(encryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("SECURITY_ERROR", "Could not decrypt DB password");
            return null;
        }
    }

    public static java.sql.Connection Connector() {
        java.sql.Connection myConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            // Decrypt during connection
            String decryptedPass = getDecryptedPassword();

            myConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":" + port + "/" + database,
                    username,
                    decryptedPass
            );

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
                if (connection != null && connection.isClosed() == false) {
                    connection.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void runQuery(String query) {
        runQuery(query, null);
    }

    public static void runQuery(String query, Runnable onComplete) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection connection = Connector();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                    statement.close(); //MUST CLOSE IN ORDER FOR APP TO RUN
                    connection.close();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                } else {
                    Log.d("EXECUTED_QUERY_FAILED", "Executed query failed due to Connector");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                executorService.shutdown();
            }
        });
    }

    public static String getLastIDPlus1UsersQuery() {
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

    public static String getLastIDPlus1BooksQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> maxIDIncoming = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT MAX(ID) + 1 FROM BOOKS");
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

    public static List<String> getRoomIDsQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFutureRoomIDs = executorService.submit(() -> {
            List<String> roomIDList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM ROOMS");
            while (setResult.next()) {
                roomIDList.add(setResult.getString("ID"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return roomIDList;
        });
        try {
            return listFutureRoomIDs.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getBookNamesQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFutureBookNames = executorService.submit(() -> {
            List<String> bookNameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME, BOOK_AUTHOR FROM BOOKS");
            while (setResult.next()) {
                bookNameList.add(setResult.getString("BOOK_NAME") + " by " + setResult.getString("BOOK_AUTHOR"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookNameList;
        });
        try {
            return listFutureBookNames.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getReservedBookNamesFromUserQuery(String UserID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFutureReservedBookNames = executorService.submit(() -> {
            List<String> reservedBookNameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("select BOOK_NAME from BOOKS INNER JOIN BOOKS_BORROWED ON BOOKS.ID = BOOKS_BORROWED.BOOK_ID INNER JOIN USERS ON USERS.ID = BOOKS_BORROWED.USER_ID WHERE USER_ID = '"+UserID+"' AND BOOKS_BORROWED.RESERVE_STATUS = 'Reserved'");
            while (setResult.next()) {
                reservedBookNameList.add(setResult.getString("BOOK_NAME"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return reservedBookNameList;
        });
        try {
            return listFutureReservedBookNames.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getBookNamesReservedPendingPlusStatusFromUserQuery(String UserID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFutureReservedBookNames = executorService.submit(() -> {
            List<String> reservedBookNameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("select BOOKS.BOOK_NAME, BOOKS_BORROWED.RESERVE_STATUS from BOOKS INNER JOIN BOOKS_BORROWED ON BOOKS.ID = BOOKS_BORROWED.BOOK_ID INNER JOIN USERS ON USERS.ID = BOOKS_BORROWED.USER_ID WHERE USER_ID = '"+UserID+"' AND (BOOKS_BORROWED.RESERVE_STATUS = 'Reserved' OR BOOKS_BORROWED.RESERVE_STATUS = 'Pending')");
            while (setResult.next()) {
                reservedBookNameList.add((setResult.getString("BOOK_NAME") + " (" + setResult.getString("RESERVE_STATUS") + ")"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return reservedBookNameList;
        });
        try {
            return listFutureReservedBookNames.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getBorrowedIDsFromUserQuery(String UserID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFutureReservedIDs = executorService.submit(() ->
        {
            List<String> reservedIDList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("select ID from BOOKS_BORROWED WHERE USER_ID = '"+UserID+"'");
            while (setResult.next())
            {
                reservedIDList.add(setResult.getString("ID"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return reservedIDList;
        });
        try {
            return listFutureReservedIDs.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getBorrowedBooksDetailedQuery(String UserID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executorService.submit(() -> {
            List<String> detailsList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery(
                    "SELECT BOOKS_BORROWED.ID, BOOKS_BORROWED.BOOK_ID, BOOKS.BOOK_NAME, BOOKS_BORROWED.RESERVE_STATUS, BOOKS_BORROWED.DATE_BORROWED " +
                            "FROM BOOKS_BORROWED " +
                            "INNER JOIN BOOKS ON BOOKS_BORROWED.BOOK_ID = BOOKS.ID " +
                            "WHERE BOOKS_BORROWED.USER_ID = '" + UserID + "' AND (BOOKS_BORROWED.RESERVE_STATUS = 'Reserved' OR BOOKS_BORROWED.RESERVE_STATUS = 'Pending')"
            );
            while (setResult.next()) {
                detailsList.add(setResult.getString("ID") + ";;;" +
                        setResult.getString("BOOK_ID") + ";;;" +
                        setResult.getString("BOOK_NAME") + ";;;" +
                        setResult.getString("RESERVE_STATUS") + ";;;" +
                        setResult.getString("DATE_BORROWED"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return detailsList;
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getPendingBooksReservedQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuturePendingBooks = executorService.submit(() -> {
            List<String> pendingBooksList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM BOOKS_BORROWED WHERE RESERVE_STATUS = 'Pending'");
            while (setResult.next()) {
                pendingBooksList.add(setResult.getString("ID"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return pendingBooksList;
        });
        try {
            return listFuturePendingBooks.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getPendingBooksReservedWithDetailsQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuturePendingBooks = executorService.submit(() -> {
            List<String> pendingBooksList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery(
                    "SELECT BOOKS_BORROWED.ID, USERS.FIRST_NAME, USERS.LAST_NAME, BOOKS.BOOK_NAME " +
                            "FROM BOOKS_BORROWED " +
                            "INNER JOIN USERS ON BOOKS_BORROWED.USER_ID = USERS.ID " +
                            "INNER JOIN BOOKS ON BOOKS_BORROWED.BOOK_ID = BOOKS.ID " +
                            "WHERE BOOKS_BORROWED.RESERVE_STATUS = 'Pending'");
            while (setResult.next()) {
                String details = "Request #" + setResult.getString("ID") + ": " +
                        setResult.getString("FIRST_NAME") + " " + setResult.getString("LAST_NAME") +
                        " - " + setResult.getString("BOOK_NAME");
                pendingBooksList.add(details);
            }
            setResult.close();
            statement.close();
            connection.close();
            return pendingBooksList;
        });
        try {
            return listFuturePendingBooks.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getPendingRoomsReservedQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuturePendingRooms = executorService.submit(() -> {
            List<String> pendingRoomsList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM ROOMS_RESERVED WHERE RESERVE_STATUS = 'Pending'");
            while (setResult.next()) {
                pendingRoomsList.add(setResult.getString("ID"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return pendingRoomsList;
        });
        try {
            return listFuturePendingRooms.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getPendingRoomsReservedWithDetailsQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuturePendingRooms = executorService.submit(() -> {
            List<String> pendingRoomsList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery(
                    "SELECT ROOMS_RESERVED.ID, USERS.FIRST_NAME, USERS.LAST_NAME, ROOMS_RESERVED.DATE, ROOMS_RESERVED.SLOT " +
                    "FROM ROOMS_RESERVED " +
                    "INNER JOIN USERS ON ROOMS_RESERVED.USER_ID = USERS.ID " +
                    "WHERE ROOMS_RESERVED.RESERVE_STATUS = 'Pending'");
            while (setResult.next()) {
                int slot = setResult.getInt("SLOT");
                String details = "Request #" + setResult.getString("ID") + ": " +
                        setResult.getString("FIRST_NAME") + " " + setResult.getString("LAST_NAME") +
                        " | Date: " + setResult.getString("DATE") + " | Slot: " + slot + " (" + slotToTime(slot) + ")";
                pendingRoomsList.add(details);
            }
            setResult.close();
            statement.close();
            connection.close();
            return pendingRoomsList;
        });
        try {
            return listFuturePendingRooms.get();
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
            ResultSet setResult = statement.executeQuery("SELECT ID FROM USERS WHERE USER_NAME = '" + username + "'");
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

    public static String getFirstNameFromIDQuery(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureFirstName = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT FIRST_NAME FROM USERS WHERE ID = '" + ID + "'");
            setResult.next();
            String firstName = setResult.getString("FIRST_NAME");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return firstName;
        });
        try {
            return futureFirstName.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getLastNameFromIDQuery(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureLastName = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT LAST_NAME FROM USERS WHERE ID = '" + ID + "'");
            setResult.next();
            String LastName = setResult.getString("LAST_NAME");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return LastName;
        });
        try {
            return futureLastName.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getTypeFromIDQuery(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureUserType = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_TYPE FROM USERS WHERE ID = '" + ID + "'");
            setResult.next();
            String UserType = setResult.getString("USER_TYPE");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return UserType;
        });
        try {
            return futureUserType.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getEmailFromIDQuery(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureEmail = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_EMAIL FROM USERS WHERE ID = '" + ID + "'");
            setResult.next();
            String userEmail = setResult.getString("USER_EMAIL");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return userEmail;
        });
        try {
            return futureEmail.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getUsernameFromID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureUsername = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_NAME FROM USERS WHERE ID = " + ID);
            setResult.next();
            String username = setResult.getString("USER_NAME");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return username;
        });
        try {
            return futureUsername.get();
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
            ResultSet setResult = statement.executeQuery("SELECT USER_PASSWORD FROM USERS WHERE ID = " + ID);
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
            ResultSet setResult = statement.executeQuery("SELECT USER_TYPE FROM USERS WHERE ID = " + ID);
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

    public static String getSchoolFromIDQuery(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureSchool = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT SCHOOL FROM USERS WHERE ID = '" + ID + "'");
            setResult.next();
            String userSchool = setResult.getString("SCHOOL");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return userSchool;
        });
        try {
            return futureSchool.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getBookIDFromBookNameQuery(String bookName) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookNameID = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM BOOKS WHERE BOOK_NAME = '" + bookName + "'");
            setResult.next();
            String bookNameID = setResult.getString("ID");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookNameID;
        });
        try {
            return futureBookNameID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getBookTitleFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookTitle = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookName = setResult.getString("BOOK_NAME");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookName;
        });
        try {
            return futureBookTitle.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getAuthorFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookAuthor = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_AUTHOR FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookAuthor = setResult.getString("BOOK_AUTHOR");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookAuthor;
        });
        try {
            return futureBookAuthor.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getBookCategoryFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookCategory = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT CATEGORY FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookCategory = setResult.getString("CATEGORY");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookCategory;
        });
        try {
            return futureBookCategory.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getBookSummaryFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookSummary = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT SUMMARY FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookSummary = setResult.getString("SUMMARY");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookSummary;
        });
        try {
            return futureBookSummary.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getQuantityTotalFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookQuantityTotal = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT QUANTITY_TOTAL FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookQuantityTotal = setResult.getString("QUANTITY_TOTAL");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookQuantityTotal;
        });
        try {
            return futureBookQuantityTotal.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getQuantityBorrowedFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookQuantityBorrowed = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT QUANTITY_BORROWED FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookQuantityBorrowed = setResult.getString("QUANTITY_BORROWED");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookQuantityBorrowed;
        });
        try {
            return futureBookQuantityBorrowed.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getQuantityAvailableMinus1FromBookID(String BookID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookQuantityAvailableMinus1 = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT QUANTITY_AVAILABLE - 1 FROM BOOKS WHERE ID = '" + BookID + "'");
            setResult.next();
            String bookQuantityAvailable = setResult.getString("QUANTITY_AVAILABLE - 1");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookQuantityAvailable;
        });
        try {
            return futureBookQuantityAvailableMinus1.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getQuantityBorrowedPlus1FromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookQuantityBorrowedPlus1 = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT QUANTITY_BORROWED + 1 FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookQuantityBorrowedPlus1 = setResult.getString("QUANTITY_BORROWED + 1");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookQuantityBorrowedPlus1;
        });
        try {
            return futureBookQuantityBorrowedPlus1.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getQuantityAvailableFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookQuantityAvailable = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT QUANTITY_AVAILABLE FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookQuantityAvailable = setResult.getString("QUANTITY_AVAILABLE");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookQuantityAvailable;
        });
        try {
            return futureBookQuantityAvailable.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getBookImageFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookImage = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT IMAGE FROM BOOKS WHERE ID = '" + ID + "'");
            setResult.next();
            String bookImage = setResult.getString("IMAGE");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookImage;
        });
        try {
            return futureBookImage.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getRoomIDFromRoomReserveID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureRoomID = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ROOM_ID FROM ROOMS_RESERVED WHERE ID = '" + ID + "'");
            setResult.next();
            String roomID = setResult.getString("ROOM_ID");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return roomID;
        });
        try {
            return futureRoomID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getUserIDFromRoomReserveID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureUserID = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_ID FROM ROOMS_RESERVED WHERE ID = '" + ID + "'");
            setResult.next();
            String userID = setResult.getString("USER_ID");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return userID;
        });
        try {
            return futureUserID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getSlotFromRoomReserveID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureSlot = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT SLOT FROM ROOMS_RESERVED WHERE ID = '" + ID + "'");
            setResult.next();
            String slot = setResult.getString("SLOT");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return slot;
        });
        try {
            return futureSlot.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getDateFromRoomReserveID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureDate = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT DATE FROM ROOMS_RESERVED WHERE ID = '" + ID + "'");
            setResult.next();
            String reserveDate = setResult.getString("DATE");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return reserveDate;
        });
        try {
            return futureDate.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getRoomIDsReservedOnDateQuery(String date) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executorService.submit(() -> {
            List<String> reservedList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            // Returns ROOM_ID and SLOT for every reservation on the given date
            ResultSet setResult = statement.executeQuery("SELECT ROOM_ID, SLOT FROM ROOMS_RESERVED WHERE DATE = '" + date + "'" +
                    " AND RESERVE_STATUS != 'Canceled'");
            while (setResult.next()) {
                // Store as "ROOM_ID:SLOT" pairs so caller can parse both values
                reservedList.add(setResult.getString("ROOM_ID") + ":" + setResult.getString("SLOT"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return reservedList;
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static void insertRoomReservationQuery(String roomID, String userID, String date, String slot) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection connection = Connector();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(
                            "INSERT INTO ROOMS_RESERVED (ROOM_ID, USER_ID, RESERVE_STATUS, DATE, SLOT) VALUES ("
                                    + roomID + ", "
                                    + userID + ", "
                                    + "'Pending', "
                                    + "'" + date + "', "
                                    + slot + ")"
                    );
                    statement.close();
                    connection.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                executorService.shutdown();
            }
        });
    }

    public static List<String> getBorrowedBooksQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuture = executorService.submit(() -> {
            List<String> borrowedList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery(
                    "SELECT USERS.FIRST_NAME, USERS.LAST_NAME, BOOKS.BOOK_NAME " +
                            "FROM BOOKS_BORROWED " +
                            "INNER JOIN USERS ON BOOKS_BORROWED.USER_ID = USERS.ID " +
                            "INNER JOIN BOOKS ON BOOKS_BORROWED.BOOK_ID = BOOKS.ID " +
                            "WHERE BOOKS_BORROWED.RESERVE_STATUS = 'Reserved'");
            while (setResult.next()) {
                borrowedList.add(setResult.getString("FIRST_NAME") + " " +
                        setResult.getString("LAST_NAME") + " - " +
                        setResult.getString("BOOK_NAME"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return borrowedList;
        });
        try {
            return listFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getBorrowedBooksWithDetailsQuery() {
        return getBorrowedBooksQuery();
    }

    public static String getBookIDFromBorrowedBooksID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookID = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_ID FROM BOOKS_BORROWED WHERE ID = '" + ID + "'");
            setResult.next();
            String bookID = setResult.getString("BOOK_ID");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return bookID;
        });
        try {
            return futureBookID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getUserIDFromBorrowedBooksID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureUserID = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_ID FROM BOOKS_BORROWED WHERE ID = '" + ID + "'");
            setResult.next();
            String UserID = setResult.getString("USER_ID");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return UserID;
        });
        try {
            return futureUserID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getDateFromBorrowedBooksID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureDate = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT DATE_BORROWED FROM BOOKS_BORROWED WHERE ID = '" + ID + "'");
            setResult.next();
            String dateBorrowed = setResult.getString("DATE_BORROWED");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return dateBorrowed;
        });
        try {
            return futureDate.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getStatusFromBorrowedBooksID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureStatus = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT RESERVE_STATUS FROM BOOKS_BORROWED WHERE ID = '" + ID + "'");
            setResult.next();
            String reserveStatus = setResult.getString("RESERVE_STATUS");
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return reserveStatus;
        });
        try {
            return futureStatus.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getAvailableBooksQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFuture = executorService.submit(() -> {
            List<String> availableBooksList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            // Available books = QUANTITY_AVAILABLE > 0
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME, BOOK_AUTHOR FROM BOOKS WHERE QUANTITY_AVAILABLE > 0");
            while (setResult.next())
            {
                availableBooksList.add(setResult.getString("BOOK_NAME") + " by " + setResult.getString("BOOK_AUTHOR"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return availableBooksList;
        });
        try {
            return listFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static List<String> getReservedBooksFromUserID(String UserID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> futureReservedBooksFromUser = executorService.submit(() -> {
            List<String> reservedBooksUser = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_ID FROM BOOKS_BORROWED WHERE USER_ID = '" + UserID + "' AND (RESERVE_STATUS = 'Pending' OR RESERVE_STATUS = 'Reserved')");
            while (setResult.next())
            {
                reservedBooksUser.add(setResult.getString("BOOK_ID"));
            }
            setResult.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            statement.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            connection.close();//MUST CLOSE IN ORDER FOR APP TO RUN
            return reservedBooksUser;
        });
        try {
            return futureReservedBooksFromUser.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static String getLastIDPlus1BooksBorrowedQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> maxIDIncoming = executorService.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT MAX(ID) + 1 FROM BOOKS_BORROWED");
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

    public static void returnBook(String borrowedID, String bookID, Runnable onComplete) {
        runQuery("UPDATE BOOKS SET QUANTITY_AVAILABLE = QUANTITY_AVAILABLE + 1, QUANTITY_BORROWED = QUANTITY_BORROWED - 1 WHERE ID = '" + bookID + "'", () -> {
            runQuery("UPDATE BOOKS_BORROWED SET RESERVE_STATUS = 'Returned' WHERE ID = '" + borrowedID + "'", onComplete);
        });
    }

    public static String slotToTime(int slot) {
        switch (slot) {
            case 1: return "8:00 AM";
            case 2: return "10:00 AM";
            case 3: return "12:00 PM";
            case 4: return "2:00 PM";
            case 5: return "4:00 PM";
            default: return "Unknown";
        }
    }
}
