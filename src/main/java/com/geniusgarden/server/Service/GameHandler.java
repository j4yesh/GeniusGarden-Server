package com.geniusgarden.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geniusgarden.server.Model.payLoad;
import com.geniusgarden.server.Model.player;
import com.geniusgarden.server.Model.result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class GameHandler extends TextWebSocketHandler {

//    private static final Map<String, WebSocketSession> sessions = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GameHandler.class);
    private static final Map<String, Map<String,WebSocketSession>> rooms = new HashMap<>();
    private static final Map<String, player> idPlayerMap = new HashMap<>();
    private final List<Float> spawnPosition = Arrays.asList(-7.0f, 2.0f, 0.0f);
    private static int playerLimitForRoom = 3;
    private static int maxAns = 5;
    private static float arenaSide = 38f;


    @Autowired
    questionMaker questionMaker;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String conType = getRequestType(session);
        String roomId = getRoomId(session);
        logger.info(conType + ": connection type.");
        if ( !rooms.containsKey(roomId) && conType.equals("join")) {
            payLoad errorPl = new payLoad();
            errorPl.setType("Error");
            errorPl.setData("Enter valid room id");

            sendMessageToClientWithoutRoom(session,JsonUtil.toJson(errorPl));
            session.close();
            return;
        }
        if(rooms.containsKey(roomId) && conType.equals("host")){
            payLoad errorPl = new payLoad();
            errorPl.setType("Error");
            errorPl.setData("please try again to host.");
            sendMessageToClientWithoutRoom(session,JsonUtil.toJson(errorPl));
            session.close();
            return;
        }
        if(rooms.containsKey(roomId) && rooms.get(roomId).size()>playerLimitForRoom){
            payLoad errorPl = new payLoad();
            errorPl.setType("Error");
            errorPl.setData("Room is full. (Room limit is 3).");
            sendMessageToClientWithoutRoom(session,JsonUtil.toJson(errorPl));
            session.close();
            return;
        }

        logger.info("New connection to room: " + roomId);
        rooms.computeIfAbsent(roomId, k -> new HashMap<>()).put(session.getId(),session);
        logger.info("sizing : "+rooms.entrySet().size());


//        rooms.put(new HashMap<>(roomId,new HashMap<>()));
        Map<String,WebSocketSession> sessions = rooms.get(roomId);

        sessions.put(session.getId(), session);

        payLoad pl = new payLoad();
        pl.setSocketId(session.getId());
        pl.setType("new player");


        float halfArenaSide = arenaSide/2;
        Float randX = questionMaker.random.nextFloat(arenaSide) - halfArenaSide;
        Float randY = questionMaker.random.nextFloat(arenaSide) - halfArenaSide;


        pl.setPosition(Arrays.asList(randX, randY, 0.0f));


        pl.setName(session.getId());
        this.spawnPosition.set(0,this.spawnPosition.get(0) + 2f);
        pl.setRotation(0f);

        logger.info("new player joined "+session.getId());

        broadcastMessage(session, JsonUtil.toJson(pl));

        pl.setType("self id");
        pl.setSocketId(session.getId());
        pl.setName("You");
        sendMessageToClient(roomId,session.getId(),JsonUtil.toJson(pl));
        for(Map.Entry<String,WebSocketSession> it: sessions.entrySet()){
            String key = it.getKey();
            WebSocketSession value = it.getValue();
            if(key != session.getId()){
                payLoad p = new payLoad();
                p.setSocketId(key);
                p.setType("new player");
                p.setPosition(Arrays.asList(0f,0f,0f));
                p.setName(it.getValue().getId());
                p.setRotation(0f);
                sendMessageToClient(roomId,session.getId(), JsonUtil.toJson(p));
            }
        }

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String roomId = getRoomId(session);

//        logger.info("Message received from room " + roomId + ": " + message.getPayload());
        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);

        String payload = (String) message.getPayload();

        ObjectMapper objectMapper = new ObjectMapper();

        payLoad pl = objectMapper.readValue(payload, payLoad.class);

        switch (pl.getType()) {
            case "position" -> broadcastMessage(session, payload);
            case "addRat" -> {
//            logger.info("Received payload: " + payload);
                player p = idPlayerMap.get(pl.getSocketId());
                p.setRatCnt(p.getRatCnt() + 1);
                List<player> playersWithinRoom = getRankList(roomId);
                if (p.getRatCnt() == maxAns) {

                    for(int i=0;i<playersWithinRoom.size();i++){
                        result r = new result();
                        r.setName(playersWithinRoom.get(i).getName());
                        r.setSocketId(playersWithinRoom.get(i).getSocketId());
                        r.setRank(playersWithinRoom.size()-i);
                        for(player p1 : playersWithinRoom){
                            r.addRank(p1.getName());
                        }

                        String resultString = JsonUtil.toJson(r);
                        logger.info("p1: "+resultString);
                        payLoad pl1 = new payLoad();
                        pl1.setType("result");
                        pl1.setSocketId(r.getSocketId());
                        pl1.setData(resultString);

                        logger.info(pl1.toString());
                        sendMessageToClient(roomId,r.getSocketId(),JsonUtil.toJson(pl1));
                    }
//                    rooms.remove(roomId);
                    return;
                }

                payLoad pl1 = new payLoad();
                pl1.setType("addRat");
                pl1.setQuestion(session.getId());
                pl1.setAnswer(pl.getAnswer());
                pl1.setData(JsonUtil.toJson(playersWithinRoom));

                broadcastMessage(roomId, JsonUtil.toJson(pl1));
            }
            case "startGame" -> {
                payLoad pl1 = new payLoad();
                pl1.setType("startGame");
//            Map<String,WebSocketSession> refRoom = rooms.get(roomId);
//            for(Map.Entry<String,WebSocketSession> it: refRoom.entrySet()){
//                sendMessageToClient(roomId,it.getValue().getId(),JsonUtil.toJson(pl1));
//            }
                broadcastMessage(roomId, JsonUtil.toJson(pl1));
            }
            case "setName" -> {
                logger.info("received a setName call with name : " + pl.getData());
                payLoad pl1 = new payLoad();
                pl1.setType("setName");
                pl1.setSocketId(session.getId());
                pl1.setData(pl.getData());
                broadcastMessage(roomId, JsonUtil.toJson(pl1));

                for (Map.Entry<String, player> it : idPlayerMap.entrySet()) {
                    payLoad pl2 = new payLoad();
                    pl2.setType("setName");
                    pl2.setSocketId(it.getKey());
                    pl2.setData(it.getValue().getName());
                    sendMessageToClient(roomId, session.getId(), JsonUtil.toJson(pl2));
                }

                idPlayerMap.put(session.getId(), new player(pl.getData(), roomId, session.getId(), 0));
            }
            case "removeRat"->{
                player p = idPlayerMap.get(session.getId());
                p.setRatCnt(Math.max(0,p.getRatCnt()-1));

                payLoad pl2 = new payLoad();
                pl2.setType("removeRat");
                pl2.setSocketId(session.getId());

                List<String> sortedRank = getRankListStr(roomId);
                pl2.setData(JsonUtil.toJson(sortedRank));

                broadcastMessage(roomId,JsonUtil.toJson(pl2));
            }
        }

    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        Map<String,WebSocketSession> sessions = rooms.get(roomId);;
        logger.info("The player is leaving : "+session.getId());

        sessions.remove(session.getId());

        rooms.put(roomId, sessions);

        if(sessions.isEmpty()){
            rooms.remove(roomId);
            idPlayerMap.remove(session.getId());
            logger.info("The room is closed with id : "+roomId);
            return ;
        }

        synchronized (this.spawnPosition) {
            this.spawnPosition.set(0, this.spawnPosition.get(0) - 2);
        }

        payLoad pl = new payLoad();
        pl.setSocketId(session.getId());
        pl.setType("leave player");
        pl.setName(idPlayerMap.get(session.getId()).getName());
        idPlayerMap.remove(session.getId());

        logger.info("player left: "+session.getId());

        broadcastMessage(roomId,JsonUtil.toJson(pl));

    }

    private void broadcastMessage(WebSocketSession senderSession, String message) {
        String roomId = getRoomId(senderSession);
        Map<String,WebSocketSession> sessions = rooms.get(roomId);

        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen() && !session.getId().equals(senderSession.getId())) {
                try {
                    session.sendMessage(textMessage);
                } catch (Exception e) {
                    System.err.println("Error sending message to session ID: " + session.getId());
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessageToClientWithoutRoom(WebSocketSession session, String message) {
        String roomId = getRoomId(session);

        TextMessage textMessage = new TextMessage(message);
        try {
            session.sendMessage(textMessage);
        } catch (Exception e) {
            System.err.println("Error sending message to session ID: " + session.getId());
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String roomId,String message) {
        Map<String, WebSocketSession> sessions = rooms.get(roomId);

        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions.values()) {
                try {
                    session.sendMessage(textMessage);
                } catch (Exception e) {
                    System.err.println("Error sending message to session ID: " + session.getId());
                    e.printStackTrace();

                }
        }
    }

    private void sendMessageToClient(String roomId,String sessionId, String message) {
        Map<String,WebSocketSession> sessions = rooms.get(roomId);
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                TextMessage textMessage = new TextMessage(message);
                session.sendMessage(textMessage);
//                logger.info("Message sent to session ID: " + sessionId);
            } catch (Exception e) {
                logger.warn("Error sending message to session ID: " + sessionId);
                e.printStackTrace();
            }
        } else {
            logger.warn("Session ID: " + sessionId + " not found or not open.");
        }
    }

    @Scheduled(fixedRate = 5000)
    public void spawnRat() {
//        logger.info("sending the spawn rat message1" + rooms.entrySet().size());

        for (Map.Entry<String, Map<String, WebSocketSession>> sessions : rooms.entrySet()) {
//            logger.info("sending the spawn rat message2");

            List<String> question = new ArrayList<>();
            List<String> answer = new ArrayList<>();
            int n = sessions.getValue().size();
            String roomId = sessions.getKey();

            questionMaker.makeQuestion(question, answer, n);
            int idx = 0;
            Set<String> ansValid = new HashSet<>();

            for (Map.Entry<String, WebSocketSession> it : sessions.getValue().entrySet()) {
                WebSocketSession session = it.getValue();
//                logger.info("sending the spawn rat message3");
                if (session.isOpen()) {
                    payLoad p = new payLoad();
                    p.setSocketId(session.getId());
                    p.setType("spawn rat");

                    float halfArenaSide = arenaSide/2;
                    Float randX = questionMaker.random.nextFloat(arenaSide) - halfArenaSide;
                    Float randY = questionMaker.random.nextFloat(arenaSide) - halfArenaSide;

                    p.setPosition(Arrays.asList(randX, randY, 0f));
                    p.setQuestion(question.get(idx));
                    p.setAnswer(answer.get(idx));
                    ansValid.add(answer.get(idx));

                    try {
                        broadcastMessage(roomId, JsonUtil.toJson(p));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    idx++;
                }
            }
            for (int i = 0; i < 3; i++) {
                String num = questionMaker.operand.get(questionMaker.random.nextInt(10));
                if (!ansValid.contains(num)) {
                    payLoad p = new payLoad();
                    p.setType("dummy rat");

                    float halfArenaSide = arenaSide/2;
                    Float randX = questionMaker.random.nextFloat(arenaSide) - halfArenaSide;
                    Float randY = questionMaker.random.nextFloat(arenaSide) - halfArenaSide;

                    p.setPosition(Arrays.asList(randX, randY, 0.0f));

                    p.setAnswer(num);
                    try {
                        broadcastMessage(roomId, JsonUtil.toJson(p));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getRoomId(WebSocketSession session) {
        String uri = Objects.requireNonNull(session.getUri()).toString();
        return uri.split("/")[4];
    }

    private String getRequestType(WebSocketSession session) {
        String uri = Objects.requireNonNull(session.getUri()).toString();
        String[] tempList = uri.split("/");
        for(String it: tempList){
            logger.info(it);
        }
        return uri.split("/")[5];

    }

    private List<player> getRankList(String roomId){
        List<player> playersWithinRoom = new ArrayList<>();
        for(Map.Entry<String,WebSocketSession> it : rooms.get(roomId).entrySet()){
            player p1 = idPlayerMap.get(it.getValue().getId());
            playersWithinRoom.add(p1);
        }
        playersWithinRoom.sort(new Comparator<player>() {
            @Override
            public int compare(player o1, player o2) {
                return (o1.getRatCnt()>o2.getRatCnt())?1:0;
            }
        });
        return playersWithinRoom;
    }



    private List<String> getRankListStr(String roomId){
        List<player> playersWithinRoom = getRankList(roomId);
            List<String> strPlayer = new ArrayList<>();
            for(player it: playersWithinRoom){
                strPlayer.add(it.getName());
            }
        return strPlayer;
    }

}
