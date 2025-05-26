package modelo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.regex.*;

/**
 * Ventana principal del analizador léxico Bash.
 * Permite ingresar cadenas de código, analizarlas léxicamente y visualizar los tokens resultantes.
 */
public class RegexAutomataGenerator extends JFrame {

    // Componentes de la GUI
    private JTextField cadenaField;                  // Campo de entrada para ingresar la cadena de texto
    private DefaultListModel<String> cadenasModel;   // Modelo para almacenar las cadenas ingresadas
    private DefaultListModel<String> resultadosModel;// Modelo para mostrar los resultados del análisis
    private JList<String> listaCadenas;              // Lista visual para mostrar cadenas ingresadas
    private JList<String> listaResultados;           // Lista visual para mostrar los tokens generados

    /**
     * Constructor de la interfaz gráfica y configuración inicial.
     */
    public RegexAutomataGenerator() {
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
     * Recorre todas las cadenas ingresadas, las divide en lexemas y las clasifica según expresiones regulares.
     */
    private void analizarCadenas() {
        resultadosModel.clear(); // Limpia resultados anteriores

        // Lista de patrones de expresión regular y sus respectivos tipos de token
        Pattern[] patrones = new Pattern[]{
                Pattern.compile("\\b(if|fi|then|else|for|while|do|done|echo)\\b"),  // Palabras clave
                Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"),  // Identificadores
                Pattern.compile("\\b\\d+(\\.\\d+)?\\b"),    // Números enteros o decimales
                Pattern.compile("(\"[^\"]*\"|'[^']*')"),    // Cadenas entre comillas simples o dobles
                Pattern.compile("(==|!=|&&|\\|\\||=|\\+|-|\\*|/|%)"), // Operadores
                Pattern.compile("#.*")                      // Comentarios (desde # hasta fin de línea)
        };

        String[] tipos = new String[]{
                "Palabra clave", "Identificador", "Número", "Cadena", "Operador", "Comentario"
        };

        // Recorre cada cadena ingresada para analizarla
        for (int i = 0; i < cadenasModel.size(); i++) {
            String linea = cadenasModel.get(i);
            String texto = linea.strip(); // Elimina espacios al inicio y final

            boolean encontrado;

            // Procesamiento de tokens en la cadena
            while (!texto.isEmpty()) {
                texto = texto.stripLeading(); // Elimina espacios al inicio
                encontrado = false;

                for (int j = 0; j < patrones.length; j++) {
                    Matcher matcher = patrones[j].matcher(texto);
                    if (matcher.find() && matcher.start() == 0) {
                        String token = matcher.group(); // Extrae el token
                        resultadosModel.addElement(new Token(tipos[j], token).toString()); // Agrega el token reconocido al modelo
                        texto = texto.substring(matcher.end()); // Continúa con el texto restante
                        encontrado = true;
                        break;
                    }
                }

                // Si no se encuentra un token válido
                if (!encontrado) {
                    resultadosModel.addElement("Error: token inválido en -> " + texto);
                    break; // Detiene el análisis de esta línea
                }
            }

            resultadosModel.addElement("────────────"); // Separador visual entre líneas analizadas
        }
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
        SwingUtilities.invokeLater(RegexAutomataGenerator::new); // Lanza la GUI en el hilo de eventos de Swing
    }
}
