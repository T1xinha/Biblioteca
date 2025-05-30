package org.example;

public class Cliente extends Usuario {

    // Construtor atualizado para chamar super() sem cargo e departamento
    public Cliente(int id, String nome, String login) {
        super(id, nome, login);
    }

    // Método para exibir as informações do cliente
    @Override
    public void exibirInformacoes() {
        super.exibirInformacoes();
        System.out.println("Este usuário é um Cliente.");
    }

    // Método exemplo: visualizar livros disponíveis
    public static void visualizarLivrosDisponiveis() {
        System.out.println("Listando livros disponíveis para empréstimo...");
        // Lógica para listar livros
    }

    // Método exemplo: reservar livro
    public static void reservarLivro(int idLivro) {
        System.out.println("Cliente reservou o livro de ID: " + idLivro);
        // Lógica para reservar livro
    }
}
