package salchichon_script;

import java.io.FileReader;
import java_cup.runtime.Symbol;

public class Main {

    public static void main(String[] args) {
        generarCompilador();
        probarLexer("salchichon_script/ejemplo código 1.sintactico.base 1.scc");
        probarParser("salchichon_script/ejemplo código 1.sintactico.base 1.scc");

    }

    private static void generarCompilador() {
        try {
            String ruta = "salchichon_script/";
            String opcFlex[] = { ruta + "lexer.flex", "-d", ruta };
            jflex.Main.generate(opcFlex);
            String opcCUP[] = { "-destdir", ruta, "-parser", "Parser", ruta + "parser.cup" };
            java_cup.Main.main(opcCUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void probarLexer(String archivo) {
        try {
            Lexer scan = new Lexer(new FileReader(archivo));
            Symbol s;
            
            scan.closeWriter();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void probarParser(String archivo) {
        try {
            Lexer lexer = new Lexer(new FileReader(archivo));
            Parser parser = new Parser(lexer);

            parser.parse(); 
            lexer.closeWriter();

            if (parser.tieneErrores) {
                System.out.println("\u001B[33m Análisis sintáctico completado con errores.\u001B[0m");
            } else {
                System.out.println("Análisis sintáctico completado sin errores.");
            }
        } catch (Exception e) {
            System.err.println("Error durante el análisis sintáctico:");
            e.printStackTrace();
        }
    }

}