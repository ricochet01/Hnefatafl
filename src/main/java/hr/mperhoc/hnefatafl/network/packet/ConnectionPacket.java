package hr.mperhoc.hnefatafl.network.packet;

import hr.mperhoc.hnefatafl.piece.PieceType;

import java.io.Serial;

public class ConnectionPacket extends Packet {
    @Serial
    private static final long serialVersionUID = -8630531225647249821L;
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
