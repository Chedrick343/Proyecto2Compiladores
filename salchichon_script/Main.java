package salchichon_script;

import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java_cup.runtime.Symbol;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);

        JFileChooser chooser = new JFileChooser();
        String rutaActual = System.getProperty("user.dir");
        chooser.setCurrentDirectory(new File(rutaActual));
        chooser.setDialogTitle("Selecciona un archivo");

        int result = chooser.showOpenDialog(frame);
        File archivo;
        if (result == JFileChooser.APPROVE_OPTION) {
            archivo = chooser.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            System.out.println("Ruta seleccionada: " + archivo.getAbsolutePath());
            generarCompilador();
            probarLexer(ruta);
            probarParser(ruta);
        } else {
            System.out.println("Selección cancelada.");
        }

        frame.dispose();
        

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