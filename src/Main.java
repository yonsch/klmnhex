import gui.Table;

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
        Table table = new Table(f.getData(), columns);
        table.setDisplayMode(Table.DisplayMode.HEX);
        table.setBorder(null);

        frame.getContentPane().add(table);
        frame.setVisible(true);
    }
}
