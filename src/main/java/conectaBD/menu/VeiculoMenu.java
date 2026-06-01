package conectaBD.menu;

import conectaBD.model.Veiculo;
import conectaBD.service.ClienteService;
import conectaBD.service.VeiculoService;

import java.util.List;
import java.util.Scanner;

public class VeiculoMenu {
    private final VeiculoService service;
    private final ClienteService clienteService;
    private final Scanner scanner;

    public VeiculoMenu(VeiculoService service, ClienteService clienteService, Scanner scanner) {
        this.service = service;
        this.clienteService = clienteService;
        this.scanner = scanner;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- Veiculos ---");
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Cadastrar");
            System.out.println("4. Atualizar");
            System.out.println("5. Excluir");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> listarTodos();
                case "2" -> buscarPorId();
                case "3" -> cadastrar();
                case "4" -> atualizar();
                case "5" -> excluir();
                case "0" -> rodando = false;
                default  -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void listarTodos() {
        List<Veiculo> lista = service.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum veiculo cadastrado.");
        } else {
            System.out.println("\n== Lista de Veiculos ==");
            lista.forEach(System.out::println);
        }
    }

    private void buscarPorId() {
        int id = lerInt("ID do veiculo: ");
        try {
            System.out.println("\n" + service.buscarPorId(id));
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void cadastrar() {
        System.out.println("\n-- Novo Veiculo --");
        System.out.println("Clientes disponiveis:");
        clienteService.listarTodos().forEach(c -> System.out.println("  " + c));
        Veiculo v = new Veiculo();
        v.setPlaca(lerString("Placa: "));
        v.setModelo(lerString("Modelo: "));
        v.setMarca(lerString("Marca: "));
        v.setAno(lerInt("Ano: "));
        v.setCor(lerString("Cor: "));
        v.setIdCliente(lerInt("ID do cliente: "));
        try {
            service.cadastrar(v);
            System.out.println("OK: Veiculo cadastrado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void atualizar() {
        int id = lerInt("ID do veiculo a atualizar: ");
        try {
            Veiculo atual = service.buscarPorId(id);
            System.out.println("Atual: " + atual);
            System.out.println("(Pressione Enter para manter o valor atual)");
            Veiculo v = new Veiculo();
            v.setId(id);
            String placa = lerString("Placa [" + atual.getPlaca() + "]: ");
            v.setPlaca(placa.isBlank() ? atual.getPlaca() : placa);
            String modelo = lerString("Modelo [" + atual.getModelo() + "]: ");
            v.setModelo(modelo.isBlank() ? atual.getModelo() : modelo);
            String marca = lerString("Marca [" + atual.getMarca() + "]: ");
            v.setMarca(marca.isBlank() ? atual.getMarca() : marca);
            String anoStr = lerString("Ano [" + atual.getAno() + "]: ");
            v.setAno(anoStr.isBlank() ? atual.getAno() : Integer.parseInt(anoStr.trim()));
            String cor = lerString("Cor [" + atual.getCor() + "]: ");
            v.setCor(cor.isBlank() ? atual.getCor() : cor);
            String clienteStr = lerString("ID do cliente [" + atual.getIdCliente() + "]: ");
            v.setIdCliente(clienteStr.isBlank() ? atual.getIdCliente() : Integer.parseInt(clienteStr.trim()));
            service.atualizar(v);
            System.out.println("OK: Veiculo atualizado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void excluir() {
        int id = lerInt("ID do veiculo a excluir: ");
        System.out.print("Confirma exclusao? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Exclusao cancelada.");
            return;
        }
        try {
            service.excluir(id);
            System.out.println("OK: Veiculo excluido com sucesso.");
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

    private String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
