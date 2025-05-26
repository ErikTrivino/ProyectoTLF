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
            "if", "fi", "then", "else", "for", "while", "do", "done", "echo","_"
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
 class AutomataEnteroConSigno {
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
class AutomataNumeroReal {
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

 class AutomataCadena {
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

class AutomataComentario {
    public static boolean reconocer(String input) {
        int estado = 0;
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);
            switch (estado) {
                case 0:
                    if (Character.isWhitespace(c)) {
                        // seguimos en estado 0
                        i++;
                    } else if (c == '#') {
                        estado = 1;
                        i++;
                    } else {
                        return false; // no es un comentario
                    }
                    break;
                case 1:
                    // estado de comentario, acepta cualquier cosa hasta el final
                    i++;
                    break;
            }
        }

        // estado 1 es de aceptación (se encontró al menos un '#')
        return estado == 1;
    }
}

