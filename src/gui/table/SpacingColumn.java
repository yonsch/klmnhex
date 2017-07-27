package gui.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

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

            c.setOnMousePressed(e1 -> getTableView().getSelectionModel().clearAndSelect(c.getIndex(), this));
            c.setOnDragDetected(e1 -> c.startFullDrag());
            return c;
        });
    }
}
