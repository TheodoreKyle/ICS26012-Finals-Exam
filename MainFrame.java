import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String MENU_CARD = "menu";
    private static final String ABOUT_CARD = "about";

    private JButton roundRobinButton;
    private JButton aboutButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame() {
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Round Robin");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        roundRobinButton = new JButton("Round Robin");
        roundRobinButton.setFocusPainted(false);

        roundRobinButton.setPreferredSize(new Dimension(240, 100));
        roundRobinButton.setFont(new Font("Arial", Font.BOLD, 24));
        roundRobinButton.setForeground(Color.BLACK);
        roundRobinButton.setBackground(new Color(255, 160, 160));
        roundRobinButton.setContentAreaFilled(false);
        roundRobinButton.setOpaque(true);
        roundRobinButton.setBorderPainted(false);

        roundRobinButton.getModel().addChangeListener(e -> {
            if (roundRobinButton.getModel().isPressed()) {
                roundRobinButton.setBackground(new Color(220, 120, 120));
            } else {
                roundRobinButton.setBackground(new Color(255, 160, 160));
            }
        });

        aboutButton = new JButton("About");
        aboutButton.setFocusPainted(false);

        aboutButton.setPreferredSize(new Dimension(240, 100));
        aboutButton.setFont(new Font("Arial", Font.BOLD, 24));
        aboutButton.setForeground(Color.BLACK);
        aboutButton.setBackground(new Color(160, 200, 255));
        aboutButton.setContentAreaFilled(false);
        aboutButton.setOpaque(true);
        aboutButton.setBorderPainted(false);

        Color aboutNormalColor = new Color(160, 200, 255);
        Color aboutPressedColor = new Color(120, 160, 220);

        aboutButton.getModel().addChangeListener(e -> {
            if (aboutButton.getModel().isPressed()) {
                aboutButton.setBackground(aboutPressedColor);
            } else {
                aboutButton.setBackground(aboutNormalColor);
            }
        });

        // Show the About screen when About is clicked
        aboutButton.addActionListener(e -> cardLayout.show(cardPanel, ABOUT_CARD));

        // Place the buttons one below the other
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // 20px space below Round Robin
        centerPanel.add(roundRobinButton, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(aboutButton, gbc);

        // Screens: main interface and About
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(centerPanel, MENU_CARD);
        cardPanel.add(new AboutPanel(() -> cardLayout.show(cardPanel, MENU_CARD)), ABOUT_CARD);

        add(cardPanel, BorderLayout.CENTER);
    }
}