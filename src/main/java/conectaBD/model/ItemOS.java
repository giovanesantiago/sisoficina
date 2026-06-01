package conectaBD.model;

import java.math.BigDecimal;

public class ItemOS {
    private int id;
    private int idOs;
    private int idPeca;
    private int quantidade;
    private BigDecimal precoUnitarioMomento;

    public ItemOS() {}

    public ItemOS(int idOs, int idPeca, int quantidade, BigDecimal precoUnitarioMomento) {
        this.idOs = idOs;
        this.idPeca = idPeca;
        this.quantidade = quantidade;
        this.precoUnitarioMomento = precoUnitarioMomento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdOs() { return idOs; }
    public void setIdOs(int idOs) { this.idOs = idOs; }

    public int getIdPeca() { return idPeca; }
    public void setIdPeca(int idPeca) { this.idPeca = idPeca; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitarioMomento() { return precoUnitarioMomento; }
    public void setPrecoUnitarioMomento(BigDecimal precoUnitarioMomento) { this.precoUnitarioMomento = precoUnitarioMomento; }

    @Override
    public String toString() {
        BigDecimal subtotal = precoUnitarioMomento != null
            ? precoUnitarioMomento.multiply(BigDecimal.valueOf(quantidade))
            : BigDecimal.ZERO;
        return "[Item ID: " + id + "] Peça ID: " + idPeca +
               " | Qtd: " + quantidade + " | Preço unit.: R$ " + precoUnitarioMomento +
               " | Subtotal: R$ " + subtotal;
    }
}
