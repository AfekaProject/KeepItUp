package com.afeka.keepitup.keepitup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";

    private static final SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TRANSACTIONS);
        db.execSQL(SQL_CREATE_TRANSACTIONS);
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
                    FeedTransaction.COLUMN_NAME_NOTIFICATION + " TEXT," +
                    " );";

    private static final String SQL_DELETE_TRANSACTIONS =
            "DROP TABLE IF EXISTS " + FeedTransaction.TABLE_NAME;

    public void addTransaction (Transaction transaction){
        ContentValues values = new ContentValues();
        values.put(FeedTransaction.COLUMN_NAME_NAME,transaction.getName());
        values.put(FeedTransaction.COLUMN_NAME_TYPE,transaction.getType().toString());
        values.put(FeedTransaction.COLUMN_NAME_COMPANY,transaction.getCompany());
        String date = sdt.format(transaction.getStartDate());
        values.put(FeedTransaction.COLUMN_NAME_START_DATE,date);
        date = sdt.format(transaction.getEndDate());
        values.put(FeedTransaction.COLUMN_NAME_START_DATE,date);
        values.put(FeedTransaction.COLUMN_NAME_NOTES,transaction.getNotes());
        values.put(FeedTransaction.COLUMN_NAME_PRICE,transaction.getPrice());
        values.put(FeedTransaction.COLUMN_NAME_CHARGE_TYPE,transaction.getChargeType().toString());
        values.put(FeedTransaction.COLUMN_NAME_NOTIFICATION,transaction.getNotification().toString());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(FeedTransaction.TABLE_NAME,null,values);
        db.close();
    }

    public void removeTransaction (int id){
        final String DELETE_TRANSACTION = "DELETE FROM " + FeedTransaction.TABLE_NAME +
                " WHERE " + FeedTransaction._ID + "=" + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DELETE_TRANSACTION);
        db.close();
    }

    public ArrayList<Transaction> getTransactionList (Transaction.TransactionType selectedType){
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {FeedTransaction._ID,FeedTransaction.COLUMN_NAME_NAME,
                FeedTransaction.COLUMN_NAME_TYPE,FeedTransaction.COLUMN_NAME_COMPANY,
                FeedTransaction.COLUMN_NAME_START_DATE,FeedTransaction.COLUMN_NAME_END_DATE,
                FeedTransaction.COLUMN_NAME_NOTES,FeedTransaction.COLUMN_NAME_PRICE,
                FeedTransaction.COLUMN_NAME_CHARGE_TYPE,FeedTransaction.COLUMN_NAME_NOTIFICATION};
        String selection = FeedTransaction.COLUMN_NAME_TYPE+"";
        String[] selectionArgs = {selectedType+""};
        String sortOrder = FeedTransaction.COLUMN_NAME_START_DATE + " DESC";
        Cursor cursor = db.query(FeedTransaction.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedTransaction._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_NAME));
            Transaction.TransactionType type = Transaction.TransactionType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_TYPE)));
            String company = cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_COMPANY));
            Date startDate,endDate;
            try{
                startDate = sdt.parse(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_START_DATE)));
                endDate = sdt.parse(cursor.getString(cursor.getColumnIndexOrThrow(FeedTransaction.COLUMN_NAME_END_DATE)));

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

            list.add(new Transaction.TransactionBuilder(id,name,type,company,startDate)
                    .setEndDate(endDate).setNotes(notes)
                    .setPrice(price).setChargeType(chargeType).setNotification(notification)
                    .build());
        }
        return list;
    }

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
}
