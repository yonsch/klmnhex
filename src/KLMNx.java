import gui.HexTable;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class KLMNx extends Application
{
    // Now Using--  J-F-X! Mm hm ಠ_ರೃ so fancy

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("KLMN Hex Editor");

        BorderPane root = new BorderPane();

        final HexFile[] f = new HexFile[1];
        f[0] = new HexFile("readme.md");
        HexTable table = new HexTable();
        HexTable charTable = new HexTable();
        charTable.setDisplayMode(HexTable.DisplayMode.CHAR);

        MenuBar menu = new MenuBar();
        menu.useSystemMenuBarProperty().set(true);
        Menu file = new Menu("File");
        menu.getMenus().add(file);
        MenuItem open = new MenuItem("Open");
        file.getItems().add(open);
        open.setOnAction(e ->  {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Choose a Binary File To Open");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Types", "*.*"));
            File selected = chooser.showOpenDialog(primaryStage);

            if (selected == null) return;

            f[0] = new HexFile(selected.getAbsolutePath());
            table.setData(f[0]);
            charTable.setData(f[0]);

            primaryStage.setTitle("KLMN Hex Editor (" + selected.getAbsolutePath() + ")");
        });
        MenuItem saveAs = new MenuItem("Save As");
        file.getItems().add(saveAs);
        saveAs.setOnAction(e ->  {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Choose a Binary File To Save To");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Types", "*.*"));
            File selected = chooser.showSaveDialog(primaryStage);

            if (selected != null) f[0].saveAs(selected.getAbsolutePath());
        });
        MenuItem save = new MenuItem("Save");
        file.getItems().add(save);
        MenuItem seperator0 = new SeparatorMenuItem();
        file.getItems().add(seperator0);
        MenuItem exit = new MenuItem("Exit");
        file.getItems().add(exit);
        exit.setOnAction(e -> System.exit(0));
        save.setOnAction(e -> f[0].save());
        Menu view = new Menu("View");
        menu.getMenus().add(view);
        Menu selectMode = new Menu("Select View Mode");
        CheckMenuItem[] modes = {
                new CheckMenuItem("Hex Mode"),
                new CheckMenuItem("Signed Decimal Mode"),
                new CheckMenuItem("Unsigned Decimal Mode"),
        }; selectMode.getItems().addAll(modes);
        modes[0].setSelected(true);
        for (int i = 0; i < modes.length; i++) {
            final int n = i;
            modes[n].setOnAction(e -> {
                for (int j = 0; j < modes.length; j++)
                    modes[j].setSelected(j == n);
                for (HexTable.DisplayMode j : HexTable.DisplayMode.values())
                    if (j.ordinal() == n) table.setDisplayMode(j);
            });
        }
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        view.getItems().add(selectMode);

        root.setTop(menu);
        VBox center = new VBox();
        center.getChildren().add(table.createHeader());
        center.getChildren().add(table);
        root.setCenter(center);
//        root.setRight(charTable);
        Scene scene = new Scene(root, 725, 500);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) System.exit(0);
        });
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(t -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
