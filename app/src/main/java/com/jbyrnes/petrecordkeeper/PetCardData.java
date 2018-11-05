package com.jbyrnes.petrecordkeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PetCardData {
    private SQLiteDatabase database;
    private SQLiteOpenHelper databaseHelper;

    public PetCardData(Context context) {
        this.databaseHelper = new PetDatabase(context);
    }

    private static final String[] PROFILE_COLUMNS = {
            PetDatabase.COLUMN_ID,
            PetDatabase.PET_NAME,
            PetDatabase.BREED,
            PetDatabase.SPECIES,
            PetDatabase.BIRTH_DATE
    };

    private static final String[] VACCINATIONS_COLUMNS = {
            PetDatabase.COLUMN_ID,
            PetDatabase.VACCINATION_NAME,
            PetDatabase.VACCINATION_FREQUENCY,
            PetDatabase.VACCINATION_DESCRIPTION,
            PetDatabase.SPECIES
    };

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    public ArrayList<PetCard> getAll() {
        ArrayList<PetCard> cards = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM PET_PROFILES ORDER BY NAME DESC", null);

            int count = cursor.getCount();

            if (count > 0 && cursor.moveToFirst()) {
                do {
                cards.add(new PetCard(
                        cursor.getInt(0), //id,
                        cursor.getString(1), //name
                        cursor.getString(2), //species
                        cursor.getString(3), //breed
                        cursor.getString(4), //birth date
                        cursor.getBlob(5) //picture
                ));}
                while (cursor.moveToNext());
           }
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public PetCard getSingleCard(String name, long id) {
        PetCard card = new PetCard();
        Cursor cursor = null;

        try {
            if (database == null) open();
            cursor = database.rawQuery("SELECT * FROM PET_PROFILES WHERE NAME = '" + name + "' AND ID = " + id + " ORDER BY NAME DESC", null);

            int count = cursor.getCount();

            if (count == 1 && cursor.moveToFirst()) {
                    card = new PetCard(
                            cursor.getLong(0), //id,
                            cursor.getString(1), //name
                            cursor.getString(2), //species
                            cursor.getString(3), //breed
                            cursor.getString(4), //birth date
                            cursor.getBlob(5) //picture
                    );}

        } catch (Exception ex){
            System.out.println(ex.getMessage());
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return card;
    }

    public PetCard create(PetCard card) {
        ContentValues values = new ContentValues();
        values.put(PetDatabase.PET_NAME, card.getName());
        long id = database.insert(PetDatabase.TABLE_PET_PROFILES, null, values);
        card.setId(id);
        return card;
    }
}