package com.example.weheal;

public class Insumo {

    private String ID;
    private String name;
    private String type;
    private String image;
    private String description;
    private String owner;
    private String owner_photo;
    private int quantity;

    public Insumo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getID() { return ID; }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner_photo() {
        return owner_photo;
    }

    public void setOwner_photo(String owner_photo) {
        this.owner_photo = owner_photo;
    }
}


