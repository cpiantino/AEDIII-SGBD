package aed3;
import java.io.*;

public interface Registro extends Comparable, Cloneable {
    
    public void setCodigo(int codigo);
    public int getCodigo();
    public String getString();   // retorna um campo string qualquer (nome, título, descricao, etc.)
    
    public void writeRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException;
    public void readRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException, ClassNotFoundException;
    public void writeRegistroIndicadorTamanho(DataOutputStream arq) throws IOException;
    public void readRegistroIndicadorTamanho(DataInputStream arq) throws IOException, ClassNotFoundException;
    
    public int compareTo( Object b  );
    public Object clone() throws CloneNotSupportedException;
    
}
