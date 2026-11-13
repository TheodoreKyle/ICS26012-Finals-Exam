import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String MENU_CARD = "menu";
    private static final String ABOUT_CARD = "about";
    private static final String MULTILEVEL_QUEUE_CARD = "multilevelQueue";
    private static final String ROUND_ROBIN_CARD = "roundRobin";

    private static final String SAMPLE_ROUND_ROBIN_CARD = "sampleRoundRobin";
    private static final String SAMPLE_MULTILEVEL_QUEUE_CARD = "sampleMultilevelQueue";

    private JButton multilevelQueueButton;
    private JButton roundRobinButton;
    private JButton sstfButton;
    private JButton fcfsButton;
    private JButton aboutButton;

    private JButton sampleMultilevelQueueButton;
    private JButton sampleRoundRobinButton;
    private JButton sampleSstfButton;
    private JButton sampleFcfsButton; 

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private AboutPanel aboutPanel;
    private MultilevelQueuePanel multilevelQueuePanel;
    private RoundRobinPanel roundRobinPanel;
    private SampleRoundRobinPanel sampleRoundRobinPanel;
    private SampleMultilevelQueuePanel sampleMultilevelQueuePanel;


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

        sstfButton = new JButton("<html><center>Shortest Seek<br>Time First</center></html>");
        sstfButton.setFocusPainted(false);
        sstfButton.setPreferredSize(new Dimension(240, 100));
        sstfButton.setFont(new Font("Arial", Font.BOLD, 24));
        sstfButton.setForeground(Color.BLACK);
        sstfButton.setBackground(new Color(255, 160, 160));
        sstfButton.setContentAreaFilled(false);
        sstfButton.setOpaque(true);
        sstfButton.setBorderPainted(false);

        sstfButton.getModel().addChangeListener(e -> {
            if (sstfButton.getModel().isPressed()) {
                sstfButton.setBackground(new Color(220, 120, 120));
            } else {
                sstfButton.setBackground(new Color(255, 160, 160));
            }
        });

        fcfsButton = new JButton("<html><center>First Come<br>First Serve</center></html>");
        fcfsButton.setFocusPainted(false);
        fcfsButton.setPreferredSize(new Dimension(240, 100));
        fcfsButton.setFont(new Font("Arial", Font.BOLD, 24));
        fcfsButton.setForeground(Color.BLACK);
        fcfsButton.setBackground(new Color(255, 160, 160));
        fcfsButton.setContentAreaFilled(false);
        fcfsButton.setOpaque(true);
        fcfsButton.setBorderPainted(false);

        fcfsButton.getModel().addChangeListener(e -> {
            if (fcfsButton.getModel().isPressed()) {
                fcfsButton.setBackground(new Color(220, 120, 120));
            } else {
                fcfsButton.setBackground(new Color(255, 160, 160));
            }
        });

        sampleSstfButton = new JButton("<html><center>Sample<br>Shortest Seek<br>Time First</center></html>");
        sampleSstfButton.setFocusPainted(false);
        sampleSstfButton.setPreferredSize(new Dimension(240, 100));
        sampleSstfButton.setFont(new Font("Arial", Font.BOLD, 20));
        sampleSstfButton.setForeground(Color.BLACK);
        sampleSstfButton.setBackground(new Color(247, 245, 97));
        sampleSstfButton.setContentAreaFilled(false);
        sampleSstfButton.setOpaque(true);
        sampleSstfButton.setBorderPainted(false);

        sampleSstfButton.getModel().addChangeListener(e -> {
            if (sampleSstfButton.getModel().isPressed()) {
                sampleSstfButton.setBackground(new Color(210, 210, 80)); 
            } else {
                sampleSstfButton.setBackground(new Color(247, 245, 97));
            }
        });

        sampleFcfsButton = new JButton("<html><center>Sample<br>First Come<br>First Serve</center></html>");
        sampleFcfsButton.setFocusPainted(false);
        sampleFcfsButton.setPreferredSize(new Dimension(240, 100));
        sampleFcfsButton.setFont(new Font("Arial", Font.BOLD, 20));
        sampleFcfsButton.setForeground(Color.BLACK);
        sampleFcfsButton.setBackground(new Color(247, 245, 97));
        sampleFcfsButton.setContentAreaFilled(false);
        sampleFcfsButton.setOpaque(true);
        sampleFcfsButton.setBorderPainted(false);

        sampleFcfsButton.getModel().addChangeListener(e -> {
            if (sampleFcfsButton.getModel().isPressed()) {
                sampleFcfsButton.setBackground(new Color(210, 210, 80)); 
            } else {
                sampleFcfsButton.setBackground(new Color(247, 245, 97));
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

        sampleMultilevelQueueButton.addActionListener(e -> {
            if (sampleMultilevelQueuePanel == null) {
                sampleMultilevelQueuePanel = new SampleMultilevelQueuePanel(() -> cardLayout.show(cardPanel, MENU_CARD));
                cardPanel.add(sampleMultilevelQueuePanel, SAMPLE_MULTILEVEL_QUEUE_CARD);
            }
            cardLayout.show(cardPanel, SAMPLE_MULTILEVEL_QUEUE_CARD);
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 20);
        centerPanel.add(multilevelQueueButton, gbc); 

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 20); 
        centerPanel.add(roundRobinButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 20); 
        centerPanel.add(sstfButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); 
        centerPanel.add(fcfsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 20); 
        centerPanel.add(sampleMultilevelQueueButton, gbc); 

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 20); 
        centerPanel.add(sampleRoundRobinButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 20); 
        centerPanel.add(sampleSstfButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); 
        centerPanel.add(sampleFcfsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4; 
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(aboutButton, gbc);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(centerPanel, MENU_CARD);

        add(cardPanel, BorderLayout.CENTER);
    }
}