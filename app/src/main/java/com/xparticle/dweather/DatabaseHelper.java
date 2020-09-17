package com.xparticle.dweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "MyWeather.db";
    public final static String TABLE_NAME = "myWeather_table";
    public final static String COL_1 = "ID";
    public final static String COL_2 = "NAME";
    public final static String COL_3 = "WEATHER";
    public final static String COL_4 = "TEMP";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT," +
                "WEATHER TEXT," +
                "TEMP INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name,String weather,String temp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,weather);
        contentValues.put(COL_4,temp);

        long result= db.insert(TABLE_NAME,null,contentValues);
        if(result== -1){
            return false;
        }
        else {
            return true;
        }
    }

//    public int deleteData(String id){
//        SQLiteDatabase db= this.getWritableDatabase();
//
//        return db.delete(TABLE_NAME,"ID=?",new String[]{id});
//    }

    public boolean updateData(String id,String name,String weather,String temp){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,weather);
        contentValues.put(COL_4,temp);

        db.update(TABLE_NAME,contentValues,"ID=?",new String[]{id});
        return true;
    }
    public Cursor readData(){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+COL_1+" DESC",null);
        return cursor;
    }
    public Cursor viewData(String id){
        SQLiteDatabase db= this.getWritableDatabase();

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE ID='"+id+"'";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

//    public Cursor viewAllData(){
//        SQLiteDatabase db= this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
//        return cursor;
//    }

//    public void deleteAll(){
//        SQLiteDatabase db= this.getReadableDatabase();
//        db.execSQL("DELETE FROM "+TABLE_NAME)   ;
//    }

}