public class Symbol {
    String name;
    String descriptor;
    public Symbol(String name, String descriptor) {
        this.name = name;
        this.descriptor = descriptor;
    }
    @Override
    public String toString() { return "Name: " + this.name + "\nDescriptor: " + this.descriptor + "\n"; }
}
