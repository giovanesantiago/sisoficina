package conectaBD.menu;

import conectaBD.service.ClienteService;
import conectaBD.service.MecanicoService;
import conectaBD.service.OrdemServicoService;

import java.util.List;
import java.util.Scanner;

public class RelatorioMenu {
    private final OrdemServicoService osService;
    private final ClienteService clienteService;
    private final MecanicoService mecanicoService;
    private final Scanner scanner;

    private static final String[] HEADER_OS = {"ID", "Status", "Abertura", "Fechamento", "Mao Obra", "Total", "Cliente", "Placa", "Modelo", "Mecanico"};
    private static final String[] HEADER_PECAS = {"Item ID", "Peca", "Qtd", "Preco Unit.", "Subtotal"};

    public RelatorioMenu(OrdemServicoService osService, ClienteService clienteService,
                         MecanicoService mecanicoService, Scanner scanner) {
        this.osService = osService;
        this.clienteService = clienteService;
        this.mecanicoService = mecanicoService;
        this.scanner = scanner;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- Relatorios ---");
            System.out.println("1. Todas as OS (completo)");
            System.out.println("2. OS por cliente");
            System.out.println("3. OS por mecanico");
            System.out.println("4. Pecas usadas em uma OS");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> relatorioOsCompletas();
                case "2" -> relatorioOsPorCliente();
                case "3" -> relatorioOsPorMecanico();
                case "4" -> relatorioPecasPorOs();
                case "0" -> rodando = false;
                default  -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void relatorioOsCompletas() {
        try {
            List<String[]> rows = osService.listarOsCompletas();
            System.out.println("\n== Relatorio: Todas as Ordens de Servico ==");
            imprimirTabela(HEADER_OS, rows);
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void relatorioOsPorCliente() {
        System.out.println("\nClientes cadastrados:");
        clienteService.listarTodos().forEach(c ->
            System.out.println("  [" + c.getId() + "] " + c.getNome()));
        System.out.print("Nome (ou parte do nome) do cliente: ");
        String nome = scanner.nextLine().trim();
        try {
            List<String[]> rows = osService.listarOsPorCliente(nome);
            System.out.println("\n== OS do cliente: " + nome + " ==");
            imprimirTabela(HEADER_OS, rows);
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void relatorioOsPorMecanico() {
        System.out.println("\nMecanicos cadastrados:");
        mecanicoService.listarTodos().forEach(m ->
            System.out.println("  [" + m.getId() + "] " + m.getNome()));
        System.out.print("Nome (ou parte do nome) do mecanico: ");
        String nome = scanner.nextLine().trim();
        try {
            List<String[]> rows = osService.listarOsPorMecanico(nome);
            System.out.println("\n== OS do mecanico: " + nome + " ==");
            imprimirTabela(HEADER_OS, rows);
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void relatorioPecasPorOs() {
        System.out.print("ID da OS: ");
        try {
            int idOs = Integer.parseInt(scanner.nextLine().trim());
            List<String[]> rows = osService.listarPecasPorOs(idOs);
            System.out.println("\n== Pecas da OS #" + idOs + " ==");
            imprimirTabela(HEADER_PECAS, rows);
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void imprimirTabela(String[] headers, List<String[]> rows) {
        if (rows.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length && i < widths.length; i++) {
                int len = row[i] != null ? row[i].length() : 4;
                if (len > widths[i]) widths[i] = len;
            }
        }
        String separator = buildSeparator(widths);
        System.out.println(separator);
        System.out.println(buildRow(headers, widths));
        System.out.println(separator);
        for (String[] row : rows) {
            System.out.println(buildRow(row, widths));
        }
        System.out.println(separator);
        System.out.println("Total: " + rows.size() + " registro(s).");
    }

    private String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    private String buildRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < widths.length; i++) {
            String cell = (i < cells.length && cells[i] != null) ? cells[i] : "null";
            sb.append(" ").append(padRight(cell, widths[i])).append(" |");
        }
        return sb.toString();
    }

    private String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }
}
