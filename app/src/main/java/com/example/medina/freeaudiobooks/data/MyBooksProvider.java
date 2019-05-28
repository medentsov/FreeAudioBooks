package com.example.medina.freeaudiobooks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * content provider containing one
 * single table and incapsulating
 * favourites database
 */

public class MyBooksProvider extends ContentProvider {

    final String LOG_TAG = "myLogs";

    // // constants for database
    // Database
    static final String DB_NAME = "mydb";
    static final int DB_VERSION = 1;

    // Table
    static final String BOOK_TABLE = "books";

    // Fields
    static final String BOOK_ID = "_id";
    static final String BOOK_NAME = "name";
    static final String BOOK_LINK = "link";
    static final String BOOK_IMGLINK = "imglink";

    // Table creation script
    static final String DB_CREATE = "create table " + BOOK_TABLE + "("
            + BOOK_ID + " integer primary key autoincrement, "
            + BOOK_NAME + " text, "
            + BOOK_LINK + " text, "
            + BOOK_IMGLINK + " text" + ");";
//--------------------------------------------------------
    // // uri
    // authority
    static final String AUTHORITY = "com.medina.providers.BookBase";

    // path
    static final String BOOK_PATH = "books";

    // Common Uri
    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + BOOK_PATH);
//---------------------------------------------------------
    // data types
    // strings set
    static final String BOOK_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + BOOK_PATH;

    // one String
    static final String BOOK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + BOOK_PATH;
//---------------------------------------------------------
    //// UriMatcher
    // common Uri
    static final int URI_BOOKS = 1;

    // Uri with ID
    static final int URI_BOOKS_ID = 2;

    // description and creation of UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, BOOK_PATH, URI_BOOKS);
        uriMatcher.addURI(AUTHORITY, BOOK_PATH + "/#", URI_BOOKS_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // describing reading process
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());
        // check Uri
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS: // common Uri
                Log.d(LOG_TAG, "URI_BOOKS");
                // if sorting is not defined, we make a new one by the name
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BOOK_NAME + " ASC";
                }
                break;
            case URI_BOOKS_ID: // Uri with ID
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_BOOKS_ID, " + id);
                // adding ID to the selection results
                if (TextUtils.isEmpty(selection)) {
                    selection = BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + BOOK_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BOOK_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                BOOK_CONTENT_URI);
        return cursor;
    }
    // describing inserting process
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_BOOKS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(BOOK_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(BOOK_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }
    // describing deleting process
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(LOG_TAG, "URI_BOOKS");
                break;
            case URI_BOOKS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_BOOKS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + BOOK_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(BOOK_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
    // describing updating process
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(LOG_TAG, "URI_BOOKS");
                break;
            case URI_BOOKS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_BOOKS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + BOOK_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(BOOK_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                return BOOK_CONTENT_TYPE;
            case URI_BOOKS_ID:
                return BOOK_CONTENT_ITEM_TYPE;
        }
        return null;
    }
//creating the database
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
