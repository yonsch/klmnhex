import gui.HexTab;
import gui.HexTable;
import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class KLMNx extends Application
{
    // Now Using--  J-F-X! Mm hm ಠ_ರೃ so fancy

    private void setPalette() throws Exception {
        Path path = Paths.get("src/style.css");

        int[] palette = {0x34363E, 0xE7F9F8, 0xA9B3CE, 0x7284A8, 0x9E788F}; // change palette easily using this
        String[] names = {"-palette-dark-color", "-palette-light-color", "-palette-midtone1-color", "palette-midtone2-color", "palette-accent-color"};

        String css = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        for (int i = 0; i < 5; i++)
            css = css.replaceAll(names[i] + ":#.*?;", String.format(names[i] + ":#%06X;", palette[i]));
        Files.write(path, css.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("KLMN Hex Editor");

        BorderPane root = new BorderPane();

        TabPane center = new TabPane();
        // todo: the ability to create 'new' tabs
        VBox placeHolder = new VBox();
        Label name = new Label("KLMNx");
        name.setFont(Font.font("System", FontWeight.BOLD, 200));
        placeHolder.prefWidthProperty().bind(root.widthProperty());
        placeHolder.prefHeightProperty().bind(root.heightProperty());
        name.setPrefWidth(800);
        name.setAlignment(Pos.CENTER);
        Label description = new Label("The KLMN hex editor (pronounced KALMAN).\n" +
                "\n" +
                "The KLMN hex editor is destined to be the worlds best free hex editor.\n" +
                "KLMNx is designed to be powerful, light-weight and easy to use.\n(todo: actual useful information)");
        description.setFont(Font.font("System", FontWeight.NORMAL, 23));
        placeHolder.getChildren().addAll(name, description);
        placeHolder.getStyleClass().add("place-holder");
        BooleanBinding noTabs = Bindings.isEmpty(center.getTabs());
        placeHolder.visibleProperty().bind(noTabs);
        placeHolder.managedProperty().bind(noTabs);

        Stack<String> recentFiles = new Stack<>(); // should be a queue? NOPE, it's redundant anyway, todo
        try { recentFiles.addAll(Arrays.asList(new String(Files.readAllBytes(Paths.get("_recentfiles")), StandardCharsets.UTF_8).split("\n"))); }
        catch (Exception ignored) {} // file not exists, will be created later

        MenuBar menu = new MenuBar();
        menu.useSystemMenuBarProperty().set(true);
        Menu file = new Menu("_File");
        menu.getMenus().add(file);
        MenuItem open = new MenuItem("_Open");
        Menu openRecent = new Menu("Open _Recent");
        openRecent.disableProperty().bind(Bindings.isEmpty(openRecent.getItems()));
        recentFiles.forEach(f -> {
            MenuItem recentFile = new MenuItem(f.substring(f.lastIndexOf('\\') + 1));
            recentFile.setOnAction(e -> {
                HexTab t = new HexTab(new HexFile(f));
                t.setText(recentFile.getText());
                t.setFullTitle("KLMN Hex Editor (" + f + ")");
                center.getTabs().add(t);
                center.getSelectionModel().select(t);
            });
            openRecent.getItems().add(recentFile);
        });
        file.getItems().addAll(open, openRecent);
        open.setAccelerator(KeyCombination.keyCombination("ctrl+o"));
        open.setOnAction(e ->  {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Choose a Binary File To Open");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Types", "*.*"));
            File selected = chooser.showOpenDialog(stage);

            if (selected == null) return;

            HexTab t = new HexTab(new HexFile(selected.getAbsolutePath()));
            t.setText(selected.getName());
            t.setFullTitle("KLMN Hex Editor (" + selected.getAbsolutePath() + ")");
            center.getTabs().add(t);
            center.getSelectionModel().select(t);

            recentFiles.push(selected.getAbsolutePath());
            if (recentFiles.size() > 6) recentFiles.remove(0);

            openRecent.getItems().clear();
            final String[] recentFilesString = { "" };
            recentFiles.forEach(f -> {
                recentFilesString[0] = f + '\n' + recentFilesString[0];
                MenuItem recentFile = new MenuItem(f.substring(f.lastIndexOf('\\') + 1));
                recentFile.setOnAction(e1 -> { // remaining bugs: duplicates, order doesn't change when opening
                    HexTab t1 = new HexTab(new HexFile(f));
                    t1.setText(recentFile.getText());
                    t1.setFullTitle("KLMN Hex Editor (" + f + ")");
                    center.getTabs().add(t1);
                    center.getSelectionModel().select(t1);
                });
                openRecent.getItems().add(0, recentFile);
            });
            try { Files.write(Paths.get("_recentfiles"), recentFilesString[0].getBytes()); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        MenuItem saveAs = new MenuItem("Save _As");
        saveAs.setAccelerator(KeyCombination.keyCombination("ctrl+alt+s"));
        file.getItems().add(saveAs);
        saveAs.setOnAction(e ->  {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Choose a Binary File To Save To");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Types", "*.*"));
            File selected = chooser.showSaveDialog(stage);

            if (selected != null) {
                ((HexFile) ((HexTab) center.getSelectionModel().getSelectedItem()).getData()).saveAs(selected.getAbsolutePath());
            }
        });
        MenuItem save = new MenuItem("_Save");
        save.setAccelerator(KeyCombination.keyCombination("ctrl+s"));
        file.getItems().add(save);
        MenuItem seperator0 = new SeparatorMenuItem();
        file.getItems().add(seperator0);
        MenuItem closeTab = new MenuItem("Close _Tab");
        closeTab.setAccelerator(KeyCombination.keyCombination("ctrl+w"));
        closeTab.setOnAction(e -> center.getTabs().remove(center.getSelectionModel().getSelectedItem()));
        closeTab.disableProperty().bind(noTabs);
        file.getItems().add(closeTab);
        MenuItem exit = new MenuItem("_Exit");
        exit.setAccelerator(KeyCombination.keyCombination("alt+f4"));
        file.getItems().add(exit);
        exit.setOnAction(e -> System.exit(0));
        save.setOnAction(e ->
                ((HexFile) ((HexTab) center.getSelectionModel().getSelectedItem()).getData()).save()
        );
        Menu edit = new Menu("_Edit");
        menu.getMenus().add(edit);
        MenuItem undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("ctrl+z"));
        undo.setOnAction(e -> {
            HexTab t = (HexTab) center.getSelectionModel().getSelectedItem();
            if (t != null) t.undo();
        });
        edit.getItems().add(undo);
        MenuItem redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("ctrl+shift+z"));
        edit.getItems().add(redo);
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
                    if (j.ordinal() == n) ((HexTab) center.getSelectionModel().getSelectedItem()).setDisplayMode(j);
            });
        }
        center.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                stage.setTitle("KLMN Hex Editor");
                return;
            }
            stage.setTitle(((HexTab) newV).getFullTitle());
            HexTable.DisplayMode m = ((HexTab) center.getSelectionModel().getSelectedItem()).getTable().getDisplayMode();
            switch (m) {
                case HEX: modes[0].getOnAction().handle(null); break;
                case DECIMAL: modes[1].getOnAction().handle(null); break;
                case UDECIMAL: modes[2].getOnAction().handle(null);
            }
        });
        Menu indexRadix = new Menu("Select _Index Base");
        view.getItems().add(indexRadix);
        CheckMenuItem base16 = new CheckMenuItem("Hexadecimal");
        CheckMenuItem base10 = new CheckMenuItem("Decimal"); // todo: select/ deselect these based on tab
        base16.setSelected(true);
        base10.setOnAction(e -> {
            base10.setSelected(true);
            base16.setSelected(false);
            for (Tab t : center.getTabs()) ((HexTab) t).setRadix(10);
        });
        base16.setOnAction(e -> {
            base16.setSelected(true);
            base10.setSelected(false);
            for (Tab t : center.getTabs()) ((HexTab) t).setRadix(16);
        }); indexRadix.getItems().addAll(base16, base10);

        view.getItems().add(selectMode);

        root.setTop(menu);
        root.setCenter(new VBox(placeHolder, center));
        VBox.setVgrow(center, Priority.ALWAYS);
        Scene scene = new Scene(root, 802, 700);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {   // eventually will be removed
            if (e.getCode() == KeyCode.ESCAPE) System.exit(0);
            if (e.isControlDown() && !e.isAltDown()) {
                switch (e.getCode()) {
                    case TAB:
                        if (center.getTabs().size() > 1) {
                            if (!e.isShiftDown()) center.getSelectionModel().select(
                                    (center.getSelectionModel().getSelectedIndex()) % center.getTabs().size());
                            else center.getSelectionModel().select(
                                        (center.getSelectionModel().getSelectedItem()));
                        }
                        else if (e.isShiftDown()) return;
                        break;
                    case DIGIT1:
                        if (center.getTabs().size() > 0) center.getSelectionModel().select(0); break;
                    case DIGIT2:
                        if (center.getTabs().size() > 1) center.getSelectionModel().select(1); break;
                    case DIGIT3:
                        if (center.getTabs().size() > 2) center.getSelectionModel().select(2); break;
                    case DIGIT4:
                        if (center.getTabs().size() > 3) center.getSelectionModel().select(3); break;
                    case DIGIT5:
                        if (center.getTabs().size() > 4) center.getSelectionModel().select(4); break;
                    case DIGIT6:
                        if (center.getTabs().size() > 5) center.getSelectionModel().select(5); break;
                    case DIGIT7:
                        if (center.getTabs().size() > 6) center.getSelectionModel().select(6); break;
                    case DIGIT8:
                        if (center.getTabs().size() > 7) center.getSelectionModel().select(7); break;
                    case DIGIT9:
                        if (center.getTabs().size() > 1) center.getSelectionModel().select(
                                center.getTabs().get(center.getTabs().size() - 1)); break;
                }
            }
        });
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);

        stage.setOnCloseRequest(close -> System.exit(0));
        stage.setScene(scene);
        stage.show();
    }
}
