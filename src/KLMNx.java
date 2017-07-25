import gui.table.HexTable;
import gui.table.HexTableWrapper;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class KLMNx extends Application
{
    // Now Using--  J-F-X! Mm hm ಠ_ರೃ so fancy

    private void setPalette() throws Exception {
        Path path = Paths.get("src/style.css");

//        int[] palette = {0x32292F, 0xF0F7F4, 0x99E1D9, 0x70ABAF, 0x705D56}; //default
//        int[] palette = {0x0, 0xFFFFFF, 0xCCCCCC, 0xAAAAAA, 0xDD1133}; // international yummies
        int[] palette = {0x34363E, 0xE7F9F8, 0xA9B3CE, 0x7284A8, 0x9E788F}; // erik
        String[] names = {"-palette-dark-color", "-palette-light-color", "-palette-midtone1-color", "palette-midtone2-color", "palette-accent-color"};

        String css = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        for (int i = 0; i < 5; i++)
            css = css.replaceAll(names[i] + ":#.*?;", String.format(names[i] + ":#%06X;", palette[i]));
        Files.write(path, css.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("KLMN Hex Editor");
        setPalette();

        BorderPane root = new BorderPane();

        final HexFile[] f = new HexFile[1];
        f[0] = new HexFile("readme.md");
        HexTableWrapper table = new HexTableWrapper();
        table.setData(f[0]);

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
            table.setData(f[0]);
            table.clearSelection();

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
        root.setMinWidth(table.getTable().getMaxWidth());
        Scene scene = new Scene(root, table.getTable().getMaxWidth(), 700);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) System.exit(0);
        });
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(close -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
