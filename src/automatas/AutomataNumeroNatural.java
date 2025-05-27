package automatas;

public class AutomataNumeroNatural {
    public static boolean reconocer(String input) {
        if (input.isEmpty()) return false;
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
