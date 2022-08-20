package com.nixcalcj.calculator;

public class Expression {
    private String infixExpr;
    private StringBuffer postfixExpr = new StringBuffer();
    private StringBuffer tmpNumString = new StringBuffer();
    public boolean isIntegerOnly = true;
    public Expression(String inputExpr){
        infixExpr = inputExpr;
    }

    private int StoreNumber(final int pos, String type){
        int i = pos;
        if (type.equals("in")){
            while (i < infixExpr.length() && (Character.isDigit(infixExpr.charAt(i)) || infixExpr.charAt(i) == '.')) {
                if (infixExpr.charAt(i) == '.') {
                    isIntegerOnly = false;
                }
                tmpNumString.append(infixExpr.charAt(i));
                i++;
            }
        } else if (type.equals("post")){
            while (i < postfixExpr.length() && (Character.isDigit(infixExpr.charAt(i)) || postfixExpr.charAt(i) == '.')){
                tmpNumString.append(postfixExpr.charAt(i));
                i++;
            }
        }
        return i;
    }
}
