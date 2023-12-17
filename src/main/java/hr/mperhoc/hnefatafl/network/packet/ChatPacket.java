package hr.mperhoc.hnefatafl.network.packet;

import java.io.Serial;

public class ChatPacket extends Packet {
    @Serial
    private static final long serialVersionUID = -479737092822156834L;

    public ChatPacket() {
        super(PacketHeaders.CHAT);
    }
}
