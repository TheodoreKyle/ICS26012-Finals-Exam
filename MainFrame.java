import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String MENU_CARD = "menu";
    private static final String ABOUT_CARD = "about";
    private static final String MULTILEVEL_QUEUE_CARD = "multilevelQueue";
    private static final String ROUND_ROBIN_CARD = "roundRobin";

    private static final String SAMPLE_ROUND_ROBIN_CARD = "sampleRoundRobin";

    private JButton multilevelQueueButton;
    private JButton roundRobinButton;
    private JButton aboutButton;

    private JButton sampleMultilevelQueueButton;
    private JButton sampleRoundRobinButton; 

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private AboutPanel aboutPanel;
    private MultilevelQueuePanel multilevelQueuePanel;
    private RoundRobinPanel roundRobinPanel;
    private SampleRoundRobinPanel sampleRoundRobinPanel;


    public MainFrame() {
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Round Robin");
        setSize(1450, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());  
    }

    private void initializeComponents() {
        multilevelQueueButton = new JButton("Multilevel Queue");
        multilevelQueueButton.setFocusPainted(false);
        multilevelQueueButton.setPreferredSize(new Dimension(240, 100));
        multilevelQueueButton.setFont(new Font("Arial", Font.BOLD, 24));
        multilevelQueueButton.setForeground(Color.BLACK);
        multilevelQueueButton.setBackground(new Color(255, 160, 160));
        multilevelQueueButton.setContentAreaFilled(false);
        multilevelQueueButton.setOpaque(true);
        multilevelQueueButton.setBorderPainted(false);

        multilevelQueueButton.getModel().addChangeListener(e -> {
            if (multilevelQueueButton.getModel().isPressed()) {
                multilevelQueueButton.setBackground(new Color(220, 120, 120));
            } else {
                multilevelQueueButton.setBackground(new Color(255, 160, 160));
            }
        });

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

        sampleMultilevelQueueButton = new JButton("<html><center>Sample<br>Multilevel Queue</center></html>");
        sampleMultilevelQueueButton.setFocusPainted(false);
        sampleMultilevelQueueButton.setPreferredSize(new Dimension(240, 100));
        sampleMultilevelQueueButton.setFont(new Font("Arial", Font.BOLD, 24));
        sampleMultilevelQueueButton.setForeground(Color.BLACK);
        sampleMultilevelQueueButton.setBackground(new Color(247, 245, 97));
        sampleMultilevelQueueButton.setContentAreaFilled(false);
        sampleMultilevelQueueButton.setOpaque(true);
        sampleMultilevelQueueButton.setBorderPainted(false);

        sampleMultilevelQueueButton.getModel().addChangeListener(e -> {
            if (sampleMultilevelQueueButton.getModel().isPressed()) {
                sampleMultilevelQueueButton.setBackground(new Color(210, 210, 80)); 
            } else {
                sampleMultilevelQueueButton.setBackground(new Color(247, 245, 97));
            }
        });

        sampleRoundRobinButton = new JButton("<html><center>Sample<br>Round Robin</center></html>");
        sampleRoundRobinButton.setFocusPainted(false);
        sampleRoundRobinButton.setPreferredSize(new Dimension(240, 100));
        sampleRoundRobinButton.setFont(new Font("Arial", Font.BOLD, 24));
        sampleRoundRobinButton.setForeground(Color.BLACK);
        sampleRoundRobinButton.setBackground(new Color(247, 245, 97));
        sampleRoundRobinButton.setContentAreaFilled(false);
        sampleRoundRobinButton.setOpaque(true);
        sampleRoundRobinButton.setBorderPainted(false);

        sampleRoundRobinButton.getModel().addChangeListener(e -> {
            if (sampleRoundRobinButton.getModel().isPressed()) {
                sampleRoundRobinButton.setBackground(new Color(210, 210, 80)); 
            } else {
                sampleRoundRobinButton.setBackground(new Color(247, 245, 97));
            }
        });

        Color aboutNormalColor = new Color(160, 200, 255);
        Color aboutPressedColor = new Color(120, 160, 220);

        aboutButton.getModel().addChangeListener(e -> {
            if (aboutButton.getModel().isPressed()) {
                aboutButton.setBackground(aboutPressedColor);
            } else {
                aboutButton.setBackground(aboutNormalColor);
            }
        });

        aboutButton.addActionListener(e -> {
            if (aboutPanel == null) {
                aboutPanel = new AboutPanel(() -> cardLayout.show(cardPanel, MENU_CARD));
                cardPanel.add(aboutPanel, ABOUT_CARD);
            }
            cardLayout.show(cardPanel, ABOUT_CARD);
        });

        multilevelQueueButton.addActionListener(e -> {
            if (multilevelQueuePanel == null) {
                multilevelQueuePanel = new MultilevelQueuePanel(() -> cardLayout.show(cardPanel, MENU_CARD));
                cardPanel.add(multilevelQueuePanel, MULTILEVEL_QUEUE_CARD);
            }
            cardLayout.show(cardPanel, MULTILEVEL_QUEUE_CARD);
        });

        roundRobinButton.addActionListener(e -> {
            if (roundRobinPanel == null) {
                roundRobinPanel = new RoundRobinPanel(() -> cardLayout.show(cardPanel, MENU_CARD));
                cardPanel.add(roundRobinPanel, ROUND_ROBIN_CARD);
            }
            cardLayout.show(cardPanel, ROUND_ROBIN_CARD);
        });

        sampleRoundRobinButton.addActionListener(e -> {
            if (sampleRoundRobinPanel == null) {
                sampleRoundRobinPanel = new SampleRoundRobinPanel(() -> cardLayout.show(cardPanel, MENU_CARD));
                cardPanel.add(sampleRoundRobinPanel, SAMPLE_ROUND_ROBIN_CARD);
            }
            cardLayout.show(cardPanel, SAMPLE_ROUND_ROBIN_CARD);
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 20);
        centerPanel.add(multilevelQueueButton, gbc); 

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); 
        centerPanel.add(roundRobinButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 20); 
        centerPanel.add(sampleMultilevelQueueButton, gbc); 

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); 
        centerPanel.add(sampleRoundRobinButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; 
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(aboutButton, gbc);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(centerPanel, MENU_CARD);

        add(cardPanel, BorderLayout.CENTER);
    }
}