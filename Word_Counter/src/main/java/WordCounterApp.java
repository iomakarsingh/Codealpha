import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class WordCounterApp {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Enable anti-aliasing for text
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            WordCounterGUI gui = new WordCounterGUI();
            gui.setVisible(true);
        });
    }
}