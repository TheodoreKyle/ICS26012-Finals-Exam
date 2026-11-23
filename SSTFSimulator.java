import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Shortest Seek Time First (SSTF) Disk Scheduling Simulator
 * Operating Systems Project - Executable JAR Application
 */
public class SSTFSimulator extends JFrame {

    private JTextField requestsField;
    private JTextField headField;
    private JTextField maxCylinderField;

    private DefaultTableModel tableModel;
    private JLabel sequenceLabel;
    private JLabel totalSeekLabel;
    private JLabel avgSeekLabel;
    private SeekChartPanel chartPanel;

    // Data model for each step in SSTF execution
    private static class SeekStep {
        final int stepNumber;
        final int fromCylinder;
        final int toCylinder;
        final int distance;
        final int cumulativeDistance;

        SeekStep(int stepNumber, int fromCylinder, int toCylinder, int distance, int cumulativeDistance) {
            this.stepNumber = stepNumber;
            this.fromCylinder = fromCylinder;
            this.toCylinder = toCylinder;
            this.distance = distance;
            this.cumulativeDistance = cumulativeDistance;
        }
    }

    public SSTFSimulator() {
        setTitle("SSTF Disk Scheduling Simulator - Operating Systems Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 680);
        setMinimumSize(new Dimension(880, 580));
        setLocationRelativeTo(null);

        initUI();
        loadDefaultExample();
        runSSTF();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // --- TOP: Title & Input Controls ---
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel headerLabel = new JLabel("Shortest Seek Time First (SSTF) Disk Scheduling Simulator");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setForeground(new Color(30, 41, 59));
        topContainer.add(headerLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Request Queue Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(new JLabel("Request Queue (comma/space separated):"), gbc);

        requestsField = new JTextField(28);
        requestsField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        inputPanel.add(requestsField, gbc);

        // Initial Head Position
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(new JLabel("Initial Head:"), gbc);

        headField = new JTextField(6);
        headField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.15;
        inputPanel.add(headField, gbc);

        // Total Cylinders
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(new JLabel("Total Cylinders:"), gbc);

        maxCylinderField = new JTextField(6);
        maxCylinderField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 0.15;
        inputPanel.add(maxCylinderField, gbc);

        // Action Buttons Row
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonBar.setOpaque(false);

        JButton runButton = createStyledButton("Calculate SSTF", new Color(37, 99, 235), Color.WHITE);
        JButton exampleButton = createStyledButton("Load Textbook Example", new Color(226, 232, 240), new Color(30, 41, 59));
        JButton randomButton = createStyledButton("Random Queue", new Color(226, 232, 240), new Color(30, 41, 59));
        JButton clearButton = createStyledButton("Clear", new Color(254, 226, 226), new Color(153, 27, 27));

        runButton.addActionListener(e -> runSSTF());
        exampleButton.addActionListener(e -> {
            loadDefaultExample();
            runSSTF();
        });
        randomButton.addActionListener(e -> {
            generateRandomInputs();
            runSSTF();
        });
        clearButton.addActionListener(e -> clearAll());

        buttonBar.add(runButton);
        buttonBar.add(exampleButton);
        buttonBar.add(randomButton);
        buttonBar.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6; gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 2, 2, 2);
        inputPanel.add(buttonBar, gbc);

        topContainer.add(inputPanel, BorderLayout.CENTER);
        root.add(topContainer, BorderLayout.NORTH);

        // --- CENTER: Split Pane (Step Table on Left, Visual Seek Chart on Right) ---
        String[] columns = {"Step", "From", "To (Closest)", "Seek Distance", "Cumulative"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable stepTable = new JTable(tableModel);
        stepTable.setRowHeight(26);
        stepTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        stepTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        stepTable.getTableHeader().setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < stepTable.getColumnCount(); i++) {
            stepTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane tableScroll = new JScrollPane(stepTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Step-by-Step Seek Execution",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)
        ));
        tableScroll.setBackground(Color.WHITE);

        chartPanel = new SeekChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Disk Head Movement Graph",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, chartPanel);
        splitPane.setResizeWeight(0.45);
        splitPane.setDividerLocation(430);
        splitPane.setBorder(null);
        root.add(splitPane, BorderLayout.CENTER);

        // --- BOTTOM: Summary Metrics ---
        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 14, 10, 14)
        ));

        sequenceLabel = new JLabel("Seek Sequence: -");
        sequenceLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        sequenceLabel.setForeground(new Color(30, 41, 59));

        totalSeekLabel = new JLabel("Total Head Movement (Seek Time): -");
        totalSeekLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalSeekLabel.setForeground(new Color(29, 78, 216));

        avgSeekLabel = new JLabel("Average Seek Distance: -");
        avgSeekLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        avgSeekLabel.setForeground(new Color(51, 65, 85));

        summaryPanel.add(sequenceLabel);
        summaryPanel.add(totalSeekLabel);
        summaryPanel.add(avgSeekLabel);

        root.add(summaryPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(
                            Math.max(0, bg.getRed() - 15),
                            Math.max(0, bg.getGreen() - 15),
                            Math.max(0, bg.getBlue() - 15)
                    ));
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setRolloverEnabled(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDefaultExample() {
        requestsField.setText("82, 170, 43, 140, 16, 190");
        headField.setText("50");
        maxCylinderField.setText("200");
    }

    private void generateRandomInputs() {
        Random rand = new Random();
        int maxCyl = 200;
        try {
            int parsedMax = Integer.parseInt(maxCylinderField.getText().trim());
            if (parsedMax > 10) maxCyl = parsedMax;
        } catch (NumberFormatException ignored) {
            maxCylinderField.setText("200");
        }

        int head = rand.nextInt(maxCyl);
        headField.setText(String.valueOf(head));

        int count = 8;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(", ");
            sb.append(rand.nextInt(maxCyl));
        }
        requestsField.setText(sb.toString());
    }

    private void clearAll() {
        requestsField.setText("");
        headField.setText("");
        tableModel.setRowCount(0);
        sequenceLabel.setText("Seek Sequence: -");
        totalSeekLabel.setText("Total Head Movement (Seek Time): -");
        avgSeekLabel.setText("Average Seek Distance: -");
        chartPanel.clearData();
    }

    private void runSSTF() {
        try {
            String reqText = requestsField.getText().trim();
            String headText = headField.getText().trim();
            String maxCylText = maxCylinderField.getText().trim();

            if (reqText.isEmpty() || headText.isEmpty() || maxCylText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in the Request Queue, Initial Head Position, and Total Cylinders.",
                        "Missing Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int totalCylinders = Integer.parseInt(maxCylText);
            if (totalCylinders <= 1) {
                throw new IllegalArgumentException("Total cylinders must be greater than 1.");
            }

            int initialHead = Integer.parseInt(headText);
            if (initialHead < 0 || initialHead >= totalCylinders) {
                throw new IllegalArgumentException(
                        "Initial head position must be between 0 and " + (totalCylinders - 1) + ".");
            }

            String[] tokens = reqText.split("[,\\s]+");
            List<Integer> requests = new ArrayList<>();
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    int cyl = Integer.parseInt(token);
                    if (cyl < 0 || cyl >= totalCylinders) {
                        throw new IllegalArgumentException(
                                "Request cylinder " + cyl + " is out of bounds [0, " + (totalCylinders - 1) + "].");
                    }
                    requests.add(cyl);
                }
            }

            if (requests.isEmpty()) {
                throw new IllegalArgumentException("Please enter at least one disk request.");
            }

            // Execute Shortest Seek Time First (SSTF) Algorithm
            List<SeekStep> steps = computeSSTF(requests, initialHead);

            // Populate Table
            tableModel.setRowCount(0);
            StringBuilder seqBuilder = new StringBuilder();
            seqBuilder.append(initialHead);

            int totalSeek = 0;
            for (SeekStep step : steps) {
                tableModel.addRow(new Object[]{
                        step.stepNumber,
                        step.fromCylinder,
                        step.toCylinder,
                        step.distance,
                        step.cumulativeDistance
                });
                seqBuilder.append(" -> ").append(step.toCylinder);
                totalSeek = step.cumulativeDistance;
            }

            double avgSeek = (double) totalSeek / steps.size();

            sequenceLabel.setText("Seek Sequence: " + seqBuilder);
            totalSeekLabel.setText("Total Head Movement (Seek Time): " + totalSeek + " cylinders");
            avgSeekLabel.setText(String.format("Average Seek Distance: %.2f cylinders", avgSeek));

            // Update Visual Chart
            chartPanel.setData(initialHead, steps, totalCylinders);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid integer values for cylinders and head position.",
                    "Invalid Number Format", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Range Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Core SSTF Scheduling Algorithm
     * Repeatedly selects the unserviced request with the minimum seek distance
     * from the current read/write head position.
     */
    private List<SeekStep> computeSSTF(List<Integer> requests, int initialHead) {
        int n = requests.size();
        boolean[] visited = new boolean[n];
        List<SeekStep> steps = new ArrayList<>();

        int currentHead = initialHead;
        int cumulativeSeek = 0;

        for (int step = 1; step <= n; step++) {
            int closestIndex = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
                    int distance = Math.abs(requests.get(i) - currentHead);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestIndex = i;
                    }
                }
            }

            visited[closestIndex] = true;
            int nextCylinder = requests.get(closestIndex);
            cumulativeSeek += minDistance;

            steps.add(new SeekStep(step, currentHead, nextCylinder, minDistance, cumulativeSeek));
            currentHead = nextCylinder;
        }

        return steps;
    }

    /**
     * Custom Swing Panel to render the disk head-movement graph.
     */
    private static class SeekChartPanel extends JPanel {
        private Integer initialHead = null;
        private List<SeekStep> steps = new ArrayList<>();
        private int totalCylinders = 200;

        SeekChartPanel() {
            setBackground(Color.WHITE);
        }

        void setData(int initialHead, List<SeekStep> steps, int totalCylinders) {
            this.initialHead = initialHead;
            this.steps = new ArrayList<>(steps);
            this.totalCylinders = totalCylinders;
            repaint();
        }

        void clearData() {
            this.initialHead = null;
            this.steps.clear();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (initialHead == null || steps.isEmpty()) {
                g.setColor(Color.GRAY);
                g.drawString("Run SSTF to display the disk head movement graph.", 30, 50);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padLeft = 36;
            int padRight = 36;
            int padTop = 48;
            int padBottom = 30;

            int width = getWidth() - padLeft - padRight;
            int height = getHeight() - padTop - padBottom;
            if (width <= 20 || height <= 20) {
                g2.dispose();
                return;
            }

            int maxCyl = Math.max(1, totalCylinders - 1);

            // Draw top cylinder axis line
            g2.setColor(new Color(148, 163, 184));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padLeft, padTop, padLeft + width, padTop);

            // Axis boundary labels (0 and maxCylinder)
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(71, 85, 105));
            g2.drawString("0", padLeft - 4, padTop - 16);
            String maxLabel = String.valueOf(maxCyl);
            g2.drawString(maxLabel, padLeft + width - g2.getFontMetrics().stringWidth(maxLabel) / 2, padTop - 16);

            // Collect all visited points including initial head
            List<Integer> points = new ArrayList<>();
            points.add(initialHead);
            for (SeekStep s : steps) {
                points.add(s.toCylinder);
            }

            int numPoints = points.size();
            double yStep = (double) height / Math.max(1, numPoints - 1);

            // Draw dashed vertical guide lines & top tick labels
            Stroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
            for (int cyl : points) {
                int x = padLeft + (int) Math.round(((double) cyl / maxCyl) * width);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(dashed);
                g2.drawLine(x, padTop, x, padTop + height);

                g2.setColor(new Color(51, 65, 85));
                String lbl = String.valueOf(cyl);
                int lblW = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, x - lblW / 2, padTop - 5);
            }

            // Draw seek path segments
            g2.setColor(new Color(37, 99, 235));
            g2.setStroke(new BasicStroke(2.2f));
            for (int i = 0; i < numPoints - 1; i++) {
                double x1 = padLeft + ((double) points.get(i) / maxCyl) * width;
                double y1 = padTop + i * yStep;
                double x2 = padLeft + ((double) points.get(i + 1) / maxCyl) * width;
                double y2 = padTop + (i + 1) * yStep;
                g2.draw(new Line2D.Double(x1, y1, x2, y2));
            }

            // Draw nodes
            for (int i = 0; i < numPoints; i++) {
                int cyl = points.get(i);
                double x = padLeft + ((double) cyl / maxCyl) * width;
                double y = padTop + i * yStep;

                g2.setColor(i == 0 ? new Color(22, 163, 74) : new Color(29, 78, 216));
                g2.fill(new Ellipse2D.Double(x - 4.5, y - 4.5, 9, 9));

                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String tag = (i == 0) ? cyl + " (Start)" : String.valueOf(cyl);
                g2.drawString(tag, (int) x + 7, (int) y + 4);
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            SSTFSimulator app = new SSTFSimulator();
            app.setVisible(true);
        });
    }
}