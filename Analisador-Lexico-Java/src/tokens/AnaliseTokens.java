package tokens;

public class AnaliseTokens {
    public static final String[] PALAVRA_CHAVE={"boolean","break",
    		"byte","char","class","default","Double","else","System","out","print",
            "println","float","for","if","import","int","long",
            "new","package","private","protected","public",
            "return","short","static","void","while","true","NULL","false","String"};
    
    public static final String SIMBOLO="& | + - * / % + - = < > ! [ ] { } ( ) . , ; : ";
    public static final String ALFABETO="a b c d e f g h i j k l m n o p q r s t u v w x y z _ " +
            "Q W E R T Y U I O P A S D F G H J K L Z X C V B N M";
    public static final String NUMERO=" 0 1 2 3 4 5 6 7 8 9";
    public static boolean eAlfabeto(String token){return ALFABETO.contains(token);}
    public static boolean eNumero(String token){return NUMERO.contains(token);}
   
    public static boolean ePalavraChave(String token){
       for(int i=0;i<PALAVRA_CHAVE.length;i++){
           if (PALAVRA_CHAVE[i].equals(token))
                   return true;
       }
       return false;
    }
    
    public static boolean isSymbol(String token){
        return SIMBOLO.contains(token);
    }
    
    public static String getNomeSimbolo(String simbolo){
        switch (simbolo){
            case "=":  return "OP";
            case "--": return "OP";
            case "++": return "OP";
            case "!":  return "OP";
            case "+":  return "OP";
            case "-":  return "OP";
            case "/":  return "OP";
            case "*":  return "OP";
            case "%":  return "OP";
            case "<":  return "OP";
            case ">":  return "OP";
            case "==": return "OP";
            case "<=": return "OP";
            case ">=": return "OP";
            case "!=": return "OP";
            case "&&": return "OP";
            case "||": return "OP";
            default:   return "";
        }
    }

}
