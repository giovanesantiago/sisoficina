package conectaBD.menu;

import conectaBD.model.Mecanico;
import conectaBD.service.MecanicoService;

import java.util.List;
import java.util.Scanner;

public class MecanicoMenu {
    private final MecanicoService service;
    private final Scanner scanner;

    public MecanicoMenu(MecanicoService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- Mecanicos ---");
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
        List<Mecanico> lista = service.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum mecanico cadastrado.");
        } else {
            System.out.println("\n== Lista de Mecanicos ==");
            lista.forEach(System.out::println);
        }
    }

    private void buscarPorId() {
        int id = lerInt("ID do mecanico: ");
        try {
            System.out.println("\n" + service.buscarPorId(id));
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void cadastrar() {
        System.out.println("\n-- Novo Mecanico --");
        Mecanico m = new Mecanico();
        m.setNome(lerString("Nome: "));
        m.setCpf(lerString("CPF: "));
        m.setEspecialidade(lerString("Especialidade: "));
        m.setTelefone(lerString("Telefone: "));
        try {
            service.cadastrar(m);
            System.out.println("OK: Mecanico cadastrado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void atualizar() {
        int id = lerInt("ID do mecanico a atualizar: ");
        try {
            Mecanico atual = service.buscarPorId(id);
            System.out.println("Atual: " + atual);
            System.out.println("(Pressione Enter para manter o valor atual)");
            Mecanico m = new Mecanico();
            m.setId(id);
            String nome = lerString("Nome [" + atual.getNome() + "]: ");
            m.setNome(nome.isBlank() ? atual.getNome() : nome);
            String cpf = lerString("CPF [" + atual.getCpf() + "]: ");
            m.setCpf(cpf.isBlank() ? atual.getCpf() : cpf);
            String esp = lerString("Especialidade [" + atual.getEspecialidade() + "]: ");
            m.setEspecialidade(esp.isBlank() ? atual.getEspecialidade() : esp);
            String tel = lerString("Telefone [" + atual.getTelefone() + "]: ");
            m.setTelefone(tel.isBlank() ? atual.getTelefone() : tel);
            service.atualizar(m);
            System.out.println("OK: Mecanico atualizado com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void excluir() {
        int id = lerInt("ID do mecanico a excluir: ");
        System.out.print("Confirma exclusao? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Exclusao cancelada.");
            return;
        }
        try {
            service.excluir(id);
            System.out.println("OK: Mecanico excluido com sucesso.");
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
