package hr.mperhoc.hnefatafl.chat.service;

import java.rmi.Remote;
import java.util.List;

import java.rmi.RemoteException;

public interface RemoteChatService extends Remote {

    String REMOTE_CHAT_OBJECT_NAME = "hr.mperhoc.hnefatafl.chat.service";

    void sendMessage(String message) throws RemoteException;

    List<String> getAllMessages() throws RemoteException;
}
