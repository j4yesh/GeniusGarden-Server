package com.geniusgarden.server.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@Document("otp")
public class Otp {
    @Id
    private String otp;
    private String userEmail;
    private Date createdAt;
    private Date ExpireDate ;
}
