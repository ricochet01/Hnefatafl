package hr.mperhoc.hnefatafl.network.packet;

import java.io.Serializable;

public abstract class Packet {
    private final String header;
    private Serializable objectToSend;

    public Packet(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public Serializable getObjectToSend() {
        return objectToSend;
    }

    public void setObjectToSend(Serializable objectToSend) {
        this.objectToSend = objectToSend;
    }
}
