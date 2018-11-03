package com.jbyrnes.petrecordkeeper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class PetCard {

    private long id;
    private String name;
    private String species;
    private String breed;
    private String birthDate;
    private byte[] picture;
    private int position = 0;

    public PetCard(){}

    public PetCard(long id, String name, String species, String breed, String birthDate, byte[] picture) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.picture = picture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Bitmap getPicture() {
        return BitmapFactory.decodeByteArray(picture, 0, picture.length);
    }
}