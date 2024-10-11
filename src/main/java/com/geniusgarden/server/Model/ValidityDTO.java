package com.geniusgarden.server.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidityDTO {
    private String key;
    private String username;
    private String roomId;
}
