package hr.mperhoc.hnefatafl.network.packet;

public class PingPacket extends Packet {
    public PingPacket() {
        super(PacketHeaders.PING);
    }
}
