package com.geniusgarden.server.GameplayModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
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
