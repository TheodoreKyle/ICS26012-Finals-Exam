import javax.swing.*;
import java.awt.*;
import javax.swing.text.AbstractDocument;

public class RoundRobinPanel extends JPanel {

    private JTextField processesField;
    private JTextField quantumField;

    public RoundRobinPanel(Runnable onBack) {
        setLayout(new BorderLayout());

        JPanel formAndHeaderPanel = new  JPanel(new BorderLayout());
        formAndHeaderPanel.add(createInputPanel(), BorderLayout.NORTH);
        formAndHeaderPanel.add(createHeaderPanel(), BorderLayout.CENTER); 

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(createTopPanel(onBack), BorderLayout.NORTH);
        northPanel.add(formAndHeaderPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
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

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("How many processes?"), gbc);

        gbc.gridx = 1;
        formPanel.add(processesField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Time Quantum:"), gbc);

        gbc.gridx = 1;
        formPanel.add(quantumField, gbc);

        gbc.gridx = 2;
        formPanel.add(createLabel("ms"), gbc);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        topPanel.add(formPanel);
        return topPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(120, 35));
        textField.setFont(new Font("Arial", Font.PLAIN, 20));

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerOnlyFilter()); 

        return textField;
    }

    private JPanel createHeaderPanel() {
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

    JPanel headerPanel = new JPanel(new GridLayout(1, headers.length, 5, 0));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    for (String header : headers) {
        JLabel label = new JLabel(header, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(label);
    }

    return headerPanel;
}
}