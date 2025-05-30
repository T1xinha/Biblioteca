package org.example;

import java.sql.*;

public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private boolean disponivel;

    public Livro(int id, String titulo, String autor, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = disponivel;
    }

    // Getters e Setters omitidos por brevidade...

    public static void adicionarLivro(String titulo, String autor) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.conectar();
            String sql = "INSERT INTO livro (titulo, autor, disponivel) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setBoolean(3, true);
            stmt.executeUpdate();
            System.out.println("Livro adicionado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    public static void listarLivros() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM livro";
            rs = stmt.executeQuery(sql);

            System.out.printf("%-5s %-40s %-25s %-10s\n", "ID", "TÍTULO", "AUTOR", "DISPONÍVEL");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = ajustarTexto(rs.getString("titulo"), 40);
                String autor = ajustarTexto(rs.getString("autor"), 25);
                boolean disponivel = rs.getBoolean("disponivel");

                System.out.printf("%-5d %-40s %-25s %-10s\n", id, titulo, autor, disponivel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, rs);
        }
    }

    public static void pesquisar(String termo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            String sql = "SELECT * FROM livro WHERE titulo LIKE ? OR autor LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + termo + "%");
            stmt.setString(2, "%" + termo + "%");
            rs = stmt.executeQuery();

            boolean encontrado = false;

            System.out.printf("%-5s %-40s %-25s %-10s\n", "ID", "TÍTULO", "AUTOR", "DISPONÍVEL");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = ajustarTexto(rs.getString("titulo"), 40);
                String autor = ajustarTexto(rs.getString("autor"), 25);
                boolean disponivel = rs.getBoolean("disponivel");

                System.out.printf("%-5d %-40s %-25s %-10s\n", id, titulo, autor, disponivel);
                encontrado = true;
            }

            if (!encontrado) {
                System.out.println("Nenhum livro encontrado com o termo '" + termo + "'.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar livros: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, rs);
        }
    }

    private static void fecharConexao(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }

    // Método auxiliar para limitar e ajustar o texto (com "..." se for necessário)
    private static String ajustarTexto(String texto, int limite) {
        if (texto.length() <= limite) {
            return texto;
        } else {
            return texto.substring(0, limite - 3) + "...";
        }
    }
}
