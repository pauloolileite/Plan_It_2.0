package Model;

public class Servico {
    private int id;
    private String nome;
    private int duracao; // em minutos
    private double preco;

    public Servico(int id, String nome, int duracao, double preco) {
        this.id = id;
        this.nome = nome;
        this.duracao = duracao;
        this.preco = preco;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getDuracao() { return duracao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    @Override
    public String toString() {
        return nome;
    }
}