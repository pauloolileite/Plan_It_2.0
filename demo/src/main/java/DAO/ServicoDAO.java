package DAO;

import Model.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {
    private final Connection conexao;

    public ServicoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Servico servico) throws SQLException {
        String sql = "INSERT INTO servico (nome, duracao, preco) VALUES (?, ?, ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, servico.getNome());
        stmt.setInt(2, servico.getDuracao());
        stmt.setDouble(3, servico.getPreco());
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Servico> listarTodos() throws SQLException {
        List<Servico> lista = new ArrayList<>();
        String sql = "SELECT * FROM servico";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Servico s = new Servico(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("duracao"),
                    rs.getDouble("preco")
            );
            lista.add(s);
        }
        rs.close();
        stmt.close();
        return lista;
    }

    public int buscarIdPorNome(String nome) throws SQLException {
        String sql = "SELECT id FROM servico WHERE nome = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, nome);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            rs.close();
            stmt.close();
            return id;
        }
        rs.close();
        stmt.close();
        throw new SQLException("Serviço não encontrado com nome: " + nome);
    }

    public void atualizar(Servico servico) throws SQLException {
        String sql = "UPDATE servico SET nome = ?, duracao = ?, preco = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, servico.getNome());
        stmt.setInt(2, servico.getDuracao());
        stmt.setDouble(3, servico.getPreco());
        stmt.setInt(4, servico.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void excluirServico(int id) throws SQLException {
        String sql = "DELETE FROM servico WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    public boolean existePorNome(String nome) {
        String sql = "SELECT COUNT(*) FROM servico WHERE nome = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar serviço por nome: " + e.getMessage(), e);
        }
        return false;
    }

}