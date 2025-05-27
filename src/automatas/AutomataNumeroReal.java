package automatas;

public class AutomataNumeroReal {
    public static boolean reconocer(String input) {
        if (!input.contains(".")) return false;
        int signo = 0;
        if (input.startsWith("-")) {
            if (input.length() == 1) return false; // solo "-"
            signo = 1;
        }
        String sinSigno = input.substring(signo); // elimina el signo para dividir
        String[] partes = sinSigno.split("\\.");
        if (partes.length != 2) return false;
        return AutomataNumeroNatural.reconocer(partes[0]) && AutomataNumeroNatural.reconocer(partes[1]);
    }
}
