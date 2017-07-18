import gui.HexTable;

import javax.swing.*;

/**
 * Created by Michael on 7/18/2017.
 */
public class Main
{
    public static void main(String[] args) {
        HexFile f = new HexFile();
        f.open("readme.md");

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setTitle("KLMN Hex Editor");
        frame.setLocationRelativeTo(null);

        String[] columns = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        HexTable table = new HexTable(f.getData(), columns);
        table.setDisplayMode(HexTable.DisplayMode.CHAR);
        table.setBorder(null);

        final JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");

        open.addActionListener(e -> {
            f.open("otherFile");
            table.setData(f.getData());
        });
        save.addActionListener(e -> f.saveAs("test"));
        exit.addActionListener(e -> frame.setVisible(false));

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(table);
        frame.setVisible(true);
    }
}
