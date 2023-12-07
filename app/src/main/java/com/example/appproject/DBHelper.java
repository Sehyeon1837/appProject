package com.example.appproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "test_db";
    private static final String COLUMN_NAME_ARRAY = "data";
    private static final String COLUMN_NAME_DOUBLE_ARRAY = "uhi";
    private static final String COLUMN_NAME_YEAR = "year";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String memoSQL = "CREATE TABLE " + DB_NAME +"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_ARRAY + " TEXT, " +
                COLUMN_NAME_DOUBLE_ARRAY + " TEXT, " +
                COLUMN_NAME_YEAR + " TEXT)";

        db.execSQL(memoSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == DATABASE_VERSION) {
            db.execSQL("drop table " + DB_NAME);
            onCreate(db);
        }

    }

    public List<DataModel> getDataByYear(String year) {
        List<DataModel> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"ID", COLUMN_NAME_ARRAY, COLUMN_NAME_DOUBLE_ARRAY, COLUMN_NAME_YEAR};
        String selection = COLUMN_NAME_YEAR + "=?";
        String[] selectionArgs = {year};

        Cursor cursor = db.query(DB_NAME, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex("ID");
                int stringArrayColumnIndex = cursor.getColumnIndex(COLUMN_NAME_ARRAY);
                int doubleArrayColumnIndex = cursor.getColumnIndex(COLUMN_NAME_DOUBLE_ARRAY);
                int yearColumnIndex = cursor.getColumnIndex(COLUMN_NAME_YEAR);

                int id = (idColumnIndex != -1) ? cursor.getInt(idColumnIndex) : -1;
                String stringArrayAsString = (stringArrayColumnIndex != -1) ? cursor.getString(stringArrayColumnIndex) : null;
                String doubleArrayAsString = (doubleArrayColumnIndex != -1) ? cursor.getString(doubleArrayColumnIndex) : null;
                String yearValue = (yearColumnIndex != -1) ? cursor.getString(yearColumnIndex) : null;

                if (year.equals(yearValue)) {
                    String[] strarr = stringArrayAsString.split(",");

                    String[] doubleArrayAsStrings = doubleArrayAsString.split(",");
                    double[] douarr = new double[doubleArrayAsStrings.length];
                    for (int i = 0; i < doubleArrayAsStrings.length; i++) {
                        douarr[i] = Double.parseDouble(doubleArrayAsStrings[i]);
                    }

                    DataModel dataModel = new DataModel(id, strarr, douarr, yearValue);
                    dataList.add(dataModel);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dataList;
    }

    public void insertArrays(String[] stringArray, double[] doubleArray, String year) {
        Log.d("Data", "값 추가");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String stringArrayAsString = joinArray(stringArray);
        String doubleArrayAsString = joinArrayForDouble(doubleArray);

        values.put(COLUMN_NAME_ARRAY, stringArrayAsString);
        values.put(COLUMN_NAME_DOUBLE_ARRAY, doubleArrayAsString);
        values.put(COLUMN_NAME_YEAR, year);

        db.insert(DB_NAME, null, values);
        db.close();
    }

    private String joinArray(String[] array) {
        StringBuilder result = new StringBuilder();
        for (String element : array) {
            result.append(element).append(",");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    private String joinArrayForDouble(double[] array) {
        StringBuilder result = new StringBuilder();
        String temp;
        for (double element : array) {
            temp = Double.toString(element);
            result.append(element).append(",");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }
}
