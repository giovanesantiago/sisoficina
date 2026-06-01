package conectaBD.menu;

import conectaBD.model.ItemOS;
import conectaBD.model.OrdemServico;
import conectaBD.service.ItemOSService;
import conectaBD.service.OrdemServicoService;
import conectaBD.service.PecaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class OrdemServicoMenu {
    private final OrdemServicoService service;
    private final ItemOSService itemOSService;
    private final PecaService pecaService;
    private final Scanner scanner;

    public OrdemServicoMenu(OrdemServicoService service, ItemOSService itemOSService,
                            PecaService pecaService, Scanner scanner) {
        this.service = service;
        this.itemOSService = itemOSService;
        this.pecaService = pecaService;
        this.scanner = scanner;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- Ordens de Servico ---");
            System.out.println("1.  Listar todas");
            System.out.println("2.  Buscar por ID");
            System.out.println("3.  Cadastrar");
            System.out.println("4.  Atualizar");
            System.out.println("5.  Excluir");
            System.out.println("6.  Adicionar peca a OS");
            System.out.println("7.  Remover peca da OS");
            System.out.println("8.  Fechar OS");
            System.out.println("9.  Cancelar OS");
            System.out.println("0.  Voltar");
            System.out.print("Opcao: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> listarTodos();
                case "2" -> buscarPorId();
                case "3" -> cadastrar();
                case "4" -> atualizar();
                case "5" -> excluir();
                case "6" -> adicionarPeca();
                case "7" -> removerPeca();
                case "8" -> fecharOS();
                case "9" -> cancelarOS();
                case "0" -> rodando = false;
                default  -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void listarTodos() {
        List<OrdemServico> lista = service.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma OS cadastrada.");
        } else {
            System.out.println("\n== Lista de Ordens de Servico ==");
            lista.forEach(System.out::println);
        }
    }

    private void buscarPorId() {
        int id = lerInt("ID da OS: ");
        try {
            OrdemServico os = service.buscarPorId(id);
            System.out.println("\n" + os);
            List<ItemOS> itens = itemOSService.listarPorOs(id);
            if (itens.isEmpty()) {
                System.out.println("  Sem pecas vinculadas.");
            } else {
                System.out.println("  Pecas:");
                itens.forEach(i -> System.out.println("    " + i));
            }
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void cadastrar() {
        System.out.println("\n-- Nova Ordem de Servico --");
        OrdemServico os = new OrdemServico();
        os.setIdVeiculo(lerInt("ID do veiculo: "));
        os.setIdMecanico(lerInt("ID do mecanico: "));
        os.setValorMaoObra(lerDecimal("Valor da mao de obra (ex: 150.00): "));
        try {
            service.cadastrar(os);
            System.out.println("OK: OS cadastrada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void atualizar() {
        int id = lerInt("ID da OS a atualizar: ");
        try {
            OrdemServico atual = service.buscarPorId(id);
            System.out.println("Atual: " + atual);
            System.out.println("Status validos: ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA");
            System.out.println("(Pressione Enter para manter o valor atual)");
            OrdemServico os = new OrdemServico();
            os.setId(id);
            String status = lerString("Status [" + atual.getStatus() + "]: ");
            os.setStatus(status.isBlank() ? atual.getStatus() : status.trim().toUpperCase());
            String maoObra = lerString("Valor mao de obra [" + atual.getValorMaoObra() + "]: ");
            os.setValorMaoObra(maoObra.isBlank() ? atual.getValorMaoObra() : new BigDecimal(maoObra.trim().replace(",", ".")));
            String veiculo = lerString("ID do veiculo [" + atual.getIdVeiculo() + "]: ");
            os.setIdVeiculo(veiculo.isBlank() ? atual.getIdVeiculo() : Integer.parseInt(veiculo.trim()));
            String mecanico = lerString("ID do mecanico [" + atual.getIdMecanico() + "]: ");
            os.setIdMecanico(mecanico.isBlank() ? atual.getIdMecanico() : Integer.parseInt(mecanico.trim()));
            service.atualizar(os);
            System.out.println("OK: OS atualizada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void excluir() {
        int id = lerInt("ID da OS a excluir: ");
        System.out.print("Confirma exclusao? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Exclusao cancelada.");
            return;
        }
        try {
            service.excluir(id);
            System.out.println("OK: OS excluida com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void adicionarPeca() {
        int idOs = lerInt("ID da OS: ");
        System.out.println("Pecas disponiveis:");
        pecaService.listarTodos().forEach(p -> System.out.println("  " + p));
        int idPeca = lerInt("ID da peca: ");
        int quantidade = lerInt("Quantidade: ");
        try {
            itemOSService.adicionarPeca(idOs, idPeca, quantidade);
            System.out.println("OK: Peca adicionada a OS com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void removerPeca() {
        int idOs = lerInt("ID da OS: ");
        try {
            List<ItemOS> itens = itemOSService.listarPorOs(idOs);
            if (itens.isEmpty()) {
                System.out.println("Nenhuma peca vinculada a esta OS.");
                return;
            }
            System.out.println("Itens da OS:");
            itens.forEach(i -> System.out.println("  " + i));
            int idItem = lerInt("ID do item a remover: ");
            itemOSService.removerPeca(idItem);
            System.out.println("OK: Item removido da OS com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void fecharOS() {
        int id = lerInt("ID da OS a fechar: ");
        System.out.print("Confirma fechamento? Isso ira decrementar o estoque das pecas. (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Operacao cancelada.");
            return;
        }
        try {
            service.fecharOS(id);
            OrdemServico os = service.buscarPorId(id);
            System.out.println("OK: OS fechada com sucesso. Valor total: R$ " + os.getValorTotal());
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void cancelarOS() {
        int id = lerInt("ID da OS a cancelar: ");
        System.out.print("Confirma cancelamento? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Operacao cancelada.");
            return;
        }
        try {
            service.cancelarOS(id);
            System.out.println("OK: OS cancelada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Digite um numero inteiro.");
            }
        }
    }

    private BigDecimal lerDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Digite um numero decimal.");
            }
        }
    }

    private String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
