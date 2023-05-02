package lazar.jovanovic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    //Register/Login strings
    private final String TABLE_NAME = "USERS";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PASSWORD = "Password";

    //WelcomeActivity strings
    private final String TABLE_NAME1 = "LISTS";
    public static final String COLUMN_LIST_NAME = "Name";
    public static final String COLUMN_LIST_CREATOR = "Creator";
    public static final String COLUMN_LIST_SHARED = "Shared";


    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Register/Login table
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT);");

        //WelcomeActivity table
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME1 +
                " (" + COLUMN_LIST_NAME + " TEXT, " +
                COLUMN_LIST_CREATOR + " TEXT, " +
                COLUMN_LIST_SHARED + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Register/Login methods
    public Boolean insertRegister(String username, String email, String password){
        if(checkRegister(username, email)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PASSWORD, password);

            db.insert(TABLE_NAME, null, values);
            close();
            return true;
        }

        close();
        return false;
    }

    private Boolean checkRegister(String username, String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursorUsername = db.query(TABLE_NAME, null,
                COLUMN_USERNAME + " =?", new String[] {username},
                null, null, null);
        Cursor cursorEmail = db.query(TABLE_NAME, null,
                COLUMN_EMAIL + " =?", new String[] {email},
                null, null, null);

        if(cursorUsername.getCount() == 0 && cursorEmail.getCount() == 0){
            close();
            return true;
        }

        close();
        return false;
    }

    public Boolean checkLogin(String username, String password){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {COLUMN_PASSWORD},
                COLUMN_USERNAME + " =?",
                        new String[] {username},
                null, null, null);

        if(cursor.getCount() == 1){
            cursor.moveToFirst();
            String storedPassword = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            if(storedPassword.equals(password)){
                close();
                return true;
            }
        }
        close();
        return false;
    }

    //WelcomeActivity methods
    public void findSharedLists(List<ListElement> sharedLists){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                new String[] {COLUMN_LIST_NAME, COLUMN_LIST_SHARED},
                COLUMN_LIST_SHARED + " =?",
                new String[] {"yes"},
                null, null, null);

        while(cursor.moveToNext()){
            String listName = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));
            String shared = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_SHARED));
            Boolean listShared;

            if(shared.equals("yes"))
                listShared = true;
            else if (shared.equals("no"))
                listShared = false;
            else
                listShared = null;

            sharedLists.add(new ListElement(listName, listShared));
        }

        cursor.close();
        close();
    }

    public Boolean findUserLists(List<ListElement> userLists, String username){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                new String[] {COLUMN_LIST_NAME, COLUMN_LIST_SHARED},
                COLUMN_LIST_CREATOR + " =?",
                new String[] {username},
                null, null, null);

        if(cursor.getCount() == 0){
            Log.d("DBHELPER", "LIST IS EMPTY");
            close();
            return false;
        }

        while(cursor.moveToNext()){
            String listName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));
            String listShared = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_SHARED));
            Log.d("DBHELPER", "listCreator: " + listName + " listShared: " + listShared);
            Boolean isListShared;
            if (listShared.equals("yes"))
                isListShared = true;
            else if (listShared.equals("no"))
                isListShared = false;
            else
                isListShared = null;
            Log.d("DBHELPER", "isListShared: " + isListShared.toString());
            ListElement le = new ListElement(listName, isListShared);
            Log.d("DBHELPER", "le.getCreator: " + le.getmNaslov() +
                    " le.getShared: " + le.getmShared().toString());
            userLists.add(le);
        }
        close();
        return true;
    }

    public Boolean insertList(String name, String creator, String shared){
        if(checkList(name)){
            SQLiteDatabase db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_LIST_NAME, name);
            contentValues.put(COLUMN_LIST_CREATOR, creator);
            contentValues.put(COLUMN_LIST_SHARED, shared);

            db.insert(TABLE_NAME1, null, contentValues);
            close();
            return true;
        }
        return false;
    }

    private Boolean checkList(String name) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                new String[] {COLUMN_LIST_NAME},
                COLUMN_LIST_NAME + " =?",
                new String[] {name},
                null, null, null);

        if(cursor.getCount() == 0){
            close();
            return true;
        }

        close();
        return false;
    }
}
