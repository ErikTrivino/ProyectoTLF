package automatas;

public class AutomataIdentificador {
    public static boolean reconocer(String input) {
        if (input.isEmpty() || input.length() > 10) return false;
        if (!Character.isLetter(input.charAt(0)) && input.charAt(0) != '_') return false;
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') return false;
        }
        return true;
    }

}
