package com.nixcalcj.calculator;

import java.util.ArrayList;
import java.util.Stack;

public class BinaryTree {
    private String inputExpr;
    public BinaryTree(String inputEx){
        inputExpr = inputEx;
    }
    private Expression expr = new Expression(inputExpr);
    private ArrayList<Expression.Token> tokens = new ArrayList<>();

    public class Node {
        String data;
        Node left = new Node();
        Node right = new Node();
    }

    private Node NewNode(String newData){
        Node node = new Node();
        node.data = newData;
        node.left = null;
        node.right = null;
        return node;
    }
    private Node BuildExpressionTree(){
        expr.BuildPostfixString();
        tokens = expr.Tokenizer();
        Stack<Node> treeStack = new Stack<>();

        Node[] tmpArray = new Node[2];
        for (int i=0; i < tokens.size(); i++){
            if (tokens.get(i).chr == 'n') {
                treeStack.push(NewNode(tokens.get(i).str));
            }
            else if (tokens.get(i).chr == 'o'){
                tmpArray[1] = treeStack.peek();
                treeStack.pop();
                tmpArray[0] = treeStack.peek();
                treeStack.pop();

                treeStack.push(NewNode(tokens.get(i).str));
                treeStack.peek().left = tmpArray[0];
                treeStack.peek().right = tmpArray[1];
            }
        }
        return treeStack.peek();
    }
}
