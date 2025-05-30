package org.example;

public class Funcionario extends Usuario {

    // Removeu os atributos cargo e departamento

    // Construtor atualizado para remover cargo e departamento
    public Funcionario(int id, String nome, String login) {
        super(id, nome, login); // Usa o construtor da classe Usuario sem cargo e departamento
    }

    // Método para exibir as informações do funcionário (inclui as informações do usuário)
    @Override
    public void exibirInformacoes() {
        super.exibirInformacoes(); // Exibe id, nome, login
    }

    // Método para adicionar um novo funcionário ao sistema
    public static void adicionarFuncionario(int id, String nome, String login) {
        // Aqui você pode adicionar lógica para adicionar o funcionário ao banco de dados
        System.out.println("Funcionário " + nome + " adicionado com sucesso!");
    }

    // Método para listar todos os funcionários (poderia ser implementado para buscar do banco de dados)
    public static void listarFuncionarios() {
        // Aqui você pode adicionar a lógica para buscar os funcionários do banco de dados
        System.out.println("Listando todos os funcionários...");
        // Exemplo de exibição
        System.out.println("ID: 1, Nome: Carlos");
        System.out.println("ID: 2, Nome: Ana");
    }
}
