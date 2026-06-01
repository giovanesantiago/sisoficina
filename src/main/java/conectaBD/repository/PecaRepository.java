package conectaBD.repository;

import conectaBD.model.Peca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PecaRepository {
    private final Connection con;

    public PecaRepository(Connection con) {
        this.con = con;
    }

    public List<Peca> findAll() {
        List<Peca> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco_unitario, quantidade_estoque FROM peca ORDER BY id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar peças: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Peca> findById(int id) {
        String sql = "SELECT id, nome, descricao, preco_unitario, quantidade_estoque FROM peca WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar peça: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void save(Peca p) {
        String sql = "INSERT INTO peca (nome, descricao, preco_unitario, quantidade_estoque) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setBigDecimal(3, p.getPrecoUnitario());
            ps.setInt(4, p.getQuantidadeEstoque());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar peça: " + e.getMessage(), e);
        }
    }

    public void update(Peca p) {
        String sql = "UPDATE peca SET nome=?, descricao=?, preco_unitario=?, quantidade_estoque=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setBigDecimal(3, p.getPrecoUnitario());
            ps.setInt(4, p.getQuantidadeEstoque());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar peça: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM peca WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir peça: " + e.getMessage(), e);
        }
    }

    public boolean isVinculadaAOsAtiva(int idPeca) {
        String sql = "SELECT COUNT(*) FROM item_os io " +
                     "JOIN ordem_servico os ON io.id_os = os.id " +
                     "WHERE io.id_peca = ? AND os.status IN ('ABERTA', 'EM_ANDAMENTO')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPeca);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar vínculo da peça: " + e.getMessage(), e);
        }
    }

    private Peca mapRow(ResultSet rs) throws SQLException {
        Peca p = new Peca();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        return p;
    }
}
