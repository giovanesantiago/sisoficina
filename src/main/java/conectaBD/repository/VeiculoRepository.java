package conectaBD.repository;

import conectaBD.model.Veiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VeiculoRepository {
    private final Connection con;

    public VeiculoRepository(Connection con) {
        this.con = con;
    }

    public List<Veiculo> findAll() {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT id, placa, modelo, marca, ano, cor, id_cliente FROM veiculo ORDER BY id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar veículos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Veiculo> findById(int id) {
        String sql = "SELECT id, placa, modelo, marca, ano, cor, id_cliente FROM veiculo WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veículo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Veiculo> findByClienteId(int idCliente) {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT id, placa, modelo, marca, ano, cor, id_cliente FROM veiculo WHERE id_cliente = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veículos do cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    public void save(Veiculo v) {
        String sql = "INSERT INTO veiculo (placa, modelo, marca, ano, cor, id_cliente) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getModelo());
            ps.setString(3, v.getMarca());
            ps.setInt(4, v.getAno());
            ps.setString(5, v.getCor());
            ps.setInt(6, v.getIdCliente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar veículo: " + e.getMessage(), e);
        }
    }

    public void update(Veiculo v) {
        String sql = "UPDATE veiculo SET placa=?, modelo=?, marca=?, ano=?, cor=?, id_cliente=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getModelo());
            ps.setString(3, v.getMarca());
            ps.setInt(4, v.getAno());
            ps.setString(5, v.getCor());
            ps.setInt(6, v.getIdCliente());
            ps.setInt(7, v.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar veículo: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM veiculo WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir veículo: " + e.getMessage(), e);
        }
    }

    public boolean hasOrdens(int idVeiculo) {
        String sql = "SELECT COUNT(*) FROM ordem_servico WHERE id_veiculo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar ordens do veículo: " + e.getMessage(), e);
        }
    }

    private Veiculo mapRow(ResultSet rs) throws SQLException {
        Veiculo v = new Veiculo();
        v.setId(rs.getInt("id"));
        v.setPlaca(rs.getString("placa"));
        v.setModelo(rs.getString("modelo"));
        v.setMarca(rs.getString("marca"));
        v.setAno(rs.getInt("ano"));
        v.setCor(rs.getString("cor"));
        v.setIdCliente(rs.getInt("id_cliente"));
        return v;
    }
}
