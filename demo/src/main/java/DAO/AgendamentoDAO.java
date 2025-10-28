package DAO;

import Model.Agendamento;
import Model.Cliente;
import Model.Funcionario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {
    private final Connection conexao;

    public AgendamentoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Agendamento agendamento) throws SQLException {
        String sql = "INSERT INTO agendamento (id_cliente, id_funcionario, servico, data, hora, observacoes, status) VALUES (?, ?, ?, ?, ?, ?, 'ativo')";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, agendamento.getCliente().getId());
        stmt.setInt(2, agendamento.getFuncionario().getId());
        stmt.setString(3, agendamento.getServico());
        stmt.setString(4, agendamento.getData());
        stmt.setString(5, agendamento.getHora());
        stmt.setString(6, agendamento.getObservacoes());
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Agendamento> listarTodos() throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT a.*, c.nome AS cliente_nome, c.telefone, c.email, f.nome AS funcionario_nome, f.cargo " +
                "FROM agendamento a " +
                "JOIN cliente c ON a.id_cliente = c.id " +
                "JOIN funcionario f ON a.id_funcionario = f.id " +
                "WHERE a.status = 'ativo'";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Cliente cliente = new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("cliente_nome"),
                    rs.getString("telefone"),
                    rs.getString("email")
            );
            Funcionario funcionario = new Funcionario(
                    rs.getInt("id_funcionario"),
                    rs.getString("funcionario_nome"),
                    rs.getString("cargo")
            );
            Agendamento ag = new Agendamento(
                    rs.getInt("id"),
                    cliente,
                    funcionario,
                    rs.getString("servico"),
                    rs.getString("data"),
                    rs.getString("hora"),
                    rs.getString("observacoes")
            );
            lista.add(ag);
        }
        rs.close();
        stmt.close();
        return lista;
    }

    public void cancelar(int id) throws SQLException {
        String sql = "UPDATE agendamento SET status = 'cancelado' WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    public boolean existeConflito(int idFuncionario, String data, String hora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agendamento WHERE id_funcionario = ? AND data = ? AND hora = ? AND status = 'ativo'";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, idFuncionario);
        stmt.setString(2, data);
        stmt.setString(3, hora);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        boolean conflito = rs.getInt(1) > 0;
        rs.close();
        stmt.close();
        return conflito;
    }

    public List<Agendamento> consultarPorFiltros(String cliente, String funcionario, String servico, String data) throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT a.*, c.nome AS cliente_nome, c.telefone, c.email, f.nome AS funcionario_nome, f.cargo " +
                "FROM agendamento a " +
                "JOIN cliente c ON a.id_cliente = c.id " +
                "JOIN funcionario f ON a.id_funcionario = f.id " +
                "WHERE a.status = 'ativo' ";

        if (!cliente.isEmpty()) sql += "AND c.nome LIKE ? ";
        if (!funcionario.isEmpty()) sql += "AND f.nome LIKE ? ";
        if (!servico.isEmpty()) sql += "AND a.servico LIKE ? ";
        if (!data.isEmpty()) sql += "AND a.data = ? ";

        PreparedStatement stmt = conexao.prepareStatement(sql);
        int index = 1;
        if (!cliente.isEmpty()) stmt.setString(index++, "%" + cliente + "%");
        if (!funcionario.isEmpty()) stmt.setString(index++, "%" + funcionario + "%");
        if (!servico.isEmpty()) stmt.setString(index++, "%" + servico + "%");
        if (!data.isEmpty()) stmt.setString(index++, data);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Cliente cli = new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("cliente_nome"),
                    rs.getString("telefone"),
                    rs.getString("email")
            );
            Funcionario func = new Funcionario(
                    rs.getInt("id_funcionario"),
                    rs.getString("funcionario_nome"),
                    rs.getString("cargo")
            );
            Agendamento ag = new Agendamento(
                    rs.getInt("id"),
                    cli,
                    func,
                    rs.getString("servico"),
                    rs.getString("data"),
                    rs.getString("hora"),
                    rs.getString("observacoes")
            );
            lista.add(ag);
        }
        rs.close();
        stmt.close();
        return lista;
    }
}
