package aed3;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class Tarefa implements Registro{
	private int cod;
	private String desc;
	private int codProjeto;
	private int codColaborador;
	private String vencimento;
	private short prioridade;
	
	Tarefa(){
		this.cod = 0;
		this.codColaborador = 0;
		this.codProjeto = 0;
		this.desc = "";
		this.prioridade = -1;
		this.vencimento = "";
	}
	
	Tarefa(int c, String d, int cP, int cC, String v, short p, ArquivoIndexado arqP, ArquivoIndexado arqC) throws Exception{
		if(arqP.buscarCodigo(cP) != null && arqC.buscarCodigo(cC) != null){
			this.cod = c;
			this.codColaborador = cC;
			this.codProjeto = cP;		
			this.desc = d;
			this.prioridade = p;
			this.vencimento = v;
		}else{
			throw new Exception("Código inválido");
		}
	}
	
	public void setCodigo(int c){
		this.cod = c;
	}
	
	public int getCodigo(){
		return this.cod;
	}
	
	public String getString(){
		return codColaborador+"";
	}
	
	public void writeRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException{
		ByteArrayOutputStream registro = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream( registro );
        
        saida.writeInt(cod);
        saida.writeUTF(desc);
        saida.writeInt(codProjeto);
        saida.writeInt(codColaborador);
        saida.writeUTF(vencimento);
        saida.writeShort(prioridade);

        byte[] buffer = registro.toByteArray();
        
        short tamanho = (short)buffer.length;
        arq.writeShort(tamanho);
        arq.write(buffer);
	}
    public void readRegistroIndicadorTamanho(RandomAccessFile arq) throws IOException, ClassNotFoundException{
    	short tamanho = arq.readShort();
        byte[] ba = new byte[tamanho];
        if(arq.read(ba) != tamanho) throw new IOException("Dados inconsistentes");
        
        ByteArrayInputStream registro = new ByteArrayInputStream(ba);
        DataInputStream entrada = new DataInputStream(registro);
        this.cod = entrada.readInt();
        this.desc = entrada.readUTF();
        this.codProjeto = entrada.readInt();
        this.codColaborador = entrada.readInt();
        this.vencimento = entrada.readUTF();
        this.prioridade = entrada.readShort();
    }
    public void writeRegistroIndicadorTamanho(DataOutputStream arq) throws IOException{
    	
    }
    public void readRegistroIndicadorTamanho(DataInputStream arq) throws IOException, ClassNotFoundException{
    	
    }
    
    public int compareTo( Object b  ){
    	return cod - ((Tarefa)b).cod;
    }
    public Object clone() throws CloneNotSupportedException{
    	return super.clone();
    }
    
    public String toString() {
        return "\nCódigo...............:" + cod +
                "\nDescrição............:" + desc +
                "\nProjeto (código).....:" + codProjeto +
                "\nColaborador (código).:" + codColaborador +
                "\nData de validade.....:" + vencimento +
                "\nPrioridade...........:" + prioridade;

    }
}
