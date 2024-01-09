package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.board.GameMove;
import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    private static final String FILENAME = "xml/game-moves.xml";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private XmlUtils() {
    }

    public static void createNewFile() {
        try {
            Document document = createDocument("game-moves");
            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementation = builder.getDOMImplementation();

        return implementation.createDocument(null, element, null);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);

        return element;
    }

    private static void saveDocument(Document document, String filename) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(FILENAME)));
    }

    public static void saveGameMove(GameMove move) {
        List<GameMove> moves = getAllGameMoves();
        moves.add(move);

        try {
            Document document = createDocument("game-moves");

            for (GameMove gameMoveElement : moves) {
                Element gameMove = document.createElement("move");
                document.getDocumentElement().appendChild(gameMove);

                // Custom XML structure for our game move //
                Piece piece = gameMoveElement.getPieceMoved();

                // Writing the entire piece data
                gameMove.appendChild(createElement(document, "piece-type", piece.getPieceType().name()));
                gameMove.appendChild(createElement(document, "old-x", String.valueOf(piece.getX())));
                gameMove.appendChild(createElement(document, "old-y", String.valueOf(piece.getY())));

                // And then the new location of the piece, and when the move was played
                gameMove.appendChild(createElement(document, "new-x", String.valueOf(gameMoveElement.getX())));
                gameMove.appendChild(createElement(document, "new-y", String.valueOf(gameMoveElement.getY())));
                gameMove.appendChild(createElement(document, "date-time", gameMoveElement.getDateTime().format(FORMATTER)));
            }

            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static List<GameMove> getAllGameMoves() {
        List<GameMove> moves = new ArrayList<>();
        File file = new File(FILENAME);

        if (file.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Element gameMovesDocumentElement = document.getDocumentElement();

                NodeList gameMovesNodeList = gameMovesDocumentElement.getChildNodes();

                // Iterating over each game move
                for (int i = 0; i < gameMovesNodeList.getLength(); i++) {
                    PieceType type = PieceType.ATTACKER;
                    int oldX = 0, oldY = 0;
                    int newX = 0, newY = 0;
                    LocalDateTime dateTime = LocalDateTime.now();

                    Node node = gameMovesNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element gameMoveElement = (Element) node;
                        NodeList gameMoveElementNodeList = gameMoveElement.getChildNodes();

                        // Iterating over each node element
                        for (int j = 0; j < gameMoveElementNodeList.getLength(); j++) {
                            if (gameMoveElementNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                Element childElement = (Element) gameMoveElementNodeList.item(j);

                                // Now, we check which tag was that
                                switch (childElement.getTagName()) {
                                    case "piece-type" -> type = PieceType.valueOf(childElement.getTextContent());
                                    case "old-x" -> oldX = Integer.valueOf(childElement.getTextContent());
                                    case "old-y" -> oldY = Integer.valueOf(childElement.getTextContent());
                                    case "new-x" -> newX = Integer.valueOf(childElement.getTextContent());
                                    case "new-y" -> newY = Integer.valueOf(childElement.getTextContent());
                                    case "date-time" ->
                                            dateTime = LocalDateTime.parse(childElement.getTextContent(), FORMATTER);
                                }
                            }
                        }
                    } else {
                        continue;
                    }

                    GameMove move = new GameMove(new Piece(oldX, oldY, type), newX, newY, dateTime);
                    moves.add(move);
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }

        return moves;
    }
}
