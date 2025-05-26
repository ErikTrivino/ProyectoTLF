package modelo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RegexAutomataGeneratorV2 extends JFrame {

    private JTextField cadenaField;
    private DefaultListModel<String> cadenasModel;
    private DefaultListModel<String> resultadosModel;
    private JList<String> listaCadenas;
    private JList<String> listaResultados;

    public  RegexAutomataGeneratorV2() {
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

        for (int i = 0; i < cadenasModel.size(); i++) {
            String linea = cadenasModel.get(i);
            List<String> lexemas = dividirLinea(linea);
            int pos = 0;

            for (String lexema : lexemas) {
                TipoToken tipo = clasificar(lexema);
                //resultadosModel.addElement(new Token(tipo.toString(), lexema.toString()).toString());
                resultadosModel.addElement(new Token(tipo.toString(), lexema, pos).toString());
                pos++;
            }
            resultadosModel.addElement("────────────");
        }
    }

    private List<String> dividirLinea(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean dentroCadena = false;
        char delimitadorCadena = ' ';

        // Lista de operadores de comparación válidos en bash
        String[] comparadores = {"-eq", "-ne", "-lt", "-le", "-gt", "-ge"};

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (dentroCadena) {
                buffer.append(c);
                if (c == delimitadorCadena) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                    dentroCadena = false;
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                dentroCadena = true;
                delimitadorCadena = c;
                buffer.append(c);
            } else if (Character.isWhitespace(c)) {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
            } else {
                buffer.append(c);

                // Verificar si lo que llevamos acumulado es un operador de comparación completo
                for (String op : comparadores) {
                    if (buffer.toString().equals(op)) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                        break;
                    }
                }

                // Si el siguiente carácter es espacio o separador, vaciar el buffer
                if (i + 1 == linea.length() || Character.isWhitespace(linea.charAt(i + 1)) ||
                        "[](){};,+-*/=%<>!&|".indexOf(linea.charAt(i + 1)) != -1) {
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                }
            }
        }

        if (buffer.length() > 0) tokens.add(buffer.toString());
        return tokens;
    }


    private TipoToken clasificar(String lexema) {
        if (AutomataOperadorComparacion.reconocer(lexema)) return TipoToken.COMPARISON_OPERATOR;
        if (AutomataPalabraClave.reconocer(lexema)) return TipoToken.KEYWORD;
        if (AutomataNumeroNatural.reconocer(lexema)) return TipoToken.NATURAL_NUMBER;
        if (AutomataEnteroConSigno.reconocer(lexema)) return TipoToken.SIGNED_INTEGER;
        if (AutomataNumeroReal.reconocer(lexema)) return TipoToken.REAL_NUMBER;
        //if (AutomataOperadorComparacion.reconocer(lexema)) return TipoToken.COMPARISON_OPERATOR;
        if (AutomataOperadorLogico.reconocer(lexema)) return TipoToken.LOGICAL_OPERATOR;
        if (AutomataOperadorAritmetico.reconocer(lexema)) return TipoToken.ARITHMETIC_OPERATOR;
        if (AutomataAsignacion.reconocer(lexema)) return TipoToken.ASSIGNMENT_OPERATOR;
        if (AutomataIncremento.reconocer(lexema)) return TipoToken.INCREMENT_DECREMENT_OPERATOR;
        if (AutomataIdentificador.reconocer(lexema)) return TipoToken.IDENTIFIER;
        if (lexema.equals("(")) return TipoToken.OPEN_PARENTHESIS;
        if (lexema.equals(")")) return TipoToken.CLOSE_PARENTHESIS;
        if (lexema.equals("{")) return TipoToken.OPEN_BRACE;
        if (lexema.equals("}")) return TipoToken.CLOSE_BRACE;
        if (lexema.equals("[")) return TipoToken.OPEN_BRACE;
        if (lexema.equals("]")) return TipoToken.CLOSE_BRACE;
        if (lexema.equals(";")) return TipoToken.STATEMENT_TERMINATOR;
        if (lexema.equals(",")) return TipoToken.SEPARATOR;
        if (AutomataCadena.reconocer(lexema)) return TipoToken.STRING_LITERAL;
        if (lexema.startsWith("\"") && !lexema.endsWith("\"")) return TipoToken.INCOMPLETE_STRING;
        if (AutomataComentario.reconocer(lexema)) return TipoToken.LINE_COMMENT;
        return TipoToken.UNKNOWN_TOKEN;
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
        SwingUtilities.invokeLater(RegexAutomataGeneratorV2::new);
    }
}
