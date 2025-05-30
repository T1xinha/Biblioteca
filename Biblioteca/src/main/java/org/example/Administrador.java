package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// Scanner seria necessário se fôssemos adicionar lógica de input aqui
// import java.util.Scanner;

public class Administrador extends Usuario {

    public Administrador(int id, String nome, String login) {
        super(id, nome, login);
    }

    @Override
    public void exibirInformacoes() {
        super.exibirInformacoes();
        System.out.println("Este usuário é um Administrador.");
    }

    // Método atualizado para refletir a adição de um LivroFisico completo.
    // Nota: A coleta dos dados (tópicos, categoria, etc.) precisaria ocorrer
    // antes de chamar este método, ou este método precisaria de um Scanner
    // para coletá-los internamente, similar ao que foi feito no Main.
    public static void adicionarLivro(String titulo, String autor, String topicos, String categoria,
                                      int anoPublicacao, String codLivro, String codInterno) {
        // Chama o método centralizado em LivroFisico para adicionar o livro
        LivroFisico.adicionarLivroFisico(titulo, autor, topicos, categoria, anoPublicacao, codLivro, codInterno);
        // A mensagem de sucesso/erro já é tratada por LivroFisico.adicionarLivroFisico()
    }

    // Método para remover livro por ID (mantido como está, funcional)
    public static void removerLivroPorId(int idLivro) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.conectar();
            String sql = "DELETE FROM livro WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idLivro);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Livro com ID " + idLivro + " removido com sucesso.");
            } else {
                System.out.println("Nenhum livro encontrado com o ID fornecido.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao remover o livro: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

    // Método atualizado: listar livros com ID, título, autor e disponibilidade/quantidade
    public static void listarLivros() {
        // Chama o método centralizado em LivroFisico que lida com a listagem detalhada
        LivroFisico.listarLivrosFisicosComDisponibilidade();
    }
}