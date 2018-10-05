package com.jbyrnes.petrecordkeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.AccessControlContext;
import java.sql.Date;

public class PetDatabase extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "PET_DATABASE";
    private static final int DB_VERSION = 1;

    public static final String TABLE_PET_PROFILES = "PET_PROFILES";
    public static final String TABLE_VACCINATIONS_LIST = "VACCINATIONS_LIST";
    //public static final String TABLE_VACCINATIONS_HISTORY = "VACCINATIONS_HISTORY";
    //public static final String TABLE_MEDICATION_REMINDERS = "MEDICATION_REMINDERS";

    public static final String COLUMN_ID = "ID";
    public static final String PET_NAME = "NAME";
    public static final String SPECIES = "SPECIES";
    public static final String BREED = "BREED";
    public static final String BIRTH_DATE = "BIRTHDATE";
    public static final String PICTURE = "PICTURE";
    public static final String VACCINATION_NAME = "NAME";
    public static final String VACCINATION_DESCRIPTION = "DESCRIPTION";
    public static final String VACCINATION_FREQUENCY = "FREQUENCY";

    private static final String CREATE_TABLE_PET_PROFILES = "CREATE TABLE "
            + TABLE_PET_PROFILES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PET_NAME + " TEXT, " + SPECIES + " TEXT, "
            + BREED + " TEXT, " + BIRTH_DATE + " TEXT, " + PICTURE + " BLOB" + ");";

    private static final String CREATE_TABLE_VACCINATIONS_LIST = "CREATE TABLE "
            + TABLE_VACCINATIONS_LIST + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + VACCINATION_NAME + " TEXT, "
            + VACCINATION_FREQUENCY + " TEXT, " + SPECIES + " TEXT, " + VACCINATION_DESCRIPTION + " TEXT" + ");";

//    private static final String CREATE_TABLE_VACCINATIONS_HISTORY = "CREATE TABLE "
//            + TABLE_VACCINATIONS_HISTORY + "(" + COLUMN_ID + " INTEGER," + KEY_CITY + " TEXT );";

//    private static final String CREATE_TABLE_MEDICATION_REMINDERS = "CREATE TABLE "
//            + TABLE_MEDICATION_REMINDERS + "(" + COLUMN_ID + " INTEGER," + KEY_CITY + " TEXT );";

    public PetDatabase(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PET_PROFILES);
        db.execSQL(CREATE_TABLE_VACCINATIONS_LIST);
        //db.execSQL(CREATE_TABLE_VACCINATIONS_HISTORY);
        //db.execSQL(CREATE_TABLE_MEDICATION_REMINDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PET_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VACCINATIONS_LIST);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_VACCINATIONS_HISTORY);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATION_REMINDERS);
        onCreate(db);
    }
}