package conectaBD;
import java.sql.*;
import javax.swing.JOptionPane;

public class ConectaPostgres {
    private Connection con = null;
    public Statement stmt;
    public ResultSet rs;
    private String endereco;
    private String usuario;
    private String senha;

    public void Conectar(String strEnd, String strUsuario, String strSenha) {
        endereco = strEnd;
        usuario = strUsuario;
        senha = strSenha;
        System.out.println("Tentando realizar conexão só apresentando...");
        JOptionPane.showMessageDialog(null, "Tentando realizar conexão só apresentando...");
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(endereco, usuario, strSenha);
            stmt = con.createStatement();
            JOptionPane.showMessageDialog(null, "Banco conectado com sucesso");
        } catch (ClassNotFoundException cnfe) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar o driver viu Bradesco");
            cnfe.printStackTrace();
        } catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(null, "erro na query");
            sqlex.printStackTrace();
        }
    }

    public void Desconectar() {
        try {
            con.close();
        } catch (SQLException onConClose) {
            JOptionPane.showMessageDialog(null, "Erro ao desconectar o banco");
            onConClose.printStackTrace();
        }
    }
}
