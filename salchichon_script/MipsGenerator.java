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
            if (right.startsWith("param[")) {
                if (!dataDecls.containsKey(left)) {
                    dataDecls.put(left, left + ": .word 0");
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
        boolean inFunction = false;
        int linesCounter = -1;
        String currentFunction = "";
        for (String raw : lines) {
            linesCounter++;
            String line = raw.trim();
            if (line.isEmpty()) continue;
            

            // TO-DO: hay que hacer manejo de etiquetas, if-goto, parametros, llamadas a funciones.
            // DID IT: Aquí escribimos en el archivo de salida las lineas que cumplen con lo que hay que hacer
            if (line.endsWith(":") && !line.startsWith("principal") && !line.contains("_end")) {
                inFunction = true;
                currentFunction = line.substring(0, line.length() - 1);
                out.println(line);
                
                // Prologo de función - espacio para guardar $ra y frame pointer
                out.println("    addiu $sp, $sp, -8");
                out.println("    sw    $ra, 4($sp)");
                out.println("    sw    $fp, 0($sp)");
                out.println("    move  $fp, $sp");
                
                // Reservar espacio para variables locales si es necesario
                // Y cargar parámetros a variables temporales
                handleFunctionPrologue(currentFunction);
                continue;
            }
            
            // Detectar fin de función
            if (line.contains("_end:")) {
                inFunction = false;
                out.println(line);
                continue;
            }
            if(line.startsWith("principal")){
                out.println(line);
                out.println("    add $sp, $sp, -8");
                out.println("    sw $ra, 0($sp)");
            }
                if (line.endsWith(":") && !line.equals("principal:")) {
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
                String lineaPrincipal = lines.get(linesCounter+2).trim();
                System.out.println(lineaPrincipal);
                if(lineaPrincipal.contains("principal_end")){
                    out.println("    lw  $ra, 0($sp)");
                    out.println("    addiu $sp, $sp, 8");
                    out.println("    jr $ra"); 
                }else{
                    handleCall(line);
                    out.println("    la $a0, nl");
                    out.println("    jal printStr");
                    continue;
                }
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
    private void handleFunctionPrologue(String funcName) {
        out.println("    # Cargando parámetros para " + funcName);
    }
    private void handleCall(String line) {
        // Ej: "call printInt(), 1"
        System.out.println(line);
        int startName = line.indexOf("call") + 4;
        int par = line.indexOf('(', startName);
        String funcName = line.substring(startName, par).trim();
        int cantidadParametros = Integer.parseInt(line.substring(line.lastIndexOf(',') + 1).trim());
        System.out.println("Parámetros: " + cantidadParametros);

        switch (funcName) {
            case "printInt":
                handleCallPrintInt();
                paramQueue.clear(); // Limpiamos la cola
                break;
            case "printFloat":
                handleCallPrintFloat();
                paramQueue.clear();
                break;
            case "printStr":
                handleCallPrintStr();
                paramQueue.clear();
                break;
            case "readInt":
            case "readFloat":
                out.println("    # ERROR: llamada a " + funcName +
                            " sin asignación no está soportada aún");
                paramQueue.clear();
                break;
            case "return":
                // call return(), 1
                handleFunctionReturn();
                paramQueue.clear();
                break;
            default:
                // Para funciones definidas por el usuario
                handleUserFunctionCall(funcName, cantidadParametros);
                paramQueue.clear();
        }
    }
    private void handleFunctionReturn() {
        out.println("    # Epílogo de función");
        out.println("    move  $sp, $fp");
        out.println("    lw    $ra, 4($sp)");
        out.println("    lw    $fp, 0($sp)");
        out.println("    addiu $sp, $sp, 8");
        out.println("    jr    $ra");
    }
    private void handleUserFunctionCall(String funcName, int paramCount) {
        // Para funciones con más de 4 parámetros, necesitamos ajustar
        // la pila antes de llamar
        
        if (paramCount > 4) {
            out.println("    # Reservando espacio para parámetros en pila");
            int stackSpace = (paramCount - 4) * 4;
            out.println("    addiu $sp, $sp, -" + stackSpace);
        }
        
        // Pasar parámetros
        for (int i = 0; i < Math.min(paramCount, 4); i++) {
            String arg = paramQueue.get(i);
            if (arg.matches("-?\\d+")) {
                out.println("    li   $a" + i + ", " + arg);
            } else {
                out.println("    lw   $a" + i + ", " + arg);
            }
        }
        
        // Parámetros adicionales en pila
        if (paramCount > 4) {
            for (int i = 4; i < paramCount; i++) {
                String arg = paramQueue.get(i);
                int offset = (i - 4) * 4;
                if (arg.matches("-?\\d+")) {
                    out.println("    li   $t0, " + arg);
                    out.println("    sw   $t0, " + offset + "($sp)");
                } else {
                    out.println("    lw   $t0, " + arg);
                    out.println("    sw   $t0, " + offset + "($sp)");
                }
            }
        }
        
        out.println("    jal " + funcName);
        
        // Limpiar parámetros de la pila si los hubo
        if (paramCount > 4) {
            int stackSpace = (paramCount - 4) * 4;
            out.println("    addiu $sp, $sp, " + stackSpace);
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
        String arg = paramQueue.get(0);
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
            out.println("    la   $a0, " + arg);
        } else {
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
    private void handleParamAssignment(String left, String right) {
        // Extraer índice del parámetro: param[0], param[1], etc.
        int startIdx = right.indexOf('[') + 1;
        int endIdx = right.indexOf(']');
        int paramIndex = Integer.parseInt(right.substring(startIdx, endIdx));
        
        // Los primeros 4 parámetros vienen en $a0-$a3
        if (paramIndex < 4) {
            out.println("    sw   $a" + paramIndex + ", " + left);
        } else {
            // Parámetros adicionales vienen en la pila
            // Cálculo: 8 (ra + fp) + (paramIndex - 4) * 4
            int offset = 8 + (paramIndex - 4) * 4;
            out.println("    lw   $t0, " + offset + "($fp)");
            out.println("    sw   $t0, " + left);
        }
    }
    // Manejo de asignaciones y aritmetica
    private void handleAssign(String line) {

        int idxEq = line.indexOf('=');
        String left  = line.substring(0, idxEq).trim();
        String right = line.substring(idxEq + 1).trim();

        if (right.startsWith("param[")) {
            handleParamAssignment(left, right);
            return;
        }

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
        int cantidadParametros = Integer.parseInt(right.substring(right.lastIndexOf(',') + 1).trim());

        if (funcName.equals("readFloat")) {
            out.println("    jal  readFloat");
            out.println("    swc1 $f0, " + left);
            return;
        }

        if (funcName.equals("readInt")) {
            out.println("    jal  readInt");
            out.println("    sw   $v0, " + left);
            return;
        }

        // Para funciones de usuario que devuelven valores
        if (!funcName.startsWith("print")) {
            // Primero manejar los parámetros
            int regParamCount = Math.min(paramQueue.size(), 4);
            
            for (int i = 0; i < regParamCount; i++) {
                String arg = paramQueue.get(i);
                if (arg.matches("-?\\d+")) {
                    out.println("    li   $a" + i + ", " + arg);
                } else {
                    out.println("    lw   $a" + i + ", " + arg);
                }
            }
            
            // Parámetros adicionales en pila
            if (paramQueue.size() > 4) {
                for (int i = 4; i < paramQueue.size(); i++) {
                    String arg = paramQueue.get(i);
                    out.println("    addi $sp, $sp, -4");
                    if (arg.matches("-?\\d+")) {
                        out.println("    li   $t0, " + arg);
                        out.println("    sw   $t0, 0($sp)");
                    } else {
                        out.println("    lw   $t0, " + arg);
                        out.println("    sw   $t0, 0($sp)");
                    }
                }
            }
        }
        
        out.println("    jal  " + funcName);
        
        // Limpiar pila si hubo parámetros adicionales
        if (paramQueue.size() > 4) {
            int stackParams = paramQueue.size() - 4;
            out.println("    addi $sp, $sp, " + (stackParams * 4));
        }
        
        // Guardar valor de retorno
        out.println("    sw   $v0, " + left);
        
        // Limpiar cola de parámetros
        paramQueue.clear();
    }
}
