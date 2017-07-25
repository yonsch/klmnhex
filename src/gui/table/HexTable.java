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

        for (int i = 0; i < 4; i++) getColumns().add(new HexColumn(i));
        for (int i = 4; i < 16; i++) getColumns().add(new HexColumn(i));
        getColumns().add(5, new SpacingColumn());
        getColumns().add(10, new SpacingColumn());
        getColumns().add(15, new SpacingColumn());
        setMaxWidth(25 * 16 + 10 * 3 + 80 + 20);

        setEditable(true);

        HexSelectionModel selectionModel = new HexSelectionModel(this);
        selectionModel.ignore(getColumns().get(0));
        selectionModel.skip(5, 10, 15);
        setSelectionModel(selectionModel);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        refresh();
    }
    public DisplayMode getDisplayMode() { return displayMode; }

    public ListView<String> generateHeader() {
        ListView<String> res = new ListView<>();
        res.setOrientation(Orientation.HORIZONTAL);
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
                    if (index >=0 && index < getColumns().size()) {
                        setPrefWidth(getColumns().get(index).getPrefWidth());
                        if (getPrefWidth() == 80) setPrefWidth(84); // not sure why...
                    }
                }
            };

            c.setAlignment(Pos.CENTER);
            return c;
        });
        for (int i = 0; i < getColumns().size(); i++)
            res.getItems().add(getColumns().get(i).getText());
        res.setEditable(false);
        res.setMinHeight(35); //
        res.setMaxHeight(35); // not resizable
        res.setMaxWidth(getMaxWidth());
        res.getStyleClass().add("table-header");
        return res;
    }
}
