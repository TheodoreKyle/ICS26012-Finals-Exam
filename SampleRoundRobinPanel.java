import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SampleRoundRobinPanel extends JPanel {

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Font FONT_BOLD_30 = new Font("Arial", Font.BOLD, 30);
    private static final Font FONT_BOLD_16 = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Arial", Font.PLAIN, 14);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);
    private static final DecimalFormat DF = new DecimalFormat("0.##");

    public SampleRoundRobinPanel(Runnable onBack) {
        setLayout(new BorderLayout());

        add(createTopPanel(onBack), BorderLayout.NORTH);
        
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainContentPanel.add(createTableStatsAndGanttPanel(), BorderLayout.CENTER);

        JScrollPane mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16); 

        add(mainScrollPane, BorderLayout.CENTER);
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

    private JPanel createTableStatsAndGanttPanel() {
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

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(resultTable.getTableHeader(), BorderLayout.NORTH);
        tableWrapper.add(resultTable, BorderLayout.CENTER);
        
        double totalTime = 32.0; 
        int n = 4; 
        
        double cpuUtil = (totalTime / totalTime) * 100.0;
        double throughput = (double) n / totalTime; 
        
        DecimalFormat dfPercent = new DecimalFormat("0.##");
        DecimalFormat dfThroughput = new DecimalFormat("0.####");

        JLabel cpuUtilLabel = new JLabel("CPU Utilization: " + dfPercent.format(cpuUtil) + "%");
        cpuUtilLabel.setFont(FONT_BOLD_16);
        cpuUtilLabel.setForeground(new Color(0, 100, 0)); 

        JLabel throughputLabel = new JLabel("Throughput: " + dfThroughput.format(throughput) + " processes per ms");
        throughputLabel.setFont(FONT_BOLD_16);
        throughputLabel.setForeground(new Color(0, 100, 0));
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        statsPanel.setOpaque(false);
        statsPanel.add(cpuUtilLabel);
        statsPanel.add(throughputLabel);

        JPanel statsWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        statsWrapper.add(statsPanel);
        
        List<GanttSegment> segments = new ArrayList<>();
        segments.add(new GanttSegment("P1", 0, 5));
        segments.add(new GanttSegment("P2", 5, 8));
        segments.add(new GanttSegment("P3", 8, 13));
        segments.add(new GanttSegment("P4", 13, 15));
        segments.add(new GanttSegment("P1", 15, 20));
        segments.add(new GanttSegment("P3", 20, 21));
        segments.add(new GanttSegment("P1", 21, 26));
        segments.add(new GanttSegment("P1", 26, 31));
        segments.add(new GanttSegment("P1", 31, 32));

        GanttChartPanel ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setSegments(segments);
        
        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);
        ganttScrollPane.setPreferredSize(new Dimension(800, 120)); 
        ganttScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        ganttScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        double sumCt = 76;
        double sumTat = 76;
        double sumWt = 44;

        JLabel totalCtLabel = createStatLabel("Total Completion Time: " + DF.format(sumCt) + "ms");
        JLabel totalTatLabel = createStatLabel("Total Turn Around Time: " + DF.format(sumTat) + "ms");
        JLabel totalWtLabel = createStatLabel("Total Waiting Time: " + DF.format(sumWt) + "ms");

        JLabel avgCtLabel = createStatLabel("Average Completion Time: " + DF.format(sumCt / n) + "ms");
        JLabel avgTatLabel = createStatLabel("Average Turn Around Time: " + DF.format(sumTat / n) + "ms");
        JLabel avgWtLabel = createStatLabel("Average Waiting Time: " + DF.format(sumWt / n) + "ms");

        JPanel sumsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        sumsPanel.setOpaque(false);
        sumsPanel.add(totalCtLabel);
        sumsPanel.add(totalTatLabel);
        sumsPanel.add(totalWtLabel);

        JPanel avgsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        avgsPanel.setOpaque(false);
        avgsPanel.add(avgCtLabel);
        avgsPanel.add(avgTatLabel);
        avgsPanel.add(avgWtLabel);

        JPanel bottomStatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        bottomStatsPanel.add(sumsPanel);
        bottomStatsPanel.add(Box.createHorizontalStrut(120)); 
        bottomStatsPanel.add(avgsPanel);

        JPanel bottomGroup = new JPanel(new BorderLayout());
        bottomGroup.add(statsWrapper, BorderLayout.NORTH);
        bottomGroup.add(ganttScrollPane, BorderLayout.CENTER); 
        bottomGroup.add(bottomStatsPanel, BorderLayout.SOUTH); 
        
        JPanel alignmentWrapper = new JPanel(new BorderLayout());
        alignmentWrapper.add(bottomGroup, BorderLayout.NORTH); 
        
        JPanel tableAndStatsPanel = new JPanel(new BorderLayout());
        tableAndStatsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20)); 
        tableAndStatsPanel.add(tableWrapper, BorderLayout.NORTH);
        tableAndStatsPanel.add(alignmentWrapper, BorderLayout.CENTER); 
        
        return tableAndStatsPanel;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD_16);
        label.setForeground(Color.BLACK);
        return label;
    }
}