import javax.swing.*;
import java.awt.*;

public class Item extends JMenuItem {
    private final String title;
    public Item(String title) {
        this.title = title;
        setText(this.title);
        setBackground(Main.textAreaBackground);
        setForeground(Main.zoeYellow);
        setFont(Main.customFont);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Color.WHITE);
                setForeground(Main.mioMagenta);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(Main.textAreaBackground);
                setForeground(Main.zoeYellow);
            }
        });
        addActionListener(e -> selected());
    }
    private void selected () {
        if(!Main.menuItems.contains(this)) {
            String textBeforeCaret = Main.inputTextArea.getText().substring(0, Main.inputTextArea.getCaretPosition());
            int ifSpace = textBeforeCaret.endsWith(" ") ? 1 : 0;
            String[] words = textBeforeCaret.trim().split("\\s+");
            String lastWord = words.length > 0 ? words[words.length - 1] : "";
            String[] withBrackets = {"if", "elseIf", "when", "for", "while", "try", "catch",};
            boolean withBracketsBool = false;
            for(String word: withBrackets)
                if (word.equals(title)) {
                    withBracketsBool = true;
                    break;
                }
            if(ifSpace == 0) {
                if(withBracketsBool) {
                    Main.inputTextArea.replaceRange(title + "( )", Main.inputTextArea.getCaretPosition() - lastWord.length(), Main.inputTextArea.getCaretPosition());
                    Main.inputTextArea.setCaretPosition(Main.inputTextArea.getCaretPosition()-1);
                }
                else Main.inputTextArea.replaceRange(title + " ", Main.inputTextArea.getCaretPosition() - lastWord.length(), Main.inputTextArea.getCaretPosition());
            } else {
                if(withBracketsBool) {
                    Main.inputTextArea.insert(title + "( )", Main.inputTextArea.getCaretPosition());
                    Main.inputTextArea.setCaretPosition(Main.inputTextArea.getCaretPosition()-1);
                }
                else Main.inputTextArea.insert(title + " ", Main.inputTextArea.getCaretPosition());
            }
            Main.suggestions.setVisible(false);
            Main.inputTextArea.requestFocusInWindow();
        }
    }
}
