package conectaBD.service;

import conectaBD.model.OrdemServico;
import conectaBD.repository.ItemOSRepository;
import conectaBD.repository.OrdemServicoRepository;
import conectaBD.repository.PecaRepository;

import java.util.List;

public class OrdemServicoService {
    private final OrdemServicoRepository osRepo;
    private final ItemOSRepository itemOSRepo;
    private final PecaRepository pecaRepo;

    public OrdemServicoService(OrdemServicoRepository osRepo, ItemOSRepository itemOSRepo, PecaRepository pecaRepo) {
        this.osRepo = osRepo;
        this.itemOSRepo = itemOSRepo;
        this.pecaRepo = pecaRepo;
    }

    public List<OrdemServico> listarTodos() {
        return osRepo.findAll();
    }

    public OrdemServico buscarPorId(int id) {
        return osRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + id));
    }

    public void cadastrar(OrdemServico os) {
        if (os.getIdVeiculo() <= 0)
            throw new RuntimeException("ID do veículo inválido.");
        if (os.getIdMecanico() <= 0)
            throw new RuntimeException("ID do mecânico inválido.");
        os.setStatus("ABERTA");
        osRepo.save(os);
    }

    public void atualizar(OrdemServico os) {
        buscarPorId(os.getId());
        List<String> validos = List.of("ABERTA", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA");
        if (!validos.contains(os.getStatus()))
            throw new RuntimeException("Status inválido. Use: ABERTA, EM_ANDAMENTO, CONCLUIDA ou CANCELADA.");
        osRepo.update(os);
    }

    public void excluir(int id) {
        OrdemServico os = buscarPorId(id);
        if (os.getStatus().equals("CONCLUIDA"))
            throw new RuntimeException("OS concluída não pode ser excluída. Cancele-a primeiro se necessário.");
        osRepo.delete(id);
    }

    public void fecharOS(int idOs) {
        OrdemServico os = buscarPorId(idOs);
        if (!os.getStatus().equals("ABERTA") && !os.getStatus().equals("EM_ANDAMENTO"))
            throw new RuntimeException("OS não pode ser fechada. Status atual: " + os.getStatus() + ". Só é possível fechar OS com status ABERTA ou EM_ANDAMENTO.");
        osRepo.fecharOS(idOs);
    }

    public void cancelarOS(int idOs) {
        OrdemServico os = buscarPorId(idOs);
        if (!os.getStatus().equals("ABERTA") && !os.getStatus().equals("EM_ANDAMENTO"))
            throw new RuntimeException("OS não pode ser cancelada. Status atual: " + os.getStatus() + ". Só é possível cancelar OS com status ABERTA ou EM_ANDAMENTO.");
        osRepo.cancelarOS(idOs);
    }

    public List<String[]> listarOsCompletas() {
        return osRepo.findAllCompletas();
    }

    public List<String[]> listarOsPorCliente(String nomeCliente) {
        return osRepo.findOsCompletasByClienteNome(nomeCliente);
    }

    public List<String[]> listarOsPorMecanico(String nomeMecanico) {
        return osRepo.findOsCompletasByMecanicoNome(nomeMecanico);
    }

    public List<String[]> listarPecasPorOs(int idOs) {
        buscarPorId(idOs);
        return itemOSRepo.findItensByOsIdDetalhado(idOs);
    }
}
