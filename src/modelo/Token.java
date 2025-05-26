package modelo;

/**
 * Representa un token léxico identificado durante el análisis de una cadena de texto.
 * Cada token posee un tipo (categoría léxica) y un valor (contenido textual).
 *
 * Esta clase es utilizada para encapsular la información de los elementos léxicos
 * reconocidos en una cadena de entrada, como palabras clave, identificadores, números, etc.
 */
class Token {
    /** Tipo o categoría del token (e.g., "Palabra clave", "Identificador", "Número"). */
    private String tipo;

    /** Valor textual del token (e.g., "if", "variable1", "42"). */
    private String valor;

    /**
     * Construye un nuevo token con el tipo y valor especificados.
     *
     * @param tipo  Categoría léxica del token.
     * @param valor Contenido textual del token.
     */
    public Token(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    /**
     * Devuelve una representación en cadena del token, combinando su tipo y valor.
     * Este método es útil para mostrar los tokens reconocidos de forma legible.
     *
     * @return Una cadena con el formato "tipo: valor".
     */
    @Override
    public String toString() {
        return tipo + ": " + valor;
    }
}
