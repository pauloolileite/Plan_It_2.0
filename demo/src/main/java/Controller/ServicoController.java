package Controller;

import DAO.ServicoDAO;
import Model.Servico;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServicoController {
    private final ServicoDAO servicoDAO;

    public ServicoController(Connection conexao) {
        this.servicoDAO = new ServicoDAO(conexao);
    }

    public void cadastrarServico(String nome, String duracaoStr, String precoStr) throws Exception {
        validarCampos(nome, duracaoStr, precoStr);
        validarDuplicidade(nome);

        int duracao = converterDuracao(duracaoStr);
        double preco = converterPreco(precoStr);

        Servico servico = new Servico(0, nome, duracao, preco);
        servicoDAO.inserir(servico);
    }

    private void validarCampos(String nome, String duracaoStr, String precoStr) {
        if (nome == null || nome.isBlank() ||
                duracaoStr == null || duracaoStr.isBlank() ||
                precoStr == null || precoStr.isBlank()) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }
    }

    public void validarDuplicidade(String nome) {
        if (servicoDAO.existePorNome(nome)) {
            throw new IllegalArgumentException("Já existe um serviço com este nome.");
        }
    }

    private int converterDuracao(String duracaoStr) {
        try {
            String[] partes = duracaoStr.split(":");
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            return horas * 60 + minutos;
        } catch (Exception e) {
            throw new IllegalArgumentException("Duração inválida. Use o formato hh:mm.");
        }
    }

    private double converterPreco(String precoStr) {
        try {
            return Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço inválido. Use ponto decimal.");
        }
    }

    public List<Servico> listarServicos() throws SQLException {
        return servicoDAO.listarTodos();
    }

    public int buscarIdPorNome(String nome) throws SQLException {
        return servicoDAO.buscarIdPorNome(nome);
    }

    public void atualizarServico(Servico servico) throws SQLException {
        servicoDAO.atualizar(servico);
    }

    public void excluirServico(int id) throws SQLException {
        servicoDAO.excluirServico(id);
    }
}
