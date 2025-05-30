package org.example;

import java.util.Scanner;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (UsuarioDAO.quantidadeUsuarios() == 0) {
            System.out.println("Nenhum usuário cadastrado. Vamos criar o primeiro administrador:");
            System.out.print("Nome: "); String nomeAdmin = scanner.nextLine();
            System.out.print("Login: "); String loginAdmin = scanner.nextLine();
            System.out.print("Senha: "); String senhaAdmin = scanner.nextLine();
            System.out.print("RA: "); String raAdmin = scanner.nextLine();
            if (UsuarioDAO.cadastrarUsuarioComRA(nomeAdmin, loginAdmin, senhaAdmin, "administrador", raAdmin)) {
                System.out.println("Administrador criado com sucesso! Faça login para continuar.\n");
            } else {
                System.out.println("Erro ao criar administrador. Encerrando o programa.");
                scanner.close(); return;
            }
        }
        Usuario usuarioLogado = null;
        while (usuarioLogado == null) {
            System.out.print("Login: "); String login = scanner.nextLine();
            System.out.print("Senha: "); String senha = scanner.nextLine();
            usuarioLogado = UsuarioDAO.autenticar(login, senha);
        }
        System.out.println("\nBem-vindo, " + usuarioLogado.getNome() + " (" + usuarioLogado.getClass().getSimpleName() + ")!\n");

        if (usuarioLogado instanceof Administrador) {
            menuAdministrador(scanner, usuarioLogado);
        } else if (usuarioLogado instanceof Funcionario) {
            menuFuncionario(scanner, usuarioLogado);
        } else {
            menuUsuarioComum(scanner, usuarioLogado);
        }
        scanner.close();
        System.out.println("Programa encerrado.");
    }

    private static void menuAdministrador(Scanner scanner, Usuario usuarioLogado) {
        int opcao;
        do {
            System.out.println("\n==== Menu Administrador ====");
            System.out.println("1. Adicionar Livro Físico");
            System.out.println("2. Remover Livro Físico (por ID)");
            System.out.println("3. Listar Livros Físicos");
            System.out.println("4. Pesquisar Livros Físicos");
            System.out.println("5. Emprestar Livro");
            System.out.println("6. Devolver Livro");
            System.out.println("7. Listar Todos os Empréstimos Ativos");
            System.out.println("8. Reservar Livro para Usuário");
            System.out.println("9. Listar Todas as Reservas Pendentes");
            System.out.println("10. Pesquisar Reservas Pendentes");
            System.out.println("11. Listar Usuários");
            System.out.println("12. Cadastrar Novo Usuário (RA automático)");
            System.out.println("13. Editar Dados Cadastrais"); // OPÇÃO MOVIDA E RENOMEADA
            System.out.println("14. Remover Usuário (por RA)"); // Renumerado
            System.out.println("15. Sair");                   // Renumerado
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
            } else {
                System.out.println("Opção inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = 0;
            }
            scanner.nextLine();

            switch (opcao) {
                // Cases 1 a 10 e 12, 14, 15 como nas últimas versões
                // (Omitidos por brevidade, mas devem ser os códigos que já tínhamos)
                case 1: /* Adicionar Livro */
                    System.out.println("--- Adicionar Novo Livro Físico ---");
                    System.out.print("Título: "); String titulo = scanner.nextLine();
                    System.out.print("Autor: "); String autor = scanner.nextLine();
                    System.out.print("Tópicos: "); String topicos = scanner.nextLine();
                    System.out.println("\nEscolha a Categoria:");
                    List<String> categoriasDisponiveis = LivroFisico.CATEGORIAS_VALIDAS;
                    for (int i = 0; i < categoriasDisponiveis.size(); i++) { System.out.println((i + 1) + ". " + categoriasDisponiveis.get(i)); }
                    System.out.print("Digite o número da categoria: "); String categoriaEscolhida = null;
                    if (scanner.hasNextInt()) {
                        int escolha = scanner.nextInt(); scanner.nextLine();
                        if (escolha > 0 && escolha <= categoriasDisponiveis.size()) categoriaEscolhida = categoriasDisponiveis.get(escolha - 1);
                        else { System.out.println("Categoria inválida."); break; }
                    } else { System.out.println("Entrada inválida."); scanner.nextLine(); break; }
                    System.out.print("Ano de Publicação: "); int ano = 0;
                    if (scanner.hasNextInt()) ano = scanner.nextInt(); else { System.out.println("Ano inválido."); scanner.nextLine(); break;}
                    scanner.nextLine();
                    System.out.print("Código do Livro (ISBN): "); String codL = scanner.nextLine();
                    System.out.print("Código Interno: "); String codI = scanner.nextLine();
                    LivroFisico.adicionarLivroFisico(titulo, autor, topicos, categoriaEscolhida, ano, codL, codI);
                    break;
                case 2: /* Remover Livro */
                    LivroFisico.listarLivrosFisicosComDisponibilidade();
                    System.out.print("\nID do livro para remover: "); int idRem = 0;
                    if(scanner.hasNextInt()) idRem = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                    scanner.nextLine(); Administrador.removerLivroPorId(idRem);
                    break;
                case 3: /* Listar Livros */
                    LivroFisico.listarLivrosFisicosComDisponibilidade();
                    break;
                case 4: /* Pesquisar Livros */
                    System.out.print("Termo de pesquisa: "); String termoP = scanner.nextLine();
                    LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoP);
                    break;
                case 5: /* Emprestar Livro */
                    System.out.print("Pesquisar livro (título/autor): "); String termoLEmp = scanner.nextLine();
                    LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoLEmp);
                    System.out.print("ID do livro para emprestar: "); int idLEmp = 0;
                    if(scanner.hasNextInt()) idLEmp = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                    scanner.nextLine();
                    System.out.print("Pesquisar usuário (nome/RA): "); String termoUEmp = scanner.nextLine();
                    UsuarioDAO.pesquisarEListarUsuarios(termoUEmp);
                    System.out.print("\nRA do usuário para empréstimo: "); String raUEmp = scanner.nextLine();
                    Usuario usrEmp = UsuarioDAO.buscarUsuarioPorRA(raUEmp);
                    if (usrEmp == null) System.out.println("Usuário RA " + raUEmp + " não encontrado.");
                    else {
                        EmprestimoStatus statusEmp = EmprestimoDAO.emprestarLivros(idLEmp, usrEmp.getId());
                        if (statusEmp == EmprestimoStatus.ERRO_LIVRO_INDISPONIVEL) {
                            System.out.print("Livro indisponível. Deseja criar uma reserva para '" + usrEmp.getNome() + "'? (S/N): ");
                            if (scanner.nextLine().equalsIgnoreCase("S")) ReservaDAO.criarReserva(idLEmp, usrEmp.getId());
                            else System.out.println("Reserva não criada.");
                        }
                    }
                    break;
                case 6: /* Devolver Livro */
                    System.out.println("--- Devolução de Livro ---");
                    System.out.println("A seguir, a lista de TODOS os empréstimos ativos no sistema:");
                    EmprestimoDAO.listarTodosEmprestimosAtivos();
                    System.out.print("\nPesquisar usuário (nome/RA) que está devolvendo: "); String termoUDev = scanner.nextLine();
                    UsuarioDAO.pesquisarEListarUsuarios(termoUDev);
                    System.out.print("\nConfirme RA do usuário para devolução: "); String raUDev = scanner.nextLine();
                    Usuario usrDev = UsuarioDAO.buscarUsuarioPorRA(raUDev);
                    if (usrDev == null) System.out.println("Usuário RA " + raUDev + " não encontrado.");
                    else {
                        System.out.println("\nEmpréstimos ativos para " + usrDev.getNome() + ":");
                        EmprestimoDAO.listarMeusEmprestimosAtivos(usrDev.getId());
                        System.out.print("ID do livro a ser devolvido por este usuário: "); int idLDev = 0;
                        if(scanner.hasNextInt()) idLDev = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                        scanner.nextLine(); EmprestimoDAO.devolverLivro(idLDev, usrDev.getId());
                    }
                    break;
                case 7: /* Listar Todos Empréstimos Ativos */ EmprestimoDAO.listarTodosEmprestimosAtivos(); break;
                case 8: /* Reservar Livro para Usuário */
                    System.out.println("--- Reservar Livro para Usuário ---");
                    System.out.print("Pesquisar livro (título/autor): "); String termoLRes = scanner.nextLine();
                    LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoLRes);
                    System.out.print("ID do livro para reservar: "); int idLRes = 0;
                    if(scanner.hasNextInt()) idLRes = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                    scanner.nextLine();
                    System.out.print("Pesquisar usuário (nome/RA) para reserva: "); String termoURes = scanner.nextLine();
                    UsuarioDAO.pesquisarEListarUsuarios(termoURes);
                    System.out.print("\nRA do usuário para reserva: "); String raURes = scanner.nextLine();
                    Usuario usrRes = UsuarioDAO.buscarUsuarioPorRA(raURes);
                    if (usrRes == null) System.out.println("Usuário RA " + raURes + " não encontrado.");
                    else ReservaDAO.criarReserva(idLRes, usrRes.getId());
                    break;
                case 9: /* Listar Todas as Reservas Pendentes */ ReservaDAO.listarTodasAsReservasPendentes(); break;
                case 10: /* Pesquisar Reservas Pendentes */
                    System.out.print("Termo para pesquisar reservas (título livro, nome/RA usuário): "); String termoPRes = scanner.nextLine();
                    ReservaDAO.pesquisarEListarReservasPendentes(termoPRes);
                    break;
                case 11: /* Listar Usuários */ UsuarioDAO.listarUsuarios(); break;
                case 12: /* Cadastrar Usuário */
                    System.out.println("--- Cadastrar Novo Usuário ---");
                    System.out.print("Nome: "); String nomeUNovo = scanner.nextLine();
                    System.out.print("Login: "); String loginUNovo = scanner.nextLine();
                    System.out.print("Senha: "); String senhaUNovo = scanner.nextLine();
                    System.out.println("Tipo: 1.Admin 2.Funcionario 3.Cliente"); System.out.print("Escolha: "); int tipoOpt = 0;
                    if(scanner.hasNextInt()) tipoOpt = scanner.nextInt(); else {System.out.println("Tipo inválido."); scanner.nextLine(); break;}
                    scanner.nextLine(); String tipoUStr;
                    switch(tipoOpt) { case 1: tipoUStr="administrador"; break; case 2: tipoUStr="funcionario"; break; case 3: tipoUStr="cliente"; break; default: tipoUStr="cliente"; System.out.println("Padrão: cliente.");}
                    if(UsuarioDAO.cadastrarUsuario(nomeUNovo, loginUNovo, senhaUNovo, tipoUStr)) System.out.println("Usuário cadastrado com RA automático.");
                    break;

                case 13: // EDITAR DADOS CADASTRAIS (NOVA OPÇÃO MOVIDA PARA CÁ)
                    menuEditarDados(scanner, usuarioLogado);
                    break;

                case 14: /* Remover Usuário */
                    UsuarioDAO.listarUsuarios();
                    System.out.print("RA do usuário para remover: "); String raURem = scanner.nextLine();
                    Usuario usrRem = UsuarioDAO.buscarUsuarioPorRA(raURem);
                    if(usrRem == null) System.out.println("Usuário RA " + raURem + " não encontrado.");
                    else if (usrRem.getId() == usuarioLogado.getId()) System.out.println("Não pode remover a si mesmo.");
                    else UsuarioDAO.removerUsuarioPorId(usrRem.getId());
                    break;
                case 15: // Sair
                    System.out.println("Saindo do menu administrador...");
                    break;
                default:
                    if (opcao != 0) System.out.println("Opção inválida.");
            }
        } while (opcao != 15); // Condição do loop atualizada
    }

    // NOVO MÉTODO PARA SUBMENU DE EDIÇÃO
    private static void menuEditarDados(Scanner scanner, Usuario adminLogado) {
        int escolhaSubMenu;
        do {
            System.out.println("\n--- Menu Editar Dados Cadastrais ---");
            System.out.println("1. Editar Livro");
            System.out.println("2. Editar Usuário");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            if (scanner.hasNextInt()) {
                escolhaSubMenu = scanner.nextInt();
            } else {
                System.out.println("Opção inválida.");
                scanner.nextLine();
                escolhaSubMenu = 0;
            }
            scanner.nextLine();

            switch (escolhaSubMenu) {
                case 1:
                    editarLivroInterface(scanner);
                    break;
                case 2:
                    editarUsuarioInterface(scanner, adminLogado);
                    break;
                case 3:
                    System.out.println("Retornando ao menu principal do administrador...");
                    break;
                default:
                    if(escolhaSubMenu != 0) System.out.println("Opção inválida.");
            }
        } while (escolhaSubMenu != 3);
    }

    // NOVO MÉTODO: Interface para editar livro
    private static void editarLivroInterface(Scanner scanner) {
        System.out.println("\n--- Editar Livro ---");
        System.out.print("Digite o termo de pesquisa (título/autor) do livro a editar: ");
        String termoLivro = scanner.nextLine();
        LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoLivro);

        System.out.print("Digite o ID do livro que deseja editar: ");
        int idLivroEditar;
        if (scanner.hasNextInt()) {
            idLivroEditar = scanner.nextInt();
            scanner.nextLine();
        } else {
            System.out.println("ID inválido. Operação cancelada.");
            scanner.nextLine();
            return;
        }

        LivroFisico livroParaEditar = LivroFisico.buscarLivroCompletoPorId(idLivroEditar);
        if (livroParaEditar == null) {
            return;
        }

        System.out.println("\n--- Editando Livro ID: " + livroParaEditar.getId() + " ---");
        livroParaEditar.exibirInformacoes();
        System.out.println("------------------------------------");
        System.out.println("Deixe o campo em branco e pressione Enter para não alterar o valor existente.");

        System.out.print("Novo Título (atual: " + livroParaEditar.getTitulo() + "): ");
        String novoTitulo = scanner.nextLine();
        if (novoTitulo.isEmpty()) novoTitulo = livroParaEditar.getTitulo();

        System.out.print("Novo Autor (atual: " + livroParaEditar.getAutor() + "): ");
        String novoAutor = scanner.nextLine();
        if (novoAutor.isEmpty()) novoAutor = livroParaEditar.getAutor();

        System.out.print("Novos Tópicos (atual: " + (livroParaEditar.getTopicos() != null ? livroParaEditar.getTopicos() : "") + "): ");
        String novosTopicos = scanner.nextLine();
        if (novosTopicos.isEmpty()) novosTopicos = livroParaEditar.getTopicos();

        System.out.println("\nCategoria Atual: " + livroParaEditar.getCategoria());
        System.out.println("Escolha a Nova Categoria (ou Enter para manter):");
        List<String> categoriasDisponiveis = LivroFisico.CATEGORIAS_VALIDAS;
        for (int i = 0; i < categoriasDisponiveis.size(); i++) {
            System.out.println((i + 1) + ". " + categoriasDisponiveis.get(i));
        }
        System.out.print("Digite o número da nova categoria: ");
        String inputCategoria = scanner.nextLine();
        String novaCategoria = livroParaEditar.getCategoria();
        if (!inputCategoria.isEmpty()) {
            if (inputCategoria.matches("\\d+")) {
                int escolhaCatNum = Integer.parseInt(inputCategoria);
                if (escolhaCatNum > 0 && escolhaCatNum <= categoriasDisponiveis.size()) {
                    novaCategoria = categoriasDisponiveis.get(escolhaCatNum - 1);
                } else { System.out.println("Número de categoria inválido. Mantendo: " + novaCategoria); }
            } else { System.out.println("Entrada inválida para categoria. Mantendo: " + novaCategoria); }
        }

        System.out.print("Novo Ano de Publicação (atual: " + livroParaEditar.getAnoPublicacao() + "): ");
        String inputAno = scanner.nextLine();
        int novoAno = livroParaEditar.getAnoPublicacao();
        if (!inputAno.isEmpty()) {
            try { novoAno = Integer.parseInt(inputAno); }
            catch (NumberFormatException e) { System.out.println("Ano inválido. Mantendo: " + novoAno); }
        }

        System.out.print("Novo Código do Livro (ISBN) (atual: " + (livroParaEditar.getCodLivro() != null ? livroParaEditar.getCodLivro() : "") + "): ");
        String novoCodLivro = scanner.nextLine();
        if (novoCodLivro.isEmpty()) novoCodLivro = livroParaEditar.getCodLivro();

        System.out.print("Novo Código Interno (atual: " + (livroParaEditar.getCodInterno() != null ? livroParaEditar.getCodInterno() : "") + "): ");
        String novoCodInterno = scanner.nextLine();
        if (novoCodInterno.isEmpty()) novoCodInterno = livroParaEditar.getCodInterno();

        System.out.print("Nova Quantidade Total em Estoque (atual: " + livroParaEditar.getQuantidade() + "): ");
        String inputQtde = scanner.nextLine();
        int novaQuantidade = livroParaEditar.getQuantidade();
        if (!inputQtde.isEmpty()) {
            try { novaQuantidade = Integer.parseInt(inputQtde); if(novaQuantidade < 0) { System.out.println("Quantidade não pode ser negativa. Mantendo: " + novaQuantidade); novaQuantidade = livroParaEditar.getQuantidade(); } }
            catch (NumberFormatException e) { System.out.println("Quantidade inválida. Mantendo: " + novaQuantidade); }
        }

        if (LivroFisico.atualizarDetalhesLivro(livroParaEditar.getId(), novoTitulo, novoAutor, novosTopicos,
                novaCategoria, novoAno, novoCodLivro, novoCodInterno, novaQuantidade)) {
            // Mensagem de sucesso já é dada por atualizarDetalhesLivro
        } else {
            // Mensagem de erro já é dada por atualizarDetalhesLivro
        }
    }

    private static void editarUsuarioInterface(Scanner scanner, Usuario adminLogado) {
        System.out.println("\n--- Editar Usuário ---");
        System.out.print("Digite o termo de pesquisa (nome/RA) do usuário a editar: ");
        String termoUsuario = scanner.nextLine();
        UsuarioDAO.pesquisarEListarUsuarios(termoUsuario);

        System.out.print("Digite o RA do usuário que deseja editar: ");
        String raEditar = scanner.nextLine();
        Usuario usuarioParaEditar = UsuarioDAO.buscarUsuarioPorRA(raEditar);

        if (usuarioParaEditar == null) {
            System.out.println("Usuário com RA '" + raEditar + "' não encontrado.");
            return;
        }
        // Para pegar o RA e tipo_usuario atuais para exibição, vamos precisar de um método
        // em UsuarioDAO que retorne esses campos junto com o objeto, ou consultá-los
        // aqui se o objeto Usuario base não os tiver.
        // Por ora, exibiremos o que temos e o RA usado na busca.
        // O método buscarUsuarioPorRA retorna o tipo correto de objeto, podemos inferir o tipo.
        String tipoAtual = "";
        if (usuarioParaEditar instanceof Administrador) tipoAtual = "administrador";
        else if (usuarioParaEditar instanceof Funcionario) tipoAtual = "funcionario";
        else if (usuarioParaEditar instanceof Cliente) tipoAtual = "cliente";

        System.out.println("\n--- Editando Usuário ID: " + usuarioParaEditar.getId() + " (Login: " + usuarioParaEditar.getLogin() + ") ---");
        System.out.println("Nome Atual: " + usuarioParaEditar.getNome());
        System.out.println("Login Atual: " + usuarioParaEditar.getLogin());
        System.out.println("RA Atual (usado na busca): " + raEditar);
        System.out.println("Tipo Atual: " + tipoAtual);
        System.out.println("------------------------------------");
        System.out.println("Deixe o campo em branco e pressione Enter para não alterar o valor existente.");

        System.out.print("Novo Nome (atual: " + usuarioParaEditar.getNome() + "): ");
        String novoNome = scanner.nextLine();
        if (novoNome.isEmpty()) novoNome = usuarioParaEditar.getNome();

        System.out.print("Novo Login (atual: " + usuarioParaEditar.getLogin() + "): ");
        String novoLogin = scanner.nextLine();
        if (novoLogin.isEmpty()) novoLogin = usuarioParaEditar.getLogin();

        System.out.print("Nova Senha (deixe em branco para NÃO alterar): ");
        String novaSenha = scanner.nextLine();

        System.out.println("\nTipo de Usuário Atual: " + tipoAtual);
        System.out.println("Escolha o Novo Tipo (ou Enter para manter): 1.Admin 2.Funcionario 3.Cliente");
        System.out.print("Digite o número do novo tipo: ");
        String inputTipo = scanner.nextLine();
        String novoTipoUsuario = tipoAtual;
        if (!inputTipo.isEmpty()) {
            if (inputTipo.matches("\\d+")) {
                int escolhaTipoNum = Integer.parseInt(inputTipo);
                switch (escolhaTipoNum) {
                    case 1: novoTipoUsuario = "administrador"; break;
                    case 2: novoTipoUsuario = "funcionario"; break;
                    case 3: novoTipoUsuario = "cliente"; break;
                    default: System.out.println("Número de tipo inválido. Mantendo tipo: " + novoTipoUsuario); break;
                }
            } else { System.out.println("Entrada inválida para tipo. Mantendo tipo: " + novoTipoUsuario); }
        }

        if (usuarioParaEditar.getId() == adminLogado.getId() &&
                usuarioParaEditar instanceof Administrador &&
                !novoTipoUsuario.equalsIgnoreCase("administrador")) {
            if (UsuarioDAO.contarAdministradores() <= 1) {
                System.out.println("AVISO: Este é o único administrador. Não é possível alterar seu tipo para não-administrador.");
                novoTipoUsuario = "administrador";
            }
        }

        System.out.print("Novo RA (atual: " + raEditar + "): ");
        String novoRa = scanner.nextLine();
        if (novoRa.isEmpty()) novoRa = raEditar;

        if (UsuarioDAO.atualizarDetalhesUsuario(usuarioParaEditar.getId(), novoNome, novoLogin,
                (novaSenha.isEmpty() ? null : novaSenha),
                novoTipoUsuario, novoRa)) {
            // Mensagem de sucesso já é dada por atualizarDetalhesUsuario
        } else {
            // Mensagem de erro já é dada por atualizarDetalhesUsuario
        }
    }

    private static void menuFuncionario(Scanner scanner, Usuario usuarioLogado) { /* ... como na última versão ... */
        // O menu do funcionário não terá a opção de editar dados por enquanto.
        // Cole aqui o menuFuncionario da última versão funcional.
        int opcao;
        do {
            System.out.println("\n==== Menu Funcionário ====");
            System.out.println("1. Listar Livros Físicos");
            System.out.println("2. Pesquisar Livros Físicos");
            System.out.println("3. Emprestar Livro");
            System.out.println("4. Devolver Livro");
            System.out.println("5. Listar Todos os Empréstimos Ativos");
            System.out.println("6. Reservar Livro para Usuário");
            System.out.println("7. Listar Usuários");
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");
            if (scanner.hasNextInt()) { opcao = scanner.nextInt(); }
            else { System.out.println("Opção inválida."); scanner.nextLine(); opcao = 0; }
            scanner.nextLine();
            switch (opcao) {
                case 1: LivroFisico.listarLivrosFisicosComDisponibilidade(); break;
                case 2: System.out.print("Termo de pesquisa: "); LivroFisico.pesquisarLivrosFisicosComDisponibilidade(scanner.nextLine()); break;
                case 3:
                    System.out.print("Pesquisar livro (título/autor): "); String termoLEmpFunc = scanner.nextLine();
                    LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoLEmpFunc);
                    System.out.print("ID do livro para emprestar: "); int idLEmpFunc = 0;
                    if(scanner.hasNextInt()) idLEmpFunc = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                    scanner.nextLine();
                    System.out.print("Pesquisar usuário (nome/RA): "); String termoUEmpFunc = scanner.nextLine();
                    UsuarioDAO.pesquisarEListarUsuarios(termoUEmpFunc);
                    System.out.print("\nRA do usuário para empréstimo: "); String raUEmpFunc = scanner.nextLine();
                    Usuario usrEmpFunc = UsuarioDAO.buscarUsuarioPorRA(raUEmpFunc);
                    if (usrEmpFunc == null) System.out.println("Usuário RA " + raUEmpFunc + " não encontrado.");
                    else {
                        EmprestimoStatus statusEmpFunc = EmprestimoDAO.emprestarLivros(idLEmpFunc, usrEmpFunc.getId());
                        if (statusEmpFunc == EmprestimoStatus.ERRO_LIVRO_INDISPONIVEL) {
                            System.out.print("Livro indisponível. Deseja criar uma reserva para '" + usrEmpFunc.getNome() + "'? (S/N): ");
                            if (scanner.nextLine().equalsIgnoreCase("S")) ReservaDAO.criarReserva(idLEmpFunc, usrEmpFunc.getId());
                            else System.out.println("Reserva não criada.");
                        }
                    }
                    break;
                case 4:
                    System.out.println("--- Devolução de Livro ---");
                    EmprestimoDAO.listarTodosEmprestimosAtivos();
                    System.out.print("\nPesquisar usuário (nome/RA) que está devolvendo: "); String termoUDevFunc = scanner.nextLine();
                    UsuarioDAO.pesquisarEListarUsuarios(termoUDevFunc);
                    System.out.print("\nConfirme RA do usuário para devolução: "); String raUDevFunc = scanner.nextLine();
                    Usuario usrDevFunc = UsuarioDAO.buscarUsuarioPorRA(raUDevFunc);
                    if (usrDevFunc == null) System.out.println("Usuário RA " + raUDevFunc + " não encontrado.");
                    else {
                        System.out.println("\nEmpréstimos ativos para " + usrDevFunc.getNome() + ":");
                        EmprestimoDAO.listarMeusEmprestimosAtivos(usrDevFunc.getId());
                        System.out.print("ID do livro a ser devolvido: "); int idLDevFunc = 0;
                        if(scanner.hasNextInt()) idLDevFunc = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                        scanner.nextLine(); EmprestimoDAO.devolverLivro(idLDevFunc, usrDevFunc.getId());
                    }
                    break;
                case 5: EmprestimoDAO.listarTodosEmprestimosAtivos(); break;
                case 6:
                    System.out.println("--- Reservar Livro para Usuário ---");
                    System.out.print("Pesquisar livro (título/autor): "); String termoLResFunc = scanner.nextLine();
                    LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoLResFunc);
                    System.out.print("ID do livro para reservar: "); int idLResFunc = 0;
                    if(scanner.hasNextInt()) idLResFunc = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                    scanner.nextLine();
                    System.out.print("Pesquisar usuário (nome/RA) para reserva: "); String termoUResFunc = scanner.nextLine();
                    UsuarioDAO.pesquisarEListarUsuarios(termoUResFunc);
                    System.out.print("\nRA do usuário para reserva: "); String raUResFunc = scanner.nextLine();
                    Usuario usrResFunc = UsuarioDAO.buscarUsuarioPorRA(raUResFunc);
                    if (usrResFunc == null) System.out.println("Usuário RA " + raUResFunc + " não encontrado.");
                    else ReservaDAO.criarReserva(idLResFunc, usrResFunc.getId());
                    break;
                case 7: UsuarioDAO.listarUsuarios(); break;
                case 8: System.out.println("Saindo do menu funcionário..."); break;
                default: if (opcao != 0) System.out.println("Opção inválida.");
            }
        } while (opcao != 8);
    }

    private static void menuUsuarioComum(Scanner scanner, Usuario usuarioLogado) { /* ... como na última versão ... */
        int opcao;
        do {
            System.out.println("\n==== Menu Usuário (" + usuarioLogado.getNome() + ") ====");
            System.out.println("1. Listar Todos os Livros Físicos");
            System.out.println("2. Pesquisar Livros Físicos");
            System.out.println("3. Reservar Livro");
            System.out.println("4. Ver Minhas Reservas Ativas");
            System.out.println("5. Ver Meus Empréstimos Ativos");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            if (scanner.hasNextInt()) { opcao = scanner.nextInt(); }
            else { System.out.println("Opção inválida."); scanner.nextLine(); opcao = 0; }
            scanner.nextLine();
            switch (opcao) {
                case 1: LivroFisico.listarLivrosFisicosComDisponibilidade(); break;
                case 2: System.out.print("Termo de pesquisa: "); LivroFisico.pesquisarLivrosFisicosComDisponibilidade(scanner.nextLine()); break;
                case 3:
                    System.out.print("Pesquisar livro (título/autor): "); String termoLResC = scanner.nextLine();
                    LivroFisico.pesquisarLivrosFisicosComDisponibilidade(termoLResC);
                    System.out.print("ID do livro para reservar: "); int idLResC = 0;
                    if(scanner.hasNextInt()) idLResC = scanner.nextInt(); else {System.out.println("ID inválido."); scanner.nextLine(); break;}
                    scanner.nextLine(); ReservaDAO.criarReserva(idLResC, usuarioLogado.getId());
                    break;
                case 4: ReservaDAO.consultarReservasPorUsuario(usuarioLogado.getId()); break;
                case 5: EmprestimoDAO.listarMeusEmprestimosAtivos(usuarioLogado.getId()); break;
                case 6: System.out.println("Saindo do menu usuário..."); break;
                default: if (opcao != 0) System.out.println("Opção inválida.");
            }
        } while (opcao != 6);
    }
}