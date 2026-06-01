package conectaBD.repository;

import conectaBD.model.OrdemServico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository {
    private final Connection con;

    public OrdemServicoRepository(Connection con) {
        this.con = con;
    }

    public List<OrdemServico> findAll() {
        List<OrdemServico> lista = new ArrayList<>();
        String sql = "SELECT id, data_abertura, data_fechamento, status, valor_mao_obra, valor_total, id_veiculo, id_mecanico FROM ordem_servico ORDER BY id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ordens de serviço: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<OrdemServico> findById(int id) {
        String sql = "SELECT id, data_abertura, data_fechamento, status, valor_mao_obra, valor_total, id_veiculo, id_mecanico FROM ordem_servico WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar OS: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<String> findStatusById(int id) {
        String sql = "SELECT status FROM ordem_servico WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getString("status"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar status da OS: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void save(OrdemServico os) {
        String sql = "INSERT INTO ordem_servico (status, valor_mao_obra, id_veiculo, id_mecanico) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, os.getStatus() != null ? os.getStatus() : "ABERTA");
            ps.setBigDecimal(2, os.getValorMaoObra());
            ps.setInt(3, os.getIdVeiculo());
            ps.setInt(4, os.getIdMecanico());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar OS: " + e.getMessage(), e);
        }
    }

    public void update(OrdemServico os) {
        String sql = "UPDATE ordem_servico SET status=?, valor_mao_obra=?, id_veiculo=?, id_mecanico=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, os.getStatus());
            ps.setBigDecimal(2, os.getValorMaoObra());
            ps.setInt(3, os.getIdVeiculo());
            ps.setInt(4, os.getIdMecanico());
            ps.setInt(5, os.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar OS: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM ordem_servico WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir OS: " + e.getMessage(), e);
        }
    }

    public void fecharOS(int idOs) {
        try (CallableStatement cs = con.prepareCall("CALL sp_fechar_os(?)")) {
            cs.setInt(1, idOs);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar OS: " + e.getMessage(), e);
        }
    }

    public void cancelarOS(int idOs) {
        String sql = "UPDATE ordem_servico SET status='CANCELADA', data_fechamento=now() WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOs);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar OS: " + e.getMessage(), e);
        }
    }

    public List<String[]> findAllCompletas() {
        return queryView("SELECT id, status, data_abertura, data_fechamento, valor_mao_obra, valor_total, cliente, placa, modelo, mecanico FROM vw_os_completa ORDER BY id", null);
    }

    public List<String[]> findOsCompletasByClienteNome(String nomeCliente) {
        String sql = "SELECT id, status, data_abertura, data_fechamento, valor_mao_obra, valor_total, cliente, placa, modelo, mecanico FROM vw_os_completa WHERE LOWER(cliente) LIKE LOWER(?) ORDER BY id";
        return queryView(sql, "%" + nomeCliente + "%");
    }

    public List<String[]> findOsCompletasByMecanicoNome(String nomeMecanico) {
        String sql = "SELECT id, status, data_abertura, data_fechamento, valor_mao_obra, valor_total, cliente, placa, modelo, mecanico FROM vw_os_completa WHERE LOWER(mecanico) LIKE LOWER(?) ORDER BY id";
        return queryView(sql, "%" + nomeMecanico + "%");
    }

    private List<String[]> queryView(String sql, String param) {
        List<String[]> lista = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("status"),
                        rs.getString("data_abertura"),
                        rs.getString("data_fechamento"),
                        rs.getString("valor_mao_obra"),
                        rs.getString("valor_total"),
                        rs.getString("cliente"),
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getString("mecanico")
                    });
                }
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar relatório: " + e.getMessage(), e);
        }
        return lista;
    }

    private OrdemServico mapRow(ResultSet rs) throws SQLException {
        OrdemServico os = new OrdemServico();
        os.setId(rs.getInt("id"));
        Timestamp ta = rs.getTimestamp("data_abertura");
        if (ta != null) os.setDataAbertura(ta.toLocalDateTime());
        Timestamp tf = rs.getTimestamp("data_fechamento");
        if (tf != null) os.setDataFechamento(tf.toLocalDateTime());
        os.setStatus(rs.getString("status"));
        os.setValorMaoObra(rs.getBigDecimal("valor_mao_obra"));
        os.setValorTotal(rs.getBigDecimal("valor_total"));
        os.setIdVeiculo(rs.getInt("id_veiculo"));
        os.setIdMecanico(rs.getInt("id_mecanico"));
        return os;
    }
}
