package modelo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del analizador léxico Bash.
 * Permite ingresar cadenas de código, analizarlas léxicamente y visualizar los tokens resultantes.
 */
public class RegexAutomataGeneratorV2 extends JFrame {

    // Componentes de la GUI
    private JTextField cadenaField;                  // Campo de entrada para ingresar la cadena de texto
    private DefaultListModel<String> cadenasModel;   // Modelo para almacenar las cadenas ingresadas
    private DefaultListModel<String> resultadosModel;// Modelo para mostrar los resultados del análisis
    private JList<String> listaCadenas;              // Lista visual para mostrar cadenas ingresadas
    private JList<String> listaResultados;           // Lista visual para mostrar los tokens generados

    /**
     * Constructor de la interfaz gráfica y configuración inicial.
     */
    public RegexAutomataGeneratorV2() {
        setTitle("Analizador Léxico Bash"); // Título de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra el programa al cerrar la ventana
        setSize(600, 700); // Tamaño de la ventana
        setLayout(new FlowLayout()); // Usa un diseño simple de flujo para los componentes

        // Inicialización de componentes
        cadenaField = new JTextField(40); // Campo de texto para ingresar la cadena Bash
        cadenasModel = new DefaultListModel<>();
        resultadosModel = new DefaultListModel<>();

        listaCadenas = new JList<>(cadenasModel);
        listaResultados = new JList<>(resultadosModel);

        // Etiqueta y campo para ingresar cadenas
        add(new JLabel("Cadena (línea Bash):"));
        add(cadenaField);

        // Botones para agregar, analizar y graficar
        JButton addBtn = new JButton("Agregar Cadena");
        JButton analizarBtn = new JButton("Analizar Léxicamente");
        JButton graficarBtn = new JButton("Generar Autómata (Demo)");

        // Acción del botón para agregar cadenas a la lista
        addBtn.addActionListener(e -> {
            String cadena = cadenaField.getText();
            if (!cadena.isEmpty()) {
                cadenasModel.addElement(cadena); // Agrega la cadena al modelo
                cadenaField.setText(""); // Limpia el campo de texto
            }
        });

        // Acción del botón para analizar léxicamente las cadenas
        analizarBtn.addActionListener(e -> analizarCadenas());

        // Acción del botón para generar el autómata de demostración
        graficarBtn.addActionListener(e -> generarDotYGraficar());

        // Agrega los botones a la interfaz
        add(addBtn);
        add(analizarBtn);
        add(graficarBtn);

        // Agrega listas para mostrar cadenas y resultados
        add(new JLabel("Cadenas:"));
        add(new JScrollPane(listaCadenas)).setPreferredSize(new Dimension(500, 100));

        add(new JLabel("Tokens reconocidos:"));
        add(new JScrollPane(listaResultados)).setPreferredSize(new Dimension(500, 300));

        setVisible(true); // Muestra la ventana
    }

    /**
     * Recorre todas las cadenas ingresadas, las divide en lexemas y las clasifica según autómatas específicos.
     */
    private void analizarCadenas() {
        resultadosModel.clear(); // Limpia resultados anteriores

        // Recorre cada cadena ingresada para analizarla
        for (int i = 0; i < cadenasModel.size(); i++) {
            String linea = cadenasModel.get(i);
            List<String> lexemas = dividirLinea(linea);
            int pos = 0;

            // Procesamiento de tokens en la cadena
            for (String lexema : lexemas) {
                TipoToken tipo = clasificar(lexema); // Clasifica el lexema utilizando los autómatas
                resultadosModel.addElement(new Token(tipo.toString(), lexema, pos).toString()); // Agrega el token reconocido al modelo
                pos++;
            }
            resultadosModel.addElement("────────────"); // Separador visual entre líneas analizadas
        }
    }

