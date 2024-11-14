package com.geniusgarden.server.Controller;

import com.geniusgarden.server.GameplayModel.pair;
import com.geniusgarden.server.GameplayModel.player;
import com.geniusgarden.server.Model.GameSetting;
import com.geniusgarden.server.Model.Notification;
import com.geniusgarden.server.Service.GameHandler;
import com.geniusgarden.server.Service.JsonUtil;
import com.geniusgarden.server.env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/server")
public class GameStructure {

    @Autowired
    GameHandler gameHandler;

    @PostMapping("/setting")
    public ResponseEntity<String> setting(@RequestBody GameSetting gameSetting) {
        try {
            if ( gameSetting.valid() &&
                    gameSetting.getKey().equals(env.settingKey)) {

                GameHandler.playerLimitForRoom = gameSetting.getPlayerLimit();
                GameHandler.maxAns = gameSetting.getMaxAns();

                player.pickRange = gameSetting.getPickRange();
                player.speed = gameSetting.getSpeed();

                return ResponseEntity.status(HttpStatus.OK).body("Settings Updated!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Please provide all required fields: playerLimit, maxAns, pickRange, speed.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/getsetting")
    ResponseEntity<String> getSetting(@RequestBody GameSetting gameSetting){
        GameHandler.logger.info(gameSetting.toString());
        try{
            if(gameSetting.getKey().equals(env.settingKey)){
                GameSetting gameSetting1 = new GameSetting(
                        GameHandler.playerLimitForRoom,
                        GameHandler.maxAns,
                        player.pickRange,
                        player.speed
                );
                return ResponseEntity.status(HttpStatus.OK).body(JsonUtil.toJson(gameSetting1));
            }else{
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("unauthorized");
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/getgame")
    public ResponseEntity<String> getGame(@RequestBody GameSetting gameSetting) {
        try {
            if (gameSetting.getKey().equals(env.settingKey)) {


                Map<String, Map<String, WebSocketSession>> rooms = gameHandler.rooms;

                List<pair<String, List<pair<String, String>>>> roomList = new ArrayList<>();

                for (Map.Entry<String, Map<String, WebSocketSession>> roomEntry : rooms.entrySet()) {
                    String roomId = roomEntry.getKey();
                    Map<String, WebSocketSession> players = roomEntry.getValue();

                    List<pair<String, String>> playerList = new ArrayList<>();

                    for (Map.Entry<String, WebSocketSession> playerEntry : players.entrySet()) {
                        String playerId = playerEntry.getKey();
                        WebSocketSession session = playerEntry.getValue();

                        playerList.add(new pair<>(gameHandler.idPlayerMap.get(playerId).getName(),
                                session.getId()));
                    }

                    roomList.add(new pair<>(roomId, playerList));
                }

                return ResponseEntity.status(HttpStatus.OK).body(JsonUtil.toJson(roomList));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/sendnotification")
    public ResponseEntity<String> sendNotification(@RequestBody Notification notification) {
        try {
            if (notification.getKey().equals(env.settingKey)) {
                if(gameHandler.sendNotification(notification)){
                    return ResponseEntity.status(HttpStatus.OK).body("Notification  Sent ");
                }else{
                    return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Failed to send.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
