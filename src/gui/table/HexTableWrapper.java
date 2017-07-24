package gui.table;

import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/24/2017.
 *
 * This class is in charge of displaying a HexTable with its surrounding components.
 */
public class HexTableWrapper extends VBox
{
    private HexTable table;
    private ListView header;

    public HexTableWrapper() {
        super();
        table = new HexTable();
        header = table.createHeader();

        getChildren().add(header);
        getChildren().add(table);

        setVgrow(table, Priority.ALWAYS);
    }

    public ListView getHeader() { return header; }
    public HexTable getTable() { return table; }

    public void setData(HexData data) { table.setItems(data); }
    public void clearSelection() { table.getSelectionModel().clearSelection(); }
    public void setDisplayMode(HexTable.DisplayMode mode) { table.setDisplayMode(mode); }
}
