package Interpreter;

import java.util.ArrayList;


public class Lexer {

    public static ArrayList<Token> lex(String expression){
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);
            if (c=='"'){
                int endLabel = getLabelEnd(expression, i + 1);
                tokens.add(new Token("LABEL",expression.substring(i,endLabel+1)));
                i=endLabel+1;
            }
            else if (c=='\''){
                int endLabel = getSubLabelEnd(expression, i + 1);
                tokens.add(new Token("SUBLABEL",expression.substring(i,endLabel+1)));
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
                int endLabel = getLoopEnd(expression, i + 1);
                tokens.add(new Token("LOOP",expression.substring(i,endLabel+1)));
                i=endLabel+1;
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
                String s = expression.substring(i, endNumber);
                if (s.indexOf('.')>=0) tokens.add(new Token("FLOAT",s));
                else tokens.add(new Token("INT",s));
                i=endNumber;
            }
            else if (c=='$'){
                int endLabel = getVariableEnd(expression, i + 1);
                tokens.add(new Token("VARIABLE",expression.substring(i,endLabel+1)));
                i=endLabel+1;
            }
            else if ("+-*/%".indexOf(c)>=0){
                tokens.add(new Token("OPERATION",Character.toString(c)));
                i++;
            }
            else if (Character.isLetter(c)){
                int endLabel = getFuncEnd(expression, i + 1);
                tokens.add(new Token("FUNCTION",expression.substring(i, endLabel)));
                i=endLabel;
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
    private static int getSubLabelEnd(String s, int i){
        while(i<s.length()){
            if (s.charAt(i) == '\'') return i;
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
            if ((!Character.isDigit(s.charAt(i))) && !(s.charAt(i)=='.')) return i;
            i++;
        }
        return 0;
    }
    private static int getFuncEnd(String s, int i){
        while(i<s.length()){
            if (!Character.isLetter(s.charAt(i))) return i;
            i++;
        }
        return 0;
    }
    private static int getLoopEnd(String s, int i){
        while(i<s.length()){
            if (s.charAt(i) == '@') return i;
            i++;
        }
        return 0;
    }
}
