package Conexao;

import java.sql.*;

public class EmprestimoDAO {

    // Método para emprestar um livro
    public static void emprestarLivro(int livroId, int usuarioId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.conectar();
            String sql = "INSERT INTO emprestimos (livro_id, usuario_id, data_emprestimo) VALUES (?, ?, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, livroId);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
            System.out.println("Empréstimo realizado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao realizar o empréstimo: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // Método para devolver um livro
    public static void devolverLivro(int livroId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.conectar();
            String sql = "UPDATE emprestimos SET devolvido = TRUE, data_devolucao = NOW() WHERE livro_id = ? AND devolvido = FALSE";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, livroId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Livro devolvido com sucesso.");
            } else {
                System.out.println("Este livro não está registrado como emprestado ou já foi devolvido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao devolver o livro: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // Método para fechar a conexão com o banco de dados
    private static void fecharConexao(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}
