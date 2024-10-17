package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "publicRoom")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class publicRoom {
    @Id
    private String id;
    private int players;
    private boolean isUsed;
//    public List<String> used;
//    public List<String>unused;
}
