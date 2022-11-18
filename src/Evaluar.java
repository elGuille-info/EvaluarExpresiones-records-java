// Evaluar expresiones simples usando los record definidos en SealedRecord.
// Se permite *, /, +, - con este nivel de precedencia.
// Las expresiones entre paréntesis se evalúan primero.
//
//  op1 + op2 * op3 se evalúa como op1 + (op2*op3)
//  op1 * op2 + op3 se evalúa como (op1*op2) + op3


//package com.example.evaluar;

public final class Evaluar {
    /**
     * Evalúa una expresión. Punto de entrada para evaluar expresiones.
     *
     * @param expresion La expresión a evaluar.
     * @return El valor entero de la expresión evaluada.
     */
    public static int evalua(String expresion) {
        if (expresion == null || expresion.trim().equals(""))
            return -1;

        // Quitar todos los caracteres en blanco.
        expresion = expresion.replace(" ", "");

        int resultado;

        // Primero se evalúan todas las expresiones entre paréntesis.
        var res = evaluaParentesis(expresion);

        // Si hay algún operador, evaluar la expresión.
        if (hayOperador(res)) {
            resultado = evaluar(res);
        } else {
            // Si no hay operadores, es que es el resultado.
            resultado = Integer.parseInt(res);
        }

        return resultado;
    }

    /**
     * Evalúa el contenido de las expresiones entre paréntesis.
     * @param expresion Expresión a evaluar (puede tener o no paréntesis).
     * @return La cadena sin los paréntesis y con lo que haya entre paréntesis ya evaluado.
     */
    private static String evaluaParentesis(String expresion) {
        boolean hay;
        do {
            // Posición del paréntesis de apertura.
            int ini = expresion.indexOf('(');
            // Si hay paréntesis de apertura...
            if (ini > -1) {
                // Posición del paréntesis de cierre.
                int fin = expresion.indexOf(')', ini);
                // Si hay paréntesis de cierre...
                if (fin > -1) {
                    // Comprobar si hay otro de empiece antes del cierre.
                    var ini2 = expresion.indexOf('(', ini + 1);
                    if (ini2 > -1 && ini2 < fin) {
                        // Hay uno de apertura antes del de cierre, evaluar desde ahí.
                        ini = ini2;
                    }
                    // En Java, substring, es desde inicio inclusive hasta fin exclusive.
                    // En .NET es desde inicio con la cantidad de caracteres del segundo parámetro.
                    var exp = expresion.substring(ini + 1, fin);
                    // Evaluar el resultado de la expresión.
                    int res = evaluar(exp);
                    // Asignar el resultado a la expresión.
                    //  Si hay varias expresiones (entre paréntesis) como la evaluada,
                    //      se reemplazarán por el resultado.
                    //
                    // Esto es seguro, ya que al estar entre paréntesis
                    //  las mismas expresiones tendrán los mismos resultados,
                    //  a diferencia de lo que ocurriría si no estuvieran entre paréntesis.
                    expresion = expresion.replace("(" + exp + ")", String.valueOf(res));
                }
            }

            // Aquí llegará se haya evaluado o no la expresión entre paréntesis.
            // Si había alguna expresión entre paréntesis, se habrá evaluado, pero puede que haya más.

            // Para no repetir la comprobación en caso de que no haya más paréntesis. (17/nov/22 14.10)
            //      Nota: Esta optimización no es estrictamente necesaria, pero...
            // Ya que, en el primer if se comprueba como mínimo si hay de apertura.
            // Si lo hubiera, después se revisará si hay de cierre.
            // Si no se cumplen los dos casos,
            //  en el if del bloque else, como mínimo, se vuelve a evaluar si hay de apertura.
            // Si no hay de apertura el primer if fallará y en el segundo solo se comprobará si hay de cierre.
            boolean hayApertura = expresion.indexOf('(') > -1;

            // Si no hay más paréntesis, salir.
            // Por seguridad, comprobar que estén los dos paréntesis.
            // Si hay de apertura y cierre, continuar.
            if (hayApertura && expresion.indexOf(')') > -1) {
                hay = true;
            } else {
                // Quitar los que hubiera (si no están emparejados).
                if (hayApertura || expresion.indexOf(')') > -1){
                    //System.err.println("Los paréntesis no están emparejados:\n    " + expresion);
                    expresion = expresion.replace("(", "").replace(")", "");
                }
                hay = false;
            }

            // Repetir si hay más expresiones entre paréntesis de apertura y cierre.
            //  Si hay paréntesis y no están emparejados, no se comprueba nada más.
        } while (hay);

        return expresion;
    }

