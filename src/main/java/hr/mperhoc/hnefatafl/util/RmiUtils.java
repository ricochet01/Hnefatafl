package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.chat.service.RemoteChatService;
import hr.mperhoc.hnefatafl.chat.service.RemoteChatServiceImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtils {
    private static final int PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;

    public static RemoteChatService startRmiChatServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            // TODO: either return the chat service or pass it in as an argument
            RemoteChatService remoteChatService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService,
                    RANDOM_PORT_HINT);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);

            System.err.println("Object registered in RMI registry");
            return remoteChatService;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static RemoteChatService startRmiChatClient(String address) {
        try {
            Registry registry = LocateRegistry.getRegistry(address, PORT);
            // TODO: either return the chat service or pass it in as an argument
            RemoteChatService remoteChatService = (RemoteChatService) registry.lookup(
                    RemoteChatService.REMOTE_CHAT_OBJECT_NAME);

            return remoteChatService;
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
