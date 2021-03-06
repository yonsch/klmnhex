package gui;

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
    private int hexIndex = 0;
    protected HexEditListener onEdit = null;

    private Byte[][] originalData;

    // selection takes about 5 times more nano seconds, but still not very bad
    public HexTable() {
        super();
        setPrefWidth(20);
        setEditable(true);
        setSelectionModel(new HexSelectionModel(this));

        itemsProperty().addListener((o) -> {
            originalData = new Byte[getItems().size()][];
            for (int i = 0; i < getItems().size(); i++) originalData[i] = getItems().get(i).clone();
        });
        setOnSort(System.out::println);
    }

    public boolean hasChanged(int row, HexColumn column) {
        return column != null && getItems().get(row)[column.getIndex()] != null &&
            !getItems().get(row)[column.getIndex()].equals(originalData[row][column.getIndex()]);
    }

    public void addHexColumns(int count) {
        for (int i = 0, offset = hexIndex; i < count; i++, hexIndex++)
            getColumns().add(new HexColumn(i + offset));
        setPrefWidth(getPrefWidth() + count * 27);
    }

    public void addIndexColumn() {
        IndexColumn column = new IndexColumn("index");
        getColumns().add(column);
        ((HexSelectionModel) getSelectionModel()).ignore(column);
        setPrefWidth(getPrefWidth() + 80);
    }

    public void addSpacingColumn() {
        ((HexSelectionModel) getSelectionModel()).skip(getColumns().size());
        getColumns().add(new SpacingColumn());
        setPrefWidth(getPrefWidth() + 10);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        refresh();
    }
    public DisplayMode getDisplayMode() { return displayMode; }

    public interface HexEditListener { void changed(TablePosition c, Byte oldV, Byte newV); }
    public void setOnEdit(HexEditListener onEdit) { this.onEdit = onEdit; }

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
