import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class WordCounterGUI extends JFrame {
    private JTextArea textArea;
    private JLabel wordCountLabel;
    private JButton countButton;
    private JButton clearButton;
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 252);
    private static final Color SECONDARY_COLOR = new Color(240, 242, 245);

    public WordCounterGUI() {
        // Set up the frame
        setTitle("Modern Word Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(null);

        // Update main panel styling
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Add title label
        JLabel titleLabel = new JLabel("Word Counter");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Update text area styling
        textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(SECONDARY_COLOR);
        textArea.setForeground(new Color(50, 50, 50));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Create and style scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));
        scrollPane.setBackground(SECONDARY_COLOR);
        scrollPane.getViewport().setBackground(SECONDARY_COLOR);

        // Create stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        wordCountLabel = new JLabel("Words: 0");
        wordCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        wordCountLabel.setForeground(PRIMARY_COLOR);
        statsPanel.add(wordCountLabel);

        // Create button panel with new layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // Update buttons
        countButton = new JButton("Count Words");
        clearButton = new JButton("Clear");
        styleButton(countButton);
        styleButton(clearButton);

        // Create bottom panel to hold stats and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        buttonPanel.add(clearButton);
        buttonPanel.add(countButton);

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add button listeners
        countButton.addActionListener(e -> countWords());
        clearButton.addActionListener(e -> clearText());

        // Add key listener for real-time counting
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                countWords();
            }
        });

        // Add placeholder text
        textArea.setText("Type or paste your text here...");
        textArea.setForeground(new Color(150, 150, 150));
        
        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals("Type or paste your text here...")) {
                    textArea.setText("");
                    textArea.setForeground(new Color(50, 50, 50));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText("Type or paste your text here...");
                    textArea.setForeground(new Color(150, 150, 150));
                }
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(new Color(25, 25, 112));  // Dark blue background
        button.setForeground(new Color(255, 255, 255));  // Pure white text
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect with a different color
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 0, 139));  // Medium dark blue on hover
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(25, 25, 112));  // Back to original dark blue
            }
        });
    }

    private void countWords() {
        String text = textArea.getText().trim();
        int wordCount = 0;
        
        if (!text.isEmpty()) {
            wordCount = text.split("\\s+").length;
        }
        
        wordCountLabel.setText("Words: " + wordCount);
    }

    private void clearText() {
        textArea.setText("Type or paste your text here...");
        textArea.setForeground(new Color(150, 150, 150));
        wordCountLabel.setText("Words: 0");
    }
} 