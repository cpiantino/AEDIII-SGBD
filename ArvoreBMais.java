package aed3;
import java.io.*;
import java.util.ArrayList;

public class ArvoreBMais {

    private int  ordem;
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    
    // Variáveis usadas nas funções recursivas (já que não é possível passar valores por referência)
    private int  chave1Extra;
    private int  chave2Extra;
    private long paginaExtra;
    private boolean cresceu;
    private boolean diminuiu;
    
    
    class ArvoreBMais_Pagina {

        protected int    ordem;
        protected int    n;
        protected int[]  chaves1;
        protected int[]  chaves2;
        protected long[] filhos;
        private int TAMANHO_REGISTRO = 8;
        protected int TAMANHO_PAGINA;

        public ArvoreBMais_Pagina(int o) {
            n = 0;
            ordem = o;
            chaves1 = new int[ordem*2];
            chaves2 = new int[ordem*2];
            filhos = new long[ordem*2+1];
            for(int i=0; i<ordem*2; i++) {  
                chaves1[i] = -1;
                chaves2[i] = -1;
                filhos[i] = -1;
            }
            filhos[ordem*2] = -1;
            TAMANHO_PAGINA = 4 + (ordem*2)*8 + (ordem*2+1)*8;
        }
        
        protected byte[] getBytes() throws IOException {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(ba);
            out.writeInt(n);
            int i=0;
            while(i<n) {
                out.writeLong(filhos[i]);
                out.writeInt(chaves1[i]);
                out.writeInt(chaves2[i]);
                i++;
            }
            out.writeLong(filhos[i]);
            byte[] registroVazio = new byte[TAMANHO_REGISTRO];
            while(i<ordem*2){
                out.write(registroVazio);
                out.writeLong(filhos[i+1]);
                i++;
            }
            return ba.toByteArray();
        }

        public void setBytes(byte[] buffer) throws IOException {
            ByteArrayInputStream ba = new ByteArrayInputStream(buffer);
            DataInputStream in = new DataInputStream(ba);
            n = in.readInt();
            int i=0;
            while(i<ordem*2) {
                filhos[i] = in.readLong();
                chaves1[i] = in.readInt();
                chaves2[i] = in.readInt();
                i++;
            }
            filhos[i] = in.readLong();
        }
    }
    
    public ArvoreBMais(int o, String na) throws IOException {
        ordem = o;
        nomeArquivo = na;
        arquivo = new RandomAccessFile(nomeArquivo,"rw");
        if(arquivo.length()<8) 
            arquivo.writeLong(-1);  // raiz vazia
    }
    
