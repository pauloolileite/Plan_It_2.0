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
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getTelefone());
        stmt.setString(3, cliente.getEmail());
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Cliente c = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("telefone"),
                    rs.getString("email")
            );
            lista.add(c);
        }
        rs.close();
        stmt.close();
        return lista;
    }

    public boolean telefoneExiste(String telefone) {
        try {
            var stmt = conexao.prepareStatement("SELECT COUNT(*) FROM cliente WHERE telefone = ?");
            stmt.setString(1, telefone);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getTelefone());
        stmt.setString(3, cliente.getEmail());
        stmt.setInt(4, cliente.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }
}
