package automatas;

public class AutomataComentario {
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
