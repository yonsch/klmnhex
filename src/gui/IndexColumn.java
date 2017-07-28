package gui;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/24/2017.
 *
 * The first column of an HexTable, contains indices of the rows
 */
class IndexColumn extends TableColumn<Byte[], String>
{
    private int radix = 16;
    IndexColumn(String text) {
        super(text);

        setEditable(false);
        setPrefWidth(80);

        setCellFactory(e -> new IndexCell());
        setCellValueFactory(e -> null);
    }

    public void setRadix(int radix) { this.radix = radix; }

    private class IndexCell extends TableCell<Byte[], String>
    {
        IndexCell() {
            super();

            // double-click
            setOnMouseClicked(e -> {
                if (e.getClickCount() <= 1) getTableView().getSelectionModel().clearSelection();
            });
            addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (e.getClickCount() > 1) getTableView().getSelectionModel().clearAndSelect(getIndex());
            });
            setOnDragDetected(e -> startFullDrag());
            setOnMouseDragEntered(e -> getTableView().getSelectionModel().select(getIndex()));

            getStyleClass().add("index-list");
            setAlignment(Pos.CENTER);
        }

        @Override
        public void updateIndex(int index) {
            super.updateIndex(index);

            if (isEmpty() || index < 0) setText(null);
            else setText(String.format("%1$8s", Integer.toString(index * 16, radix).toUpperCase()).replace(' ', '0'));
        }
    }
}
