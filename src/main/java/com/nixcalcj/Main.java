package com.nixcalcj;
import com.nixcalcj.calculator.BinaryTree;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.print("No expression input.\n");
        }
        else{
            BinaryTree expression = new BinaryTree(args[0]);
            expression.ComputeAndDisplay();
        }
    }
}