package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDAO {

    /**
     * Cria uma nova reserva para um livro por um usuário.
     */
    public static void criarReserva(int livroId, int usuarioId) {
        int quantidadeDisponivel = LivroFisico.getQuantidadeDisponivelLivro(livroId);

        if (quantidadeDisponivel == -1) {
            System.out.println("LOG: Reserva não criada. Motivo: Livro com ID " + livroId + " não encontrado.");
            return;
        }
        if (quantidadeDisponivel > 0) {
            System.out.println("LOG: Reserva não criada. Motivo: Livro ID " + livroId + " está atualmente disponível (" + quantidadeDisponivel + " cópia(s)).");
            System.out.println("       Reservas são para livros indisponíveis.");
            return;
        }

        Connection conn = null;
        String sqlVerificaEmprestimoAtivo = "SELECT COUNT(*) AS total FROM emprestimo WHERE livro_id = ? AND usuario_id = ? AND devolvido = FALSE";
        String sqlVerificaReservaAtiva = "SELECT COUNT(*) AS total FROM reserva WHERE livro_id = ? AND usuario_id = ? AND status_reserva = 'ATIVA'";
        String sqlInsertReserva = "INSERT INTO reserva (livro_id, usuario_id, data_reserva, status_reserva) VALUES (?, ?, NOW(), 'ATIVA')";

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtVerificaEmprestimo = conn.prepareStatement(sqlVerificaEmprestimoAtivo)) {
                stmtVerificaEmprestimo.setInt(1, livroId);
                stmtVerificaEmprestimo.setInt(2, usuarioId);
                try (ResultSet rsVerificaEmprestimo = stmtVerificaEmprestimo.executeQuery()) {
                    if (rsVerificaEmprestimo.next() && rsVerificaEmprestimo.getInt("total") > 0) {
                        System.out.println("LOG: Reserva não criada. Motivo: Usuário ID " + usuarioId + " já possui o livro ID " + livroId + " emprestado.");
                        conn.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement stmtVerificaReserva = conn.prepareStatement(sqlVerificaReservaAtiva)) {
                stmtVerificaReserva.setInt(1, livroId);
                stmtVerificaReserva.setInt(2, usuarioId);
                try (ResultSet rsVerificaReserva = stmtVerificaReserva.executeQuery()) {
                    if (rsVerificaReserva.next() && rsVerificaReserva.getInt("total") > 0) {
                        System.out.println("LOG: Reserva não criada. Motivo: Usuário ID " + usuarioId + " já possui uma reserva ativa para o livro ID " + livroId + ".");
                        conn.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertReserva)) {
                stmtInsert.setInt(1, livroId);
                stmtInsert.setInt(2, usuarioId);
                int rowsAffected = stmtInsert.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Livro reservado com sucesso! Você será o próximo da fila quando estiver disponível.");
                    conn.commit();
                } else {
                    System.out.println("LOG: Reserva não criada. Motivo: Nenhuma linha afetada pelo comando de inserção.");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("LOG: Erro SQL ao criar reserva: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); if (!conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Lista as reservas ativas ou aguardando retirada de um usuário.
     */
    public static void consultarReservasPorUsuario(int usuarioId) {
        String sql = """
            SELECT r.id AS reserva_id, r.livro_id, l.titulo AS livro_titulo, r.data_reserva, r.status_reserva,
                   r.data_limite_retirada,
                   (SELECT COUNT(*) + 1 FROM reserva r2
                    WHERE r2.livro_id = r.livro_id AND r2.status_reserva = 'ATIVA' AND r2.data_reserva < r.data_reserva) AS posicao_fila
            FROM reserva r
            JOIN livro l ON r.livro_id = l.id
            WHERE r.usuario_id = ? AND (r.status_reserva = 'ATIVA' OR r.status_reserva = 'AGUARDANDO_RETIRADA')
            ORDER BY r.data_reserva ASC;
            """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n====================================== MINHAS RESERVAS ======================================");
                System.out.printf("%-10s %-8s %-30s %-15s %-12s %-15s %s\n", "ID Reserva", "ID Livro", "Título", "Data Reserva", "Posição Fila", "Status", "Retirar Até");
                System.out.println("--------------------------------------------------------------------------------------------------------------------");
                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    int reservaId = rs.getInt("reserva_id");
                    int livroId = rs.getInt("livro_id");
                    String livroTitulo = rs.getString("livro_titulo");
                    Timestamp dataReservaTS = rs.getTimestamp("data_reserva");
                    String dataReserva = dataReservaTS != null ? dataReservaTS.toLocalDateTime().toLocalDate().toString() : "N/A";
                    String statusReserva = rs.getString("status_reserva");
                    int posicaoFila = (statusReserva.equals("ATIVA")) ? rs.getInt("posicao_fila") : 0;
                    Timestamp dataLimiteTS = rs.getTimestamp("data_limite_retirada");
                    String dataLimite = (dataLimiteTS != null && statusReserva.equals("AGUARDANDO_RETIRADA")) ? dataLimiteTS.toLocalDateTime().toLocalDate().toString() : "---";

                    System.out.printf("%-10d %-8d %-30.30s %-15s %-12s %-15s %s\n",
                            reservaId, livroId, livroTitulo, dataReserva, (posicaoFila > 0 ? String.valueOf(posicaoFila) : "---"), statusReserva, dataLimite);
                }
                if (!encontrado) {
                    System.out.println("Você não possui reservas ativas ou aguardando retirada no momento.");
                }
                System.out.println("====================================================================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar suas reservas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Reserva buscarProximaReservaAtivaParaLivro(int livroId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM reserva WHERE livro_id = ? AND status_reserva = 'ATIVA' ORDER BY data_reserva ASC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reserva(
                            rs.getInt("id"), rs.getInt("livro_id"), rs.getInt("usuario_id"),
                            rs.getTimestamp("data_reserva"), rs.getString("status_reserva"),
                            rs.getTimestamp("data_notificacao_disponibilidade"), rs.getTimestamp("data_limite_retirada")
                    );
                }
            }
        }
        return null;
    }

    private static boolean atualizarStatusReservaParaAguardandoRetirada(int reservaId, int diasParaRetirada, Connection conn) throws SQLException {
        String sql = "UPDATE reserva SET status_reserva = 'AGUARDANDO_RETIRADA', data_notificacao_disponibilidade = NOW(), data_limite_retirada = DATE_ADD(NOW(), INTERVAL ? DAY) WHERE id = ? AND status_reserva = 'ATIVA'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, diasParaRetirada);
            stmt.setInt(2, reservaId);
            return stmt.executeUpdate() > 0;
        }
    }

    public static void processarProximaReservaAposDevolucao(int livroId, Connection conn) throws SQLException {
        Reserva proximaReserva = buscarProximaReservaAtivaParaLivro(livroId, conn);
        if (proximaReserva != null) {
            int diasParaRetirada = 2;
            boolean atualizado = atualizarStatusReservaParaAguardandoRetirada(proximaReserva.getId(), diasParaRetirada, conn);
            if (atualizado) {
                Usuario reservador = UsuarioDAO.buscarUsuarioPorId(proximaReserva.getUsuarioId());
                String tituloLivro = LivroFisico.getTituloLivroPorId(livroId, conn);
                if (tituloLivro == null) tituloLivro = "ID " + livroId;
                String nomeReservador = (reservador != null) ? reservador.getNome() : "Usuário ID " + proximaReserva.getUsuarioId();
                LocalDate dataLimite = LocalDate.now().plusDays(diasParaRetirada);
                System.out.println("\n--------------------------------------------------------------------");
                System.out.println("AVISO DE RESERVA ATENDIDA:");
                System.out.println("O livro '" + tituloLivro + "' (ID: " + livroId + ") foi devolvido e está agora");
                System.out.println("disponível e reservado para o usuário: " + nomeReservador + ".");
                System.out.println("Por favor, o usuário deve retirar o livro até " + dataLimite.toString() + ".");
                System.out.println("--------------------------------------------------------------------");
            } else {
                System.out.println("Aviso: Não foi possível atualizar o status da próxima reserva para o livro ID " + livroId + " (Reserva ID: " + proximaReserva.getId() + ").");
            }
        }
    }

    public static Reserva buscarReservaAguardandoRetirada(int livroId, int usuarioId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM reserva WHERE livro_id = ? AND usuario_id = ? AND status_reserva = 'AGUARDANDO_RETIRADA' LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId); stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reserva(
                            rs.getInt("id"), rs.getInt("livro_id"), rs.getInt("usuario_id"),
                            rs.getTimestamp("data_reserva"), rs.getString("status_reserva"),
                            rs.getTimestamp("data_notificacao_disponibilidade"), rs.getTimestamp("data_limite_retirada")
                    );
                }
            }
        }
        return null;
    }

    public static Reserva buscarReservaAguardandoRetiradaParaOutroUsuario(int livroId, int solicitanteUsuarioId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM reserva WHERE livro_id = ? AND usuario_id != ? AND status_reserva = 'AGUARDANDO_RETIRADA' ORDER BY data_reserva ASC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId); stmt.setInt(2, solicitanteUsuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reserva(
                            rs.getInt("id"), rs.getInt("livro_id"), rs.getInt("usuario_id"),
                            rs.getTimestamp("data_reserva"), rs.getString("status_reserva"),
                            rs.getTimestamp("data_notificacao_disponibilidade"), rs.getTimestamp("data_limite_retirada")
                    );
                }
            }
        }
        return null;
    }

    public static boolean marcarReservaComoAtendida(int reservaId, Connection conn) throws SQLException {
        String sql = "UPDATE reserva SET status_reserva = 'ATENDIDA' WHERE id = ? AND status_reserva = 'AGUARDANDO_RETIRADA'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            return stmt.executeUpdate() > 0;
        }
    }

    // NOVO MÉTODO: Listar Todas as Reservas Pendentes (Ativas ou Aguardando Retirada)
    public static void listarTodasAsReservasPendentes() {
        String sql = """
            SELECT r.id AS reserva_id, 
                   l.id AS livro_id, l.titulo AS livro_titulo,
                   u.id AS usuario_id, u.nome AS usuario_nome, u.ra AS usuario_ra,
                   r.data_reserva, r.status_reserva, r.data_notificacao_disponibilidade, r.data_limite_retirada
            FROM reserva r
            JOIN livro l ON r.livro_id = l.id
            JOIN usuario u ON r.usuario_id = u.id
            WHERE r.status_reserva = 'ATIVA' OR r.status_reserva = 'AGUARDANDO_RETIRADA'
            ORDER BY r.status_reserva ASC, r.data_reserva ASC; 
            """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n==================================== LISTA DE TODAS AS RESERVAS PENDENTES =====================================");
            System.out.printf("%-10s %-8s %-25s %-8s %-20s %-10s %-12s %-19s %-19s %-19s\n", // Ajustado formato
                    "ID Reserva", "ID Livro", "Título do Livro", "ID Usu.", "Nome Usuário", "RA Usuário",
                    "Status", "Data Reserva", "Notificado em", "Retirar Até");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                int reservaId = rs.getInt("reserva_id");
                int livroId = rs.getInt("livro_id");
                String livroTitulo = rs.getString("livro_titulo");
                int usuarioId = rs.getInt("usuario_id");
                String usuarioNome = rs.getString("usuario_nome");
                String usuarioRA = rs.getString("usuario_ra");
                Timestamp dataReservaTS = rs.getTimestamp("data_reserva");
                String dataReserva = dataReservaTS != null ? dataReservaTS.toLocalDateTime().toString().replace("T", " ").substring(0, 16) : "N/A";
                String statusReserva = rs.getString("status_reserva");
                Timestamp dataNotificacaoTS = rs.getTimestamp("data_notificacao_disponibilidade");
                String dataNotificacao = dataNotificacaoTS != null ? dataNotificacaoTS.toLocalDateTime().toString().replace("T", " ").substring(0, 16) : "---";
                Timestamp dataLimiteTS = rs.getTimestamp("data_limite_retirada");
                String dataLimite = dataLimiteTS != null ? dataLimiteTS.toLocalDateTime().toString().replace("T", " ").substring(0, 16) : "---";

                System.out.printf("%-10d %-8d %-25.25s %-8d %-20.20s %-10s %-12s %-19s %-19s %-19s\n",
                        reservaId, livroId, livroTitulo, usuarioId, usuarioNome, usuarioRA,
                        statusReserva, dataReserva, dataNotificacao, dataLimite);
            }
            if (!encontrado) {
                System.out.println("Nenhuma reserva pendente (ATIVA ou AGUARDANDO_RETIRADA) encontrada no momento.");
            }
            System.out.println("=====================================================================================================================================================================\n");
        } catch (SQLException e) {
            System.out.println("Erro ao listar todas as reservas pendentes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // NOVO MÉTODO: Pesquisar Reservas Pendentes
    public static void pesquisarEListarReservasPendentes(String termo) {
        String termoPesquisa = "%" + termo.toLowerCase() + "%";
        String sql = """
            SELECT r.id AS reserva_id, 
                   l.id AS livro_id, l.titulo AS livro_titulo,
                   u.id AS usuario_id, u.nome AS usuario_nome, u.ra AS usuario_ra,
                   r.data_reserva, r.status_reserva, r.data_notificacao_disponibilidade, r.data_limite_retirada
            FROM reserva r
            JOIN livro l ON r.livro_id = l.id
            JOIN usuario u ON r.usuario_id = u.id
            WHERE (LOWER(l.titulo) LIKE ? OR LOWER(u.nome) LIKE ? OR LOWER(u.ra) LIKE ?)
              AND (r.status_reserva = 'ATIVA' OR r.status_reserva = 'AGUARDANDO_RETIRADA')
            ORDER BY r.status_reserva ASC, r.data_reserva ASC;
            """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, termoPesquisa);
            stmt.setString(2, termoPesquisa);
            stmt.setString(3, termoPesquisa);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n================================ PESQUISA DE RESERVAS PENDENTES =================================");
                System.out.printf("%-10s %-8s %-25s %-8s %-20s %-10s %-12s %-19s %-19s %-19s\n", // Ajustado formato
                        "ID Reserva", "ID Livro", "Título do Livro", "ID Usu.", "Nome Usuário", "RA Usuário",
                        "Status", "Data Reserva", "Notificado em", "Retirar Até");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    int reservaId = rs.getInt("reserva_id");
                    int livroId = rs.getInt("livro_id");
                    String livroTitulo = rs.getString("livro_titulo");
                    int usuarioId = rs.getInt("usuario_id");
                    String usuarioNome = rs.getString("usuario_nome");
                    String usuarioRA = rs.getString("usuario_ra");
                    Timestamp dataReservaTS = rs.getTimestamp("data_reserva");
                    String dataReserva = dataReservaTS != null ? dataReservaTS.toLocalDateTime().toString().replace("T", " ").substring(0,16) : "N/A";
                    String statusReserva = rs.getString("status_reserva");
                    Timestamp dataNotificacaoTS = rs.getTimestamp("data_notificacao_disponibilidade");
                    String dataNotificacao = dataNotificacaoTS != null ? dataNotificacaoTS.toLocalDateTime().toString().replace("T", " ").substring(0,16) : "---";
                    Timestamp dataLimiteTS = rs.getTimestamp("data_limite_retirada");
                    String dataLimite = dataLimiteTS != null ? dataLimiteTS.toLocalDateTime().toString().replace("T", " ").substring(0,16) : "---";

                    System.out.printf("%-10d %-8d %-25.25s %-8d %-20.20s %-10s %-12s %-19s %-19s %-19s\n",
                            reservaId, livroId, livroTitulo, usuarioId, usuarioNome, usuarioRA,
                            statusReserva, dataReserva, dataNotificacao, dataLimite);
                }
                if (!encontrado) {
                    System.out.println("Nenhuma reserva pendente encontrada para o termo: '" + termo + "'.");
                }
                System.out.println("=====================================================================================================================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar reservas pendentes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}