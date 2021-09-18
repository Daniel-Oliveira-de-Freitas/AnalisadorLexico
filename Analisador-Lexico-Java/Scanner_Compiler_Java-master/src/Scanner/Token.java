package Scanner;

public class Token {
    int lineNo;
    String catagoy;
    String name;
    String value;
    public Token(int lineNo,String catagory,String name,String value){
        this.lineNo=lineNo;
        this.catagoy=catagory;
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
    public String getCatagoy(){
        return catagoy;
    }
    public void updateValue(String value){
        this.value+=value;
    }
}
