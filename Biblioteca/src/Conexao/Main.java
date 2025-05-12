package Conexao;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        // Menu de opções para o usuário interagir com o sistema
        do {
            System.out.println("==== Sistema de Biblioteca ====");
            System.out.println("1. Adicionar Livro");
            System.out.println("2. Listar Livros");
            System.out.println("3. Pesquisar Livros");
            System.out.println("4. Emprestar Livro");
            System.out.println("5. Devolver Livro");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha deixada pelo nextInt()

            switch (opcao) {
                case 1:
                    // Adicionar Livro
                    System.out.println("Digite o tipo de livro (1 - Físico, 2 - Digital): ");
                    int tipoLivro = scanner.nextInt();
                    scanner.nextLine(); // Consumir a quebra de linha

                    System.out.print("Digite o título do livro: ");
                    String titulo = scanner.nextLine();

                    System.out.print("Digite o autor do livro: ");
                    String autor = scanner.nextLine();

                    if (tipoLivro == 1) {
                        // Livro Físico
                        System.out.print("Digite o número de páginas: ");
                        int numPaginas = scanner.nextInt();
                        scanner.nextLine(); // Consumir a quebra de linha

                        System.out.print("Digite a localização na biblioteca: ");
                        String localizacao = scanner.nextLine();

                        LivroFisico.adicionarLivroFisico(titulo, autor, numPaginas, localizacao);
                    } else if (tipoLivro == 2) {
                        // Livro Digital
                        // Aqui você pode adicionar lógica para livros digitais
                        LivroBase.adicionarLivro(titulo, autor);
                    } else {
                        System.out.println("Tipo de livro inválido.");
                    }
                    break;

                case 2:
                    // Listar Livros
                    Livro.listarLivros();
                    break;

                case 3:
                    // Pesquisar Livros
                    System.out.print("Digite o termo de pesquisa: ");
                    String termoPesquisa = scanner.nextLine();
                    Livro.pesquisar(termoPesquisa);
                    break;

                case 4:
                    // Emprestar Livro
                    System.out.print("Digite o ID do livro para emprestar: ");
                    int idLivroEmprestimo = scanner.nextInt();
                    System.out.print("Digite o ID do usuário: ");
                    int idUsuarioEmprestimo = scanner.nextInt();
                    EmprestimoDAO.emprestarLivro(idLivroEmprestimo, idUsuarioEmprestimo);
                    break;

                case 5:
                    // Devolver Livro
                    System.out.print("Digite o ID do livro para devolver: ");
                    int idLivroDevolucao = scanner.nextInt();
                    EmprestimoDAO.devolverLivro(idLivroDevolucao);
                    break;

                case 6:
                    // Sair
                    System.out.println("Saindo do sistema...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }

        } while (opcao != 6); // O programa continua até que a opção 6 seja escolhida

        scanner.close();
    }
}
