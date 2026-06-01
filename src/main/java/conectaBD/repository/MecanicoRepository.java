package conectaBD.repository;

import conectaBD.model.Mecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MecanicoRepository {
    private final Connection con;

    public MecanicoRepository(Connection con) {
        this.con = con;
    }

    public List<Mecanico> findAll() {
        List<Mecanico> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, especialidade, telefone FROM mecanico ORDER BY id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar mecânicos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Mecanico> findById(int id) {
        String sql = "SELECT id, nome, cpf, especialidade, telefone FROM mecanico WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar mecânico: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void save(Mecanico m) {
        String sql = "INSERT INTO mecanico (nome, cpf, especialidade, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setString(2, m.getCpf());
            ps.setString(3, m.getEspecialidade());
            ps.setString(4, m.getTelefone());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar mecânico: " + e.getMessage(), e);
        }
    }

    public void update(Mecanico m) {
        String sql = "UPDATE mecanico SET nome=?, cpf=?, especialidade=?, telefone=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setString(2, m.getCpf());
            ps.setString(3, m.getEspecialidade());
            ps.setString(4, m.getTelefone());
            ps.setInt(5, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar mecânico: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM mecanico WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir mecânico: " + e.getMessage(), e);
        }
    }

    public boolean hasOrdens(int idMecanico) {
        String sql = "SELECT COUNT(*) FROM ordem_servico WHERE id_mecanico = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMecanico);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar ordens do mecânico: " + e.getMessage(), e);
        }
    }

    private Mecanico mapRow(ResultSet rs) throws SQLException {
        Mecanico m = new Mecanico();
        m.setId(rs.getInt("id"));
        m.setNome(rs.getString("nome"));
        m.setCpf(rs.getString("cpf"));
        m.setEspecialidade(rs.getString("especialidade"));
        m.setTelefone(rs.getString("telefone"));
        return m;
    }
}
