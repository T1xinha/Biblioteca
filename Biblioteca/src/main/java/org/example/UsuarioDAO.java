package org.example;

import java.sql.*;

public class UsuarioDAO {

    // ... (MÉTODOS autenticar, cadastrarUsuario, cadastrarUsuarioComRA, removerUsuarioPorId,
    //      listarUsuarios, buscarUsuarioPorRA, quantidadeUsuarios, pesquisarEListarUsuarios,
    //      buscarUsuarioPorId - COMO ESTAVAM NA ÚLTIMA VERSÃO CORRIGIDA) ...
    // (Omitidos aqui por brevidade, mas devem ser mantidos)
    public static Usuario autenticar(String login, String senha) { /* ...código anterior... */
        String sql = "SELECT id, nome, login, tipo_usuario FROM usuario WHERE login = ? AND senha = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login); stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id"); String nome = rs.getString("nome");
                    String loginDb = rs.getString("login"); String tipoUsuario = rs.getString("tipo_usuario");
                    switch (tipoUsuario.toLowerCase()) {
                        case "administrador": return new Administrador(id, nome, loginDb);
                        case "funcionario": return new Funcionario(id, nome, loginDb);
                        default: return new Cliente(id, nome, loginDb);
                    }
                } else { System.out.println("Login ou senha incorretos."); }
            }
        } catch (SQLException e) { System.out.println("Erro na autenticação: " + e.getMessage()); e.printStackTrace(); }
        return null;
    }
    public static boolean cadastrarUsuario(String nome, String login, String senha, String tipoUsuario) { /* ...código anterior... */
        String ra = String.valueOf(System.currentTimeMillis()); if (ra.length() > 7) ra = ra.substring(ra.length() - 7);
        String sql = "INSERT INTO usuario (nome, login, senha, tipo_usuario, ra) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome); stmt.setString(2, login); stmt.setString(3, senha); stmt.setString(4, tipoUsuario); stmt.setString(5, ra);
            if (stmt.executeUpdate() > 0) return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) System.out.println("Erro ao cadastrar: Login ou RA já existe.");
            else System.out.println("Erro ao cadastrar usuário: " + e.getMessage()); e.printStackTrace();
        } return false;
    }
    public static boolean cadastrarUsuarioComRA(String nome, String login, String senha, String tipoUsuario, String ra) { /* ...código anterior... */
        String sql = "INSERT INTO usuario (nome, login, senha, tipo_usuario, ra) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome); stmt.setString(2, login); stmt.setString(3, senha); stmt.setString(4, tipoUsuario); stmt.setString(5, ra);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) System.out.println("Erro ao cadastrar com RA: Login ou RA já existe.");
            else System.out.println("Erro ao cadastrar usuário com RA: " + e.getMessage()); e.printStackTrace();
        } return false;
    }
    public static void removerUsuarioPorId(int id) { /* ...código anterior com tratamento de FK... */
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() > 0) {
                System.out.println("Usuário ID " + id + " removido com sucesso.");
                System.out.println("Se este usuário possuía histórico de empréstimos, esses registros foram anonimizados (usuário_id definido como NULO).");
            } else System.out.println("Nenhum usuário encontrado com o ID " + id + " para remoção.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || e.getErrorCode() == 1217) {
                System.out.println("ERRO: Não é possível remover este usuário. Motivo: O usuário ainda é referenciado em outras partes do sistema (que não foram configuradas para ON DELETE SET NULL).");
            } else System.out.println("Erro ao remover usuário ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void listarUsuarios() { /* ...código anterior... */
        String sql = "SELECT id, nome, login, tipo_usuario, ra FROM usuario ORDER BY nome";
        try (Connection conn = Conexao.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n============================= LISTA DE USUÁRIOS =============================");
            System.out.printf("%-5s %-10s %-30s %-20s %-15s\n", "ID", "RA", "Nome", "Login", "Tipo");
            System.out.println("---------------------------------------------------------------------------------");
            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                System.out.printf("%-5d %-10s %-30.30s %-20.20s %-15s\n", rs.getInt("id"), rs.getString("ra"), rs.getString("nome"), rs.getString("login"), rs.getString("tipo_usuario"));
            }
            if (!encontrado) System.out.println("Nenhum usuário cadastrado.");
            System.out.println("=================================================================================\n");
        } catch (SQLException e) { System.out.println("Erro ao listar usuários: " + e.getMessage()); e.printStackTrace(); }
    }
    public static Usuario buscarUsuarioPorRA(String ra) { /* ...código anterior... */
        String sql = "SELECT id, nome, login, tipo_usuario FROM usuario WHERE ra = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ra);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id"); String nome = rs.getString("nome");
                    String login = rs.getString("login"); String tipoUsuario = rs.getString("tipo_usuario");
                    switch (tipoUsuario.toLowerCase()) {
                        case "administrador": return new Administrador(id, nome, login);
                        case "funcionario": return new Funcionario(id, nome, login);
                        default: return new Cliente(id, nome, login);
                    }
                }
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar usuário pelo RA: " + e.getMessage()); e.printStackTrace(); }
        return null;
    }
    public static Usuario buscarUsuarioPorId(int idUsuario) { /* ...código anterior... */
        String sql = "SELECT id, nome, login, tipo_usuario, ra FROM usuario WHERE id = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome"); String login = rs.getString("login");
                    String tipoUsuario = rs.getString("tipo_usuario");
                    switch (tipoUsuario.toLowerCase()) {
                        case "administrador": return new Administrador(idUsuario, nome, login);
                        case "funcionario": return new Funcionario(idUsuario, nome, login);
                        default: return new Cliente(idUsuario, nome, login);
                    }
                }
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar usuário por ID: " + e.getMessage()); e.printStackTrace(); }
        return null;
    }
    public static int quantidadeUsuarios() { /* ...código anterior... */
        String sql = "SELECT COUNT(*) AS total FROM usuario";
        try (Connection conn = Conexao.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { System.out.println("Erro ao contar usuários: " + e.getMessage()); e.printStackTrace(); }
        return 0;
    }
    public static void pesquisarEListarUsuarios(String termo) { /* ...código anterior... */
        String sql = "SELECT id, nome, login, tipo_usuario, ra FROM usuario WHERE LOWER(nome) LIKE ? OR LOWER(ra) LIKE ? ORDER BY nome";
        String termoPesquisa = "%" + termo.toLowerCase() + "%";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, termoPesquisa); stmt.setString(2, termoPesquisa);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=========== RESULTADO DA PESQUISA DE USUÁRIOS ===========");
                System.out.printf("%-5s %-10s %-30s %-20s %-15s\n", "ID", "RA", "Nome", "Login", "Tipo");
                System.out.println("---------------------------------------------------------------------------------");
                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    System.out.printf("%-5d %-10s %-30.30s %-20.20s %-15s\n", rs.getInt("id"), rs.getString("ra"), rs.getString("nome"), rs.getString("login"), rs.getString("tipo_usuario"));
                }
                if (!encontrado) System.out.println("Nenhum usuário encontrado para o termo: '" + termo + "'.");
                System.out.println("=================================================================================\n");
            }
        } catch (SQLException e) { System.out.println("Erro ao pesquisar usuários: " + e.getMessage()); e.printStackTrace(); }
    }


    // NOVO MÉTODO PARA ATUALIZAR DETALHES DO USUÁRIO
    public static boolean atualizarDetalhesUsuario(int idUsuario, String novoNome, String novoLogin,
                                                   String novaSenha, String novoTipoUsuario, String novoRa) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE usuario SET nome = ?, login = ?, tipo_usuario = ?, ra = ?");
        // A senha só é atualizada se uma nova senha não vazia for fornecida
        if (novaSenha != null && !novaSenha.isEmpty()) {
            sqlBuilder.append(", senha = ?");
        }
        sqlBuilder.append(" WHERE id = ?");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            int paramIndex = 1;
            stmt.setString(paramIndex++, novoNome);
            stmt.setString(paramIndex++, novoLogin);
            stmt.setString(paramIndex++, novoTipoUsuario);
            stmt.setString(paramIndex++, novoRa);

            if (novaSenha != null && !novaSenha.isEmpty()) {
                stmt.setString(paramIndex++, novaSenha); // LEMBRETE: SENHA EM TEXTO PLANO!
            }
            stmt.setInt(paramIndex, idUsuario);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Código de erro do MySQL para entrada duplicada (UNIQUE constraint)
                System.out.println("Erro ao atualizar usuário: Novo login ou RA já existe para outro usuário.");
            } else {
                System.out.println("Erro ao atualizar dados do usuário ID " + idUsuario + ": " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    // NOVO MÉTODO PARA CONTAR ADMINISTRADORES
    public static int contarAdministradores() {
        String sql = "SELECT COUNT(*) AS total_admins FROM usuario WHERE tipo_usuario = 'administrador'";
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement(); // Statement simples para query sem parâmetros
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total_admins");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar administradores: " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Retorna 0 em caso de erro ou se não houver admins
    }
}