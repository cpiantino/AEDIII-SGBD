package aed3;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ControlePrincipal {
    
    private static Scanner console = new Scanner(System.in);
    private static ArquivoIndexado<Colaborador> arquivoColaboradores;
    private static ArquivoIndexado<Projeto> arquivoProjetos;
    private static ArquivoIndexado<Tarefa> arquivoTarefas;
    private static ArvoreBMais arvoreProjetoTarefas;
    private static ArvoreBMais arvoreColaboradorTarefas;

   public static void main(String[] args) {
       
       try {
           arquivoColaboradores = new ArquivoIndexado<>(Colaborador.class, "Colaboradores.db", "Colaboradores1.idx", "Colaboradores2.idx");
           arquivoProjetos = new ArquivoIndexado<>(Projeto.class, "Projetos.db", "Projetos1.idx", "Projetos2.idx");
           arquivoTarefas = new ArquivoIndexado<>(Tarefa.class, "Tarefas.db", "Tarefas1.idx", "Tarefas2.idx");
           arvoreProjetoTarefas = new ArvoreBMais(1,"ProjetoTarefas.idx");
           arvoreColaboradorTarefas = new ArvoreBMais(1, "ColaboradorTarefas.idx");

           // menu
           int opcao;
           do {
               System.out.println("\n\n\n----------Menu Principal----------\n");
               System.out.println("COLABORADOR");
               System.out.println(" 1 Colaborador - Listar");
               System.out.println(" 2 Colaborador - Incluir");
               System.out.println(" 3 Colaborador - Alterar");
               System.out.println(" 4 Colaborador - Excluir");
               System.out.println(" 5 Colaborador - Busca por código");
               System.out.println(" 6 Colaborador - Busca por nome");
               System.out.println(" 7 Colaborador - Rel por projeto");
               System.out.println(" 8 Colaborador - Rel por data");
               System.out.println("");
               System.out.println("PROJETO");
               System.out.println(" 9 Projeto - Listar");
               System.out.println("10 Projeto - Incluir");
               System.out.println("11 Projeto - Alterar");
               System.out.println("12 Projeto - Excluir");
               System.out.println("13 Projeto - Busca por código");
               System.out.println("14 Projeto - Busca por nome");
               System.out.println("15 Projeto - Rel de tarefas");
               System.out.println("");
               System.out.println("TAREFA");
               System.out.println("16 Tarefa - Listar");
               System.out.println("17 Tarefa - Incluir");
               System.out.println("18 Tarefa - Alterar");
               System.out.println("19 Tarefa - Excluir");
               System.out.println("");
               System.out.println("OTHER");
               System.out.println("98 - Reorganizar arquivoColaboradores");
               System.out.println("99 - Povoar BD");
               System.out.println("0 - Sair");
               System.out.print("\nOpção: ");
               opcao = Integer.valueOf(console.nextLine());
               
               switch(opcao) {
                   case 1: listarColaborador(); break;
                   case 2: incluirColaborador(); break;
                   case 3: alterarColaborador(); break;
                   case 4: excluirColaborador(); break;
                   case 5: buscarColaboradorCodigo(); break;
                   case 6: buscarColaboradorNome(); break;
                   case 7: relatorioTarefasDoColaboradorPorProjeto(); break; //Rel por projeto
                   case 8: relatorioTarefasDoColaboradorPorData(); break; //Rel por data
                   case 9: listarProjeto(); break;
                   case 10: incluirProjeto(); break;
                   case 11: alterarProjeto(); break;
                   case 12: excluirProjeto(); break;
                   case 13: buscarProjetoCodigo(); break;
                   case 14: buscarProjetoNome(); break;
                   case 15: relatorioTarefasDoProjeto(); break; //Rel Projeto por tarefa
                   case 16: listarTarefa(); break;
                   case 17: incluirTarefa(); break; //Incluir tarefa
                   case 18: alterarTarefa(); break; //Alterar tarefa
                   case 19: excluirTarefa(); break;//Excluir tarefa
                   case 98: reorganizar(); break;
                   case 99: povoar(); break;
                   case 0: break;
                   default: System.out.println("Opção inválida");
               }
               
           } while(opcao!=0);
       } catch(Exception e) {
           e.printStackTrace();
       }
       
   }
   
   // ColaboradorS
   
   public static void listarColaborador() throws Exception {
       
       Object[] Colaboradores = arquivoColaboradores.listar();
       
       for(int i=0; i<Colaboradores.length; i++) {
           System.out.println("\n"+(Colaborador)Colaboradores[i]);
       }
       
   }
   
   public static void incluirColaborador() throws Exception {
       
       String nome, email;
       
       System.out.println("\nINCLUSÃO");
       System.out.print("Nome: ");
       nome = console.nextLine();
       System.out.print("email: ");
       email = console.nextLine();
       
       System.out.print("\nConfirma inclusão? ");
       char confirma = console.nextLine().charAt(0);
       if(confirma=='s' || confirma=='S') {
           Colaborador l = new Colaborador(-1, nome, email);
           int cod = arquivoColaboradores.incluir(l);
           System.out.println("Colaborador incluído com código: "+cod);
       }
   }

   
   public static void alterarColaborador() throws Exception {
       
       System.out.println("\nALTERAÇÃO");

       int codigo;
       System.out.print("Código: ");
       codigo = Integer.valueOf(console.nextLine());
       if(codigo <=0) 
           return;
       
       Colaborador l;
       if( (l = (Colaborador)arquivoColaboradores.buscarCodigo(codigo))!=null ) {
            System.out.println(l);
            
            String nome, email;
            
            System.out.print("\nNovo nome: ");
            nome = console.nextLine();
            System.out.print("Novo email: ");
            email = console.nextLine();

            System.out.print("\nConfirma alteração? ");
            char confirma = console.nextLine().charAt(0);
            if(confirma=='s' || confirma=='S') {
                
                l.nome = (nome.length()>0?nome:l.nome);
                l.email = (email.length()>0?email:l.email);
                
                if( arquivoColaboradores.alterar(l) ) 
                    System.out.println("Colaborador alterado.");
                else
                    System.out.println("Colaborador não pode ser alterado.");
            }
       }
       else
           System.out.println("Colaborador não encontrado");
       
   }
  
   
   public static void excluirColaborador() throws Exception {
       
       System.out.println("\nEXCLUSÃO");

       int codigo;
       System.out.print("Código: ");
       codigo = Integer.valueOf(console.nextLine());
       if(codigo <=0) 
           return;
       
       Colaborador l;
       if( (l = (Colaborador)arquivoColaboradores.buscarCodigo(codigo))!=null ) {
            System.out.println(l);
            System.out.print("\nConfirma exclusão? ");
            char confirma = console.nextLine().charAt(0);
            if(confirma=='s' || confirma=='S') {
                if( arquivoColaboradores.excluir(codigo) ) {
                    System.out.println("Colaborador excluído.");
                }
            }
       }
       else
           System.out.println("Colaborador não encontrado");
       
   }
   
   
   public static void buscarColaboradorCodigo() throws Exception {
       
       System.out.println("\nBUSCA POR CÓDIGO");
       
       int codigo;
       System.out.print("Código: ");
       codigo = Integer.valueOf(console.nextLine());
       if(codigo <=0) 
           return;
       
       Colaborador l;
       if( (l = (Colaborador)arquivoColaboradores.buscarCodigo(codigo))!=null )
           System.out.println(l);
       else
           System.out.println("Colaborador não encontrado");
   }

   public static void buscarColaboradorNome() throws Exception {
       
       System.out.println("\nBUSCA POR NOME");
       
       String nome;
       System.out.print("Nome: ");
       nome = console.nextLine();
       if(nome == "") 
           return;
       
       Colaborador l;
       if( (l = (Colaborador)arquivoColaboradores.buscarString(nome))!=null )
           System.out.println(l);
       else
           System.out.println("Colaborador não encontrado");
   }
   
   public static void listarProjeto() throws Exception {
       
       Object[] Projetos = arquivoProjetos.listar();
       
       for(int i=0; i<Projetos.length; i++) {
           System.out.println("\n"+(Projeto)Projetos[i]);
       }
       
   }
   
   public static void incluirProjeto() throws Exception {
       
       String nome;
       
       System.out.println("\nINCLUSÃO");
       System.out.print("Nome: ");
       nome = console.nextLine();
       
       System.out.print("\nConfirma inclusão? ");
       char confirma = console.nextLine().charAt(0);
       if(confirma=='s' || confirma=='S') {
           Projeto l = new Projeto(-1, nome);
           int cod = arquivoProjetos.incluir(l);
           System.out.println("Projeto incluído com código: "+cod);
       }
   }

   
   public static void alterarProjeto() throws Exception {
       
       System.out.println("\nALTERAÇÃO");

       int codigo;
       System.out.print("Código: ");
       codigo = Integer.valueOf(console.nextLine());
       if(codigo <=0) 
           return;
       
       Projeto l;
       if( (l = (Projeto)arquivoProjetos.buscarCodigo(codigo))!=null ) {
            System.out.println(l);
            
            String nome;
            
            System.out.print("\nNovo nome: ");
            nome = console.nextLine();

            System.out.print("\nConfirma alteração? ");
            char confirma = console.nextLine().charAt(0);
            if(confirma=='s' || confirma=='S') {
                
                l.nome = (nome.length()>0?nome:l.nome);
                
                if( arquivoProjetos.alterar(l) ) 
                    System.out.println("Projeto alterado.");
                else
                    System.out.println("Projeto não pode ser alterado.");
            }
       }
       else
           System.out.println("Projeto não encontrado");
       
   }
  
   
   public static void excluirProjeto() throws Exception {
       
       System.out.println("\nEXCLUSÃO");

       int codigo;
       System.out.print("Código: ");
       codigo = Integer.valueOf(console.nextLine());
       if(codigo <=0) 
           return;
       
       Projeto l;
       if( (l = (Projeto)arquivoProjetos.buscarCodigo(codigo))!=null ) {
            System.out.println(l);
            System.out.print("\nConfirma exclusão? ");
            char confirma = console.nextLine().charAt(0);
            if(confirma=='s' || confirma=='S') {
                if( arquivoProjetos.excluir(codigo) ) {
                    System.out.println("Projeto excluído.");
                }
            }
       }
       else
           System.out.println("Projeto não encontrado");
       
   }
   
   
   public static void buscarProjetoCodigo() throws Exception {
       
       System.out.println("\nBUSCA POR CÓDIGO");
       
       int codigo;
       System.out.print("Código: ");
       codigo = Integer.valueOf(console.nextLine());
       if(codigo <=0) 
           return;
       
       Projeto l;
       if( (l = (Projeto)arquivoProjetos.buscarCodigo(codigo))!=null )
           System.out.println(l);
       else
           System.out.println("Projeto não encontrado");
   }

   public static void buscarProjetoNome() throws Exception {
       
       System.out.println("\nBUSCA POR NOME");
       
       String nome;
       System.out.print("Nome: ");
       nome = console.nextLine();
       if(nome == "") 
           return;
       
       Projeto l;
       if( (l = (Projeto)arquivoProjetos.buscarString(nome))!=null )
           System.out.println(l);
       else
           System.out.println("Projeto não encontrado");
   }
   
   public static void listarTarefa() throws Exception {
       
       Object[] Tarefas = arquivoTarefas.listar();
       
       for(int i=0; i<Tarefas.length; i++) {
           System.out.println("\n"+(Tarefa)Tarefas[i]);
       }
       
   }
   
   public static void incluirTarefa() throws Exception {
       
       String descricao, vencimento;
       int codProjeto, codColaborador;
       short prioridade;
       
       System.out.println("\nINCLUSÃO");
       System.out.print("Descrição: ");
       descricao = console.nextLine();
       System.out.print("Vencimento: ");
       vencimento = console.nextLine();
       System.out.print("Projeto (código): ");
       codProjeto = console.nextInt();
       console.nextLine();
       System.out.print("Colaborador (código): ");
       codColaborador = console.nextInt();
       console.nextLine();
       System.out.print("Prioridade (0 a 3): ");
       prioridade = console.nextShort();
       console.nextLine();
       
       System.out.print("\nConfirma inclusão? ");
       char confirma = console.nextLine().charAt(0);
       if(confirma=='s' || confirma=='S') {
    	   try{
    		   Tarefa l = new Tarefa(-1, descricao, codProjeto, codColaborador, vencimento, prioridade, arquivoProjetos, arquivoColaboradores);
    		   int cod = arquivoTarefas.incluir(l);
    		   arvoreProjetoTarefas.inserir(codProjeto,cod);
                   arvoreColaboradorTarefas.inserir(codColaborador, cod);
    		   System.out.println("Tarefa incluída com código: "+cod);
    	   }catch(Exception e){
    		   e.printStackTrace();
    	   }
       }
   }

    public static void alterarTarefa() throws Exception {

        System.out.println("\nALTERAÇÃO DE TAREFA");

        int codigo;
        System.out.print("Código: ");
        codigo = Integer.valueOf(console.nextLine());
        if(codigo <=0)
            return;

        Tarefa l;
        if( (l = (Tarefa)arquivoTarefas.buscarCodigo(codigo))!=null ) {
            System.out.println(l);

            String desc;
            int codProjeto;
            int codColaborador;
            String vencimento;
            short prioridade;

            System.out.print("Nova descrição: ");
            desc = console.nextLine();
            System.out.print("Novo projeto da tarefa: ");
            codProjeto = console.nextInt();
            System.out.print("Novo colaborador responsável: ");
            codColaborador = console.nextInt();
            System.out.print("Nova data de vencimento: ");
            vencimento = console.nextLine();
            console.nextLine();
            System.out.print("Nova prioridade: ");
            prioridade = console.nextShort();
            console.nextLine();
            System.out.print("Confirma alteração? ");
            char confirma = console.nextLine().charAt(0);
            if(confirma=='s' || confirma=='S') {

                l.desc = (desc.length()>0?desc:l.desc);
                l.codProjeto = (codProjeto>0?codProjeto:l.codProjeto);
                l.codColaborador = (codColaborador>0?codColaborador:l.codColaborador);
                l.vencimento = l.vencimento;
                if (vencimento.length() > 0) {
                    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdf.parse(vencimento));
                    l.vencimento = cal;
                }
                l.prioridade = ((0<=prioridade&&prioridade<=3)?prioridade:l.prioridade);

                if(arquivoProjetos.buscarCodigo(codProjeto) != null && arquivoColaboradores.buscarCodigo(codColaborador) != null) {
                    if( arquivoTarefas.alterar(l) )
                        System.out.println("Tarefa alterada.");
                    else
                        System.out.println("Tarefa não pode ser alterada.");
                }
                else {
                    String exceptionStringProjeto = arquivoProjetos.buscarCodigo(codProjeto) == null ? "(Projeto)" : "";
                    String exceptionStringColaborador = arquivoColaboradores.buscarCodigo(codColaborador) == null ? "(Colaborador)" : "";
                    System.out.println("\nCódigo "+exceptionStringProjeto+exceptionStringColaborador+" inválido");
                }
            }
        }
        else
            System.out.println("Tarefa não encontrada");

    }


    public static void excluirTarefa() throws Exception {

        System.out.println("\nEXCLUSÃO DE TAREFA");

        int codigo;
        System.out.print("Código: ");
        codigo = Integer.valueOf(console.nextLine());
        if(codigo <=0)
            return;

        Tarefa l;
        if( (l = (Tarefa)arquivoTarefas.buscarCodigo(codigo))!=null ) {
            System.out.println(l);
            System.out.print("\nConfirma exclusão? ");
            char confirma = console.nextLine().charAt(0);
            if(confirma=='s' || confirma=='S') {
                if( arquivoTarefas.excluir(codigo) ) {
                    arvoreProjetoTarefas.excluir(l.codProjeto,l.getCodigo()); 
                    arvoreColaboradorTarefas.excluir(l.codColaborador, l.getCodigo());
                    System.out.println("Tarefa excluída.");
                }
            }
        }
        else
            System.out.println("Tarefa não encontrada");

    }
   
   public static void relatorioTarefasDoProjeto()throws Exception{
	   int codigoProjeto;
	   int [] listaTarefas = null;
	   
	   System.out.println("\nRELATÓRIO DE TAREFAS");
	   System.out.print("Projeto (código): ");
	   codigoProjeto = console.nextInt();
	   console.nextLine();

       if (arquivoProjetos.buscarCodigo(codigoProjeto) != null) {
           listaTarefas = arvoreProjetoTarefas.lista(codigoProjeto);

           System.out.println("\nPROJETO:");
           System.out.println(arquivoProjetos.buscarCodigo(codigoProjeto));
           System.out.println("TAREFAS:");
           for(int x=0;x<listaTarefas.length;x++){
               Tarefa tarefa = (Tarefa) arquivoTarefas.buscarCodigo(listaTarefas[x]);
               Colaborador colaborador = (Colaborador) arquivoColaboradores.buscarCodigo(tarefa.codColaborador);
               String nome = colaborador==null? "(!)Excluido(!)" : colaborador.nome;
               int codigo = colaborador==null? -1 : colaborador.codigo;
               System.out.println("Nome do Reponsável: "+nome+" (Cod:"+codigo+")");
               System.out.println(tarefa);
           }
       }else {
           System.out.println("\nProjeto não existe (!)");
       }
   }
   
   public static void relatorioTarefasDoColaboradorPorProjeto()throws Exception{
	   int codigoColaborador;
	   int [] listaTarefas = null;
	   
	   System.out.println("\nRELATÓRIO DE TAREFAS");
	   System.out.print("Colaborador (código): ");
	   codigoColaborador = console.nextInt();
	   console.nextLine();

       if (arquivoColaboradores.buscarCodigo(codigoColaborador) != null) {
           listaTarefas = arvoreColaboradorTarefas.lista(codigoColaborador);
           List<Tarefa> tarefas = new ArrayList<Tarefa>();
           for (int i=0; i<listaTarefas.length; i++) tarefas.add((Tarefa)arquivoTarefas.buscarCodigo(listaTarefas[i]));
           
           Comparator<Tarefa> porProjeto = new Comparator<Tarefa>() {
               @Override
               public int compare(Tarefa t1, Tarefa t2) {
                   if (t1.codProjeto < t2.codProjeto) return -1;
                   else if (t1.codProjeto > t2.codProjeto) return 1;
                   else {
                        if (t1.prioridade > t2.prioridade) return -1;
                        else if (t1.prioridade < t2.prioridade) return 1;
                        else {
                            return t1.vencimento.compareTo(t2.vencimento);
                        }
                   }
               }
           };
           tarefas.sort(porProjeto);
           Colaborador colaborador = (Colaborador)arquivoColaboradores.buscarCodigo(codigoColaborador);
           System.out.println("Nome: "+colaborador.nome+"\n");
           for (Tarefa tarefa : tarefas) System.out.println(tarefa);
        }else {
            System.out.println("\nColaborador não existe (!)");
        }
   }
   
   public static void relatorioTarefasDoColaboradorPorData()throws Exception{
	   int codigoColaborador;
	   int [] listaTarefas = null;
	   
	   System.out.println("\nRELATÓRIO DE TAREFAS");
	   System.out.print("Colaborador (código): ");
	   codigoColaborador = console.nextInt();
	   console.nextLine();

       if (arquivoColaboradores.buscarCodigo(codigoColaborador) != null) {
           listaTarefas = arvoreColaboradorTarefas.lista(codigoColaborador);
           List<Tarefa> tarefas = new ArrayList<Tarefa>();
           for (int i=0; i<listaTarefas.length; i++) tarefas.add((Tarefa)arquivoTarefas.buscarCodigo(listaTarefas[i]));
           
           Comparator<Tarefa> porProjeto = new Comparator<Tarefa>() {
               @Override
               public int compare(Tarefa t1, Tarefa t2) {
                   if (t1.vencimento.compareTo(t2.vencimento)<0) return -1;
                   else if (t1.vencimento.compareTo(t2.vencimento)>0) return 1;
                   else {
                       if (t1.prioridade > t2.prioridade) return -1;
                       else if (t1.prioridade < t2.prioridade) return 1;
                       else return 0;
                   }
               }
           };
           tarefas.sort(porProjeto);
           Colaborador colaborador = (Colaborador)arquivoColaboradores.buscarCodigo(codigoColaborador);
           System.out.println("Nome: "+colaborador.nome+"\n");
           for (Tarefa tarefa : tarefas) System.out.println(tarefa);
        }else {
            System.out.println("\nColaborador não existe (!)");
        }
   }

   public static void reorganizar() throws Exception {

        System.out.println("\nREORGANIZAÇÃO");
        arquivoColaboradores.reorganizar();
        System.out.println("\nArquivo de Colaboradores reorganizado");
    
   }
   
   public static void povoar() throws Exception {
        arquivoColaboradores.incluir(new Colaborador(-1,"O Pequeno Príncipe","Antoine de Saint-Exupéry"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Número Zero","Humberto Eco"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Garota no Trem","Paula Hawkins"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Rainha Vermelha","Victoria Aveyard"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Sol É Para Todos","Harper Lee"));
        arquivoColaboradores.incluir(new Colaborador(-1,"1984","George Orwell"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Odisséia","Homero"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Sherlock Holmes","Arthur Conan Doyle"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Joyland","Stephen King"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Objetos Cortantes","Gillian Flynn"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Lista Negra","Jennifer Brown"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Garota Exemplar","Gillian Flynn"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Iluminado","Stephen King"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Queda de Gigantes","Ken Follett"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Eternidade Por Um Fio","Ken Follett"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Inverno do Mundo","Ken Follett"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Guerra dos Tronos","George R. R. Martin"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Revolução dos Bichos","George Orwell"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Mundo de Sofia","Jostein Gaarder"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Velho e o Mar","Ernest Hemingway"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Escuridão Total Sem Estrelas","Stephen King"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Pintassilgo","Donna Tartt"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Se Eu Ficar","Gayle Forman"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Toda Luz Que Não Podemos Ver","Anthony Doerr"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Eu, Você e a Garota Que Vai Morrer","Jesse Andrews"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Na Natureza Selvagem","Jon Krakauer"));
        arquivoColaboradores.incluir(new Colaborador(-1,"Eu, Robô","Isaac Asimov"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Demonologista","Andrew Pyper"));
        arquivoColaboradores.incluir(new Colaborador(-1,"O Último Policial","Ben Winters"));
        arquivoColaboradores.incluir(new Colaborador(-1,"A Febre","Megan Abbott"));
       
   }
   
}
