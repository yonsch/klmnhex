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

    public HexTableFX(Byte[][] data) {
        super();
        setEditable(true);

        TableColumn index = new HexColumn("index", 0);
        index.setEditable(false);
        index.setPrefWidth(60);
        getColumns().add(index);

        if (data.length == 0) return;
        columns = data[0].length;

        for (int i = 1; i < columns + 1; i++) getColumns().add(new HexColumn(i));
        this.data = new DataManager(data);
        setItems(this.data);

        setMaxWidth(40 * columns + 80);
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
        public HexColumn(int i) { this(String.format("%01X", i - 1), i); }
        public HexColumn(String s, int i) {
            super(s);

            setPrefWidth(40);
            setResizable(false);
            setSortable(false);
            setEditable(true);

            // fucking hate everything about this shit
            setCellFactory(p -> {
                TextFieldTableCell<Byte[], String> t;
                if (i == 0)
                    t = new TextFieldTableCell<Byte[], String>(new DefaultStringConverter()) {
                        @Override
                        public void updateIndex(int index) {
                            super.updateIndex(index);
                            if (isEmpty() || index < 0) setText(null);
                            else setText(String.format("%06d", index));
                        }
                    };
                else t = new TextFieldTableCell<>(new DefaultStringConverter());

                t.setEditable(true);
                t.setAlignment(Pos.CENTER);
                return t;
            });
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
    }
}
