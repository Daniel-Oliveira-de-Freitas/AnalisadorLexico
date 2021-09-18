package sample;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

public class TempFile {
    private String filetext;
    private int size;

    public boolean input(File f)
    {
        try{
            FileInputStream fin=new FileInputStream(f);
            size=fin.available();
            byte b[]=new byte[size];
            fin.read(b);
            filetext=new String(b);
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
            FileOutputStream fout=new FileOutputStream(f);
            byte b[]=filetext.getBytes();
            fout.write(b);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    void setFiletext(String s)
    {
        filetext=s;
    }
    public String getFiletext()
    {
        return filetext;
    }
}
