package Conexao;

public class LivroBase {

    private int id;
    private String titulo;
    private String autor;
    private boolean disponivel;

    // Construtor
    public LivroBase(int id, String titulo, String autor, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = disponivel;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    // Método para exibir as informações do livro
    public void exibirInformacoes() {
        System.out.println("ID: " + id);
        System.out.println("Título: " + titulo);
        System.out.println("Autor: " + autor);
        System.out.println("Disponível: " + (disponivel ? "Sim" : "Não"));
    }

    // Método para adicionar um livro à base de dados (será sobrescrito nas subclasses)
    public static void adicionarLivro(String titulo, String autor) {
        // Este método será sobrescrito nas subclasses para fornecer implementação específica
    }
}
