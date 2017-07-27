package gui.table;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.util.*;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/22/2017.
 *
 * Unlike the default SelectionModel, HexSelectionModel ensures that the selection is always continuous.
 * HexSelectionModel also has a row selection mode, where the beginning of the selection is a row,
 * which will always stay fully selected.
 */
class HexSelectionModel extends TableView.TableViewSelectionModel<Byte[]>
{
//    private int start = -1, end = -1;
    private ModifiableIntegerProperty start = new ModifiableIntegerProperty(-1),
        end = new ModifiableIntegerProperty(-1);
    private int width, height;

    private TableView.TableViewSelectionModel<Byte[]> original;
    private HashMap<TableColumn, Double> columns = new HashMap<>();
    private ArrayList<TableColumn> indices = new ArrayList<>();
    private Set<TableColumn> ignored = new HashSet<>();
    private Set<Integer> skipped = new HashSet<>();

    // whether the user is currently scrolling (if so, stop him at first/last rows)
    private boolean scrolling = false,
    // whether start is a row
    rowMode = false;

    public HexSelectionModel(TableView<Byte[]> tableView) {
        super(tableView);
        original = tableView.getSelectionModel();

        for (int i = 0; i < tableView.getColumns().size(); i++) columns.put(tableView.getColumns().get(i), (double)i);
        width = columns.size();
        height = tableView.getItems().size();
        tableView.itemsProperty().addListener((obs, oldV, newV) -> height = tableView.getItems().size());
        tableView.getColumns().addListener((InvalidationListener) (obs) -> {
            width = 0;
            columns.clear();
            indices.clear();
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                TableColumn t = tableView.getColumns().get(i);
                if (skipped.contains(i)) columns.put(t, (double)width - 0.5);
                else if (!ignored.contains(t)) {
                    indices.add(t);
                    columns.put(t, (double) width++);
                }
            }
        });

        setSelectionMode(SelectionMode.MULTIPLE);
        setCellSelectionEnabled(true);

        EventHandler<KeyEvent> eventHandler = e -> {
            switch (e.getCode()) {
                case LEFT:
                    e.consume();
                    if (end.decrease().get() < 0) {
                        if (scrolling) end.set(0);
                        else end.set(height * width - 1);
                    }
                    break;
                case RIGHT:
                    e.consume();
                    if (end.increase().get() >= height * width) {
                        if (scrolling) end.set(height * width - 1);
                        else end.set(0);
                    }
                    break;
                case UP:
                    e.consume();
                    if (e.isShiftDown()) end.set(Math.max(end.get() - width, 0));
                    else if (end.get() < width) { if(!scrolling) end.set(end.get() % width + (height - 1) * width); }
                    else end.sub(width);
                    break;
                case DOWN:
                    e.consume();
                    if (e.isShiftDown()) end.set(Math.min(end.get() + width, height * width - 1));
                    else if (end.get() + width >= height * width) { if (!scrolling) end.set(end.get() % width); }
                    else end.add(width);
                    break;
                default: return;
            }
            if (!e.isShiftDown()) start.set(end.get());
            else fixStartRow();
            scrolling = true;
        };
        tableView.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        tableView.addEventFilter(KeyEvent.KEY_RELEASED, e -> scrolling = false);

        start.getProperty().addListener((obs, oldV, newV) -> {
            if ((int)newV == -1) return;
            original.clearSelection();
            original.select((int)newV / width, indices.get((int)newV % width));
        });
        end.getProperty().addListener((obs, oldV, newV) -> {
            if ((int)newV != -1)
                original.select((int)newV / width, indices.get((int)newV % width));
        });
        original.selectedIndexProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            VirtualFlow vf = (VirtualFlow)((TableViewSkin) tableView.getSkin()).getChildren().get(1);
            int first = vf.getFirstVisibleCellWithinViewPort().getIndex(),
                last = vf.getLastVisibleCellWithinViewPort().getIndex(),
                current = end.get() / width;

            if (current > last) vf.scrollTo(current - last + first);
            else if (current < first) vf.scrollTo(current);
        }));
    }

    public void ignore(TableColumn... ignore) {
        int l = ignored.size();
        ignored.addAll(Arrays.asList(ignore));
        width -= ignored.size() - l; // not ignore.length, since there can be double occurrences
        columns.clear();

        int i = 0;
        for (TableColumn t : getTableView().getColumns())
            if (!ignored.contains(t)) columns.put(t, (double) i++);
    }

    public void skip(Integer... skip) { skipped.addAll(Arrays.asList(skip)); }

    private int getIndex(int row, int col) { return row * width + col; }
    private double getIndex(int row, TableColumn col) { return row * width + columns.get(col); }

    @Override
    public ObservableList<TablePosition> getSelectedCells() { return original.getSelectedCells(); }

    @Override
    public boolean isSelected(int row, TableColumn<Byte[], ?> column) {
        if (column == null || ignored.contains(column)) return false;

        double i = getIndex(row, column);
        return (i <= start.get() && i >= end.get()) || (i >= start.get() && i <= end.get());
    }

    @Override
    public void select(int row, TableColumn<Byte[], ?> column) {
        if (row < 0 || column == null || ignored.contains(column)) return;
        end.set((int) getIndex(row, column));
        fixStartRow();

//        original.select(row, column);
    }

    @Override
    public void clearAndSelect(int row, TableColumn<Byte[], ?> column) {
        if (column == null || ignored.contains(column)) return;
        start.set((int) getIndex(row, column));
        end.set(start.get());
        rowMode = false;
    }

    @Override
    public void select(int row) {
        if (row < 0) return;
        end.set(row * width);
        if (end.get() > start.get()) end.add(width - 1);
        if (start != end) fixStartRow();
    }

    @Override
    public void clearAndSelect(int row) {
        if (row < 0) return;
        start.set(row * width);
        end.set(start.get() + width - 1);
        rowMode = true;
    }

    private void fixStartRow() {
        if (!rowMode) return;

        if (end.get() > start.get()) start.sub(start.get() % width);
        else if (start.get() > end.get()) start.sub((start.get() % width) + 1 - width);
        else rowMode = false;
    }

    @Override
    public void clearSelection(int row, TableColumn<Byte[], ?> column) {
        start.set(-1); end.set(-1);             // since selection is continuous, you can't deselect specific cells,
        original.clearSelection(row, column);   // so everything is deselected if you try to
    }

    @Override
    public void clearSelection() {
        start.set(-1); end.set(-1);
        original.clearSelection();
    }

    // manually implemented in an EventHandler of tableView in the constructor
    @Override
    public void selectLeftCell() {}
    @Override
    public void selectRightCell() {}
    @Override
    public void selectAboveCell() {}
    @Override
    public void selectBelowCell() {}

    public IntegerProperty startProperty() { return start.getProperty(); }
    public IntegerProperty endProperty() { return end.getProperty(); }
}
