package gui.table;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    private int start = -1, end = -1, width, height;

    private TableView.TableViewSelectionModel<Byte[]> original;
    private HashMap<TableColumn, Integer> columns = new HashMap<>();
    private int ignoredSize = 0;
    private Set<TableColumn> ignored = new HashSet<>();
    private Set<Integer> skipped = new HashSet<>();

    // whether the user is currently scrolling (if so, stop him at first/last rows)
    private boolean scrolling = false,
    // whether start is a row
    rowMode = false;

    public HexSelectionModel(TableView<Byte[]> tableView) {
        super(tableView);
        original = tableView.getSelectionModel();

        for (int i = 0; i < tableView.getColumns().size(); i++) columns.put(tableView.getColumns().get(i), i);
        width = columns.size();
        height = tableView.getItems().size();
        tableView.itemsProperty().addListener((obs, oldV, newV) -> height = tableView.getItems().size());

        setSelectionMode(SelectionMode.MULTIPLE);
        setCellSelectionEnabled(true);

        EventHandler<KeyEvent> eventHandler = e -> {
            switch (e.getCode()) {
                case LEFT:
                    e.consume();
                    if (--end < 0) {
                        if (scrolling) end = 0;
                        else end = height * width - 1;
                    }
                    break;
                case RIGHT:
                    e.consume();
                    if (++end >= height * width) {
                        if (scrolling) end = height * width - 1;
                        else end = 0;
                    }
                    break;
                case UP:
                    e.consume();
                    if (e.isShiftDown()) end = Math.max(end - width, 0);
                    else if (end < width) { if(!scrolling) end = end % width + (height - 1) * width; }
                    else end -= width;
                    break;
                case DOWN:
                    e.consume();
                    if (e.isShiftDown()) end = Math.min(end + width, height * width - 1);
                    else if (end + width >= height * width) { if (!scrolling) end = end % width; }
                    else end += width;
                    break;
                default: return;
            }
            if (!e.isShiftDown()) start = end;
            else fixStartRow();
            original.select(end / width, tableView.getColumns().get(end % width + ignoredSize));
            scrolling = true;
        };
        tableView.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            eventHandler.handle(e);
            if (skipped.contains(end % width + ignoredSize)) eventHandler.handle(e);
        });
        tableView.addEventFilter(KeyEvent.KEY_RELEASED, e -> scrolling = false);

        original.selectedIndexProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            VirtualFlow vf = (VirtualFlow)((TableViewSkin) tableView.getSkin()).getChildren().get(1);
            int first = vf.getFirstVisibleCellWithinViewPort().getIndex(),
                last = vf.getLastVisibleCellWithinViewPort().getIndex(),
                current = end / width;

            if (current > last) vf.scrollTo(current - last + first);
            else if (current < first) vf.scrollTo(current);
        }));
    }

    public void ignore(TableColumn... ignore) {
        int l = ignored.size();
        ignored.addAll(Arrays.asList(ignore));
        ignoredSize = ignored.size();
        width -= ignoredSize - l;
        columns.clear();

        int i = 0;
        for (TableColumn t : getTableView().getColumns())
            if (!ignored.contains(t)) columns.put(t, i++);
    }

    public void skip(Integer... skip) { skipped.addAll(Arrays.asList(skip)); }

    private int getIndex(int row, int col) { return row * width + col; }
    private int getIndex(int row, TableColumn col) { return row * width + columns.get(col); }

    @Override
    public ObservableList<TablePosition> getSelectedCells() { return original.getSelectedCells(); }

    @Override
    public boolean isSelected(int row, TableColumn<Byte[], ?> column) {
        if (column == null || ignored.contains(column)) return false;

        int i = getIndex(row, column);
        return (i <= start && i >= end) || (i >= start && i <= end);
    }

    @Override
    public void select(int row, TableColumn<Byte[], ?> column) {
        if (row < 0 || column == null || ignored.contains(column)) return;
        end = getIndex(row, column);
        fixStartRow();

        original.select(row, column);
    }

    @Override
    public void clearAndSelect(int row, TableColumn<Byte[], ?> column) {
        if (column == null || ignored.contains(column)) return;
        start = end = getIndex(row, column);
        rowMode = false;

        original.clearSelection();
        original.select(row, column);  // for some reason, original.clearAndSelect() doesn't behave properly
    }

    @Override
    public void select(int row) {
        if (row < 0) return;
        end = row * width;
        if (end > start) end += width - 1;
        if (start != end) fixStartRow();

        original.select(row);
    }

    @Override
    public void clearAndSelect(int row) {
        if (row < 0) return;
        start = row * width;
        end = start + width - 1;
        rowMode = true;

        original.clearSelection();
        original.select(row);  // for some reason, original.clearAndSelect() doesn't behave properly
    }

    private void fixStartRow() {
        if (!rowMode) return;

        if (end > start) start -= start % width;
        else if (start > end) start -= (start % width) + 1 - width;
        else rowMode = false;
    }

    @Override
    public void clearSelection(int row, TableColumn<Byte[], ?> column) {
        start = end = -1;                       // since selection is continuous, you can't deselect specific cells,
        original.clearSelection(row, column);   // so everything is deselected if you try to
    }

    @Override
    public void clearSelection() {
        start = end = -1;
        original.clearSelection();
    }

    public TableView.TableViewSelectionModel<Byte[]> getOriginal() { return original; }

    // manually implemented in an EventHandler of tableView in the constructor
    @Override
    public void selectLeftCell() {}
    @Override
    public void selectRightCell() {}
    @Override
    public void selectAboveCell() {}
    @Override
    public void selectBelowCell() {}
}
