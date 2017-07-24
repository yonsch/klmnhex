import gui.table.HexTable;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
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
        table.setItems(f[0]);

        MenuBar menu = new MenuBar();
        menu.useSystemMenuBarProperty().set(true);
        Menu file = new Menu("_File");
        menu.getMenus().add(file);
        MenuItem open = new MenuItem("_Open");
        file.getItems().add(open);
        open.setAccelerator(KeyCombination.keyCombination("ctrl+o"));
        open.setOnAction(e ->  {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Choose a Binary File To Open");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Types", "*.*"));
            File selected = chooser.showOpenDialog(primaryStage);

            if (selected == null) return;

            f[0] = new HexFile(selected.getAbsolutePath());
            table.setItems(f[0]);
            table.getSelectionModel().clearSelection();

            primaryStage.setTitle("KLMN Hex Editor (" + selected.getAbsolutePath() + ")");
        });
        MenuItem saveAs = new MenuItem("Save _As");
        saveAs.setAccelerator(KeyCombination.keyCombination("ctrl+alt+s"));
        file.getItems().add(saveAs);
        saveAs.setOnAction(e ->  {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Choose a Binary File To Save To");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Types", "*.*"));
            File selected = chooser.showSaveDialog(primaryStage);

            if (selected != null) f[0].saveAs(selected.getAbsolutePath());
        });
        MenuItem save = new MenuItem("_Save");
        save.setAccelerator(KeyCombination.keyCombination("ctrl+s"));
        file.getItems().add(save);
        MenuItem seperator0 = new SeparatorMenuItem();
        file.getItems().add(seperator0);
        MenuItem exit = new MenuItem("_Exit");
        exit.setAccelerator(KeyCombination.keyCombination("alt+f4"));
        file.getItems().add(exit);
        exit.setOnAction(e -> System.exit(0));
        save.setOnAction(e -> f[0].save());
        Menu view = new Menu("_View");
        menu.getMenus().add(view);
        Menu selectMode = new Menu("Select View _Mode");
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

        view.getItems().add(selectMode);

        root.setTop(menu);
        root.setCenter(table);
        Scene scene = new Scene(root, 1100, 700);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) System.exit(0);
        });
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(close -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

    private class SB
    {
        private ScrollBar scrollBar = new ScrollBar(), old;
        private HexTable table;
        private HBox hBox;
        private Pane center;
        private BorderPane root;
        public SB(ScrollBar old, HexTable table, HBox hBox, Pane center, BorderPane root) {
            this.old = old;
            this.hBox = hBox;
            this.table = table;
            this.center = center;
            this.root = root;

            ChangeListener<Scene> initializer = new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene)  {
                    if (newScene != null) {
                        scrollBar.applyCss();
                        scrollBar.getParent().layout();
                        Pane thumb = (Pane) scrollBar.lookup(".thumb");
                        System.out.println(thumb); // <-- No longer null
                        scrollBar.sceneProperty().removeListener(this);

                        scrollBar.setTranslateX(-75);
                        scrollBar.setOrientation(Orientation.VERTICAL);
                        scrollBar.minProperty().bind(old.minProperty());
                        scrollBar.maxProperty().bind(old.maxProperty());
                        old.valueProperty().bindBidirectional(scrollBar.valueProperty());

                        hBox.getChildren().addAll(table, scrollBar);
                        center.getChildren().clear();
                        center.getChildren().add(table.createHeader());
                        center.getChildren().add(hBox);

                        root.setCenter(center);
                    }
                }
            }; scrollBar.sceneProperty().addListener(initializer);
        }
    }
}
