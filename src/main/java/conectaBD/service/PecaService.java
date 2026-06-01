package conectaBD.service;

import conectaBD.model.Peca;
import conectaBD.repository.PecaRepository;

import java.util.List;

public class PecaService {
    private final PecaRepository repo;

    public PecaService(PecaRepository repo) {
        this.repo = repo;
    }

    public List<Peca> listarTodos() {
        return repo.findAll();
    }

    public Peca buscarPorId(int id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Peça não encontrada com ID: " + id));
    }

    public void cadastrar(Peca p) {
        if (p.getNome() == null || p.getNome().isBlank())
            throw new RuntimeException("Nome da peça é obrigatório.");
        if (p.getPrecoUnitario() == null)
            throw new RuntimeException("Preço da peça é obrigatório.");
        repo.save(p);
    }

    public void atualizar(Peca p) {
        buscarPorId(p.getId());
        if (p.getNome() == null || p.getNome().isBlank())
            throw new RuntimeException("Nome da peça é obrigatório.");
        if (repo.isVinculadaAOsAtiva(p.getId()))
            throw new RuntimeException("Peça está vinculada a uma OS em aberto ou em andamento. Não é possível editá-la.");
        repo.update(p);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (repo.isVinculadaAOsAtiva(id))
            throw new RuntimeException("Peça está vinculada a uma OS em aberto ou em andamento. Não é possível excluí-la.");
        repo.delete(id);
    }
}
