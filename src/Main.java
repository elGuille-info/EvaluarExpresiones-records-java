// EvaluarExpresiones.  (16/nov/22 15.12)
// Evaluador de expresiones simples a partir del contenido de una cadena.
// Basado en el código de ejemplo de Prueba08 con sealed interface y records.

//package com.example.evaluar;

public class Main {
    public static void main(String[] args) {

        System.out.println("Evaluar expresiones simples de números enteros.");
        System.out.println();

        String expresion;

        // Esto se calculaba mal. (17/nov/22 19.14)
        // 1+2*5+3+4+22*5
        // ya que al evaluar 2*5 = 10, se convertía el 22*5 en 210
        //  Lo utiliza como 1+10+3+4+210 = 218
        expresion = "1+2*5+3+4+22*5";
        mostrarResultado(expresion);

        // No hay paréntesis de apertura, a ver cómo lo gestiona. (17/nov/22 13.59)
        //  OK: Lo ignora y muestra el error.
        //  Lo utiliza como 1+2*5+3+4 = 18
        expresion = "1+2)*5+3+4";
        mostrarResultado(expresion);

        // Con paréntesis de apertura sin el de cierre. (17/nov/22 15.50)
        //  Lo utiliza como 1+2*5+3+4 = 18
        expresion = "(1+2*5+3+4";
        mostrarResultado(expresion);

        // Los paréntesis no están emparejados: falta el de cierre.
        //  OK: Lo ignora y muestra el error.
        //  Lo utiliza como (1+2)*(5+3+4) = 36
        expresion = "((1+2)*(5+3+4)";
        mostrarResultado(expresion);

        expresion = "(1+2)*5+3+4";
        mostrarResultado(expresion);

        expresion = "((1+2)*5+3+4)";
        mostrarResultado(expresion);

        expresion = "(1+2)*(5+3)+4";
        mostrarResultado(expresion);

        expresion = "1+2*5+3+4";
        mostrarResultado(expresion);

        expresion = "1 + 2 + 3 + 4*5";
        mostrarResultado(expresion);

        expresion = "2*5 + 3*2 + 3 + 4*5 + 3*2";
        mostrarResultado(expresion);
    }

    private static void mostrarResultado(String expresion) {
        System.out.print(expresion + " = ");
        long startTime = System.nanoTime();
        //long iniTime = System.currentTimeMillis();
        var res = Evaluar.evalua(expresion);
        System.out.println(res);
        long elapsedTime = System.nanoTime() - startTime;
        //long elapsedTime = System.currentTimeMillis() - iniTime;
        System.out.printf("  Tiempo empleado: %,.2f ns/1.000\n", (elapsedTime / 1000.0));
    }
}