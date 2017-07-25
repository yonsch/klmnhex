package gui.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DefaultStringConverter;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/24/2017.
 *
 * A HexTable column.
 */
class HexColumn extends TableColumn<Byte[], String>
{
    HexColumn(int i) {
        super(String.format("%01X", i));

        setPrefWidth(25); // default width, can be changed
        setResizable(false);
        setSortable(false);

        setCellFactory(p -> new HexCell());
        setOnEditCommit(t -> {
            if (!(getTableView() instanceof HexTable)) {
                System.err.println("Warning: HexColumn used outside of HexTable!");
                return;
            }

            Byte res = null;
            switch (((HexTable) getTableView()).getDisplayMode()) {
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
            t.getRowValue()[i] = res;
            t.getTableColumn().setVisible(false);
            t.getTableColumn().setVisible(true);
        });
        setCellValueFactory(param -> {
            if (!(getTableView() instanceof HexTable)) {
                System.err.println("Warning: HexColumn used outside of HexTable!");
                return new SimpleStringProperty("err");
            }

            Byte v = param.getValue()[i];
            if (v == null) return new SimpleStringProperty("- -");
            switch (((HexTable) getTableView()).getDisplayMode()) {
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

    public Pos alignment = Pos.CENTER;
    private class HexCell extends TextFieldTableCell<Byte[], String>
    {
        HexCell() {
            super(new DefaultStringConverter());

            setOnMousePressed(e -> getTableView().getSelectionModel().clearAndSelect(getIndex(), HexColumn.this));
            setOnDragDetected(e -> startFullDrag());
            setOnMouseDragEntered(e -> getTableView().getSelectionModel().select(getIndex(), HexColumn.this));

            setAlignment(alignment);
            setPadding(new Insets(0));
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) setText(null);
            else setText(item);
        }
    }
}