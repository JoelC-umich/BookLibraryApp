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

    protected static String database = "defaultdb";
    protected static String ip = "libraryapp-library-app.b.aivencloud.com";
    protected static String port = "10606";
    protected static String username = "avnadmin";
    protected static String password = "AVNS_VqaSpcSMcqZ2--67GYI";

    public static String IDWhenLoggingIn;

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

    public static void runQuery(String query) {
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
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME FROM BOOKS");
            while (setResult.next()) {
                bookNameList.add(setResult.getString("BOOK_NAME"));
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

    public static List<String> getReservedBookNamesQuery() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> listFutureReservedBookNames = executorService.submit(() -> {
            List<String> reservedBookNameList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            ResultSet setResult = statement.executeQuery("SELECT BOOK_NAME FROM BOOKS WHERE QUANTITY_BORROWED > 0");
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

    public static String getBookNameFromBookID(String ID) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureBookName = executorService.submit(() -> {
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
            return futureBookName.get();
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

    public static String getRoomIDFromReserveID(String ID) {
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

    public static String getUserIDFromReserveID(String ID) {
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

    public static String getSlotFromReserveID(String ID) {
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


    public static List<String> getRoomIDsReservedOnDateQuery(String date) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executorService.submit(() -> {
            List<String> reservedList = new ArrayList<>();
            Connection connection = Connector();
            Statement statement = connection.createStatement();
            // Returns ROOM_ID and SLOT for every reservation on the given date
            ResultSet setResult = statement.executeQuery(
                    "SELECT ROOM_ID, SLOT FROM ROOMS_RESERVED WHERE DATE = '" + date + "' AND RESERVE_STATUS != 'Cancelled'"
            );
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

}