package conectaBD.service;

import conectaBD.model.Veiculo;
import conectaBD.repository.VeiculoRepository;

import java.util.List;

public class VeiculoService {
    private final VeiculoRepository repo;

    public VeiculoService(VeiculoRepository repo) {
        this.repo = repo;
    }

    public List<Veiculo> listarTodos() {
        return repo.findAll();
    }

    public Veiculo buscarPorId(int id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + id));
    }

    public List<Veiculo> listarPorCliente(int idCliente) {
        return repo.findByClienteId(idCliente);
    }

    public void cadastrar(Veiculo v) {
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new RuntimeException("Placa do veículo é obrigatória.");
        if (v.getModelo() == null || v.getModelo().isBlank())
            throw new RuntimeException("Modelo do veículo é obrigatório.");
        if (v.getMarca() == null || v.getMarca().isBlank())
            throw new RuntimeException("Marca do veículo é obrigatória.");
        repo.save(v);
    }

    public void atualizar(Veiculo v) {
        buscarPorId(v.getId());
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new RuntimeException("Placa do veículo é obrigatória.");
        repo.update(v);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (repo.hasOrdens(id))
            throw new RuntimeException("Veículo possui ordens de serviço vinculadas. Não é possível excluí-lo.");
        repo.delete(id);
    }
}
