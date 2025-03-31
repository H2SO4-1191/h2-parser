public class Token {
    String value;
    String type;
    Token(String value, String type) {
        this.value = value;
        this.type = type;
    }
    @Override
    public String toString() { return "Token: " + this.value + "\nType: " + this.type + "\n"; }
}
