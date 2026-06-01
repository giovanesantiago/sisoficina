package conectaBD.model;

public class Mecanico {
    private int id;
    private String nome;
    private String cpf;
    private String especialidade;
    private String telefone;

    public Mecanico() {}

    public Mecanico(String nome, String cpf, String especialidade, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.especialidade = especialidade;
        this.telefone = telefone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        return "[ID: " + id + "] " + nome + " | CPF: " + cpf +
               " | Especialidade: " + especialidade + " | Tel: " + telefone;
    }
}
