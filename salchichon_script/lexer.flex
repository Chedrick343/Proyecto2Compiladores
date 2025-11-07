package salchichon_script;

import java_cup.runtime.Symbol;
    import java.io.PrintWriter;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.util.HashMap;

// cambio de seccion
%%


%cup            // Integracion de CUP
%class Lexer    // Nombre de la clase generada
%public
%unicode        // UTF-8
%line           // Guarda el numero de linea
%column         // Guarda el numero de columna
%state CADENA

%{  


    HashMap<String, String> tablaVariables = new HashMap<>();
    HashMap<String, String> tablaIdentificadores = new HashMap<>();
    HashMap<String, String> tablaConstantes = new HashMap<>();
    HashMap<String, String> tablaPalabrasReservadas = new HashMap<>();

    PrintWriter tokenWriter;

    {
        try {
            tokenWriter = new PrintWriter(new FileWriter("tokens.txt"));
        } catch (IOException e) {
            System.err.println("Error al abrir archivo tokens.txt");
        }
    }

    public void closeWriter() {
        if (tokenWriter != null) tokenWriter.close();
    }

    String cadena = "";

    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
// Lexemas - simbolos
PAR_A       =   "є"
PAR_C       =   "э"
BLO_A       =   "¿"
BLO_C       =   "?"
COMA        =   ","
FLECHA      =   "->"
FIN_E       =   "$"
ASIGN       =   "="
COR_A       =   "["
COR_C       =   "]"
COMILLA_SIMP =  \'

// Operadores
MAS         =   "\+"
MENOS       =   "\-"
POR         =   "\*"
DIV         =   "/"
DIV_ENT     =   "//"
MOD         =   "%"
POT         =   "\^"

// Operadores lógicos
AND         =   "@"
OR          =   "~"
NOT         =   "Σ"
// Operadores de incremento y decremento
INC         =   "\+\+"
DEC         =   "\-\-"

// Operadores relacionales
MAYOR       =   ">"
MENOR       =   "<"
MAY_IGU     =   ">="
MEN_IGU     =   "<="
DIF         =   "!="
IGUAL       =   "=="

// Palabras reservadas
INIT        =   "init"
INT1        =   "int"
CHAR1       =   "char"
STR1        =   "string"
FLOAT1      =   "float"
BOOL1       =   "bool"
LET         =   "let"
VOID        =   "void"
TRUE1       =   "true"
FALSE1      =   "false"
DECIDE      =   "decide"
OF          =   "of"
ELSE        =   "else"
END         =   "end"
LOOP        =   "loop"
EXIT        =   "exit"
WHEN        =   "when"
FOR1        =   "for"
STEP        =   "step"
TO          =   "to"
DOWNTO      =   "downto"
DO          =   "do"
RETURN      =   "return"
BREAK       =   "break"
IMPRIMIR    =   "output"
LEER        =   "input"

// funcion principal
MAIN        =   "principal"

// Regex
ENTERO          =   (0)|(-?[1-9][0-9]*)
DECIMALES       =   [0-9]*[1-9]+
FLOTANTE        =   (0\.0)|(-?(([1-9][0-9]*)\.({DECIMALES}|0))|(0\.{DECIMALES}))
CHAR            =   \' [^'\n] \'
CADENA_CHAR     =   \' [^'\n]* \'
ID              =   [_a-zA-ZñÑ][_0-9a-zA-ZñÑ]*

SPACE           =   [ \t\f\r]
ENTER           =   [\n]


// Comentarios
FIN_L   =   \r|\n|\r\n
INPUT1  =   [^\r\n]

COM_S   =   \| {INPUT1}* {FIN_L}?
COM_C   =   "¡" ([^!]|(\n|\r))* "!"

COMENTARIO = {COM_S} | {COM_C}

