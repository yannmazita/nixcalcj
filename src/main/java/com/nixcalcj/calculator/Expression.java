package com.nixcalcj.calculator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Expression {
    private String infixExpr;
    private StringBuffer postfixExpr = new StringBuffer();
    private StringBuffer tmpNumString = new StringBuffer();
    public boolean isIntegerOnly = true;
    
    public class Token{
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
            while (i < postfixExpr.length() && (Character.isDigit(postfixExpr.charAt(i)) || postfixExpr.charAt(i) == '.')){
                tmpNumString.append(postfixExpr.charAt(i));
                i++;
            }
        }
        return i;
    }

    private void ClearNumber(){
        tmpNumString.delete(0, tmpNumString.length());
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

    public ArrayList<Token> Tokenizer(){
        return Tokenizer(postfixExpr.toString(), "post");
    }

    private int GetPrecedence(String operator1, String operator2){
        char[] arr = {'^','1','*','2','/','2','+','3','-','3'};
        char[] arrGetPrecedence = new char[2];
        for (int i = 0; i < 10; i += 2){
            if (operator1.equals(Character.toString(arr[i]))){
                arrGetPrecedence[0] = arr[i + 1];
            } else if (operator2.equals(Character.toString(arr[i]))){
                arrGetPrecedence[1] = arr[i + 1];
            }
        }
        return Character.compare(arrGetPrecedence[1], arrGetPrecedence[0]);
    }

    private Queue<String> BuildPostfixQueue() {
        Queue<String> outputQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        ArrayList<Token> tokens = Tokenizer(infixExpr, "in");

        for (int i = 0; i < tokens.size(); i++){
            if (tokens.get(i).chr == 'n'){
                outputQueue.add(tokens.get(i).str);
            }
            else if (tokens.get(i).chr == 'o'){
                while( !(operatorStack.empty()) &&
                        (IsOperator(operatorStack.peek())) && !(operatorStack.peek().equals(Character.toString('('))) &&
                        (GetPrecedence(operatorStack.peek(), tokens.get(i).str)==1 ||
                                ((GetPrecedence(operatorStack.peek(), tokens.get(i).str)==0) &&
                                        IsLeftAssociative(tokens.get(i).str)))){
                    outputQueue.add(operatorStack.peek());
                    operatorStack.pop();
                }
                operatorStack.add(tokens.get(i).str);
            }
            else if (tokens.get(i).chr == 'O'){
                outputQueue.add(Character.toString('0'));
                while( !(operatorStack.empty()) &&
                        (IsOperator(operatorStack.peek())) && !(operatorStack.peek().equals(Character.toString('('))) &&
                        (GetPrecedence(operatorStack.peek(), tokens.get(i).str)==1 ||
                                ((GetPrecedence(operatorStack.peek(), tokens.get(i).str)==0) &&
                                        IsLeftAssociative(tokens.get(i).str)))){
                    outputQueue.add(operatorStack.peek());
                    operatorStack.pop();
                }
                operatorStack.add(tokens.get(i).str);
            }
            else if (tokens.get(i).chr == 'l') {
                operatorStack.add(tokens.get(i).str);
            }
            else if (tokens.get(i).chr == 'r'){
                while (!(operatorStack.empty()) && !(operatorStack.peek().equals(Character.toString('(')))){
                    outputQueue.add(operatorStack.peek());
                    operatorStack.pop();
                }
                if (!(operatorStack.empty()) && !(operatorStack.peek().equals(Character.toString('(')))){
                    operatorStack.pop();
                }
            }
        }
        while (!(operatorStack.empty())){
            if (!operatorStack.peek().equals(Character.toString('('))){
                outputQueue.add(operatorStack.peek());
                operatorStack.pop();
            }
        }
        return outputQueue;
    }

    public void BuildPostfixString(){
        Queue<String> que = BuildPostfixQueue();
        while (!(que.isEmpty())){
            postfixExpr.append(que.peek());
            postfixExpr.append(" ");
            que.remove();
        }
        if (postfixExpr.length() > 1){
            postfixExpr.deleteCharAt(postfixExpr.length() - 1);
        }
    }
}
