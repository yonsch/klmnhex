import gui.HexTable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws Exception {

        ArrayList<HexFile> files = new ArrayList<>();
        HexFile ff = new HexFile("readme.md");
        files.add(ff);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setTitle("KLMN Hex Editor");
        frame.setLocationRelativeTo(null);

        HexTable table = new HexTable(files.get(0).getDataArray());
        table.setDisplayMode(HexTable.DisplayMode.HEX);
        table.setBorder(null);

        final JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");

        JMenuItem selectViewMode = new JMenu("Select View Mode");
        JMenuItem[] modes = {
                new JCheckBoxMenuItem("Hex Mode", true),
                new JCheckBoxMenuItem("Signed Decimal Mode"),
                new JCheckBoxMenuItem("Unsigned Decimal Mode"),
                new JCheckBoxMenuItem("Character Mode")
        };

        for (int i = 0; i < modes.length; i++) {
            final int n = i;
            modes[n].addActionListener(e -> {
                for (int j = 0; j < modes.length; j++)
                    modes[j].setSelected(j == n);
                for (HexTable.DisplayMode j : HexTable.DisplayMode.values())
                    if (j.ordinal() == n) table.setDisplayMode(j);
            });
            selectViewMode.add(modes[n]);
        } viewMenu.add(selectViewMode);

        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");

        open.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose a File To Open");
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                HexFile file = new HexFile(chooser.getSelectedFile().getAbsolutePath());
                files.clear();
                files.add(file);
                table.setData(file.getDataArray());
            }
        });
        save.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose a File To Save Your Data To");
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION)
                files.get(0).saveAs(chooser.getSelectedFile().getAbsolutePath());
        });
        exit.addActionListener(e -> frame.dispose());

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);

        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(table);
        frame.setVisible(true);
    }
}

