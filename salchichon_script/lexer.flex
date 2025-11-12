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

// Operadores
MAS         =   "+"
MENOS       =   "-"
POR         =   "*"
DIV         =   "/"
DIV_ENT     =   "//"
MOD         =   "%"
POT         =   "^"

// Operadores lógicos
AND         =   "@"
OR          =   "~"
NOT         =   "Σ"

// Operadores de incremento y decremento
INC         =   "++"
DEC         =   "--"

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
ENTEROPO        =   (0)|([1-9][0-9]*)
DECIMALES       =   [0-9]*[1-9]+
FLOTANTE        =   (0\.0)|(-?(([1-9][0-9]*)\.({DECIMALES}|0))|(0\.{DECIMALES}))
CHAR            =   \'[^'\n]\'
ID              =   [_a-zA-ZñÑ][_0-9a-zA-ZñÑ]*

SPACE           =   [ \t\f\r]
ENTER           =   [\n]

// Comentarios
FIN_L   =   \r|\n|\r\n
INPUT1  =   [^\r\n]

COM_S   =   \|{INPUT1}*{FIN_L}?
COM_C   =   "¡"([^!]|(\n|\r))*"!"

COMENTARIO = {COM_S}|{COM_C}

%%
<YYINITIAL> {INIT}          { return symbol(sym.INIT, yytext()); }
<YYINITIAL> {RETURN}        { return symbol(sym.RETURN, yytext()); }
<YYINITIAL> {BREAK}         { return symbol(sym.BREAK, yytext()); }
<YYINITIAL> {DO}            { return symbol(sym.DO, yytext()); }
<YYINITIAL> {DOWNTO}        { return symbol(sym.DOWNTO, yytext()); }
<YYINITIAL> {TO}            { return symbol(sym.TO, yytext()); }
<YYINITIAL> {STEP}          { return symbol(sym.STEP, yytext()); }
<YYINITIAL> {FOR1}          { return symbol(sym.FOR1, yytext()); }
<YYINITIAL> {WHEN}          { return symbol(sym.WHEN, yytext()); }
<YYINITIAL> {EXIT}          { return symbol(sym.EXIT, yytext()); }
<YYINITIAL> {LOOP}          { return symbol(sym.LOOP, yytext()); }
<YYINITIAL> {END}           { return symbol(sym.END, yytext()); }
<YYINITIAL> {TRUE1}         { return symbol(sym.TRUE1, "true"); }
<YYINITIAL> {FALSE1}        { return symbol(sym.FALSE1, "false"); }
<YYINITIAL> {DECIDE}        { return symbol(sym.DECIDE, yytext()); }
<YYINITIAL> {ELSE}          { return symbol(sym.ELSE, yytext()); }
<YYINITIAL> {OF}            { return symbol(sym.OF, yytext()); }

// TIPOS DE DATOS - CORREGIDOS
<YYINITIAL> {INT1}          { return symbol(sym.INT1, "int"); }
<YYINITIAL> {CHAR1}         { return symbol(sym.CHAR1, "char"); }
<YYINITIAL> {STR1}          { return symbol(sym.STR1, "string"); }
<YYINITIAL> {FLOAT1}        { return symbol(sym.FLOAT1, "float"); }
<YYINITIAL> {BOOL1}         { return symbol(sym.BOOL1, "boolean"); }

<YYINITIAL> {LET}           { return symbol(sym.LET, yytext()); }
<YYINITIAL> {VOID}          { return symbol(sym.VOID, yytext()); }
<YYINITIAL> {PAR_A}         { return symbol(sym.PAR_A, yytext()); }
<YYINITIAL> {PAR_C}         { return symbol(sym.PAR_C, yytext()); }
<YYINITIAL> {BLO_A}         { return symbol(sym.BLO_A, yytext()); }
<YYINITIAL> {BLO_C}         { return symbol(sym.BLO_C, yytext()); }
<YYINITIAL> {COMA}          { return symbol(sym.COMA, yytext()); }
<YYINITIAL> {FIN_E}         { return symbol(sym.FIN_E, yytext()); }
<YYINITIAL> {ASIGN}         { return symbol(sym.ASIGN, yytext()); }
<YYINITIAL> {FLECHA}        { return symbol(sym.FLECHA, yytext()); }

