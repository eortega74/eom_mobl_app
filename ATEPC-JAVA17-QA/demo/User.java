package com.example.demo;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data; //Data tag generates getters, setters, toString, equals
import jakarta.persistence.Entity;


@Data
@Entity
@Table(name = "users")
public class User {
    public String name;
    public String email;
    public String role;
    @Id
    public String ssoID;

}
