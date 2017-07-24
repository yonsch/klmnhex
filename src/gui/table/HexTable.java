package gui.table;

import gui.HexData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DefaultStringConverter;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class HexTable extends TableView<Byte[]>  // TODO: extend VBox instead and contain header by default. Fuck generality
{
    public enum DisplayMode { HEX, DECIMAL, UDECIMAL, CHAR }

    private ObservableList columns;
    private DisplayMode displayMode = DisplayMode.HEX;

    int colWidth;

    public HexTable() {
        super();

        TableColumn index = new IndexColumn("index");
        getColumns().add(index);

        colWidth = 40;
        for (int i = 1; i < 17; i++) getColumns().add(new HexColumn(i));
        setMaxWidth(colWidth * 16 + 95);
        columns = getColumns();
        setEditable(true);

        setSelectionModel(new HexSelectionModel(this, 1));
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        refresh();
    }

    // possible todo- move to package-private files
    private class HexColumn extends TableColumn<Byte[], String>
    {
        public HexColumn(int i) {
            super("");

            setPrefWidth(colWidth);
            setResizable(false);
            setSortable(false);

            setCellFactory(p -> new HexCell());
            setOnEditCommit(t -> {
                Byte res = null;
                switch (displayMode) {
                    case HEX:
                        try { res = Byte.parseByte(t.getNewValue(), 16); }
                        catch (Exception e) { res = Byte.parseByte(t.getOldValue(), 16); }
                        break;
                    case DECIMAL:
                    case UDECIMAL:
                        try { res = Byte.parseByte(t.getNewValue()); }
                        catch (Exception e) { res = Byte.parseByte(t.getOldValue()); }
                        break;
                    case CHAR:
                        if (t.getNewValue().length() == 1) res = (byte) t.getNewValue().charAt(0);
                        else if (t.getNewValue().equals("\\n")) res = (byte)'\n';
                        else if (t.getNewValue().equals("\\r")) res = (byte)'\r';
                        else if (t.getNewValue().equals("\\t")) res = (byte)'\t';
                        else res = (byte) t.getOldValue().charAt(0);
                }
                t.getRowValue()[i - 1] = res;
                t.getTableColumn().setVisible(false);
                t.getTableColumn().setVisible(true);
            });
            setCellValueFactory(param -> {
                Byte v = param.getValue()[i - 1];
                if (v == null) return new SimpleStringProperty("- -");
                switch (displayMode) {
                    case DECIMAL:
                        return new SimpleStringProperty(String.format("%d", v));
                    case UDECIMAL:
                        return new SimpleStringProperty(String.format("%d", v & 0xFF));
                    case HEX:
                        return new SimpleStringProperty(String.format("%02X", v));
                    case CHAR:
                        if (Character.isDefined(v)) {
                            String c = Character.toString((char) v.byteValue());
                            if (c.charAt(0) == '\r') c = "\\r";
                            else if (c.charAt(0) == '\n') c = "\\n";
                            else if (c.charAt(0) == '\t') c = "\\t";
                            return new SimpleStringProperty(c);
                        }
                        else return new SimpleStringProperty(".");
                }

                return null;
            });
        }

        private class HexCell extends TextFieldTableCell<Byte[], String>
        {
            public HexCell() {
                super(new DefaultStringConverter());

                setOnMousePressed(e -> getSelectionModel().clearAndSelect(getIndex(), HexColumn.this));
                setOnDragDetected(e -> startFullDrag());
                setOnMouseDragEntered(e -> getSelectionModel().select(getIndex(), HexColumn.this));

                setAlignment(Pos.CENTER);
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) setText(null);
                else setText(item);
            }
        }
    }

    public class IndexColumn extends TableColumn<Integer, String>
    {
        public IndexColumn(String name) {
            super(name);

            setEditable(false);
            setPrefWidth(80);

            setCellFactory(e -> new IndexCell());
            setCellValueFactory(e -> null);
        }

        private class IndexCell extends TableCell<Integer, String>
        {
            public IndexCell() {
                super();

                // double-click
                setOnMouseClicked(e -> {
                    if (e.getClickCount() <= 1) getSelectionModel().clearSelection();
                });
                addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    if (e.getClickCount() > 1 && columns.size() > 1)
                        getSelectionModel().clearAndSelect(getIndex());
                });
                setOnDragDetected(e -> startFullDrag());
                setOnMouseDragEntered(e -> getSelectionModel().select(getIndex()));

                getStyleClass().add("index-list");
                setAlignment(Pos.CENTER);
            }

            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);

                if (isEmpty() || index < 0) setText(null);
                else setText(String.format("%08X", index));
            }
        }
    }

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
            c.setPrefWidth(colWidth);
            return c;
        });
        for (int i = 0; i < 16; i++) res.getItems().add(String.format("%01X", i));
        res.setEditable(false);
        res.setMinHeight(35);
        res.setMaxHeight(35);
        res.setMaxWidth(colWidth * 16 + 60 + 15);
        res.getStyleClass().add("table-header");
        return res;
    }
}
