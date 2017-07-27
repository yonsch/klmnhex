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

        Platform.runLater(() -> {
            ScrollBar scroll = (ScrollBar) table.lookup(".scroll-bar");
            scroll.valueProperty().bindBidirectional(((ScrollBar) text.lookup(".scroll-bar")).valueProperty());
            scroll.visibleAmountProperty().addListener(obs -> {
                if (scroll.getVisibleAmount() < 0.05) scroll.setVisibleAmount(0.05);
            });
            text.lookup(".scroll-bar").setDisable(true);
        });

        ((HexSelectionModel) text.getSelectionModel()).startProperty().bindBidirectional(
                ((HexSelectionModel) table.getSelectionModel()).startProperty());
        ((HexSelectionModel) text.getSelectionModel()).endProperty().bindBidirectional(
                ((HexSelectionModel) table.getSelectionModel()).endProperty());

        table.setOnEdit((row, column) -> {
            text.getColumns().get(column.getIndex()).setVisible(false);
            text.getColumns().get(column.getIndex()).setVisible(true);
        });
        text.setOnEdit((row, column) -> {
            table.getColumns().get(column.getIndex()).setVisible(false);
            table.getColumns().get(column.getIndex()).setVisible(true);
        });

        getChildren().add(header);
        getChildren().add(new HBox(table, text));

        text.setPlaceholder(new Label(""));
        table.setPlaceholder(new Label("No Open File"));

        setVgrow(getChildren().get(1), Priority.ALWAYS);
        setPrefWidth(table.getPrefWidth() + text.getPrefWidth());
    }

    public ListView getHeader() { return header; }
    public HexTable getTable() { return table; }

    public void setRadix(int radix) {
        ((IndexColumn) table.getColumns().get(0)).setRadix(radix);
        table.refresh();
    }

    public void setData(HexData data) {
        table.setItems(data);
        text.setItems(data);
    }
    public void clearSelection() { table.getSelectionModel().clearSelection(); }
    public void setDisplayMode(HexTable.DisplayMode mode) { table.setDisplayMode(mode); }
}
