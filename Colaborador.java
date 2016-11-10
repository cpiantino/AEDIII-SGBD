package aed3;
import java.io.*;

public class Colaborador implements Registro {
    protected int codigo;
    protected String nome;
    protected String email;

    
    public final int TAMANHO_NOME = 20;
    public final int TAMANHO_EMAIL = 20;
    public final int TAMANHO_REGISTRO = TAMANHO_NOME + TAMANHO_EMAIL + 8;
    
    public Colaborador(int c, String n, String e) {
        codigo = c;
        nome = n;
        email = e;

    }
    public Colaborador() {
        codigo = 0;
        nome = "";
        email = "";

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
        return "\nCódigo.:" + codigo +
                "\nNome...:" + nome +
                "\nE-mail.:" + email;

    }
    
    public final void writeRegistroTamanhoVariavel(RandomAccessFile arq) throws IOException {
        arq.writeInt(codigo);
        arq.writeUTF(nome);
        arq.writeUTF(email);

    }
    
    public final void readRegistroTamanhoVariavel(RandomAccessFile arq) throws IOException, ClassNotFoundException {
        codigo = arq.readInt();
        nome = arq.readUTF();
        email = arq.readUTF();

    }

    public final void writeRegistroTamanhoFixo(RandomAccessFile arq) throws IOException {
        
        ByteArrayOutputStream registro = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream( registro );
        
        saida.writeInt(codigo);
        byte[] aux = nome.getBytes();
        byte[] buffer = new byte[TAMANHO_NOME];
        int i;
        for(i=0; i<aux.length; i++)
            buffer[i] = aux[i];
        while(i<TAMANHO_NOME)
            buffer[i++] = ' ';
        saida.write(buffer);
        aux = email.getBytes();
        buffer = new byte[TAMANHO_EMAIL];
        for(i=0; i<aux.length; i++)
            buffer[i] = aux[i];
        while(i<TAMANHO_EMAIL)
            buffer[i++] = ' ';
        saida.write(buffer);

        arq.write(registro.toByteArray());
    }
    
    public final void readRegistroTamanhoFixo(RandomAccessFile arq) throws IOException, ClassNotFoundException {
        
        byte[] ba = new byte[TAMANHO_REGISTRO];
        if(arq.read(ba) != TAMANHO_REGISTRO) throw new IOException("Dados inconsistentes");
        
        ByteArrayInputStream registro = new ByteArrayInputStream(ba);
        DataInputStream entrada = new DataInputStream(registro);
        codigo = entrada.readInt();
        
        byte[] buffer = new byte[TAMANHO_NOME];
        entrada.read(buffer);
        nome = new String(buffer).trim();
        buffer = new byte[TAMANHO_EMAIL];
        entrada.read(buffer);
        email = new String(buffer).trim();

    }

    public final void writeRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException {

        ByteArrayOutputStream registro = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream( registro );
        
        saida.writeInt(codigo);
        saida.writeUTF(nome);
        saida.writeUTF(email);

        byte[] buffer = registro.toByteArray();
        
        short tamanho = (short)buffer.length;
        arq.writeShort(tamanho);
        arq.write(buffer);

    }
    
   public final void writeRegistroIndicadorTamanho(DataOutputStream arq) throws IOException {

        ByteArrayOutputStream registro = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream( registro );
        
        saida.writeInt(codigo);
        saida.writeUTF(nome);
        saida.writeUTF(email);

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
        email = entrada.readUTF();

    }

    public final void readRegistroIndicadorTamanho(DataInputStream arq) throws IOException, ClassNotFoundException {
        
        short tamanho = arq.readShort();
        byte[] ba = new byte[tamanho];
        if(arq.read(ba) != tamanho) throw new IOException("Dados inconsistentes");
        
        ByteArrayInputStream registro = new ByteArrayInputStream(ba);
        DataInputStream entrada = new DataInputStream(registro);
        codigo = entrada.readInt();
        nome = entrada.readUTF();
        email = entrada.readUTF();

    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int compareTo( Object b ) {
        return codigo - ((Colaborador)b).codigo;
    }
    
}
