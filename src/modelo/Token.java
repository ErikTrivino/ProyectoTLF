package modelo;

// Clase auxiliar Token
class Token {
    String tipo;
    String valor;

    public Token(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return tipo + ": " + valor;
    }
}