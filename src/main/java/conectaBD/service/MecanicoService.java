package conectaBD.service;

import conectaBD.model.Mecanico;
import conectaBD.repository.MecanicoRepository;

import java.util.List;

public class MecanicoService {
    private final MecanicoRepository repo;

    public MecanicoService(MecanicoRepository repo) {
        this.repo = repo;
    }

    public List<Mecanico> listarTodos() {
        return repo.findAll();
    }

    public Mecanico buscarPorId(int id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Mecânico não encontrado com ID: " + id));
    }

    public void cadastrar(Mecanico m) {
        if (m.getNome() == null || m.getNome().isBlank())
            throw new RuntimeException("Nome do mecânico é obrigatório.");
        if (m.getCpf() == null || m.getCpf().isBlank())
            throw new RuntimeException("CPF do mecânico é obrigatório.");
        repo.save(m);
    }

    public void atualizar(Mecanico m) {
        buscarPorId(m.getId());
        if (m.getNome() == null || m.getNome().isBlank())
            throw new RuntimeException("Nome do mecânico é obrigatório.");
        repo.update(m);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (repo.hasOrdens(id))
            throw new RuntimeException("Mecânico possui ordens de serviço vinculadas. Não é possível excluí-lo.");
        repo.delete(id);
    }
}
