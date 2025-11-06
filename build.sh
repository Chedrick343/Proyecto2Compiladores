#!/bin/bash
set -e

JFLEX_JAR="libs/jflex-full-1.9.1.jar"
CUP_JAR="libs/java-cup-11b.jar"
RUN_JAR="libs/java-cup-11b-runtime.jar"
SRC_DIR="salchichon_script"

echo "Limpiando archivos antiguos..."
rm -f "$SRC_DIR"/Lexer.java "$SRC_DIR"/Parser.java "$SRC_DIR"/Sym.java

# ===== Generar lexer =====
echo "Generando lexer..."
java -jar "$JFLEX_JAR" "$SRC_DIR/lexer.flex"

# ===== Generar parser =====
echo "Generando parser..."
java -jar "$CUP_JAR" -destdir "$SRC_DIR" -parser Parser -symbols sym "$SRC_DIR/parser.cup"

# ===== Compilar todo =====
echo "Compilando fuentes..."
javac -cp ".:$JFLEX_JAR:$CUP_JAR:$RUN_JAR" "$SRC_DIR"/*.java

echo "Compilación completa."

# ===== Ejecutar =====
echo "Ejecutando programa..."
java -cp ".:$JFLEX_JAR:$CUP_JAR:$RUN_JAR" salchichon_script.Main