    /**
     * Divide una línea en tokens utilizando reglas básicas de Bash.
     * @param linea La línea de código Bash a dividir.
     * @return Lista de tokens (lexemas) extraídos.
     */
    private List<String> dividirLinea(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean dentroCadena = false;
        char delimitadorCadena = ' ';

        // Lista de operadores múltiples (de comparación y lógicos principalmente)
        String[] operadoresMultiples = {
                "==", "!=", "<=", ">=", "-eq", "-ne", "-lt", "-le", "-gt", "-ge", "&&", "||", "**", "++", "--"
        };

        // Caracteres que pueden ser operadores unarios (como '!' o '=' solos)
        String caracteresSeparables = "!&|=<>+-*/%;,(){}[]";

        int i = 0;
        while (i < linea.length()) {
            char c = linea.charAt(i);

            // Dentro de una cadena (comillas simples o dobles)
            if (dentroCadena) {
                buffer.append(c);
                if (c == delimitadorCadena) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                    dentroCadena = false;
                }
                i++;
                continue;
            }

            // Inicia cadena
            if (c == '"' || c == '\'') {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                dentroCadena = true;
                delimitadorCadena = c;
                buffer.append(c);
                i++;
                continue;
            }

            // Espacio: cerrar token actual
            if (Character.isWhitespace(c)) {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                i++;
                continue;
            }

            // Verifica si hay un operador múltiple en esta posición
            boolean operadorDetectado = false;
            for (String op : operadoresMultiples) {
                int len = op.length();
                if (i + len <= linea.length()) {
                    String posible = linea.substring(i, i + len);
                    if (posible.equals(op)) {
                        if (buffer.length() > 0) {
                            tokens.add(buffer.toString());
                            buffer.setLength(0);
                        }
                        tokens.add(posible);
                        i += len;
                        operadorDetectado = true;
                        break;
                    }
                }
            }
            if (operadorDetectado) continue;

            // Si encontramos un operador unario
            if (caracteresSeparables.indexOf(c) != -1) {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            // Agrega el carácter al buffer
            buffer.append(c);
            i++;

            // Si el siguiente carácter es separador, cierra el token actual
            if (i < linea.length()) {
                char siguiente = linea.charAt(i);
                if (Character.isWhitespace(siguiente) || caracteresSeparables.indexOf(siguiente) != -1) {
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                }
            }
        }

        // Si quedó algo pendiente en el buffer
        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
        }

        return tokens;
    }

    /**
     * Clasifica un lexema en un tipo de token utilizando autómatas específicos.
     * @param lexema El lexema a clasificar.
     * @return El tipo de token reconocido.
     */
    private TipoToken clasificar(String lexema) {
        if (AutomataOperadorComparacion.reconocer(lexema)) return TipoToken.COMPARISON_OPERATOR;
        if (AutomataPalabraClave.reconocer(lexema)) return TipoToken.KEYWORD;
        if (AutomataNumeroNatural.reconocer(lexema)) return TipoToken.NATURAL_NUMBER;
        if (AutomataEnteroConSigno.reconocer(lexema)) return TipoToken.SIGNED_INTEGER;
        if (AutomataNumeroReal.reconocer(lexema)) return TipoToken.REAL_NUMBER;
        if (AutomataOperadorLogico.reconocer(lexema)) return TipoToken.LOGICAL_OPERATOR;
        if (AutomataOperadorAritmetico.reconocer(lexema)) return TipoToken.ARITHMETIC_OPERATOR;
        if (AutomataAsignacion.reconocer(lexema)) return TipoToken.ASSIGNMENT_OPERATOR;
        if (AutomataIncremento.reconocer(lexema)) return TipoToken.INCREMENT_DECREMENT_OPERATOR;
        if (AutomataIdentificador.reconocer(lexema)) return TipoToken.IDENTIFIER;
        if (lexema.equals("(")) return TipoToken.OPEN_PARENTHESIS;
        if (lexema.equals(")")) return TipoToken.CLOSE_PARENTHESIS;
        if (lexema.startsWith("\"") || lexema.startsWith("'")) return TipoToken.STRING_LITERAL;
        if (lexema.startsWith("#")) return TipoToken.LINE_COMMENT;
        return TipoToken.UNKNOWN_TOKEN;
    }

    /**
     * Genera un archivo .dot de ejemplo y lo convierte en imagen PNG con Graphviz.
     * Este método es demostrativo, no está vinculado al análisis real.
     */
    private void generarDotYGraficar() {
        // Código DOT estático para un autómata simple
        String dot = "digraph automata {\n" +
                "    rankdir=LR;\n" +
                "    node [shape = doublecircle]; qf;\n" +
                "    node [shape = circle];\n" +
                "    q0 -> q1 [ label = \"token\" ];\n" +
                "    q1 -> qf [ label = \"ε\" ];\n" +
                "}";

        try {
            // Guarda el archivo .dot
            File dotFile = new File("automata.dot");
            FileWriter writer = new FileWriter(dotFile);
            writer.write(dot);
            writer.close();

            // Llama a Graphviz para generar el PNG a partir del archivo .dot
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "automata.dot", "-o", "automata.png");
            Process process = pb.start();
            int exitCode = process.waitFor(); // Espera a que termine

            if (exitCode == 0) {
                JOptionPane.showMessageDialog(this, "Autómata generado exitosamente como 'automata.png'.");
                Desktop.getDesktop().open(new File("automata.png")); // Abre la imagen resultante
            } else {
                mostrarError("Error al generar el autómata con Graphviz.");
            }

        } catch (IOException | InterruptedException e) {
            mostrarError("Error: " + e.getMessage()); // Manejo de errores
        }
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje de error.
     * @param mensaje El mensaje a mostrar.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Método principal. Inicia la aplicación.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegexAutomataGeneratorV2::new); // Lanza la GUI en el hilo de eventos de Swing
    }
}
