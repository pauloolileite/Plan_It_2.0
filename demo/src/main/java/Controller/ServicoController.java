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

    public void cadastrarServico(Servico servico) throws SQLException {
        validarServico(servico); // Chama método de validação
        servicoDAO.inserir(servico);
    }

    public List<Servico> listarServicos() throws SQLException {
        return servicoDAO.listarTodos();
    }

    public Servico buscarServicoPorId(int id) throws SQLException {
        return servicoDAO.buscarPorId(id);
    }

    public void atualizarServico(Servico servico) throws SQLException {
        validarServico(servico); // Chama método de validação
        if (servico.getId() <= 0) { // Validação extra para atualização
            throw new IllegalArgumentException("ID do serviço inválido para atualização.");
        }
        servicoDAO.atualizar(servico);
    }

    public void excluirServico(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do serviço inválido para exclusão.");
        }
        servicoDAO.excluir(id);
    }

    // --- NOVO MÉTODO: validarServico ---
    /**
     * Valida os campos obrigatórios e os valores de um objeto Servico.
     * @param servico O objeto Servico a ser validado.
     * @throws IllegalArgumentException Se alguma validação falhar.
     */
    private void validarServico(Servico servico) throws IllegalArgumentException {
        if (servico == null) {
            throw new IllegalArgumentException("Dados do serviço inválidos.");
        }
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do serviço não pode estar vazio.");
        }
        if (servico.getDuracao() <= 0) { // Valida duracao
            throw new IllegalArgumentException("A duração do serviço deve ser maior que zero minutos.");
        }
        if (servico.getPreco() <= 0) { // Usa getPreco() e valida
            throw new IllegalArgumentException("O preço do serviço deve ser maior que zero.");
        }
    }
}