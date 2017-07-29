import gui.HexTab;
import gui.HexTable;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
//        setPalette();

        BorderPane root = new BorderPane();

        TabPane center = new TabPane();
        // todo: the ability to create 'new' tabs
        Label placeHolder = new Label("KLMNx");
        placeHolder.setFont(Font.font(null, FontWeight.BOLD, 200));
        placeHolder.setPrefSize(802, 700);
        placeHolder.setAlignment(Pos.CENTER);
        placeHolder.getStyleClass().add("place-holder");
        BooleanBinding bb = Bindings.isEmpty(center.getTabs());
        placeHolder.visibleProperty().bind(bb);
        placeHolder.managedProperty().bind(bb);

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

            HexTab t = new HexTab(new HexFile(selected.getAbsolutePath()));
            t.setText(selected.getName());
            center.getTabs().add(t);
            center.getSelectionModel().select(t);

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

            if (selected != null) {
                ((HexFile) ((HexTab) center.getSelectionModel().getSelectedItem()).getData()).saveAs(selected.getAbsolutePath());
            }
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
        save.setOnAction(e ->
                ((HexFile) ((HexTab) center.getSelectionModel().getSelectedItem()).getData()).save()
        );
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
            if (newV == null) return;
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
