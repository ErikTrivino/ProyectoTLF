package modelo;

// Clase auxiliar Token
class Token {
    private final String tipo;
    private final String lexema;
    private final int posicion;

    public Token(String tipo, String lexema, int posicion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = posicion;
    }

    @Override
    public String toString() {
        return String.format("%s: '%s' en posición %d", tipo, lexema, posicion);
    }
}
// Automatas para reconocer tokens de Bash

 class AutomataPalabraClave {
    private static final String[] palabrasClave = {
            "if", "fi", "then", "else", "for", "while", "do", "done", "echo"
    };

    public static boolean reconocer(String input) {
        for (String palabra : palabrasClave) {
            if (palabra.equals(input)) return true;
        }
        return false;
    }
}

 class AutomataNumeroNatural {
    public static boolean reconocer(String input) {
        if (input.isEmpty()) return false;
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}

 class AutomataNumeroReal {
    public static boolean reconocer(String input) {
        if (!input.contains(".")) return false;
        String[] partes = input.split("\\.");
        if (partes.length != 2) return false;
        return AutomataNumeroNatural.reconocer(partes[0]) && AutomataNumeroNatural.reconocer(partes[1]);
    }
}

 class AutomataOperadorComparacion {
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

 class AutomataOperadorLogico {
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

 class AutomataOperadorAritmetico {
    private static final String[] operadores = {
            "+", "-", "*", "/", "%", "**"
    };

    public static boolean reconocer(String input) {
        for (String op : operadores) {
            if (op.equals(input)) return true;
        }
        return false;
    }
}

 class AutomataAsignacion {
    public static boolean reconocer(String input) {
        return input.equals("=");
    }
}

 class AutomataIncremento {
    public static boolean reconocer(String input) {
        return input.equals("++") || input.equals("--");
    }
}

 class AutomataIdentificador {
    public static boolean reconocer(String input) {
        if (input.isEmpty() || input.length() > 10) return false;
        if (!Character.isLetter(input.charAt(0)) && input.charAt(0) != '_') return false;
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') return false;
        }
        return true;
    }
}