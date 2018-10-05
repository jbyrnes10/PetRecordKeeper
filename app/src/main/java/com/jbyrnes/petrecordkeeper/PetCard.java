package com.jbyrnes.petrecordkeeper;

public class PetCard {

    private long id;
    private String name;

    public PetCard() { }

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
}