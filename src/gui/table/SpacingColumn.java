package gui.table;

import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/25/2017.
 */
public class SpacingColumn extends TableColumn
{
    public SpacingColumn() {
        super("");
        setPrefWidth(10);
        setCellFactory(e -> {
            TableCell c = new TableCell();
            c.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
            return c;
        });
    }
}
