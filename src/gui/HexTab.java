package gui;

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
public class HexTab extends Tab
{
    private HexTable table, text;
    private ListView header;
    private HexData data;

    public HexTab() { this(null); }
    public HexTab(HexData data) {
        super();

        VBox content = new VBox();
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

        Runnable syncScrolls = () -> {
            ScrollBar scroll = (ScrollBar) table.lookup(".scroll-bar");
            scroll.valueProperty().bindBidirectional(((ScrollBar) text.lookup(".scroll-bar")).valueProperty());
            scroll.visibleAmountProperty().addListener(obs -> {
                if (scroll.getVisibleAmount() < 0.05) scroll.setVisibleAmount(0.05);
            });
            text.lookup(".scroll-bar").setDisable(true);
        };
        Platform.runLater(() -> Platform.runLater(syncScrolls)); // stupid, but if it ain't broke, don't fix it

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

        content.getChildren().add(header);
        content.getChildren().add(new HBox(table, text));

        text.setPlaceholder(new Label(""));
        table.setPlaceholder(new Label("No Open File"));

        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);
        content.setPrefWidth(table.getPrefWidth() + text.getPrefWidth());

        setContent(content);
        if (data != null) setData(data);
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

        this.data = data;
    }
    public HexData getData() { return data; }
    public void clearSelection() { table.getSelectionModel().clearSelection(); }
    public void setDisplayMode(HexTable.DisplayMode mode) { table.setDisplayMode(mode); }
}
