package edu.uiuc.cs427app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * The DatabaseHelper class is responsible for managing the SQLite database used for user data and location mapping.
 * It provides methods for creating and updating the database, adding and retrieving user information,
 * and managing user-location mappings.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String UID = "UID";
    public static final String USERID = "USERID";
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String USER_THEME = "USER_THEME";

    public static final String USER_LOC_MAP = "USER_LOC_MAP";
    public static final String LOC_ID = "LOC_ID";

    public static final String LOC_TABLE = "LOC_TABLE";
    public static final String LOC_NAME = "LOC_NAME";

    public static final String CITY_TABLE = "CITY_TABLE";

    public static final String CITY_NAME = "CITY_NAME";


    /**
     * Constructs a new DatabaseHelper instance.
     *
     * @param context The context in which the database is created.
     */

    public DatabaseHelper(@Nullable Context context) {
        super(context, "teamFive.db", null, 1);
    }

    /**
     * Called when the database is created for the first time. This method defines the database schema
     * by creating the User and UserLocationMap tables.
     *
     * @param db The SQLite database to create.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        String createUserTable = " CREATE TABLE " + USER_TABLE + " ( " + UID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                USERID + " VARCHAR(50), " + USER_NAME + " VARCHAR(50), " + PASSWORD + " VARCHAR(50), " +
                USER_THEME + " VARCHAR(30)) ";


        String createUserLocMapTable = " CREATE TABLE " + USER_LOC_MAP + " ( " +
                USERID + " VARCHAR(50), " + LOC_NAME + " VARCHAR(50) ) ";

        String createCityTable = " CREATE TABLE " + CITY_TABLE + " ( " +
                CITY_NAME + " VARCHAR(50) ) ";

        db.execSQL(createUserTable);
        db.execSQL(createUserLocMapTable);
        db.execSQL(createCityTable);

    }

    /**
     * If DB Update require will use this
      */

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    /**
     * Adds a user to the User Table. Used for user registration.
     *
     * @param userBO The UserBO object containing user details.
     * @return True if the user is successfully added, false otherwise.
     */

    // This method will a User to User table. Use it for User Registration
    public boolean addUSer(UserBO userBO) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USERID, userBO.getUserId());
        cv.put(USER_NAME,userBO.getUserName());
        cv.put(PASSWORD,userBO.getPassword());
        cv.put(USER_THEME,userBO.getUserTheme());

        long insert = db.insert(USER_TABLE, null, cv);
        db.close();

        if(insert == -1) {
            return false;
        }else{
            return true;
        }

    }


    /**
     * populateCities to City Master table
     * populateCities to City Master Table for available choices
     *  @return boolean
     */
    public boolean populateCities() {

        final String[] CITY_LIST = {"Champaign", "Chicago", "New York","Los Angeles", "San Francisco","Springfield", "Rockford", "Urbana", "Moline"};

        long insert = -1;

        boolean cityExist = (getCityList().size() > 0) ? true :  false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if(cityExist){
            db.delete(CITY_TABLE,null , null);
        }

        for (String city : CITY_LIST){
            cv.put(CITY_NAME, city);
            insert = db.insert(CITY_TABLE, null, cv);
        }

        db.close();

        if(insert == -1) {
            return false;
        }else{
            return true;
        }

    }

    /**
     * getCityList for drop down population from city master table
     * @return List<String>
     */
    public List<String> getCityList(){

        String queryString = " SELECT * FROM " + CITY_TABLE ;

        SQLiteDatabase db = this.getReadableDatabase();

        List<String> cityList = new ArrayList<>();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            do {
                cityList.add(cursor.getString(0).toString());
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cityList;

    }


    /**
     * Retrieves user details based on user ID and password.
     *
     * @param userId     The user's ID.
     * @param password   The user's password.
     * @return A UserBO object containing the user's details, or an empty UserBO if not found.
     */
    // This will fetch a User details based on user id and password
    public UserBO getUSer(String userId, String password){

        UserBO userBO = new UserBO();

        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE  " + USERID + " =  '" + userId + "' AND "
                + PASSWORD + " = '"  +   password + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            do {
                int uid = cursor.getInt(0);

                userBO.setUserId(cursor.getString(1).toString());
                userBO.setUserName(cursor.getString(2).toString());
                userBO.setPassword(cursor.getString(3).toString());
                userBO.setUserTheme(cursor.getString(4).toString());

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userBO;

    }

    /**
     * Deletes a test user based on user ID. This method is used to clean up test users during database initialization.
     *
     * @param userId The user ID to be deleted.
     */
    // This will clean Test users upon DB initialization
    public void  deleteTestUser(String userId){

        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(USER_LOC_MAP, "USERID = ?", new String[]{userId});
            db.delete(USER_TABLE," USERID = ? ", new String[]{userId});
            System.out.println(" Deleted successfully ");
        }catch (Exception ex){
            System.out.println(" Deleted Failed ");
            ex.printStackTrace();
        }
        db.close();
    }
    /**
     * Adds test user-location mappings. This is typically used for testing and adds predefined user-location associations.
     *
     * @return True if the mappings are added successfully, false otherwise.
     */
    // Add Test Location Map
    public boolean addTestUSerLocMapping() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        String userId = "Test";
        //db.delete(USER_LOC_MAP," USERID = ? ", new String[]{userId});

        userId = "Test1";
        //db.delete(USER_LOC_MAP," USERID = ? ", new String[]{userId});

        long insert = -1;
        int i = 1;
        while (i<5){
            cv.put(USERID, "Test");
            cv.put(LOC_NAME,"Chicago-" + i);
            //insert = db.insert(USER_LOC_MAP, null, cv);
            i++;
        }

        int j = 1;
        while (j<5){
            cv.put(USERID, "Test1");
            cv.put(LOC_NAME,"Buffalo-" + j);
            //insert = db.insert(USER_LOC_MAP, null, cv);
            j++;
        }
        db.close();

        return insert != -1;

    }
    /**
     * Retrieves all location names for a given user ID in ascending order.
     *
     * @param userId The user ID for which locations are retrieved.
     * @return A list of location names for the user.
     */
    // This will fetch all location names for a given user id in ascending order
    public List<String> getUserLocations(String userId) {
        List<String> locations = new ArrayList<>();

        String queryString = "SELECT " + LOC_NAME + " FROM " + USER_LOC_MAP + " WHERE  " + USERID + " =  ?" + " ORDER BY " + LOC_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{userId});

        if (cursor.moveToFirst()) {
            do {
                locations.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return locations;
    }

    /**
     * Adds a location for a given user ID only if it doesn't already exist.
     *
     * @param userId   The user ID to which the location is added.
     * @param location The location name to be added.
     */
    // This method adds a location for a given user id only if it doesn't exist
    public void addLocation(String userId, String location) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Check if the location already exists for the user
        String queryString = "SELECT " + LOC_NAME + " FROM " + USER_LOC_MAP + " WHERE " + USERID + " = ? AND " + LOC_NAME + " = ?";
        Cursor cursor = db.rawQuery(queryString, new String[]{userId, location});
        boolean locationExists = cursor.moveToFirst();
        cursor.close();

        // If location doesn't exist, add it
        if (!locationExists) {
            ContentValues cv = new ContentValues();
            cv.put(USERID, userId);
            cv.put(LOC_NAME, location);

            db.insert(USER_LOC_MAP, null, cv);
        }
        db.close();
    }

    /**
     * This method removes a location for a given user id only if it exists
     * @param userId
     * @param location
     */
    public void deleteLocation(String userId, String location) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Check if the location already exists for the user
        String queryString = "SELECT " + LOC_NAME + " FROM " + USER_LOC_MAP + " WHERE " + USERID + " = ? AND " + LOC_NAME + " = ?";
        Cursor cursor = db.rawQuery(queryString, new String[]{userId, location});
        boolean locationExists = cursor.moveToFirst();
        cursor.close();

        // If location exists, remove it
        if (locationExists) {
            String[] whereArgs = new String[]{userId, location};
            db.delete(USER_LOC_MAP, "USERID = ? AND LOC_NAME = ?", whereArgs);
        }
        db.close();
    }

}
