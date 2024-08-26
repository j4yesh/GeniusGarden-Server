package com.geniusgarden.server.Service;

import com.geniusgarden.server.Model.payLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Map<String, WebSocketSession> sessions = new HashMap<>();

    private final List<Float> spawnPosition = Arrays.asList(-7.0f, 2.0f, 0.0f);

    @Autowired
    questionMaker questionMaker;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);

        payLoad pl = new payLoad();
        pl.setSocketId(session.getId());
        pl.setType("new player");
        pl.setPosition(spawnPosition);
        this.spawnPosition.set(0,this.spawnPosition.get(0) + 2f);
        pl.setRotation(0f);

        System.out.println("new player joined "+session.getId());

        broadcastMessage(session, JsonUtil.toJson(pl));

        pl.setType("self id");

        sendMessageToClient(session.getId(),JsonUtil.toJson(pl));
        for(Map.Entry<String,WebSocketSession> it: sessions.entrySet()){
            String key = it.getKey();
            WebSocketSession value = it.getValue();
            if(key != session.getId()){
                payLoad p = new payLoad();
                p.setSocketId(key);
                p.setType("new player");
                p.setPosition(Arrays.asList(0f,0f,0f));
                p.setRotation(0f);
                sendMessageToClient(session.getId(), JsonUtil.toJson(p));
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
//        System.out.println("Received payload: " + payload);
        broadcastMessage(session, payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        payLoad pl = new payLoad();
        pl.setSocketId(session.getId());
        pl.setType("leave player");
        this.spawnPosition.set(0,this.spawnPosition.get(0) - 2);

        System.out.println("player left: "+session.getId());

        broadcastMessage(session, JsonUtil.toJson(pl));
    }

    private void broadcastMessage(WebSocketSession senderSession, String message) {
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
    private void broadcastMessage(String message) {
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

    private void sendMessageToClient(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                TextMessage textMessage = new TextMessage(message);
                session.sendMessage(textMessage);
                System.out.println("Message sent to session ID: " + sessionId);
            } catch (Exception e) {
                System.err.println("Error sending message to session ID: " + sessionId);
                e.printStackTrace();

            }
        } else {
            System.err.println("Session ID: " + sessionId + " not found or not open.");
        }
    }

    @Scheduled(fixedRate = 5000)
    private void spawnRat(){
        List<String>question=new ArrayList<String>();
        List<String>answer=new ArrayList<String>();
        int n=sessions.size();
        questionMaker.makeQuestion(question,answer,n);
        int idx=0;
        Set<String> ansValid = new HashSet<String>();
        for(Map.Entry<String,WebSocketSession> it: sessions.entrySet()){
            payLoad p = new payLoad();
            p.setSocketId(it.getValue().getId());
            p.setType("spawn rat");

            Float randX=questionMaker.random.nextFloat(18f)-9f; //(-9,9)
            Float randY=questionMaker.random.nextFloat(18f)-9f;

            p.setPosition(Arrays.asList(randX,randY,0f));
            p.setQuestion(question.get(idx));
            p.setAnswer(answer.get(idx));
            ansValid.add(answer.get(idx));
            broadcastMessage(JsonUtil.toJson(p));

            idx++;
        }
        for(int i=0;i<3;i++){
            String num = questionMaker.operand.get(questionMaker.random.nextInt(10));
            if(!ansValid.contains(num)) {

                payLoad p = new payLoad();
                p.setType("dummy rat");

                float randX = questionMaker.random.nextFloat(18f) - 9f; //(-9,9)
                float randY = questionMaker.random.nextFloat(18f) - 9f;

                p.setPosition(Arrays.asList(randX, randY, 0f));
                p.setAnswer(num);

                broadcastMessage( JsonUtil.toJson(p));
            }
        }
    }

}
