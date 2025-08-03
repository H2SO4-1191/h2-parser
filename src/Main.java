import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
public class Main {
    static JFrame mainFrame;
    static JPanel backgroundPanel;
    static JTextArea inputTextArea;
    static Table tokenTable, symbolTable;
    static DefaultTableModel tokenTableData, symbolTableData;
    static JScrollPane inputScroll;
    static JMenuBar mainMenu;
    static JMenu dropMenu;
    static Item run;
    static Item auto;
    static Item open;
    static Item save;
    static Item lines;
    static Item newFile;
    static Item musicItem;
    static Item exit;
    static ArrayList<Item> menuItems;
    static JPopupMenu suggestions;
    static final Color tableBackground = new Color(20, 33, 61);
    static final Color textAreaBackground = new Color(33, 37, 41);
    static final Color mioMagenta = new Color(185, 1, 112);
    static final Color zoeYellow = new Color(185, 190, 1);
    static Font customFont;
    static Clip backgroundMusic, sound;
    static JLabel autoLbl;
    static String fileName;
    static String filePath;
    static boolean autoBool = true;
    static boolean machineTyping = false;
    static boolean soundBool = true;
    static boolean isCurly = false;
    static {
        UIManager.put("Menu.selectionBackground", Color.WHITE);
        UIManager.put("Menu.selectionForeground", mioMagenta);
        UIManager.put("MenuItem.selectionBackground", Color.WHITE);
        UIManager.put("MenuItem.selectionForeground", mioMagenta);
        UIManager.put("Table.selectionBackground", Color.WHITE);
        UIManager.put("Table.selectionForeground", mioMagenta);
    }
    public static void main(String[] args) {
        setupMainFrame();
    }
    private static void setupMainFrame() {
        essentials();
        textArea();
        tables();
        inputScrollBars();
        mainMenu();
        finalizing();
        sayWelcome();
    }
    private static void essentials() {
        //Font
        try {customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/RetroGaming.ttf")).deriveFont(Font.PLAIN, 16);}
        catch (Exception e) {System.out.println(e.getMessage());}
        //Music
        setMusic(0);
        //Frame
        mainFrame = new JFrame("H2COMPILER - Unsaved.txt");
        mainFrame.setIconImage(new ImageIcon("src/assets/disco-ball.png").getImage());
        mainFrame.setSize(new Dimension(750, 600));
        mainFrame.setMinimumSize(new Dimension(750, 600));
        mainFrame.setPreferredSize(new Dimension(750, 600));
        mainFrame.getContentPane().setMinimumSize(new Dimension(750, 600));
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitPressed();
            }
        });
        mainFrame.setLayout(new BorderLayout(5, 5));
        mainFrame.setLocationRelativeTo(null);
        //Panel
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image back = new ImageIcon("src/assets/background.png").getImage();
                g.drawImage(back, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
    }
    private static void textArea() {
        inputTextArea = new JTextArea();
        inputTextArea.setFont(customFont);
        inputTextArea.setBackground(textAreaBackground);
        inputTextArea.setForeground(mioMagenta);
        inputTextArea.setMargin(new Insets(10, 10, 10, 10));
        inputTextArea.setCaretColor(zoeYellow);
        inputTextArea.setSelectedTextColor(zoeYellow);
        inputTextArea.setSelectionColor(mioMagenta);
        inputTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (suggestions.isVisible()) {
                        e.consume();
                        try {
                            suggestions.show(inputTextArea, inputTextArea.getCaret().getMagicCaretPosition().x, inputTextArea.getCaret().getMagicCaretPosition().y + inputTextArea.getFont().getSize());
                        } catch(Exception ex) {
                            suggestions.show(inputTextArea, 0, inputTextArea.getFont().getSize());
                        }
                    }
                    suggestions.requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    if (!suggestions.isVisible()) {
                        suggestions.removeAll();
                        for(Symbol id: LexicalAnalyzer.symbolsList) suggestions.add(new Item(id.name));
                        for(String keyword: LexicalAnalyzer.keywords) suggestions.add(new Item(keyword));
                        try {
                            suggestions.show(inputTextArea, inputTextArea.getCaret().getMagicCaretPosition().x, inputTextArea.getCaret().getMagicCaretPosition().y + inputTextArea.getFont().getSize());
                        } catch(Exception ex) {
                            suggestions.show(inputTextArea, 0, inputTextArea.getFont().getSize());
                        }
                    } else {
                        try {
                            suggestions.requestFocusInWindow();
                            ((JMenuItem) suggestions.getComponent(0)).doClick();
                            inputTextArea.requestFocusInWindow();
                            suggestions.setVisible(false);
                        } catch (Exception ex) {
                            for(Symbol id: LexicalAnalyzer.symbolsList) suggestions.add(new Item(id.name));
                            for(String keyword: LexicalAnalyzer.keywords) suggestions.add(new Item(keyword));
                            try {
                                suggestions.show(inputTextArea, inputTextArea.getCaret().getMagicCaretPosition().x, inputTextArea.getCaret().getMagicCaretPosition().y + inputTextArea.getFont().getSize());
                            } catch(Exception exc) {
                                suggestions.show(inputTextArea, 0, inputTextArea.getFont().getSize());
                            }
                        }
                    }
                }
            }
            public void keyTyped(java.awt.event.KeyEvent e) {
                customKeyTyped(e.getKeyChar());
            }
        });
        inputTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                LexicalAnalyzer.validate();
                suggestions.removeAll();
                if(autoBool) analyze();
                LexicalAnalyzer.analyzeLine(inputTextArea.getText());
                addSuggestions();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                LexicalAnalyzer.validate();
                suggestions.removeAll();
                suggestions.setVisible(false);
                if(autoBool) analyze();
            }
            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        inputScroll = new JScrollPane(inputTextArea);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        suggestions = new JPopupMenu();
        suggestions.setBackground(tableBackground);
        suggestions.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                try {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                suggestions.setVisible(false);
                suggestions.removeAll();
                inputTextArea.requestFocusInWindow();
            }
        });
        suggestions.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    MenuElement[] elements = MenuSelectionManager.defaultManager().getSelectedPath();
                    if (elements.length > 0 && elements[elements.length - 1] instanceof JMenuItem) {
                        ((JMenuItem) elements[elements.length - 1]).doClick();
                        inputTextArea.requestFocusInWindow();
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    e.consume();
                    suggestions.setVisible(false);
                    inputTextArea.requestFocusInWindow();
                }
            }
        });
    }
    private static void tables() {
        tokenTableData = new DefaultTableModel(new String[] {"Token", "Type"}, 0);
        tokenTable = new Table(tokenTableData);
        symbolTableData = new DefaultTableModel(new String[] {"Symbol", "Descriptor"}, 0);
        symbolTable = new Table(symbolTableData);
    }
    private static void inputScrollBars() {
        final JScrollBar[] allScrolls = { inputScroll.getVerticalScrollBar(), inputScroll.getHorizontalScrollBar() };
        for(JScrollBar scroll: allScrolls) {
            scroll.setBackground(textAreaBackground);
            UIManager.put("ScrollBar.thumb", new ColorUIResource(mioMagenta));
            scroll.setUI(new BasicScrollBarUI());
            scroll.setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors(){
                    this.thumbColor = mioMagenta;
                }
            });
        }
    }
    private static void mainMenu() {
        mainMenu = new JMenuBar();
        mainMenu.setBackground(textAreaBackground);
        dropMenu = new JMenu("MENU");
        dropMenu.setMnemonic(KeyEvent.VK_M);
        dropMenu.setIcon(new ImageIcon("src/assets/menu.png"));
        run = new Item("Run");
        run.addActionListener(e -> analyze());
        run.setMnemonic(KeyEvent.VK_R);
        auto = new Item("Auto");
        auto.addActionListener(e -> autoPressed());
        auto.setMnemonic(KeyEvent.VK_A);
        newFile = new Item("New");
        newFile.addActionListener(e -> newFilePressed());
        newFile.setMnemonic(KeyEvent.VK_N);
        open = new Item("Open");
        open.addActionListener(e -> openPressed());
        open.setMnemonic(KeyEvent.VK_O);
        save = new Item("Save");
        save.addActionListener(e -> saveOrDiscard());
        save.setMnemonic(KeyEvent.VK_S);
        lines = new Item("Lines");
        lines.addActionListener(e -> linesPressed());
        lines.setMnemonic(KeyEvent.VK_L);
        musicItem = new Item("Music");
        musicItem.addActionListener(e -> setMusic(1));
        musicItem.setMnemonic(KeyEvent.VK_M);
        exit = new Item("Exit");
        exit.addActionListener(e -> exitPressed());
        exit.setMnemonic(KeyEvent.VK_E);
        dropMenu.setBackground(Main.textAreaBackground);
        dropMenu.setForeground(Main.zoeYellow);
        dropMenu.setFont(Main.customFont);
        dropMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dropMenu.setBackground(Color.WHITE);
                dropMenu.setForeground(mioMagenta);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dropMenu.setBackground(textAreaBackground);
                dropMenu.setForeground(zoeYellow);
            }
        });
        menuItems = new ArrayList<>(List.of(run, auto, newFile, open, save, lines, musicItem, exit));
        for(Item item: menuItems) dropMenu.add(item);
        mainMenu.add(dropMenu);
        dropMenu.setOpaque(true);
        mainMenu.add(Box.createHorizontalGlue());
        autoLbl = new JLabel("Auto-Analyze: ON");
        autoLbl.setForeground(zoeYellow);
        autoLbl.setFont(customFont);
        autoLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        mainMenu.add(autoLbl);
    }
    private static void finalizing() {
        //TextArea and Tables
        JSplitPane tables = new JSplitPane((JSplitPane.VERTICAL_SPLIT), tokenTable, symbolTable);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputScroll, tables);
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation((int) (mainFrame.getWidth() * 0.6));
                tables.setDividerLocation((int) (mainFrame.getHeight() * 0.6));
            }
        });
        //Adding
        backgroundPanel.add(splitPane, BorderLayout.CENTER);
        mainFrame.add(backgroundPanel, BorderLayout.CENTER);
        mainFrame.setJMenuBar(mainMenu);
        mainFrame.setVisible(true);
    }
    private static void sayWelcome() {
        String randomPath = "src/assets/".concat(String.valueOf(new Random().nextInt(3))).concat(".txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(randomPath))) {
            inputTextArea.setFocusable(false);
            StringBuilder fullText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fullText.append(line).append("\n");
            }
            Timer timer = new Timer(10, e -> {
                if (!fullText.isEmpty()) {
                    inputTextArea.append(fullText.substring(0, 1));
                    fullText.deleteCharAt(0);
                } else {
                    ((Timer)e.getSource()).stop();
                    inputTextArea.setFocusable(true);
                    inputTextArea.requestFocusInWindow();
                }
            });
            timer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void customKeyTyped(char key) {
        if(!machineTyping) makeSound();
        int caretPos = inputTextArea.getCaretPosition();
        switch (key) {
            case '\'', '"':
                inputTextArea.insert(String.valueOf(key), caretPos);
                inputTextArea.setCaretPosition(caretPos);
                break;
            case '(':
                inputTextArea.insert(")", caretPos);
                inputTextArea.setCaretPosition(caretPos);
                break;
            case '{':
                inputTextArea.insert("}", caretPos);
                inputTextArea.setCaretPosition(caretPos);
                isCurly = true;
                break;
            case '[':
                inputTextArea.insert("]", caretPos);
                inputTextArea.setCaretPosition(caretPos);
                break;
            case '*':
                if(inputTextArea.getText().charAt(caretPos-1) == '/') {
                    inputTextArea.insert("*/", caretPos);
                    inputTextArea.setCaretPosition(caretPos);
                }
                break;
            case '\n':
                try {
                    suggestions.setVisible(false);
                    String textBeforeCaret = inputTextArea.getText(0, caretPos);
                    String textAfterCaret = inputTextArea.getText(caretPos, inputTextArea.getDocument().getLength() - caretPos);
                    if(textBeforeCaret.contains("{")) {
                        int i = 0;
                        String spaces = "";
                        for(char c: textBeforeCaret.toCharArray()) if(c == '{') i++; else if(c == '}') i--;
                        for (int j = 0; j < i-1; j++) spaces = spaces.concat("     ");
                        if(textBeforeCaret.trim().endsWith("{") && textAfterCaret.contains("}")){
                            if(i > 1) inputTextArea.insert(spaces, caretPos);
                            if(isCurly) {
                                inputTextArea.insert("\n" , caretPos);
                                isCurly = false;
                            }
                            inputTextArea.setCaretPosition(caretPos);
                        }
                        if(i > 0) inputTextArea.insert(spaces + "     ", caretPos);
                    }
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }
    private static void addSuggestions() {
        String textBeforeCaret = inputTextArea.getText().substring(0, inputTextArea.getCaretPosition()+1);
        if(!textBeforeCaret.endsWith(" ")) {
            String[] words = textBeforeCaret.trim().split("\\s+");
            String lastWord = words.length > 0 ? words[words.length - 1] : "";
            for(String keyword: LexicalAnalyzer.keywords) if(!lastWord.isBlank() && keyword.contains(lastWord)) suggestions.add(new Item(keyword));
            for(Symbol id: LexicalAnalyzer.symbolsList) if(!lastWord.isBlank() && id.name.contains(lastWord)) suggestions.add(new Item(id.name));
            try {
                if (suggestions.getComponentCount() > 0) suggestions.remove(suggestions.getComponentCount() - 1);
                suggestions.setVisible(true);
                Rectangle rect = inputTextArea.modelToView2D(inputTextArea.getCaretPosition()).getBounds();
                suggestions.show(inputTextArea, rect.x, rect.y + rect.height);
                inputTextArea.requestFocusInWindow();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else suggestions.setVisible(false);
    }
    private static void analyze() {
        tokenTableData.setRowCount(0);
        symbolTableData.setRowCount(0);
        for(Token token: LexicalAnalyzer.analyzeLine(inputTextArea.getText())) tokenTableData.addRow(new String[] {token.value, token.type});
        dealWithSymbolTable();
        LexicalAnalyzer.validate();
        if(autoBool) {
            tokenTable.scrollRectToVisible(tokenTable.table.getCellRect(tokenTable.table.getRowCount() - 1, 0, true));
            symbolTable.scrollRectToVisible(symbolTable.table.getCellRect(symbolTable.table.getRowCount() - 1, 0, true));
        } else {
            tokenTable.scrollRectToVisible(tokenTable.table.getCellRect(0, 0, true));
            symbolTable.scrollRectToVisible(symbolTable.table.getCellRect(0, 0, true));
        }
    }
    private static void dealWithSymbolTable() {
        symbolTableData.setRowCount(0);
        for(Symbol symbol: LexicalAnalyzer.symbolsList) symbolTableData.addRow(new String[] {symbol.name, symbol.descriptor});
    }
    private static void autoPressed() {
        autoBool = !autoBool;
        if(autoBool) {
            autoLbl.setText("Auto-Analyze: ON");
            autoLbl.setForeground(zoeYellow);
        } else {
            autoLbl.setText("Auto-Analyze: OFF");
            autoLbl.setForeground(mioMagenta);
        }
        analyze();
    }
    private static void newFilePressed() {
        int answer = saveOrDiscard();
        if(answer == 0 || answer == 1) {
            fileName = null;
            filePath = null;
            mainFrame.setTitle("H2COMPILER - Unsaved.txt");
            inputTextArea.setText("");
            tokenTableData.setRowCount(0);
            LexicalAnalyzer.validate();
        }
    }
    private static void openPressed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                int answer = saveOrDiscard();
                if(answer == 0 || answer == 1) {
                    Scanner scanner = new Scanner(selectedFile);
                    String fileContent = "";
                    while (scanner.hasNext()) fileContent = fileContent.concat(scanner.nextLine() + System.lineSeparator());
                    inputTextArea.setText("");
                    machineTyping = true;
                    for (char c : fileContent.toCharArray()) {
                        if (c == '\n') inputTextArea.dispatchEvent(new KeyEvent(inputTextArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n'));
                        else inputTextArea.dispatchEvent(new KeyEvent(inputTextArea, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, c));
                    }
                    int end = inputTextArea.getText().lastIndexOf("\n");
                    if (end != -1) inputTextArea.replaceRange("", end, inputTextArea.getText().length());
                    fileName = selectedFile.getName();
                    filePath = selectedFile.getAbsolutePath();
                    mainFrame.setTitle("H2COMPILER - " + fileName);
                    machineTyping = false;
                    tokenTableData.setRowCount(0);
                    ArrayList<Token> tokens = LexicalAnalyzer.analyze(selectedFile);
                    for(Token token :tokens) tokenTableData.addRow(new String[] { token.value, token.type });
                    LexicalAnalyzer.validate();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    private static void linesPressed() {
        JOptionPane.showMessageDialog(mainFrame, "Type your code in the console terminal.", "Hint", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Type your code here:\n");
        Scanner scanner = new Scanner(System.in);
        try (PrintWriter writer = new PrintWriter("src/assets/sampleCode.txt")) {
            String writeLine = scanner.nextLine();
            while(!writeLine.equals("0")) {
                writer.println(writeLine);
                writeLine = scanner.nextLine();
            }
            System.out.println("\nFile saved successfully, result is in the main frame.\n");
            writer.flush();
            try(BufferedReader reader = new BufferedReader(new FileReader("src/assets/sampleCode.txt"))) {
                String code = "";
                String readLine = reader.readLine();
                while (readLine != null) {
                    System.out.println(readLine);
                    code = code.concat(readLine + "\n");
                    readLine = reader.readLine();
                }
                tokenTableData.setRowCount(0);
                for(Token token: LexicalAnalyzer.analyzeLine(code)) tokenTableData.addRow(new String[] {token.value, token.type});
                dealWithSymbolTable();
                LexicalAnalyzer.validate();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void exitPressed() {
        int answer = saveOrDiscard();
        if(answer == 0 || answer == 1) System.exit(0);
    }
    private static int saveOrDiscard() {
        String message = fileName == null ? "Do you wish to save this file?" : "Do you wish to save changes?";
        int answer = JOptionPane.showConfirmDialog(mainFrame, message, fileName == null ? "Unsaved.txt" : fileName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch(answer) {
            case 0:
                if(fileName == null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
                    if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        if(!fileToSave.getName().toLowerCase().endsWith(".txt")) fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                        try (FileWriter writer = new FileWriter(fileToSave)) {
                            writer.write(inputTextArea.getText());
                            mainFrame.setTitle("H2COMPILER - " + fileToSave.getName());
                            fileName = fileToSave.getName();
                            filePath = fileToSave.getAbsolutePath();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                } else {
                    try (FileWriter writer = new FileWriter(filePath)) {
                        writer.write(inputTextArea.getText());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case -1:
            case 1:
            case 2: break;
        }
        return answer;
    }
    private static void setMusic(int q) {
        try {
            File musicFile = new File("src/assets/Krayzius & Brainstorm - Virtual Boy.wav");
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
            if(q == 0) {
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInput);
                backgroundMusic.start();
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                if(backgroundMusic.isRunning()) backgroundMusic.stop();
                else backgroundMusic.start();
                soundBool = !soundBool;
            }
        } catch (Exception e) {System.out.println(e.getMessage());}
    }
    private static void makeSound() {
        try {
            if(soundBool) {
                File musicFile = new File("src/assets/pixel-hit.wav");
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
                sound = AudioSystem.getClip();
                sound.open(audioInput);
                sound.start();
            }
        } catch (Exception e) {System.out.println(e.getMessage());}
    }
}