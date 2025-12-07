package salchichon_script;
import java.io.*;
import java.nio.file.*;
import java.util.*;


public class MipsGenerator {
    private final Path input;
    private final Path output;
    private final PrintWriter out;


    private final Map<String, String> dataDecls = new LinkedHashMap<>();
    private final List<String> paramQueue = new ArrayList<>();

    private boolean isTemp(String name) {
        return name.matches("^[tf]\\d+$");
    }

    public MipsGenerator(String inputFile, String outputFile) throws IOException {
        this.input  = Paths.get(inputFile);
        this.output = Paths.get(outputFile);
        this.out    = new PrintWriter(Files.newBufferedWriter(output));
    }

    public void generate() throws IOException {
        List<String> lines = Files.readAllLines(input);

        //recorremos el archivo una primeera vez para ver qué es lo que va
        // en el data
        firstPass(lines);
        writeHeader();
        out.flush();
        out.close();
    }


    private void firstPass(List<String> lines) {
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.endsWith(":")) continue;          // etiquetas func1:, principal:, etc)
            if (line.startsWith("if ")) continue;      // if f6 goto ...
            if (line.startsWith("goto ")) continue;    // goto etiqueta
            if (line.startsWith("param ")) continue;   // param tXX
            if (line.startsWith("call ")) continue;    // call func(), n

            // Patrón básico:  x = algo
            int idxEq = line.indexOf('=');
            if (idxEq < 0) continue;

            String left  = line.substring(0, idxEq).trim();   // nombre del elemento, variable, temporal etc
            String right = line.substring(idxEq + 1).trim();  // valor de la variable, temporal, etc...

            // Si la parte de la derecha empieza con comillas y termina con comillas entonces estamos tratando con un string
            // por lo tanto lo creamos como asciiz
            if (right.startsWith("\"") && right.endsWith("\"")) {
                if (!dataDecls.containsKey(left)) {
                    String esc = escapeString(right.substring(1, right.length() - 1)); //acá quitamos las comillas
                    dataDecls.put(left, left + ": .asciiz \"" + esc + "\""); //aquí colocamos el nombre de la variable el tipo, ascii en este caso y el valor
                }
                continue;
            }

            //si empieza por comillas simples entonces estamos tratando con un char
            //sabemos que los chares son sólo de 1 byte
            if (right.startsWith("'") && right.endsWith("'") && right.length() >= 3) {
                if (!dataDecls.containsKey(left)) {
                    int code = right.charAt(1);
                    dataDecls.put(left, left + ": .word " + code); //iniciamos el char con word
                }
                continue;
            }

            // Booleanos true/false -> 1 / 0
            if (right.equals("true") || right.equals("false")) {
                if (!dataDecls.containsKey(left)) {
                    int val = right.equals("true") ? 1 : 0;
                    dataDecls.put(left, left + ": .word " + val); //hacemos lo mismo para los booleanos
                }
                continue;
            }

            //los flotantes los guardamos como un word pero antes los convertimos a hexadecimal para poder manejarlos en mips más fácilmentte
            if (isFloatLiteral(right)) {
                if (!dataDecls.containsKey(left)) {
                    String hex64 = toMips64Hex(right);
                    dataDecls.put(left, left + ": .word " + hex64);
                }
                continue;
            }

            if (right.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                String declR = dataDecls.get(right);
                if (declR != null && declR.contains(".asciiz")) {
                    String afterColon = declR.substring(declR.indexOf(':') + 1).trim();  // ".asciiz \"...\""
                    if (!isTemp(left)) {
                        dataDecls.put(left, left + ": " + afterColon);
                    }
                    continue; 
                }
            }

            // Enteros / temps / HP / etc -> .word si no existen
            if (!dataDecls.containsKey(left) && !left.startsWith("param")) {
                dataDecls.put(left, left + ": .word 0");
            }
        }
        //Aun no está muy claro como haremos la parte de los arreglos.
        if (usesHP(dataDecls)) {
            dataDecls.putIfAbsent("HP", "HP: .word 0");
        }
    }

    private boolean usesHP(Map<String,String> data) {
        for (String k : data.keySet()) {
            if (k.equals("HP")) return true;
        }
        return false;
    }

    private boolean isFloatLiteral(String s) {
        return s.matches("-?\\d+\\.\\d+");
    }

    /**
     * Convierte un literal double (como texto) a hex de 64 bits para MIPS.
     * Ej: "2.4" -> 0x4003333333333333
     */
    private String toMips64Hex(String literal) {
        double val = Double.parseDouble(literal);
        long bits = Double.doubleToRawLongBits(val);
        return String.format("0x%016X", bits);
    }

    private String escapeString(String s) {
        return s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\t", "\\t");
    }


    //Aquí basicamente escribimos en el data todo lo que guardamos anteriormente
    //Y además incluimos unos macros, (algunas facilitadas por el profe)
    //para facilitar el código que escribiremos en MIPS
    private void writeHeader() {

        out.println(".data");
        out.println("    nl: .asciiz \"\\n\"");

        for (String decl : dataDecls.values()) {
            out.println("    " + decl);
        }

        out.println();
        out.println(".text");
        out.println(".globl main");
        out.println();

        out.println("main:");
        out.println("    jal principal");
        out.println("    li   $v0, 10");
        out.println("    syscall");
        out.println();

        out.println("#SYSCALL");
        out.println("printStr:");
        out.println("    li   $v0, 4");
        out.println("    syscall");
        out.println("    jr $ra");
        out.println(".end printStr");
        out.println();
        out.println("printInt:");
        out.println("    li   $v0, 1");
        out.println("    syscall");
        out.println("    jr $ra");
        out.println(".end printInt");
        out.println();
        out.println("printFloat:");
        out.println("    li   $v0, 2");
        out.println("    syscall");
        out.println("    jr $ra");
        out.println(".end printFloat");
        out.println();
        out.println("readInt:");
        out.println("    li   $v0, 5");
        out.println("    syscall");
        out.println("    jr $ra");
        out.println(".end readInt");
        out.println();
        out.println("readFloat:");
        out.println("    li   $v0, 6");
        out.println("    syscall");
        out.println("    jr $ra");
        out.println(".end readFloat");
        out.println();
    }



}
