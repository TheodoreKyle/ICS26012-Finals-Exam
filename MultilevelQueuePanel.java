import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.math.RoundingMode;
import javax.swing.text.AbstractDocument;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MultilevelQueuePanel extends JPanel {

    private JTextField processesField;
    private JLabel processesErrorLabel; 

    private JTextField queue1QuantumField;
    private JTextField queue2QuantumField; 

    private JLabel queue1ErrorLabel;
    private JLabel queue2ErrorLabel;

    private JLabel tableErrorLabel;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    private JButton generateRandomButton;
    private JButton clearAllButton;
    private JButton calculateButton;
    private JLabel cpuUtilLabel;
    private JLabel throughputLabel;
    private JLabel totalCtLabel;
    private JLabel totalTatLabel;
    private JLabel totalWtLabel;
    private JLabel totalRtLabel;
    private JLabel avgCtLabel;
    private JLabel avgTatLabel;
    private JLabel avgWtLabel;
    private JLabel avgRtLabel;
    private GanttChartPanel ganttChartPanel;

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Font FONT_BOLD_20 = new Font("Arial", Font.BOLD, 20);
    private static final Font FONT_BOLD_16 = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font FONT_PLAIN_20 = new Font("Arial", Font.PLAIN, 20);
    private static final Font FONT_PLAIN_14 = new Font("Arial", Font.PLAIN, 14);

    private static final Dimension DIM_BTN_LARGE = new Dimension(220, 60);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);
    private static final Dimension DIM_INPUT = new Dimension(120, 35);

    public MultilevelQueuePanel(Runnable onBack) {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(createTopPanel(onBack), BorderLayout.NORTH);
        northPanel.add(createInputPanel(), BorderLayout.CENTER);

        contentPanel.add(northPanel, BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
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

    private JPanel createInputPanel() {
        processesField = createTextField();
        queue1QuantumField = createTextField();
        queue2QuantumField = createTextField();
        
        processesErrorLabel = createErrorLabel();
        queue1ErrorLabel = createErrorLabel();
        queue2ErrorLabel = createErrorLabel(); 
        
        
        addValidation(processesField, processesErrorLabel, "1[0-9]|20", "Please input only from 10 to 20");
        addValidation(queue1QuantumField, queue1ErrorLabel, "^([1-9][0-9]?|100)$", "Please input only from 1 to 100");
        addValidation(queue2QuantumField, queue2ErrorLabel, "^([1-9][0-9]?|100)$", "Please input only from 1 to 100");
        
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
        formPanel.add(createLabel("Enter Round Robin Time Quantum for Queue 1:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(queue1QuantumField, gbc);
        
        gbc.gridx = 2;
        formPanel.add(createLabel("ms"), gbc);
        
        gbc.gridx = 3;
        formPanel.add(queue1ErrorLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Enter Round Robin Time Quantum for Queue 2:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(queue2QuantumField, gbc);
        
        gbc.gridx = 2;
        formPanel.add(createLabel("ms"), gbc); 
        
        gbc.gridx = 3;
        formPanel.add(queue2ErrorLabel, gbc);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        topPanel.add(formPanel);
        return topPanel;
    }

    private void updateTableRows() {
        String text = processesField.getText();
        if (!text.matches("1[0-9]|20")) return;
        int targetRows = Integer.parseInt(text);

        while (tableModel.getRowCount() < targetRows) {
            Object[] newRow = new Object[tableModel.getColumnCount()];
            newRow[0] = "P";
            tableModel.addRow(newRow);
        }
        while (tableModel.getRowCount() > targetRows) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }

        resultTable.revalidate();
        resultTable.repaint();
        revalidate();
        repaint();
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

        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column <= 3; 
            }
        };

        resultTable = new JTable(tableModel);
        resultTable.setFont(FONT_PLAIN_14);
        resultTable.setRowHeight(30);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(Color.GRAY);

        resultTable.setRowSelectionAllowed(true);
        resultTable.setColumnSelectionAllowed(true);
        resultTable.setCellSelectionEnabled(true); 
        resultTable.setSurrendersFocusOnKeystroke(true);
        resultTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        InputMap im = resultTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = resultTable.getActionMap();

        im.put(KeyStroke.getKeyStroke("ENTER"), "Action.enter");
        am.put("Action.enter", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = resultTable.getSelectedRow();
                int col = resultTable.getSelectedColumn();
                if (row == -1 || col == -1) return;
                
                if (resultTable.isEditing()) resultTable.getCellEditor().stopCellEditing();
                
                if (row < resultTable.getRowCount() - 1) {
                    resultTable.changeSelection(row + 1, col, false, false);
                    if (resultTable.isCellEditable(row + 1, col)) {
                        resultTable.editCellAt(row + 1, col);
                        Component editor = resultTable.getEditorComponent();
                        if (editor != null) editor.requestFocusInWindow();
                    }
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("TAB"), "Action.tab");
        am.put("Action.tab", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = resultTable.getSelectedRow();
                int col = resultTable.getSelectedColumn();
                if (row == -1 || col == -1) return;
                
                if (resultTable.isEditing()) resultTable.getCellEditor().stopCellEditing();
                
                if (col < 3) {
                    resultTable.changeSelection(row, col + 1, false, false);
                    if (resultTable.isCellEditable(row, col + 1)) {
                        resultTable.editCellAt(row, col + 1);
                        Component editor = resultTable.getEditorComponent();
                        if (editor != null) editor.requestFocusInWindow();
                    }
                }
            }
        });

        JTableHeader tableHeader = resultTable.getTableHeader();
        tableHeader.setFont(FONT_BOLD_14);
        tableHeader.setPreferredSize(new Dimension(0, 40));
        tableHeader.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultTable.setDefaultRenderer(Object.class, cellRenderer);

        resultTable.getColumnModel().getColumn(0).setCellEditor(createCellEditor(new ProcessIdFilter(
            3, () -> showTableError("Please input only from 0 to 999"), () -> showTableError(""))));

        resultTable.getColumnModel().getColumn(1).setCellEditor(createCellEditor(new LimitedIntegerFilter(
            1, () -> showTableError("Queue must be 1, 2, or 3"), () -> showTableError("")) {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    String incoming = (text == null) ? "" : text;
                    if (!incoming.isEmpty() && !incoming.matches("[123]")) {
                        showTableError("Queue must be 1, 2, or 3");
                        return;
                    }
                    super.replace(fb, offset, length, incoming, attrs);
                }
            }));

        resultTable.getColumnModel().getColumn(2).setCellEditor(createCellEditor(new LimitedIntegerFilter(
            4, () -> showTableError("Please input only from 0 to 9999"), () -> showTableError(""))));

        resultTable.getColumnModel().getColumn(3).setCellEditor(createCellEditor(new LimitedIntegerFilter(
            4, () -> showTableError("Please input only from 0 to 9999"), () -> showTableError(""))));

        tableErrorLabel = createErrorLabel();
        tableErrorLabel.setPreferredSize(new Dimension(0, 28));

        generateRandomButton = createGenerateRandomButton();
        generateRandomButton.addActionListener(e -> generateRandomInputs());

        clearAllButton = createClearAllButton();
        clearAllButton.addActionListener(e -> clearAll());

        calculateButton = createCalculateButton();
        calculateButton.addActionListener(e -> calculate());

        cpuUtilLabel = new JLabel("");
        cpuUtilLabel.setFont(FONT_BOLD_16);
        cpuUtilLabel.setForeground(new Color(0, 100, 0));

        throughputLabel = new JLabel("");
        throughputLabel.setFont(FONT_BOLD_16);
        throughputLabel.setForeground(new Color(0, 100, 0));

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        statsPanel.setOpaque(false); 
        statsPanel.add(cpuUtilLabel);
        statsPanel.add(throughputLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        buttonPanel.add(generateRandomButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(clearAllButton);
        buttonPanel.add(Box.createHorizontalStrut(15)); 
        buttonPanel.add(calculateButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(statsPanel); 

        ganttChartPanel = new GanttChartPanel();
        
        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);
        ganttScrollPane.setPreferredSize(new Dimension(800, 120)); 
        ganttScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        ganttScrollPane.setBorder(BorderFactory.createEmptyBorder());

        totalCtLabel = createStatLabel("");
        totalTatLabel = createStatLabel("");
        totalWtLabel = createStatLabel("");
        totalRtLabel = createStatLabel("");
        avgCtLabel = createStatLabel("");
        avgTatLabel = createStatLabel("");
        avgWtLabel = createStatLabel("");
        avgRtLabel = createStatLabel("");

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

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(ganttScrollPane, BorderLayout.CENTER);
        southPanel.add(bottomStatsPanel, BorderLayout.SOUTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tablePanel.add(tableErrorLabel, BorderLayout.NORTH);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(resultTable.getTableHeader(), BorderLayout.NORTH);
        tableWrapper.add(resultTable, BorderLayout.CENTER);
        
        tablePanel.add(tableWrapper, BorderLayout.CENTER);
        tablePanel.add(southPanel, BorderLayout.SOUTH); 
        
        return tablePanel;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD_16);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createGenerateRandomButton() {
        JButton button = new JButton("<html><center>Generate Random<br>Inputs</center></html>");
        button.setFocusPainted(false);
        button.setPreferredSize(DIM_BTN_LARGE);
        button.setFont(FONT_BOLD_20);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 128));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color randomNormalColor = new Color(0, 0, 128);
        Color randomPressedColor = new Color(0, 0, 90);

        button.getModel().addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(randomPressedColor);
            } else {
                button.setBackground(randomNormalColor);
            }
        });

        return button;
    }

    private JButton createClearAllButton() {
        JButton button = new JButton("Clear All");
        button.setFocusPainted(false);
        button.setPreferredSize(DIM_BTN_LARGE);
        button.setFont(FONT_BOLD_24);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(180, 0, 0)); 
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color clearNormalColor = new Color(180, 0, 0);
        Color clearPressedColor = new Color(130, 0, 0);

        button.getModel().addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(clearPressedColor);
            } else {
                button.setBackground(clearNormalColor);
            }
        });

        return button;
    }

    private JButton createCalculateButton() {
        JButton button = new JButton("Calculate");
        button.setFocusPainted(false);
        button.setPreferredSize(DIM_BTN_LARGE);
        button.setFont(FONT_BOLD_24);
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

    private void generateRandomInputs() {
        if (resultTable.isEditing()) {
            resultTable.getCellEditor().stopCellEditing();
        }

        Random rand = new Random();

        String numStr = processesField.getText();
        if (numStr.isEmpty() || !numStr.matches("1[0-9]|20")) {
            int randomProcs = 10 + rand.nextInt(11); 
            processesField.setText(String.valueOf(randomProcs));
        }

        String q1Str = queue1QuantumField.getText();
        if (q1Str.isEmpty() || !q1Str.matches("^([1-9][0-9]?|100)$")) {
            int randomQ1 = 1 + rand.nextInt(20); 
            queue1QuantumField.setText(String.valueOf(randomQ1));
        }

        String q2Str = queue2QuantumField.getText();
        if (q2Str.isEmpty() || !q2Str.matches("^([1-9][0-9]?|100)$")) {
            int randomQ2 = 1 + rand.nextInt(20); 
            queue2QuantumField.setText(String.valueOf(randomQ2));
        }

        int rows = tableModel.getRowCount();
        Set<Integer> usedIDs = new HashSet<>();

        for (int i = 0; i < rows; i++) {
            int processId;
            do {
                processId = rand.nextInt(41); 
            } while (usedIDs.contains(processId));
            usedIDs.add(processId);

            int queueNum = 1 + rand.nextInt(3);
            int arrivalTime = rand.nextInt(21); 
            int burstTime = 1 + rand.nextInt(20); 

            tableModel.setValueAt("P" + processId, i, 0);
            tableModel.setValueAt(queueNum, i, 1);
            tableModel.setValueAt(arrivalTime, i, 2);
            tableModel.setValueAt(burstTime, i, 3);

            tableModel.setValueAt(null, i, 4);
            tableModel.setValueAt(null, i, 5);
            tableModel.setValueAt(null, i, 6);
            tableModel.setValueAt(null, i, 7);
        }

        clearOutputs();
        tableErrorLabel.setText("");
    }

    private void clearOutputs() {
        ganttChartPanel.clear();
        cpuUtilLabel.setText("");
        throughputLabel.setText("");
        totalCtLabel.setText("");
        totalTatLabel.setText("");
        totalWtLabel.setText("");
        totalRtLabel.setText("");
        avgCtLabel.setText("");
        avgTatLabel.setText("");
        avgWtLabel.setText("");
        avgRtLabel.setText("");
    }

    private void clearAll() {
        if (resultTable.isEditing()) {
            resultTable.getCellEditor().stopCellEditing();
        }

        processesField.setText("");
        queue1QuantumField.setText("");
        queue2QuantumField.setText("");

        processesErrorLabel.setText("");
        queue1ErrorLabel.setText("");
        queue2ErrorLabel.setText("");
        tableErrorLabel.setText("");

        tableModel.setRowCount(0);
        
        resultTable.revalidate();
        resultTable.repaint();
        revalidate();
        repaint();
        
        clearOutputs();
    }

    private void calculate() {
        if (resultTable.isEditing()) {
            resultTable.getCellEditor().stopCellEditing();
        }

        if (hasIncompleteInputs()) {
            showTableError("Please input all fields");
            clearOutputs();
            return;
        }

        if (hasDuplicateProcessIds()) {
            showTableError("No Process ID should be the same");
            clearOutputs();
            return;
        }

        showTableError("");
        executeMultilevelQueue();
    }

    private boolean hasIncompleteInputs() {
        if (!processesField.getText().matches("1[0-9]|20")) return true;
        if (!queue1QuantumField.getText().matches("^([1-9][0-9]?|100)$")) return true;
        if (!queue2QuantumField.getText().matches("^([1-9][0-9]?|100)$")) return true;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object id = tableModel.getValueAt(row, 0);
            Object queue = tableModel.getValueAt(row, 1);
            Object arrival = tableModel.getValueAt(row, 2);
            Object burst = tableModel.getValueAt(row, 3);

            if (isBlank(id) || id.toString().equals("P") || isBlank(queue) || isBlank(arrival) || isBlank(burst)) {
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
            if (!seenIds.add(id)) return true;
        }
        return false;
    }

    private void executeMultilevelQueue() {
        int n = tableModel.getRowCount();
        int highQuantum = Integer.parseInt(queue1QuantumField.getText());
        int mediumQuantum = Integer.parseInt(queue2QuantumField.getText());
        List<ProcessData> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            
            Object atObj = tableModel.getValueAt(i, 2); 
            int at = isBlank(atObj) ? 0 : Integer.parseInt(atObj.toString());
            
            int bt = Integer.parseInt(tableModel.getValueAt(i, 3).toString()); 
            
            ProcessData p = new ProcessData(id, at, bt, i);
            p.rt = Integer.parseInt(tableModel.getValueAt(i, 1).toString()); 
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

        while(!pendingQueue.isEmpty() || !highPriority.isEmpty() || !mediumPriority.isEmpty() || !lowPriority.isEmpty()) {
            
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
                
                int executionTime = Math.min(highQuantum, current.remBt);
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
            } 
            else if (!mediumPriority.isEmpty()) {
                ProcessData current = mediumPriority.remove();
                if (current.firstTime) {
                    tableModel.setValueAt(currentTime - current.at, current.rowIndex, 7);
                    current.firstTime = false;
                }
                
                int executionTime = Math.min(mediumQuantum, current.remBt);
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
            } 
            else if (!lowPriority.isEmpty()){
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

        cpuUtilLabel.setText("CPU Utilization: " + df.format(cpuUtil) + "%");
        throughputLabel.setText("Throughput: " + dfThroughput.format(throughput) + " processes per ms");
        
        totalCtLabel.setText("Total Completion Time: " + df.format(sumCt) + "ms");
        totalTatLabel.setText("Total Turn Around Time: " + df.format(sumTat) + "ms");
        totalWtLabel.setText("Total Waiting Time: " + df.format(sumWt) + "ms");
        totalRtLabel.setText("Total Response Time: " + df.format(sumRt) + "ms");
        
        avgCtLabel.setText("Average Completion Time: " + df.format(sumCt / n) + "ms");
        avgTatLabel.setText("Average Turn Around Time: " + df.format(sumTat / n) + "ms");
        avgWtLabel.setText("Average Waiting Time: " + df.format(sumWt / n) + "ms");
        avgRtLabel.setText("Average Response Time: " + df.format(sumRt / n) + "ms");
        
        ganttChartPanel.setSegments(segments);
    }
    
    private void updateReadyQueues(Queue<ProcessData> pendingQueue, Queue<ProcessData> highPriority, 
            Queue<ProcessData> mediumPriority, Queue<ProcessData> lowPriority, int currentTime) {
        
        while(!pendingQueue.isEmpty() && pendingQueue.peek().at <= currentTime) {
            ProcessData current = pendingQueue.remove();
            int priority = current.rt; 
            
            switch (priority) {
                case 1: highPriority.add(current); break;
                case 2: mediumPriority.add(current); break;
                case 3: lowPriority.add(current); break;
            }
        }
    }

    private DefaultCellEditor createCellEditor(javax.swing.text.DocumentFilter filter) {
        JTextField cellField = new JTextField();
        cellField.setFont(FONT_PLAIN_14);
        cellField.setHorizontalAlignment(SwingConstants.CENTER);
        cellField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((AbstractDocument) cellField.getDocument()).setDocumentFilter(filter);

        DefaultCellEditor cellEditor = new DefaultCellEditor(cellField);
        cellEditor.setClickCountToStart(1);

        cellEditor.addCellEditorListener(new javax.swing.event.CellEditorListener() {
            @Override
            public void editingStopped(javax.swing.event.ChangeEvent e) { showTableError(""); }
            @Override
            public void editingCanceled(javax.swing.event.ChangeEvent e) { showTableError(""); }
        });

        InputMap fieldIm = cellField.getInputMap(JComponent.WHEN_FOCUSED);
        fieldIm.put(KeyStroke.getKeyStroke("ENTER"), "none");
        fieldIm.put(KeyStroke.getKeyStroke("TAB"), "none");

        cellField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(cellField::selectAll);
            }
        });
        return cellEditor;
    }

    private void showTableError(String message) {
        tableErrorLabel.setText(message);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(DIM_INPUT);
        textField.setFont(FONT_PLAIN_20);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerOnlyFilter()); 
        return textField;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD_20);
        return label;
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setFont(FONT_BOLD_16);
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
}
