package Controller;

import DAO.ClienteDAO;
import Model.Cliente;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController(Connection conexao) {
        this.clienteDAO = new ClienteDAO(conexao);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public void salvarCliente(Cliente cliente, String nome, String telefone, String email) throws Exception {
        validarCamposObrigatorios(nome, telefone);

        if (cliente == null) {
            verificarTelefoneDuplicado(telefone);
            clienteDAO.inserir(new Cliente(0, nome, telefone, email));
        } else {
            cliente.setNome(nome);
            cliente.setTelefone(telefone);
            cliente.setEmail(email);
            clienteDAO.atualizar(cliente);
        }
    }

    private void validarCamposObrigatorios(String nome, String telefone) {
        if (nome == null || nome.isBlank() || telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Preencha os campos obrigatórios.");
        }
    }

    private void verificarTelefoneDuplicado(String telefone) throws Exception {
        if (clienteDAO.telefoneExiste(telefone)) {
            throw new IllegalArgumentException("Telefone já cadastrado.");
        }
    }

    public void excluirCliente(int id) throws SQLException {
        clienteDAO.excluir(id);
    }
}
