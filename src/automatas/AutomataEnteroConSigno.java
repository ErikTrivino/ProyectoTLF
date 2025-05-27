package automatas;

public class AutomataEnteroConSigno {
    public static boolean reconocer(String input) {
        if (input == null || input.isEmpty()) return false;

        int start = 0;
        if (input.charAt(0) == '-') {
            if (input.length() == 1) return false; // solo "-" no es válido
            start = 1;
        }
        for (int i = start; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i))) return false;
        }
        return true;
    }
}
