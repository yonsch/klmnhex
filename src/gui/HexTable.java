package gui;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Created by Michael on 7/18/2017.
 */
public class HexTable extends JScrollPane
{
    public enum DisplayMode { DECIMAL, HEX, CHAR }

    private DisplayMode displayMode = DisplayMode.DECIMAL;
    private JTable table;

    public HexTable(Object[][] data, String[] headers) {
        this(new JTable(data, headers));
        table.setModel(new HexTableModel(data));
    }

    private HexTable(JTable table) {
        super(table);

        this.table = table;
        table.setShowVerticalLines(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setDefaultRenderer(Object.class, new HexCellRenderer());
        JTable rowTable = new RowNumberTable(table);
        setRowHeaderView(rowTable);
        setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
    }

    public void setDisplayMode(DisplayMode displayMode) { this.displayMode = displayMode; }

    public void setData(Object[][] data) { ((HexTableModel) table.getModel()).setData(data); }

    /* a cell editor that supports different display modes */
    private class HexCellRenderer extends DefaultTableCellRenderer
    {
        public HexCellRenderer() { super(); }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            setHorizontalAlignment(SwingConstants.CENTER);

            if (value != null) {
                byte v = (byte) value;
                switch (displayMode) {
                    case DECIMAL:
                        setText(String.format("%d", v));
                        break;
                    case HEX:
                        setText(String.format("%02X", v));
                        break;
                    case CHAR:
                        setText(String.format("%c", v));
                }
            } else setText("oops");

            setFont(getFont().deriveFont(Font.PLAIN));
            if (isSelected) setBackground(table.getSelectionBackground());
            else setBackground(table.getBackground());

            return this;
        }
    }

    /* a table model that supports changing table data */
    private class HexTableModel extends AbstractTableModel {
        private Object[][] data;

        public HexTableModel(Object[][] data) { this.data = data; }

        @Override
        public int getRowCount() { return data.length; }
        @Override
        public int getColumnCount() { return data[0].length;}

        @Override
        public boolean isCellEditable(int row, int col) { return false; }

        @Override
        public Object getValueAt(int row, int col) { return data[row][col]; }

        public void setData(Object[][] data) {
            this.data = data;
            fireTableDataChanged();
        }
    }

    /*
    *  Blatantly Stolen From http://www.camick.com/java/source/RowNumberTable.java
    *
    *	Use a JTable as a renderer for row numbers of a given main table.
    *  This table must be added to the row header of the scrollpane that
    *  contains the main table.
    */
    private static class RowNumberTable extends JTable
            implements ChangeListener, PropertyChangeListener, TableModelListener
    {
        private JTable main;

        public RowNumberTable(JTable table) {
            main = table;
            main.addPropertyChangeListener(this);
            main.getModel().addTableModelListener(this);

            setFocusable(false);
            setAutoCreateColumnsFromModel(false);
            setSelectionModel(main.getSelectionModel());


            TableColumn column = new TableColumn();
            column.setHeaderValue(" ");
            addColumn(column);
            column.setCellRenderer(new RowNumberRenderer());

            getColumnModel().getColumn(0).setPreferredWidth(50);
            setPreferredScrollableViewportSize(getPreferredSize());
        }

        @Override
        public void addNotify() {
            super.addNotify();

            Component c = getParent();

            //  Keep scrolling of the row table in sync with the main table.

            if (c instanceof JViewport) {
                JViewport viewport = (JViewport) c;
                viewport.addChangeListener(this);
            }
        }

        /*
         *  Delegate method to main table
         */
        @Override
        public int getRowCount() {
            return main.getRowCount();
        }

        @Override
        public int getRowHeight(int row) {
            int rowHeight = main.getRowHeight(row);

            if (rowHeight != super.getRowHeight(row)) {
                super.setRowHeight(row, rowHeight);
            }

            return rowHeight;
        }

        /*
         *  No model is being used for this table so just use the row number
         *  as the value of the cell.
         */
        @Override
        public Object getValueAt(int row, int column) {
            return Integer.toString(row + 1);
        }

        /*
         *  Don't edit data in the main TableModel by mistake
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /*
         *  Do nothing since the table ignores the model
         */
        @Override
        public void setValueAt(Object value, int row, int column) {}

        //
//  Implement the ChangeListener
//
        public void stateChanged(ChangeEvent e) {
            //  Keep the scrolling of the row table in sync with main table

            JViewport viewport = (JViewport) e.getSource();
            JScrollPane scrollPane = (JScrollPane) viewport.getParent();
            scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
        }

        //
//  Implement the PropertyChangeListener
//
        public void propertyChange(PropertyChangeEvent e) {
            //  Keep the row table in sync with the main table

            if ("selectionModel".equals(e.getPropertyName())) {
                setSelectionModel(main.getSelectionModel());
            }

            if ("rowHeight".equals(e.getPropertyName())) {
                repaint();
            }

            if ("model".equals(e.getPropertyName())) {
                main.getModel().addTableModelListener(this);
                revalidate();
            }
        }

        //
//  Implement the TableModelListener
//
        @Override
        public void tableChanged(TableModelEvent e) {
            revalidate();
        }

        /*
         *  Attempt to mimic the table header renderer
         */
        private static class RowNumberRenderer extends DefaultTableCellRenderer
        {
            public RowNumberRenderer() {
                setHorizontalAlignment(JLabel.CENTER);
            }

            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();

                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                }

                if (isSelected) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }

                setText((value == null) ? "" : value.toString());
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));

                return this;
            }
        }
    }
}