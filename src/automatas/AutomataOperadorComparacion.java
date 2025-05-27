package automatas;

public class AutomataOperadorComparacion {
    private static final String[] operadores = {
            "-eq", "-ne", "-lt", "-le", "-gt", "-ge", "==", "!=", "<", ">"
    };

    public static boolean reconocer(String input) {
        for (String op : operadores) {
            if (op.equals(input)) return true;
        }
        return false;
    }
}
