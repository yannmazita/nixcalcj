package com.nixcalcj;
import com.nixcalcj.calculator.BinaryTree;

public class Main {
    public static void main(String[] args) {
        BinaryTree expression = new BinaryTree(args[0]);
        expression.ComputeAndDisplay();
    }
}