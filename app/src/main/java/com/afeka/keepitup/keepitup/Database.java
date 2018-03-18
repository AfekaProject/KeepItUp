package com.afeka.keepitup.keepitup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";
    private static final SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");
    private static final String IMAGE_FOLDER = "images";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRANSACTIONS);
        db.execSQL(SQL_CREATE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TRANSACTIONS);
        db.execSQL(SQL_DELETE_IMAGES);
        db.execSQL(SQL_CREATE_TRANSACTIONS);
        db.execSQL(SQL_CREATE_IMAGES);
    }

    public void addBackup(ArrayList<Transaction> arrayList){
        for (int i = 0 ; i< arrayList.size() ; i++){
            addTransaction(arrayList.get(i));
        }
    }

    public int addTransaction (Transaction transaction){
        ContentValues values = new ContentValues();
        values.put(FeedTransaction.COLUMN_NAME_NAME,transaction.getName());
        values.put(FeedTransaction.COLUMN_NAME_TYPE,transaction.getType().toString());
        values.put(FeedTransaction.COLUMN_NAME_COMPANY,transaction.getCompany());
        String date = sdt.format(transaction.getStartDate());
        values.put(FeedTransaction.COLUMN_NAME_START_DATE,date);
        if (transaction.getEndDate()!=null)
            date = sdt.format(transaction.getEndDate());
        else
            date = "";
        values.put(FeedTransaction.COLUMN_NAME_END_DATE,date);
        values.put(FeedTransaction.COLUMN_NAME_NOTES,transaction.getNotes());
        values.put(FeedTransaction.COLUMN_NAME_PRICE,transaction.getPrice());
        values.put(FeedTransaction.COLUMN_NAME_CHARGE_TYPE,transaction.getChargeType().toString());
        values.put(FeedTransaction.COLUMN_NAME_NOTIFICATION,transaction.getNotification().toString());
        SQLiteDatabase db = getWritableDatabase();
        int id = (int)db.insert(FeedTransaction.TABLE_NAME,null,values);
        db.close();
        addImages(id,transaction.getDocuments());

        return id;
    }

    public void removeTransaction (int id){
        final String DELETE_TRANSACTION = "DELETE FROM " + FeedTransaction.TABLE_NAME +
                " WHERE " + FeedTransaction._ID + "=" + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DELETE_TRANSACTION);
        db.close();
    }

    public Transaction getTransactionById (int transactionId){
        SQLiteDatabase db = getReadableDatabase();
        Transaction t = null;
/*
        String[] projection = {FeedTransaction._ID,FeedTransaction.COLUMN_NAME_NAME,
                FeedTransaction.COLUMN_NAME_TYPE,FeedTransaction.COLUMN_NAME_COMPANY,
                FeedTransaction.COLUMN_NAME_START_DATE,FeedTransaction.COLUMN_NAME_END_DATE,
                FeedTransaction.COLUMN_NAME_NOTES,FeedTransaction.COLUMN_NAME_PRICE,
                FeedTransaction.COLUMN_NAME_CHARGE_TYPE,FeedTransaction.COLUMN_NAME_NOTIFICATION};
        String selection = FeedTransaction._ID +"= ?";
        String[] selectionArgs = {transactionId+""};
        String sortOrder = FeedTransaction.COLUMN_NAME_START_DATE + " DESC";
        Cursor cursor = db.query(FeedTransaction.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        */
        Cursor cursor = db.rawQuery("SELECT * " +
                "FROM " + FeedTransaction.TABLE_NAME +
                " WHERE " + FeedTransaction._ID + "=" + transactionId , null);
        if (cursor.moveToFirst())
            t = readTransactionCursor(cursor);
        cursor.close();
        t.setDocuments(getImages(t.getId()));
        db.close();
        return t;
    }

    public ArrayList<Bitmap> getImages (int id) {
        ArrayList<Bitmap> images = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {FeedImages.COLUMN_NAME_FILE};
        String selection = FeedImages.COLUMN_NAME_TRANSACTION_ID +"= ?";
        String[] selectionArgs = {id+""};
        Cursor cursor = db.query(FeedImages.TABLE_NAME,projection,selection,selectionArgs,null,null,null);

            while(cursor.moveToNext()){
                if (!cursor.isNull(cursor.getColumnIndexOrThrow(FeedImages.COLUMN_NAME_FILE))){
                    // byte[] imgByte = cursor.getBlob(cursor.getColumnIndex(FeedImages.COLUMN_NAME_FILE));
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    File f = new File(cursor.getString(cursor.getColumnIndex(FeedImages.COLUMN_NAME_FILE)));
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap==null)
                        Log.e("Database:","bitmap = null");
                    images.add(bitmap);
                }
            }
        cursor.close();
        db.close();
        return images;
    }

    public ArrayList<Transaction> getTransactionList (Transaction.TransactionType selectedType){
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {FeedTransaction._ID,FeedTransaction.COLUMN_NAME_NAME,
                FeedTransaction.COLUMN_NAME_TYPE,FeedTransaction.COLUMN_NAME_COMPANY,
                FeedTransaction.COLUMN_NAME_START_DATE,FeedTransaction.COLUMN_NAME_END_DATE,
                FeedTransaction.COLUMN_NAME_NOTES,FeedTransaction.COLUMN_NAME_PRICE,
                FeedTransaction.COLUMN_NAME_CHARGE_TYPE,FeedTransaction.COLUMN_NAME_NOTIFICATION};
        String selection = FeedTransaction.COLUMN_NAME_TYPE+"= ?";
        String[] selectionArgs = {selectedType+""};
        String sortOrder = FeedTransaction.COLUMN_NAME_START_DATE + " DESC";
        Cursor cursor = db.query(FeedTransaction.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        while (cursor.moveToNext()){
            list.add(readTransactionCursor(cursor));
        }
        cursor.close();
        for (int i = 0 ; i<list.size() ; i++){
            list.get(i).setDocuments(getImages(list.get(i).getId()));
        }
        db.close();
        return list;
    }

    public ArrayList<Transaction> getAllTransactions (){
        ArrayList<Transaction> list = new ArrayList<>();
        for (int i = 0 ; i < Transaction.TransactionType.values().length ; i++){
            list.addAll(getTransactionList(Transaction.TransactionType.values()[i]));
        }
        return list;
    }

    public void clearTables (){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + FeedTransaction.TABLE_NAME);
        db.execSQL("DELETE FROM " + FeedImages.TABLE_NAME);
    }

    private void addImages (long id, ArrayList<Bitmap> images){
        if (images==null)
            return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0 ; i <  images.size() ; i++){
            String name = id + "_" + i + "image";
            String converted = saveImageToDevice(name,images.get(i));
            values.put(FeedImages.COLUMN_NAME_TRANSACTION_ID,id);
            values.put(FeedImages.COLUMN_NAME_FILE,converted);
            db.insert(FeedImages.TABLE_NAME,null,values);
        }
        db.close();
    }

    private String saveImageToDevice(String name,Bitmap image){

        String external = Environment.getExternalStorageDirectory().toString();
        File directory = new File (external+"/"+IMAGE_FOLDER);
        directory.mkdirs();
        String fName = name +".jpg";
        File file = new File(directory,fName);
        FileOutputStream fos;
        String imagePath;
        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100 , fos);
            fos.close();
            imagePath = file.getAbsolutePath();
        }
        catch (Exception ex) {
            Log.i("DATABASE", "Problem updating picture", ex);
            imagePath = "";
        }
        return imagePath;
    }

    private Transaction readTransactionCursor(Cursor cursor){

        int id = (int)cursor.getLong(cursor.getColumnIndexOrThrow(FeedTransaction._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_NAME));
        Transaction.TransactionType type = Transaction.TransactionType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_TYPE)));
        String company = cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_COMPANY));
        Date startDate,endDate;
        try{
            startDate = sdt.parse(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_START_DATE)));
            String tempDate = cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_END_DATE));
            if (tempDate =="")
                endDate = null;
            else
                endDate = sdt.parse(tempDate);

        } catch (ParseException e){
            startDate = Calendar.getInstance().getTime();
            endDate = Calendar.getInstance().getTime();
            e.printStackTrace();
        }

        //optional parameters
        //ArrayList<File> documents;
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_NOTES));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_PRICE));
        Transaction.ChargeType chargeType = Transaction.ChargeType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_CHARGE_TYPE)));
        Transaction.ForwardNotification notification = Transaction.ForwardNotification.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_NOTIFICATION)));

        return new Transaction.TransactionBuilder(name,type,company,startDate)
                .setId(id).setEndDate(endDate).setNotes(notes)
                .setPrice(price).setChargeType(chargeType).setNotification(notification)
                .build();
    }

    private static final String SQL_CREATE_TRANSACTIONS =
            "CREATE TABLE " + FeedTransaction.TABLE_NAME + " (" +
                    FeedTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedTransaction.COLUMN_NAME_NAME + " TEXT," +
                    FeedTransaction.COLUMN_NAME_TYPE + " TEXT," +
                    FeedTransaction.COLUMN_NAME_COMPANY + " TEXT," +
                    FeedTransaction.COLUMN_NAME_START_DATE + " DATE," +
                    FeedTransaction.COLUMN_NAME_END_DATE + " DATE," +
                    FeedTransaction.COLUMN_NAME_NOTES + " TEXT," +
                    FeedTransaction.COLUMN_NAME_PRICE + " REAL," +
                    FeedTransaction.COLUMN_NAME_CHARGE_TYPE + " TEXT," +
                    FeedTransaction.COLUMN_NAME_NOTIFICATION + " TEXT" +
                    " );";

    private static final String SQL_DELETE_TRANSACTIONS =
            "DROP TABLE IF EXISTS " + FeedTransaction.TABLE_NAME;

    private static final String SQL_CREATE_IMAGES =
            "CREATE TABLE " + FeedImages.TABLE_NAME + " (" +
                    FeedImages.COLUMN_NAME_TRANSACTION_ID + " INTEGER, " +
                    FeedImages.COLUMN_NAME_FILE + " TEXT" +
                    " );";

    private static final String SQL_DELETE_IMAGES =
            "DROP TABLE IF EXISTS " + FeedImages.TABLE_NAME;

    private class FeedTransaction implements BaseColumns{
        private static final String TABLE_NAME = "transactions";
        private static final String COLUMN_NAME_NAME = "name";
        private static final String COLUMN_NAME_TYPE = "type";
        private static final String COLUMN_NAME_COMPANY = "company";
        private static final String COLUMN_NAME_START_DATE = "startDate";
        private static final String COLUMN_NAME_END_DATE = "endDate";
        private static final String COLUMN_NAME_NOTES = "notes";
        private static final String COLUMN_NAME_PRICE = "price";
        private static final String COLUMN_NAME_CHARGE_TYPE = "chargeType";
        private static final String COLUMN_NAME_NOTIFICATION = "notification";
    }

    private class FeedImages implements BaseColumns{
        private static final String TABLE_NAME = "images";
        private static final String COLUMN_NAME_TRANSACTION_ID = "transactionId";
        private static final String COLUMN_NAME_FILE = "file";
    }
}