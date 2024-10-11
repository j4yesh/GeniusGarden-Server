package com.geniusgarden.server.Model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("user")
public class AuthUser {
    @Id
    private String username;
    private String password;
    private String email;
    private String otp;
    private boolean active;

    private int games ;
    private int correct;
    private int wrong;
    private float acceptance;
}
