import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.awt.datatransfer.*;
import javax.swing.text.DefaultCaret;
import javax.swing.JFrame;
import java.util.List;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;

public class WordCounterGUI extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JLabel wordCountLabel, charCountLabel, sentenceCountLabel, paragraphCountLabel;
    private JLabel readingTimeLabel, speakingTimeLabel;
    private JButton countButton, clearButton;
    private boolean isDarkMode = false;
    
    // Light theme colors
    private static final Color LIGHT_PRIMARY = new Color(70, 130, 180);
    private static final Color LIGHT_BACKGROUND = new Color(250, 250, 252);
    private static final Color LIGHT_SECONDARY = new Color(240, 242, 245);
    private static final Color LIGHT_TEXT = new Color(50, 50, 50);
    
    // Dark theme colors
    private static final Color DARK_PRIMARY = new Color(100, 149, 237);
    private static final Color DARK_BACKGROUND = new Color(30, 30, 35);
    private static final Color DARK_SECONDARY = new Color(45, 45, 50);
    private static final Color DARK_TEXT = new Color(220, 220, 220);

    // Add these as class fields
    private JLabel uniqueWordsLabel;
    private JLabel longestWordLabel;
    private Map<String, Integer> wordFrequencyMap;
    private JProgressBar progressBar;
    private JPanel previewPanel;
    private Timer progressTimer;
    private long totalBytes;
    private long processedBytes;
    private int wordCount;
    private int charCount;
    private int sentenceCount;
    private int paragraphCount;
    private double readingTime;
    private double speakingTime;
    private String longestWord;
    private int uniqueWordCount;

    public WordCounterGUI() {
        // Set up the frame
        setTitle("Advanced Word Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Create menu bar
        setupMenuBar();

        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(LIGHT_BACKGROUND);

        // Header panel with title and theme toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_BACKGROUND);
        
        JLabel titleLabel = new JLabel("Advanced Word Counter");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(LIGHT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JToggleButton themeToggle = new JToggleButton("🌙");
        themeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        themeToggle.setFocusPainted(false);
        styleToggleButton(themeToggle);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(themeToggle, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Text area setup with improved styling
        textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(LIGHT_SECONDARY);
        textArea.setForeground(LIGHT_TEXT);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Scroll pane with custom styling
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 205), 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 10));
        statsPanel.setBackground(LIGHT_BACKGROUND);
        statsPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        wordCountLabel = createStatsLabel("Words: 0");
        charCountLabel = createStatsLabel("Characters: 0");
        sentenceCountLabel = createStatsLabel("Sentences: 0");
        paragraphCountLabel = createStatsLabel("Paragraphs: 0");
        readingTimeLabel = createStatsLabel("Reading Time: 0 min");
        speakingTimeLabel = createStatsLabel("Speaking Time: 0 min");

        statsPanel.add(wordCountLabel);
        statsPanel.add(charCountLabel);
        statsPanel.add(sentenceCountLabel);
        statsPanel.add(paragraphCountLabel);
        statsPanel.add(readingTimeLabel);
        statsPanel.add(speakingTimeLabel);

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(LIGHT_BACKGROUND);

        JButton uploadButton = new JButton("Upload File");
        styleButton(uploadButton);
        buttonPanel.add(uploadButton, 0);
        
        clearButton = new JButton("Clear");
        countButton = new JButton("Analyze Text");
        styleButton(clearButton);
        styleButton(countButton);

        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(clearButton);
        buttonPanel.add(countButton);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(LIGHT_BACKGROUND);
        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Add listeners
        setupListeners();
        setupThemeToggle(themeToggle, mainPanel);

        // Initialize placeholder
        setPlaceholderText();

        // Setup drag and drop
        setupDragAndDrop();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu exportMenu = new JMenu("Export");
        exportMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem exportTextItem = new JMenuItem("Export Statistics to TXT");
        JMenuItem exportCsvItem = new JMenuItem("Export Analysis to CSV");
        
        exportTextItem.addActionListener(e -> exportStats("txt"));
        exportCsvItem.addActionListener(e -> exportStats("csv"));
        
        exportMenu.add(exportTextItem);
        exportMenu.add(exportCsvItem);
        menuBar.add(exportMenu);

        setJMenuBar(menuBar);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                textArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n");
                }
                updateStats();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.write(textArea.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportStats(String format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            format.toUpperCase() + " files", format));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                if (format.equals("txt")) {
                    writer.println("Text Analysis Results");
                    writer.println("===================");
                    writer.println("Words: " + wordCount);
                    writer.println("Characters: " + charCount);
                    writer.println("Sentences: " + sentenceCount);
                    writer.println("Paragraphs: " + paragraphCount);
                    writer.println("Reading Time: " + readingTime + " minutes");
                    writer.println("Speaking Time: " + speakingTime + " minutes");
                } else if (format.equals("csv")) {
                    writer.println("Word,Frequency,Percentage");
                    wordFrequencyMap.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .forEach(entry -> {
                            double percentage = (entry.getValue() * 100.0) / wordCount;
                            writer.printf("%s,%d,%.1f%%\n", 
                                entry.getKey(), 
                                entry.getValue(), 
                                percentage);
                        });
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel createStatsLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(LIGHT_PRIMARY);
        return label;
    }

    private void setupListeners() {
        countButton.addActionListener(e -> showAnalysisPopup());
        clearButton.addActionListener(e -> clearText());
        
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateStats();
            }
        });

        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals("Type or paste your text here...")) {
                    textArea.setText("");
                    textArea.setForeground(isDarkMode ? DARK_TEXT : LIGHT_TEXT);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    setPlaceholderText();
                }
            }
        });

        // Add upload button listener
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals("Upload File")) {
                    button.addActionListener(e -> uploadFile());
                }
            }
        }
    }

    private void setupThemeToggle(JToggleButton themeToggle, JPanel mainPanel) {
        themeToggle.addActionListener(e -> {
            isDarkMode = themeToggle.isSelected();
            themeToggle.setText(isDarkMode ? "☀️" : "🌙");
            updateTheme(mainPanel);
        });
    }

    private void updateTheme(JPanel mainPanel) {
        Color bgColor = isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
        Color primaryColor = isDarkMode ? DARK_PRIMARY : LIGHT_PRIMARY;
        Color secondaryColor = isDarkMode ? DARK_SECONDARY : LIGHT_SECONDARY;
        Color textColor = isDarkMode ? DARK_TEXT : LIGHT_TEXT;

        mainPanel.setBackground(bgColor);
        textArea.setBackground(secondaryColor);
        textArea.setForeground(textColor);
        
        // Update all components' colors
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(bgColor);
                for (Component inner : ((JPanel) comp).getComponents()) {
                    if (inner instanceof JLabel) {
                        inner.setForeground(primaryColor);
                    }
                }
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        
        // Different colors for different buttons
        if (button == clearButton) {
            button.setBackground(new Color(220, 53, 69));  // Red color for clear button
            button.setForeground(Color.WHITE);
            
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(200, 35, 51));  // Darker red on hover
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(220, 53, 69));  // Back to original red
                }
            });
        } else if (button == countButton) {
            button.setBackground(new Color(40, 167, 69));  // Green color for analyze button
            button.setForeground(Color.WHITE);
            
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(33, 136, 56));  // Darker green on hover
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(40, 167, 69));  // Back to original green
                }
            });
        } else if (button.getText().equals("Upload File")) {
            button.setBackground(new Color(0, 123, 255));  // Blue color for upload button
            button.setForeground(Color.WHITE);
            
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(0, 105, 217));  // Darker blue on hover
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(0, 123, 255));  // Back to original blue
                }
            });
        }
        
        // Modern button styling
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add drop shadow effect
        button.putClientProperty("JButton.buttonType", "roundRect");
        
        // Make the button slightly raised
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
    }

    private void styleToggleButton(JToggleButton button) {
        button.setPreferredSize(new Dimension(40, 40));
        button.setBackground(LIGHT_SECONDARY);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updateStats() {
        String text = textArea.getText();
        if (text.equals("Type or paste your text here...")) {
            text = "";
        }

        // Word count
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        
        // Character count
        int charCount = text.length();
        
        // Sentence count
        int sentenceCount = text.isEmpty() ? 0 : text.split("[.!?]+").length;
        
        // Paragraph count
        int paragraphCount = text.isEmpty() ? 0 : text.split("\n\\s*\n").length;
        
        // Calculate reading time (average reading speed: 200 words per minute)
        double readingTime = Math.ceil(wordCount / 200.0);
        
        // Calculate speaking time (average speaking speed: 130 words per minute)
        double speakingTime = Math.ceil(wordCount / 130.0);

        // Add unique words count
        String[] words = text.toLowerCase().split("\\s+");
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        int uniqueWordCount = uniqueWords.size();
        
        // Find longest word
        String longestWord = "";
        if (words.length > 0) {
            longestWord = Arrays.stream(words)
                .filter(w -> w.matches("[a-zA-Z]+"))
                .max(Comparator.comparingInt(String::length))
                .orElse("");
        }
        
        // Word frequency analysis
        wordFrequencyMap = new HashMap<>();
        for (String word : words) {
            if (word.matches("[a-zA-Z]+")) {
                wordFrequencyMap.merge(word, 1, Integer::sum);
            }
        }

        // Update labels
        wordCountLabel.setText("Words: " + wordCount);
        charCountLabel.setText("Characters: " + charCount);
        sentenceCountLabel.setText("Sentences: " + sentenceCount);
        paragraphCountLabel.setText("Paragraphs: " + paragraphCount);
        readingTimeLabel.setText("Reading Time: " + readingTime + " min");
        speakingTimeLabel.setText("Speaking Time: " + speakingTime + " min");

        this.wordCount = wordCount;
        this.charCount = charCount;
        this.sentenceCount = sentenceCount;
        this.paragraphCount = paragraphCount;
        this.readingTime = readingTime;
        this.speakingTime = speakingTime;
        this.longestWord = longestWord;
        this.uniqueWordCount = uniqueWordCount;
    }

    private void showAnalysisPopup() {
        updateStats();

        String text = textArea.getText();
        if (text.equals("Type or paste your text here...")) {
            text = "";
        }

        // Get the current counts
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int charCount = text.length();
        int sentenceCount = text.isEmpty() ? 0 : text.split("[.!?]+").length;
        int paragraphCount = text.isEmpty() ? 0 : text.split("\n\\s*\n").length;
        double readingTime = Math.ceil(wordCount / 200.0);
        double speakingTime = Math.ceil(wordCount / 130.0);

        // Create and show the popup dialog with statistics
        JPanel statsPopup = new JPanel();
        statsPopup.setLayout(new BoxLayout(statsPopup, BoxLayout.Y_AXIS));
        statsPopup.setBorder(new EmptyBorder(20, 30, 20, 30));
        statsPopup.setBackground(Color.WHITE);

        // Add title
        JLabel titleLabel = new JLabel("Text Analysis Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(40, 167, 69));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPopup.add(titleLabel);
        statsPopup.add(Box.createVerticalStrut(15));

        // Create and add statistics labels with custom styling
        String[] stats = {
            "📝 Total Words: " + wordCount,
            "📊 Characters: " + charCount,
            "📋 Sentences: " + sentenceCount,
            "📑 Paragraphs: " + paragraphCount,
            "⏱️ Estimated Reading Time: " + readingTime + " minutes",
            "🗣️ Estimated Speaking Time: " + speakingTime + " minutes"
        };

        for (String stat : stats) {
            JLabel label = new JLabel(stat);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPopup.add(label);
        }

        // Add additional text analysis
        if (!text.isEmpty()) {
            statsPopup.add(Box.createVerticalStrut(15));
            
            // Average word length
            double avgWordLength = text.trim().isEmpty() ? 0 : 
                (double) text.replaceAll("\\s+", "").length() / wordCount;
            JLabel avgWordLabel = new JLabel(String.format("📏 Average Word Length: %.1f characters", avgWordLength));
            avgWordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            avgWordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPopup.add(avgWordLabel);
            
            // Words per sentence
            double wordsPerSentence = sentenceCount == 0 ? 0 : (double) wordCount / sentenceCount;
            JLabel wpsLabel = new JLabel(String.format("📈 Words per Sentence: %.1f", wordsPerSentence));
            wpsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            wpsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPopup.add(wpsLabel);
        }

        // Add more statistics
        statsPopup.add(Box.createVerticalStrut(15));
        
        // Unique words
        JLabel uniqueWordsLabel = new JLabel(String.format("🎯 Unique Words: %d", uniqueWordCount));
        uniqueWordsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        uniqueWordsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPopup.add(uniqueWordsLabel);
        
        // Longest word
        if (!longestWord.isEmpty()) {
            JLabel longestWordLabel = new JLabel(String.format("📏 Longest Word: %s (%d letters)", 
                longestWord, longestWord.length()));
            longestWordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            longestWordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPopup.add(longestWordLabel);
        }
        
        // Add "Show More Details" button
        JButton detailsButton = new JButton("Word Frequency Analysis");
        styleButton(detailsButton);
        detailsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsButton.addActionListener(e -> showWordFrequencyDialog());
        
        statsPopup.add(Box.createVerticalStrut(15));
        statsPopup.add(detailsButton);

        // Show the custom dialog
        JOptionPane optionPane = new JOptionPane(statsPopup, 
            JOptionPane.PLAIN_MESSAGE, 
            JOptionPane.DEFAULT_OPTION, 
            null, 
            new Object[]{}, // No buttons
            null);

        JDialog dialog = optionPane.createDialog(this, "Text Analysis");
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        // Add a close button with matching style to clear button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(220, 53, 69));  // Same red as clear button
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect matching clear button
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(200, 35, 51));  // Darker red on hover
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(220, 53, 69));  // Back to original red
            }
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setBorderPainted(false);  // Matching the clear button style
        
        statsPopup.add(Box.createVerticalStrut(20));
        statsPopup.add(closeButton);

        // Show dialog
        dialog.setVisible(true);
    }

    private void showWordFrequencyDialog() {
        // Create frequency table
        String[] columnNames = {"Word", "Frequency", "Percentage"};
        Object[][] data = wordFrequencyMap.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(20) // Show top 20 words
            .map(entry -> {
                double percentage = (entry.getValue() * 100.0) / wordCount;
                return new Object[]{
                    entry.getKey(),
                    entry.getValue(),
                    String.format("%.1f%%", percentage)
                };
            })
            .toArray(Object[][]::new);

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JDialog dialog = new JDialog(this, "Word Frequency Analysis", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void clearText() {
        setPlaceholderText();
        updateStats();
    }

    private void setPlaceholderText() {
        textArea.setText("Type or paste your text here...");
        textArea.setForeground(new Color(150, 150, 150));
    }

    private void setupDragAndDrop() {
        textArea.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    if (!droppedFiles.isEmpty()) {
                        processFile(droppedFiles.get(0));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(WordCounterGUI.this,
                        "Error processing dropped file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a File");
        
        // Add support for multiple file formats
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Supported Files (*.txt, *.doc, *.docx, *.pdf)", 
            "txt", "doc", "docx", "pdf"));
            
        // Add file preview
        previewPanel = new JPanel(new BorderLayout());
        JTextArea previewArea = new JTextArea(10, 40);
        previewArea.setEditable(false);
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);
        
        fileChooser.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                File file = (File) e.getNewValue();
                if (file != null) {
                    showFilePreview(file, previewArea);
                }
            }
        });
        
        fileChooser.setAccessory(previewPanel);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            processFile(fileChooser.getSelectedFile());
        }
    }

    private void showFilePreview(File file, JTextArea previewArea) {
        try {
            String preview = getFileContent(file, true);
            previewArea.setText(preview.substring(0, Math.min(preview.length(), 500)) + "...");
        } catch (Exception ex) {
            previewArea.setText("Cannot preview this file type");
        }
    }

    private void processFile(File file) {
        // Setup progress dialog
        JDialog progressDialog = new JDialog(this, "Reading File...", true);
        progressDialog.setLayout(new BorderLayout(10, 10));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Processing...");
        
        JLabel statusLabel = new JLabel("Reading file...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        progressPanel.add(statusLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        progressDialog.add(progressPanel);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        // Start processing in background
        SwingWorker<String, Integer> worker = new SwingWorker<String, Integer>() {
            @Override
            protected String doInBackground() throws Exception {
                totalBytes = file.length();
                processedBytes = 0;
                return getFileContent(file, false);
            }

            @Override
            protected void done() {
                try {
                    String content = get();
                    textArea.setText(content);
                    textArea.setForeground(isDarkMode ? DARK_TEXT : LIGHT_TEXT);
                    updateStats();
                    
                    // Show success message
                    showFileInfo(file);
                    
                    progressDialog.dispose();
                } catch (Exception ex) {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(WordCounterGUI.this,
                        "Error processing file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // Start progress update timer
        progressTimer = new Timer(100, e -> {
            int progress = (int) ((processedBytes * 100) / totalBytes);
            progressBar.setValue(progress);
        });
        progressTimer.start();

        worker.execute();
        progressDialog.setVisible(true);
    }

    private String getFileContent(File file, boolean preview) throws Exception {
        String extension = getFileExtension(file);
        StringBuilder content = new StringBuilder();
        
        switch (extension.toLowerCase()) {
            case "txt":
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                        processedBytes += line.length() + 1;
                        if (preview && content.length() > 500) break;
                    }
                }
                break;
                
            case "pdf":
                try (PDDocument document = PDDocument.load(file)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    content.append(stripper.getText(document));
                    processedBytes = totalBytes;
                }
                break;
                
            case "doc":
            case "docx":
                try (FileInputStream fis = new FileInputStream(file);
                     XWPFDocument document = new XWPFDocument(fis)) {
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                    content.append(extractor.getText());
                    processedBytes = totalBytes;
                }
                break;
                
            default:
                throw new UnsupportedOperationException("Unsupported file format: " + extension);
        }
        
        return content.toString();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // Empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private void showFileInfo(File file) {
        // Calculate file size
        long fileSize = file.length();
        String sizeUnit = "bytes";
        double size = fileSize;
        
        if (size > 1024) {
            size = size / 1024;
            sizeUnit = "KB";
        }
        if (size > 1024) {
            size = size / 1024;
            sizeUnit = "MB";
        }
        
        JOptionPane.showMessageDialog(this,
            String.format("File processed successfully!\n\nFile: %s\nSize: %.2f %s\nType: %s",
                file.getName(),
                size,
                sizeUnit,
                getFileExtension(file).toUpperCase()),
            "File Processed",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 
