package com.dicap.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "photographers")
public class Photographer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomComplet;
    private String city;
    private  String country;
    private double hourPrice;
    private String phone;
    private String email;
    private String password;
    private String ImageName;
    @Lob
    private byte[] Imagecontent;
    private  Role role;

    public  boolean checkPassword(String password){
        return this.password.equals(password);
    }
}
