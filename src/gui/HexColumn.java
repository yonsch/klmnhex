package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/24/2017.
 *
 * A HexTable column.
 */
class HexColumn extends TableColumn<Byte[], String>
{
    private int i;
    HexColumn(int i) {
        super(String.format("%01X", i));

        this.i = i;
        setPrefWidth(27); // default width, can be changed
        setResizable(false);
        setSortable(false);

        setCellFactory(p -> new HexCell());
        setCellValueFactory(param -> {
            if (!(getTableView() instanceof HexTable)) {
                System.err.println("Warning: HexColumn used outside of HexTable!");
                return new SimpleStringProperty("err");
            }

            Byte v = param.getValue()[i];
            if (((HexTable) getTableView()).getDisplayMode() != HexTable.DisplayMode.CHAR
                    && v == null) return new SimpleStringProperty("- -");
            switch (((HexTable) getTableView()).getDisplayMode()) {
                case DECIMAL:
                    return new SimpleStringProperty(String.format("%d", v));
                case UDECIMAL:
                    return new SimpleStringProperty(String.format("%d", v & 0xFF));
                case HEX:
                    return new SimpleStringProperty(String.format("%02X", v));
                case CHAR:
                    if (v == null) return null;
                    if (!Character.isDefined(v) || v == '\n' || v == '\r' || v == '\t' || v == '\0')
                        return new SimpleStringProperty(".");
                    return new SimpleStringProperty(Character.toString((char) v.byteValue()));
            }

            return null;
        });
    }

    public int getIndex() { return i; }

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
            setOnEditCommit(t -> {
                if (!(getTableView() instanceof HexTable)) {
                    System.err.println("Warning: HexColumn used outside of HexTable!");
                    return;
                }
                Byte res = null;
                switch (((HexTable) getTableView()).getDisplayMode()) {
                    case HEX:
                        try {
                            short r = Short.parseShort(t.getNewValue(), 16);
                            if (r > 0xFF) throw new Exception();
                            res = (byte) r;
                        }
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
                        else if (t.getNewValue().equals("")) res = (byte)' ';
                        else res = (byte) t.getOldValue().charAt(0);
                }
                t.getRowValue()[i] = res;
                t.getTableColumn().setVisible(false);
                t.getTableColumn().setVisible(true);

                if (((HexTable) getTableView()).onEdit != null) {
                    TablePosition<Byte[], String> pos = t.getTablePosition();
                    ((HexTable) getTableView()).onEdit.onEdit(pos.getRow(), (HexColumn) pos.getTableColumn());
                }
            });
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                return;
            }
            pseudoClassStateChanged(PseudoClass.getPseudoClass("null-value"), item == null);
            pseudoClassStateChanged(PseudoClass.getPseudoClass("edited"), ((HexTable) getTableView()).hasChanged(getIndex(), HexColumn.this));
            setText(item == null ? "." : item);
        }
    }
}