package Controller;

import DAO.AgendamentoDAO;
import Model.Agendamento;
import Model.Cliente;
import Model.Funcionario;
import Model.Servico;
import Utils.DataUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AgendamentoController {
    private final AgendamentoDAO agendamentoDAO;

    public AgendamentoController(Connection conexao) {
        this.agendamentoDAO = new AgendamentoDAO(conexao);
    }

    public void cadastrarAgendamento(Agendamento agendamento) throws SQLException {
        agendamentoDAO.inserir(agendamento);
    }

    public List<Agendamento> listarAgendamentos() throws SQLException {
        return agendamentoDAO.listarTodos();
    }

    public void excluirAgendamento(int id) throws SQLException {
        agendamentoDAO.cancelar(id);
    }

    public boolean verificarConflito(int idFuncionario, String data, String hora) throws SQLException {
        return agendamentoDAO.existeConflito(idFuncionario, data, hora);
    }

    public List<Agendamento> consultarPorFiltros(String cliente, String funcionario, String servico, String data) throws SQLException {
        return agendamentoDAO.consultarPorFiltros(cliente, funcionario, servico, data);
    }

    public void cadastrarAgendamento(
            Cliente cliente,
            Funcionario funcionario,
            Servico servico,
            Date data,
            String hora,
            String observacoes
    ) throws Exception {
        validarCamposAgendamento(cliente, funcionario, servico, data, hora);

        String dataFormatada = DataUtils.formatarPadrao(data);

        if (verificarConflito(funcionario.getId(), dataFormatada, hora)) {
            throw new IllegalArgumentException("Já existe um agendamento para este profissional neste horário.");
        }

        Agendamento agendamento = new Agendamento(0, cliente, funcionario, servico.getNome(), dataFormatada, hora, observacoes);
        cadastrarAgendamento(agendamento);
    }

    private void validarCamposAgendamento(
            Cliente cliente,
            Funcionario funcionario,
            Servico servico,
            Date data,
            String hora
    ) {
        if (cliente == null) throw new IllegalArgumentException("Cliente deve ser selecionado.");
        if (funcionario == null) throw new IllegalArgumentException("Funcionário deve ser selecionado.");
        if (servico == null) throw new IllegalArgumentException("Serviço deve ser selecionado.");
        if (data == null) throw new IllegalArgumentException("Data deve ser informada.");
        if (hora == null || hora.isEmpty()) throw new IllegalArgumentException("Hora deve ser informada.");
    }
}
