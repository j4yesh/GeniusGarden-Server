package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Documented;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class player {
    private String name;
    private String refRoom;
    private String SocketId;
    private int ratCnt;

}
