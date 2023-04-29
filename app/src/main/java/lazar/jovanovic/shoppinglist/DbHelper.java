package lazar.jovanovic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private final String TABLE_NAME = "USERS";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PASSWORD = "Password";

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

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

        Cursor cursor = db.query(TABLE_NAME, null,
                COLUMN_USERNAME + " =?",
                        new String[] {username},
                null, null, null);

        if(cursor.getCount() == 1){
            close();
            return true;
        }
        close();
        return false;
    }


}
