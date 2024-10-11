package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("room")
@AllArgsConstructor
public class Room {
    @Id
    private String roomId;
    private List<String> players;

    public void addPlayer(String username){
        players.add(username);
    }
}
