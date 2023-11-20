package hr.mperhoc.hnefatafl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class NetworkUtils {

    public static String getLocalAddress() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPublicAddress() {
        // Public IP address
        try {
            URL whatIsMyIp = new URL("http://checkip.amazonaws.com"); // This returns only the IP address
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
            return in.readLine(); // You get the IP as a String
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}