    // Testa se a árvore está vazia
    public boolean vazia() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        return raiz == -1;
    }
    
        
    // Método para buscar
    public int[] lista(int c1) throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        if(raiz!=-1)
            return lista1(c1,raiz);
        else
            return new int[0];
    }
    
    private int[] lista1(int chave1, long pagina) throws IOException {
        if(pagina==-1)
            return new int[0];
        
        arquivo.seek(pagina);
        ArvoreBMais_Pagina pa = new ArvoreBMais_Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
 
        // Encontra (recursivamente) a página com as chaves
        int i=0;
        while(i<pa.n && chave1>pa.chaves1[i]) {
            i++;
        }
        
        // Registro encontrado. Retorna a primeira chave2 (se não especificada) ou a chave seguinte à chave2
        if(i<pa.n && pa.filhos[0]==-1 && chave1==pa.chaves1[i]) { // chave1 encontrada em uma folha
            ArrayList lista = new ArrayList();
            while(chave1==pa.chaves1[i]) {
                lista.add(pa.chaves2[i]);
                i++;
                if(i==pa.n) {
                    if(pa.filhos[ordem*2]==-1)
                        break;
                    arquivo.seek(pa.filhos[ordem*2]);
                    arquivo.read(buffer);
                    pa.setBytes(buffer);
                    i=0;
                }
            }
            int[] resposta = new int[lista.size()];
            for(int j=0; j<lista.size(); j++)
                resposta[j] = (int)lista.get(j);
            return resposta;

        }
        else if(i==pa.n && pa.filhos[0]==-1) { // testa se chave está na próxima folha
            if(pa.filhos[ordem*2]==-1)
                return new int[0];
            arquivo.seek(pa.filhos[ordem*2]);
            arquivo.read(buffer);
            pa.setBytes(buffer);
            i=0;
            if(chave1==pa.chaves1[0]) {
                ArrayList lista = new ArrayList();
                while(chave1==pa.chaves1[i]) {
                    lista.add(pa.chaves2[i]);
                    i++;
                    if(i==pa.n) {
                        if(pa.filhos[ordem*2]==-1)
                            break;
                        arquivo.seek(pa.filhos[ordem*2]);
                        arquivo.read(buffer);
                        pa.setBytes(buffer);
                        i=0;
                    }
                }
                int[] resposta = new int[lista.size()];
                for(int j=0; j<lista.size(); j++)
                    resposta[j] = (int)lista.get(j);
                return resposta;
            }
            else
                return new int[0];
        }
        if(i==pa.n || chave1<=pa.chaves1[i])
            return lista1(chave1, pa.filhos[i]);
        else
            return lista1(chave1, pa.filhos[i+1]);
    }
        
    
    // Método para inclusão -> transforma a chamada em um função recursiva a partir da raoz
    public void inserir(int c1, int c2) throws IOException {

        if(c1<0 || c2<0) {
            System.out.println( "Chaves não podem ser negativas" );
            return;
        }
            
        arquivo.seek(0);       
        long pagina;                           // carrega a raiz como primeira página
        pagina = arquivo.readLong();

        chave1Extra = c1;         // converte chave e chaves2 para tipos de referência, para que possam ser atualizados pela função recursiva
        chave2Extra = c2;
        paginaExtra = -1;       // ponteiro para a página filho direito do registro promovido
        cresceu = false;        // controla se houve crescimento da árvore
                
        // Inclui o registro (na chave1Extra e no chave2Extra) na página
        inserir1(pagina);
        
        // Testa a necessidade de criação de uma nova raiz
        if(cresceu) {
            ArvoreBMais_Pagina novaPagina = new ArvoreBMais_Pagina(ordem);
            novaPagina.n = 1;
            novaPagina.chaves1[0] = chave1Extra;
            novaPagina.chaves2[0] = chave2Extra;
            novaPagina.filhos[0] = pagina;
            novaPagina.filhos[1] = paginaExtra;
            
            // Achar o espaço em disco....
            arquivo.seek(arquivo.length());
            long raiz = arquivo.getFilePointer();
            arquivo.write(novaPagina.getBytes());
            arquivo.seek(0);
            arquivo.writeLong(raiz);
        }
    }
    
    private void inserir1(long pagina) throws IOException {
        
        // testa se atingiu uma página folha. Caso afirmativo, cria o registro e o devolve para ser incluído
        if(pagina==-1) {
            cresceu = true;
            paginaExtra = -1;
            return;
        }
        
        // Lê a página
        arquivo.seek(pagina);
        ArvoreBMais_Pagina pa = new ArvoreBMais_Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
        
        // Encontra o próximo ponteiro de descida
        int i=0;
        while(i<pa.n && (chave1Extra>pa.chaves1[i] || (chave1Extra==pa.chaves1[i] && chave2Extra>pa.chaves2[i]))) {
            i++;
        }
        
        // Testa se o registro já existe em uma folha
        if(i<pa.n && pa.filhos[0]==-1 && chave1Extra==pa.chaves1[i] && chave2Extra==pa.chaves2[i]) {
            cresceu = false;
            return;
        }
        
        // busca recursivamente por uma nova página
        if(i==pa.n || chave1Extra<pa.chaves1[i] || (chave1Extra==pa.chaves1[i] && chave2Extra<=pa.chaves2[i]))
            inserir1(pa.filhos[i]);
        else
            inserir1(pa.filhos[i+1]);
        
        // Controle o retorno das chamadas recursivas sem a inclusão de nova página (se o registro já existir ou couber em página existente)
        if(!cresceu)
            return;
        
        // Se tiver espaço na página
        if(pa.n<ordem*2) {
            long proximaPagina=-1;
            if(pa.filhos[0]==-1 && pa.filhos[ordem*2]!=-1) proximaPagina = pa.filhos[ordem*2]; // mantém referência para a próxima folha
            for(int j=pa.n; j>i; j--) {
                pa.chaves1[j] = pa.chaves1[j-1];
                pa.chaves2[j] = pa.chaves2[j-1];
                pa.filhos[j+1] = pa.filhos[j];
            }
            pa.chaves1[i] = chave1Extra;
            pa.chaves2[i] = chave2Extra;
            pa.filhos[i+1] = paginaExtra;
            pa.n++;
            if(proximaPagina!=-1) pa.filhos[ordem*2]=proximaPagina;
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            cresceu=false;
            return;
        }
        
        // Overflow: divide a página e move metade dos registros
        ArvoreBMais_Pagina np = new ArvoreBMais_Pagina(ordem);
        for(int j=0; j<ordem; j++) {
            np.chaves1[j] = pa.chaves1[j+ordem];
            np.chaves2[j] = pa.chaves2[j+ordem];   
            np.filhos[j+1] = pa.filhos[j+ordem+1];  
        }
        np.filhos[0] = pa.filhos[ordem];
        np.n = ordem;
        pa.n = ordem;
        
        // Testa o lado de inserção
        if(i<=ordem) {   // novo registro deve ficar na página da esquerda
            for(int j=ordem; j>0 && j>i; j--) {
                pa.chaves1[j] = pa.chaves1[j-1];
                pa.chaves2[j] = pa.chaves2[j-1];
                pa.filhos[j+1] = pa.filhos[j];
            }
            pa.chaves1[i] = chave1Extra;
            pa.chaves2[i] = chave2Extra;
            pa.filhos[i+1] = paginaExtra;
            pa.n++;
        } else {
            int j=0;
            for(j=ordem; j>0 && (chave1Extra<np.chaves1[j-1] || (chave1Extra==np.chaves1[j-1]&&chave2Extra<np.chaves2[j-1]) ); j--) {
                np.chaves1[j] = np.chaves1[j-1];
                np.chaves2[j] = np.chaves2[j-1];
                np.filhos[j+1] = np.filhos[j];
            }
            np.chaves1[j] = chave1Extra;
            np.chaves2[j] = chave2Extra;
            np.filhos[j+1] = paginaExtra;
            np.n++;
        }
        chave1Extra = pa.chaves1[pa.n-1];
        chave2Extra = pa.chaves2[pa.n-1];
        
        arquivo.seek(arquivo.length());
        paginaExtra = arquivo.getFilePointer();
        arquivo.write(np.getBytes());

        if(pa.filhos[0]==-1) pa.filhos[ordem*2] = paginaExtra; // Testa se é uma folha e se o último ponteiro deve apontar para a próxima página
        arquivo.seek(pagina);
        arquivo.write(pa.getBytes());
    }

    
    public boolean excluir(int chave1, int chave2) throws IOException {
                
        arquivo.seek(0);       
        long pagina;                           // carrega a raiz como primeira página
        pagina = arquivo.readLong();

        diminuiu = false;        // controla se houve redução da árvore
                
        // Inclui o registro (na chave1Extra e no chave2Extra) na página
        boolean excluido = excluir1(chave1, chave2, pagina);
        if(excluido && diminuiu) {
            arquivo.seek(pagina);
            ArvoreBMais_Pagina pa = new ArvoreBMais_Pagina(ordem);
            byte[] buffer = new byte[pa.TAMANHO_PAGINA];
            arquivo.read(buffer);
            pa.setBytes(buffer);
            if(pa.n == 0) {
                arquivo.seek(0);
                arquivo.writeLong(pa.filhos[0]);  // Atualiza raiz. A raiz antiga deveria ser encaixada na lista de espaços disponíveis
            }
        }
         
        return excluido;
    }
    
    private boolean excluir1(int chave1, int chave2, long pagina) throws IOException {
        
        boolean excluido=false;
        int diminuido;
        
        // Testa se o registro não foi encontrado na árvore
        if(pagina==-1) {
            diminuiu=false;
            return false;
        }
        
        // Lê a página no disco
        arquivo.seek(pagina);
        ArvoreBMais_Pagina pa = new ArvoreBMais_Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);

        // Encontra (recursivamente) a página
        int i=0;
        while(i<pa.n && (chave1>pa.chaves1[i] || (chave1==pa.chaves1[i] && chave2>pa.chaves2[i]))) {
            i++;
        }

        // registro encontrado
        if(i<pa.n && pa.filhos[0]==-1 && chave1==pa.chaves1[i] && chave2==pa.chaves2[i]) {

            int j;
            for(j=i; j<pa.n-1; j++) {
                pa.chaves1[j] = pa.chaves1[j+1];
                pa.chaves2[j] = pa.chaves2[j+1];
            }
            pa.n--;
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            diminuiu = pa.n<ordem;
            return true;
        }

        if(i==pa.n || chave1<pa.chaves1[i] || (chave1==pa.chaves1[i] && chave2<=pa.chaves2[i])) {
            excluido = excluir1(chave1, chave2, pa.filhos[i]);
            diminuido = i;
        } else {
            excluido = excluir1(chave1, chave2, pa.filhos[i+1]);
            diminuido = i+1;
        }
        
        // Testa se houve redução de nós
        if(diminuiu) {

            long paginaFilho = pa.filhos[diminuido];
            ArvoreBMais_Pagina pFilho = new ArvoreBMais_Pagina(ordem);
            arquivo.seek(paginaFilho);
            arquivo.read(buffer);
            pFilho.setBytes(buffer);
            
            long paginaIrmao;
            ArvoreBMais_Pagina pIrmao;
            
            // Testa fusão com irmão esquerdo
            if(diminuido>0) {
                paginaIrmao = pa.filhos[diminuido-1];
                pIrmao = new ArvoreBMais_Pagina(ordem);
                arquivo.seek(paginaIrmao);
                arquivo.read(buffer);
                pIrmao.setBytes(buffer);
                
                // Testa se o irmão pode emprestar algum registro
                if(pIrmao.n>ordem) {
                    
                    // move todos os registros no filho para a direita
                    for(int j=pFilho.n; j>0; j--) {
                        pFilho.chaves1[j] = pFilho.chaves1[j-1];
                        pFilho.chaves2[j] = pFilho.chaves2[j-1];
                        pFilho.filhos[j+1] = pFilho.filhos[j];
                    }
                    pFilho.filhos[1] = pFilho.filhos[0];
                    pFilho.n++;
                    
                    // copia o elemento do irmão
                    pFilho.chaves1[0] = pIrmao.chaves1[pIrmao.n-1];
                    pFilho.chaves2[0] = pIrmao.chaves2[pIrmao.n-1];
                    
                    // sobe o elemento do irmão
                    pFilho.filhos[0] = pIrmao.filhos[pIrmao.n];
                    pIrmao.n--;
                    pa.chaves1[diminuido-1] = pIrmao.chaves1[pIrmao.n-1];
                    pa.chaves2[diminuido-1] = pIrmao.chaves2[pIrmao.n-1];
                    diminuiu = false;
                }
                
                // Se não puder emprestar, faz a fusão dos dois irmãos
                else {

                    // copia todos os registros para o irmão da esquerda
                    for(int j=0; j<pFilho.n; j++) {
                        pIrmao.chaves1[pIrmao.n] = pFilho.chaves1[j];
                        pIrmao.chaves2[pIrmao.n] = pFilho.chaves2[j];
                        pIrmao.filhos[pIrmao.n+1] = pFilho.filhos[j+1];
                        pIrmao.n++;
                    }
                    pFilho.n = 0;   // aqui o endereço do filho poderia ser incluido em uma lista encadeada no cabeçalho, indicando os espaços reaproveitáveis
                    
                    // puxa os registros no pai
                    for(int j=diminuido-1; j<pa.n; j++) {
                        pa.chaves1[j] = pa.chaves1[j+1];
                        pa.chaves2[j] = pa.chaves2[j+1];
                        pa.filhos[j+1] = pa.filhos[j+2];
                    }
                    pa.n--;
                    diminuiu = pa.n<ordem;  // testa se o pai também ficou sem o número mínimo de elementos
                }
            }
            
            // fusão com o irmão direito
            else {
                paginaIrmao = pa.filhos[diminuido+1];
                pIrmao = new ArvoreBMais_Pagina(ordem);
                arquivo.seek(paginaIrmao);
                arquivo.read(buffer);
                pIrmao.setBytes(buffer);
                
                // Testa se o irmão pode emprestar algum registro
                if(pIrmao.n>ordem) {
                    
                    // copia o elemento do irmão
                    pFilho.chaves1[pFilho.n] = pIrmao.chaves1[0];
                    pFilho.chaves2[pFilho.n] = pIrmao.chaves2[0];
                    pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                    pFilho.n++;

                    // sobe o elemento do irmão
                    pa.chaves1[diminuido] = pIrmao.chaves1[0];
                    pa.chaves2[diminuido] = pIrmao.chaves2[0];
                    
                    // move todos os registros no irmão para a esquerda
                    int j;
                    for(j=0; j<pIrmao.n-1; j++) {
                        pIrmao.chaves1[j] = pIrmao.chaves1[j+1];
                        pIrmao.chaves2[j] = pIrmao.chaves2[j+1];
                        pIrmao.filhos[j] = pIrmao.filhos[j+1];
                    }
                    pIrmao.filhos[j] = pIrmao.filhos[j+1];
                    pIrmao.n--;
                    diminuiu = false;
                }
                
                else {
                    // copia todos os registros do irmão da direita
                    for(int j=0; j<pIrmao.n; j++) {
                        pFilho.chaves1[pFilho.n] = pIrmao.chaves1[j];
                        pFilho.chaves2[pFilho.n] = pIrmao.chaves2[j];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[j+1];
                        pFilho.n++;
                    }
                    pIrmao.n = 0;   // aqui o endereço do irmão poderia ser incluido em uma lista encadeada no cabeçalho, indicando os espaços reaproveitáveis
                    
                    // puxa os registros no pai
                    for(int j=diminuido; j<pa.n-1; j++) {
                        pa.chaves1[j] = pa.chaves1[j+1];
                        pa.chaves2[j] = pa.chaves2[j+1];
                        pa.filhos[j+1] = pa.filhos[j+2];
                    }
                    pa.n--;
                    diminuiu = pa.n<ordem;  // testa se o pai também ficou sem o número mínimo de elementos
                }
            }
            
            // Atualiza todos os registros
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            arquivo.seek(paginaFilho);
            arquivo.write(pFilho.getBytes());
            arquivo.seek(paginaIrmao);
            arquivo.write(pIrmao.getBytes());
        }
        return excluido;
    }
    
    
    public void print() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        if(raiz!=-1)
            print1(raiz,0,0);
        System.out.println();
    }
    
    private void print1(long pagina, int nivel, int dados) throws IOException {
        if(pagina==-1)
            return;
        int i;

        arquivo.seek(pagina);
        ArvoreBMais_Pagina pa = new ArvoreBMais_Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
        
        System.out.print(nivel+"."+dados+": (");
        for(i=0; i<pa.n; i++) {
            if(pa.filhos[i]!=-1)
                System.out.print((nivel+1)+"."+((nivel*dados*ordem*2)+i));
            System.out.print(") "+pa.chaves1[i]+","+pa.chaves2[i]+" (");
        }
        for(;i<ordem*2;i++) {
            if(pa.filhos[i]!=-1)
                System.out.print((nivel+1)+"."+((nivel*dados*ordem*2)+i));
            System.out.print(") - (");
        }
        if(pa.filhos[i]==-1)
            System.out.println(")");
        else
            System.out.println((nivel+1)+"."+((nivel*dados*ordem*2)+i)+")");
        for(i=0;i<=ordem*2;i++)
            print1(pa.filhos[i],nivel+1,((nivel*dados*ordem*2)+i));
    }
    
    
    public static void main(String[] args) {

        ArvoreBMais arvore;
        
        try {
            
            File f = new File("dados.db");
            f.delete();
            arvore = new ArvoreBMais(2,"dados.db");
            
            System.out.println("Árvore B+ de ordem 2:\n");
            System.out.println("Inserção de 20,20 - 20,21 - 30,30 - 20,13");
            arvore.inserir( 20, 20 );
            arvore.inserir( 20, 21 );
            arvore.inserir( 30, 30 );
            arvore.inserir( 20, 13 );
            arvore.print();
            System.out.println("Inserção de 20,28");
            arvore.inserir( 20, 28 );
            arvore.print();
            
            System.out.println("Lista de chaves2 de 20:");
            int[] lista = arvore.lista(20);
            for(int i=0; i<lista.length; i++) {
                System.out.print(lista[i]+"  ");
            }
            System.out.println("\n");
            
            System.out.println("Inserção de 10,10 - 10,11");
            arvore.inserir(10,10);
            arvore.inserir(10,11);
            arvore.print();

            System.out.println("Remoção de 20,20");
            arvore.excluir(20,20);
            arvore.print();
            System.out.println("Remoção de 20,13");
            arvore.excluir(20,13);
            arvore.print();


            System.out.println("Lista de chaves2 de 20:");
            lista = arvore.lista(20);
            for(int i=0; i<lista.length; i++) {
                System.out.print(lista[i]+" ");
            }
            System.out.println("\n");

            
            System.out.println("Remoção de 20,21");
            arvore.excluir(20,21);
            arvore.print();
            System.out.println("Remoção de 20,28");
            arvore.excluir(20,28);
            arvore.print();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
            
        
    }
    
}
