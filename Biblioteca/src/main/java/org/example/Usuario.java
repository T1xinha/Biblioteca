package org.example;

public class Usuario {

    private int id;
    private String nome;
    private String login;

    // Construtor simplificado (sem cargo e departamento)
    public Usuario(int id, String nome, String login) {
        this.id = id;
        this.nome = nome;
        this.login = login;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    // Método para exibir informações básicas do usuário
    public void exibirInformacoes() {
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        System.out.println("Login: " + login);
    }
}
