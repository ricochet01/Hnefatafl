package hr.mperhoc.hnefatafl.jndi;

public enum ConfigKey {
    SERVER_PORT("server.port"),
    CLIENT_PORT("client.port"),
    RMI_PORT("rmi.port"),
    RANDOM_PORT_HINT("random.port.hint");

    private final String keyName;

    ConfigKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
