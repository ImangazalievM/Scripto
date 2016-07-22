package imangazaliev.scripto.sample;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;
    @SerializedName("age")
    private int age;
    @SerializedName("height")
    private float height;
    @SerializedName("married")
    private boolean married;

    public User() {

    }

    public User(String name, String surname, int age, float height, boolean married) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.height = height;
        this.married = married;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public float getHeight() {
        return height;
    }

    public boolean isMarried() {
        return married;
    }

    public String getUserInfo() {
        return String.format("Name: %s \nSurname: %s \nAge: %d \nHeight: %s \nMarried: %s", name, surname, age, height, married);
    }

}
