package DAO;

import Model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final Connection conexao;

    public ClienteDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, email) VALUES (?, ?, ?)";
        // Usando try-with-resources para garantir que o PreparedStatement seja fechado
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());

            // --- MODIFICAÇÃO PARA EVITAR NULL ---
            // Se o email for null ou vazio, insere uma string vazia "" em vez de null.
            // Isto só funciona se a coluna NÃO permitir NULL no banco de dados,
            // mas você quer inserir mesmo assim. O ideal é permitir NULL na coluna.
            if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
                stmt.setString(3, ""); // Insere string vazia
            } else {
                stmt.setString(3, cliente.getEmail()); // Insere o email fornecido
            }
            // --- FIM DA MODIFICAÇÃO ---

            stmt.executeUpdate();
        } // stmt.close() é chamado automaticamente aqui
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        // Usando try-with-resources
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                lista.add(c);
            }
        } // rs.close() e stmt.close() são chamados automaticamente
        return lista;
    }

    public boolean telefoneExiste(String telefone) {
        // Usando try-with-resources
        String sql = "SELECT COUNT(*) FROM cliente WHERE telefone = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, telefone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // É bom logar o erro ou lançar uma exceção específica da aplicação
            e.printStackTrace();
        }
        return false;
    }

    public void atualizar(Cliente cliente) throws SQLException {
        // CORREÇÃO: Nome da tabela estava errado (clientes em vez de cliente)
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());

            // --- MODIFICAÇÃO PARA EVITAR NULL (similar ao inserir) ---
            if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
                stmt.setString(3, ""); // Atualiza para string vazia
            } else {
                stmt.setString(3, cliente.getEmail()); // Atualiza com o email fornecido
            }
            // --- FIM DA MODIFICAÇÃO ---

            stmt.setInt(4, cliente.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        // Usando try-with-resources
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } // stmt.close() é chamado automaticamente
    }

    public Cliente buscarPorId(int id) throws SQLException {
        // CORREÇÃO: Nome da tabela estava errado (clientes em vez de cliente)
        String sql = "SELECT id, nome, telefone, email FROM cliente WHERE id = ?";
        Cliente cliente = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("email")
                    );
                }
            }
        }
        return cliente;
    }
}