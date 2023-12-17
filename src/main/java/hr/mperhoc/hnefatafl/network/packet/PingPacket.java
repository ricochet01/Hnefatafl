package hr.mperhoc.hnefatafl.network.packet;

import java.io.Serial;

public class PingPacket extends Packet {
    @Serial
    private static final long serialVersionUID = 3442807620933456526L;

    public PingPacket() {
        super(PacketHeaders.PING);
    }
}
