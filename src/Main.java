// EvaluarExpresiones.  (16/nov/22 15.12)
// Evaluador de expresiones simples a partir del contenido de una cadena.
// Basado en el código de ejemplo de Prueba08 con sealed interface y records.

//package com.example.evaluar;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Evaluar expresiones simples.");
        System.out.println();

        String expresion;
        int res;

        expresion = "((1+2)*(5+3+4)";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

        expresion = "(1+2)*5+3+4";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

        expresion = "((1+2)*5+3+4)";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

        expresion = "(1+2)*(5+3)+4";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

        expresion = "1+2*5+3+4";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

        expresion = "1 + 2 + 3 + 4*5";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

        expresion = "2*5 + 3*2 + 3 + 4*5 + 3*2";
        res = Evaluar.evalua(expresion);
        System.out.println(expresion + " = " + res);

    }
}