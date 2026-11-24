/**
 * ICS26012 - Operating Systems
 * GUI Panel for FCFS Disk Scheduling
 * Owner: Wyn Icezel A. Vega
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class FCFSDiskPanel extends JPanel {

    private final JTextField txtDiskSize;
    private final JTextField txtInitialHead;
    private final JTextField txtRequests;

    private final DefaultTableModel tableModel;
    private final JLabel lblSeekOrder;
    private final JLabel lblTotalMovement;
    private final JLabel lblAvgSeek;
    private final SeekGraphPanel graphPanel;

    private final DiskScheduler scheduler;
    private final Random random;

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);

    public FCFSDiskPanel(Runnable onBack) {
        this.scheduler = new FCFSDiskScheduler();
        this.random = new Random();
        setLayout(new BorderLayout());

        add(createTopPanel(onBack), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(new EmptyBorder(0, 12, 12, 12));

        JPanel inputContainer = new JPanel(new BorderLayout(8, 8));
        inputContainer.setBorder(new TitledBorder("FCFS Disk Scheduling - Input Parameters"));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDiskSize = new JTextField("200", 10);
        txtInitialHead = new JTextField(10);
        txtRequests = new JTextField(38);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Disk Size (N, default 200):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        fieldsPanel.add(txtDiskSize, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Initial Head Position:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        fieldsPanel.add(txtInitialHead, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Request Queue (comma/space separated):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        fieldsPanel.add(txtRequests, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnRandom = createStyledButton("Random Queue", new Color(0, 0, 128), new Color(0, 0, 90));
        JButton btnReset = createStyledButton("Reset / Clear", new Color(180, 0, 0), new Color(130, 0, 0));
        JButton btnCalculate = createStyledButton("Run Simulation", new Color(0, 100, 0), new Color(0, 70, 0));

        buttonPanel.add(btnRandom);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnCalculate);

        buttonPanel.add(btnRandom);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnCalculate);

        inputContainer.add(fieldsPanel, BorderLayout.CENTER);
        inputContainer.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.add(inputContainer, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.45);

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBorder(new TitledBorder("Step-by-Step Execution & Metrics"));

        String[] columns = {"Step", "From Cylinder", "To Cylinder", "Head Movement"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable resultTable = new JTable(tableModel);
        resultTable.setRowHeight(22);
        leftPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JPanel metricsPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        metricsPanel.setBorder(new EmptyBorder(6, 4, 4, 4));
        lblSeekOrder = new JLabel("Order of Service: -");
        lblTotalMovement = new JLabel("Total Head Movement: -");
        lblAvgSeek = new JLabel("Average Seek Time Distance: -");

        Font boldFont = new Font("SansSerif", Font.BOLD, 13);
        lblTotalMovement.setFont(boldFont);
        lblAvgSeek.setFont(boldFont);

        metricsPanel.add(lblSeekOrder);
        metricsPanel.add(lblTotalMovement);
        metricsPanel.add(lblAvgSeek);
        leftPanel.add(metricsPanel, BorderLayout.SOUTH);

        graphPanel = new SeekGraphPanel();
        graphPanel.setBorder(new TitledBorder("Disk Head Movement Graph"));

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(graphPanel);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER); 

        btnRandom.addActionListener(e -> generateRandomQueue());
        btnReset.addActionListener(e -> resetForm());
        btnCalculate.addActionListener(e -> runSimulation());
    }

    private JButton createStyledButton(String text, Color normalColor, Color pressedColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setForeground(Color.WHITE);
        button.setBackground(normalColor);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(6, 14, 6, 14));

        button.getModel().addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(pressedColor);
            } else {
                button.setBackground(normalColor);
            }
        });

        return button;
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

    private void generateRandomQueue() {
        int diskSize = 200;
        try {
            String diskStr = txtDiskSize.getText().trim();
            if (!diskStr.isEmpty()) {
                int parsed = Integer.parseInt(diskStr);
                if (parsed > 1) {
                    diskSize = parsed;
                } else {
                    txtDiskSize.setText("200");
                }
            } else {
                txtDiskSize.setText("200");
            }
        } catch (NumberFormatException ex) {
            txtDiskSize.setText("200");
        }

        boolean validHead = false;
        try {
            String headStr = txtInitialHead.getText().trim();
            if (!headStr.isEmpty()) {
                int h = Integer.parseInt(headStr);
                if (h >= 0 && h < diskSize) {
                    validHead = true;
                }
            }
        } catch (NumberFormatException ignored) {
        }

        if (!validHead) {
            txtInitialHead.setText(String.valueOf(random.nextInt(diskSize)));
        }

        int count = 10 + random.nextInt(11);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(random.nextInt(diskSize));
            if (i < count - 1) {
                sb.append(", ");
            }
        }
        txtRequests.setText(sb.toString());
    }

    private void resetForm() {
        txtDiskSize.setText("200");
        txtInitialHead.setText("");
        txtRequests.setText("");
        tableModel.setRowCount(0);
        lblSeekOrder.setText("Order of Service: -");
        lblTotalMovement.setText("Total Head Movement: -");
        lblAvgSeek.setText("Average Seek Time Distance: -");
        graphPanel.setResult(null);
    }

    private void runSimulation() {
        try {
            String diskStr = txtDiskSize.getText().trim();
            if (diskStr.isEmpty()) {
                throw new IllegalArgumentException("Disk size cannot be empty.");
            }
            int diskSize = Integer.parseInt(diskStr);
            if (diskSize <= 0) {
                throw new IllegalArgumentException("Disk size must be a positive integer.");
            }

            String headStr = txtInitialHead.getText().trim();
            if (headStr.isEmpty()) {
                throw new IllegalArgumentException("Initial head position cannot be empty.");
            }
            int initialHead = Integer.parseInt(headStr);
            if (initialHead < 0 || initialHead >= diskSize) {
                throw new IllegalArgumentException("Initial head position must be between 0 and " + (diskSize - 1) + ".");
            }

            String reqStr = txtRequests.getText().trim();
            if (reqStr.isEmpty()) {
                throw new IllegalArgumentException("Request queue cannot be empty.");
            }
            String[] tokens = reqStr.split("[,\\s]+");
            List<Integer> requests = new ArrayList<>();
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    int cyl = Integer.parseInt(token);
                    if (cyl < 0 || cyl >= diskSize) {
                        throw new IllegalArgumentException("Cylinder " + cyl + " is out of valid range [0, " + (diskSize - 1) + "].");
                    }
                    requests.add(cyl);
                }
            }

            if (requests.isEmpty()) {
                throw new IllegalArgumentException("Please enter at least one valid cylinder request.");
            }

            DiskScheduleResult result = scheduler.schedule(requests, initialHead, diskSize, "NONE");
            displayResult(result);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid numeric input. Please enter whole numbers for disk size, head position, and requests.",
                    "Input Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResult(DiskScheduleResult result) {
        tableModel.setRowCount(0);
        List<Integer> seq = result.getSeekSequence();
        List<Integer> moves = result.getStepMovements();

        for (int i = 0; i < moves.size(); i++) {
            tableModel.addRow(new Object[]{
                    i + 1,
                    seq.get(i),
                    seq.get(i + 1),
                    moves.get(i)
            });
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seq.size(); i++) {
            sb.append(seq.get(i));
            if (i < seq.size() - 1) sb.append(" -> ");
        }

        lblSeekOrder.setText("Order of Service: " + sb);
        lblTotalMovement.setText("Total Head Movement: " + result.getTotalHeadMovement() + " cylinders");
        lblAvgSeek.setText(String.format("Average Seek Time Distance: %.2f cylinders", result.getAverageSeekDistance()));

        graphPanel.setResult(result);
    }

    private static class SeekGraphPanel extends JPanel {
        private DiskScheduleResult result;

        public SeekGraphPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(520, 400));
        }

        public void setResult(DiskScheduleResult result) {
            this.result = result;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (result == null || result.getSeekSequence().isEmpty()) {
                g.setColor(Color.GRAY);
                g.drawString("Run a simulation to view the head movement path.", 40, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padLeft = 45;
            int padRight = 70;
            int padTop = 36;
            int padBottom = 30;

            int plotW = getWidth() - padLeft - padRight;
            int plotH = getHeight() - padTop - padBottom;
            if (plotW <= 0 || plotH <= 0) return;

            int maxCylinder = Math.max(1, result.getDiskSize() - 1);
            List<Integer> seq = result.getSeekSequence();

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();

            g2.setColor(new Color(150, 160, 175));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padLeft, padTop, padLeft + plotW, padTop);

            Set<Integer> uniqueSet = new TreeSet<>(seq);
            Set<Integer> topAxisMarkers = new TreeSet<>(uniqueSet);
            topAxisMarkers.add(0);
            topAxisMarkers.add(maxCylinder);

            List<Integer> sortedMarkers = new ArrayList<>(topAxisMarkers);
            int n = sortedMarkers.size();
            int[] labelCenterX = new int[n];
            int[] widths = new int[n];

            for (int i = 0; i < n; i++) {
                int cyl = sortedMarkers.get(i);
                labelCenterX[i] = padLeft + (int) Math.round(((double) cyl / maxCylinder) * plotW);
                widths[i] = fm.stringWidth(String.valueOf(cyl));
            }

            int minGap = 6;
            for (int pass = 0; pass < 8; pass++) {
                for (int i = 0; i < n - 1; i++) {
                    int rightEdge = labelCenterX[i] + (widths[i] / 2);
                    int leftEdgeNext = labelCenterX[i + 1] - (widths[i + 1] / 2);
                    int overlap = (rightEdge + minGap) - leftEdgeNext;
                    if (overlap > 0) {
                        int shift = (overlap + 1) / 2;
                        labelCenterX[i] -= shift;
                        labelCenterX[i + 1] += shift;
                    }
                }
            }

            Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0);

            for (int i = 0; i < n; i++) {
                int cyl = sortedMarkers.get(i);
                int trueX = padLeft + (int) Math.round(((double) cyl / maxCylinder) * plotW);

                if (uniqueSet.contains(cyl)) {
                    g2.setColor(new Color(215, 220, 230));
                    g2.setStroke(dashed);
                    g2.drawLine(trueX, padTop, trueX, padTop + plotH);
                }

                String cylStr = String.valueOf(cyl);
                boolean isBoundary = (cyl == 0 || cyl == maxCylinder) && !uniqueSet.contains(cyl);
                g2.setColor(isBoundary ? new Color(110, 120, 135) : new Color(45, 55, 70));
                g2.drawString(cylStr, labelCenterX[i] - (widths[i] / 2), padTop - 8);
            }

            int steps = seq.size();
            double stepY = (steps > 1) ? (double) plotH / (steps - 1) : plotH;

            int prevX = padLeft + (int) Math.round(((double) seq.get(0) / maxCylinder) * plotW);
            int prevY = padTop;

            for (int i = 1; i < steps; i++) {
                int cyl = seq.get(i);
                int currX = padLeft + (int) Math.round(((double) cyl / maxCylinder) * plotW);
                int currY = padTop + (int) Math.round(i * stepY);

                g2.setColor(new Color(55, 95, 215));
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawLine(prevX, prevY, currX, currY);

                prevX = currX;
                prevY = currY;
            }

            for (int i = 0; i < steps; i++) {
                int cyl = seq.get(i);
                int currX = padLeft + (int) Math.round(((double) cyl / maxCylinder) * plotW);
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
        }
    }
}