    /**
     * Evalúa la expresión indicada quitando los espacios en blanco, (no hay expresiones entre paréntesis).
     * Se evalúan las operaciones (entre enteros) de suma (+), resta (-), multiplicación (*) y división (/).
     * @param expresion La expresión a evaluar.
     * @return Un valor entero con el resultado de la expresión evaluada.
     */
    private static int evaluar(String expresion) { //throws Exception {
        // Si la expresión es nula o una cadena vacía, se devuelve cero.
        if (expresion == null || expresion.trim().equals(""))
            return 0;

        // Quitar todos los caracteres en blanco.
        expresion = expresion.replace(" ", "");

        // Evaluar la expresión indicada.

        String op1, op2;
        int resultado = 0;
        TuplePair<Character, Integer> donde;

        do {
            // Buscar la operación a realizar.
            donde = siguienteOperadorConPrecedencia(expresion);
            if (donde.posicion == -1) {
                break;
            }

            // Si la posición es cero es que delante no hay nada.
            if (donde.posicion == 0) {
                System.err.println("La posición del operador es cero.");
                // No lanzar una excepción, devolver -1.
                return -1;
            }

            ConstantExpr res1, res2;

            // Asignar todos los caracteres hasta el signo al primer operador.
            op1 = expresion.substring(0, donde.posicion).trim();
            op1 = buscarUnNumero(op1, true);
            res1 = new ConstantExpr(Integer.parseInt(op1));

            // op2 tendrá el resto de la expresión.
            op2 = expresion.substring(donde.posicion + 1).trim();
            // Buscar el número hasta el siguiente operador.
            op2 = buscarUnNumero(op2, false);
            res2 = new ConstantExpr(Integer.parseInt(op2));

            resultado = switch (donde.operador) {
                case '+' -> new PlusExpr(res1, res2).eval();
                case '-' -> new MinusExpr(res1, res2).eval();
                case '*' -> new TimesExpr(res1, res2).eval();
                case '/' -> new DivideExpr(res1, res2).eval();
                default -> 0;
            };
            var laOperacion = op1 + donde.operador + op2;
            var elResultado = String.valueOf(resultado);

            // Cambiar por el resultado esta expresión. (18/nov/22 00.20)

            // La posición donde está esta operación (si hay más de una solo se busca la primera).
            var posOp = expresion.indexOf(laOperacion);

            // Si está al principio de la cadena asignar el resultado más lo que haya tras la operación.
            if (posOp == 0) {
                expresion = elResultado + expresion.substring(laOperacion.length());
            }
            // Si no está al principio,
            //  añadir lo que hubiera antes de esta operación, el resultado y lo que haya después de la operación.
            else {
                expresion = expresion.substring(0,  posOp) + elResultado + expresion.substring(posOp + laOperacion.length());
            }
        } while (hayOperador(expresion));

        return resultado;
    }

    /**
     * Los operadores en el orden de precedencia.
     * Sin incluir los paréntesis que se procesan por separado.
     */
    static final String losOperadores = "*/+-";

    /**
     * Array de tipo char con los operadores en el orden de precedencia.
     */
    static final char[] operadores = losOperadores.toCharArray();

    /**
     * Comprueba si la cadena indicada tiene alguno de los operadores aceptados.
     * @param expresion La cadena a comprobar.
     * @return True si contiene algún operador, false en caso contrario.
     */
    static boolean hayOperador(String expresion) {
        for (char c : operadores) {
            if (expresion.indexOf(c) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca el siguiente signo de operación (teniendo en cuenta la precedencia: * / + -).
     * @param expresion La expresión a evaluar.
     * @return Una tupla con el operador hallado y la posición en la expresión.
     *         Si no se ha hallado, la tupla contendrá '\u0000' como el operador y -1 como la posición.
     */
    static TuplePair<Character, Integer> siguienteOperadorConPrecedencia(String expresion) {
        for (char op : operadores) {
            int pos = expresion.indexOf(op);
            if (pos > -1) {
                return new TuplePair<>(op, pos);
            }
        }

        return new TuplePair<>('\u0000', -1);
    }

    /**
     * Busca el número anterior o siguiente.
     * @param expresion  La expresión a evaluar.
     * @param elAnterior True si se busca el número anterior (desde el final),
     *                   en otro caso se busca el número siguiente (desde el principio).
     * @return La cadena con el número hallado.
     */
    static String buscarUnNumero(String expresion, boolean elAnterior) {
        StringBuilder sb = new StringBuilder();

        var a = expresion.toCharArray();
        // Cuando se busca el anterior se hace desde el final,
        //  ya que la cadena tendrá un número precedido por un signo de operación o nada más.
        // Cuando se busca el siguiente, se hace desde el principio,
        //  porque la cadena tendrá el resto de la expresión a evaluar.
        int inicio = elAnterior ? a.length - 1 : 0;
        int fin = elAnterior ? 0 : a.length - 1;
        int i = inicio;

        while (elAnterior ? i >= fin : i <= fin) {
            if (losOperadores.indexOf(a[i]) == -1) {
                sb.append(a[i]);
            } else {
                break;
            }
            if (elAnterior) {
                i--;
            } else {
                i++;
            }
        }
        // Si se ha encontrado algo y se busca el número anterior,
        //  invertirlo ya que se habrá añadido desde el final.
        if (elAnterior && sb.length() > 1) {
            sb.reverse();
        }
        return sb.toString().trim();
    }

    /**
     * Tupla de dos valores para usar al buscar un operador y la posición del mismo.
     * @param operador Un valor del tipo T1.
     * @param posicion Un valor del tipo T2.
     * @param <T1> El tipo (por referencia) del primer parámetro.
     * @param <T2> El tipo (por referencia) del segundo parámetro.
     */
    record TuplePair<T1, T2>(T1 operador, T2 posicion) {
    }
}
