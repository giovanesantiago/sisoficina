package conectaBD.menu;

import conectaBD.service.*;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final ClienteMenu clienteMenu;
    private final VeiculoMenu veiculoMenu;
    private final MecanicoMenu mecanicoMenu;
    private final OrdemServicoMenu osMenu;
    private final PecaMenu pecaMenu;
    private final RelatorioMenu relatorioMenu;

    public Menu(ClienteService clienteService, VeiculoService veiculoService,
                MecanicoService mecanicoService, OrdemServicoService osService,
                PecaService pecaService, ItemOSService itemOSService, Scanner scanner) {
        this.scanner = scanner;
        this.clienteMenu = new ClienteMenu(clienteService, scanner);
        this.veiculoMenu = new VeiculoMenu(veiculoService, clienteService, scanner);
        this.mecanicoMenu = new MecanicoMenu(mecanicoService, scanner);
        this.osMenu = new OrdemServicoMenu(osService, itemOSService, pecaService, scanner);
        this.pecaMenu = new PecaMenu(pecaService, scanner);
        this.relatorioMenu = new RelatorioMenu(osService, clienteService, mecanicoService, scanner);
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n===== SisOficina =====");
            System.out.println("1. Clientes");
            System.out.println("2. Veiculos");
            System.out.println("3. Mecanicos");
            System.out.println("4. Pecas");
            System.out.println("5. Ordens de Servico");
            System.out.println("6. Relatorios");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> clienteMenu.iniciar();
                case "2" -> veiculoMenu.iniciar();
                case "3" -> mecanicoMenu.iniciar();
                case "4" -> pecaMenu.iniciar();
                case "5" -> osMenu.iniciar();
                case "6" -> relatorioMenu.iniciar();
                case "0" -> rodando = false;
                default  -> System.out.println("Opcao invalida.");
            }
        }
        System.out.println("Encerrando SisOficina. Ate logo!");
    }
}
