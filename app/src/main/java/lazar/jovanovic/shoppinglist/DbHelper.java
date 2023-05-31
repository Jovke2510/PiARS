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

    //ShowListActivity
    private final String TABLE_NAME2 = "ITEMS";
    static final String COLUMN_ITEMS_NAME = "Name";
    static final String COLUMN_ITEMS_LIST_NAME = "List_Name";
    static final String COLUMN_ITEMS_CHECKED = "Checked";
    static final String COLUMN_ITEMS_ID = "ID";

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

        //ShowListActivity
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME2 +
                " (" + COLUMN_ITEMS_NAME + " TEXT, " +
                COLUMN_ITEMS_LIST_NAME + " TEXT, " +
                COLUMN_ITEMS_CHECKED + " TEXT, " +
                COLUMN_ITEMS_ID + " TEXT);");
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
            cursorUsername.close();
            cursorEmail.close();
            close();
            return true;
        }

        cursorEmail.close();
        cursorUsername.close();
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
                cursor.close();
                return true;
            }
        }
        close();
        cursor.close();
        return false;
    }

    //WelcomeActivity methods
    public boolean findSharedLists(List<ListElement> sharedLists){
        sharedLists.clear();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                null,
                COLUMN_LIST_SHARED + " =?",
                new String[] {"yes"},
                null, null, null);

        if(cursor.getCount() == 0){
            Log.d("DBHELPER", "LIST IS EMPTY");
            close();
            cursor.close();
            return false;
        }

        while(cursor.moveToNext()){
            String listName = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));
            String shared = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_SHARED));
            String listUsername = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_CREATOR));
            Boolean listShared;

            if(shared.equals("yes"))
                listShared = true;
            else if (shared.equals("no"))
                listShared = false;
            else
                listShared = null;

            sharedLists.add(new ListElement(listName, listShared, listUsername));
        }

        cursor.close();
        close();
        return true;
    }

    public Boolean findUserLists(List<ListElement> userLists, String username){
        userLists.clear();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                null,
                COLUMN_LIST_CREATOR + " =?",
                new String[] {username},
                null, null, null);

        if(cursor.getCount() == 0){
            Log.d("DBHELPER", "LIST IS EMPTY");
            close();
            cursor.close();
            return false;
        }

        while(cursor.moveToNext()){
            String listName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));
            String listShared = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_SHARED));
            String listUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_CREATOR));
            Boolean isListShared;
            if (listShared.equals("yes"))
                isListShared = true;
            else if (listShared.equals("no"))
                isListShared = false;
            else
                isListShared = null;
            ListElement le = new ListElement(listName, isListShared, listUsername);
            userLists.add(le);
        }
        close();
        cursor.close();
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
            cursor.close();
            return true;
        }

        close();
        cursor.close();
        return false;
    }

    public String findListCreator(String name){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                new String[] {COLUMN_LIST_CREATOR},
                COLUMN_LIST_NAME + " =?",
                new String[] {name},
                null, null, null);

        if(cursor.getCount() == 0){
            cursor.close();
            close();
            return "";
        }

        cursor.moveToFirst();
        String creator = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_CREATOR));

        cursor.close();
        close();
        return creator;
    }
    public void removeList(String stNaslov){
        SQLiteDatabase db = getWritableDatabase();

        Log.d("DEHELPER", stNaslov);
        db.delete(TABLE_NAME1,
                COLUMN_LIST_NAME +" =?",
                new String[] {stNaslov});

        close();
    }
    public TaskElement insertTask(String item_name, String list_name, String checked, String id) {
        if(!checkItemName(id))
            return null;
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ITEMS_NAME, item_name);
        contentValues.put(COLUMN_ITEMS_LIST_NAME, list_name);
        contentValues.put(COLUMN_ITEMS_CHECKED, checked);
        contentValues.put(COLUMN_ITEMS_ID, id);

        db.insert(TABLE_NAME2, null, contentValues);
        close();
        boolean isChecked = checked.equals("yes");

        return new TaskElement(item_name, isChecked, list_name);
    }

    private boolean checkItemName(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME2,
                new String[] {COLUMN_ITEMS_ID},
                COLUMN_ITEMS_ID + " =?",
                new String[] {id},
                null, null, null);

        if(cursor.getCount() == 0){
            close();
            cursor.close();
            return true;
        }
        close();
        cursor.close();
        return false;
    }

    public boolean findItems(List<TaskElement> itemsList, String stNaslov) {
        itemsList.clear();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME2,
                new String[] {COLUMN_ITEMS_NAME, COLUMN_ITEMS_CHECKED},
                COLUMN_ITEMS_LIST_NAME + " = ?",
                new String[] {stNaslov},
                null, null, null);

        if(cursor.getCount() == 0){
            Log.d("DBHELPER", "NO TASK ITEMS");
            close();
            cursor.close();
            return false;
        }

        while (cursor.moveToNext()){
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_ITEMS_NAME));
            String checked = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_ITEMS_CHECKED));

            boolean isChecked = checked.equals("yes");

            TaskElement te = new TaskElement(name, isChecked, stNaslov);
            itemsList.add(te);
        }
        close();
        cursor.close();
        return true;
    }

    public void removeItem(TaskElement item, String stNaslov) {
        SQLiteDatabase db = getWritableDatabase();

        String itemID = item.getmNaslov() + stNaslov;
        Log.d("DEHELPER", "String koji glumi id: " + itemID);
        db.delete(TABLE_NAME2,
                COLUMN_ITEMS_ID +" =?",
                new String[] {itemID});

        close();
    }

    public void updateChecked(TaskElement te, boolean isChecked) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ITEMS_CHECKED, isChecked ? "yes" : "no");

        String whereClause = COLUMN_ITEMS_NAME + " =? AND " +
                COLUMN_ITEMS_LIST_NAME + " = ?";

        db.update(TABLE_NAME2, contentValues, whereClause,
                new String[] {te.getmNaslov(), te.getmItemListName()});

        close();
    }

    public boolean findListsNamed(List<ListElement> namedLists, String lista25) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME1,
                new String[] {COLUMN_LIST_NAME, COLUMN_LIST_SHARED},
                COLUMN_LIST_NAME + " =?",
                new String[] {lista25},
                null, null, null);

        if(cursor.getCount() == 0){
            cursor.close();
            close();
            return false;
        }

        while(cursor.moveToNext()){
            String listName = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));
            String shared = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_SHARED));
            String listUsername = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LIST_CREATOR));

            Boolean listShared;

            if(shared.equals("yes"))
                listShared = true;
            else if (shared.equals("no"))
                listShared = false;
            else
                listShared = null;

            namedLists.add(new ListElement(listName, listShared, listUsername));
        }

        cursor.close();
        close();
        return true;
    }

    public void deleteSharedLists() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME1,
                COLUMN_LIST_SHARED +" =?",
                new String[] {"yes"});

        close();
    }

    public void insertLists(List<ListElement> lists) {
        if(lists != null){
            for(ListElement i : lists){
                String shared;
                if(i.getmShared())
                    shared = "yes";
                else
                    shared = "no";
                insertList(i.getmNaslov(), i.getmUsername(), shared);
            }
        }
    }
}
