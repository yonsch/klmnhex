package gui;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/25/2017.
 */
class SpacingColumn extends TableColumn<Byte[], String>
{
    public SpacingColumn() {
        super("");
        setPrefWidth(10);
        setCellFactory(e -> {
            TableCell<Byte[], String> c = new TableCell<>();

            c.addEventFilter(MouseEvent.MOUSE_PRESSED, e1 -> {
                e1.consume();
                if (e1.isShiftDown()) getTableView().getSelectionModel().select(c.getIndex(), this);
                else getTableView().getSelectionModel().clearAndSelect(c.getIndex(), this);
            });
            c.setOnDragDetected(e1 -> c.startFullDrag());
            return c;
        });
    }
}
