package DAO;

import Model.Agendamento;
import Model.Cliente;
import Model.Funcionario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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

    private static final Logger logger = Logger.getLogger(AgendamentoDAO.class.getName());

    public boolean existeConflito(int idFuncionario, String data, String hora) throws SQLException {
        // Chama a versão mais completa passando null para o ID a ignorar
        return existeConflito(idFuncionario, data, hora, null);
    }

    /**
     * Verifica se existe conflito de horário para um funcionário em uma data/hora específica,
     * opcionalmente ignorando um ID de agendamento (útil ao EDITAR).
     * @param idFuncionario ID do funcionário.
     * @param data Data no formato "yyyy-MM-dd".
     * @param hora Hora no formato "HH:mm".
     * @param idAgendamentoIgnorar O ID do agendamento a ser ignorado na verificação (pode ser null).
     * @return true se houver conflito com OUTRO agendamento, false caso contrário.
     * @throws SQLException Se ocorrer erro no banco de dados.
     */
    public boolean existeConflito(int idFuncionario, String data, String hora, Integer idAgendamentoIgnorar) throws SQLException {
        // Constrói a query base
        String sql = "SELECT COUNT(*) FROM agendamentos WHERE funcionario_id = ? AND data = ? AND hora = ?";

        // Adiciona a condição para ignorar um ID específico, se fornecido
        if (idAgendamentoIgnorar != null && idAgendamentoIgnorar > 0) {
            sql += " AND id != ?";
        }

        logger.fine("Verificando conflito com SQL: " + sql + " | Params: [FuncID=" + idFuncionario + ", Data=" + data + ", Hora=" + hora + (idAgendamentoIgnorar != null ? ", IgnorarID=" + idAgendamentoIgnorar : "") + "]");

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            stmt.setString(2, data);
            stmt.setString(3, hora);
            // Define o quarto parâmetro apenas se necessário
            if (idAgendamentoIgnorar != null && idAgendamentoIgnorar > 0) {
                stmt.setInt(4, idAgendamentoIgnorar);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean conflito = rs.getInt(1) > 0;
                    logger.fine("Resultado da verificação de conflito: " + conflito);
                    return conflito;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao verificar conflito para Funcionario ID: " + idFuncionario + " em " + data + " " + hora, e);
            throw e; // Relança a exceção
        }
        // Retorna false em caso de erro inesperado (embora exceção seja lançada acima)
        return false;
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
    // --- MÉTODO buscarPorId CORRIGIDO ---
    public Agendamento buscarPorId(int id) throws SQLException {
        // Ajuste na query SQL para buscar 'cargo' do funcionário em vez de telefone/email/tipo
        String sql = "SELECT a.id as agendamento_id, a.data, a.hora, a.observacoes, a.servico as nome_servico, " +
                "c.id as cliente_id, c.nome as cliente_nome, c.telefone as cliente_telefone, c.email as cliente_email, " +
                "f.id as funcionario_id, f.nome as funcionario_nome, f.cargo as funcionario_cargo " + // <- Busca f.cargo
                "FROM agendamentos a " +
                "JOIN clientes c ON a.cliente_id = c.id " +
                "JOIN funcionarios f ON a.funcionario_id = f.id " +
                "WHERE a.id = ?";
        Agendamento agendamento = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente(
                            rs.getInt("cliente_id"),
                            rs.getString("cliente_nome"),
                            rs.getString("cliente_telefone"),
                            rs.getString("cliente_email")
                    );

                    // --- CORREÇÃO AQUI ---
                    // Cria o Funcionario usando o construtor correto (id, nome, cargo)
                    Funcionario funcionario = new Funcionario(
                            rs.getInt("funcionario_id"),
                            rs.getString("funcionario_nome"),
                            rs.getString("funcionario_cargo") // <- Usa o cargo obtido da query
                    );
                    // --- FIM DA CORREÇÃO ---

                    agendamento = new Agendamento(
                            rs.getInt("agendamento_id"),
                            cliente,
                            funcionario,
                            rs.getString("nome_servico"),
                            rs.getString("data"),
                            rs.getString("hora"),
                            rs.getString("observacoes")
                    );
                }
            }
        }
        return agendamento; // Retorna null se não encontrar
    }

    // --- NOVO MÉTODO: atualizar ---
    public void atualizar(Agendamento agendamento) throws SQLException {
        String sql = "UPDATE agendamentos SET cliente_id = ?, funcionario_id = ?, servico = ?, data = ?, hora = ?, observacoes = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, agendamento.getCliente().getId());
            stmt.setInt(2, agendamento.getFuncionario().getId());
            stmt.setString(3, agendamento.getServico());
            stmt.setString(4, agendamento.getData()); // Data como String
            stmt.setString(5, agendamento.getHora());   // Hora como String
            stmt.setString(6, agendamento.getObservacoes());
            stmt.setInt(7, agendamento.getId());
            stmt.executeUpdate();
        }
    }
}
