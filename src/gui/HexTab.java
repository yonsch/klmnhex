package gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.Stack;

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
    private String fullTitle = "";

    private Stack<HistoryEvent> history = new Stack<>(); // todo: add redo

    public interface HistoryEvent {
        void undo();
        void redo();
    }

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
                if (table.getHeight() == 0) return;
                if (scroll.getVisibleAmount() * table.getHeight() < 20)
                    scroll.setVisibleAmount(20.0 / table.getHeight()); // min height of 20 pixels
            });
            text.lookup(".scroll-bar").setDisable(true);
        };
        Platform.runLater(() -> Platform.runLater(syncScrolls)); // stupid, but if it ain't broke, don't fix it

        ((HexSelectionModel) text.getSelectionModel()).startProperty().bindBidirectional(
                ((HexSelectionModel) table.getSelectionModel()).startProperty());
        ((HexSelectionModel) text.getSelectionModel()).endProperty().bindBidirectional(
                ((HexSelectionModel) table.getSelectionModel()).endProperty());

        table.setOnEdit((obs, oldV, newV) -> {
            int i = ((HexColumn) obs.getTableColumn()).getIndex();
            text.getColumns().get(i).setVisible(false);
            text.getColumns().get(i).setVisible(true);

            history.push(new HistoryEvent() {
                @Override public void undo() {
                    table.getItems().get(obs.getRow())[i] = oldV;
                    text.getColumns().get(i).setVisible(false);
                    text.getColumns().get(i).setVisible(true);
                }
                @Override public void redo() {}//{ obs.setValue(newV); }
            });
        });
        text.setOnEdit((obs, oldV, newV) -> {
            int i = ((HexColumn) obs.getTableColumn()).getIndex();
            table.getColumns().get(i).setVisible(false); // this won't work, since table has spacing
            table.getColumns().get(i).setVisible(true);  // columns and the indices are off.
                                                         // I'll have to implement something more serious.

            history.push(new HistoryEvent() {
                @Override public void undo(){}// { obs.setValue(oldV); }
                @Override public void redo(){}// { obs.setValue(newV); }
            });
        });

        content.getChildren().add(header);
        content.getChildren().add(new HBox(table, text));

        text.setPlaceholder(new Label(""));
        table.setPlaceholder(new Label("No Open File"));

        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);
        content.setPrefWidth(table.getPrefWidth() + text.getPrefWidth());

        ScrollPane scrollPane = new ScrollPane(content); // todo: fix horizontal scroll
        scrollPane.setFitToHeight(true);
        setContent(scrollPane);
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

    public String getFullTitle() { return fullTitle; }
    public void setFullTitle(String fullTitle) { this.fullTitle = fullTitle; }

    public void undo() {
        if (!history.empty())
            history.pop().undo();
    }
}
