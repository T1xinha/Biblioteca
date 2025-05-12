package Conexao;

public class Usuario {

    private int id;
    private String nome;
    private String tipoUsuario; // "comum" ou "administrador"

    // Construtor
    public Usuario(int id, String nome, String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.tipoUsuario = tipoUsuario;
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

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    // Método para exibir as informações do usuário
    public void exibirInformacoes() {
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        System.out.println("Tipo de Usuário: " + tipoUsuario);
    }

    // Método para adicionar um novo usuário ao sistema
    public static void adicionarUsuario(int id, String nome, String tipoUsuario) {
        // Aqui você pode adicionar lógica para adicionar o usuário ao banco de dados
        System.out.println("Usuário " + nome + " do tipo " + tipoUsuario + " adicionado com sucesso!");
    }

    // Método para listar todos os usuários (se houver um banco de dados, seria necessário buscar os usuários)
    public static void listarUsuarios() {
        // Aqui você pode adicionar a lógica para buscar os usuários do banco de dados
        System.out.println("Listando todos os usuários...");
        // Exemplo de exibição
        System.out.println("ID: 1, Nome: João, Tipo: Comum");
        System.out.println("ID: 2, Nome: Maria, Tipo: Administrador");
    }
}
