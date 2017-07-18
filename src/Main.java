import gui.Table;
import javax.swing.*;


public class Main
{
    public static void main(String[] args) {
            HexFile f = new HexFile();
            f.open("pig.stl");
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(700, 500);
            frame.setTitle("KLMN Hex Editor");
            frame.setLocationRelativeTo(null);

            String[] columns = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
            Object[][] data = f.getData();
            Table table = new Table(data, columns);
            table.setBorder(null);

            frame.getContentPane().add(table);
            frame.setVisible(true);
    }
}
