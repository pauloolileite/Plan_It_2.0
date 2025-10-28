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
        // Inclui duracao e preco na query
        String sql = "INSERT INTO servico (nome, duracao, preco) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, servico.getNome());
            stmt.setInt(2, servico.getDuracao()); // Adiciona duracao
            stmt.setDouble(3, servico.getPreco()); // Usa preco
            stmt.executeUpdate();
        }
    }

    public List<Servico> listarTodos() throws SQLException {
        List<Servico> lista = new ArrayList<>();
        // Inclui duracao e preco na query
        String sql = "SELECT id, nome, duracao, preco FROM servico";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Servico s = new Servico(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("duracao"), // Obtém duracao
                        rs.getDouble("preco") // Obtém preco
                );
                lista.add(s);
            }
        }
        return lista;
    }

    public Servico buscarPorId(int id) throws SQLException {
        // Inclui duracao e preco na query
        String sql = "SELECT id, nome, duracao, preco FROM servico WHERE id = ?";
        Servico servico = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    servico = new Servico(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getInt("duracao"), // Obtém duracao
                            rs.getDouble("preco") // Obtém preco
                    );
                }
            }
        }
        return servico;
    }

    public void atualizar(Servico servico) throws SQLException {
        // Inclui duracao e preco na query
        String sql = "UPDATE servico SET nome = ?, duracao = ?, preco = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, servico.getNome());
            stmt.setInt(2, servico.getDuracao()); // Atualiza duracao
            stmt.setDouble(3, servico.getPreco()); // Atualiza preco
            stmt.setInt(4, servico.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servico WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}