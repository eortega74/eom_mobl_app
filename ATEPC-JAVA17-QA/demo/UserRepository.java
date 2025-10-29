package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository; //this is an interface


public interface UserRepository extends JpaRepository<User, String> {

}



