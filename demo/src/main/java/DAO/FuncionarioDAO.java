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
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, funcionario.getNome());
        stmt.setString(2, funcionario.getCargo());
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionario";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Funcionario f = new Funcionario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cargo")
            );
            lista.add(f);
        }
        rs.close();
        stmt.close();
        return lista;
    }
}
