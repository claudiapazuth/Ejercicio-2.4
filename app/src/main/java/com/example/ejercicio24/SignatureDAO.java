package com.example.ejercicio24;

// SignatureDAO.java
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SignatureDAO {
    private DBHelper dbHelper;

    public SignatureDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertSignature(String description, byte[] blobData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", description);
        values.put("blob_data", blobData);
        long id = db.insert("signatures", null, values);
        db.close();
        return id;
    }

    public Cursor getAllSignatures() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("signatures", null, null, null, null, null, null);
    }
}
