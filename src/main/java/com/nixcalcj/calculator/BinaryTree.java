package com.nixcalcj.calculator;

import java.util.ArrayList;
import java.util.Stack;

public class BinaryTree {
    private Expression expr;

    public BinaryTree(String inputExpr){
        expr = new Expression(inputExpr);
    }
    private ArrayList<Expression.Token> tokens = new ArrayList<>();

    public class Node {
        String data;
        Node left = null;
        Node right = null;
    }

    /**
     * Create new node.
     * @param newData the data to be stored in the node.
     * @return reference to the new node.
     */
    private Node NewNode(String newData){
        Node node = new Node();
        node.data = newData;
        node.left = null;
        node.right = null;
        return node;
    }

    /**
     * Build (binary) expression tree.
     * This method builds an expression with each node being either an operator or an operand. Each node is made of
     * a reference to an instance of the Node class. Each Node instance has a 'data' string, a reference to the left
     * child and a reference to the right child (references to Node instances).
     * @return a reference to the root of the tree.
     */
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


    /**
     * Evaluate (binary) expression tree.
     * This method evaluates an expression tree by going through it in preorder.
     * @param node root node for the evaluation.
     * @return Number the result of the evaluation.
     */
    private Number EvaluateExpressionTree(Node node){
        if (node == null){
            return 0;
        }
        if (node.left == null && node.right == null){
            if (expr.isIntegerOnly){
                return Long.parseLong(node.data);
            }
            else {
                return Double.parseDouble(node.data);
            }
        }

        Number leftOperand = EvaluateExpressionTree(node.left);
        Number rightOperand = EvaluateExpressionTree(node.right);

        if (node.data.equals(Character.toString('+'))){
            if ((leftOperand instanceof Long) || ((rightOperand instanceof Long))){
                return (Long)leftOperand + (Long)rightOperand;
            }
            else {
                return (Double)leftOperand + (Double)rightOperand;
            }
        }
        if (node.data.equals(Character.toString('-'))){
            if ((leftOperand instanceof Long) || ((rightOperand instanceof Long))){
                return (Long)leftOperand - (Long)rightOperand;
            }
            else {
                return (Double)leftOperand - (Double)rightOperand;
            }
        }
        if (node.data.equals(Character.toString('*'))){
            if ((leftOperand instanceof Long) || ((rightOperand instanceof Long))){
                return (Long)leftOperand * (Long)rightOperand;
            }
            else {
                return (Double)leftOperand * (Double)rightOperand;
            }
        }
        if (node.data.equals(Character.toString('/'))){
            if (rightOperand.floatValue() == 0){
                System.out.println("Division by zero");
                return 0;
            }
            return (Double)leftOperand / (Double)rightOperand;
        }
        else if (node.data.equals(Character.toString('^'))){
            return Math.pow((Double)leftOperand, (Double)rightOperand);
        }
        return 0;
    }

    /**
     * Compute and send result to stdout.
     */
    public void ComputeAndDisplay(){
        Node tree = BuildExpressionTree();
        System.out.println(EvaluateExpressionTree(tree));
    }
}
