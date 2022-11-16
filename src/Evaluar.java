// Evaluar expresiones simples usando los record definidos en SealedRecord.
// Se permite *, /, +, - con este nivel de precedencia.
// Las expresiones entre paréntesis se evalúan primero.
//
//  op1 + op2 * op3 se evalúa como op1 + (op2*op3)
//  op1 * op2 + op3 se evalúa como (op1*op2) + op3


//package com.example.evaluar;

public final class Evaluar {
    public static int evalua(String expresion) throws Exception {
        if (expresion == null || expresion.trim().equals(""))
            return -1;
        // Quitar todos los caracteres en blanco.
        expresion = expresion.replace(" ", "");

        int resultado = 0;

        // Evaluar todas las expresiones entre paréntesis.
        var res = evaluaParentesis(expresion);
        // Si hay operadores, evaluarlo.
        if (hayOperador(res)) {
            resultado = evaluar(res);
        }
        else {
            // Si no hay operadores, es el resultado.
            resultado = Integer.parseInt(res);
        }

        return resultado;
    }

    private static String evaluaParentesis(String expresion) throws Exception {
        boolean hay = false;
        do {
            int ini = expresion.indexOf('(');
            if (ini > -1){
                int fin = expresion.indexOf(')', ini);
                if (fin > -1){
                    // Comprobar si hay otro de empiece antes del cierre
                    var ini2 = expresion.indexOf('(', ini+1);
                    if (ini2 > -1 && ini2 < fin){
                        //System.err.println(ini2);
                        ini = ini2;
                    }
                    // En Java es desde inicio inclusive hasta fin exclusive
                    var exp = expresion.substring(ini + 1, fin);
                    int res = evaluar(exp);
                    expresion = expresion.replace("("+ exp + ")", String.valueOf(res));
                }
            }
            // Si no hay más paréntesis, salir.
            // Por seguridad, comprobar que haya los dos paréntesis
            // Si hay de apertura y cierre, continuar.
            if (expresion.indexOf('(') > -1 && expresion.indexOf(')') > -1){
                hay = true;
            }
            else {
                // Quitar los que hubiera (por si no están emparejados)
                if (expresion.indexOf('(') > -1 || expresion.indexOf(')') > -1){
                    System.err.println("Los paréntesis no están emparejados:\n    " + expresion);
                    expresion = expresion.replace("(", "").replace(")", "");
                }
                hay = false;
            }
        } while (hay);

        return expresion;
    }

    private static int evaluar(String expresion) throws Exception {
        if (expresion == null || expresion.trim().equals(""))
            return 0;

        // Quitar todos los caracteres en blanco.
        expresion = expresion.replace(" ", "");

        // Evaluar la expresión indicada.

        String op1, op2;
        int posicion;
        char signo;
        int resultado = 0;

        do {
            // Buscar la operación a realizar.
            signo = buscarSiguienteSigno(expresion);
            // Si no hay más operaciones, salir.
            if (signo == '\u0000'){ break;}
            // Posición del signo en la cadena a evaluar.
            posicion = posSigno(expresion, signo);
            // Si no se ha hallado, salir (esto no debería ocurrir)
            if (posicion == -1) {
                break;}

            ConstantExpr res1, res2;

            // Si la posición es cero es que delante no hay nada
            if (posicion == 0) {
                System.err.println(posicion);
                throw new Exception("Esto no debe ocurrir.");
            }

            // Asignar todos los caracteres hasta el signo al primer operador
            op1 = expresion.substring(0, posicion).trim();
            var op11 = buscarNumeroAnterior(op1);
            op1=op11;
            res1 = new ConstantExpr(Integer.parseInt(op1));

            op2 = expresion.substring(posicion+1).trim();
            var op22 = buscarNumeroSiguiente(op2);
            op2 = op22;
            res2 = new ConstantExpr(Integer.parseInt(op2));

            resultado = switch (signo){
                case '+' -> new PlusExpr(res1, res2).eval();
                case '-' -> new MinusExpr(res1, res2).eval();
                case '*' -> new TimesExpr(res1, res2).eval();
                case '/' -> new DivideExpr(res1, res2).eval();
                default -> 0;
            };
            var operacion = op1 + signo + op2;
            var elResultado = String.valueOf(resultado);
            expresion = expresion.replace(operacion, elResultado);
        } while (hayOperador(expresion));

        return resultado;
    }

    /**
     * Comprueba si la cadena indicada tiene alguno de los operadores aceptados.
     * @param expresion La cadena a comprobar.
     * @return True si contiene algún operador, false en caso contrario.
     */
    static boolean hayOperador(String expresion){
        for (char c : signos){
            int p = expresion.indexOf(c);
            if (p > -1){
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve la posición del carácter indicado en la cadena.
     * @param expresion La cadena a comprobar.
     * @param signo El caracter a buscar.
     * @return La posición o -1 si no se ha encontrado.
     */
    static int posSigno(String expresion, char signo){
        var pos = expresion.indexOf(signo);
        return pos;
    }

    /**
     * Los operadores en el orden de precedencia.
     * Sin incluir los paréntesis que se procesan por separado.
     */
    //static final String operadores = "()*/+-";
    static final String operadores = "*/+-";

    /**
     * Array de tipo char con los operadores.
     */
    static final char[] signos = operadores.toCharArray();

    /**
     * Busca el siguiente signo de operación, con este orden: * / + -
     * @param expresion La expresión a evaluar.
     * @return El signo hallado o 0 ('\u0000') si no se halla.
     */
    static char buscarSiguienteSigno(String expresion){
        //var signos = operadores.toCharArray();
        for (int i = 0; i < signos.length; i++){
            int p = expresion.indexOf(signos[i]);
            if (p > -1){
                return signos[i];
            }
        }
        return '\u0000';
    }

    /**
     * Busca un número desde el final hasta el principio (o si encuentra un signo de operación)
     * @param expresion La expresión a evaluar.
     * @return Una cadena con el número hallado.
     */
    static String buscarNumeroAnterior(String expresion){
        StringBuilder sb = new StringBuilder();

        var a = expresion.toCharArray();
        int inicio = a.length-1;
        int fin = 0;

        for (int i = inicio; i >= fin; i--){
            if (operadores.indexOf(a[i]) == -1){
                sb.append(a[i]);
            }
            else {
                break;
            }
        }
        if (sb.length() > 1){
            sb.reverse();
        }
        return sb.toString().trim();
    }

    /**
     * Busca un número desde el principio hasta el final (o si encuentra un signo de operación)
     * @param expresion La expresión a evaluar.
     * @return Una cadena con el número hallado.
     */
    static String buscarNumeroSiguiente(String expresion){
        StringBuilder sb = new StringBuilder();

        var a = expresion.toCharArray();
        int inicio = 0;
        int fin = a.length-1;
        for (int i = inicio; i <= fin; i++){
            if (operadores.indexOf(a[i]) == -1){
                sb.append(a[i]);
            }
            else {
                break;
            }
        }
        return sb.toString().trim();
    }
}
