package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

import java.awt.*;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class HexTableFX extends TableView<Byte[]>
{
    public enum DisplayMode { HEX, DECIMAL, UDECIMAL, CHAR }

    private DataManager data;
    private int columns;
    private DisplayMode displayMode = DisplayMode.HEX;
    private HexColumn.HexCell start;

    public HexTableFX(Byte[][] data) {
        super();
        setEditable(true);

        TableColumn index = new HexColumn("index", 0);
        index.setEditable(false);
        index.setPrefWidth(60);
        getColumns().add(index);
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (data.length == 0) return;
        columns = data[0].length;

        for (int i = 1; i < columns + 1; i++) getColumns().add(new HexColumn(i));
        this.data = new DataManager(data);
        setItems(this.data);

        Byte[] header = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        getItems().add(0, header);

        setMaxWidth(40 * columns + 85);

        getSelectionModel().clearSelection();
    }

    public void setData(Byte[][] data) {
        TableColumn index = getColumns().get(0);
        getColumns().clear();
        getColumns().add(index);

        if (data.length == 0) return;

        columns = data[0].length;
        for (int i = 1; i < getColumnCount() + 1; i++) getColumns().add(new HexColumn(i));
        this.data = new DataManager(data);
        setItems(this.data);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        refresh();
    }

    public int getColumnCount() { return columns; }

    private class HexColumn extends TableColumn<Byte[], String>
    {
        private int i;

        public HexColumn(int i) { this(String.format("%01X", i - 1), i); }
        public HexColumn(String s, int i) {
            super(s);

            this.i = i;
            setPrefWidth(40);
            setResizable(false);
            setSortable(false);

            // fucking hate everything about this shit
            setCellFactory(p -> new HexCell()); // TODO: header cells, index cells...
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
                if (i == 0) return null;
                if (param.getValue() == data.getHeader()) return new SimpleStringProperty(String.format("%01X", i - 1));

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

                if (getColumn() != 0) {
                    setOnDragDetected(e -> {
                        if (getRow() == 0) return;
                        startFullDrag();
                        HexTableFX.this.getSelectionModel().select(getIndex(), HexColumn.this);
                        start = this;
                    });
                    setOnMouseDragEntered(e -> {
                        // TODO: custom SelectionModel, custom input controls, custom everything
                        if (getRow() == 0) return;
                        HexTableFX.this.getSelectionModel().clearSelection();
                        if (getRow() > start.getRow()) {
                            for (int i = getColumn(); i > 0; i--)
                                HexTableFX.this.getSelectionModel().select(getRow(), HexTableFX.this.getColumns().get(i));
                            for (int i = columns; i >= start.getColumn(); i--)
                                HexTableFX.this.getSelectionModel().select(start.getRow(), HexTableFX.this.getColumns().get(i));
                            for (int i = start.getRow() + 1; i < getRow(); i++)
                                for (int j = 1; j < 17; j++)
                                    HexTableFX.this.getSelectionModel().select(i, HexTableFX.this.getColumns().get(j));
                        } else if (getRow() < start.getRow()) {
                            for (int i = start.getColumn(); i > 0; i--)
                                HexTableFX.this.getSelectionModel().select(start.getRow(), HexTableFX.this.getColumns().get(i));
                            for (int i = columns; i >= getColumn(); i--)
                                HexTableFX.this.getSelectionModel().select(getRow(), HexTableFX.this.getColumns().get(i));
                            for (int i = getRow() + 1; i < start.getRow(); i++)
                                for (int j = 1; j < 17; j++)
                                    HexTableFX.this.getSelectionModel().select(i, HexTableFX.this.getColumns().get(j));
                        } else if (getColumn() > start.getColumn())
                            for (int i = getColumn(); i > start.getColumn(); i--)
                                HexTableFX.this.getSelectionModel().select(getRow(), HexTableFX.this.getColumns().get(i));
                        else if (getColumn() < start.getColumn())
                            for (int i = start.getColumn(); i > getColumn(); i--)
                                HexTableFX.this.getSelectionModel().select(getRow(), HexTableFX.this.getColumns().get(i));
                        else
                            HexTableFX.this.getSelectionModel().select(getRow(), HexTableFX.this.getColumns().get(getColumn()));
                    });
                }
                else HexCell.this.setOnMouseClicked(e -> {
                    if (e.getClickCount() > 1)
                        getSelectionModel().selectRange(getRow(), HexColumn.this,
                                getRow(), HexTableFX.this.getColumns().get(columns));
                });

                setAlignment(Pos.CENTER);
            }

            public int getRow() { return getIndex(); }
            public int getColumn() { return HexColumn.this.i; }

            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if(getColumn() == 0) {
                    if (isEmpty() || index <= 0) setText(null);
                    else setText(String.format("%06d", index - 1));
                }
                else if (index == 0) editableProperty().set(false);
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) setText(null);
                else setText(item);
            }
        }
    }
}
