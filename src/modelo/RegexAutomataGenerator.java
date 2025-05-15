package modelo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.regex.*;

public class RegexAutomataGenerator extends JFrame {
/*
    private JTextField cadenaField;
    private DefaultListModel<String> cadenasModel;
    private DefaultListModel<String> resultadosModel;
    private JList<String> listaCadenas;
    private JList<String> listaResultados;

    public RegexAutomataGenerator() {
        setTitle("Analizador Léxico Bash");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 700);
        setLayout(new FlowLayout());

        cadenaField = new JTextField(40);
        cadenasModel = new DefaultListModel<>();
        resultadosModel = new DefaultListModel<>();

        listaCadenas = new JList<>(cadenasModel);
        listaResultados = new JList<>(resultadosModel);

        add(new JLabel("Cadena (línea Bash):"));
        add(cadenaField);

        JButton addBtn = new JButton("Agregar Cadena");
        JButton analizarBtn = new JButton("Analizar Léxicamente");
        JButton graficarBtn = new JButton("Generar Autómata (Demo)");

        addBtn.addActionListener(e -> {
            String cadena = cadenaField.getText();
            if (!cadena.isEmpty()) {
                cadenasModel.addElement(cadena);
                cadenaField.setText("");
            }
        });

        analizarBtn.addActionListener(e -> analizarCadenas());
        graficarBtn.addActionListener(e -> generarDotYGraficar());

        add(addBtn);
        add(analizarBtn);
        add(graficarBtn);

        add(new JLabel("Cadenas:"));
        add(new JScrollPane(listaCadenas)).setPreferredSize(new Dimension(500, 100));

        add(new JLabel("Tokens reconocidos:"));
        add(new JScrollPane(listaResultados)).setPreferredSize(new Dimension(500, 300));

        setVisible(true);
    }

    private void analizarCadenas() {
        resultadosModel.clear();

        Pattern[] patrones = new Pattern[]{
                Pattern.compile("\\b(if|fi|then|else|for|while|do|done|echo)\\b"), // KEYWORD
                Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]{0,9}"), // IDENTIFIER
                Pattern.compile("\\b\\d+\\b"), // NATURAL_NUMBER
                Pattern.compile("\\b\\d+\\.\\d+\\b"), // REAL_NUMBER
                Pattern.compile("(\\+|\\-|\\*|/|%|\\*\\*)"), // ARITHMETIC_OPERATOR
                Pattern.compile("(-eq|-ne|-lt|-le|-gt|-ge|==|!=|<|>)"), // COMPARISON_OPERATOR
                Pattern.compile("(&&|\\|\\||!)"), // LOGICAL_OPERATOR
                Pattern.compile("="), // ASSIGNMENT_OPERATOR
                Pattern.compile("(\\+\\+|--)", Pattern.LITERAL), // INCREMENT_DECREMENT_OPERATOR
                Pattern.compile("\\("), // OPEN_PARENTHESIS
                Pattern.compile("\\)"), // CLOSE_PARENTHESIS
                Pattern.compile("\\{"), // OPEN_BRACE
                Pattern.compile("\\}"), // CLOSE_BRACE
                Pattern.compile(";"), // STATEMENT_TERMINATOR
                Pattern.compile(","), // SEPARATOR
                Pattern.compile("(\"([^\\\"]|\\.)*\"|'([^\\']|\\.)*')"), // STRING_LITERAL
                Pattern.compile("#.*"), // LINE_COMMENT
                Pattern.compile(":\\s*'([\\s\\S]*?)'"), // BLOCK_COMMENT

                Pattern.compile("\\["), // OPEN_BRACE o SYMBOL
                Pattern.compile("\\]"), // CLOSE_BRACE o SYMBOL
                Pattern.compile("-gt|-lt|-eq|-ne|-ge|-le"), // COMPARISON_OPERATOR (ya está, pero tal vez mal ubicado)
                Pattern.compile("then"), // KEYWORD (falta incluirlo si no está bien capturado)

        };

        TipoToken[] categorias = new TipoToken[]{
                TipoToken.KEYWORD,
                TipoToken.IDENTIFIER,
                TipoToken.NATURAL_NUMBER,
                TipoToken.REAL_NUMBER,
                TipoToken.ARITHMETIC_OPERATOR,
                TipoToken.COMPARISON_OPERATOR,
                TipoToken.LOGICAL_OPERATOR,
                TipoToken.ASSIGNMENT_OPERATOR,
                TipoToken.INCREMENT_DECREMENT_OPERATOR,
                TipoToken.OPEN_PARENTHESIS,
                TipoToken.CLOSE_PARENTHESIS,
                TipoToken.OPEN_BRACE,
                TipoToken.CLOSE_BRACE,
                TipoToken.STATEMENT_TERMINATOR,
                TipoToken.SEPARATOR,
                TipoToken.STRING_LITERAL,
                TipoToken.LINE_COMMENT,
                TipoToken.BLOCK_COMMENT,
                TipoToken.CLOSE_BRACE,
                TipoToken.CLOSE_BRACE,
                TipoToken.COMPARISON_OPERATOR,
                TipoToken.KEYWORD
        };

        for (int i = 0; i < cadenasModel.size(); i++) {
            String texto = cadenasModel.get(i).strip();
            boolean encontrado;

            while (!texto.isEmpty()) {
                texto = texto.stripLeading();
                encontrado = false;

                for (int j = 0; j < patrones.length; j++) {
                    Matcher matcher = patrones[j].matcher(texto);
                    if (matcher.find() && matcher.start() == 0) {
                        String token = matcher.group();
                        resultadosModel.addElement(new Tokenv1(categorias[j].toString(), token).toString());
                        texto = texto.substring(matcher.end());
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    if (texto.startsWith("\"")) {
                        resultadosModel.addElement(new Tokenv1(TipoToken.UNTERMINATED_STRING.toString(), texto).toString());
                    } else {
                        resultadosModel.addElement(new Tokenv1(TipoToken.UNKNOWN_TOKEN.toString(), texto).toString());
                    }
                    break;
                }
            }
            resultadosModel.addElement("────────────");
        }
    }

    private void generarDotYGraficar() {
        String dot = "digraph automata {\n" +
                "    rankdir=LR;\n" +
                "    node [shape = doublecircle]; qf;\n" +
                "    node [shape = circle];\n" +
                "    q0 -> q1 [ label = \"token\" ];\n" +
                "    q1 -> qf [ label = \"ε\" ];\n" +
                "}";

        try {
            File dotFile = new File("automata.dot");
            FileWriter writer = new FileWriter(dotFile);
            writer.write(dot);
            writer.close();

            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "automata.dot", "-o", "automata.png");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                JOptionPane.showMessageDialog(this, "Autómata generado exitosamente como 'automata.png'.");
                Desktop.getDesktop().open(new File("automata.png"));
            } else {
                mostrarError("Error al generar el autómata con Graphviz.");
            }

        } catch (IOException | InterruptedException e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegexAutomataGenerator::new);
    }*/
}

// Enum con las categorías léxicas


// Clase Token
    class Tokenv1 {
    String tipo;
    String valor;

    public Tokenv1(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return tipo + ": " + valor;
    }
}

