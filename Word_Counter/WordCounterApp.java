import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class WordCounterApp {
    public static void main(String[] args) {
        try {
            // Set system look and feel for a modern appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            WordCounterGUI gui = new WordCounterGUI();
            gui.setVisible(true);
        });
    }
}