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
    // esto qur??? me imagino que es para manejar param de funciones, hagale chedrick no sea vago

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

        // ahora si ya la segunda pasada
        secondPass(lines);

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
                    // SIEMPRE: x es un puntero
                    if (!dataDecls.containsKey(left)) {
                        dataDecls.put(left, left + ": .word 0");
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

        out.println("# ================== SYSCALL");
        out.println("printStr:");
        out.println("    add $sp, $sp, -8");
        out.println("    sw $ra, 0($sp)");

        out.println("    li   $v0, 4");
        out.println("    syscall");

        out.println("    lw  $ra, 0($sp)");
        out.println("    addi $sp, $sp, 8");
        out.println("    jr $ra");
        out.println(".end printStr");
        out.println();

        out.println("printInt:");
        out.println("    add $sp, $sp, -8");
        out.println("    sw $ra, 0($sp)");
        out.println("    li   $v0, 1");
        out.println("    syscall");
        out.println("    lw  $ra, 0($sp)");
        out.println("    addi $sp, $sp, 8");
        out.println("    jr $ra");
        out.println(".end printInt");
        out.println();
        out.println("printFloat:");
        out.println("    add $sp, $sp, -8");
        out.println("    sw $ra, 0($sp)");
        out.println("    li   $v0, 2");
        out.println("    syscall");
        out.println("    lw  $ra, 0($sp)");
        out.println("    addi $sp, $sp, 8");
        out.println("    jr $ra");
        out.println(".end printFloat");
        out.println();

        out.println("readInt:");
        out.println("    add $sp, $sp, -8");
        out.println("    sw $ra, 0($sp)");
        out.println("    li   $v0, 5");
        out.println("    syscall");
        out.println("    lw  $ra, 0($sp)");
        out.println("    addi $sp, $sp, 8");
        out.println("    jr $ra");
        out.println(".end readInt");
        out.println();
        out.println("readFloat:");
        out.println("    add $sp, $sp, -8");
        out.println("    sw $ra, 0($sp)");
        out.println("    li   $v0, 6");
        out.println("    syscall");
        out.println("    lw  $ra, 0($sp)");
        out.println("    addi $sp, $sp, 8");
        out.println("    jr $ra");
        out.println(".end readFloat");
        out.println("# ================== FIN SYSCALL");
        out.println();
    }

    // La segunda pasada ya es para generar el codigo MIPS
    // Aqui es donde ya se hace la logica y traduccion
    private void secondPass(List<String> lines) {
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            // TO-DO: hay que hacer manejo de etiquetas, if-goto, parametros, llamadas a funciones.
            // DID IT: Aquí escribimos en el archivo de salida las lineas que no requieren de mucha lógica
            if (line.endsWith(":")) {  //Escribimos etiquetas
                out.println(line);
                continue;
            }

            if (line.startsWith("goto ")) {
                String label = line.substring(5).trim();
                out.println("    j " + label);//escribimos saltos a funciones
                continue;
            }

            if (line.startsWith("if ")) {
                // if f6 goto func1_decide1_caso1
                handleIfGoto(line); //llamamos a un handle para modularizar la lógica
                continue;
            }

            if (line.startsWith("param ")) {    //si lo que se esta ingresando es un parametro
                String arg = line.substring("param".length()).trim(); 
                paramQueue.add(arg); //agregamos el parametro a la lista de parametros
                continue;
            }

            if (line.startsWith("call ")) {
                handleCall(line);
                out.println("    la $a0, nl");
                out.println("    jal printStr");
                continue;
            }




            // Asignaciones
            if (line.contains("=")) {
                handleAssign(line);
                continue;
            }

            // Si hay algo que no deberia llegar
            System.out.println("quesesto " + line);
        }
    }
    private void handleCall(String line) {
        // Ej: "call printInt(), 1"
        int startName = line.indexOf("call") + 4;
        int par = line.indexOf('(', startName);
        String funcName = line.substring(startName, par).trim();

        switch (funcName) { // si algunas de las llamadas a funciones son funciones del sistema entonces
            case "printInt"://llamamos a los handle para escribir los llamados al sistema
                handleCallPrintInt(); 
                paramQueue.remove(0);
                break;
            case "printFloat":
                handleCallPrintFloat();
                paramQueue.remove(0);
                break;
            case "printStr":
                handleCallPrintStr();
                paramQueue.remove(0);
                break;
            case "readInt":
            case "readFloat":
                // Estas llamadas se manejan desde handleAssignFromCall
                // porque devuelven valor (x = call readFloat(), 1)
                out.println("    # ERROR: llamada a " + funcName +
                            " sin asignación no está soportada aún");
                break;
            case "return":
                // call return(), 1
                out.println("    jr  $ra");
                break;
            default:
                out.println("    jal " + funcName);
        }
    }
    private void handleCallPrintFloat() {
        if (paramQueue.isEmpty()) {
            out.println("    # ERROR: printFloat sin param");
            return;
        }
        String arg = paramQueue.get(0);
        out.println("    lwc1 $f12, " + arg);
        out.println("    jal  printFloat");
    }
    private void handleCallPrintInt() {
        if (paramQueue.isEmpty()) {
            out.println("    # ERROR: printInt sin param");
            return;
        }
        String arg = paramQueue.get(0); // sólo un parámetro
        if (arg.matches("-?\\d+")) {
            out.println("    li   $a0, " + arg);
        } else {
            out.println("    lw   $a0, " + arg);
        }
        out.println("    jal  printInt");
    }
    private void handleCallPrintStr() {
        if (paramQueue.isEmpty()) {
            out.println("    # ERROR: printStr sin param");
            return;
        }
        String arg = paramQueue.get(0);
        String decl = dataDecls.get(arg);
        if (decl != null && decl.contains(".asciiz")) {
            // Es una cadena literal: label directo
            out.println("    la   $a0, " + arg);
        } else {
            // Es una variable string/puntero:
            // str: .word 0, que en tiempo de ejecución tendrá la dirección del literal.
            out.println("    lw   $a0, " + arg);
        }
        out.println("    jal  printStr");
    }
    private void handleIfGoto(String line) {
        //esperamos encontrar algo como if condicion goto etiqueta
        //entonces hacemos un split
        String[] parts = line.split("\\s+");
        String cond = parts[1];
        String label = parts[3];
        //aqui cargamos la condición y despues saltamos si la condicion es falsa
        out.println("    lw   $t0, " + cond);
        out.println("    bne  $t0, $zero, " + label);  //escribimos en el archivo de salida
    }
    // Manejo de asignaciones y aritmetica
    private void handleAssign(String line) {

        int idxEq = line.indexOf('=');
        String left  = line.substring(0, idxEq).trim();
        String right = line.substring(idxEq + 1).trim();

        if (right.startsWith("call ")) {
            handleAssignFromCall(left, right);
            return;
        }

        if (right.matches("-?\\d+")) {
            out.println("    li   $t0, " + right);
            out.println("    sw   $t0, " + left);
            return;
        }

        if (right.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            String rightDecl = dataDecls.get(right);

            // Si el que vamos a asignar es asciiz, hay que copiar el address
            // no el contenido, la no lw
            if (rightDecl != null && rightDecl.contains(".asciiz")) {
                out.println("    la   $t0, " + right);   // t0 = &t5
                out.println("    sw   $t0, " + left);    // str = &t5
            } else {
                out.println("    lw   $t0, " + right);
                out.println("    sw   $t0, " + left);
            }
            return;
        }

        // manejo de operaciones de dos operandos
        // No maneja floats aun
        String[] parts = right.split("\\s+");
        if (parts.length == 3) {
            String op1 = parts[0];
            String op  = parts[1];
            String op2 = parts[2];

            loadIntLike("$t0", op1);
            loadIntLike("$t1", op2);

            switch (op) {
                case "+":
                    out.println("    add  $t2, $t0, $t1");
                    break;
                case "-":
                    out.println("    sub  $t2, $t0, $t1");
                    break;
                case "*":
                    out.println("    mul  $t2, $t0, $t1");
                    break;
                case "%":
                    out.println("    div  $t0, $t1");
                    out.println("    mfhi $t2");
                    break;
                case "//":
                    out.println("    div  $t0, $t1");
                    out.println("    mflo $t2");
                    break;
                case ">":
                    genRelational("sgt", left);
                    return;
                case "<":
                    genRelational("slt", left);
                    return;

                // TO-DO: completar con más operadores (>=, <=, ==, !=, &&, || y manejo de floats)
                // IMPORTANTE

                default:
                    out.println("    move $t2, $t0");
            }

            out.println("    sw   $t2, " + left);
            return;
        }

        // Caso de negacion
        if (right.startsWith("-")) {
            String expr = right.substring(1).trim();
            loadIntLike("$t0", expr);
            out.println("    sub  $t1, $zero, $t0");
            out.println("    sw   $t1, " + left);
            return;
        }

        if (right.startsWith("!")) {
            String expr = right.substring(1).trim();
            loadIntLike("$t0", expr);
            out.println("    sltu $t1, $t0, 1");
            out.println("    sw   $t1, " + left);
            return;
        }
    }

    // Carga un int o un literal en un registro
    private void loadIntLike(String reg, String operand) {
        if (operand.matches("-?\\d+")) {
            out.println("    li   " + reg + ", " + operand);
        } else {
            out.println("    lw   " + reg + ", " + operand);
        }
    }

    // Genera codigo para operaciones relacionales
    private void genRelational(String mipsInstr, String left) {
        out.println("    " + mipsInstr + "  $t2, $t0, $t1");
        out.println("    sw   $t2, " + left);
    }

    // Manejo de asignaciones desde llamadas a funciones
    private void handleAssignFromCall(String left, String right) {
        int startName = right.indexOf("call") + 4;
        int par = right.indexOf('(', startName);
        String funcName = right.substring(startName, par).trim();

        if (funcName.equals("readFloat")) {
            out.println("    jal  readFloat");
            // usamos store word coprocessor 1 para el float que queda 
            out.println("    swc1 $f0, " + left);
            return;
        }

        if (funcName.equals("readInt")) {
            out.println("    jal  readInt");
            out.println("    sw   $v0, " + left);
            return;
        }

        // funcion normal con retorno en $v0
        out.println("    jal  " + funcName);
        out.println("    sw   $v0, " + left);
    }
}
