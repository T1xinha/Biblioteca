package org.example;

public class LivroBase {
    private int id;
    private String titulo;
    private String autor;
    private boolean disponivel; // Este campo pode ser redundante se usarmos 'quantidade' para lógica de disponibilidade.
    private String topicos;
    private String categoria;
    private int anoPublicacao;
    private String codLivro;
    private String codInterno;
    private int quantidade; // Quantidade total em estoque

    public LivroBase(int id, String titulo, String autor, boolean disponivel,
                     String topicos, String categoria, int anoPublicacao,
                     String codLivro, String codInterno, int quantidade) { // Adicionado quantidade
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = (quantidade > 0); // Define 'disponivel' com base na quantidade
        this.topicos = topicos;
        this.categoria = categoria;
        this.anoPublicacao = anoPublicacao;
        this.codLivro = codLivro;
        this.codInterno = codInterno;
        this.quantidade = quantidade; // Atribui quantidade
    }

    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponivel() { return this.quantidade > 0; } // Reflete a disponibilidade real
    public String getTopicos() { return topicos; }
    public String getCategoria() { return categoria; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public String getCodLivro() { return codLivro; }
    public String getCodInterno() { return codInterno; }
    public int getQuantidade() { return quantidade; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; } // Pode ser removido se 'disponivel' for sempre derivado
    public void setTopicos(String topicos) { this.topicos = topicos; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }
    public void setCodLivro(String codLivro) { this.codLivro = codLivro; }
    public void setCodInterno(String codInterno) { this.codInterno = codInterno; }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        this.disponivel = (quantidade > 0); // Atualiza 'disponivel' ao mudar quantidade
    }

    public void exibirInformacoes() {
        System.out.println("ID: " + id);
        System.out.println("Título: " + titulo);
        System.out.println("Autor: " + autor);
        System.out.println("Quantidade Total em Estoque: " + quantidade);
        System.out.println("Tópicos: " + (topicos != null && !topicos.isEmpty() ? topicos : "N/A"));
        System.out.println("Categoria: " + (categoria != null && !categoria.isEmpty() ? categoria : "N/A"));
        System.out.println("Ano de Publicação: " + (anoPublicacao > 0 ? anoPublicacao : "N/A"));
        System.out.println("Código do Livro (ISBN): " + (codLivro != null && !codLivro.isEmpty() ? codLivro : "N/A"));
        System.out.println("Código Interno: " + (codInterno != null && !codInterno.isEmpty() ? codInterno : "N/A"));
    }

    public static void adicionarLivro(String titulo, String autor, String topicos, String categoria,
                                      int anoPublicacao, String codLivro, String codInterno) {
        System.out.println("Placeholder em LivroBase. Use LivroFisico.adicionarLivroFisico.");
    }
}