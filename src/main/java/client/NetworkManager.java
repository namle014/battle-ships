package client;

import battleships.PWFModeController;
import battleships.WaitViewController;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import common.Network.*;

import java.io.IOException;
import java.util.ArrayList;

public class NetworkManager {
    private Client client;
    private PlayerInfo playerInfo;
    private RoomUpdate currentRoom;
    private String currentRoomId;
    private boolean isJoiningFromList = false;
    private WaitViewController waitModeController;

    public NetworkManager() throws IOException {
        client = new Client();
        registerClasses();
        client.start();
        client.connect(5000, "localhost", 54555, 54777);

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
    }

    public void setWaitModeController(WaitViewController waitModeController) {
        this.waitModeController = waitModeController;
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
        }
        if (isJoiningFromList) {
            waitModeController.setRoomId(currentRoomId);
        }
    }

    private void handleChat(ChatMessage chat) {
        GamePlayScene.onChatReceived(chat);
    }

    private void handleRoomListResponse(RoomListResponse response) {
        RoomListScene.onRoomListReceived(response); // Nhận bất kỳ lúc nào từ server
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