package Scanner;

public class Literal {
    int lineNo;
    String name;
    String value;
    public Literal(int lineNo,String name,String value){
        this.lineNo=lineNo;
        this.name=name;
        this.value=value;
    }
    public int getLineNo(){return lineNo;}
    public String getName(){
        return name;
    }
    public String getValue(){
        return value;
    }
}
