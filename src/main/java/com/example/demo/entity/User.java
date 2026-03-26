package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // ✅ Getter and Setter for id
    public Long getId() {
        return id;
    }

    // ✅ Getter and Setter for firstName
    public String getFirstName() { 
        return firstName; 
    }

    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    // 🆕 ADD THIS (JSON → firstname mapping)
    @JsonProperty("firstname")
    public void setFirstname(String firstname) {
        this.firstName = firstname;
    }

    // ✅ Getter and Setter for lastName
    public String getLastName() { 
        return lastName; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    // 🆕 ADD THIS (JSON → lastname mapping)
    @JsonProperty("lastname")
    public void setLastname(String lastname) {
        this.lastName = lastname;
    }

    // ✅ Getter and Setter for email
    public String getEmail() { 
        return email; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    // ✅ Getter and Setter for password
    public String getPassword() { 
        return password; 
    }

    public void setPassword(String password) { 
        this.password = password; 
    }

    // ✅ toString (unchanged)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}