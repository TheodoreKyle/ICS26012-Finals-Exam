import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class SampleSstfPanel extends JPanel {

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);

    private static final Font FONT_LABEL = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_VALUE = new Font("Arial", Font.PLAIN, 16);
    private static final Font FONT_TABLE = new Font("Arial", Font.PLAIN, 16);
    private static final Font FONT_TABLE_HEADER = new Font("Arial", Font.BOLD, 16);

    private JTextField requestsField;
    private JTextField headField;
    private JTextField maxCylinderField;

    private DefaultTableModel tableModel;
    private JLabel sequenceLabel;
    private JLabel totalSeekLabel;
    private JLabel avgSeekLabel;
    private SeekGraphPanel graphPanel;

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

    public SampleSstfPanel(Runnable onBack) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        add(createTopPanel(onBack), BorderLayout.NORTH);
        initUI();
        loadSampleAndRun();
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
        topPanel.setOpaque(false);
        topPanel.add(backButton);
        return topPanel;
    }

    private void initUI() { 
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(0, 14, 14, 14));
        root.setBackground(new Color(245, 247, 250));

        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel headerLabel = new JLabel("Sample Shortest Seek Time First ");
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

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblRequests = new JLabel("Request Queue:");
        lblRequests.setFont(FONT_LABEL);
        inputPanel.add(lblRequests, gbc);

        requestsField = createReadOnlyField(28);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        inputPanel.add(requestsField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblHead = new JLabel("Initial Head:");
        lblHead.setFont(FONT_LABEL);
        inputPanel.add(lblHead, gbc);

        headField = createReadOnlyField(6);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.15;
        inputPanel.add(headField, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblMaxCyl = new JLabel("Total Cylinders:");
        lblMaxCyl.setFont(FONT_LABEL);
        inputPanel.add(lblMaxCyl, gbc);

        maxCylinderField = createReadOnlyField(6);
        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 0.15;
        inputPanel.add(maxCylinderField, gbc);

        topContainer.add(inputPanel, BorderLayout.CENTER);
        root.add(topContainer, BorderLayout.NORTH);

        String[] columns = {"Step", "From", "To (Closest)", "Seek Distance", "Cumulative"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable stepTable = new JTable(tableModel);
        stepTable.setRowHeight(34);
        stepTable.setFont(FONT_TABLE);
        stepTable.setForeground(Color.BLACK);
        stepTable.setShowGrid(true);
        stepTable.setGridColor(new Color(203, 213, 225));
        stepTable.getTableHeader().setFont(FONT_TABLE_HEADER);
        stepTable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        stepTable.getTableHeader().setBackground(new Color(241, 245, 249));
        stepTable.setRowSelectionAllowed(false);
        stepTable.setFocusable(false);

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
        splitPane.setDividerLocation(610); 
        splitPane.setBorder(null);
        root.add(splitPane, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 14, 10, 14)
        ));

        sequenceLabel = new JLabel("Seek Sequence: -");
        sequenceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        sequenceLabel.setForeground(new Color(30, 41, 59));

        totalSeekLabel = new JLabel("Total Head Movement (Seek Time): -");
        totalSeekLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalSeekLabel.setForeground(new Color(29, 78, 216));

        avgSeekLabel = new JLabel("Average Seek Distance: -");
        avgSeekLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        avgSeekLabel.setForeground(new Color(51, 65, 85));

        summaryPanel.add(sequenceLabel);
        summaryPanel.add(totalSeekLabel);
        summaryPanel.add(avgSeekLabel);

        root.add(summaryPanel, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);
    }

    private JTextField createReadOnlyField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_VALUE);
        field.setForeground(Color.BLACK);
        field.setEditable(false);
        field.setFocusable(false);
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    private void loadSampleAndRun() {
        List<Integer> requests = Arrays.asList(82, 170, 43, 140, 16, 190);
        int initialHead = 50;
        int totalCylinders = 200;

        requestsField.setText("82, 170, 43, 140, 16, 190");
        headField.setText(String.valueOf(initialHead));
        maxCylinderField.setText(String.valueOf(totalCylinders));

        List<SeekStep> steps = computeSSTF(requests, initialHead);

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

        graphPanel.setData(initialHead, steps, totalCylinders);
    }

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
}