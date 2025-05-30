package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class LivroFisico extends LivroBase {

    public LivroFisico(int id, String titulo, String autor, boolean disponivel, String TOPICOS, String categoria,
                       int anoPublicacao, String codLivro, String codInterno, int quantidade) { // Adicionado quantidade
        super(id, titulo, autor, disponivel, TOPICOS, categoria, anoPublicacao, codLivro, codInterno, quantidade); // Passa quantidade
    }

    public static final List<String> CATEGORIAS_VALIDAS = Arrays.asList(
            "História", "Ciências", "Fantasia", "Literatura", "Tecnologia"
    );

    // --- MÉTODO ADICIONAR LIVRO FÍSICO (como na última versão) ---
    public static void adicionarLivroFisico(String titulo, String autor, String TOPICOS, String categoria,
                                            int anoPublicacao, String codLivro, String codInterno) {
        if (!CATEGORIAS_VALIDAS.contains(categoria)) {
            System.out.println("Erro: Categoria fornecida ('" + categoria + "') não é uma categoria válida.");
            System.out.println("Operação de adição de livro cancelada. Categorias válidas: " + CATEGORIAS_VALIDAS);
            return;
        }
        Connection conn = null; PreparedStatement stmt = null; ResultSet rs = null;
        String sqlSelect = "SELECT id, quantidade FROM livro WHERE LOWER(titulo) = LOWER(?) AND LOWER(autor) = LOWER(?)";
        String sqlUpdate = "UPDATE livro SET quantidade = ?, TOPICOS = ?, categoria = ?, ano_publicacao = ?, cod_livro = ?, cod_interno = ? WHERE id = ?";
        String sqlInsert = "INSERT INTO livro (titulo, autor, TOPICOS, categoria, ano_publicacao, cod_livro, cod_interno, quantidade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = Conexao.conectar();
            stmt = conn.prepareStatement(sqlSelect);
            stmt.setString(1, titulo.toLowerCase()); stmt.setString(2, autor.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                int idExistente = rs.getInt("id"); int quantidadeAtual = rs.getInt("quantidade");
                try { if (rs != null && !rs.isClosed()) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (stmt != null && !stmt.isClosed()) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                stmt = conn.prepareStatement(sqlUpdate);
                stmt.setInt(1, quantidadeAtual + 1); stmt.setString(2, TOPICOS); stmt.setString(3, categoria);
                stmt.setInt(4, anoPublicacao); stmt.setString(5, codLivro); stmt.setString(6, codInterno);
                stmt.setInt(7, idExistente);
                if (stmt.executeUpdate() > 0) {
                    System.out.println("Quantidade do livro '" + titulo + "' atualizada para " + (quantidadeAtual + 1) + ". Outras informações também podem ter sido atualizadas.");
                } else { System.out.println("Falha ao atualizar livro existente."); }
            } else {
                try { if (rs != null && !rs.isClosed()) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (stmt != null && !stmt.isClosed()) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                stmt = conn.prepareStatement(sqlInsert);
                stmt.setString(1, titulo); stmt.setString(2, autor); stmt.setString(3, TOPICOS);
                stmt.setString(4, categoria); stmt.setInt(5, anoPublicacao); stmt.setString(6, codLivro);
                stmt.setString(7, codInterno); stmt.setInt(8, 1);
                if (stmt.executeUpdate() > 0) {
                    System.out.println("Livro físico '" + titulo + "' adicionado com quantidade 1.");
                } else { System.out.println("Falha ao adicionar novo livro."); }
            }
        } catch (SQLException e) { System.out.println("Erro de SQL ao adicionar/atualizar livro: " + e.getMessage()); e.printStackTrace();
        } finally {
            try { if (rs != null && !rs.isClosed()) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null && !stmt.isClosed()) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // --- MÉTODOS removerLivroPorId, listarLivrosFisicosComDisponibilidade, pesquisarLivrosFisicosComDisponibilidade,
    // getQuantidadeDisponivelLivro, getTituloLivroPorId - COMO NA ÚLTIMA VERSÃO CORRIGIDA ---
    // (Omitidos aqui por brevidade, mas devem ser mantidos)
    public static void removerLivroPorId(int id) { /* ...código anterior... */
        String sql = "DELETE FROM livro WHERE id = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() > 0) System.out.println("Livro ID " + id + " removido.");
            else System.out.println("Livro ID " + id + " não encontrado para remoção.");
        } catch (SQLException e) { System.out.println("Erro ao remover livro: " + e.getMessage()); e.printStackTrace(); }
    }

    public static void listarLivrosFisicosComDisponibilidade() { /* ...código anterior... */
        String sql = """
            SELECT l.id, l.titulo, l.autor, l.topicos, l.categoria, l.ano_publicacao,
                   l.cod_livro, l.cod_interno, l.quantidade,
                   COALESCE(SUM(CASE WHEN e.devolvido = FALSE THEN 1 ELSE 0 END), 0) AS quantidade_emprestada
            FROM livro l LEFT JOIN emprestimo e ON l.id = e.livro_id
            GROUP BY l.id, l.titulo, l.autor, l.topicos, l.categoria, l.ano_publicacao, l.cod_livro, l.cod_interno, l.quantidade
            ORDER BY l.titulo
            """;
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            System.out.println("\n=========================== LISTA DE LIVROS FÍSICOS ===========================");
            System.out.printf("%-5s %-35s %-25s %-18s %-12s %-10s\n", "ID", "Título", "Autor", "Categoria", "Disponíveis", "Total");
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                int id = rs.getInt("id"); String titulo = rs.getString("titulo"); String autor = rs.getString("autor");
                String categoria = rs.getString("categoria"); int quantidadeTotal = rs.getInt("quantidade");
                int quantidadeEmprestada = rs.getInt("quantidade_emprestada");
                int quantidadeDisponivel = quantidadeTotal - quantidadeEmprestada;
                System.out.printf("%-5d %-35.35s %-25.25s %-18.18s %-12d %-10d\n", id, titulo, autor, categoria, quantidadeDisponivel, quantidadeTotal);
            }
            if (!encontrado) System.out.println("Nenhum livro encontrado no catálogo.");
            System.out.println("===========================================================================================================\n");
        } catch (SQLException e) { System.out.println("Erro ao listar livros: " + e.getMessage()); e.printStackTrace(); }
    }

    public static void pesquisarLivrosFisicosComDisponibilidade(String termo) { /* ...código anterior... */
        String termoPesquisa = "%" + termo.toLowerCase() + "%";
        String sql = """
            SELECT l.id, l.titulo, l.autor, l.topicos, l.categoria, l.ano_publicacao,
                   l.cod_livro, l.cod_interno, l.quantidade,
                   COALESCE(SUM(CASE WHEN e.devolvido = FALSE THEN 1 ELSE 0 END), 0) AS quantidade_emprestada
            FROM livro l LEFT JOIN emprestimo e ON l.id = e.livro_id
            WHERE LOWER(l.titulo) LIKE ? OR LOWER(l.autor) LIKE ? OR LOWER(l.categoria) LIKE ? OR LOWER(l.topicos) LIKE ?
            GROUP BY l.id, l.titulo, l.autor, l.topicos, l.categoria, l.ano_publicacao, l.cod_livro, l.cod_interno, l.quantidade
            ORDER BY l.titulo
            """;
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, termoPesquisa); stmt.setString(2, termoPesquisa);
            stmt.setString(3, termoPesquisa); stmt.setString(4, termoPesquisa);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n======================= RESULTADO DA PESQUISA DE LIVROS FÍSICOS =======================");
                System.out.printf("%-5s %-35s %-25s %-18s %-12s %-10s\n", "ID", "Título", "Autor", "Categoria", "Disponíveis", "Total");
                System.out.println("-----------------------------------------------------------------------------------------------------------");
                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    int id = rs.getInt("id"); String titulo = rs.getString("titulo"); String autor = rs.getString("autor");
                    String categoria = rs.getString("categoria"); int quantidadeTotal = rs.getInt("quantidade");
                    int quantidadeEmprestada = rs.getInt("quantidade_emprestada");
                    int quantidadeDisponivel = quantidadeTotal - quantidadeEmprestada;
                    System.out.printf("%-5d %-35.35s %-25.25s %-18.18s %-12d %-10d\n", id, titulo, autor, categoria, quantidadeDisponivel, quantidadeTotal);
                }
                if (!encontrado) System.out.println("Nenhum livro encontrado para o termo: '" + termo + "'");
                System.out.println("===========================================================================================================\n");
            }
        } catch (SQLException e) { System.out.println("Erro ao pesquisar livros: " + e.getMessage()); e.printStackTrace(); }
    }

    public static int getQuantidadeDisponivelLivro(int livroId) { /* ...código anterior... */
        int quantidadeDisponivel = -1;
        String sql = """
            SELECT (l.quantidade - COALESCE(SUM(CASE WHEN e.devolvido = FALSE THEN 1 ELSE 0 END), 0)) AS disponivel
            FROM livro l LEFT JOIN emprestimo e ON l.id = e.livro_id
            WHERE l.id = ? GROUP BY l.id, l.quantidade;
            """;
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) quantidadeDisponivel = rs.getInt("disponivel");
                else System.out.println("Aviso: Livro ID " + livroId + " não encontrado para verificar disponibilidade para reserva.");
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar qtd disponível do livro ID " + livroId + ": " + e.getMessage()); e.printStackTrace(); }
        return quantidadeDisponivel;
    }

    public static String getTituloLivroPorId(int livroId, Connection conn) { /* ...código anterior... */
        String sql = "SELECT titulo FROM livro WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("titulo");
            }
        } catch (SQLException e) { System.err.println("Erro ao buscar título do livro por ID " + livroId + ": " + e.getMessage()); }
        return null;
    }

    // NOVO MÉTODO PARA BUSCAR LIVRO COMPLETO (INCLUINDO QUANTIDADE) PARA EDIÇÃO
    public static LivroFisico buscarLivroCompletoPorId(int livroId) {
        String sql = "SELECT * FROM livro WHERE id = ?";
        LivroFisico livro = null;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int quantidade = rs.getInt("quantidade");
                    livro = new LivroFisico(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            (quantidade > 0), // 'disponivel' baseado na quantidade
                            rs.getString("TOPICOS"),
                            rs.getString("categoria"),
                            rs.getInt("ano_publicacao"),
                            rs.getString("cod_livro"),
                            rs.getString("cod_interno"),
                            quantidade // Passando a quantidade para o construtor
                    );
                } else {
                    System.out.println("Livro com ID " + livroId + " não encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar livro completo por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return livro;
    }

    // NOVO MÉTODO PARA ATUALIZAR DETALHES DO LIVRO
    public static boolean atualizarDetalhesLivro(int livroId, String novoTitulo, String novoAutor,
                                                 String novosTopicos, String novaCategoria, int novoAno,
                                                 String novoCodLivro, String novoCodInterno, int novaQuantidade) {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, TOPICOS = ?, categoria = ?, " +
                "ano_publicacao = ?, cod_livro = ?, cod_interno = ?, quantidade = ? WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoTitulo);
            stmt.setString(2, novoAutor);
            stmt.setString(3, novosTopicos);
            stmt.setString(4, novaCategoria);
            stmt.setInt(5, novoAno);
            stmt.setString(6, novoCodLivro);
            stmt.setString(7, novoCodInterno);
            stmt.setInt(8, novaQuantidade);
            stmt.setInt(9, livroId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro ID " + livroId + ": " + e.getMessage());
            if (e.getErrorCode() == 1062) { // Código de erro do MySQL para entrada duplicada
                System.out.println("Falha na atualização: Novo Código do Livro ou Código Interno já existe para outro livro.");
            }
            e.printStackTrace();
            return false;
        }
    }
}