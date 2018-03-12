import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import static spark.Spark.*;

import static j2html.TagCreator.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1;

    public static void main(String[] args) {
        staticFiles.location("/public");
        staticFiles.expireTime(600);
        exception(Exception.class, (e, req, res) -> {
            System.err.println("Exception happened");
            e.printStackTrace();
        });
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }

    public static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(
                        new JSONObject()
                        .put("message", createHtmlMessageFromSender(sender, message))
                        .put("userList", userUsernameMap.values())
                        .toString()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String createHtmlMessageFromSender(String sender, String message) {
        return article()
                .with(
                        b(sender + " said:"),
                        p(message),
                        span()
                            .withClass("timestamp")
                            .withText(new SimpleDateFormat("HH:mm:ss")
                                        .format(new Date()))
                ).render();
    }
}
