package com.example.booklibraryapp;

import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    
    // Shared executor for all background tasks. Can be overridden in tests.
    public static ExecutorService executor = Executors.newCachedThreadPool();

    public static void setExecutor(ExecutorService newExecutor) {
        executor = newExecutor;
    }

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
        executor.execute(() -> {
            String connectionStatus = null;
            Connection connection = null;
            try {
                connection = Connector();
                if (connection == null) {
                    connectionStatus = "Connection Failed";
                } else {
                    connectionStatus = "Connection Succeeded";
                    connection.close();
                }
            } catch (Exception e) {
                Log.e("CONNECTION_ERROR", "Error checking connection", e);
            }

            if (connectionStatus != null) {
                Log.i("CONNECTION_STATUS", connectionStatus);
            }
        });
    }

    public static void runQuery(String query) {
        runQuery(query, null);
    }

    public static void runQuery(String query, Runnable onComplete) {
        executor.execute(() -> {
            try {
                Connection connection = Connector();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                    statement.close();
                    connection.close();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                } else {
                    Log.d("EXECUTED_QUERY_FAILED", "Executed query failed due to Connector");
                }
            } catch (Exception e) {
                Log.e("QUERY_ERROR", "Error running query: " + query, e);
            }
        });
    }

    public static String getLastIDPlus1UsersQuery() {
        Future<String> maxIDIncoming = executor.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT MAX(ID) + 1 FROM USERS");
            setResult.next();
            String maxID = setResult.getString("MAX(ID) + 1");
            setResult.close();
            statement.close();
            connection.close();
            return maxID;
        });
        try {
            return maxIDIncoming.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastIDPlus1BooksQuery() {
        Future<String> maxIDIncoming = executor.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT MAX(ID) + 1 FROM BOOKS");
            setResult.next();
            String maxID = setResult.getString("MAX(ID) + 1");
            setResult.close();
            statement.close();
            connection.close();
            return maxID;
        });
        try {
            return maxIDIncoming.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getUsernamesQuery() {
        Future<List<String>> listFuture = executor.submit(() -> {
            List<String> usernameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT USER_NAME FROM USERS");
            while (setResult.next()) {
                usernameList.add(setResult.getString("USER_NAME"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return usernameList;
        });
        try {
            return listFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getUsersFullNamesWithUsernamesQuery() {
        Future<List<String>> listFuture = executor.submit(() -> {
            List<String> userList = new ArrayList<>();
            Connection connection = Connector();
            if (connection == null) return userList;
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT FIRST_NAME, LAST_NAME, USER_NAME FROM USERS");
            while (setResult.next()) {
                userList.add(setResult.getString("FIRST_NAME") + " " + setResult.getString("LAST_NAME") + ";;;" + setResult.getString("USER_NAME"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return userList;
        });
        try {
            return listFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRoomIDsQuery() {
        Future<List<String>> listFutureRoomIDs = executor.submit(() -> {
            List<String> roomIDList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM ROOMS");
            while (setResult.next()) {
                roomIDList.add(setResult.getString("ID"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return roomIDList;
        });
        try {
            return listFutureRoomIDs.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getBookNamesQuery() {
        Future<List<String>> listFutureBookNames = executor.submit(() -> {
            List<String> bookNameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME, BOOK_AUTHOR FROM BOOKS");
            while (setResult.next()) {
                bookNameList.add(setResult.getString("BOOK_NAME") + " by " + setResult.getString("BOOK_AUTHOR"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return bookNameList;
        });
        try {
            return listFutureBookNames.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getReservedBookNamesFromUserQuery(String UserID) {
        Future<List<String>> listFutureReservedBookNames = executor.submit(() -> {
            List<String> reservedBookNameList = new ArrayList<>();
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("select BOOK_NAME from BOOKS INNER JOIN BOOKS_BORROWED ON BOOKS.ID = BOOKS_BORROWED.BOOK_ID INNER JOIN USERS ON USERS.ID = BOOKS_BORROWED.USER_ID WHERE USER_ID = ? AND BOOKS_BORROWED.RESERVE_STATUS = 'Reserved'");
            statement.setString(1, UserID);
            ResultSet setResult = statement.executeQuery();
            while (setResult.next()) {
                reservedBookNameList.add(setResult.getString("BOOK_NAME"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return reservedBookNameList;
        });
        try {
            return listFutureReservedBookNames.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getBookNamesReservedPendingPlusStatusFromUserQuery(String UserID) {
        Future<List<String>> listFutureReservedBookNames = executor.submit(() -> {
            List<String> reservedBookNameList = new ArrayList<>();
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("select BOOKS.BOOK_NAME, BOOKS_BORROWED.RESERVE_STATUS from BOOKS INNER JOIN BOOKS_BORROWED ON BOOKS.ID = BOOKS_BORROWED.BOOK_ID INNER JOIN USERS ON USERS.ID = BOOKS_BORROWED.USER_ID WHERE USER_ID = ? AND (BOOKS_BORROWED.RESERVE_STATUS = 'Reserved' OR BOOKS_BORROWED.RESERVE_STATUS = 'Pending')");
            statement.setString(1, UserID);
            ResultSet setResult = statement.executeQuery();
            while (setResult.next()) {
                reservedBookNameList.add((setResult.getString("BOOK_NAME") + " (" + setResult.getString("RESERVE_STATUS") + ")"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return reservedBookNameList;
        });
        try {
            return listFutureReservedBookNames.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getBorrowedIDsFromUserQuery(String UserID) {
        Future<List<String>> listFutureReservedIDs = executor.submit(() ->
        {
            List<String> reservedIDList = new ArrayList<>();
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("select ID from BOOKS_BORROWED WHERE USER_ID = ?");
            statement.setString(1, UserID);
            ResultSet setResult = statement.executeQuery();
            while (setResult.next())
            {
                reservedIDList.add(setResult.getString("ID"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return reservedIDList;
        });
        try {
            return listFutureReservedIDs.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getBorrowedBooksDetailedQuery(String UserID) {
        Future<List<String>> future = executor.submit(() -> {
            List<String> detailsList = new ArrayList<>();
            Connection connection = Connector();
            if (connection == null) return detailsList;
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT BOOKS_BORROWED.ID, BOOKS_BORROWED.BOOK_ID, BOOKS.BOOK_NAME, BOOKS_BORROWED.RESERVE_STATUS, BOOKS_BORROWED.DATE_BORROWED " +
                            "FROM BOOKS_BORROWED " +
                            "INNER JOIN BOOKS ON BOOKS_BORROWED.BOOK_ID = BOOKS.ID " +
                            "WHERE BOOKS_BORROWED.USER_ID = ? AND (BOOKS_BORROWED.RESERVE_STATUS = 'Reserved' OR BOOKS_BORROWED.RESERVE_STATUS = 'Pending')"
            );
            statement.setString(1, UserID);
            ResultSet setResult = statement.executeQuery();
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
        }
    }

    public static List<String> getPendingBooksReservedQuery() {
        Future<List<String>> listFuturePendingBooks = executor.submit(() -> {
            List<String> pendingBooksList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM BOOKS_BORROWED WHERE RESERVE_STATUS = 'Pending'");
            while (setResult.next()) {
                pendingBooksList.add(setResult.getString("ID"));
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
        }
    }

    public static List<String> getPendingBooksReservedWithDetailsQuery() {
        Future<List<String>> listFuturePendingBooks = executor.submit(() -> {
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
        }
    }

    public static List<String> getPendingRoomsReservedQuery() {
        Future<List<String>> listFuturePendingRooms = executor.submit(() -> {
            List<String> pendingRoomsList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT ID FROM ROOMS_RESERVED WHERE RESERVE_STATUS = 'Pending'");
            while (setResult.next()) {
                pendingRoomsList.add(setResult.getString("ID"));
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
        }
    }

    public static List<String> getPendingRoomsReservedWithDetailsQuery() {
        Future<List<String>> listFuturePendingRooms = executor.submit(() -> {
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
        }
    }

    public static String getUsernameIDQuery(String username) {
        Future<String> futureUsernameID = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT ID FROM USERS WHERE USER_NAME = ?");
            statement.setString(1, username);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String usernameID = setResult.getString("ID");
            setResult.close();
            statement.close();
            connection.close();
            return usernameID;
        });
        try {
            return futureUsernameID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFirstNameFromIDQuery(String ID) {
        Future<String> futureFirstName = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT FIRST_NAME FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String firstName = setResult.getString("FIRST_NAME");
            setResult.close();
            statement.close();
            connection.close();
            return firstName;
        });
        try {
            return futureFirstName.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastNameFromIDQuery(String ID) {
        Future<String> futureLastName = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT LAST_NAME FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String LastName = setResult.getString("LAST_NAME");
            setResult.close();
            statement.close();
            connection.close();
            return LastName;
        });
        try {
            return futureLastName.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTypeFromIDQuery(String ID) {
        Future<String> futureUserType = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_TYPE FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String UserType = setResult.getString("USER_TYPE");
            setResult.close();
            statement.close();
            connection.close();
            return UserType;
        });
        try {
            return futureUserType.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getEmailFromIDQuery(String ID) {
        Future<String> futureEmail = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_EMAIL FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String userEmail = setResult.getString("USER_EMAIL");
            setResult.close();
            statement.close();
            connection.close();
            return userEmail;
        });
        try {
            return futureEmail.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUsernameFromID(String ID) {
        Future<String> futureUsername = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_NAME FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String username = setResult.getString("USER_NAME");
            setResult.close();
            statement.close();
            connection.close();
            return username;
        });
        try {
            return futureUsername.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPasswordFromID(String ID) {
        Future<String> futurePassword = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_PASSWORD FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String password = setResult.getString("USER_PASSWORD");
            setResult.close();
            statement.close();
            connection.close();
            return password;
        });
        try {
            return futurePassword.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUserTypeFromID(String ID) {
        Future<String> futureUserType = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_TYPE FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String userType = setResult.getString("USER_TYPE");
            setResult.close();
            statement.close();
            connection.close();
            return userType;
        });
        try {
            return futureUserType.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSchoolFromIDQuery(String ID) {
        Future<String> futureSchool = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT SCHOOL FROM USERS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String userSchool = setResult.getString("SCHOOL");
            setResult.close();
            statement.close();
            connection.close();
            return userSchool;
        });
        try {
            return futureSchool.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBookIDFromBookNameQuery(String bookName) {
        Future<String> futureBookNameID = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT ID FROM BOOKS WHERE BOOK_NAME = ?");
            statement.setString(1, bookName);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookNameID = setResult.getString("ID");
            setResult.close();
            statement.close();
            connection.close();
            return bookNameID;
        });
        try {
            return futureBookNameID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBookTitleFromBookID(String ID) {
        Future<String> futureBookTitle = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT BOOK_NAME FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookName = setResult.getString("BOOK_NAME");
            setResult.close();
            statement.close();
            connection.close();
            return bookName;
        });
        try {
            return futureBookTitle.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAuthorFromBookID(String ID) {
        Future<String> futureBookAuthor = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT BOOK_AUTHOR FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookAuthor = setResult.getString("BOOK_AUTHOR");
            setResult.close();
            statement.close();
            connection.close();
            return bookAuthor;
        });
        try {
            return futureBookAuthor.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBookCategoryFromBookID(String ID) {
        Future<String> futureBookCategory = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT CATEGORY FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookCategory = setResult.getString("CATEGORY");
            setResult.close();
            statement.close();
            connection.close();
            return bookCategory;
        });
        try {
            return futureBookCategory.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBookSummaryFromBookID(String ID) {
        Future<String> futureBookSummary = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT SUMMARY FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookSummary = setResult.getString("SUMMARY");
            setResult.close();
            statement.close();
            connection.close();
            return bookSummary;
        });
        try {
            return futureBookSummary.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQuantityTotalFromBookID(String ID) {
        Future<String> futureBookQuantityTotal = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT QUANTITY_TOTAL FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookQuantityTotal = setResult.getString("QUANTITY_TOTAL");
            setResult.close();
            statement.close();
            connection.close();
            return bookQuantityTotal;
        });
        try {
            return futureBookQuantityTotal.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQuantityBorrowedFromBookID(String ID) {
        Future<String> futureBookQuantityBorrowed = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT QUANTITY_BORROWED FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookQuantityBorrowed = setResult.getString("QUANTITY_BORROWED");
            setResult.close();
            statement.close();
            connection.close();
            return bookQuantityBorrowed;
        });
        try {
            return futureBookQuantityBorrowed.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQuantityAvailableMinus1FromBookID(String BookID) {
        Future<String> futureBookQuantityAvailableMinus1 = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT QUANTITY_AVAILABLE - 1 FROM BOOKS WHERE ID = ?");
            statement.setString(1, BookID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookQuantityAvailable = setResult.getString("QUANTITY_AVAILABLE - 1");
            setResult.close();
            statement.close();
            connection.close();
            return bookQuantityAvailable;
        });
        try {
            return futureBookQuantityAvailableMinus1.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQuantityBorrowedPlus1FromBookID(String ID) {
        Future<String> futureBookQuantityBorrowedPlus1 = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT QUANTITY_BORROWED + 1 FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookQuantityBorrowedPlus1 = setResult.getString("QUANTITY_BORROWED + 1");
            setResult.close();
            statement.close();
            connection.close();
            return bookQuantityBorrowedPlus1;
        });
        try {
            return futureBookQuantityBorrowedPlus1.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQuantityAvailableFromBookID(String ID) {
        Future<String> futureBookQuantityAvailable = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT QUANTITY_AVAILABLE FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookQuantityAvailable = setResult.getString("QUANTITY_AVAILABLE");
            setResult.close();
            statement.close();
            connection.close();
            return bookQuantityAvailable;
        });
        try {
            return futureBookQuantityAvailable.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBookImageFromBookID(String ID) {
        Future<String> futureBookImage = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT IMAGE FROM BOOKS WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookImage = setResult.getString("IMAGE");
            setResult.close();
            statement.close();
            connection.close();
            return bookImage;
        });
        try {
            return futureBookImage.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRoomIDFromRoomReserveID(String ID) {
        Future<String> futureRoomID = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT ROOM_ID FROM ROOMS_RESERVED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String roomID = setResult.getString("ROOM_ID");
            setResult.close();
            statement.close();
            connection.close();
            return roomID;
        });
        try {
            return futureRoomID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUserIDFromRoomReserveID(String ID) {
        Future<String> futureUserID = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_ID FROM ROOMS_RESERVED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String userID = setResult.getString("USER_ID");
            setResult.close();
            statement.close();
            connection.close();
            return userID;
        });
        try {
            return futureUserID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSlotFromRoomReserveID(String ID) {
        Future<String> futureSlot = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT SLOT FROM ROOMS_RESERVED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String slot = setResult.getString("SLOT");
            setResult.close();
            statement.close();
            connection.close();
            return slot;
        });
        try {
            return futureSlot.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDateFromRoomReserveID(String ID) {
        Future<String> futureDate = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT DATE FROM ROOMS_RESERVED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String reserveDate = setResult.getString("DATE");
            setResult.close();
            statement.close();
            connection.close();
            return reserveDate;
        });
        try {
            return futureDate.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRoomIDsReservedOnDateQuery(String date) {
        Future<List<String>> future = executor.submit(() -> {
            List<String> reservedList = new ArrayList<>();
            Connection connection = Connector();
            if (connection == null) return reservedList;
            PreparedStatement statement = connection.prepareStatement("SELECT ROOM_ID, SLOT FROM ROOMS_RESERVED WHERE DATE = ? AND RESERVE_STATUS != 'Canceled'");
            statement.setString(1, date);
            // Returns ROOM_ID and SLOT for every reservation on the given date
            ResultSet setResult = statement.executeQuery();
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
        }
    }

    public static void insertRoomReservationQuery(String roomID, String userID, String date, String slot) {
        executor.execute(() -> {
            try {
                Connection connection = Connector();
                if (connection != null) {
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO ROOMS_RESERVED (ROOM_ID, USER_ID, RESERVE_STATUS, DATE, SLOT) VALUES (?, ?, 'Pending', ?, ?)"
                    );
                    statement.setString(1, roomID);
                    statement.setString(2, userID);
                    statement.setString(3, date);
                    statement.setString(4, slot);
                    statement.executeUpdate();
                    statement.close();
                    connection.close();
                }
            } catch (Exception e) {
                Log.e("QUERY_ERROR", "Error inserting room reservation", e);
            }
        });
    }

    public static List<String> getBorrowedBooksQuery() {
        Future<List<String>> listFuture = executor.submit(() -> {
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
        }
    }

    public static List<String> getBorrowedBooksWithDetailsQuery() {
        return getBorrowedBooksQuery();
    }

    public static String getBookIDFromBorrowedBooksID(String ID) {
        Future<String> futureBookID = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT BOOK_ID FROM BOOKS_BORROWED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String bookID = setResult.getString("BOOK_ID");
            setResult.close();
            statement.close();
            connection.close();
            return bookID;
        });
        try {
            return futureBookID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUserIDFromBorrowedBooksID(String ID) {
        Future<String> futureUserID = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT USER_ID FROM BOOKS_BORROWED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String UserID = setResult.getString("USER_ID");
            setResult.close();
            statement.close();
            connection.close();
            return UserID;
        });
        try {
            return futureUserID.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDateFromBorrowedBooksID(String ID) {
        Future<String> futureDate = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT DATE_BORROWED FROM BOOKS_BORROWED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String dateBorrowed = setResult.getString("DATE_BORROWED");
            setResult.close();
            statement.close();
            connection.close();
            return dateBorrowed;
        });
        try {
            return futureDate.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStatusFromBorrowedBooksID(String ID) {
        Future<String> futureStatus = executor.submit(() -> {
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT RESERVE_STATUS FROM BOOKS_BORROWED WHERE ID = ?");
            statement.setString(1, ID);
            ResultSet setResult = statement.executeQuery();
            setResult.next();
            String reserveStatus = setResult.getString("RESERVE_STATUS");
            setResult.close();
            statement.close();
            connection.close();
            return reserveStatus;
        });
        try {
            return futureStatus.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAvailableBooksQuery() {
        Future<List<String>> listFuture = executor.submit(() -> {
            List<String> availableBooksList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            // Available books = QUANTITY_AVAILABLE > 0
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME, BOOK_AUTHOR FROM BOOKS WHERE QUANTITY_AVAILABLE > 0");
            while (setResult.next())
            {
                availableBooksList.add(setResult.getString("BOOK_NAME") + " by " + setResult.getString("BOOK_AUTHOR"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return availableBooksList;
        });
        try {
            return listFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getReservedBooksFromUserID(String UserID) {
        Future<List<String>> futureReservedBooksFromUser = executor.submit(() -> {
            List<String> reservedBooksUser = new ArrayList<>();
            Connection connection = Connector();
            PreparedStatement statement = connection.prepareStatement("SELECT BOOK_ID FROM BOOKS_BORROWED WHERE USER_ID = ? AND (RESERVE_STATUS = 'Pending' OR RESERVE_STATUS = 'Reserved')");
            statement.setString(1, UserID);
            ResultSet setResult = statement.executeQuery();
            while (setResult.next())
            {
                reservedBooksUser.add(setResult.getString("BOOK_ID"));
            }
            setResult.close();
            statement.close();
            connection.close();
            return reservedBooksUser;
        });
        try {
            return futureReservedBooksFromUser.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastIDPlus1BooksBorrowedQuery() {
        Future<String> maxIDIncoming = executor.submit(() -> {
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT MAX(ID) + 1 FROM BOOKS_BORROWED");
            setResult.next();
            String maxID = setResult.getString("MAX(ID) + 1");
            setResult.close();
            statement.close();
            connection.close();
            return maxID;
        });
        try {
            return maxIDIncoming.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
