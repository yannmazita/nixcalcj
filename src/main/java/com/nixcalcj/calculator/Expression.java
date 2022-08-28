package com.nixcalcj.calculator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Container for expressions input by user.
 */
class Expression {
    /** Infix expression input by user. */
    private String infixExpr;
    /** Postfix conversion of 'infixExpr'. */
    private StringBuffer postfixExpr = new StringBuffer();
    /** Temporary number stred in string form. */
    private StringBuffer tmpNumString = new StringBuffer();
    /** Whether operands or the operator need only integers. */
    public boolean isIntegerOnly = true;

    /**
     * Public token class used to make pairs of strings associated with their meaning.
     */
    public class Token{
        /** The token value. It can be a number, a digit, a parenthesis... */
        public String str;
        /** The token name. 'n': number/digit, 'l' or 'r': parenthesis, 'o': operator, 'O': unary operator. */
        public char chr;
        public Token(String inputString, char inputChar){
            str = inputString;
            chr = inputChar;
        }
    }

    /**
     * Constructor for Expression.
     * @param inputExpr the infix expression input by user.
     */
    public Expression(String inputExpr){
        infixExpr = inputExpr;
    }

    /**
     * Store number starting at specific position in the expression.
     * This method stores in 'tmpNumString' the number starting at a specific position.
     * That position and the return value allow to jump numbers in the expression when necessary.
     * @param pos starting position of the number in the expression.
     * @param type either "in" for infix or "post" for postfix.
     * @return end position + 1 of the number in the expression.
     */
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

    /**
     * Clear number currently stored.
     * This method simply clears the string 'tmpNumString'.
     */
    private void ClearNumber(){
        tmpNumString.delete(0, tmpNumString.length());
    }

    /**
     * Evaluate whether a string is an operator.
     * @param inputString the string to be evaluated.
     * @return true if string is an operator, false otherwise.
     */
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

    /**
     * Evaluate whether operator is left associative.
     * @param oper the operator.
     * @return true if left associativen false otherwise.
     */
    private boolean IsLeftAssociative(String oper){
        return oper.equals("-") || oper.equals("/") || oper.equals("+") || oper.equals("*");
    }

    /**
     * Evaluate whether operator is left associative.
     * @param oper the operator.
     * @param curPos the current position in the expression.
     * @return true if character is unary minus, false otherwise.
     */
    private boolean IsUnaryMinus(String oper, int curPos){
        return oper.equals("-") && (curPos == 0 || infixExpr.charAt(curPos - 1) == '('
                || IsOperator(Character.toString(infixExpr.charAt(curPos - 1))));
    }

    /**
     * Tokenize expression.
     * This method makes tokens out of an expression using the Token class. A pair consists of a token value
     * ('+', '1'...) and a token name ('o' for operator, 'n' for number...).
     * @param inputExpr the expression to tokenize.
     * @param type either "in" for infix or "post" for postfix.
     * @return ArrayList of tokens.
     */
    private ArrayList<Token> Tokenizer(String inputExpr, String type){
        ArrayList<Token> tokens = new ArrayList<>();
        int jumpIdx = 0;
        for (int i = 0; i < inputExpr.length(); i++){
            if (i < jumpIdx && i != 0){
                continue;
            }
            if (IsOperator(Character.toString(inputExpr.charAt(i)))){
                if (type.equals("in")){
                    if (IsUnaryMinus(Character.toString(inputExpr.charAt(i)), i)) {
                        Token token = new Token(Character.toString(inputExpr.charAt(i)), 'O');
                        tokens.add(token);
                    }
                    else {
                        Token token = new Token(Character.toString(inputExpr.charAt(i)), 'o');
                        tokens.add(token);
                    }
                }
                else {
                    Token token = new Token(Character.toString(inputExpr.charAt(i)), 'o');
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

    /**
     * Tokenize postfix expression.
     * Public version of Tokenizer. This method makes tokens out of the postfix expression using the Token class.
     * A pair consists of a token value ('+', '1'...) and a token name ('o' for operator, 'n' for number...).
     * @return ArrayList of tokens.
     */
    public ArrayList<Token> Tokenizer(){
        return Tokenizer(postfixExpr.toString(), "post");
    }

    /**
     * Evaluate precedence of operators.
     * @param operator1 the first operator.
     * @param operator2 the second operator.
     * @return 1 if operator1 has precedence, -1 if operator2, 0 if same.
     */
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

    /**
     * Build postfix mathematical expression from infix expression.
     * @return queue of the converted expression.
     */
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
                if (!(operatorStack.empty()) && operatorStack.peek().equals(Character.toString('('))){
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

    /**
     * Build postfix expression from infix expression.
     * BuildPostfixQueue() is used to build a postfix queue that is then converted to a string stored in 'postfixExpr'.
     */
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
