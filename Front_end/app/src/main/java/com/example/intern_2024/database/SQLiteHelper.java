package com.example.intern_2024.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.example.intern_2024.model.Item;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DETAIL = "detail";

    private static final String SQL_CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TIME + " TEXT," +
            COLUMN_DETAIL + " TEXT)";

    public SQLiteHelper(@Nullable Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if necessary
    }

    public List<Item> getAll() {
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String order = COLUMN_ID + " DESC";
        Cursor rs = db.query(TABLE_ITEMS, null, null, null, null, null, order);

        if (rs != null) {
            try {
                while (rs.moveToNext()) {
                    int id = rs.getInt(rs.getColumnIndexOrThrow(COLUMN_ID));
                    String time = rs.getString(rs.getColumnIndexOrThrow(COLUMN_TIME));
                    String detail = rs.getString(rs.getColumnIndexOrThrow(COLUMN_DETAIL));
                    Item item = new Item(id, time, detail);
                    list.add(item);
                }
            } finally {
                rs.close();
            }
        }
        db.close();
        return list;
    }

    public long addItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, item.getTime());
        values.put(COLUMN_DETAIL, item.getDetail());
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return result;
    }
}
