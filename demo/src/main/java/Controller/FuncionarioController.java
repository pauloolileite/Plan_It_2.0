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

    public void cadastrarFuncionario(String nome, String cargo) throws SQLException {
        Funcionario f = new Funcionario(0, nome, cargo);
        funcionarioDAO.inserir(f);
    }

    public List<Funcionario> listarFuncionarios() throws SQLException {
        return funcionarioDAO.listarTodos();
    }
}
