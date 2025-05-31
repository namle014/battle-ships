package client;

import battleships.*;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import common.Network.*;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private Client client;
    private PlayerInfo playerInfo;
    private RoomUpdate currentRoom;
    private String currentRoomId;
    private boolean isJoiningFromList = false;
    private WaitViewController waitModeController;
    private BoardViewController boardViewController;
    private PlayGameViewController playGameViewController;

    public NetworkManager() throws IOException {
        client = new Client();
        registerClasses();
        client.start();
        client.connect(5000, "192.168.43.15", 54555, 54777);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LoginResponse) {
                    handleLoginResponse((LoginResponse) object);
                } else if (object instanceof RoomCreatedResponse) {
                    handleRoomCreated((RoomCreatedResponse) object);
                } else if (object instanceof RoomUpdate) {
                    handleRoomUpdate((RoomUpdate) object);
                } else if (object instanceof ChatMessage) {
                    handleChat((ChatMessage) object);
                } else if (object instanceof RoomListResponse) {
                    handleRoomListResponse((RoomListResponse) object);
                } else if (object instanceof ReadyResponse) {
                    try {
                        handleReadyResponse((ReadyResponse) object);
                    } catch (IOException e) {}
                } else if (object instanceof AttackResponse) {
                    handleAttackResponse((AttackResponse) object);
                }
            }
        });
    }

    private void registerClasses() {
        client.getKryo().register(LoginRequest.class);
        client.getKryo().register(LoginResponse.class);
        client.getKryo().register(PlayerInfo.class);
        client.getKryo().register(CreateRoomRequest.class);
        client.getKryo().register(RoomCreatedResponse.class);
        client.getKryo().register(JoinRoomRequest.class);
        client.getKryo().register(RoomListRequest.class);
        client.getKryo().register(RoomListResponse.class);
        client.getKryo().register(RoomInfo.class);
        client.getKryo().register(RoomUpdate.class);
        client.getKryo().register(ChatMessage.class);
        client.getKryo().register(LeaveRoomRequest.class);
        client.getKryo().register(ArrayList.class);
        client.getKryo().register(ReadyRequest.class);
        client.getKryo().register(ReadyResponse.class);
        client.getKryo().register(Ship.class);
        client.getKryo().register(AttackRequest.class);
        client.getKryo().register(AttackResponse.class);
        client.getKryo().register(GameResult.class);
    }

    public void setWaitModeController(WaitViewController waitModeController) {
        this.waitModeController = waitModeController;
    }

    public void setBoardViewController(BoardViewController boardViewController) {
        this.boardViewController = boardViewController;
    }

    public void setPlayGameViewController(PlayGameViewController playGameViewController) {
        this.playGameViewController = playGameViewController;
    }

    public void sendLogin(String username, int level) {
        LoginRequest login = new LoginRequest();
        login.username = username;
        login.level  = level;
        client.sendTCP(login);
    }

    public void createRoom() {
        isJoiningFromList = false;
        client.sendTCP(new CreateRoomRequest());
    }

    public void joinRoom(String roomId) {
        isJoiningFromList = true;
        currentRoomId = roomId;
        JoinRoomRequest join = new JoinRoomRequest();
        join.roomId = roomId;
        client.sendTCP(join);
    }

    public void sendChat(String message) {
        ChatMessage chat = new ChatMessage();
        chat.sender = playerInfo.username;
        chat.message = message;
        client.sendTCP(chat);
    }

    public void leaveRoom() {
        client.sendTCP(new LeaveRoomRequest());
        currentRoom = null;
        currentRoomId = null;
    }

    public void requestRoomList() {
        client.sendTCP(new RoomListRequest());
    }

    public void requestReady(boolean isReady, List<Ship> ships) {
        ReadyRequest ready = new ReadyRequest();
        ready.isReady = isReady;
        ready.ships = ships;
        client.sendTCP(ready);
    }

    public void requestAttack(int col, int row, boolean success, boolean endGame, GameResult result) {
        AttackRequest request = new AttackRequest();
        request.col = col;
        request.row = row;
        request.success = success;
        request.endGame = endGame;
        request.result = result;
        client.sendTCP(request);
    }

    private void handleLoginResponse(LoginResponse response) {
        if (response.success) {
            playerInfo = response.playerInfo;
        }
    }

    private void handleRoomCreated(RoomCreatedResponse response) {
        currentRoomId = response.roomId;
    }

    private void handleRoomUpdate(RoomUpdate update) {
        currentRoom = update;
        if(currentRoom.opponentInfo != null) {
            PlayerInfo opponentInfo = currentRoom.opponentInfo;
            System.out.println(opponentInfo.username + " " + opponentInfo.level);
            waitModeController.setOpponentInfo(opponentInfo.username, opponentInfo.level);
        } else if (waitModeController != null) {
            waitModeController.setNoOpponent();
        }
        if (isJoiningFromList) {
            waitModeController.setRoomId(currentRoomId);
        }
    }

    private void handleChat(ChatMessage chat) {
        playGameViewController.onChatReceived(chat);
    }

    private void handleRoomListResponse(RoomListResponse response) {
        RoomListScene.onRoomListReceived(response); // Nhận bất kỳ lúc nào từ server
    }

    private void handleReadyResponse(ReadyResponse response) throws IOException {
        Platform.runLater(() -> {
            try {
                System.out.println(response.startGame);
                if (response.startGame) boardViewController.startGame(response.yourTurn, response.enemyShips);
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        });
    }

    private void handleAttackResponse(AttackResponse response) {
        Platform.runLater(() -> {
            playGameViewController.handleAttacked(response.col, response.row, response.success, response.endGame, response.result);
        });
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public RoomUpdate getCurrentRoom() {
        return currentRoom;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }
}