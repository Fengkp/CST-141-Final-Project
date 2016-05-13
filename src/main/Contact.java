package main;

import java.io.*;

public class Contact implements Serializable {
    // Fields--------------------------------------------------
    private String name;
    private String phone;
    private String email;

    // Constructors--------------------------------------------
    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Contact(String name) {
        this(name, null, null);
    }

    // Getters-------------------------------------------------
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    // Setters-------------------------------------------------
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Methods-------------------------------------------------
    public String toString() {
        return name + " (" + phone + ") " + "<" + email + ">";
    }

    public boolean equals(Object other) {
        boolean result = false;

        if (other.getClass() != null) {
            Contact that = (Contact) other;
            if (this.name.equalsIgnoreCase(that.name))
                result = true;
        }
        return result;
    }

}
