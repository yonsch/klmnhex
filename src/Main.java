import gui.Table;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Created by Michael on 7/18/2017.
 */
public class Main
{
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setTitle("KLMN Hex Editor");
        frame.setLocationRelativeTo(null);

        String[] columns = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        Object[][] data = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16},
                {17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32}
        };
        Table table = new Table(data, columns);
        table.setBorder(null);

        frame.getContentPane().add(table);
        frame.setVisible(true);
    }
}
