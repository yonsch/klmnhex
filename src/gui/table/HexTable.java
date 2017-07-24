package gui.table;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class HexTable extends TableView<Byte[]>
{
    public enum DisplayMode { HEX, DECIMAL, UDECIMAL, CHAR }

    private DisplayMode displayMode = DisplayMode.HEX;

    public HexTable() {
        super();

        getColumns().add(new IndexColumn("index"));

        for (int i = 1; i < 17; i++) getColumns().add(new HexColumn(i));
        setMaxWidth(40 * 16 + 95);
        setEditable(true);

        setSelectionModel(new HexSelectionModel(this, 1));
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        refresh();
    }
    public DisplayMode getDisplayMode() { return displayMode; }

    public ListView<String> createHeader() {
        ListView<String> res = new ListView<>();
        res.setOrientation(Orientation.HORIZONTAL);
        res.getItems().add("index");
        res.setCellFactory(e -> {
            ListCell<String> c = new ListCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) setText(null);
                    else setText(item);
                }

                @Override
                public void updateIndex(int index) {
                    super.updateIndex(index);
                    if (index == 0) setPrefWidth(60);
                }
            };

            c.setAlignment(Pos.CENTER);
            c.setPrefWidth(40);
            return c;
        });
        for (int i = 0; i < 16; i++) res.getItems().add(String.format("%01X", i));
        res.setEditable(false);
        res.setMinHeight(35); //
        res.setMaxHeight(35); // not resizable
        res.setMaxWidth(getMaxWidth());
        res.getStyleClass().add("table-header");
        return res;
    }
}
