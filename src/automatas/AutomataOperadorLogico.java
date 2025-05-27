package automatas;

public class AutomataOperadorLogico {
    private static final String[] operadores = {
            "&&", "||", "!"
    };

    public static boolean reconocer(String input) {
        for (String op : operadores) {
            if (op.equals(input)) return true;
        }
        return false;
    }
}
