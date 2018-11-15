package com.jbyrnes.petrecordkeeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PetHistoryCard {

    private long id;
    private long petProfileFK;
    private String noteName;
    private long noteDate;
    private String noteText;
    private byte[] picture;
    private int position = 0;
    PetCardData cardData;

    public PetHistoryCard(){}

    public PetHistoryCard(long id, String noteName, long noteDate, String noteText, byte[] picture, long petProfileFK){
        this.id = id;
        this.noteName = noteName;
        this.noteDate = noteDate;
        this.noteText = noteText;
        this.picture = picture;
        this.petProfileFK = petProfileFK;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return noteName;
    }

    public void setName(String noteName) {
        this.noteName = noteName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getPetProfileFK() {
        return petProfileFK;
    }

    public long getNoteDate() { return noteDate; }

    public String getNoteText() { return noteText; }

    public Bitmap getPicture() {
        return BitmapFactory.decodeByteArray(picture, 0, picture.length);
    }
}
