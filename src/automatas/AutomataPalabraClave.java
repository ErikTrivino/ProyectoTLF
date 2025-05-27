package automatas;

public class AutomataPalabraClave {
    private static final String[] palabrasClave = {
            "if", "fi", "then", "else", "for", "while", "do", "done", "echo", "_"
    };

    public static boolean reconocer(String input) {
        for (String palabra : palabrasClave) {
            if (palabra.equals(input)) return true;
        }
        return false;
    }
}
