package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.chat.service.RemoteChatService;
import hr.mperhoc.hnefatafl.chat.service.RemoteChatServiceImpl;
import hr.mperhoc.hnefatafl.jndi.Config;
import hr.mperhoc.hnefatafl.jndi.ConfigKey;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtils {

    public static RemoteChatService startRmiChatServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(Config.readIntConfigValue(ConfigKey.RMI_PORT));
            RemoteChatService remoteChatService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService,
                    Config.readIntConfigValue(ConfigKey.RANDOM_PORT_HINT));
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
            Registry registry = LocateRegistry.getRegistry(address, Config.readIntConfigValue(ConfigKey.RMI_PORT));
            return (RemoteChatService) registry.lookup(
                    RemoteChatService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
