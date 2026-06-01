package conectaBD.service;

import conectaBD.model.Cliente;
import conectaBD.repository.ClienteRepository;

import java.util.List;

public class ClienteService {
    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listarTodos() {
        return repo.findAll();
    }

    public Cliente buscarPorId(int id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
    }

    public void cadastrar(Cliente c) {
        if (c.getNome() == null || c.getNome().isBlank())
            throw new RuntimeException("Nome do cliente é obrigatório.");
        if (c.getCpf() == null || c.getCpf().isBlank())
            throw new RuntimeException("CPF do cliente é obrigatório.");
        repo.save(c);
    }

    public void atualizar(Cliente c) {
        buscarPorId(c.getId());
        if (c.getNome() == null || c.getNome().isBlank())
            throw new RuntimeException("Nome do cliente é obrigatório.");
        if (c.getCpf() == null || c.getCpf().isBlank())
            throw new RuntimeException("CPF do cliente é obrigatório.");
        repo.update(c);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (repo.hasVeiculos(id))
            throw new RuntimeException("Cliente possui veículos cadastrados. Remova os veículos antes de excluir o cliente.");
        repo.delete(id);
    }
}
