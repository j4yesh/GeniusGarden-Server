package com.geniusgarden.server.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("result")
@AllArgsConstructor
public class Result {
    @Id
    private String id;
    private String username;
    private int rank;
    private int correct;
    private int wrong;
    private float acceptance;
    private Date time;
    private String conKey;
}
