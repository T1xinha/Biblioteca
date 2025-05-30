package org.example;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reserva {
    private int id;
    private int livroId;
    private int usuarioId;
    private Timestamp dataReserva; // Data e hora exata da reserva
    private String statusReserva;  // Ex: 'ATIVA', 'AGUARDANDO_RETIRADA', 'ATENDIDA', 'CANCELADA', 'EXPIRADA'
    private Timestamp dataNotificacaoDisponibilidade; // Quando o status mudou para AGUARDANDO_RETIRADA
    private Timestamp dataLimiteRetirada; // Até quando o livro fica retido

    // Construtor
    public Reserva(int id, int livroId, int usuarioId, Timestamp dataReserva, String statusReserva,
                   Timestamp dataNotificacaoDisponibilidade, Timestamp dataLimiteRetirada) {
        this.id = id;
        this.livroId = livroId;
        this.usuarioId = usuarioId;
        this.dataReserva = dataReserva;
        this.statusReserva = statusReserva;
        this.dataNotificacaoDisponibilidade = dataNotificacaoDisponibilidade;
        this.dataLimiteRetirada = dataLimiteRetirada;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getLivroId() {
        return livroId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public Timestamp getDataReserva() {
        return dataReserva;
    }

    public String getStatusReserva() {
        return statusReserva;
    }

    public Timestamp getDataNotificacaoDisponibilidade() {
        return dataNotificacaoDisponibilidade;
    }

    public Timestamp getDataLimiteRetirada() {
        return dataLimiteRetirada;
    }

    // Setters (podem ser úteis se você precisar modificar um objeto Reserva existente)
    public void setId(int id) {
        this.id = id;
    }

    public void setLivroId(int livroId) {
        this.livroId = livroId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setDataReserva(Timestamp dataReserva) {
        this.dataReserva = dataReserva;
    }

    public void setStatusReserva(String statusReserva) {
        this.statusReserva = statusReserva;
    }

    public void setDataNotificacaoDisponibilidade(Timestamp dataNotificacaoDisponibilidade) {
        this.dataNotificacaoDisponibilidade = dataNotificacaoDisponibilidade;
    }

    public void setDataLimiteRetirada(Timestamp dataLimiteRetirada) {
        this.dataLimiteRetirada = dataLimiteRetirada;
    }

    // Métodos utilitários para conversão de data, se necessário
    public LocalDate getDataReservaAsLocalDate() {
        return (dataReserva != null) ? dataReserva.toLocalDateTime().toLocalDate() : null;
    }

    public LocalDateTime getDataNotificacaoAsLocalDateTime() {
        return (dataNotificacaoDisponibilidade != null) ? dataNotificacaoDisponibilidade.toLocalDateTime() : null;
    }

    public LocalDate getDataLimiteRetiradaAsLocalDate() {
        return (dataLimiteRetirada != null) ? dataLimiteRetirada.toLocalDateTime().toLocalDate() : null;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", livroId=" + livroId +
                ", usuarioId=" + usuarioId +
                ", dataReserva=" + (getDataReservaAsLocalDate() != null ? getDataReservaAsLocalDate() : "N/A") +
                ", statusReserva='" + statusReserva + '\'' +
                ", dataNotificacao=" + (getDataNotificacaoAsLocalDateTime() != null ? getDataNotificacaoAsLocalDateTime() : "N/A") +
                ", dataLimiteRetirada=" + (getDataLimiteRetiradaAsLocalDate() != null ? getDataLimiteRetiradaAsLocalDate() : "N/A") +
                '}';
    }
}