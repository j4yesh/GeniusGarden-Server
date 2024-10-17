package com.geniusgarden.server.Service;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class questionMaker {

    public static List<String>operand = Arrays.asList("0","1","2","3","4","5","6","7","8","9");
    public static List<String>operator = Arrays.asList("+","-","x");

    public Random random;

    questionMaker(){
        this.random = new Random();
    }

    public void makeQuestion(List<String> question, List<String>answer,int n){

        for(int i=0;i<n;i++){
            int operand1 = random.nextInt(10);
            int operand2 = random.nextInt(10);
            int operator1 = random.nextInt(3);
            String que="",ans="";
            Integer res;
            switch (operator.get(operator1)){
                case "+":
                    res = operand1 + operand2;
                    ans = res.toString();
                    break;
                case "-":
                    res=operand1-operand2;
                    ans = res.toString();
                    break;
                case "x":
                    res=operand1*operand2;
                    ans=res.toString();
                    break;
                default:
                    System.out.println("Something went wrong with operator");
            }
            que= operand.get(operand1) +" "+ operator.get(operator1) +" "+ operand.get(operand2) +" = ?";
            question.add(que);
            answer.add(ans);
        }
    }
}
