import Interpreter.Lexer;
import Interpreter.Parser;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws Exception {
        String exp = "\"hello\":ascii(80);\"tricount\"=int(4);\"triangles\":@$tricount$@'n'>float(12)";
        System.out.println(Lexer.lex(exp));
    }
}

