package conectaBD.service;

import conectaBD.model.ItemOS;
import conectaBD.model.Peca;
import conectaBD.repository.ItemOSRepository;
import conectaBD.repository.OrdemServicoRepository;
import conectaBD.repository.PecaRepository;

import java.util.List;

public class ItemOSService {
    private final ItemOSRepository itemOSRepo;
    private final PecaRepository pecaRepo;
    private final OrdemServicoRepository osRepo;

    public ItemOSService(ItemOSRepository itemOSRepo, PecaRepository pecaRepo, OrdemServicoRepository osRepo) {
        this.itemOSRepo = itemOSRepo;
        this.pecaRepo = pecaRepo;
        this.osRepo = osRepo;
    }

    public List<ItemOS> listarPorOs(int idOs) {
        return itemOSRepo.findByOsId(idOs);
    }

    public List<String[]> listarDetalhadoPorOs(int idOs) {
        return itemOSRepo.findItensByOsIdDetalhado(idOs);
    }

    public void adicionarPeca(int idOs, int idPeca, int quantidade) {
        String status = osRepo.findStatusById(idOs)
            .orElseThrow(() -> new RuntimeException("OS não encontrada com ID: " + idOs));

        if (status.equals("CONCLUIDA") || status.equals("CANCELADA"))
            throw new RuntimeException("Não é possível adicionar peças a uma OS com status " + status + ".");

        Peca peca = pecaRepo.findById(idPeca)
            .orElseThrow(() -> new RuntimeException("Peça não encontrada com ID: " + idPeca));

        if (peca.getQuantidadeEstoque() <= 0)
            throw new RuntimeException("Peça '" + peca.getNome() + "' sem estoque disponível.");

        if (quantidade <= 0)
            throw new RuntimeException("A quantidade deve ser maior que zero.");

        if (quantidade > peca.getQuantidadeEstoque())
            throw new RuntimeException("Quantidade solicitada (" + quantidade + ") maior que o estoque disponível (" + peca.getQuantidadeEstoque() + ").");

        ItemOS item = new ItemOS(idOs, idPeca, quantidade, peca.getPrecoUnitario());
        itemOSRepo.save(item);
    }

    public void removerPeca(int idItem) {
        ItemOS item = itemOSRepo.findById(idItem)
            .orElseThrow(() -> new RuntimeException("Item não encontrado com ID: " + idItem));

        String status = osRepo.findStatusById(item.getIdOs())
            .orElseThrow(() -> new RuntimeException("OS associada não encontrada."));

        if (status.equals("CONCLUIDA") || status.equals("CANCELADA"))
            throw new RuntimeException("Não é possível remover peças de uma OS com status " + status + ".");

        itemOSRepo.deleteById(idItem);
    }
}
