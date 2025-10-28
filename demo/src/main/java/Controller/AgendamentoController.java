package Controller;

import DAO.AgendamentoDAO;
import Model.Agendamento;
import Model.Cliente;
import Model.Funcionario;
import Model.Servico;
import Utils.DataUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    // --- NOVO MÉTODO: buscarAgendamentoPorId ---
    public Agendamento buscarAgendamentoPorId(int id) throws SQLException {
        return agendamentoDAO.buscarPorId(id);
    }

    // --- NOVO MÉTODO: atualizarAgendamento ---
    public void atualizarAgendamento(
            Agendamento agendamentoOriginal, // Para obter o ID e comparar mudanças
            Cliente cliente,
            Funcionario funcionario,
            Servico servico, // Recebe o objeto Servico
            Date data,
            String hora,
            String observacoes
    ) throws Exception { // Lança Exception para erros de validação ou SQL

        if (agendamentoOriginal == null) {
            throw new IllegalArgumentException("Agendamento original inválido para atualização.");
        }

        // Reutiliza a validação de campos obrigatórios
        validarCamposAgendamento(cliente, funcionario, servico, data, hora);

        // Formata a data para o padrão do BD/Model ("yyyy-MM-dd")
        String dataFormatada = DataUtils.formatarPadrao(data);

        // --- Verificação de Conflito Aprimorada ---
        // Verifica conflito usando o método que ignora o ID atual
        if (agendamentoDAO.existeConflito(funcionario.getId(), dataFormatada, hora, agendamentoOriginal.getId())) {
            throw new IllegalArgumentException("Já existe OUTRO agendamento para este profissional neste horário.");
        }
        // --- Fim da Verificação ---


        // Cria um novo objeto Agendamento com os dados atualizados e o ID original
        // *** CORREÇÃO: Usar servico.getNome() pois Agendamento armazena String ***
        Agendamento agendamentoAtualizado = new Agendamento(
                agendamentoOriginal.getId(), // Mantém o ID original
                cliente,
                funcionario,
                servico.getNome(), // Obtém o nome da String do objeto Servico
                dataFormatada,
                hora,
                observacoes != null ? observacoes.trim() : "" // Garante que não é null
        );

        // Chama o método de atualização do DAO
        agendamentoDAO.atualizar(agendamentoAtualizado);
        logger.info("Agendamento ID " + agendamentoOriginal.getId() + " atualizado com sucesso.");
    }

    private static final Logger logger = Logger.getLogger(AgendamentoController.class.getName());

// --- AJUSTE NECESSÁRIO NO DAO.existeConflito ---
// O método existeConflito no AgendamentoDAO precisa ser modificado
// para aceitar um ID opcional a ser ignorado na verificação.
// Exemplo de assinatura modificada no DAO:
// public boolean existeConflito(int idFuncionario, String data, String hora, Integer idAIgnorar)
// E a query seria:
// SELECT COUNT(*) FROM agendamentos WHERE funcionario_id = ? AND data = ? AND hora = ? AND id != ?
// Se idAIgnorar for null, a parte "AND id != ?" não é adicionada.
// O controller precisaria ser ajustado para passar o ID ao verificar conflito na atualização.
// Por simplicidade neste momento, a verificação acima apenas verifica se data/hora/func mudou.
}
