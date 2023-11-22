package hr.mperhoc.hnefatafl.network.packet;

import java.io.Serial;
import java.io.Serializable;

public abstract class Packet implements Serializable {
    @Serial
    private static final long serialVersionUID = -2108992701377331395L;
    private final String header;

    public Packet(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
