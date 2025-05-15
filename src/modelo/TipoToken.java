package modelo;

public enum TipoToken {
    // Palabras clave (if, else, fi, etc.)
    KEYWORD,

    // Identificadores: nombres de variables, funciones, etc.
    IDENTIFIER,

    // Números
    NATURAL_NUMBER,
    REAL_NUMBER,

    // Operadores
    ARITHMETIC_OPERATOR,
    COMPARISON_OPERATOR,
    LOGICAL_OPERATOR,
    ASSIGNMENT_OPERATOR,
    INCREMENT_DECREMENT_OPERATOR,

    // Delimitadores y símbolos
    OPEN_PARENTHESIS,
    CLOSE_PARENTHESIS,
    OPEN_BRACE,
    CLOSE_BRACE,
    STATEMENT_TERMINATOR,  // ;
    SEPARATOR,             // ,

    // Literales
    STRING_LITERAL,

    // Comentarios
    LINE_COMMENT,
    BLOCK_COMMENT,

    // Error léxico
    UNTERMINATED_STRING,
    UNKNOWN_TOKEN
}
