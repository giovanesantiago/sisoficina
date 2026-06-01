package conectaBD;

import conectaBD.menu.Menu;
import conectaBD.repository.*;
import conectaBD.service.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class testeConexaoBD {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/sisoficina";
        String usuario = "postgres";
        String senha = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, usuario, senha);
            System.out.println("Banco de dados conectado com sucesso!");

            ClienteRepository      clienteRepo  = new ClienteRepository(con);
            VeiculoRepository      veiculoRepo  = new VeiculoRepository(con);
            MecanicoRepository     mecanicoRepo = new MecanicoRepository(con);
            PecaRepository         pecaRepo     = new PecaRepository(con);
            ItemOSRepository       itemOSRepo   = new ItemOSRepository(con);
            OrdemServicoRepository osRepo       = new OrdemServicoRepository(con);

            ClienteService      clienteService  = new ClienteService(clienteRepo);
            VeiculoService      veiculoService  = new VeiculoService(veiculoRepo);
            MecanicoService     mecanicoService = new MecanicoService(mecanicoRepo);
            PecaService         pecaService     = new PecaService(pecaRepo);
            ItemOSService       itemOSService   = new ItemOSService(itemOSRepo, pecaRepo, osRepo);
            OrdemServicoService osService       = new OrdemServicoService(osRepo, itemOSRepo, pecaRepo);

            Scanner scanner = new Scanner(System.in);
            Menu menu = new Menu(clienteService, veiculoService, mecanicoService,
                                 osService, pecaService, itemOSService, scanner);
            menu.iniciar();

            scanner.close();
            con.close();

        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL nao encontrado. Verifique o pom.xml: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            System.err.println("Verifique se o PostgreSQL esta rodando e o banco 'sisoficina' existe.");
        }
    }
}