%%
<YYINITIAL> {CADENA_CHAR}
                        {
                            if(!tablaConstantes.containsKey(yytext())){
                                tablaConstantes.put(yytext(),"cadenaChars");
                            }
                            tokenWriter.println("Token: CADENA_CHAR\tLexema: " + yytext() + "\tTabla: tablaConstantes");
                            return new Symbol(sym.CADENA_CHAR, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {COMILLA_SIMP}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"comillaSimple");
                            }
                            tokenWriter.println("Token: COMILLA_SIMP\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.COMILLA_SIMP, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {COR_C}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"cierraCorchete");
                            }
                            tokenWriter.println("Token: COR_C\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.COR_C, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {COR_A}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"abreCorchete");
                            }
                            tokenWriter.println("Token: COR_A\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.COR_A, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {INIT}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaInit");
                            }
                            tokenWriter.println("Token: INIT\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.INIT, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {RETURN}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaReturn");
                            }
                            tokenWriter.println("Token: RETURN\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.RETURN, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {BREAK}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaBreak");
                            }
                            tokenWriter.println("Token: BREAK\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.BREAK, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {DO}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaDo");
                            }
                            tokenWriter.println("Token: DO\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DO, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {DOWNTO}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaDownto");
                            }
                            tokenWriter.println("Token: DOWNTO\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DOWNTO, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {TO}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaTo");
                            }
                            tokenWriter.println("Token: TO\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.TO, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {STEP}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaStep");
                            }
                            tokenWriter.println("Token: STEP\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.STEP, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {FOR1}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaFor");
                            }
                            tokenWriter.println("Token: FOR1\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.FOR1, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {WHEN}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaWhen");
                            }
                            tokenWriter.println("Token: WHEN\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.WHEN, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {EXIT}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaExit");
                            }
                            tokenWriter.println("Token: EXIT\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.EXIT, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {LOOP}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaLoop");
                            }
                            tokenWriter.println("Token: LOOP\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.LOOP, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {END}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaEnd");
                            }
                            tokenWriter.println("Token: END\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.END, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {TRUE1}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaTrue");
                            }
                            tokenWriter.println("Token: TRUE1\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.TRUE1, yyline, yycolumn,yytext()); 
                        }


<YYINITIAL> {DECIDE}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaDecide");
                            }
                            tokenWriter.println("Token: DECIDE\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DECIDE, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {ELSE}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaElse");
                            }
                            tokenWriter.println("Token: ELSE\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.ELSE, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {OF}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaOf");
                            }
                            tokenWriter.println("Token: OF\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.OF, yyline, yycolumn, yytext()); 
                        }


<YYINITIAL> {FALSE1}
                        {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(),"PalabraReservadaFalse");
                            }
                            tokenWriter.println("Token: FALSE1\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.FALSE1, yyline, yycolumn,yytext()); 
                        }


<YYINITIAL> {INT1}      { 
                            return new Symbol(sym.INT1, yyline, yycolumn,yytext()); }


<YYINITIAL> {CHAR1}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "TipoChar");
                            }
                            tokenWriter.println("Token: CHAR1\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.CHAR1, yyline, yycolumn,yytext()); }


<YYINITIAL> {STR1}      { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "TipoString");
                            }
                            tokenWriter.println("Token: STR1\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            
                            return new Symbol(sym.STR1, yyline, yycolumn,"string"); }


<YYINITIAL> {FLOAT1}    { 
                            return new Symbol(sym.FLOAT1, yyline, yycolumn,"float"); }


<YYINITIAL> {BOOL1}     { 

                            return new Symbol(sym.BOOL1, yyline, yycolumn,"booleano"); 
                             
                             }


<YYINITIAL> {LET}       { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "PalabraReservadaLet");
                            }
                            tokenWriter.println("Token: LET\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.LET, yyline, yycolumn,yytext()); }


<YYINITIAL> {VOID}      {

                            return new Symbol(sym.VOID, yyline, yycolumn, yytext()); }


<YYINITIAL> {PAR_A}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "abreParentesis");
                            }
                            tokenWriter.println("Token: PAR_A\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");

                            return new Symbol(sym.PAR_A, yyline, yycolumn,yytext()); }


<YYINITIAL> {PAR_C}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "cierraParentesis");
                            }
                            tokenWriter.println("Token: PAR_C\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
    
                            return new Symbol(sym.PAR_C, yyline, yycolumn,yytext()); }


