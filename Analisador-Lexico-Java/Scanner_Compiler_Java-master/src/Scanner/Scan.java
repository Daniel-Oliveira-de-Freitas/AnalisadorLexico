package Scanner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Scan {
    public static final String[] KEYWORD={"abstract","assert",
            "boolean","break","byte","case","catch","char","class","const",
            "continue","default","do","double","else",
            "enum","extends","final","finally","float",
            "for","goto","if","implements","import",
            "instanceof","int","interface","long","native",
            "new","package","private","protected","public",
            "return","short","static","strictfp","uper",
            "switch","synchronized","this","throw","throws",
            "transient","try","void","volatile","while","true","NULL","false"};
    public static final String SYMBOL="& | + - * / % + - = < > ! [ ] { } ( ) . , ; : ";
    public static final String ALPHABET="a b c d e f g h i j k l m n o p q r s t u v w x y z _ " +
            "Q W E R T Y U I O P A S D F G H J K L Z X C V B N M";
    public static final String NUMBER=" 0 1 2 3 4 5 6 7 8 9";
    public static boolean isAlaphabet(String token){return ALPHABET.contains(token);}
    public static boolean isNumber(String token){return NUMBER.contains(token);}
    public static boolean isKeyword(String token){
       for(int i=0;i<KEYWORD.length;i++){
           if (KEYWORD[i].equals(token))
                   return true;
       }
       return false;
    }
    public static boolean isSymbol(String token){
        return SYMBOL.contains(token);
    }
    public static String getSymbolName(String symbol){
        switch (symbol){
            case "=":return "Assignment OP";
            case "--":
            case "++":
            case"!": return "Unary OP";
            case"+":
            case "-":
            case"/":
            case "*":
            case "%": return "Airthmetic OP";
            case "<":
            case ">":
            case "==":
            case "<=":
            case ">=":
            case "!=": return "Relation OP";
            case "&&":
            case"||": return "Logical OP";
            default: return "symbol";
        }
    }

}