// OPERADORES
<YYINITIAL> {MAS}           { return symbol(sym.MAS, yytext()); }
<YYINITIAL> {MENOS}         { return symbol(sym.MENOS, yytext()); }
<YYINITIAL> {POR}           { return symbol(sym.POR, yytext()); }
<YYINITIAL> {DIV}           { return symbol(sym.DIV, yytext()); }
<YYINITIAL> {DIV_ENT}       { return symbol(sym.DIV_ENT, yytext()); }
<YYINITIAL> {MOD}           { return symbol(sym.MOD, yytext()); }
<YYINITIAL> {POT}           { return symbol(sym.POT, yytext()); }
<YYINITIAL> {AND}           { return symbol(sym.AND, yytext()); }
<YYINITIAL> {OR}            { return symbol(sym.OR, yytext()); }
<YYINITIAL> {NOT}           { return symbol(sym.NOT, yytext()); }
<YYINITIAL> {INC}           { return symbol(sym.INC, yytext()); }
<YYINITIAL> {DEC}           { return symbol(sym.DEC, yytext()); }
<YYINITIAL> {MAYOR}         { return symbol(sym.MAYOR, yytext()); }
<YYINITIAL> {MENOR}         { return symbol(sym.MENOR, yytext()); }
<YYINITIAL> {MAY_IGU}       { return symbol(sym.MAY_IGU, yytext()); }
<YYINITIAL> {MEN_IGU}       { return symbol(sym.MEN_IGU, yytext()); }
<YYINITIAL> {DIF}           { return symbol(sym.DIF, yytext()); }
<YYINITIAL> {IGUAL}         { return symbol(sym.IGUAL, yytext()); }

<YYINITIAL> {IMPRIMIR}      { return symbol(sym.IMPRIMIR, yytext()); }
<YYINITIAL> {LEER}          { return symbol(sym.LEER, yytext()); }
<YYINITIAL> {MAIN}          { return symbol(sym.MAIN, yytext()); }

// LITERALES Y IDENTIFICADORES - CORREGIDOS
<YYINITIAL> {ENTERO}        { 

    return symbol(sym.ENTERO, yytext()); 
}
<YYINITIAL> {ENTEROPO}        { 

    return symbol(sym.ENTEROPO, yytext()); 
}

<YYINITIAL> {FLOTANTE}      { 

    return symbol(sym.FLOTANTE, yytext()); 
}

<YYINITIAL> {CHAR}          { 

    return symbol(sym.CHAR, yytext()); 
}

<YYINITIAL> {ID}            { 

    return symbol(sym.ID, yytext());
}

// MANEJO DE CADENAS CON COMILLAS DOBLES - CORREGIDO
<YYINITIAL> \"              { 
    yybegin(CADENA); 
    cadena = ""; 

}

<YYINITIAL> {SPACE}         { /* ignorar */ }
<YYINITIAL> {ENTER}         { /* ignorar */ }
<YYINITIAL> {COMENTARIO}    { /* ignorar */ }


<YYINITIAL> {COR_A}         { 
    return symbol(sym.COR_A, yytext()); 
}

<YYINITIAL> {COR_C}         { 
    return symbol(sym.COR_C, yytext()); 
}

<YYINITIAL> . {
    String errLex = "\u001B[31m Error léxico : '"+yytext()+"' en la línea: "+(yyline+1)+" y columna: "+(yycolumn+1) + "\u001B[0m ";
    System.out.println(errLex);
}

<CADENA> {
    \" { 
        String tmp = cadena; 
        cadena = ""; 
        yybegin(YYINITIAL);
        System.out.println(">>> [LEXER] Cadena completa: \"" + tmp + "\"");
        return symbol(sym.CADENA, "\"" + tmp + "\""); 
    }
    \n {
        String tmp = cadena; 
        cadena = "";  
        System.out.println("Se esperaba cierre de cadena (\")."); 
        yybegin(YYINITIAL);
    }
    [^\"] { 
        cadena += yytext(); 
        //System.out.println(">>> [LEXER] Agregando a cadena: " + yytext());
    }
}