<YYINITIAL> {BLO_A}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "abreBloque");
                            }
                            tokenWriter.println("Token: BLO_A\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.BLO_A, yyline, yycolumn,yytext()); }


<YYINITIAL> {BLO_C}     {   
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "cierraBloque");
                            }
                            tokenWriter.println("Token: BLO_C\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
    
                            return new Symbol(sym.BLO_C, yyline, yycolumn,yytext()); }


<YYINITIAL> {COMA}      { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "coma");
                            }
                            tokenWriter.println("Token: COMA\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
    
                            return new Symbol(sym.COMA, yyline, yycolumn,yytext()); }


<YYINITIAL> {FIN_E}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "finalExpresion");
                            }
                            tokenWriter.println("Token: FIN_E\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.FIN_E, yyline, yycolumn,yytext()); }


<YYINITIAL> {ASIGN}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "asignacion");
                            }
                            tokenWriter.println("Token: ASIGN\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
    
                            return new Symbol(sym.ASIGN, yyline, yycolumn,yytext()); }


<YYINITIAL> {ENTERO}    { 
                            if(!tablaConstantes.containsKey(yytext())){
                                tablaConstantes.put(yytext(), "constante");
                            }
                            tokenWriter.println("Token: ENTERO\tLexema: " + yytext() + "\tTabla: tablaConstantes");
                            return new Symbol(sym.ENTERO, yyline, yycolumn,yytext()); }


<YYINITIAL> {CHAR}      { 
                            if(!tablaConstantes.containsKey(yytext())){
                                tablaConstantes.put(yytext(), "constante");
                            }
                            tokenWriter.println("Token: CHAR\tLexema: " + yytext() + "\tTabla: tablaConstantes");
                            return new Symbol(sym.CHAR, yyline, yycolumn,yytext()); }


<YYINITIAL> {FLOTANTE}  { 
                            if(!tablaConstantes.containsKey(yytext())){
                                tablaConstantes.put(yytext(), "constante");
                            }
                            tokenWriter.println("Token: FLOTANTE\tLexema: " + yytext() + "\tTabla: tablaConstantes");
                            return new Symbol(sym.FLOTANTE, yyline, yycolumn,yytext()); }


<YYINITIAL> {FLECHA}   { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "flecha");
                            }
                            tokenWriter.println("Token: FLECHA\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
    
                            return new Symbol(sym.FLECHA, yyline, yycolumn,yytext()); }
                            

<YYINITIAL> {MAS}      { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorSuma");
                            }
                            tokenWriter.println("Token: MAS\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
    
                            return new Symbol(sym.MAS, yyline, yycolumn,yytext()); }


