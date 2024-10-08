package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String player;
    private String playerId;
    private String message;
    private String key;
}
