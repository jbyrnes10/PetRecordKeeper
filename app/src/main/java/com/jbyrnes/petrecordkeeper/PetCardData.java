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
            cursor = database.rawQuery("SELECT * FROM PET_PROFILES ORDER BY NAME ASC", null);

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
            cursor = database.rawQuery("SELECT * FROM PET_PROFILES WHERE NAME = '" + name + "' AND ID = " + id, null);

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

    public PetCard getSingleProfileCardById(long id) {
        PetCard card = new PetCard();
        Cursor cursor = null;

        try {
            if (database == null) open();
            cursor = database.rawQuery("SELECT * FROM PET_PROFILES WHERE ID = " + id, null);

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

    public ArrayList<PetHistoryCard> getAllHistoryCards(long foreignKey) {
        ArrayList<PetHistoryCard> cards = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM PET_HISTORY WHERE PET_PROFILE_FK = " + foreignKey + " ORDER BY DATE DESC", null);

            int count = cursor.getCount();
            if (count > 0 && cursor.moveToFirst()) {
                do {
                    cards.add(new PetHistoryCard(
                            cursor.getInt(0), //id,
                            cursor.getString(1), //note_name
                            cursor.getLong(2), //note_date
                            cursor.getString(3), //note_text
                            cursor.getBlob(4), //picture
                            cursor.getInt(5) //pet_profile_fk
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

    public PetHistoryCard getSingleHistoryCardById(long id) {
        PetHistoryCard card = new PetHistoryCard();
        Cursor cursor = null;

        try {
            if (database == null) open();
            cursor = database.rawQuery("SELECT * FROM PET_HISTORY WHERE ID = " + id, null);

            int count = cursor.getCount();

            if (count == 1 && cursor.moveToFirst()) {
                card = new PetHistoryCard(
                        cursor.getInt(0), //id,
                        cursor.getString(1), //note_name
                        cursor.getLong(2), //note_date
                        cursor.getString(3), //note_text
                        cursor.getBlob(4), //picture
                        cursor.getInt(5) //pet_profile_fk
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

    public PetHistoryCard create(PetHistoryCard card) {
        ContentValues values = new ContentValues();
        values.put(PetDatabase.NOTE_NAME, card.getName());
        long id = database.insert(PetDatabase.TABLE_HISTORY_LIST, null, values);
        card.setId(id);
        return card;
    }
}