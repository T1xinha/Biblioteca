package Conexao;

public class Administrador extends Funcionario {

    // Construtor
    public Administrador(int id, String nome, String tipoUsuario, String cargo, String departamento) {
        super(id, nome, tipoUsuario, cargo, departamento); // Chama o construtor da classe pai (Funcionario)
    }

    // Método para exibir as informações do administrador (inclui as informações do funcionário)
    @Override
    public void exibirInformacoes() {
        super.exibirInformacoes(); // Chama o método exibirInformacoes() da classe pai (Funcionario)
        System.out.println("Este usuário é um Administrador.");
    }

    // Método para adicionar um livro no sistema (exemplo de permissão exclusiva do administrador)
    public static void adicionarLivro(String titulo, String autor) {
        // Aqui você pode adicionar a lógica para adicionar o livro ao banco de dados
        System.out.println("Administrador adicionou o livro: " + titulo + " de " + autor);
    }

    // Método para remover um livro do sistema (exemplo de permissão exclusiva do administrador)
    public static void removerLivro(String titulo) {
        // Aqui você pode adicionar a lógica para remover o livro do banco de dados
        System.out.println("Administrador removeu o livro: " + titulo);
    }

    // Método para listar todos os livros (exemplo de ação administrativa)
    public static void listarLivros() {
        // Aqui você pode adicionar a lógica para buscar os livros no banco de dados
        System.out.println("Listando todos os livros...");
        // Exemplo de exibição
        System.out.println("Título: O Senhor dos Anéis, Autor: J.R.R. Tolkien");
        System.out.println("Título: Harry Potter, Autor: J.K. Rowling");
    }
}
