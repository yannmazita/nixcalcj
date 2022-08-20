package com.nixcalcj.calculator;
import java.util.ArrayList;

public class Expression {
    private String infixExpr;
    private StringBuffer postfixExpr = new StringBuffer();
    private StringBuffer tmpNumString = new StringBuffer();
    public boolean isIntegerOnly = true;
    
    private class Token{
        public String str;
        public char chr;
        public Token(String inputString, char inputChar){
            str = inputString;
            chr = inputChar;
        }
    }

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

    private void ClearNumber(){
        tmpNumString.delete(0, tmpNumString.length() - 1);
    }

    private boolean IsOperator(String inputString){
        if (inputString.equals("^") ||
                inputString.equals("/") ||
                inputString.equals("exp") ||
                inputString.equals("ln")){
            isIntegerOnly = false;
            return true;
        }

        return inputString.equals("+") || inputString.equals("-") || inputString.equals("*");
    }

    private boolean IsLeftAssociative(String oper){
        return oper.equals("-") || oper.equals("/") || oper.equals("+") || oper.equals("*");
    }

    private boolean IsUnaryMinus(String inputString, int curPos){
        return inputString.equals("-") && (curPos == 0 || infixExpr.charAt(curPos - 1) == '('
                || IsOperator(Character.toString(infixExpr.charAt(curPos - 1))));
    }

    private ArrayList<Token> Tokenizer(String inputExpr, String type){
        ArrayList<Token> tokens = new ArrayList<>();
        int jumpIdx = 0;
        for (int i = 0; i < inputExpr.length(); i++){
            if (i < jumpIdx && i != 0){
                continue;
            }
            if (IsOperator(Character.toString(inputExpr.charAt(i)))){
                if (IsUnaryMinus(Character.toString(inputExpr.charAt(i)), i)){
                    Token token = new Token(Character.toString(inputExpr.charAt(i)),'O');
                    tokens.add(token);
                }
                else{
                    Token token = new Token(Character.toString(inputExpr.charAt(i)),'o');
                    tokens.add(token);
                }
            }
            else if (inputExpr.charAt(i) == '(') {
                Token token = new Token(Character.toString(inputExpr.charAt(i)),'l');
                tokens.add(token);
            }

            else if (inputExpr.charAt(i) == ')') {
                Token token = new Token(Character.toString(inputExpr.charAt(i)),'r');
                tokens.add(token);
            }
            else if (Character.isDigit(inputExpr.charAt(i))){
                jumpIdx = StoreNumber(i, type);
                Token token = new Token(tmpNumString.toString(), 'n');
                tokens.add(token);
                ClearNumber();
            }
        }
        return tokens;
    }
}
