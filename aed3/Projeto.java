package aed3;
import java.io.*;

public class Projeto implements Registro {
    protected int codigo;
    protected String nome;
    
    public Projeto(int c, String n) {
        codigo = c;
        nome = n;
    }
    public Projeto() {
        codigo = 0;
        nome = "";
    }
    
    public void setCodigo(int c) {
        codigo = c;
    }
    
    public int getCodigo() {
        return codigo;
    }
    
    public String getString() {
        return nome;
    }
    
    public String toString() {
        return "Código.......:" + codigo +
               "\nNome.........:" + nome +
               "\n";
    }
    
    public final void writeRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException {

        ByteArrayOutputStream registro = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream( registro );
        
        saida.writeInt(codigo);
        saida.writeUTF(nome);
        
        byte[] buffer = registro.toByteArray();
        
        short tamanho = (short)buffer.length;
        arq.writeShort(tamanho);
        arq.write(buffer);

    }
    
    public final void readRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException, ClassNotFoundException {
        
        short tamanho = arq.readShort();
        byte[] ba = new byte[tamanho];
        if(arq.read(ba) != tamanho) throw new IOException("Dados inconsistentes");
        
        ByteArrayInputStream registro = new ByteArrayInputStream(ba);
        DataInputStream entrada = new DataInputStream(registro);
        codigo = entrada.readInt();
        nome = entrada.readUTF();
    }

    public final void readRegistroIndicadorTamanho(DataInputStream arq) throws IOException, ClassNotFoundException {
    }
    
    public void writeRegistroIndicadorTamanho(DataOutputStream arq) throws IOException {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int compareTo( Object b ) {
        return codigo - ((Projeto)b).codigo;
    }
  
}
