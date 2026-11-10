import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class SampleRoundRobinPanel extends JPanel {

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Font FONT_BOLD_30 = new Font("Arial", Font.BOLD, 30);
    private static final Font FONT_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Arial", Font.PLAIN, 14);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);

    public SampleRoundRobinPanel(Runnable onBack) {
        setLayout(new BorderLayout());

        add(createTopPanel(onBack), BorderLayout.NORTH);
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(createTitlePanel(), BorderLayout.NORTH);
        centerWrapper.add(createTablePanel(), BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createTopPanel(Runnable onBack) {
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(DIM_BTN_SMALL);
        backButton.setFont(FONT_BOLD_24);
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(160, 200, 255));
        backButton.setContentAreaFilled(false);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);

        Color backNormalColor = new Color(160, 200, 255);
        Color backPressedColor = new Color(120, 160, 220);

        backButton.getModel().addChangeListener(e -> {
            if (backButton.getModel().isPressed()) {
                backButton.setBackground(backPressedColor);
            } else {
                backButton.setBackground(backNormalColor);
            }
        });

        backButton.addActionListener(e -> onBack.run());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.add(backButton);
        return topPanel;
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Sample Round Robin with Time Quantum of 5ms");
        titleLabel.setFont(FONT_BOLD_30);
        titleLabel.setForeground(Color.BLACK);
        
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;  
        gbc.weighty = 1.0; 
        gbc.insets = new Insets(30, 0, 30, 0);
        
        titlePanel.add(titleLabel, gbc);
        return titlePanel;
    }

    private JPanel createTablePanel() {
        String[] headers = {
            "Process ID",
            "Arrival Time",
            "Burst Time",
            "Completion Time",
            "Turn Around Time",
            "Waiting Time",
            "Response Time"
        };

        Object[][] data = {
            {"P1", 0, 21, 32, 32, 11, 0},
            {"P2", 0, 3, 8, 8, 5, 5},
            {"P3", 0, 6, 21, 21, 15, 8},
            {"P4", 0, 2, 15, 15, 13, 13}
        };

        DefaultTableModel tableModel = new DefaultTableModel(data, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable resultTable = new JTable(tableModel);
        resultTable.setFont(FONT_PLAIN_14);
        resultTable.setRowHeight(30);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(Color.GRAY);
        
        resultTable.setRowSelectionAllowed(false);
        resultTable.setColumnSelectionAllowed(false);
        resultTable.setCellSelectionEnabled(false);
        resultTable.setFocusable(false);

        JTableHeader tableHeader = resultTable.getTableHeader();
        tableHeader.setFont(FONT_BOLD_14);
        tableHeader.setPreferredSize(new Dimension(0, 40));
        tableHeader.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultTable.setDefaultRenderer(Object.class, cellRenderer);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20)); 

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(resultTable.getTableHeader(), BorderLayout.NORTH);
        tableWrapper.add(resultTable, BorderLayout.CENTER);
        
        tablePanel.add(tableWrapper, BorderLayout.NORTH);
        
        return tablePanel; 
    }
}