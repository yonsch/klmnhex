package Interpreter;

import java.util.ArrayList;


public class Lexer {

    public static ArrayList<Token> lex(String expression){
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);
            //System.out.println(i);
            //System.out.println(c);
            if (c=='"'){

                int endLabel = getLabelEnd(expression, i + 1);
                tokens.add(new Token("LABEL",expression.substring(i,endLabel+1)));
                i=endLabel+1;
            }
            else if (c=='='){
                tokens.add(new Token("ASSIGN","="));
                i++;
            }
            else if (c==':'){
                tokens.add(new Token("EQUALS",":"));
                i++;
            }
            else if (c=='['){
                tokens.add(new Token("BRACKET","["));
                i++;
            }
            else if (c==']'){
                tokens.add(new Token("BRACKET","]"));
                i++;
            }
            else if (c=='('){
                tokens.add(new Token("PARANTHESES","("));
                i++;
            }
            else if (c==')'){
                tokens.add(new Token("PARANTHESES",")"));
                i++;
            }
            else if (c=='@'){
                tokens.add(new Token("LOOP","@"));
                i++;
            }
            else if (c==';'){
                tokens.add(new Token("SEMICOLON",";"));
                i++;
            }
            else if (c==','){
                tokens.add(new Token("COMMA",","));
                i++;
            }
            else if (Character.isDigit(c)){
                int endNumber = getNumberEnd(expression,i+1);
                tokens.add(new Token("NUMBER",expression.substring(i,endNumber+1)));
                i=endNumber;
            }
            else if (c=='$'){
                int endLabel = getVariableEnd(expression, i + 1);
                tokens.add(new Token("VARIABLE",expression.substring(i,endLabel+1)));
                i=endLabel+1;
            }



            else{
                i++;
            }
        }
        return tokens;
    }
    private static int getLabelEnd(String s, int i){
        while(i<s.length()){
            if (s.charAt(i) == '"') return i;
            i++;
        }
        return 0;
    }
    private static int getVariableEnd(String s, int i){
        while(i<s.length()){
            if (s.charAt(i) == '$') return i;
            i++;
        }
        return 0;
    }
    private static int getNumberEnd(String s, int i){
        while(i<s.length()){
            if (!Character.isDigit(s.charAt(i))) return i;
            i++;
        }
        return 0;
    }
}
