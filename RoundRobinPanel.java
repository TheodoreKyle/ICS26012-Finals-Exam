import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DocumentFilter;

public class RoundRobinPanel extends JPanel {

    private JTextField processesField;
    private JTextField quantumField;
    private JLabel processesErrorLabel; 
    private JLabel quantumErrorLabel;
    private JLabel tableErrorLabel; 
    private JLabel idleTimeLabel;
    private JTable resultTable;
    private JButton calculateButton; 
    private DefaultTableModel tableModel;
    private GanttChartPanel ganttChartPanel; 

    public RoundRobinPanel(Runnable onBack) {
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(createTopPanel(onBack), BorderLayout.NORTH);
        northPanel.add(createInputPanel(), BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER); 
    }

    private JPanel createTopPanel(Runnable onBack) {
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
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

    private JPanel createInputPanel() {
        processesField = createTextField();
        quantumField = createTextField();

        processesErrorLabel = createErrorLabel();
        quantumErrorLabel = createErrorLabel();

        addValidation(processesField, processesErrorLabel, "1[0-9]|20", "Please input only from 10 to 20");
        addValidation(quantumField, quantumErrorLabel, "[1-9][0-9]?", "Please input only from 1 to 99");
        onTextChange(processesField, this::updateTableRows);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("How many processes?"), gbc);

        gbc.gridx = 1;
        formPanel.add(processesField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        formPanel.add(processesErrorLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Enter Time Quantum:"), gbc);

        gbc.gridx = 1;
        formPanel.add(quantumField, gbc);

        gbc.gridx = 2;
        formPanel.add(createLabel("ms"), gbc);

        gbc.gridx = 3;
        formPanel.add(quantumErrorLabel, gbc);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        topPanel.add(formPanel);
        return topPanel;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.RED);
        return label;
    }

    private void onTextChange(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { action.run(); }

            @Override
            public void removeUpdate(DocumentEvent e) { action.run(); }

            @Override
            public void changedUpdate(DocumentEvent e) { action.run(); }
        });
    }

    private void addValidation(JTextField field, JLabel errorLabel, String validPattern, String errorMessage) {
        onTextChange(field, () -> {
            String text = field.getText();

            if (text.isEmpty() || text.matches(validPattern)) {
                errorLabel.setText("");
            } else {
                errorLabel.setText(errorMessage);
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        return label;
    }

    private void updateTableRows() {
        String text = processesField.getText();

        if (!text.matches("1[0-9]|20")) {
            return;
        }

        int targetRows = Integer.parseInt(text);

        while (tableModel.getRowCount() < targetRows) {
            Object[] newRow = new Object[tableModel.getColumnCount()];
            newRow[0] = "P";
            tableModel.addRow(newRow);
        }

        while (tableModel.getRowCount() > targetRows) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(120, 35));
        textField.setFont(new Font("Arial", Font.PLAIN, 20));

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerOnlyFilter()); 

        return textField;
    }

    private JPanel createTablePanel() {
        String[] headers = {
            "Process ID",
            "Arrival Time",
            "Burst Time",
            "Completion Time",
            "Turn Around Time",
            "Waiting Time",
            "Response Time",
            "CPU Utilization",
            "Throughput"
        };

        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column <= 2;
            }
        };

        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultTable.setRowHeight(30);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(Color.GRAY);

        resultTable.setRowSelectionAllowed(false);
        resultTable.setColumnSelectionAllowed(false);
        resultTable.setFocusable(false);

        JTableHeader tableHeader = resultTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeader.setPreferredSize(new Dimension(0, 40));
        tableHeader.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultTable.setDefaultRenderer(Object.class, cellRenderer);

        resultTable.getColumnModel().getColumn(0).setCellEditor(createCellEditor(new ProcessIdFilter(
        3,
                () -> showTableError("Please input only from 0 to 999"),
                () -> showTableError(""))));

        resultTable.getColumnModel().getColumn(1).setCellEditor(createCellEditor(new LimitedIntegerFilter(
                4,
                () -> showTableError("Please input only from 0 to 9999"),
                () -> showTableError(""))));

        resultTable.getColumnModel().getColumn(2).setCellEditor(createCellEditor(new LimitedIntegerFilter(
                4,
                () -> showTableError("Please input only from 0 to 9999"),
                () -> showTableError(""))));

        resultTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        tableErrorLabel = createErrorLabel();
        tableErrorLabel.setPreferredSize(new Dimension(0, 28));

        calculateButton = createCalculateButton();
        calculateButton.addActionListener(e -> calculate());

        idleTimeLabel = new JLabel("");
        idleTimeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        idleTimeLabel.setForeground(new Color(0, 100, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        buttonPanel.add(calculateButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(idleTimeLabel); 

        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(800, 100));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(ganttChartPanel, BorderLayout.CENTER);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tablePanel.add(tableErrorLabel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        
        // FIX: Add southPanel instead of buttonPanel directly
        tablePanel.add(southPanel, BorderLayout.SOUTH); 
        return tablePanel;
    }

    private void calculate() {
        if (resultTable.isEditing()) {
            resultTable.getCellEditor().stopCellEditing();
        }

        if (hasIncompleteInputs()) {
            showTableError("Please input all fields");
            ganttChartPanel.clear();
            idleTimeLabel.setText("");
            return;
        }

        if (hasDuplicateProcessIds()) {
            showTableError("No Process ID should be the same");
            ganttChartPanel.clear();
            idleTimeLabel.setText("");
            return;
        }

        showTableError("");
        executeRoundRobin();
    }

    private void executeRoundRobin() {
        int n = tableModel.getRowCount();
        int quantum = Integer.parseInt(quantumField.getText());
        List<ProcessData> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            
            // FIX: Treat blank arrival time as 0
            Object atObj = tableModel.getValueAt(i, 1);
            int at = isBlank(atObj) ? 0 : Integer.parseInt(atObj.toString());
            
            int bt = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            processes.add(new ProcessData(id, at, bt, i));
        }

        processes.sort(Comparator.comparingInt(p -> p.at));

        List<GanttSegment> segments = new ArrayList<>();
        Queue<ProcessData> queue = new LinkedList<>();
        int currentTime = 0;
        int completed = 0;
        int index = 0;

        if (processes.get(0).at > currentTime) {
            segments.add (new GanttSegment("Idle", currentTime, processes.get(0).at));
            currentTime = processes.get(0).at;
        }

        while (completed < n) {
            if (queue.isEmpty()) {
                if (index < n) {
                    int nextArrival = processes.get(index).at;
                    segments.add(new GanttSegment("Idle", currentTime, nextArrival));
                    currentTime = nextArrival; 
                    while (index < n && processes.get(index).at <= currentTime) {
                        queue.add(processes.get(index++));                         
                    }
                }
            } else {
                ProcessData current = queue.poll();
                
                if (current.firstTime) {
                    current.rt = currentTime - current.at;
                    current.firstTime = false;
                }

                int executeTime = Math.min(quantum, current.remBt);
                segments.add(new GanttSegment(current.id, currentTime, currentTime + executeTime));
                currentTime += executeTime;
                current.remBt -= executeTime;

                // Priority Rule: Enqueue new arrivals first
                while (index < n && processes.get(index).at <= currentTime) {
                    queue.add(processes.get(index++));
                }

                // Then enqueue preempted process
                if (current.remBt > 0) {
                    queue.add(current);
                } else {
                    current.ct = currentTime;
                    current.tat = current.ct - current.at;
                    current.wt = current.tat - current.bt;
                    completed++;
                }
            }
        }

        double totalTime = currentTime;
        int totalIdleTime = segments.stream()
                .filter(s -> s.processId.equals("Idle"))
                .mapToInt(s -> s.endTime - s.startTime)
                .sum();
                
        double cpuUtil = ((totalTime - totalIdleTime) / totalTime) * 100.0;
        double throughput = (double) n / totalTime;

        for (ProcessData p : processes) {
            tableModel.setValueAt(p.ct, p.rowIndex, 3);
            tableModel.setValueAt(p.tat, p.rowIndex, 4);
            tableModel.setValueAt(p.wt, p.rowIndex, 5);
            tableModel.setValueAt(p.rt, p.rowIndex, 6);
            tableModel.setValueAt(String.format("%.2f%%", cpuUtil), p.rowIndex, 7);
            tableModel.setValueAt(String.format("%.4f", throughput), p.rowIndex, 8);
        }

        idleTimeLabel.setText("Total Idle Time: " + totalIdleTime + " ms");
        ganttChartPanel.setSegments(segments);
    }

    private boolean hasIncompleteInputs() {
        if (!processesField.getText().matches("1[0-9]|20")) {
            return true;
        }

        if (!quantumField.getText().matches("[1-9][0-9]?")) {
            return true;
        }

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object id = tableModel.getValueAt(row, 0);
            Object burst = tableModel.getValueAt(row, 2);

            // Arrival Time isn't checked here anymore so blank is permitted
            if (isBlank(id) || id.toString().equals("P") || isBlank(burst)) {
                return true;
            }
        }

        return false;
    }

    private boolean isBlank(Object value) {
        return value == null || value.toString().trim().isEmpty();
    }

    private boolean hasDuplicateProcessIds() {
        Set<String> seenIds = new HashSet<>();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String id = String.valueOf(tableModel.getValueAt(row, 0));
            if (!seenIds.add(id)) {
                return true;
            }
        }
        return false;
    }

    private JButton createCalculateButton() {
        JButton button = new JButton("Calculate");
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(220, 60));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 100, 0));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color calculateNormalColor = new Color(0, 100, 0);
        Color calculatePressedColor = new Color(0, 70, 0);

        button.getModel().addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(calculatePressedColor);
            } else {
                button.setBackground(calculateNormalColor);
            }
        });

        return button;
    }

    private DefaultCellEditor createCellEditor(DocumentFilter filter) {
        JTextField cellField = new JTextField();
        cellField.setFont(new Font("Arial", Font.PLAIN, 14));
        cellField.setHorizontalAlignment(SwingConstants.CENTER);
        cellField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        ((AbstractDocument) cellField.getDocument()).setDocumentFilter(filter);

        DefaultCellEditor cellEditor = new DefaultCellEditor(cellField);
        cellEditor.setClickCountToStart(1);

        cellEditor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                showTableError("");
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                showTableError("");
            }
        });

        return cellEditor;
    }

    private void showTableError(String message) {
        tableErrorLabel.setText(message);
    }

    // STARTING HERE IS THE GANTT CHART, TO BE DELETED IF A FAILURE

    private static class ProcessData {
        String id;
        int at, bt, remBt, ct, tat, wt, rt, rowIndex;
        boolean firstTime = true;

        ProcessData(String id, int at, int bt, int rowIndex) {
            this.id = (!id.startsWith("P")) ? "P" + id : id;
            this.at = at;
            this.bt = bt;
            this.remBt = bt;
            this.rowIndex = rowIndex;
        }
    }

    private static class GanttSegment {
        String processId;
        int startTime;
        int endTime;

        GanttSegment(String processId, int startTime, int endTime) {
            this.processId = processId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    private static class GanttChartPanel extends JPanel {
        private List<GanttSegment> segments = new ArrayList<>();

        public void setSegments(List<GanttSegment> segments) {
            this.segments = segments;
            repaint();
        }

        public void clear() {
            this.segments.clear();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (segments == null || segments.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int paddingX = 10;
            int startY = 15;
            int barHeight = 45;
            int totalWidth = getWidth() - 2 * paddingX;

            int minTime = segments.get(0).startTime;
            int maxTime = segments.get(segments.size() - 1).endTime;
            int totalDuration = maxTime - minTime;

            if (totalDuration == 0) return;

            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < segments.size(); i++) {
                GanttSegment seg = segments.get(i);
                
                int x = paddingX + (int) ((double) (seg.startTime - minTime) / totalDuration * totalWidth);
                int nextX = paddingX + (int) ((double) (seg.endTime - minTime) / totalDuration * totalWidth);
                int width = nextX - x;

                if (seg.processId.equals("Idle")) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(new Color(235, 235, 235)); 
                }
                
                g2.fillRect(x, startY, width, barHeight);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, startY, width, barHeight);

                g2.setFont(new Font("Arial", Font.PLAIN, 16));
                
                g2.setColor(seg.processId.equals("Idle") ? Color.GRAY : Color.BLACK);
                int textWidth = fm.stringWidth(seg.processId);
                int textX = x + (width - textWidth) / 2;
                int textY = startY + (barHeight - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(seg.processId, textX, textY);

                // Draw time markers
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                String startTimeStr = String.valueOf(seg.startTime);
                g2.drawString(startTimeStr, x - fm.stringWidth(startTimeStr) / 2, startY + barHeight + 20);

                if (i == segments.size() - 1) {
                    String endTimeStr = String.valueOf(seg.endTime);
                    g2.drawString(endTimeStr, nextX - fm.stringWidth(endTimeStr) / 2, startY + barHeight + 20);
                }
            }
        }
    }
}