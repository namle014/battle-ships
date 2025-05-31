import battleships.GameResult;
import battleships.Ship;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import common.Network.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ServerApp {
    private Server server;
    private HashMap<String, RoomInfo> rooms = new HashMap<>();
    private HashMap<Connection, PlayerInfo> players = new HashMap<>();

    public ServerApp() throws IOException {
        server = new Server();
        registerClasses();

        server.start();
        server.bind(54555, 54777); // TCP 54555, UDP 54777

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Client connected: " + connection.getID());
            }

            @Override
            public void disconnected(Connection connection) {
                PlayerInfo player = players.remove(connection);
                if (player != null) {
                    removePlayerFromRoom(player, connection);
                    broadcastRoomList();
                }
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LoginRequest) {
                    handleLogin(connection, (LoginRequest) object);
                } else if (object instanceof CreateRoomRequest) {
                    handleCreateRoom(connection);
                } else if (object instanceof JoinRoomRequest) {
                    handleJoinRoom(connection, (JoinRoomRequest) object);
                } else if (object instanceof ChatMessage) {
                    handleChat(connection, (ChatMessage) object);
                } else if (object instanceof LeaveRoomRequest) {
                    handleLeaveRoom(connection);
                } else if (object instanceof RoomListRequest) {
                    handleRoomListRequest(connection);
                } else if (object instanceof ReadyRequest) {
                    handleReadyRequest(connection, (ReadyRequest) object);
                } else if (object instanceof AttackRequest) {
                    handleAttackRequest(connection, (AttackRequest) object);
                }
            }
        });
    }

    private void registerClasses() {
        server.getKryo().register(LoginRequest.class);
        server.getKryo().register(LoginResponse.class);
        server.getKryo().register(PlayerInfo.class);
        server.getKryo().register(CreateRoomRequest.class);
        server.getKryo().register(RoomCreatedResponse.class);
        server.getKryo().register(JoinRoomRequest.class);
        server.getKryo().register(RoomListRequest.class);
        server.getKryo().register(RoomListResponse.class);
        server.getKryo().register(RoomInfo.class);
        server.getKryo().register(RoomUpdate.class);
        server.getKryo().register(ChatMessage.class);
        server.getKryo().register(LeaveRoomRequest.class);
        server.getKryo().register(ArrayList.class);
        server.getKryo().register(ReadyRequest.class);
        server.getKryo().register(ReadyResponse.class);
        server.getKryo().register(Ship.class);
        server.getKryo().register(AttackRequest.class);
        server.getKryo().register(AttackResponse.class);
        server.getKryo().register(GameResult.class);
    }

    private void handleLogin(Connection connection, LoginRequest request) {
        PlayerInfo player = new PlayerInfo();
        player.username = request.username;
        player.level = request.level;
        players.put(connection, player);

        LoginResponse response = new LoginResponse();
        response.success = true;
        response.message = "Logged in successfully";
        response.playerInfo = player;
        connection.sendTCP(response);
    }

    private void handleCreateRoom(Connection connection) {
        String roomId = String.format("%04d", new Random().nextInt(10000));
        RoomInfo room = new RoomInfo();
        room.roomId = roomId;
        room.players.add(players.get(connection));
        rooms.put(roomId, room);

        RoomCreatedResponse response = new RoomCreatedResponse();
        response.roomId = roomId;
        connection.sendTCP(response);

        updateRoom(connection, room, null);
        broadcastRoomList();
    }

    private void handleJoinRoom(Connection connection, JoinRoomRequest request) {
        PlayerInfo opponent = new PlayerInfo();
        RoomInfo room = rooms.get(request.roomId);
        System.out.println("Room " + room);
        if (room != null && room.players.size() < 2) {
            room.players.add(players.get(connection));
            for (Connection c : server.getConnections()) {
                if (c != connection && room.players.contains(players.get(c))) {
                    System.out.println("Player " + players.get(c) + players.get(connection) + " joined the room");
                    opponent = players.get(c);
                    updateRoom(c, room, players.get(connection));
                }
            }
            updateRoom(connection, room, opponent);
            broadcastRoomList();
        }
    }

    private void handleLeaveRoom(Connection connection) {
        PlayerInfo player = players.get(connection);
        if (player != null) {
            removePlayerFromRoom(player, connection);
            broadcastRoomList();
        }
    }

    private void handleChat(Connection connection, ChatMessage chat) {
        RoomInfo room = findRoomByPlayer(players.get(connection));
        if (room != null) {
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c))) {
                    c.sendTCP(chat);
                }
            }
        }
    }

    private void handleRoomListRequest(Connection connection) {
        RoomListResponse response = new RoomListResponse();
        response.rooms.addAll(rooms.values()); // Thêm tất cả phòng hiện có
        connection.sendTCP(response);
    }

    private void handleReadyRequest(Connection connection, ReadyRequest request) {
        ReadyResponse response = new ReadyResponse();
        response.startGame = true;
        PlayerInfo player = players.get(connection);
        RoomInfo room = findRoomByPlayer(player);
        if (player.equals(room.players.get(0))) {
            room.Player1ships = request.ships;
        } else {
            room.Player2ships = request.ships;
        }

        if (player != null) { player.isReady = request.isReady; }
        if (room != null) {
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c))) {
                    response.startGame = (response.startGame && players.get(c).isReady);
                }
            }
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c))) {
                    response.yourTurn = (players.get(c).equals(room.players.get(0)) ? true : false);
                    if (response.startGame) {
                        if (response.yourTurn) response.enemyShips = room.Player2ships;
                        else response.enemyShips = room.Player1ships;
                    }
                    c.sendTCP(response);
                }
            }
        }
    }

    private void handleAttackRequest(Connection connection, AttackRequest request) {
        AttackResponse response = new AttackResponse();
        response.col = request.col;
        response.row = request.row;
        response.success = request.success;
        response.endGame = request.endGame;
        response.result = request.result;
        response.shipsunk = request.shipsunk;

        PlayerInfo player = players.get(connection);
        RoomInfo room = findRoomByPlayer(player);
        if (room != null) {
            for (Connection c : server.getConnections()) {
                if (room.players.contains(players.get(c)) && !players.get(c).equals(player)) {
                    c.sendTCP(response);
                }
            }
        }
    }

    private void updateRoom(Connection connection, RoomInfo room, PlayerInfo opponent) {
        RoomUpdate update = new RoomUpdate();
        update.players = room.players;
        update.canStart = room.players.size() == 2;
        update.opponentInfo = opponent;
        connection.sendTCP(update);
    }

    private RoomInfo findRoomByPlayer(PlayerInfo player) {
        for (RoomInfo room : rooms.values()) {
            if (room.players.contains(player)) return room;
        }
        return null;
    }

    private void removePlayerFromRoom(PlayerInfo player, Connection connection) {
        RoomInfo room = findRoomByPlayer(player);
        if (room != null) {
            room.players.remove(player);
            if (room.players.isEmpty()) {
                rooms.remove(room.roomId);
            } else {
                for (Connection c : server.getConnections()) {
                    if (room.players.contains(players.get(c))) {
                        updateRoom(c, room, null);
                    }
                }
            }
        }
    }

    private void broadcastRoomList() {
        RoomListResponse response = new RoomListResponse();
        response.rooms.addAll(rooms.values());
        for (Connection c : server.getConnections()) {
            c.sendTCP(response); // Gửi tới tất cả client
        }
    }

    public static void main(String[] args) throws IOException {
        new ServerApp();
        System.out.println("Server started.");
    }
}