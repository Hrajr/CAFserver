package CAFServer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

@WebSocket(maxIdleTime=50000)
@ServerEndpoint(value="/communicator/")
public class CAFServerWebSocket {

    private CAFServerWebSocketMessageHandler messageHandler = new CAFServerWebSocketMessageHandler();

    // All sessions
    public static final List<Session> sessions = new ArrayList<>();

    @OnOpen
    public void onConnect(Session session) {
        System.out.println("[WebSocket Connected] SessionID: " + session.getId());
        String message = String.format("[New client with client side session ID]: %s", session.getId());
        sessions.add(session);
        System.out.println("[#sessions]: " + sessions.size());
    }

    @OnMessage
    public void onText(String message, Session session) {
        System.out.println("[WebSocket Session ID] : " + session.getId() + " [Received] : " + message);
        handleMessageFromClient(message, session);
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        System.out.println("[WebSocket Session ID] : " + session.getId() + " [Socket Closed]: " + reason);
        sessions.remove(session);

        messageHandler.disconnectClient(session);
    }

    @OnError
    public void onError(Throwable cause, Session session) {
        System.out.println("[WebSocket Session ID] : " + session.getId() + "[ERROR]: ");
        cause.printStackTrace(System.err);
    }

    // Handle incoming message from client
    private void handleMessageFromClient(String jsonMessage, Session session) {
        Gson gson = new Gson();
        CAFWebSocketMessage wbMessage = null;
        try {
            wbMessage = gson.fromJson(jsonMessage, CAFWebSocketMessage.class);
        } catch (JsonSyntaxException ex) {
            System.out.println("[WebSocket ERROR: cannot parse Json message " + jsonMessage);
            return;
        }

        messageHandler.handleMessage(wbMessage, session);
    }




}
