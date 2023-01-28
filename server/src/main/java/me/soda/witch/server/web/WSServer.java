package me.soda.witch.server.web;
//
//import com.google.gson.JsonParseException;
//import me.soda.witch.server.data.ConnectionEventData;
//import me.soda.witch.server.data.ConnectionInfo;
//import me.soda.witch.server.data.ConnectionOperationData;
//import me.soda.witch.server.data.IndexedMessageData;
//import me.soda.witch.server.server.Server;
//import me.soda.witch.shared.socket.Connection;
//import me.soda.witch.shared.socket.messages.Message;
//import me.soda.witch.shared.socket.messages.messages.DisconnectData;
//import me.soda.witch.shared.socket.messages.messages.StringsData;
//import org.java_websocket.WebSocket;
//import org.java_websocket.handshake.ClientHandshake;
//import org.java_websocket.server.WebSocketServer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.List;
//
//public class WSServer extends WebSocketServer {
//    private static final Logger LOGGER = LoggerFactory.getLogger(WSServer.class);
//
//    static {
//        Message.registerMessage(100, ConnectionOperationData.class);
//        Message.registerMessage(101, ConnectionInfo.class);
//        Message.registerMessage(102, ConnectionEventData.class);
//        Message.registerMessage(103, IndexedMessageData.class);
//    }
//
//    public final List<WebSocket> authorizedConnections = new ArrayList<>();
//    private final Server server;
//
//    public WSServer(int port, Server server) {
//        super(new InetSocketAddress(port));
//        this.server = server;
//    }
//
//    @Override
//    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//    }
//
//    @Override
//    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        authorizedConnections.remove(conn);
//    }
//
//    @Override
//    public void onMessage(WebSocket conn, String message) {
//        String ip = conn.getRemoteSocketAddress().toString();
//        try {
//            Message msg = Message.fromJson(message);
//            if (server.config.auth && !authorizedConnections.contains(conn)) {
//                if (msg.data instanceof StringsData data
//                        && data.id().equals("auth")
//                        && data.data().length > 0
//                        && data.data()[0].equals(server.config.accessCode)) {
//                    LOGGER.info("Connection Authorized: {}", ip);
//                    authorizedConnections.add(conn);
//                } else {
//                    LOGGER.info("Connection unauthorized: {}", ip);
//                    conn.close();
//                    return;
//                }
//            }
//
//            if (msg.data instanceof ConnectionOperationData data) {
//                List<Connection> conns = getConns(data.clientIDs);
//                switch (data.operation) {
//                    case LIST -> {
//                        List<Message> msgs = new ArrayList<>();
//                        server.getConnections().forEach(conne -> msgs.add(new Message(server.clientMap.get(conne))));
//                        conn.send(Message.fromList("connection_list", msgs).toString());
//                    }
//                    case RECONNECT -> conns.forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT));
//                    case DISCONNECT -> conns.forEach(connection -> connection.close(DisconnectData.Reason.NOREC));
//                    case SEND -> conns.forEach(connection -> connection.send(data.message));
//                }
//            }
//        } catch (JsonParseException | UnsupportedOperationException ignored) {
//        } catch (Exception e) {
//            conn.send(Message.fromString("internal_exception", e.getMessage()).toString());
//            LOGGER.error(e.getMessage());
//        }
//    }
//
//    @Override
//    public void onError(WebSocket conn, Exception ex) {
//    }
//
//    @Override
//    public void onStart() {
//        LOGGER.info("Server started on {}.", getPort());
//    }
//
//    private List<Connection> getConns(List<Integer> ids) {
//        return server.getConnections().stream().filter(conne -> ids.contains(server.clientMap.get(conne).id)).toList();
//    }
//}
//