<YYINITIAL> {MENOS}    { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorResta");
                            }
                            tokenWriter.println("Token: MENOS\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MENOS, yyline, yycolumn,yytext()); }


<YYINITIAL> {POR}      { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorMultiplicacion");
                            }
                            tokenWriter.println("Token: POR\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.POR, yyline, yycolumn,yytext()); }


<YYINITIAL> {DIV}      { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorDivision");
                            }
                            tokenWriter.println("Token: DIV\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DIV, yyline, yycolumn,yytext()); }


<YYINITIAL> {DIV_ENT} { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorDivisionEntera");
                            }
                            tokenWriter.println("Token: DIV_ENT\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DIV_ENT, yyline, yycolumn,yytext()); }


<YYINITIAL> {MOD}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorModulo");
                            }
                            tokenWriter.println("Token: MOD\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MOD, yyline, yycolumn,yytext()); }


<YYINITIAL> {POT}     { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorPotencia");
                            }
                            tokenWriter.println("Token: POT\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.POT, yyline, yycolumn,yytext()); }       


<YYINITIAL> {AND}    { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorAnd");
                            }
                            tokenWriter.println("Token: AND\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.AND, yyline, yycolumn,yytext()); }


<YYINITIAL> {OR}     {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorOr");
                            }
                            tokenWriter.println("Token: OR\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.OR, yyline, yycolumn,yytext()); }


<YYINITIAL> {NOT}    { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorNot");
                            }
                            tokenWriter.println("Token: NOT\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.NOT, yyline, yycolumn,yytext()); }


<YYINITIAL> {INC}    { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorIncremento");
                            }
                            tokenWriter.println("Token: INC\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.INC, yyline, yycolumn,yytext()); }


<YYINITIAL> {DEC}    { 
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorDecremento");
                            }
                            tokenWriter.println("Token: DEC\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DEC, yyline, yycolumn,yytext()); }


<YYINITIAL> {MAYOR}      {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorMayor");
                            }
                            tokenWriter.println("Token: MAYOR\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MAYOR, yyline, yycolumn,yytext()); }


<YYINITIAL> {MENOR}      {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorMenor");
                            }
                            tokenWriter.println("Token: MENOR\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MENOR, yyline, yycolumn,yytext()); }


<YYINITIAL> {MAY_IGU}    {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorMayorIgual");
                            }
                            tokenWriter.println("Token: MAY_IGU\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MAY_IGU, yyline, yycolumn,yytext()); }


<YYINITIAL> {MEN_IGU}    {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorMenorIgual");
                            }
                            tokenWriter.println("Token: MEN_IGU\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MEN_IGU, yyline, yycolumn,yytext()); }


<YYINITIAL> {DIF}       {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorDiferente");
                            }
                            tokenWriter.println("Token: DIF\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.DIF, yyline, yycolumn,yytext()); }     


<YYINITIAL> {IGUAL}       {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "operadorIgual");
                            }
                            tokenWriter.println("Token: IGUAL\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.IGUAL, yyline, yycolumn,yytext()); }


<YYINITIAL> {IMPRIMIR} {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "PalabraReservadaPrint");
                            }
                            tokenWriter.println("Token: IMPRIMIR\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.IMPRIMIR, yyline, yycolumn, yytext()); }


<YYINITIAL> {LEER} {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "PalabraReservadaRead");
                            }
                            tokenWriter.println("Token: LEER\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.LEER, yyline, yycolumn, yytext()); }
                            

<YYINITIAL> {MAIN} {
                            if(!tablaPalabrasReservadas.containsKey(yytext())){
                                tablaPalabrasReservadas.put(yytext(), "funcionPrincipal");
                            }
                            tokenWriter.println("Token: MAIN\tLexema: " + yytext() + "\tTabla: tablaPalabrasReservadas");
                            return new Symbol(sym.MAIN, yyline, yycolumn, yytext()); }


// ID VA AL FINAL PARA RECONOCER PALABRAS RESERVADAS PRIMERO
<YYINITIAL> {ID}        { 
    
                            if(!tablaIdentificadores.containsKey(yytext())){
                                tablaIdentificadores.put(yytext(), "identificador");
                                tokenWriter.println("Token: ID\tLexema: " + yytext() + "\tTabla: tablaIdentificadores");
                                return new Symbol(sym.ID, yyline, yycolumn,yytext());
                            }
                            return new Symbol(sym.ID, yyline, yycolumn, yytext());
                            }

<YYINITIAL> [\"]        { yybegin(CADENA); cadena = ""; }
<YYINITIAL> {SPACE}     { /* ignorar */ }
<YYINITIAL> {ENTER}     { /* ignorar */ }
<YYINITIAL> {COMENTARIO} { /* ignorar */ }

<YYINITIAL> . {
        String errLex = "\u001B[31m Error léxico : '"+yytext()+"' en la línea: "+(yyline+1)+" y columna: "+(yycolumn+1) + "\u001B[0m ";
        System.out.println(errLex);
}

<CADENA> {
        [\"] { 
            String tmp = "\"" + cadena + "\""; 
            cadena = ""; 
            yybegin(YYINITIAL);
              
            if(!tablaConstantes.containsKey(yytext())){
                tablaConstantes.put(yytext(), "constante");
            }
            tokenWriter.println("Token: CADENA\tLexema: " + yytext() + "\tTabla: tablaConstantes");
            return new Symbol(sym.CADENA, yyline, yycolumn, tmp); 
        }
        [\n] {
            String tmp = cadena; 
            cadena = "";  
            System.out.println("Se esperaba cierre de cadena (\")."); 
            yybegin(YYINITIAL);
        }
        [^\"] { cadena += yytext(); }
}