package automatas;

public class AutomataCadena {
    public static boolean reconocer(String input) {
        if (input.length() < 2) return false;
        char inicio = input.charAt(0);
        char fin = input.charAt(input.length() - 1);
        // Comillas simples: todo literal, sin escapes
        if (inicio == '\'' && fin == '\'') {
            for (int i = 1; i < input.length() - 1; i++) {
                if (input.charAt(i) == '\'') {
                    return false;
                }
            }
            return true;
        }
        // Comillas dobles: permite escapes
        if (inicio == '"' && fin == '"') {
            boolean escape = false;
            for (int i = 1; i < input.length() - 1; i++) {
                char c = input.charAt(i);
                if (escape) {
                    escape = false;
                } else if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    return false; // comilla sin escape
                }
            }
            return !escape; // importante: no debe quedar \ colgado justo antes del cierre
        }
        return false;
    }
}
