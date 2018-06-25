package student.op.mehelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by varun on 2017-09-16.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "MeHelpDB.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    public static final String CONTACTS_COLUMN_RELATION = "relation";
    public static final String CONTACTS_COLUMN_IMAGE = "image";

    public static final String PERSONAL_TABLE_NAME = "personalinfo";
    public static final String PERSONAL_COLUMN_ID = "id";
    public static final String PERSONAL_COLUMN_NAME = "name";
    public static final String PERSONAL_COLUMN_PHONE = "phone";
    public static final String PERSONAL_COLUMN_ADDRESS = "address";
    public static final String PERSONAL_COLUMN_BLOOD = "blood";
    public static final String PERSONAL_COLUMN_FOODRISK = "foodrisk";
    public static final String PERSONAL_COLUMN_IMAGE = "image";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        "(" + CONTACTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        CONTACTS_COLUMN_NAME + " TEXT, " +
                        CONTACTS_COLUMN_PHONE + " TEXT, " +
                        CONTACTS_COLUMN_RELATION + " TEXT, " +
                        CONTACTS_COLUMN_IMAGE + " BLOB)"
        );
        db.execSQL(
                "create table " + PERSONAL_TABLE_NAME +
                        "(" + PERSONAL_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        PERSONAL_COLUMN_NAME + " TEXT, " +
                        PERSONAL_COLUMN_ADDRESS + " TEXT, " +
                        PERSONAL_COLUMN_PHONE + " TEXT, " +
                        PERSONAL_COLUMN_BLOOD + " TEXT, " +
                        PERSONAL_COLUMN_FOODRISK + " TEXT, " +
                        PERSONAL_COLUMN_IMAGE + " BLOB )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PERSONAL_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertContact(String name, String phone, String relation, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_PHONE, phone);
        contentValues.put(CONTACTS_COLUMN_RELATION, relation);
        contentValues.put(CONTACTS_COLUMN_IMAGE, image);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertPersonalInfo(String name, String address, String phone, String blood, String foodrisk, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PERSONAL_COLUMN_NAME, name);
        contentValues.put(PERSONAL_COLUMN_ADDRESS, address);
        contentValues.put(PERSONAL_COLUMN_PHONE, phone);
        contentValues.put(PERSONAL_COLUMN_BLOOD, blood);
        contentValues.put(PERSONAL_COLUMN_FOODRISK, foodrisk);
        contentValues.put(PERSONAL_COLUMN_IMAGE, image);
        db.insert(PERSONAL_TABLE_NAME, null, contentValues);
        return true;
    }

    public Contact getContact(int id) {
        Contact contact = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME + " where id=" + id + "", null);
        if (res != null && res.getCount() > 0) {
            if (res.moveToFirst()) {
                //int id = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID));
                String name = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME));
                String phoneNumber = res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHONE));
                String relation = res.getString(res.getColumnIndex(CONTACTS_COLUMN_RELATION));
                byte[] photo = res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE));
                contact = new Contact(id, name, phoneNumber, relation, photo);
            }
        }
        return contact;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String name, String phone, String relation, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_PHONE, phone);
        contentValues.put(CONTACTS_COLUMN_RELATION, relation);
        contentValues.put(CONTACTS_COLUMN_IMAGE, image);
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updatePersonalInfo(Integer id, String name, String address, String phone, String blood, String foodrisk, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PERSONAL_COLUMN_NAME, name);
        contentValues.put(PERSONAL_COLUMN_ADDRESS, address);
        contentValues.put(PERSONAL_COLUMN_PHONE, phone);
        contentValues.put(PERSONAL_COLUMN_BLOOD, blood);
        contentValues.put(PERSONAL_COLUMN_FOODRISK, foodrisk);
        contentValues.put(PERSONAL_COLUMN_IMAGE, image);
        db.update(PERSONAL_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllCotacts(int pos) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Contact> getAllCotacts() {
        ArrayList<Contact> array_list = new ArrayList<Contact>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME, null);
        if (res != null && res.getCount() > 0) {
            if (res.moveToFirst()) {
                do {
                    //DatabaseUtils.dumpCursorToString(res);
                    int id = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID));
                    String name = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME));
                    String phoneNumber = res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHONE));
                    String relation = res.getString(res.getColumnIndex(CONTACTS_COLUMN_RELATION));
                    byte[] photo = res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE));
                    Contact contact = new Contact(id, name, phoneNumber, relation, photo);
                    array_list.add(contact);
                    //res.moveToNext();
                } while (res.moveToNext());
            }
        }
        return array_list;
    }

    public ArrayList<PersonalInfo> getPersonalInfo() {
        ArrayList<PersonalInfo> array_list = new ArrayList<PersonalInfo>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + PERSONAL_TABLE_NAME, null);
        if (res != null && res.getCount() > 0) {
            if (res.moveToFirst()) {
                do {
                    DatabaseUtils.dumpCursorToString(res);
                    int id = res.getInt(res.getColumnIndex(PERSONAL_COLUMN_ID));
                    String name = res.getString(res.getColumnIndex(PERSONAL_COLUMN_NAME));
                    String address = res.getString(res.getColumnIndex(PERSONAL_COLUMN_ADDRESS));
                    String phoneNumber = res.getString(res.getColumnIndex(PERSONAL_COLUMN_PHONE));
                    String blood = res.getString(res.getColumnIndex(PERSONAL_COLUMN_BLOOD));
                    String foodrisk = res.getString(res.getColumnIndex(PERSONAL_COLUMN_FOODRISK));
                    byte[] photo = res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE));
                    PersonalInfo personalInfo = new PersonalInfo(id, name, address, phoneNumber, blood, foodrisk, photo);
                    array_list.add(personalInfo);
                    //res.moveToNext();
                } while (res.moveToNext());
            }
        }
        return array_list;
    }
}
