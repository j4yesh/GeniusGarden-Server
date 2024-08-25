package com.geniusgarden.server.Service;

import com.geniusgarden.server.Model.payLoad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new HashMap<>();

    private List<Integer> spawnPosition = Arrays.asList(-7, 2, 0);


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);

        payLoad pl = new payLoad();
        pl.setSocketId(session.getId());
        pl.setType("new player");
        pl.setPosition(spawnPosition);
        this.spawnPosition.set(0,this.spawnPosition.get(0) + 2);
        pl.setRotation(0.0);

        System.out.println("new player joined "+session.getId());
        String joinMessage = JsonUtil.toJson(pl);
        broadcastMessage(session, joinMessage);

        pl.setType("self id");
        joinMessage=JsonUtil.toJson(pl);
        sendMessageToClient(session.getId(),joinMessage);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
//        System.out.println("Received payload: " + payload);
        broadcastMessage(session, payload);  // Pass the current session to exclude it from broadcasting
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        payLoad pl = new payLoad();
        pl.setSocketId(session.getId());
        pl.setType("leave player");
        this.spawnPosition.set(0,this.spawnPosition.get(0) - 2);

        System.out.println("player left: "+session.getId());
        String joinMessage = JsonUtil.toJson(pl);
        broadcastMessage(session, joinMessage);
    }

    /**
     * Broadcast a message to all connected WebSocket clients except the sender.
     *
     * @param senderSession The session that sent the message.
     * @param message       The message to broadcast.
     */
    private void broadcastMessage(WebSocketSession senderSession, String message) {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen() && !session.getId().equals(senderSession.getId())) {
                try {
                    session.sendMessage(textMessage);
                } catch (Exception e) {
                    System.err.println("Error sending message to session ID: " + session.getId());
                    e.printStackTrace();
                    // Handle exception, possibly removing the session from the map
                }
            }
        }
    }

    public void sendMessageToClient(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                TextMessage textMessage = new TextMessage(message);
                session.sendMessage(textMessage);
                System.out.println("Message sent to session ID: " + sessionId);
            } catch (Exception e) {
                System.err.println("Error sending message to session ID: " + sessionId);
                e.printStackTrace();
                // Handle the exception, possibly remove the session from the map
            }
        } else {
            System.err.println("Session ID: " + sessionId + " not found or not open.");
        }
    }

}
