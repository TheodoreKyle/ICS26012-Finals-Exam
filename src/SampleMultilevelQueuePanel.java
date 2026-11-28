import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SampleMultilevelQueuePanel extends JPanel {

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Font FONT_BOLD_30 = new Font("Arial", Font.BOLD, 30);
    private static final Font FONT_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font FONT_BOLD_16 = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_BOLD_20 = new Font("Arial", Font.BOLD, 20);
    private static final Font FONT_PLAIN_14 = new Font("Arial", Font.PLAIN, 14);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);

    private static final int QUEUE_1_QUANTUM = 10;
    private static final int QUEUE_2_QUANTUM = 12;

    public SampleMultilevelQueuePanel(Runnable onBack) {
        setLayout(new BorderLayout());

        add(createTopPanel(onBack), BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER);

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

        JLabel titleLabel = new JLabel("Sample Multilevel Queue");
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
            "Queue Number",
            "Arrival Time",
            "Burst Time",
            "Completion Time",
            "Turn Around Time",
            "Waiting Time",
            "Response Time"
        };

        Object[][] inputData = {
            {"P5", 2, 4, 11, null, null, null, null},
            {"P3", 3, 8, 15, null, null, null, null},
            {"P1", 3, 3, 30, null, null, null, null},
            {"P6", 1, 2, 2, null, null, null, null},
            {"P2", 2, 0, 37, null, null, null, null},
            {"P8", 1, 0, 55, null, null, null, null},
            {"P9", 3, 7, 7, null, null, null, null},
            {"P10", 2, 5, 24, null, null, null, null},
            {"P11", 2, 9, 37, null, null, null, null}
        };

        DefaultTableModel tableModel = new DefaultTableModel(inputData, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // --- COMPUTE MULTILEVEL QUEUE ---
        int n = tableModel.getRowCount();
        List<ProcessData> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            int queueNum = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            int at = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            int bt = Integer.parseInt(tableModel.getValueAt(i, 3).toString());

            ProcessData p = new ProcessData(id, at, bt, i);
            p.rt = queueNum; // Stores queue priority (1, 2, or 3)
            processes.add(p);
        }

        processes.sort((p1, p2) -> {
            if (p1.at != p2.at) {
                return Integer.compare(p1.at, p2.at);
            } else {
                int id1 = Integer.parseInt(p1.id.replace("P", ""));
                int id2 = Integer.parseInt(p2.id.replace("P", ""));
                return Integer.compare(id1, id2);
            }
        });

        Queue<ProcessData> pendingQueue = new LinkedList<>(processes);
        Queue<ProcessData> highPriority = new LinkedList<>();
        Queue<ProcessData> mediumPriority = new LinkedList<>();
        Queue<ProcessData> lowPriority = new LinkedList<>();

        List<GanttSegment> segments = new ArrayList<>();
        int currentTime = 0;

        while (!pendingQueue.isEmpty() || !highPriority.isEmpty() || !mediumPriority.isEmpty() || !lowPriority.isEmpty()) {
            updateReadyQueues(pendingQueue, highPriority, mediumPriority, lowPriority, currentTime);

            if (highPriority.isEmpty() && mediumPriority.isEmpty() && lowPriority.isEmpty()) {
                int nextArrival = pendingQueue.peek().at;
                segments.add(new GanttSegment("Idle", currentTime, nextArrival));
                currentTime = nextArrival;
                continue;
            }

            if (!highPriority.isEmpty()) {
                ProcessData current = highPriority.remove();
                if (current.firstTime) {
                    tableModel.setValueAt(currentTime - current.at, current.rowIndex, 7);
                    current.firstTime = false;
                }

                int executionTime = Math.min(QUEUE_1_QUANTUM, current.remBt);
                segments.add(new GanttSegment(current.id, currentTime, currentTime + executionTime));
                currentTime += executionTime;
                current.remBt -= executionTime;

                updateReadyQueues(pendingQueue, highPriority, mediumPriority, lowPriority, currentTime);

                if (current.remBt > 0) {
                    highPriority.add(current);
                } else {
                    current.ct = currentTime;
                    current.tat = current.ct - current.at;
                    current.wt = current.tat - current.bt;
                }
            } else if (!mediumPriority.isEmpty()) {
                ProcessData current = mediumPriority.remove();
                if (current.firstTime) {
                    tableModel.setValueAt(currentTime - current.at, current.rowIndex, 7);
                    current.firstTime = false;
                }

                int executionTime = Math.min(QUEUE_2_QUANTUM, current.remBt);
                segments.add(new GanttSegment(current.id, currentTime, currentTime + executionTime));
                currentTime += executionTime;
                current.remBt -= executionTime;

                updateReadyQueues(pendingQueue, highPriority, mediumPriority, lowPriority, currentTime);

                if (current.remBt > 0) {
                    mediumPriority.add(current);
                } else {
                    current.ct = currentTime;
                    current.tat = current.ct - current.at;
                    current.wt = current.tat - current.bt;
                }
            } else if (!lowPriority.isEmpty()) {
                ProcessData current = lowPriority.remove();
                if (current.firstTime) {
                    tableModel.setValueAt(currentTime - current.at, current.rowIndex, 7);
                    current.firstTime = false;
                }

                int executionTime = current.remBt;
                segments.add(new GanttSegment(current.id, currentTime, currentTime + executionTime));
                currentTime += executionTime;
                current.remBt = 0;

                updateReadyQueues(pendingQueue, highPriority, mediumPriority, lowPriority, currentTime);

                current.ct = currentTime;
                current.tat = current.ct - current.at;
                current.wt = current.tat - current.bt;
            }
        }

        double totalTime = currentTime;
        int totalIdleTime = segments.stream()
                .filter(s -> s.processId.equals("Idle"))
                .mapToInt(s -> s.endTime - s.startTime)
                .sum();

        double cpuUtil = ((totalTime - totalIdleTime) / totalTime) * 100.0;
        double throughput = (double) n / totalTime;

        double sumCt = 0, sumTat = 0, sumWt = 0, sumRt = 0;

        for (ProcessData p : processes) {
            tableModel.setValueAt(p.ct, p.rowIndex, 4);
            tableModel.setValueAt(p.tat, p.rowIndex, 5);
            tableModel.setValueAt(p.wt, p.rowIndex, 6);

            sumCt += p.ct;
            sumTat += p.tat;
            sumWt += p.wt;
            sumRt += Integer.parseInt(tableModel.getValueAt(p.rowIndex, 7).toString());
        }

        DecimalFormat df = new DecimalFormat("0.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        DecimalFormat dfThroughput = new DecimalFormat("0.####");
        dfThroughput.setRoundingMode(RoundingMode.HALF_UP);

        // --- TABLE SETUP ---
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

        // --- QUEUE CONFIGURATION LABELS ---
        JLabel q1Label = new JLabel("Queue 1 - Uses Round Robin with Time Quantum of " + QUEUE_1_QUANTUM + "ms");
        q1Label.setFont(FONT_BOLD_20);
        q1Label.setForeground(Color.BLACK);

        JLabel q2Label = new JLabel("Queue 2 - Uses Round Robin with Time Quantum of " + QUEUE_2_QUANTUM + "ms");
        q2Label.setFont(FONT_BOLD_20);
        q2Label.setForeground(Color.BLACK);

        JLabel q3Label = new JLabel("Queue 3 - Uses First Come First Serve");
        q3Label.setFont(FONT_BOLD_20);
        q3Label.setForeground(Color.BLACK);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        infoPanel.setOpaque(false);
        infoPanel.add(q1Label);
        infoPanel.add(q2Label);
        infoPanel.add(q3Label);

        JPanel infoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        infoWrapper.add(infoPanel);

        JPanel topGroup = new JPanel(new BorderLayout());
        topGroup.add(infoWrapper, BorderLayout.NORTH);
        topGroup.add(tableWrapper, BorderLayout.CENTER);

        // --- 1. CPU UTILIZATION & THROUGHPUT ---
        JLabel cpuUtilLabel = new JLabel("CPU Utilization: " + df.format(cpuUtil) + "%");
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

        // --- 2. GANTT CHART ---
        GanttChartPanel ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setSegments(segments);

        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);
        ganttScrollPane.setPreferredSize(new Dimension(800, 120));
        ganttScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        ganttScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // --- 3. TOTAL & AVERAGE TIMES ---
        JLabel totalCtLabel = createStatLabel("Total Completion Time: " + df.format(sumCt) + "ms");
        JLabel totalTatLabel = createStatLabel("Total Turn Around Time: " + df.format(sumTat) + "ms");
        JLabel totalWtLabel = createStatLabel("Total Waiting Time: " + df.format(sumWt) + "ms");
        JLabel totalRtLabel = createStatLabel("Total Response Time: " + df.format(sumRt) + "ms");

        JLabel avgCtLabel = createStatLabel("Average Completion Time: " + df.format(sumCt / n) + "ms");
        JLabel avgTatLabel = createStatLabel("Average Turn Around Time: " + df.format(sumTat / n) + "ms");
        JLabel avgWtLabel = createStatLabel("Average Waiting Time: " + df.format(sumWt / n) + "ms");
        JLabel avgRtLabel = createStatLabel("Average Response Time: " + df.format(sumRt / n) + "ms");

        JPanel sumsPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        sumsPanel.setOpaque(false);
        sumsPanel.add(totalCtLabel);
        sumsPanel.add(totalTatLabel);
        sumsPanel.add(totalWtLabel);
        sumsPanel.add(totalRtLabel);

        JPanel avgsPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        avgsPanel.setOpaque(false);
        avgsPanel.add(avgCtLabel);
        avgsPanel.add(avgTatLabel);
        avgsPanel.add(avgWtLabel);
        avgsPanel.add(avgRtLabel);

        JPanel bottomStatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        bottomStatsPanel.add(sumsPanel);
        bottomStatsPanel.add(Box.createHorizontalStrut(120));
        bottomStatsPanel.add(avgsPanel);

        // --- 4. ASSEMBLE PANEL ---
        JPanel bottomGroup = new JPanel(new BorderLayout());
        bottomGroup.add(statsWrapper, BorderLayout.NORTH);
        bottomGroup.add(ganttScrollPane, BorderLayout.CENTER);
        bottomGroup.add(bottomStatsPanel, BorderLayout.SOUTH);

        JPanel alignmentWrapper = new JPanel(new BorderLayout());
        alignmentWrapper.add(bottomGroup, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tablePanel.add(topGroup, BorderLayout.NORTH);
        tablePanel.add(alignmentWrapper, BorderLayout.CENTER);

        return tablePanel;
    }

    private void updateReadyQueues(Queue<ProcessData> pendingQueue, Queue<ProcessData> highPriority,
                                   Queue<ProcessData> mediumPriority, Queue<ProcessData> lowPriority, int currentTime) {
        while (!pendingQueue.isEmpty() && pendingQueue.peek().at <= currentTime) {
            ProcessData current = pendingQueue.remove();
            int priority = current.rt;

            switch (priority) {
                case 1: highPriority.add(current); break;
                case 2: mediumPriority.add(current); break;
                case 3: lowPriority.add(current); break;
            }
        }
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD_16);
        label.setForeground(Color.BLACK);
        return label;
    }
}