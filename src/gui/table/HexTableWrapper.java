package gui.table;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/24/2017.
 *
 * This class is in charge of displaying a HexTable with its surrounding components.
 */
public class HexTableWrapper extends VBox
{
    private HexTable table, text;
    private ListView header;

    public HexTableWrapper() {
        super();
        table = new HexTable();
        table.addIndexColumn();
        table.addHexColumns(4);
        table.addSpacingColumn();
        table.addHexColumns(4);
        table.addSpacingColumn();
        table.addHexColumns(4);
        table.addSpacingColumn();
        table.addHexColumns(4);
        header = table.generateHeader();

        text = new HexTable();
        text.setDisplayMode(HexTable.DisplayMode.CHAR);
        text.addHexColumns(16);
        for (TableColumn c : text.getColumns()) c.setPrefWidth(15);
        text.setPrefWidth(15 * 16);
        text.getStyleClass().add("hex-text");

        Platform.runLater(() ->
                ((ScrollBar)table.lookup(".scroll-bar")).valueProperty().bindBidirectional(
                        ((ScrollBar) text.lookup(".scroll-bar")).valueProperty())
        );

        ((HexSelectionModel) text.getSelectionModel()).startProperty().bindBidirectional(
                ((HexSelectionModel) table.getSelectionModel()).startProperty());
        ((HexSelectionModel) text.getSelectionModel()).endProperty().bindBidirectional(
                ((HexSelectionModel) table.getSelectionModel()).endProperty());

        getChildren().add(header);
        getChildren().add(new HBox(table, text));

        setVgrow(getChildren().get(1), Priority.ALWAYS);
        setPrefWidth(table.getPrefWidth() + text.getPrefWidth());
    }

    public ListView getHeader() { return header; }
    public HexTable getTable() { return table; }

    public void setData(HexData data) { table.setItems(data); text.setItems(data); }
    public void clearSelection() { table.getSelectionModel().clearSelection(); }
    public void setDisplayMode(HexTable.DisplayMode mode) { table.setDisplayMode(mode); }
}
