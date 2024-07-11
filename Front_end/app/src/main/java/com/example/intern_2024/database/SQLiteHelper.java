package com.example.intern_2024.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.intern_2024.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHelper";
    private FirebaseUser user;
    private FirebaseFirestore db_cloud;
    private Context context;

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db_cloud = FirebaseFirestore.getInstance();
        if (user == null) {
            return;
        }
        String uid = user.getUid();

        String sqlCreateDB = "CREATE TABLE items(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "time TEXT," +
                "detail TEXT)";
        db.execSQL(sqlCreateDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if necessary
    }

    public List<Item> getAll() {
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String order = "id DESC";
        Cursor rs = db.query("items", null, null, null, null, null, order);

        while (rs != null && rs.moveToNext()) {
            int id = rs.getInt(0);
            String time = rs.getString(1);
            String detail = rs.getString(2);
            Item item = new Item(id, time, detail);
            list.add(item);
        }
        return list;
    }

    public long addItem(Item i) {
        ContentValues values = new ContentValues();
        values.put("time", i.getTime());
        values.put("detail", i.getDetail());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert("items", null, values);
    }
}
