package com.geniusgarden.server.Model;

import com.geniusgarden.server.Service.GameHandler;
import com.geniusgarden.server.Service.JsonUtil;
import com.geniusgarden.server.Service.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class player {
    public static float pickRange = 8f;
    private static float speed = 0.3f;
    private String name;
    private String SocketId;
    private String roomId;
    private int ratCnt;
    private vector3 topRight;
    private vector3 bottomLeft;
    private vector3 movement;
    private vector3 currentPos;
    private String answer;
    private String question;

//    @Autowired
    private GameHandler gamehandler;

    private ratContainer ratcontainer;
    private boolean active = true;
    public player(String name, String roomId, String SocketId, int i, vector3 iniPos, vector3 topRight, vector3 bottomLeft, vector3 vector3, ratContainer ratcontainer, String answer, String question) {
        this.name = name;
        this.roomId = roomId;
        this.SocketId = SocketId;
        this.currentPos = iniPos;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.ratcontainer = ratcontainer;
        this.answer = answer;
        this.question = question;
    }

    public void Setup(){
        Thread updateThread = new Thread(() -> {
            while (active) {
                Update();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updateThread.start();
    }

    public void Update() {
        vector3 newPosition = new vector3(
                currentPos.getX() + movement.getX(),
                currentPos.getY() + movement.getY(),
                0.5f
        );

        if (newPosition.getX() > bottomLeft.getX() && newPosition.getX() < topRight.getX()
                && newPosition.getY() > bottomLeft.getY() && newPosition.getY() < topRight.getY()) {
            currentPos.setPosition(newPosition.getX(), newPosition.getY());
        } else if(this.ratCnt!=0){
//            gamehandler.sendMessageFromServer("removeRat");
            ratcontainer.disappearRat(this.answer);
            this.ratCnt=Math.max(0,ratCnt-1);
            gamehandler.removeRat(this.SocketId,this.roomId,this.answer);
        }

        for (Map.Entry<String, vector3> it : ratcontainer.getRats().entrySet()) {
//            GameHandler.logger.info(String.valueOf(Util.calculateDistance(currentPos, it.getValue())));
            if (Util.calculateDistance(currentPos, it.getValue()) <= pickRange) {
                if (it.getKey().equals(answer)) {
                    this.ratcontainer.disappearRat(answer);
                    gamehandler.addRat(this.SocketId,this.roomId,this.answer);
                } else {
                    ratcontainer.disappearRat(it.getKey());
                    this.ratCnt=Math.max(0,ratCnt-1);
                    gamehandler.removeRat(this.SocketId,this.roomId,this.answer);
                }
            }
        }

        payLoad pl = new payLoad();
        pl.setType("position");
        pl.setPosition(currentPos.getList());
        pl.setSocketId(this.SocketId);
        pl.setRotation(0f);
        try {
            gamehandler.broadcastMessage(roomId, JsonUtil.toJson(pl));
        }catch(Exception e){
            GameHandler.logger.info(e.getMessage());
        }
//        gamehandler.logger.info("invking the update");
    }

    public void Input(List<Float> position) {
        this.movement.setX(position.get(0) * speed);
        this.movement.setY(position.get(1) * speed);
    }

}
