package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class payLoad {
    private String socketId;
    private String type;
    private List<Float> position;
    private Float rotation;
    private String question;
    private String answer;
    private String name;
    private String data;

}
