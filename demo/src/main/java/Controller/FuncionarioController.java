package Controller;

import DAO.FuncionarioDAO;
import Model.Funcionario;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FuncionarioController {
    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioController(Connection conexao) {
        this.funcionarioDAO = new FuncionarioDAO(conexao);
    }

    // Método cadastrarFuncionario original (adaptado para receber objeto)
    public void cadastrarFuncionario(Funcionario funcionario) throws SQLException {
        if (funcionario == null) {
            throw new IllegalArgumentException("Dados do funcionário inválidos.");
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do funcionário não pode estar vazio.");
        }
        if (funcionario.getCargo() == null || funcionario.getCargo().trim().isEmpty()) {
            throw new IllegalArgumentException("O cargo do funcionário não pode estar vazio.");
        }
        // Adicionar validações extras aqui se desejar

        funcionarioDAO.inserir(funcionario); // Chama o inserir original
    }

    public List<Funcionario> listarFuncionarios() throws SQLException {
        return funcionarioDAO.listarTodos();
    }

    // --- NOVO MÉTODO: excluirFuncionario ---
    /**
     * Exclui um funcionário pelo seu ID.
     * @param id O ID do funcionário a ser excluído.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void excluirFuncionario(int id) throws SQLException {
        // Validação básica
        if (id <= 0) {
            throw new IllegalArgumentException("ID do funcionário inválido para exclusão.");
        }
        // Assume que FuncionarioDAO tem um método excluir(id)
        funcionarioDAO.excluir(id);
    }
    // --- FIM DO NOVO MÉTODO ---

    // --- Métodos adicionados anteriormente ---
    public Funcionario buscarFuncionarioPorId(int id) throws SQLException {
        return funcionarioDAO.buscarPorId(id);
    }

    public void atualizarFuncionario(Funcionario funcionario) throws SQLException {
        if (funcionario == null) {
            throw new IllegalArgumentException("Dados do funcionário inválidos.");
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do funcionário não pode estar vazio.");
        }
        if (funcionario.getCargo() == null || funcionario.getCargo().trim().isEmpty()) {
            throw new IllegalArgumentException("O cargo do funcionário não pode estar vazio.");
        }
        funcionarioDAO.atualizar(funcionario);
    }
}