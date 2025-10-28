package Model;

public class Agendamento {
    private int id;
    private Cliente cliente;
    private Funcionario funcionario;
    private String servico;
    private String data;
    private String hora;
    private String observacoes;

    public Agendamento(int id, Cliente cliente, Funcionario funcionario, String servico, String data, String hora, String observacoes) {
        this.id = id;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.servico = servico;
        this.data = data;
        this.hora = hora;
        this.observacoes = observacoes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}