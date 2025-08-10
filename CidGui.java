import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CidGui extends JFrame {

    // Custom colors for professional theme
    private static final Color DARK_BG = new Color(18, 18, 18);
    private static final Color DARKER_BG = new Color(12, 12, 12);
    private static final Color ACCENT_BLUE = new Color(64, 158, 255);
    private static final Color ACCENT_BLUE_HOVER = new Color(74, 168, 255);
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(178, 178, 178);
    private static final Color BORDER_COLOR = new Color(40, 40, 40);
    private static final Color INPUT_BG = new Color(28, 28, 28);
    private static final Color CARD_BG = new Color(22, 22, 22);

    // GUI Components
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextField keyField;
    private ModernButton encryptButton;
    private ModernButton decryptButton;
    private ModernButton clearButton;
    private ModernButton copyButton;
    private JLabel titleLabel, inputLabel, outputLabel, keyLabel;
    private JPanel mainPanel, headerPanel, contentPanel, footerPanel;
    private JLabel statusLabel;

    public CidGui() {
        setupLookAndFeel();
        initializeWindow();
        createComponents();
        layoutComponents();
        addEventListeners();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Custom UI properties
        UIManager.put("ToolTip.background", CARD_BG);
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private void initializeWindow() {
        setTitle("CID Encryption Dashboard Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set background
        getContentPane().setBackground(DARK_BG);
    }

    private void createComponents() {
        // Main panel with gradient background
        mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Header panel
        headerPanel = createHeaderPanel();
        
        // Content panel
        contentPanel = createContentPanel();
        
        // Footer panel
        footerPanel = createFooterPanel();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        titleLabel = new JLabel("CID ENCRYPTION DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Professional Text Encryption & Decryption Suite");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        header.add(titlePanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 30, 20, 30));
        
        // Input section
        content.add(createInputSection());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Control section
        content.add(createControlSection());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Output section
        content.add(createOutputSection());
        
        return content;
    }

    private JPanel createInputSection() {
        JPanel section = createCardPanel();
        section.setLayout(new BorderLayout(10, 10));
        
        inputLabel = new JLabel("INPUT MESSAGE");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        inputLabel.setForeground(ACCENT_BLUE);
        
        inputTextArea = createStyledTextArea();
        inputTextArea.setToolTipText("Enter the text you want to encrypt or decrypt");
        JScrollPane inputScrollPane = createStyledScrollPane(inputTextArea);
        inputScrollPane.setPreferredSize(new Dimension(840, 120));
        
        section.add(inputLabel, BorderLayout.NORTH);
        section.add(inputScrollPane, BorderLayout.CENTER);
        
        return section;
    }

    private JPanel createControlSection() {
        JPanel section = createCardPanel();
        section.setLayout(new BorderLayout());
        
        // Key input panel
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        keyPanel.setOpaque(false);
        
        keyLabel = new JLabel("ENCRYPTION KEY");
        keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        keyLabel.setForeground(ACCENT_BLUE);
        keyLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        keyField = createStyledTextField();
        keyField.setText("4");
        keyField.setPreferredSize(new Dimension(80, 35));
        keyField.setToolTipText("Enter a number (1-25) for Caesar cipher shift");
        
        JPanel keyInputPanel = new JPanel(new BorderLayout());
        keyInputPanel.setOpaque(false);
        keyInputPanel.add(keyLabel, BorderLayout.NORTH);
        keyInputPanel.add(keyField, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        clearButton = new ModernButton("CLEAR", BORDER_COLOR, new Color(60, 60, 60));
        encryptButton = new ModernButton("ENCRYPT", ACCENT_BLUE, ACCENT_BLUE_HOVER);
        decryptButton = new ModernButton("DECRYPT", ACCENT_GREEN, new Color(86, 185, 90));
        
        buttonPanel.add(clearButton);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setOpaque(false);
        controlsPanel.add(keyInputPanel, BorderLayout.WEST);
        controlsPanel.add(buttonPanel, BorderLayout.EAST);
        
        section.add(controlsPanel, BorderLayout.CENTER);
        
        return section;
    }

    private JPanel createOutputSection() {
        JPanel section = createCardPanel();
        section.setLayout(new BorderLayout(10, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        outputLabel = new JLabel("OUTPUT RESULT");
        outputLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        outputLabel.setForeground(ACCENT_BLUE);
        
        copyButton = new ModernButton("COPY", BORDER_COLOR, new Color(60, 60, 60));
        copyButton.setPreferredSize(new Dimension(80, 25));
        
        headerPanel.add(outputLabel, BorderLayout.WEST);
        headerPanel.add(copyButton, BorderLayout.EAST);
        
        outputTextArea = createStyledTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(new Color(15, 15, 15));
        outputTextArea.setToolTipText("Encrypted/Decrypted text will appear here");
        JScrollPane outputScrollPane = createStyledScrollPane(outputTextArea);
        outputScrollPane.setPreferredSize(new Dimension(840, 120));
        
        section.add(headerPanel, BorderLayout.NORTH);
        section.add(outputScrollPane, BorderLayout.CENTER);
        
        return section;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 30, 15, 30));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_SECONDARY);
        
        JLabel versionLabel = new JLabel("CID Dashboard Pro v2.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(TEXT_SECONDARY);
        
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(versionLabel, BorderLayout.EAST);
        
        return footer;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }

    private JTextArea createStyledTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBackground(INPUT_BG);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setCaretColor(TEXT_PRIMARY);
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        return textArea;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(INPUT_BG);
        textField.setForeground(TEXT_PRIMARY);
        textField.setCaretColor(TEXT_PRIMARY);
        textField.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(6, BORDER_COLOR),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JScrollPane createStyledScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new RoundedBorder(8, BORDER_COLOR));
        scrollPane.setBackground(INPUT_BG);
        scrollPane.getViewport().setBackground(INPUT_BG);
        
        // Custom scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        
        return scrollPane;
    }

    private void layoutComponents() {
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void addEventListeners() {
        encryptButton.addActionListener(e -> processTextWithAnimation(true));
        decryptButton.addActionListener(e -> processTextWithAnimation(false));
        clearButton.addActionListener(e -> clearFields());
        copyButton.addActionListener(e -> copyToClipboard());
    }

    private void processTextWithAnimation(boolean encrypt) {
        // Disable buttons during processing
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        
        statusLabel.setText(encrypt ? "Encrypting..." : "Decrypting...");
        
        // Use SwingWorker for background processing to avoid freezing the GUI
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String message = inputTextArea.getText().trim();
                if (message.isEmpty()) {
                    throw new Exception("Please enter a message to process");
                }
                int key = Integer.parseInt(keyField.getText().trim());
                if (key < 1 || key > 25) {
                    throw new Exception("Key must be between 1 and 25");
                }
                
                if (!encrypt) {
                    key = -key;
                }
                // Simulate a short delay for UX
                Thread.sleep(200); 
                return performCaesarCipher(message, key);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    outputTextArea.setText(result);
                    statusLabel.setText("Operation completed successfully");
                } catch (Exception ex) {
                    String errorMessage = ex.getMessage();
                    if (ex.getCause() instanceof NumberFormatException) {
                        errorMessage = "Invalid key! Please enter a valid number";
                    }
                    statusLabel.setText("Error: " + errorMessage);
                    outputTextArea.setText("");
                } finally {
                    encryptButton.setEnabled(true);
                    decryptButton.setEnabled(true);
                    
                    // Reset status after delay
                    Timer resetTimer = new Timer(3000, evt -> {
                        statusLabel.setText("Ready");
                        ((Timer) evt.getSource()).stop();
                    });
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                }
            }
        };
        worker.execute();
    }

    private void clearFields() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        keyField.setText("4");
        statusLabel.setText("Fields cleared");
        
        Timer resetTimer = new Timer(2000, e -> {
            statusLabel.setText("Ready");
            ((Timer) e.getSource()).stop();
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void copyToClipboard() {
        String text = outputTextArea.getText();
        if (!text.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(text), null);
            statusLabel.setText("Copied to clipboard");
            
            Timer resetTimer = new Timer(2000, e -> {
                statusLabel.setText("Ready");
                ((Timer) e.getSource()).stop();
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }

    private static String performCaesarCipher(String msg, int key) {
        StringBuilder newMsg = new StringBuilder();
        
        for (char c : msg.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                int currentPos = c - 'a';
                int newPos = (currentPos + key) % 26;
                if (newPos < 0) newPos += 26;
                newMsg.append((char) ('a' + newPos));
            } else if (c >= 'A' && c <= 'Z') {
                int currentPos = c - 'A';
                int newPos = (currentPos + key) % 26;
                if (newPos < 0) newPos += 26;
                newMsg.append((char) ('A' + newPos));
            } else {
                newMsg.append(c);
            }
        }
        return newMsg.toString();
    }

    // --- Custom Inner Classes for Modern UI ---
    
    private static class ModernButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color baseColor, Color hoverColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 35));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color currentColor = isEnabled() ? (isHovered ? hoverColor : baseColor) : new Color(60, 60, 60);
            
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gradient = new GradientPaint(
                    0, 0, DARK_BG,
                    0, getHeight(), DARKER_BG
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color borderColor;

        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = BORDER_COLOR;
            this.trackColor = INPUT_BG;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                            thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CidGui().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
