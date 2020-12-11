package CAFServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import javax.websocket.server.ServerContainer;

public class CAFServer {
    private static final int PORT = 8095;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startWebSocketServer();
    }

    // Start the web socket server
    private static void startWebSocketServer() {

        Server webSocketServer = new Server();
        webSocketServer.setStopTimeout(10000);
        ServerConnector connector = new ServerConnector(webSocketServer);
        connector.setPort(PORT);
        connector.setIdleTimeout(45000);

        webSocketServer.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler webSocketContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        webSocketContext.setContextPath("/");
        webSocketServer.setHandler(webSocketContext);

        ServletHolder defHolder = new ServletHolder("default", DefaultServlet.class);
        defHolder.setInitParameter("idleTimeout", "44000");
        webSocketContext.addServlet(defHolder, "/");

        try {
            // Initialize javax.websocket layer
            ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(webSocketContext);

            // Add WebSocket endpoint to javax.websocket layer
            wsContainer.addEndpoint(CAFServerWebSocket.class);

            webSocketServer.start();
            //server.dump(System.err);

            webSocketServer.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
