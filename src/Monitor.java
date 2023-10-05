import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class Monitor {
    private static JFrame mainFrame;
    private static JTable workersTable;
    private static DefaultTableModel tableModel;

    public Monitor() {
        UIManager.put("Table.font", new Font("Arial", Font.PLAIN,14));
        UIManager.put("Table.cellRenderer", "centeredTextRenderer");
        mainFrame = new JFrame("Workers Monitor");
        mainFrame.setSize(1000, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] columnNames = {"Worker Name", "Pods", "CPU", "Memory", "Disk"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 1) {
                    return String.class;
                } else {
                    return Integer.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        workersTable = new JTable(tableModel);
        workersTable.getColumnModel().getColumn(2).setCellRenderer(new ProgressBarRenderer());
        workersTable.getColumnModel().getColumn(3).setCellRenderer(new ProgressBarRenderer());
        workersTable.getColumnModel().getColumn(4).setCellRenderer(new ProgressBarRenderer());
        workersTable.setRowHeight(40);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        workersTable.setDefaultRenderer(Object.class, centerRenderer);

        JTableHeader header = workersTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD,16));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setDefaultRenderer(centerRenderer);

        mainFrame.add(new JScrollPane(workersTable));
        mainFrame.setVisible(true);
    }

    public static void addRow(String name, ArrayList<String> pods, int cpuPercent, int memPercent, int disk) {
        tableModel.addRow(new Object[]{name, pods, cpuPercent, memPercent, disk});
    }

    public static void setValueAt(Object value, int row, int column) {
        workersTable.setValueAt(value, row, column);
    }

    private static class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressBarRenderer() {
            super(0, 100);
            setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Integer) {
                setValue((Integer) value);
                setString(getValue() + "%");
            }
            setForeground(Color.BLUE);
            setBackground(Color.LIGHT_GRAY);
            this.setFont(new Font("Arial", Font.BOLD,16));
            return this;
        }
    }
}