package imangazaliev.scripto.test;

import com.google.gson.annotations.SerializedName;

public class CustomTestModel {

    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;

    public CustomTestModel() {

    }

    public CustomTestModel(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

}
