import javax.swing.*;
import java.awt.*;

public class SampleMultilevelQueuePanel extends JPanel {

    private static final Font FONT_BOLD_24 = new Font("Arial", Font.BOLD, 24);
    private static final Font FONT_BOLD_30 = new Font("Arial", Font.BOLD, 30);
    private static final Dimension DIM_BTN_SMALL = new Dimension(100, 40);

    public SampleMultilevelQueuePanel(Runnable onBack) {
        setLayout(new BorderLayout());

        add(createTopPanel(onBack), BorderLayout.NORTH);
        
        add(createContentPanel(), BorderLayout.CENTER);
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

    private JPanel createContentPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Sample Multilevel Queue");
        titleLabel.setFont(FONT_BOLD_30);
        titleLabel.setForeground(Color.BLACK);
        
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;  
        gbc.weighty = 1.0; 
        gbc.insets = new Insets(30, 0, 0, 0); 
        
        centerPanel.add(titleLabel, gbc);
        return centerPanel;
    }
}