// Expresiones
// sealed interface y permits en records.
// Los records son buenos candidatos para usarlos en permits porque son final.

//package com.example.evaluar;

sealed interface Expr
        //permits ConstantExpr, PlusExpr, TimesExpr, NegExpr, MinusExpr, DivideExpr
        {
    int eval();
}

record MinusExpr(Expr a, Expr b) implements Expr {
    public int eval() { return a.eval() - b.eval(); }
}

record DivideExpr(Expr a, Expr b) implements Expr {
    public int eval() { return a.eval() / b.eval(); }
}

record ConstantExpr(int i) implements Expr {
    public int eval() { return i(); }
}

record PlusExpr(Expr a, Expr b) implements Expr {
    public int eval() { return a.eval() + b.eval(); }
}

record TimesExpr(Expr a, Expr b) implements Expr {
    public int eval() { return a.eval() * b.eval(); }
}
