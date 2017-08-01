package gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/31/2017.
 */
public class RecentFilesManager
{
    private static final Path PATH = Paths.get("./_recentfiles");
    private static final int MAX_SIZE = 6;

    private ObservableList<String> files;

    public RecentFilesManager() {
        try { files = FXCollections.observableArrayList(new String(Files.readAllBytes(PATH)).split("\n"));}
        catch (Exception e) {
            System.err.println("Warning: Could Not Read Recent Files Info");
        }
    }

    public int size() { return files.size(); }

    public String getFile(int index) { return files.get(index); }
    public void addFile(String path) {
        if (files.contains(path)) files.remove(path);

        files.add(path);
        if (files.size() > MAX_SIZE) files.remove(0);

        try { Files.write(PATH, buildString().getBytes()); }
        catch (IOException e) {
            System.err.println("Error: Could Not Save Recent Files Info");
            e.printStackTrace();
        }
    }

    public interface OpenRunnable { void open(String s); }
    public Menu bindMenu(Menu menu, OpenRunnable openRunnable) {
        ListChangeListener<String> listener = ignored -> {
            menu.getItems().clear();
            for (int i = files.size() - 1; i >= 0; i--) {
                String f = files.get(i);
                MenuItem m = new MenuItem(f.substring(f.lastIndexOf('\\') + 1));
                m.setOnAction(e -> {
                    openRunnable.open(f);
                    addFile(f); // move to top
                });
                m.setMnemonicParsing(false);
                menu.getItems().add(m);
            }
        };
        listener.onChanged(null);
        files.addListener(listener);
        return menu;
    }

    private String buildString() {
        StringBuilder s = new StringBuilder();
        files.forEach(f -> s.append(f).append('\n'));
        return s.toString();
    }
}
