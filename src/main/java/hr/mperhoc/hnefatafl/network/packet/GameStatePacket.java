package hr.mperhoc.hnefatafl.network.packet;

import hr.mperhoc.hnefatafl.board.Board;

public class GameStatePacket extends Packet {
    private final Board board;

    public GameStatePacket(Board board) {
        super(PacketHeaders.GAME_STATE);
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
