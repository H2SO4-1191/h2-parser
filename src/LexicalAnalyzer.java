import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    public LexicalAnalyzer() { throw new UnsupportedOperationException("LexicalAnalyzer cannot be instantiated."); }
    public final static ArrayList<String> keywords = new ArrayList<> (List.of("let", "out", "in", "if", "elseIf", "else", "when", "is", "end",
            "for", "while", "skip", "try", "catch", "function", "return", "true", "false", "null", "new", "to", "integer", "string", "boolean", "float",
            "main", "endl", "class"));
    private final static Set<String> operations = Set.of("=", "+", "-", "*", "/", "%", "^", "&", "|", "!", ">", "<");
    private final static Set<String> punctuation = Set.of("(", ")", "{", "}", "[", "]", ",", ";", ".", "_", "'", "\"");
    private final static ArrayList<Token> tokensList = new ArrayList<>();
    public static ArrayList<Symbol> symbolsList = new ArrayList<>();
    public final static HashMap<String, String> types = new HashMap<>(Map.of(
            "ID", "Identifier",
            "KW", "Keyword",
            "ST", "String",
            "IN", "Integer",
            "FL", "Float",
            "OP", "Operation",
            "PN", "Punctuation",
            "CM", "Comment",
            "UN", "Unknown"
    ));
    public static void validate() {
        tokensList.removeIf(token -> !Main.inputTextArea.getText().contains(token.value));
        symbolsList.removeIf(id -> {
            String regex = "\\b" + Pattern.quote(id.name) + "\\b";
            return !Pattern.compile(regex).matcher(Main.inputTextArea.getText()).find();
        });
    }
    public static ArrayList<Token> analyze(File sourceCodeFile) {
        String sourceCode = "";
        try (Scanner codeScanner = new Scanner(sourceCodeFile)) {
            while (codeScanner.hasNext()) sourceCode = sourceCode.concat(codeScanner.nextLine());
            tokensList.addAll(analyzeLine(sourceCode));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return tokensList;
    }
    public static ArrayList<Token> analyzeLine(String input) {
        ArrayList<Token> currentLineTokens = new ArrayList<>();
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);
            if (Character.isWhitespace(c)) i++; //Blank
            else if (i < input.length() - 1 && c == '/' && input.charAt(i + 1) == '*') { //comments
                i += 2;
                int start = i;
                while (i < input.length() - 1 && input.charAt(i) != '*' && input.charAt(i + 1) != '/') i++;
                if (i == input.length() - 1) i++;
                currentLineTokens.add(new Token(input.substring(start, i), types.get("CM")));
                i += 2;
            } else if (Character.isLetter(c)) { // Identifiers / Keywords
                int start = i;
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || c == '_')) i++;
                String word = input.substring(start, i);
                Token newToken = new Token("", "");
                newToken.value = word;
                newToken.type = keywords.contains(word) ? types.get("KW") : types.get("ID");
                Symbol newSymbol = new Symbol(newToken.value, "Empty");
                ArrayList<String> symbolsNames = new ArrayList<>();
                for (Symbol symbol : symbolsList) symbolsNames.add(symbol.name);
                if(newToken.type.equals(types.get("ID")) && !symbolsNames.contains(newSymbol.name)) symbolsList.add(newSymbol);
                currentLineTokens.add(newToken);
            } else if (c == '"') { //Strings
                i++;
                int start = i;
                while (i < input.length() && input.charAt(i) != '"') i++;
                currentLineTokens.add(new Token(input.substring(start, i), types.get("ST")));
                i++;
            } else if (Character.isDigit(c)) { // Integers / Floats
                int start = i;
                while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                if (i < input.length() - 1 && input.charAt(i) == '.' && Character.isDigit(input.charAt(i + 1))) {
                    do i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i)));
                    currentLineTokens.add(new Token(input.substring(start, i), types.get("FL")));
                } else currentLineTokens.add(new Token(input.substring(start, i), types.get("IN")));
            } else if (operations.contains(String.valueOf(c))) { // Operations
                if (i < input.length() - 1 && operations.contains(String.valueOf(input.charAt(i + 1)))) {
                    currentLineTokens.add(new Token(String.valueOf(c).concat(String.valueOf(input.charAt(i + 1))), types.get("OP")));
                    i += 2;
                } else {
                    currentLineTokens.add(new Token(String.valueOf(c), types.get("OP")));
                    i++;
                }
            } else if (punctuation.contains(String.valueOf(c))) { // Punctuation
                currentLineTokens.add(new Token(String.valueOf(c), types.get("PN")));
                i++;
            } else {
                currentLineTokens.add(new Token(String.valueOf(c), types.get("UN")));
                i++;
            }
        }
        return currentLineTokens;
    }
}
