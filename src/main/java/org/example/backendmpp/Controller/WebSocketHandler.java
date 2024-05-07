package org.example.backendmpp.Controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        // Optionally, you can send an initial message upon connection.
        session.sendMessage(new TextMessage("Connected to updates WebSocket."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages (if any)
    }

    public void sendUpdateToAllClients(String updateMessage) throws IOException {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(updateMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}