import javax.swing.*;
import java.awt.*;
import javax.swing.text.AbstractDocument;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RoundRobinPanel extends JPanel {

    private JTextField processesField;
    private JTextField quantumField;
    private JLabel processesErrorLabel; 
    private JLabel quantumErrorLabel;
    private JTable resultTable;
    private DefaultTableModel tableModel;

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
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
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
        int targetRows = text.matches("1[0-9]|20") ? Integer.parseInt(text) : 0;

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
                return column == 0;
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

        DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
                return this;
            }
        };

        idRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        resultTable.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        JTextField idField = new JTextField();
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 7, 0, 0)));
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new ProcessIdFilter());

        DefaultCellEditor idEditor = new DefaultCellEditor(idField);
        idEditor.setClickCountToStart(1);
        resultTable.getColumnModel().getColumn(0).setCellEditor(idEditor);

        resultTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tablePanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        return tablePanel;
    }
}