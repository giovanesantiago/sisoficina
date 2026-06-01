package conectaBD.repository;

import conectaBD.model.ItemOS;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemOSRepository {
    private final Connection con;

    public ItemOSRepository(Connection con) {
        this.con = con;
    }

    public List<ItemOS> findByOsId(int idOs) {
        List<ItemOS> lista = new ArrayList<>();
        String sql = "SELECT id, id_os, id_peca, quantidade, preco_unitario_momento FROM item_os WHERE id_os = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOs);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens da OS: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<ItemOS> findById(int id) {
        String sql = "SELECT id, id_os, id_peca, quantidade, preco_unitario_momento FROM item_os WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void save(ItemOS item) {
        String sql = "INSERT INTO item_os (id_os, id_peca, quantidade, preco_unitario_momento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, item.getIdOs());
            ps.setInt(2, item.getIdPeca());
            ps.setInt(3, item.getQuantidade());
            ps.setBigDecimal(4, item.getPrecoUnitarioMomento());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar peça à OS: " + e.getMessage(), e);
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM item_os WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover item da OS: " + e.getMessage(), e);
        }
    }

    public List<String[]> findItensByOsIdDetalhado(int idOs) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT io.id, p.nome, io.quantidade, io.preco_unitario_momento, " +
                     "(io.quantidade * io.preco_unitario_momento) AS subtotal " +
                     "FROM item_os io JOIN peca p ON io.id_peca = p.id " +
                     "WHERE io.id_os = ? ORDER BY io.id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOs);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getString("quantidade"),
                        rs.getString("preco_unitario_momento"),
                        rs.getString("subtotal")
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar peças da OS: " + e.getMessage(), e);
        }
        return lista;
    }

    private ItemOS mapRow(ResultSet rs) throws SQLException {
        ItemOS item = new ItemOS();
        item.setId(rs.getInt("id"));
        item.setIdOs(rs.getInt("id_os"));
        item.setIdPeca(rs.getInt("id_peca"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitarioMomento(rs.getBigDecimal("preco_unitario_momento"));
        return item;
    }
}
