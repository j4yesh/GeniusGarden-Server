package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameSetting {
    private int playerLimit;
    private int maxAns;
    private float pickRange;
    private float speed;
    private String key;
    public GameSetting(int playerLimitForRoom, int maxAns, float pickRange, float speed) {
        this.playerLimit=playerLimitForRoom;
        this.maxAns=maxAns;
        this.pickRange=pickRange;
        this.speed=speed;
    }

    public boolean valid() {
        return this.playerLimit > 0 && this.maxAns > 0
                && this.pickRange > 0 && this.speed > 0
                && this.key != null && !this.key.isEmpty();
    }
}
