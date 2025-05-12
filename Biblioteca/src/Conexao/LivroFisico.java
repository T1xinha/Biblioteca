package Conexao;

public class LivroFisico extends Livro {

    private int numeroDePaginas;
    private String localizacao;

    // Construtor
    public LivroFisico(int id, String titulo, String autor, boolean disponivel, int numeroDePaginas, String localizacao) {
        super(id, titulo, autor, disponivel); // Chama o construtor da classe pai (Livro)
        this.numeroDePaginas = numeroDePaginas;
        this.localizacao = localizacao;
    }

    // Getters e Setters
    public int getNumeroDePaginas() {
        return numeroDePaginas;
    }

    public void setNumeroDePaginas(int numeroDePaginas) {
        this.numeroDePaginas = numeroDePaginas;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    // Método para exibir as informações detalhadas do livro físico
    @Override
    public void exibirInformacoes() {
        super.exibirInformacoes();  // Exibe as informações da classe pai (Livro)
        System.out.println("Número de Páginas: " + numeroDePaginas);
        System.out.println("Localização: " + localizacao);
    }

    // Método para adicionar um livro físico ao banco de dados
    public static void adicionarLivroFisico(String titulo, String autor, int numeroDePaginas, String localizacao) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.conectar();
            String sql = "INSERT INTO livros (titulo, autor, disponivel) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setBoolean(3, true);  // Livro físico é disponível por padrão
            stmt.executeUpdate();

            // Após adicionar o livro físico na tabela principal, podemos adicionar a parte específica do livro físico
            // Aqui você pode implementar uma tabela diferente para armazenar informações de livro físico, se necessário

            System.out.println("Livro físico adicionado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro físico: " + e.getMessage());
        } finally {
            Livro.fecharConexao(conn, stmt, null);
        }
    }
}
