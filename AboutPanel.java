import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {

    public AboutPanel(Runnable onBack) {
        setLayout(new BorderLayout());
        add(createTopPanel(onBack), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
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

    private JPanel createContentPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        textPanel.add(createLabel("ICS26012 Finals Examination - 3ITB", Font.BOLD, 26));
        textPanel.add(Box.createVerticalStrut(30));
        textPanel.add(createLabel("Presented by:", Font.BOLD, 22));
        textPanel.add(Box.createVerticalStrut(15));
        textPanel.add(createLabel("\u2022 Choa, Theodore Kyle", Font.PLAIN, 20));
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(createLabel("\u2022 Garcia, Mark Russel", Font.PLAIN, 20));
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(createLabel("\u2022 Lagman, John Raphael", Font.PLAIN, 20));
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(createLabel("\u2022 Vega, Wyn Icezel", Font.PLAIN, 20));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(textPanel);
        return centerPanel;
    }

    private JLabel createLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}