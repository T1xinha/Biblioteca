package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmprestimoDAO {

    // MÉTODO EMPRESTARLIVROS ATUALIZADO PARA RETORNAR EmprestimoStatus
    public static EmprestimoStatus emprestarLivros(int livroId, int usuarioId) {
        Connection conn = null;
        // SQLs
        String sqlVerificaPendencias = "SELECT COUNT(*) AS total_atrasados FROM emprestimo WHERE usuario_id = ? AND devolvido = FALSE AND data_validade < CURDATE()";
        String sqlVerificaLivroDisp = "SELECT l.quantidade AS total_copias, COUNT(e.id) AS emprestimos_ativos FROM livro l LEFT JOIN emprestimo e ON l.id = e.livro_id AND e.devolvido = FALSE WHERE l.id = ? GROUP BY l.id, l.quantidade";
        String sqlVerificaUsuarioJaTemEsteLivro = "SELECT COUNT(*) AS emprestimos_usuario_livro_ativo FROM emprestimo WHERE livro_id = ? AND usuario_id = ? AND devolvido = FALSE";
        String sqlInsertEmprestimo = "INSERT INTO emprestimo (livro_id, usuario_id, data_emprestimo, data_validade, devolvido, multa) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), FALSE, 0.00)";

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            // 0. Verificar pendências de atraso do usuário
            try (PreparedStatement stmtPendencias = conn.prepareStatement(sqlVerificaPendencias)) {
                stmtPendencias.setInt(1, usuarioId);
                try (ResultSet rsPendencias = stmtPendencias.executeQuery()) {
                    if (rsPendencias.next() && rsPendencias.getInt("total_atrasados") > 0) {
                        System.out.println("Erro: Usuário ID " + usuarioId + " possui empréstimos atrasados. Regularize a situação.");
                        conn.rollback();
                        return EmprestimoStatus.ERRO_USUARIO_COM_PENDENCIAS;
                    }
                }
            }

            // A. Verificar se este livro está 'AGUARDANDO_RETIRADA' para este usuário
            Reserva reservaParaEsteUsuario = ReservaDAO.buscarReservaAguardandoRetirada(livroId, usuarioId, conn);
            if (reservaParaEsteUsuario != null) {
                System.out.println("Processando retirada de livro reservado para usuário ID " + usuarioId + "...");
                try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertEmprestimo)) {
                    stmtInsert.setInt(1, livroId);
                    stmtInsert.setInt(2, usuarioId);
                    stmtInsert.executeUpdate();
                }
                ReservaDAO.marcarReservaComoAtendida(reservaParaEsteUsuario.getId(), conn);
                LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(10);
                System.out.println("Empréstimo do livro reservado realizado com sucesso. Data prevista para devolução: " + dataDevolucaoPrevista + ".");
                conn.commit();
                return EmprestimoStatus.SUCESSO_RESERVA_ATENDIDA;
            }

            // B. Verificar se este livro está 'AGUARDANDO_RETIRADA' para OUTRO usuário
            Reserva reservaParaOutro = ReservaDAO.buscarReservaAguardandoRetiradaParaOutroUsuario(livroId, usuarioId, conn);
            if (reservaParaOutro != null) {
                Usuario reservador = UsuarioDAO.buscarUsuarioPorId(reservaParaOutro.getUsuarioId());
                String nomeReservador = (reservador != null) ? reservador.getNome() : "ID " + reservaParaOutro.getUsuarioId();
                String dataLimiteStr = reservaParaOutro.getDataLimiteRetiradaAsLocalDate() != null ? reservaParaOutro.getDataLimiteRetiradaAsLocalDate().toString() : "N/A";
                System.out.println("Erro: Este livro está retido para o usuário: " + nomeReservador + " (Reserva ID: " + reservaParaOutro.getId() + ").");
                System.out.println("Data limite para retirada: " + dataLimiteStr);
                conn.rollback();
                return EmprestimoStatus.ERRO_LIVRO_RESERVADO_OUTRO_USUARIO;
            }

            // Se não há reserva 'AGUARDANDO_RETIRADA' relevante, prosseguir com lógica normal:
            try (PreparedStatement stmtLivroDisp = conn.prepareStatement(sqlVerificaLivroDisp)) {
                stmtLivroDisp.setInt(1, livroId);
                try (ResultSet rsLivroDisp = stmtLivroDisp.executeQuery()) {
                    if (rsLivroDisp.next()) {
                        int quantidadeTotal = rsLivroDisp.getInt("total_copias");
                        int emprestimosAtivos = rsLivroDisp.getInt("emprestimos_ativos");
                        int quantidadeDisponivelGeral = quantidadeTotal - emprestimosAtivos;

                        if (quantidadeDisponivelGeral > 0) {
                            try (PreparedStatement stmtUsuarioTemLivro = conn.prepareStatement(sqlVerificaUsuarioJaTemEsteLivro)) {
                                stmtUsuarioTemLivro.setInt(1, livroId);
                                stmtUsuarioTemLivro.setInt(2, usuarioId);
                                try (ResultSet rsUsuarioTemLivro = stmtUsuarioTemLivro.executeQuery()) {
                                    if (rsUsuarioTemLivro.next() && rsUsuarioTemLivro.getInt("emprestimos_usuario_livro_ativo") > 0) {
                                        System.out.println("Erro: Usuário ID " + usuarioId + " já possui um exemplar deste livro (ID: " + livroId + ") emprestado e não devolvido.");
                                        conn.rollback();
                                        return EmprestimoStatus.ERRO_USUARIO_JA_POSSUI_LIVRO;
                                    } else {
                                        try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertEmprestimo)) {
                                            stmtInsert.setInt(1, livroId);
                                            stmtInsert.setInt(2, usuarioId);
                                            stmtInsert.executeUpdate();
                                        }
                                        LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(10);
                                        System.out.println("Empréstimo realizado com sucesso. Data prevista para devolução: " + dataDevolucaoPrevista + ".");
                                        conn.commit();
                                        return EmprestimoStatus.SUCESSO;
                                    }
                                }
                            }
                        } else {
                            System.out.println("Informação: Livro ID " + livroId + " não está disponível para empréstimo no momento (sem cópias disponíveis).");
                            conn.rollback();
                            return EmprestimoStatus.ERRO_LIVRO_INDISPONIVEL;
                        }
                    } else {
                        System.out.println("Erro: Livro com ID " + livroId + " não encontrado no catálogo.");
                        conn.rollback();
                        return EmprestimoStatus.ERRO_LIVRO_NAO_ENCONTRADO;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro geral ao realizar o empréstimo ou verificar reservas: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return EmprestimoStatus.ERRO_GERAL_EMPRESTIMO;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); if (!conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public static void devolverLivro(int livroId, int usuarioId) {
        Connection conn = null;
        PreparedStatement stmtSelectEmprestimo = null;
        ResultSet rsEmprestimo = null;
        PreparedStatement stmtUpdateEmprestimo = null;
        double multaCalculada = 0.00;
        boolean estavaAtrasado = false;
        long diasAtraso = 0;

        String sqlSelect = "SELECT data_validade FROM emprestimo WHERE livro_id = ? AND usuario_id = ? AND devolvido = FALSE";
        String sqlUpdate = "UPDATE emprestimo SET devolvido = TRUE, data_devolucao = CURDATE(), multa = ? WHERE livro_id = ? AND usuario_id = ? AND devolvido = FALSE";

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            stmtSelectEmprestimo = conn.prepareStatement(sqlSelect);
            stmtSelectEmprestimo.setInt(1, livroId);
            stmtSelectEmprestimo.setInt(2, usuarioId);
            rsEmprestimo = stmtSelectEmprestimo.executeQuery();

            if (rsEmprestimo.next()) {
                Date dataValidadeSQL = rsEmprestimo.getDate("data_validade");
                LocalDate dataValidadeLD = dataValidadeSQL.toLocalDate();
                LocalDate hojeLD = LocalDate.now();

                if (hojeLD.isAfter(dataValidadeLD)) {
                    diasAtraso = ChronoUnit.DAYS.between(dataValidadeLD, hojeLD);
                    if (diasAtraso > 0) {
                        multaCalculada = diasAtraso * 1.00;
                        estavaAtrasado = true;
                    }
                }

                stmtUpdateEmprestimo = conn.prepareStatement(sqlUpdate);
                stmtUpdateEmprestimo.setDouble(1, multaCalculada);
                stmtUpdateEmprestimo.setInt(2, livroId);
                stmtUpdateEmprestimo.setInt(3, usuarioId);

                int rowsAffected = stmtUpdateEmprestimo.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Livro devolvido com sucesso.");
                    if (estavaAtrasado) {
                        System.out.printf("Foi aplicada uma multa de R$ %.2f por %d dia(s) de atraso.\n", multaCalculada, diasAtraso);
                    } else {
                        System.out.println("Livro devolvido dentro do prazo.");
                    }

                    ReservaDAO.processarProximaReservaAposDevolucao(livroId, conn);

                    conn.commit();
                } else {
                    System.out.println("Não foi possível processar a devolução. Empréstimo não encontrado, já devolvido ou dados inconsistentes.");
                    conn.rollback();
                }
            } else {
                System.out.println("Nenhum empréstimo ativo encontrado para este livro e usuário para devolução.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao processar devolução do livro ou ao atender reserva: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Realizando rollback da transação devido a erro SQL.");
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao tentar reverter a transação: " + ex.getMessage());
                }
            }
        } finally {
            try { if (rsEmprestimo != null && !rsEmprestimo.isClosed()) rsEmprestimo.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtSelectEmprestimo != null && !stmtSelectEmprestimo.isClosed()) stmtSelectEmprestimo.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtUpdateEmprestimo != null && !stmtUpdateEmprestimo.isClosed()) stmtUpdateEmprestimo.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try { conn.setAutoCommit(true); if (!conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public static void listarMeusEmprestimosAtivos(int usuarioId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = """
            SELECT
                e.livro_id,
                l.titulo,
                l.autor,
                e.data_emprestimo,
                e.data_validade,
                e.multa AS valor_multa_registrada,
                (CASE
                    WHEN e.devolvido = FALSE AND e.data_validade < CURDATE() THEN 'Sim (Atrasado)'
                    ELSE 'Não (No Prazo)'
                END) AS status_atraso
            FROM emprestimo e
            JOIN livro l ON e.livro_id = l.id
            WHERE e.usuario_id = ? AND e.devolvido = FALSE
            ORDER BY e.data_validade ASC;
            """;
        try {
            conn = Conexao.conectar();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            rs = stmt.executeQuery();

            System.out.println("\n========================================= MEUS EMPRÉSTIMOS ATIVOS =========================================");
            System.out.printf("%-8s %-30s %-25s %-15s %-15s %-10s %-15s\n",
                    "ID Livro", "Título", "Autor", "Data Empréstimo", "Data Validade", "Multa (R$)", "Status");
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                int livroId = rs.getInt("livro_id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String dataEmprestimo = rs.getDate("data_emprestimo") != null ? rs.getDate("data_emprestimo").toString() : "N/A";
                String dataValidade = rs.getDate("data_validade") != null ? rs.getDate("data_validade").toString() : "N/A";
                double valorMulta = rs.getDouble("valor_multa_registrada");
                String statusAtraso = rs.getString("status_atraso");

                System.out.printf("%-8d %-30.30s %-25.25s %-15s %-15s %-10.2f %-15s\n",
                        livroId, titulo, autor, dataEmprestimo, dataValidade, valorMulta, statusAtraso);
            }
            if (!encontrado) {
                System.out.println("Você não possui livros emprestados no momento.");
            }
            System.out.println("=================================================================================================================\n");
        } catch (SQLException e) {
            System.out.println("Erro ao listar seus empréstimos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null && !rs.isClosed()) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null && !stmt.isClosed()) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void listarTodosEmprestimosAtivos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = """
            SELECT
                l.id AS livro_id,
                l.titulo AS livro_titulo,
                u.id AS usuario_id,
                u.nome AS usuario_nome,
                u.ra AS usuario_ra,
                e.data_emprestimo,
                e.data_validade,
                e.multa AS valor_da_multa,
                (CASE
                    WHEN e.devolvido = FALSE AND e.data_validade < CURDATE() THEN 'Sim (Atrasado)'
                    WHEN e.devolvido = FALSE AND e.data_validade >= CURDATE() THEN 'Não (No Prazo)'
                    ELSE 'N/A (Status Inválido)'
                END) AS status_atraso_multa
            FROM
                emprestimo e
            JOIN
                livro l ON e.livro_id = l.id
            LEFT JOIN /* MUDANÇA IMPORTANTE AQUI PARA LIDAR COM USUÁRIOS REMOVIDOS (usuario_id = NULL) */
                usuario u ON e.usuario_id = u.id
            WHERE
                e.devolvido = FALSE
            ORDER BY
                e.data_validade ASC, u.nome ASC;
            """;
        try {
            conn = Conexao.conectar();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            System.out.println("\n============================================= LISTA DE TODOS OS EMPRÉSTIMOS ATIVOS =============================================");
            System.out.printf("%-8s %-30s %-8s %-20s %-10s %-15s %-15s %-10s %-18s\n",
                    "ID Livro", "Título do Livro", "ID Usu.", "Nome do Usuário", "RA Usuário",
                    "Data Empréstimo", "Data Validade", "Multa (R$)", "Status Atraso");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                int livroId = rs.getInt("livro_id");
                String livroTitulo = rs.getString("livro_titulo");

                Integer usuarioIdObj = (Integer) rs.getObject("usuario_id");
                String usuarioNome = rs.getString("usuario_nome");
                String usuarioRA = rs.getString("usuario_ra");
                String displayUsuarioId = (usuarioIdObj == null) ? "N/A" : usuarioIdObj.toString();
                String displayUsuarioNome = (usuarioNome == null) ? "[Usuário Removido]" : usuarioNome;
                String displayUsuarioRA = (usuarioRA == null) ? "N/A" : usuarioRA;

                String dataEmprestimo = rs.getDate("data_emprestimo") != null ? rs.getDate("data_emprestimo").toString() : "N/A";
                String dataValidade = rs.getDate("data_validade") != null ? rs.getDate("data_validade").toString() : "N/A";
                double valorMulta = rs.getDouble("valor_da_multa");
                String statusMulta = rs.getString("status_atraso_multa");

                System.out.printf("%-8d %-30.30s %-8s %-20.20s %-10s %-15s %-15s %-10.2f %-18s\n",
                        livroId, livroTitulo, displayUsuarioId, displayUsuarioNome, displayUsuarioRA,
                        dataEmprestimo, dataValidade, valorMulta, statusMulta);
            }
            if (!encontrado) {
                System.out.println("Nenhum empréstimo ativo encontrado no momento.");
            }
            System.out.println("====================================================================================================================================\n");
        } catch (SQLException e) {
            System.out.println("Erro ao listar todos os empréstimos ativos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null && !rs.isClosed()) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null && !stmt.isClosed()) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}