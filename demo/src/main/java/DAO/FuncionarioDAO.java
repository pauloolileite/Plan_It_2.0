package DAO;

import Model.Funcionario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    private final Connection conexao;

    public FuncionarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO funcionario (nome, cargo) VALUES (?, ?)";
        // Usando try-with-resources
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.executeUpdate();
        } // stmt.close() automático
    }

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionario"; // Tabela: funcionario
        // Usando try-with-resources
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Funcionario f = new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cargo")
                );
                lista.add(f);
            }
        } // rs e stmt fechados automaticamente
        return lista;
    }

    public Funcionario buscarPorId(int id) throws SQLException {
        // CORREÇÃO: Tabela 'funcionario'
        String sql = "SELECT id, nome, cargo FROM funcionario WHERE id = ?";
        Funcionario funcionario = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    funcionario = new Funcionario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cargo")
                    );
                }
            }
        }
        return funcionario;
    }

    public void atualizar(Funcionario funcionario) throws SQLException {
        // CORREÇÃO: Tabela 'funcionario'
        String sql = "UPDATE funcionario SET nome = ?, cargo = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.setInt(3, funcionario.getId());
            stmt.executeUpdate();
        }
    }

    // --- NOVO MÉTODO: excluir ---
    /**
     * Exclui um funcionário do banco de dados pelo seu ID.
     * @param id O ID do funcionário a ser excluído.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM funcionario WHERE id = ?"; // Tabela: funcionario
        // Usando try-with-resources
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } // stmt.close() automático
    }
    // --- FIM DO NOVO MÉTODO ---
}