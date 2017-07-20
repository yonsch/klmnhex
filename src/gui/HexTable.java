package gui;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DefaultStringConverter;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class HexTable extends TableView<Byte[]>
{
    public enum DisplayMode { HEX, DECIMAL, UDECIMAL, CHAR }

    private ObservableList columns;
    private DataManager dataManager;
    private DisplayMode displayMode = DisplayMode.HEX;

    // variables used for dragging
    private HexColumn startCol, endCol;
    private int startRow, endRow;
    private boolean indexDrag = false;
    // used to stop scrolling when reaching top/bottom
    private boolean scrolling = false;

    public HexTable() {
        super();
        columns = getColumns();
        setEditable(true);

        TableColumn index = new IndexColumn("index");
        getColumns().add(index);
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        getSelectionModel().clearSelection();

        getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            VirtualFlow vf = (VirtualFlow)((TableViewSkin) getSkin()).getChildren().get(1);
            int first = vf.getFirstVisibleCellWithinViewPort().getIndex(),
                    last = vf.getLastVisibleCellWithinViewPort().getIndex();

            if (endRow > last) vf.scrollTo(endRow - last + first);
            else if (endRow < first) vf.scrollTo(endRow);
        }));
        setEventHandler(KeyEvent.KEY_PRESSED, e -> {
            e.consume();
            if (endCol == null) return;

            int nextRow = endRow, nextCol = this.endCol.i;
            switch (e.getCode()) {
                case LEFT:
                    if (nextCol - 1 == 0) { nextRow--; nextCol = columns.size() - 1; }
                    else nextCol--;
                    break;
                case UP:
                    if (nextRow == 0) {
                        if (e.isShiftDown()) nextCol = 1;
                        else if (!scrolling) nextRow = getItems().size() - 1;
                    }
                    else nextRow--;
                    break;
                case RIGHT:
                    if (nextCol + 1 == columns.size() - 1) { nextRow++; nextCol = 1; }
                    else nextCol++;
                    break;
                case DOWN:
                    if (nextRow + 1 == getItems().size()) {
                        if (e.isShiftDown()) nextCol = columns.size() - 1;
                        else if (!scrolling) nextRow = 0;
                    }
                    else nextRow++;
                    break;
            }
            scrolling = true;
            if (e.isShiftDown()) selectRange(nextRow, nextCol);
            else select(nextRow, nextCol);
        });
        addEventFilter(KeyEvent.KEY_RELEASED, e -> scrolling = false);
    }
    public HexTable(HexData hexData) {
        this();
        setData(hexData);
    }

    public void setData(HexData hexData) {
        TableColumn index = getColumns().get(0);
        getColumns().clear();
        getColumns().add(index);

        if (hexData.getRowCount() == 0) return;

        dataManager = new DataManager(hexData);
        setItems(dataManager);

        for (int i = 1; i < getColumnCount() + 1; i++) getColumns().add(new HexColumn(i));
        setMaxWidth(40 * getColumnCount() + 75);
    }

    // DO NOT use to get 0 (the index column). fixme
    public HexColumn getColumn(int i) { return (HexColumn) columns.get(i); }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        refresh();
    }

    public int getColumnCount() { return dataManager.getData().getColumnCount(); }

    private class HexColumn extends TableColumn<Byte[], String>
    {
        private int i;

        public HexColumn(int i) { this(String.format("%01X", i), i); }
        public HexColumn(String s, int i) {
            super(s);

            this.i = i;
            setPrefWidth(40);
            setResizable(false);
            setSortable(false);

            setCellFactory(p -> new HexCell()); // TODO: index cells...
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

            setOnKeyReleased(e -> {
                if (!e.isShiftDown()) return; // handled OK by default
                switch (e.getCode()) {
                    case UP:
                }
            });
        }

        private class HexCell extends TextFieldTableCell<Byte[], String>
        {
            public HexCell() {
                super(new DefaultStringConverter());

                setOnMousePressed(e -> select(getIndex(), HexColumn.this));
                setOnDragDetected(e -> {
                    startFullDrag();
                    indexDrag = false;
                });
                setOnMouseDragEntered(e -> selectRange(getIndex(), HexColumn.this));

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

    private class IndexColumn extends TableColumn<Integer, String>
    {
        public IndexColumn(String name) {
            super(name);

            setEditable(false);
            setPrefWidth(60);

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
                // double-click drag
                addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    e.consume();
                    if (e.getClickCount() > 1 && columns.size() > 1) {
                        select(getIndex(), 1);
                        selectRow(getIndex());
                        indexDrag = true;
                    }
                });
                // drag start
                setOnDragDetected(e -> { if (indexDrag && startRow == getIndex()) startFullDrag(); });
                // drag
                setOnMouseDragEntered(e -> {
                    if (!indexDrag) selectRange(getIndex(), (startRow > getIndex()) ? 1 : columns.size() - 1);
                    else getSelectionModel().selectRange(startRow, getColumn(1),
                            getIndex(), getColumn(columns.size() - 1));
                });

                getStyleClass().add("first-col");
                setAlignment(Pos.CENTER);
            }

            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);

                if (isEmpty() || index < 0) setText(null);
                else setText(String.format("%06d", index));
            }
        }
    }

    private void select(int row, int col) { select(row, getColumn(col)); }
    private void select(int row, HexColumn col) {
        startRow = row;
        endRow = row;
        startCol = col;
        endCol = col;
        getSelectionModel().clearSelection();
        getSelectionModel().select(row, col);
    }

    private void selectRange(int row, int col) { selectRange(row, getColumn(col)); }
    private void selectRange(int row, HexColumn col) {
        endRow = row;
        endCol = col;
        getSelectionModel().clearSelection();
        if (row < startRow) {
            getSelectionModel().selectRange(row, col, row, getColumn(columns.size() - 1));
            getSelectionModel().selectRange(startRow, startCol, startRow, getColumn(1));
            for (int i = row + 1; i < startRow; i++) selectRow(i);
        }
        else if (row > startRow) {
            getSelectionModel().selectRange(row, col, row, getColumn(1));
            getSelectionModel().selectRange(startRow, startCol, startRow, getColumn(columns.size() - 1));
            for (int i = startRow + 1; i < row; i++) selectRow(i);
        }
        else getSelectionModel().selectRange(row, col, startRow, startCol);
    }

    private void selectRow(int row) {
        getSelectionModel().selectRange(row, getColumn(1), row, getColumn(columns.size() - 1));
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
            c.setPrefWidth(40);
            return c;
        });
        for (int i = 0; i < 16; i++) res.getItems().add(String.format("%01X", i));
        res.setEditable(false);
        res.setMinHeight(35);
        res.setMaxHeight(35);
        res.setMaxWidth(40 * 16 + 60 + 15);
        return res;
    }
}
