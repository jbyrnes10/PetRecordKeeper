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
            cursor = db.query(PetDatabase.TABLE_PET_PROFILES, PROFILE_COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PetCard card = new PetCard();
                    card.setId(cursor.getLong(cursor.getColumnIndex(PetDatabase.COLUMN_ID)));
                    card.setName(cursor.getString(cursor.getColumnIndex(PetDatabase.PET_NAME)));
                    //card.setColorResource(cursor.getInt(cursor.getColumnIndex(PetDatabase.COLUMN_COLOR_RESOURCE)));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public PetCard create(PetCard card) {
        ContentValues values = new ContentValues();
        values.put(PetDatabase.PET_NAME, card.getName());
        //values.put(PetDatabase.)
        //values.put(PetDatabase.COLUMN_COLOR_RESOURCE, card.getColorResource());
        long id = db.insert(PetDatabase.TABLE_PET_PROFILES, null, values);
        card.setId(id);
        return card;
    }
}