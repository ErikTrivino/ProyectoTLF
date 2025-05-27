package automatas;

public class AutomataIncremento {
    public static boolean reconocer(String input) {
        return input.equals("++") || input.equals("--");
    }
}
