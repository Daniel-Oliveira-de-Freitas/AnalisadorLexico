package Scanner;

public class NonToken {
    private  String name;
    private String value;
    public NonToken(String name,String value){
        this.name=name;
        this.value=value;
    }
    public String getName(){return name;}
    public String getValue(){return value;}
    public void setName(String name,String value){
        this.name=name;
        this.value=value;
    }

}
