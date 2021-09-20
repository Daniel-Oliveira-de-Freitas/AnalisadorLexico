package tokens;

public class Token {
    int NmrLinha;
    String categoria;
    String nome;
    String valor;
    
	public Token(int nmrLinha, String categoria, String nome, String valor) {
		super();
		NmrLinha = nmrLinha;
		this.categoria = categoria;
		this.nome = nome;
		this.valor = valor;
	}
	
    public int getNmrLinha(){return NmrLinha;}
    public String getNome(){
        return nome;
    }
    public String getValor(){
        return valor;
    }
    public String getCategoria(){
        return categoria;
    }
    public void atualizarValor(String value){
        this.valor+=value;
    }
}