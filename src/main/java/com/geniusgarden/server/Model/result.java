package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class result {
    private int rank;
    private String socketId;
    private String name;
    private List<String> ranking=new ArrayList<String>();

    public void addRank(String rank) {
            ranking.add(rank);
    }
}
