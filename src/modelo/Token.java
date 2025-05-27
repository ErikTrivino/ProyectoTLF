package modelo;

import automatas.AutomataNumeroNatural;

/**
 * Representa un token léxico identificado durante el análisis de una cadena de texto.
 * Cada token posee un tipo (categoría léxica) y un valor (contenido textual).
 *
 * Esta clase es utilizada para encapsular la información de los elementos léxicos
 * reconocidos en una cadena de entrada, como palabras clave, identificadores, números, etc.
 */
class Token {
    private final String tipo;
    private final String lexema;
    private final int posicion;

    public Token(String tipo, String lexema, int posicion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = posicion;
    }

    /**
     * Devuelve una representación en cadena del token, combinando su tipo y valor.
     * Este método es útil para mostrar los tokens reconocidos de forma legible.
     *
     * @return Una cadena con el formato "tipo: valor".
     */
    @Override
    public String toString() {
        return String.format("%s: '%s' en posición %d", tipo, lexema, posicion);
    }
}
// Automatas para reconocer tokens de Bash

