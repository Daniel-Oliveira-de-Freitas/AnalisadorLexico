package controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

public class Arquivo {
    private String arquivoTexto;
    private int tamanho;

    public boolean input(File f)
    {
        try{
            FileInputStream lerArquivo=new FileInputStream(f);
            tamanho = lerArquivo.available();
            byte b[]=new byte[tamanho];
            lerArquivo.read(b);
            arquivoTexto=new String(b);
        }
        catch(Exception e)
        {

            return false;

        }
        return true;
    }
    
    public boolean output(File f)
    {
        try{
            FileOutputStream salvarArquivo =new FileOutputStream(f);
            byte b[] = arquivoTexto.getBytes();
            salvarArquivo.write(b);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    
    void setFiletext(String s)
    {
    	arquivoTexto=s;
    }
    
    public String getFiletext()
    {
        return arquivoTexto;
    }
}
