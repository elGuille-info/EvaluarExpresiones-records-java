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
    public static int evalua(String expresion) { //throws Exception {
        if (expresion == null || expresion.trim().equals(""))
            return -1;

        // Quitar todos los caracteres en blanco.
        expresion = expresion.replace(" ", "");

        int resultado = 0;

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

//    private static String ponerExpresionesEntreParentesis(String expresion) {
//        StringBuilder sb = new StringBuilder();
//        int posAnt = 0;
//        boolean seguir;
//        do {
//            // Buscar un operador y la posición.
//            var pos = posSiguienteOperador(expresion, posAnt);
//            // Si no hay, no seguir.
//            if (pos.posicion == -1) {
//                seguir = false;
//            } else {
//                String exp;
//                // Buscar el siguiente operador.
//                int posAct = pos.posicion + 1;
//                var pos2 = posSiguienteOperador(expresion, posAct);
//                if (pos2.posicion > -1) {
//                    exp = expresion.substring(posAnt, pos2.posicion);
//                } else {
//                    // La expresión estará entre posAnt y pos.posicion.
//                    exp = expresion.substring(posAnt, pos.posicion);
//                }
//                sb.append("(");
//                sb.append(exp);
//                sb.append(")");
//                // La nueva posición es la anterior más la cadena hallada.
//                posAnt = pos.posicion + exp.length();
//
////                // Buscar el siguiente signo o un paréntesis si no hay.
////                var pos2 = posSiguienteOperador(expresion, posAnt);
////                // Si no hay más operadores, esta es la cadena, no seguir buscando.
////                // Si hay más operadores, asignar la cadena y la posición donde seguir buscando.
////                if (pos2.posicion == -1) {
////                    seguir = false;
////                }
////                else {
////                    posAnt = pos2.posicion;
////                    seguir = true;
////                }
//
//                seguir = true;
//            }
//        } while (seguir);
//
//        return sb.toString();
//    }

//    /**
//     * Devuelve el siguiente operador (y la posición donde está) desde la posición indicada.
//     * @param expresion La expresión a evaluar.
//     * @return La posición y el operador en la expresión indicada, si no hay devuelve -1 en la posición.
//     */
//    private static TuplePair<Character, Integer> posSiguienteOperador(String expresion, int desde) {
//        var operador = buscarSiguienteOperador(expresion, desde);
//        // Posición y el signo de operación en la cadena a evaluar.
//        return operador;
//    }

    /**
     * Evalúa el contenido de las expresiones entre paréntesis.
     * @param expresion Expresión a evaluar (puede tener o no paréntesis).
     * @return La cadena sin los paréntesis y con lo que haya entre paréntesis ya evaluado.
     */
    private static String evaluaParentesis(String expresion) {//throws Exception {
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
                    expresion = expresion.replace("(" + exp + ")", String.valueOf(res));
                }
            }

            // Aquí llegará se haya evaluado o no la expresión entre paréntesis.
            //  Si había alguna expresión entre paréntesis, se habrá evaluado, pero puede que haya más.

            long startTime = System.nanoTime();
            String expTime = expresion;

            // Para no repetir la comprobación en caso de que no haya más paréntesis. (17/nov/22 14.10)
            //      Nota: Esta optimización no es estrictamente necesaria, pero...
            // Ya que, en el primer if se comprueba como mínimo si hay de apertura.
            // Si lo hubiera, después se revisará si hay de cierre.
            // Si no se cumplen los dos casos,
            //  en el if del bloque else, como mínimo, se vuelve a evaluar si hay de apertura.
            // Si no hay de apertura el primer if fallará y en el segundo solo se comprobará si hay de cierre.
            boolean hayApertura = expresion.indexOf('(') > -1;

            //long cierreIniTime = System.nanoTime();
            // En realidad el que se puede comprobar dos veces es si hay de apertura.
            //  Ya que si están los dos el segundo if no se evalúa.
            //  Y en caso de que se evalúe si no está el de cierre (porque sí está el de apertura),
            //      el segundo if se cumplirá si solo está el de apertura.
            boolean hayCierre = expresion.indexOf(')') > -1;

            //long cierreTime = System.nanoTime() - cierreIniTime;
            //System.err.println("  Tiempo en asignar hayCierre: " + cierreTime);

            // Si no hay más paréntesis, salir.
            // Por seguridad, comprobar que estén los dos paréntesis.
            // Si hay de apertura y cierre, continuar.
            //if (expresion.indexOf('(') > -1 && expresion.indexOf(')') > -1){
            //if (hayApertura && expresion.indexOf(')') > -1){
            if (hayApertura && hayCierre) {
                hay = true;
            } else {
                // Quitar los que hubiera (si no están emparejados).
                //if (expresion.indexOf('(') > -1 || expresion.indexOf(')') > -1){
                //if (hayApertura || expresion.indexOf(')') > -1){
                if (hayApertura || hayCierre) {
                    System.err.println("Los paréntesis no están emparejados:\n    " + expresion);
                    expresion = expresion.replace("(", "").replace(")", "");
                }
                hay = false;
            }
            long estimatedTime = System.nanoTime() - startTime;
            System.err.println("  Tiempo en evaluar " + expTime + " {" + expresion + "}: " + estimatedTime);
            //System.err.println("  Tiempo en evaluar " + expresion + ": " + estimatedTime);

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
        //int pos1, pos2;
        int posicion;
        char operador;
        int resultado = 0;
        TuplePair<Character, Integer> donde;

        do {
            // Buscar la operación a realizar.
            donde = siguienteOperadorConPrecedencia(expresion);
            operador = donde.operador;
            posicion = donde.posicion;
            if (posicion == -1) {
                break;
            }

//            operador = buscarSiguienteOperador(expresion);
//            // Si no hay más operaciones, salir.
//            if (operador == '\u0000') {
//                break;
//            }
//            // Posición del signo de operación en la cadena a evaluar.
//            posicion = posicionOperador(expresion, operador);
//            // Si no se ha hallado, salir (esto no debería ocurrir)
//            if (posicion == -1) {
//                break;
//            }

            // Si la posición es cero es que delante no hay nada.
            if (posicion == 0) {
                System.err.println("La posición del operador es cero.");
                // No lanzar una excepción, devolver -1.
                return -1;
                //throw new Exception("Esto no debe ocurrir.");
            }

            ConstantExpr res1, res2;

            // Asignar todos los caracteres hasta el signo al primer operador.
            op1 = expresion.substring(0, posicion).trim();
            //var op11 = buscarNumeroAnterior(op1);
            var op11 = buscarUnNumero(op1, true);
            op1 = op11;
//            var op11 = buscarNumero(op1, true);
//            op1 = op11.operador;
//            pos1 = op11.posicion;
            res1 = new ConstantExpr(Integer.parseInt(op1));
            //res1 = new ConstantExpr(op1);

            // op2 tendrá el resto de la expresión.
            op2 = expresion.substring(posicion + 1).trim();
            // Buscar el número hasta el siguiente operador.
            //var op22 = buscarNumeroSiguiente(op2);
            var op22 = buscarUnNumero(op2, false);
            op2 = op22;
//            var op22 = buscarNumero(op2, false);
//            op2 = op22.operador;
//            pos2 = op22.posicion;
            res2 = new ConstantExpr(Integer.parseInt(op2));
            //res2 = new ConstantExpr(op2);

            resultado = switch (operador) {
                case '+' -> new PlusExpr(res1, res2).eval();
                case '-' -> new MinusExpr(res1, res2).eval();
                case '*' -> new TimesExpr(res1, res2).eval();
                case '/' -> new DivideExpr(res1, res2).eval();
                default -> 0;
            };
            var laOperacion = op1 + operador + op2;
            var elResultado = String.valueOf(resultado);

            // Cambiar por el resultado todas las operaciones como la que se ha realizado.
            // Esto fallará si la operación está en otra parte de la expresión y contiene otros dígitos:
            //  Si la operación es: 2*5 y está también 22*5 o 2*51, etc.
            //
            // Mejor solo cambiar esta expresión. (18/nov/22 00.20)
            var posOp = expresion.indexOf(laOperacion);

            String algo;
            if (posOp == 0) {
                algo = elResultado + expresion.substring(laOperacion.length());
            }
            else {
                algo = expresion.substring(0,  posOp) + elResultado + expresion.substring(posOp + laOperacion.length());
            }

            //expresion = expresion.replace(laOperacion, elResultado);
            expresion = algo;
        } while (hayOperador(expresion));

        return resultado;
    }

    /**
     * Devuelve la posición del carácter indicado en la cadena.
     *
     * @param expresion La cadena a comprobar.
     * @param signo     El carácter a buscar.
     * @return La posición o -1 si no se ha encontrado.
     */
    static int posicionOperador(String expresion, char signo) {
        var pos = expresion.indexOf(signo);
        return pos;
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

//    /**
//     * Busca el siguiente signo de operación sin un orden en especial.
//     * Con idea que se busque desde la posición indicada en desde.
//     * @param expresion La expresión a evaluar.
//     * @param desde     La posición desde la que se comprobará.
//     * @return Un objeto del tipo Tupla2<Character, Integer> con el valor -1 en posicion si no hay.
//     */
//    static TuplePair<Character, Integer> buscarSiguienteOperador(String expresion, int desde) {
//        Integer pos;
//        Character ch;
//        var a = expresion.toCharArray();
//
//        // Recorrer todos los caracteres de la expresión desde la posición indicada
//        //  hasta que se encuentre un operador.
//        for (int i = desde; i < a.length; i++) {
//            char c = a[i];
//            pos = losOperadores.indexOf(c);
//            if (pos > -1) {
//                ch = operadores[pos];
//                return new TuplePair<>(ch, i);
//            }
//        }
//
////        for (int i = 0; i < operadores.length; i++) {
////            pos = expresion.indexOf(operadores[i], desde);
////            if (pos > -1) {
////                ch = operadores[i];
////                return new TuplaPair<>(ch, pos);
////            }
////        }
//        return new TuplePair<>('\u0000', -1);
//    }

    /**
     * Busca el siguiente signo de operación (teniendo en cuenta la precedencia: * / + -).
     * @param expresion La expresión a evaluar.
     * @return Una tupla con el operador hallado y la posición en la expresión.
     *         Si no se ha hallado, la tupla contendrá '\u0000' como el operador y -1 como la posición.
     */
    static TuplePair<Character, Integer> siguienteOperadorConPrecedencia(String expresion) {
        for (int i = 0; i < operadores.length; i++) {
            int pos = expresion.indexOf(operadores[i]);
            if (pos > -1) {
                return new TuplePair<>(operadores[i], pos);
            }
        }
        return new TuplePair<>('\u0000', -1);
    }

//    /**
//     * Busca el siguiente signo de operación, con este orden: * / + -
//     * @param expresion La expresión a evaluar.
//     * @return El operador hallado o '\u0000' si no hay.
//     */
//    static char buscarSiguienteOperador(String expresion) {
//        for (int i = 0; i < operadores.length; i++) {
//            if (expresion.indexOf(operadores[i]) > -1) {
//                return operadores[i];
//            }
//        }
//        return '\u0000';
//    }

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
     * Buscar un número y la posición en la cadena.
     * @param expresion
     * @param elAnterior
     * @return
     */
    static TuplePair<String, Integer> buscarNumero(String expresion, boolean elAnterior) {
        StringBuilder sb = new StringBuilder();

        var a = expresion.toCharArray();
        int posInicio = -1;
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
                if (posInicio == -1) {
                    posInicio = i;
                }
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
        return new TuplePair<>(sb.toString().trim(), posInicio);
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
