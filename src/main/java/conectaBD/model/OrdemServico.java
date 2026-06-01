package conectaBD.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrdemServico {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private String status;
    private BigDecimal valorMaoObra;
    private BigDecimal valorTotal;
    private int idVeiculo;
    private int idMecanico;

    public OrdemServico() {}

    public OrdemServico(int idVeiculo, int idMecanico, BigDecimal valorMaoObra) {
        this.idVeiculo = idVeiculo;
        this.idMecanico = idMecanico;
        this.valorMaoObra = valorMaoObra;
        this.status = "ABERTA";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getValorMaoObra() { return valorMaoObra; }
    public void setValorMaoObra(BigDecimal valorMaoObra) { this.valorMaoObra = valorMaoObra; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public int getIdMecanico() { return idMecanico; }
    public void setIdMecanico(int idMecanico) { this.idMecanico = idMecanico; }

    @Override
    public String toString() {
        String abertura = dataAbertura != null ? dataAbertura.format(FMT) : "-";
        String fechamento = dataFechamento != null ? dataFechamento.format(FMT) : "-";
        String maoObra = valorMaoObra != null ? "R$ " + valorMaoObra : "-";
        String total = valorTotal != null ? "R$ " + valorTotal : "-";
        return "[ID: " + id + "] Status: " + status +
               " | Abertura: " + abertura + " | Fechamento: " + fechamento +
               " | Mão de Obra: " + maoObra + " | Total: " + total +
               " | Veículo ID: " + idVeiculo + " | Mecânico ID: " + idMecanico;
    }
}
