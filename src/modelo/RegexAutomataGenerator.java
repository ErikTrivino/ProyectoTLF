package modelo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.regex.*;

public class RegexAutomataGenerator extends JFrame {

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

        // Lista de patrones y sus tipos
        Pattern[] patrones = new Pattern[]{
                Pattern.compile("\\b(if|fi|then|else|for|while|do|done|echo)\\b"),  // Palabras clave
                Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"),  // Identificadores
                Pattern.compile("\\b\\d+(\\.\\d+)?\\b"),    // Números
                Pattern.compile("(\"[^\"]*\"|'[^']*')"),    // Cadenas
                Pattern.compile("(==|!=|&&|\\|\\||=|\\+|-|\\*|/|%)"), // Operadores
                Pattern.compile("#.*")                      // Comentarios
        };

        String[] tipos = new String[]{
                "Palabra clave", "Identificador", "Número", "Cadena", "Operador", "Comentario"
        };

        for (int i = 0; i < cadenasModel.size(); i++) {
            String linea = cadenasModel.get(i);
            String texto = linea.strip();

            boolean encontrado;

            while (!texto.isEmpty()) {
                texto = texto.stripLeading();
                encontrado = false;

                for (int j = 0; j < patrones.length; j++) {
                    Matcher matcher = patrones[j].matcher(texto);
                    if (matcher.find() && matcher.start() == 0) {
                        String token = matcher.group();
                        resultadosModel.addElement(new Token(tipos[j], token).toString());
                        texto = texto.substring(matcher.end());
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    resultadosModel.addElement("Error: token inválido en -> " + texto);
                    break;
                }
            }

            resultadosModel.addElement("────────────");
        }
    }

    private void generarDotYGraficar() {
        // Modo demostrativo — graficar solo una transición genérica
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
    }
}

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
