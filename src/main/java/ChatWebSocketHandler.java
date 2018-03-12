import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, message;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User " + Main.nextUserNumber;
        Main.nextUserNumber++;
        System.out.println(username + " joined");
        Main.userUsernameMap.put(user, username);
        Main.broadcastMessage( sender = "Server", message = (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onDisconnect(Session user, int statusCode, String reason) {
        String username = Main.userUsernameMap.get(user);
        System.out.println(username + " left");
        Main.userUsernameMap.remove(user);
        Main.broadcastMessage(sender = "Server", message = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println(Main.userUsernameMap.get(user) + " sent " + message);
        Main.broadcastMessage(
                sender = Main.userUsernameMap.get(user),
                message
        );
    }
}
