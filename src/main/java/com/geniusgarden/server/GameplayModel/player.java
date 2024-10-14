package com.geniusgarden.server.GameplayModel;

import com.geniusgarden.server.Service.GameHandler;
import com.geniusgarden.server.Service.JsonUtil;
import com.geniusgarden.server.Service.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class player {
    public static float pickRange = 5f;
    public static float speed = 0.2f;
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
    private String type;
    private int correct;
    private int wrong;

//    @Autowired
    private GameHandler gamehandler;

    private ratContainer ratcontainer;
    private boolean active = true;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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

    public void Setup() {
        GameHandler.logger.info(" settup begin..");
        executorService.submit(this::updateLoop);
    }

    private void updateLoop() {
        while (active) {
            Update();
            try {
                Thread.sleep(20); // Consider using a constant for this magic number
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void Update() {
        vector3 newPosition = new vector3(
                currentPos.getX() + movement.getX(),
                currentPos.getY() + movement.getY()
        );

        if (newPosition.getX() > bottomLeft.getX() && newPosition.getX() < topRight.getX()
                && newPosition.getY() > bottomLeft.getY() && newPosition.getY() < topRight.getY()) {
            currentPos.setPosition(newPosition.getX(), newPosition.getY());
        } else if(this.ratCnt!=0){
            gamehandler.sendMessageFromServer("removeRat");
            this.ratCnt=Math.max(0,ratCnt-1);
            this.wrong++;
//            gamehandler.removeRatdisappearRat(this.answer);
            gamehandler.removeRat(this.SocketId,this.roomId,this.answer);
        }

        try {
            for (Map.Entry<String, vector3> it : ratcontainer.getRats().entrySet()) {
//            GameHandler.logger.info(String.valueOf(Util.calculateDistance(currentPos, it.getValue())));
                if (Util.calculateDistance(currentPos, it.getValue()) < pickRange) {
                    gamehandler.removeRatFromArena(this.SocketId, this.roomId, it.getKey());
                    if (!it.getKey().equals(answer)) {
//                    GameHandler.logger.info("wrong answer hit");
                        this.ratCnt = Math.max(0, ratCnt - 1);
                        this.wrong++;
                        gamehandler.removeRat(this.SocketId, this.roomId, this.answer);
                        GameHandler.logger.info(it.getKey() + " "+ this.answer + "wrong");
                    } else {
                        this.ratCnt++;
                        this.correct++;
                        gamehandler.addRat(this.SocketId, this.roomId, this.answer);
                        GameHandler.logger.info(it.getKey() + " "+ this.answer + "correct");
                    }
                    ratcontainer.disappearRat(it.getKey());
                }
            }
        }catch(Exception e){
            GameHandler.logger.info(e.getMessage());
        }

        payLoad pl = new payLoad();
        pl.setType("position");
        pl.setPosition(currentPos.getList());
        pl.setSocketId(this.SocketId);

        float angle = (float) Math.atan2(movement.getY(), movement.getX());
        float angleInDegrees = (float) (angle * (180.0 / Math.PI));
        pl.setRotation(angleInDegrees);

        try {
            gamehandler.broadcastMessage(roomId, JsonUtil.toJson(pl));
        }catch(Exception e){
            GameHandler.logger.info(e.getMessage());
        }
//        gamehandler.logger.info("invking the update");
    }

    public void Input(List<Float> position) {
        float x = position.get(0);
        float y = position.get(1);
        float magnitude = (float) Math.sqrt(x * x + y * y);

        if (magnitude > 0) {
            float normalizedX = x / magnitude;
            float normalizedY = y / magnitude;

            this.movement.setX(normalizedX * speed);
            this.movement.setY(normalizedY * speed);
        } else {
            this.movement.setX(0);
            this.movement.setY(0);
        }
    }


}
