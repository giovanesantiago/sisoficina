package conectaBD.menu;

import conectaBD.model.Cliente;
import conectaBD.service.ClienteService;

import java.util.List;
import java.util.Scanner;

public class ClienteMenu {
    private final ClienteService service;
    private final Scanner scanner;

    public ClienteMenu(ClienteService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- Clientes ---");
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
        List<Cliente> lista = service.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            System.out.println("\n== Lista de Clientes ==");
            lista.forEach(System.out::println);
        }
    }

    private void buscarPorId() {
        int id = lerInt("ID do cliente: ");
        try {
            Cliente c = service.buscarPorId(id);
            System.out.println("\n" + c);
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void cadastrar() {
        System.out.println("\n-- Novo Cliente --");
        Cliente c = new Cliente();
        c.setNome(lerString("Nome: "));
        c.setCpf(lerString("CPF: "));
        c.setTelefone(lerString("Telefone: "));
        c.setEmail(lerString("Email: "));
        try {
            service.cadastrar(c);
            System.out.println("OK: Cliente cadastrado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void atualizar() {
        int id = lerInt("ID do cliente a atualizar: ");
        try {
            Cliente atual = service.buscarPorId(id);
            System.out.println("Atual: " + atual);
            System.out.println("(Pressione Enter para manter o valor atual)");
            Cliente c = new Cliente();
            c.setId(id);
            String nome = lerString("Nome [" + atual.getNome() + "]: ");
            c.setNome(nome.isBlank() ? atual.getNome() : nome);
            String cpf = lerString("CPF [" + atual.getCpf() + "]: ");
            c.setCpf(cpf.isBlank() ? atual.getCpf() : cpf);
            String tel = lerString("Telefone [" + atual.getTelefone() + "]: ");
            c.setTelefone(tel.isBlank() ? atual.getTelefone() : tel);
            String email = lerString("Email [" + atual.getEmail() + "]: ");
            c.setEmail(email.isBlank() ? atual.getEmail() : email);
            service.atualizar(c);
            System.out.println("OK: Cliente atualizado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void excluir() {
        int id = lerInt("ID do cliente a excluir: ");
        System.out.print("Confirma exclusao? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Exclusao cancelada.");
            return;
        }
        try {
            service.excluir(id);
            System.out.println("OK: Cliente excluido com sucesso.");
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
