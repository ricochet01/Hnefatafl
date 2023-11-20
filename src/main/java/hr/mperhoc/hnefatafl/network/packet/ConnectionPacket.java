package hr.mperhoc.hnefatafl.network.packet;

import hr.mperhoc.hnefatafl.piece.PieceType;

public class ConnectionPacket extends Packet {
    private final PieceType playerSide;

    public ConnectionPacket(PieceType playerSide) {
        super(PacketHeaders.LOGIN);
        this.playerSide = playerSide;
    }

    public ConnectionPacket() {
        // The non-host client does not get to pick his side
        this(null);
    }

    public PieceType getPlayerSide() {
        return playerSide;
    }
}
