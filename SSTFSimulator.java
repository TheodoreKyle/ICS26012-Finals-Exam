import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
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
    private SeekGraphPanel graphPanel;

    // Data model for each step in SSTF execution
    private static class SeekStep {
        final int stepNumber;
        final int fromCylinder;
        final int toCylinder;
        final long distance;
        final long cumulativeDistance;

        SeekStep(int stepNumber, int fromCylinder, int toCylinder, long distance, long cumulativeDistance) {
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
        setSize(1040, 680);
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

        // --- CENTER: Split Pane (Step Table on Left, Visual Seek Graph on Right) ---
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

        graphPanel = new SeekGraphPanel();
        graphPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Disk Head Movement Graph",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, graphPanel);
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
        graphPanel.clearData();
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

            long totalSeek = 0;
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

            // Update Visual Graph
            graphPanel.setData(initialHead, steps, totalCylinders);

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
        long cumulativeSeek = 0;

        for (int step = 1; step <= n; step++) {
            int closestIndex = -1;
            long minDistance = Long.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
                    long distance = Math.abs((long) requests.get(i) - currentHead);
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
     * Disk Head Movement Graph Panel where every top-axis label, dashed guide line,
     * and plotted node shares the exact same aligned X coordinate.
     */
    private static class SeekGraphPanel extends JPanel {
        private List<Integer> seekSequence = new ArrayList<>();
        private int diskSize = 200;

        public SeekGraphPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(520, 400));
        }

        public void setData(int initialHead, List<SeekStep> steps, int totalCylinders) {
            List<Integer> seq = new ArrayList<>();
            seq.add(initialHead);
            for (SeekStep step : steps) {
                seq.add(step.toCylinder);
            }
            this.seekSequence = seq;
            this.diskSize = totalCylinders;
            repaint();
        }

        public void clearData() {
            this.seekSequence.clear();
            repaint();
        }

        /**
         * Computes non-overlapping X positions pinned between leftAnchor and rightAnchor
         * while preserving relative proximity to true proportional cylinder positions.
         */
        private int[] computeAlignedAxisX(int[] trueX, int[] widths, int minGap, int leftAnchor, int rightAnchor) {
            int n = trueX.length;
            int[] x = new int[n];
            if (n == 0) return x;
            if (n == 1) {
                x[0] = leftAnchor;
                return x;
            }

            double[] reqDist = new double[n - 1];
            double totalReq = 0;
            for (int i = 0; i < n - 1; i++) {
                reqDist[i] = (widths[i] + widths[i + 1]) / 2.0 + minGap;
                totalReq += reqDist[i];
            }

            double availableSpan = rightAnchor - leftAnchor;
            if (totalReq > availableSpan && totalReq > 0) {
                double scale = availableSpan / totalReq;
                for (int i = 0; i < n - 1; i++) {
                    reqDist[i] *= scale;
                }
            }

            double[] d = new double[n];
            d[0] = 0.0;
            for (int i = 1; i < n; i++) {
                d[i] = d[i - 1] + reqDist[i - 1];
            }

            double[] blockVal = new double[n];
            int[] blockWeight = new int[n];
            int[] blockStart = new int[n];
            int numBlocks = 0;

            for (int i = 0; i < n; i++) {
                double val = trueX[i] - d[i];
                blockVal[numBlocks] = val;
                blockWeight[numBlocks] = 1;
                blockStart[numBlocks] = i;
                numBlocks++;

                while (numBlocks >= 2 && blockVal[numBlocks - 2] > blockVal[numBlocks - 1]) {
                    int w1 = blockWeight[numBlocks - 2];
                    int w2 = blockWeight[numBlocks - 1];
                    double mergedVal = (blockVal[numBlocks - 2] * w1 + blockVal[numBlocks - 1] * w2) / (w1 + w2);
                    blockVal[numBlocks - 2] = mergedVal;
                    blockWeight[numBlocks - 2] = w1 + w2;
                    numBlocks--;
                }
            }

            double[] pos = new double[n];
            for (int b = 0; b < numBlocks; b++) {
                int start = blockStart[b];
                int end = (b + 1 < numBlocks) ? blockStart[b + 1] : n;
                for (int i = start; i < end; i++) {
                    pos[i] = blockVal[b] + d[i];
                }
            }

            // Pin boundary markers (0 and maxCylinder) to exact ends of the axis line
            pos[0] = leftAnchor;
            for (int i = 1; i < n; i++) {
                if (pos[i] < pos[i - 1] + reqDist[i - 1]) {
                    pos[i] = pos[i - 1] + reqDist[i - 1];
                }
            }

            pos[n - 1] = rightAnchor;
            for (int i = n - 2; i >= 0; i--) {
                if (pos[i] > pos[i + 1] - reqDist[i]) {
                    pos[i] = Math.max(leftAnchor, pos[i + 1] - reqDist[i]);
                }
            }

            for (int i = 0; i < n; i++) {
                x[i] = (int) Math.round(pos[i]);
            }
            return x;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (seekSequence == null || seekSequence.isEmpty()) {
                g.setColor(Color.GRAY);
                g.drawString("Run a simulation to view the head movement path.", 40, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();

            int maxCylinder = Math.max(1, diskSize - 1);
            List<Integer> seq = seekSequence;

            Set<Integer> uniqueSet = new TreeSet<>(seq);
            Set<Integer> topAxisMarkers = new TreeSet<>(uniqueSet);
            topAxisMarkers.add(0);
            topAxisMarkers.add(maxCylinder);

            List<Integer> sortedMarkers = new ArrayList<>(topAxisMarkers);
            int n = sortedMarkers.size();
            int[] widths = new int[n];
            int totalLabelWidth = 0;

            for (int i = 0; i < n; i++) {
                widths[i] = fm.stringWidth(String.valueOf(sortedMarkers.get(i)));
                totalLabelWidth += widths[i];
            }

            int padLeft = 45;
            int padRight = 70;
            int plotW = getWidth() - padLeft - padRight;
            if (plotW <= 20) {
                g2.dispose();
                return;
            }

            int minGap = 10;
            boolean useTwoTiers = (totalLabelWidth + Math.max(0, n - 1) * minGap) > plotW;
            int padTop = useTwoTiers ? 50 : 38;
            int padBottom = 30;
            int plotH = getHeight() - padTop - padBottom;
            if (plotH <= 20) {
                g2.dispose();
                return;
            }

            // Draw main horizontal cylinder axis
            g2.setColor(new Color(150, 160, 175));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padLeft, padTop, padLeft + plotW, padTop);

            int[] trueX = new int[n];
            for (int i = 0; i < n; i++) {
                int cyl = sortedMarkers.get(i);
                trueX[i] = padLeft + (int) Math.round(((double) cyl / maxCylinder) * plotW);
            }

            int effectiveGap = useTwoTiers ? 2 : minGap;
            int[] alignedX = computeAlignedAxisX(trueX, widths, effectiveGap, padLeft, padLeft + plotW);

            // Map each cylinder to its exact aligned X coordinate so labels, vertical lines, and dots match
            Map<Integer, Integer> cylToX = new HashMap<>();
            Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0);

            for (int i = 0; i < n; i++) {
                int cyl = sortedMarkers.get(i);
                int x = alignedX[i];
                cylToX.put(cyl, x);

                int labelY = (useTwoTiers && (i % 2 == 1)) ? (padTop - 23) : (padTop - 8);

                if (uniqueSet.contains(cyl)) {
                    g2.setColor(new Color(215, 220, 230));
                    g2.setStroke(dashed);
                    g2.drawLine(x, padTop, x, padTop + plotH);
                }

                String cylStr = String.valueOf(cyl);
                boolean isBoundary = (cyl == 0 || cyl == maxCylinder) && !uniqueSet.contains(cyl);
                g2.setColor(isBoundary ? new Color(110, 120, 135) : new Color(45, 55, 70));
                g2.drawString(cylStr, x - (widths[i] / 2), labelY);
            }

            int steps = seq.size();
            double stepY = (steps > 1) ? (double) plotH / (steps - 1) : plotH;

            int prevX = cylToX.get(seq.get(0));
            int prevY = padTop;

            for (int i = 1; i < steps; i++) {
                int cyl = seq.get(i);
                int currX = cylToX.get(cyl);
                int currY = padTop + (int) Math.round(i * stepY);

                g2.setColor(new Color(55, 95, 215));
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawLine(prevX, prevY, currX, currY);

                prevX = currX;
                prevY = currY;
            }

            for (int i = 0; i < steps; i++) {
                int cyl = seq.get(i);
                int currX = cylToX.get(cyl);
                int currY = padTop + (int) Math.round(i * stepY);

                g2.setColor(i == 0 ? new Color(65, 165, 75) : new Color(55, 95, 215));
                g2.fillOval(currX - 5, currY - 5, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(currX - 5, currY - 5, 10, 10);

                String pointLabel = (i == 0) ? (cyl + " (Start)") : String.valueOf(cyl);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                int textW = fm.stringWidth(pointLabel);
                int textH = fm.getAscent();

                int prevCyl = (i > 0) ? seq.get(i - 1) : cyl;
                int nextCyl = (i < steps - 1) ? seq.get(i + 1) : cyl;
                boolean placeLeft = (i > 0) && (prevCyl >= cyl) && (nextCyl >= cyl);
                if (currX + 10 + textW > getWidth() - 8) {
                    placeLeft = true;
                }
                if (placeLeft && currX - 10 - textW < 6) {
                    placeLeft = false;
                }

                int labelX = placeLeft ? (currX - 10 - textW) : (currX + 10);
                int labelY = (i == 0) ? (currY + 15) : (currY + 4);

                int boxPadX = 3;
                int boxPadY = 2;
                int boxX = labelX - boxPadX;
                int boxY = labelY - textH - boxPadY + 1;
                int boxW = textW + (boxPadX * 2);
                int boxH = textH + (boxPadY * 2);

                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(boxX, boxY, boxW, boxH, 5, 5);
                g2.setColor(new Color(210, 218, 230));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(boxX, boxY, boxW, boxH, 5, 5);

                g2.setColor(new Color(30, 35, 45));
                g2.drawString(pointLabel, labelX, labelY);
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