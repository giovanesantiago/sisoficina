package conectaBD.menu;

import conectaBD.model.Peca;
import conectaBD.service.PecaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class PecaMenu {
    private final PecaService service;
    private final Scanner scanner;

    public PecaMenu(PecaService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- Pecas ---");
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
        List<Peca> lista = service.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma peca cadastrada.");
        } else {
            System.out.println("\n== Lista de Pecas ==");
            lista.forEach(System.out::println);
        }
    }

    private void buscarPorId() {
        int id = lerInt("ID da peca: ");
        try {
            System.out.println("\n" + service.buscarPorId(id));
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void cadastrar() {
        System.out.println("\n-- Nova Peca --");
        Peca p = new Peca();
        p.setNome(lerString("Nome: "));
        p.setDescricao(lerString("Descricao: "));
        p.setPrecoUnitario(lerDecimal("Preco unitario (ex: 29.90): "));
        p.setQuantidadeEstoque(lerInt("Quantidade em estoque: "));
        try {
            service.cadastrar(p);
            System.out.println("OK: Peca cadastrada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void atualizar() {
        int id = lerInt("ID da peca a atualizar: ");
        try {
            Peca atual = service.buscarPorId(id);
            System.out.println("Atual: " + atual);
            System.out.println("(Pressione Enter para manter o valor atual)");
            Peca p = new Peca();
            p.setId(id);
            String nome = lerString("Nome [" + atual.getNome() + "]: ");
            p.setNome(nome.isBlank() ? atual.getNome() : nome);
            String desc = lerString("Descricao [" + atual.getDescricao() + "]: ");
            p.setDescricao(desc.isBlank() ? atual.getDescricao() : desc);
            String preco = lerString("Preco unitario [" + atual.getPrecoUnitario() + "]: ");
            p.setPrecoUnitario(preco.isBlank() ? atual.getPrecoUnitario() : new BigDecimal(preco.trim().replace(",", ".")));
            String qtd = lerString("Quantidade em estoque [" + atual.getQuantidadeEstoque() + "]: ");
            p.setQuantidadeEstoque(qtd.isBlank() ? atual.getQuantidadeEstoque() : Integer.parseInt(qtd.trim()));
            service.atualizar(p);
            System.out.println("OK: Peca atualizada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void excluir() {
        int id = lerInt("ID da peca a excluir: ");
        System.out.print("Confirma exclusao? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Exclusao cancelada.");
            return;
        }
        try {
            service.excluir(id);
            System.out.println("OK: Peca excluida com sucesso.");
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
