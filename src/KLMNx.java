import gui.HexTableFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
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
        HexTableFX newTable = new HexTableFX(f[0].getDataArray());

        String poo = ".menu, .menu-item, .context-menu {\n-fx-background-color: red;\n}";
        String menuStyle = "-fx-background-color: #666666;\n-fx-text-fill: #EEEEEE;";
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
            newTable.setData(f[0].getDataArray());
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
                new CheckMenuItem("Character Mode"),
        }; selectMode.getItems().addAll(modes);
        modes[0].setSelected(true);
        for (int i = 0; i < modes.length; i++) {
            final int n = i;
            modes[n].setOnAction(e -> {
                for (int j = 0; j < modes.length; j++)
                    modes[j].setSelected(j == n);
                for (HexTableFX.DisplayMode j : HexTableFX.DisplayMode.values())
                    if (j.ordinal() == n) newTable.setDisplayMode(j);
            });
        }
        newTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        view.getItems().add(selectMode);

        root.setTop(menu);
        root.setCenter(newTable);
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
