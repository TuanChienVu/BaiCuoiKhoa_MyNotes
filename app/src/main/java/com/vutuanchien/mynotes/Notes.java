package com.vutuanchien.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by MY PC on 05/04/2016.
 */
public class Notes {
    String title;
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase sqLiteDatabase;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, date text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    //    Constructor - takes the context to allow the database to be
    public Notes(Context ctx) {
        this.mCtx = ctx;
    }

    /*Open the notes database. If it cannot be opened, try to create a new
     instance of the database. If it cannot be created, throw an exception to
     signal the failure*/
    public Notes open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        sqLiteDatabase = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /*Create a new note using the title and body provided. If the note is
     successfully created return the new rowId for that note, otherwise return
     a -1 to indicate failure.*/
    public long createNote(String title, String body, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, date);

        return sqLiteDatabase.insert(DATABASE_TABLE, null, initialValues);
    }

//      Delete the note with the given rowId

    public boolean deleteNote(long rowId) {

        return sqLiteDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //     Return a Cursor over the list of all notes in the database
    public Cursor fetchAllNotes() {
        Cursor mCursor = sqLiteDatabase.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_DATE}, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

//    Return a cursor fetch by name
    public Cursor fetchNotesByName(String inputText) throws SQLException {
        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = sqLiteDatabase.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE},
                    null, null, null, null, null);

        } else {
            mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE},
                    KEY_TITLE + " like '%" + inputText + "%'", null, null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

//     Return a Cursor positioned at the note that matches the given rowId

    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                        KEY_TITLE, KEY_BODY, KEY_DATE}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    /*Update the note using the details provided. The note to be updated is
    specified using the rowId, and it is altered to use the title and body
    values passed in*/
    public boolean updateNote(long rowId, String title, String body, String date) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        //This lines is added for personal reason
        args.put(KEY_DATE, date);

        //One more parameter is added for data
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
