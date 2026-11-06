import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
    
    private JLabel cpuUtilLabel;
    private JLabel throughputLabel;
    
    // New total and average labels
    private JLabel totalCtLabel;
    private JLabel totalTatLabel;
    private JLabel totalWtLabel;
    private JLabel avgTatLabel;
    private JLabel avgWtLabel;
    
    private JTable resultTable;
    private JButton calculateButton; 
    private DefaultTableModel tableModel;
    private GanttChartPanel ganttChartPanel; 

    public RoundRobinPanel(Runnable onBack) {
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
            "Response Time"
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
                
                // Prevent out-of-bounds error if no cell is selected
                if (row == -1 || col == -1) return;
                
                if (resultTable.isEditing()) {
                    resultTable.getCellEditor().stopCellEditing();
                }
                
                if (row < resultTable.getRowCount() - 1) {
                    resultTable.changeSelection(row + 1, col, false, false);
                    if (resultTable.isCellEditable(row + 1, col)) {
                        resultTable.editCellAt(row + 1, col);
                        // Null check prevents the NullPointerException
                        Component editor = resultTable.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
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
                
                if (resultTable.isEditing()) {
                    resultTable.getCellEditor().stopCellEditing();
                }
                
                // Only move to the right if the next column is editable
                if (col < 2) {
                    resultTable.changeSelection(row, col + 1, false, false);
                    if (resultTable.isCellEditable(row, col + 1)) {
                        resultTable.editCellAt(row, col + 1);
                        Component editor = resultTable.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
                    }
                }
            }
        });

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

        cpuUtilLabel = new JLabel("");
        cpuUtilLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cpuUtilLabel.setForeground(new Color(0, 100, 0));

        throughputLabel = new JLabel("");
        throughputLabel.setFont(new Font("Arial", Font.BOLD, 16));
        throughputLabel.setForeground(new Color(0, 100, 0));

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        statsPanel.setOpaque(false); 
        statsPanel.add(cpuUtilLabel);
        statsPanel.add(throughputLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        buttonPanel.add(calculateButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(statsPanel); 

        ganttChartPanel = new GanttChartPanel();
        
        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);
        ganttScrollPane.setPreferredSize(new Dimension(800, 120)); 
        ganttScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        ganttScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Initialize sum and average labels
        totalCtLabel = createStatLabel("");
        totalTatLabel = createStatLabel("");
        totalWtLabel = createStatLabel("");
        avgTatLabel = createStatLabel("");
        avgWtLabel = createStatLabel("");

        // Setup the totals panel
        JPanel sumsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        sumsPanel.setOpaque(false);
        sumsPanel.add(totalCtLabel);
        sumsPanel.add(totalTatLabel);
        sumsPanel.add(totalWtLabel);

        JPanel avgsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        avgsPanel.setOpaque(false);
        avgsPanel.add(new JLabel("")); 
        avgsPanel.add(avgTatLabel);
        avgsPanel.add(avgWtLabel);

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
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.BLACK);
        return label;
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
        executeRoundRobin();
    }

    private void clearOutputs() {
        ganttChartPanel.clear();
        cpuUtilLabel.setText("");
        throughputLabel.setText("");
        totalCtLabel.setText("");
        totalTatLabel.setText("");
        totalWtLabel.setText("");
        avgTatLabel.setText("");
        avgWtLabel.setText("");
    }

    private void executeRoundRobin() {
        int n = tableModel.getRowCount();
        int quantum = Integer.parseInt(quantumField.getText());
        List<ProcessData> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            
            Object atObj = tableModel.getValueAt(i, 1);
            int at = isBlank(atObj) ? 0 : Integer.parseInt(atObj.toString());
            
            int bt = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            processes.add(new ProcessData(id, at, bt, i));
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

        List<GanttSegment> segments = new ArrayList<>();
        Queue<ProcessData> queue = new LinkedList<>();
        int currentTime = 0;
        int completed = 0;
        int index = 0;

       if (processes.get(0).at > currentTime) {
            segments.add(new GanttSegment("Idle", currentTime, processes.get(0).at));
            currentTime = processes.get(0).at;
        }

        while (index < n && processes.get(index).at <= currentTime) {
            queue.add(processes.get(index++));
        }

        while (completed < n) {
            if (queue.isEmpty()) {
                if (index < n) {
                    int nextArrival = processes.get(index).at;
                    if (nextArrival > currentTime) {
                        segments.add(new GanttSegment("Idle", currentTime, nextArrival));
                        currentTime = nextArrival; 
                    }
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

                while (index < n && processes.get(index).at <= currentTime) {
                    queue.add(processes.get(index++));
                }

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

        double sumCt = 0, sumTat = 0, sumWt = 0;

        for (ProcessData p : processes) {
            tableModel.setValueAt(p.ct, p.rowIndex, 3);
            tableModel.setValueAt(p.tat, p.rowIndex, 4);
            tableModel.setValueAt(p.wt, p.rowIndex, 5);
            tableModel.setValueAt(p.rt, p.rowIndex, 6);
            
            sumCt += p.ct;
            sumTat += p.tat;
            sumWt += p.wt;
        }

        cpuUtilLabel.setText(String.format("CPU Utilization: %.2f%%", cpuUtil));
        throughputLabel.setText(String.format("Throughput: %.4f", throughput));
        
        // Define standard formatter for up to 2 decimal places excluding trailing zeros
        DecimalFormat df = new DecimalFormat("0.##");
        
        totalCtLabel.setText("Total Completion Time: " + df.format(sumCt) + "ms");
        totalTatLabel.setText("Total Turn Around Time: " + df.format(sumTat) + "ms");
        totalWtLabel.setText("Total Waiting Time: " + df.format(sumWt) + "ms");
        
        avgTatLabel.setText("Average Turn Around Time: " + df.format(sumTat / n) + "ms");
        avgWtLabel.setText("Average Waiting Time: " + df.format(sumWt / n) + "ms");
        
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
}