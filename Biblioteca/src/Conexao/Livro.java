package Conexao;

import java.sql.*;

public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private boolean disponivel;

    // Construtor
    public Livro(int id, String titulo, String autor, boolean disponivel) {
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

    // Método para adicionar um livro no banco de dados
    public static void adicionarLivro(String titulo, String autor) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.conectar();
            String sql = "INSERT INTO livros (titulo, autor, disponivel) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setBoolean(3, true); // Livro é disponível por padrão
            stmt.executeUpdate();
            System.out.println("Livro adicionado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // Método para listar todos os livros no banco de dados
    public static void listarLivros() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM livros";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                boolean disponivel = rs.getBoolean("disponivel");

                System.out.println("ID: " + id + ", Título: " + titulo + ", Autor: " + autor + ", Disponível: " + disponivel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, rs);
        }
    }

    // Método para pesquisar livros no banco de dados
    public static void pesquisar(String termo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            String sql = "SELECT * FROM livros WHERE titulo LIKE ? OR autor LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + termo + "%");
            stmt.setString(2, "%" + termo + "%");
            rs = stmt.executeQuery();

            boolean encontrado = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                boolean disponivel = rs.getBoolean("disponivel");

                System.out.println("ID: " + id + ", Título: " + titulo + ", Autor: " + autor + ", Disponível: " + disponivel);
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

    // Método para fechar a conexão com o banco de dados
    private static void fecharConexao(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}

