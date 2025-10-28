package raven.modal.demo.forms; // Ou o pacote que você escolheu

import Controller.AgendamentoController; // Importa o Controller
import Controller.ClienteController; // Necessário para buscar nomes
import Controller.FuncionarioController; // Necessário para buscar nomes
import Controller.ServicoController; // Necessário para buscar nomes/valores
import Model.Agendamento; // Importa o Model principal
import Model.Cliente;
import Model.Funcionario;
import Model.Servico;
import Utils.DatabaseConnection; // Importa a classe de conexão
import com.formdev.flatlaf.FlatClientProperties;
import raven.modal.demo.system.Form;
import raven.modal.ModalDialog; // Para futuros diálogos de cadastro/edição
import raven.modal.Toast; // Para mensagens de feedback

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat; // Para formatar valor
import java.time.LocalDateTime; // Para data e hora do agendamento
import java.time.format.DateTimeFormatter; // Para formatar data/hora
import java.util.List;
import java.util.Locale; // Para formatação de moeda
import java.util.Map; // Para buscar nomes/valores associados (quando o banco funcionar)
import java.util.HashMap; // Para buscar nomes/valores associados (quando o banco funcionar)

public class FormAgendamentos extends Form {

    private JTable table;
    private DefaultTableModel tableModel;
    private AgendamentoController agendamentoController; // Controller principal
    // Controllers auxiliares para buscar dados relacionados (necessário quando o banco estiver ativo)
    private ClienteController clienteController;
    private FuncionarioController funcionarioController;
    private ServicoController servicoController;

    private JButton cmdAdicionar;
    private JButton cmdEditar;
    private JButton cmdExcluir;

    // Formatadores
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FormAgendamentos() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        // --- Inicialização dos Controllers (COMENTADO POR ENQUANTO) ---
        /*
        try {
            Connection conexao = DatabaseConnection.getConnection();
            if (conexao == null) {
                throw new SQLException("Falha ao obter conexão com o banco de dados.");
            }
            // Inicializa todos os controllers necessários
            this.agendamentoController = new AgendamentoController(conexao);
            this.clienteController = new ClienteController(conexao);
            this.funcionarioController = new FuncionarioController(conexao);
            this.servicoController = new ServicoController(conexao);

        } catch (SQLException e) {
            showDatabaseConnectionError(e.getMessage());
        } catch (Exception e) {
            showUnexpectedError(e.getMessage());
            e.printStackTrace();
        }
        */

        // Cria o painel principal com padding
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(panel, BorderLayout.CENTER);

        // Cria o painel de botões
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cmdAdicionar = new JButton("Agendar Novo");
        cmdEditar = new JButton("Editar Selecionado");
        cmdExcluir = new JButton("Excluir Selecionado");

        // Estilo moderno para botões
        cmdAdicionar.putClientProperty(FlatClientProperties.STYLE, "[light]background:tint(@background,50%);[dark]background:tint(@background,50%);borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10;");
        cmdEditar.putClientProperty(FlatClientProperties.STYLE, "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10;");
        cmdExcluir.putClientProperty(FlatClientProperties.STYLE, "[light]background:tint(#f44336, 90%);foreground:tint(#f44336, 10%);[dark]background:tint(#f44336, 30%);foreground:tint(#f44336, 90%);borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10;");

        commandPanel.add(cmdAdicionar);
        commandPanel.add(cmdEditar);
        commandPanel.add(cmdExcluir);

        // Adiciona painel de botões no topo
        panel.add(commandPanel, BorderLayout.NORTH);

        // Cria a tabela
        tableModel = new DefaultTableModel(
                new Object[][]{}, // Dados iniciais vazios
                // Colunas baseadas no Model.Agendamento e dados relacionados
                new String[]{"ID", "Cliente", "Funcionário", "Serviço", "Data/Hora", "Valor Total", "Observação"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição direta
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Adiciona a tabela com barra de rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Carrega os dados na tabela
        loadAgendamentos();

        // --- Ações dos Botões (Placeholder) ---
        cmdAdicionar.addActionListener(e -> adicionarNovoAgendamento());
        cmdEditar.addActionListener(e -> editarAgendamentoSelecionado());
        cmdExcluir.addActionListener(e -> excluirAgendamentoSelecionado());

        // Desabilita botões de edição/exclusão inicialmente
        cmdEditar.setEnabled(false);
        cmdExcluir.setEnabled(false);

        // Habilita/desabilita botões Editar/Excluir ao selecionar linha
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = table.getSelectedRow() != -1;
            cmdEditar.setEnabled(rowSelected);
            cmdExcluir.setEnabled(rowSelected);
        });

    }

    // --- Métodos de Ação ---

    private void loadAgendamentos() {
        tableModel.setRowCount(0);

        // --- LÓGICA REAL (COMENTADA POR ENQUANTO) ---
        /*
        if (agendamentoController != null && clienteController != null && funcionarioController != null && servicoController != null) {
            try {
                List<Agendamento> agendamentos = agendamentoController.listarAgendamentos();

                // Para otimizar, podemos buscar todos os clientes, funcs e serviços de uma vez
                // ou buscar um por um dentro do loop (menos eficiente para muitos agendamentos)
                // Exemplo buscando um por um (simplificado):
                if (agendamentos != null) {
                    for (Agendamento ag : agendamentos) {
                        String nomeCliente = "N/D";
                        String nomeFuncionario = "N/D";
                        String nomeServico = "N/D";
                        double valorServico = ag.getValor(); // Usa o valor salvo no agendamento

                        try {
                            Cliente cli = clienteController.buscarClientePorId(ag.getClienteId());
                            if (cli != null) nomeCliente = cli.getNome();
                        } catch (SQLException exCli) { System.err.println("Erro ao buscar cliente ID " + ag.getClienteId()); }

                        try {
                            Funcionario func = funcionarioController.buscarFuncionarioPorId(ag.getFuncionarioId());
                            if (func != null) nomeFuncionario = func.getNome();
                        } catch (SQLException exFunc) { System.err.println("Erro ao buscar funcionário ID " + ag.getFuncionarioId()); }

                         try {
                            Servico serv = servicoController.buscarServicoPorId(ag.getServicoId()); // Método precisa existir
                            if (serv != null) nomeServico = serv.getNome();
                            // Poderia recalcular o valor aqui se necessário: valorServico = serv.getValor();
                        } catch (SQLException exServ) { System.err.println("Erro ao buscar serviço ID " + ag.getServicoId()); }


                        tableModel.addRow(new Object[]{
                                ag.getId(),
                                nomeCliente, // Exibe o nome
                                nomeFuncionario, // Exibe o nome
                                nomeServico, // Exibe o nome
                                ag.getDataHora().format(dateTimeFormatter), // Formata data/hora
                                currencyFormatter.format(valorServico), // Formata valor
                                ag.getObservacao()
                        });
                    }
                }
            } catch (SQLException e) {
                showDatabaseOperationError("Erro ao carregar agendamentos: " + e.getMessage());
            } catch (Exception e) {
                showUnexpectedError("Erro inesperado ao carregar agendamentos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
             if (isVisible()) {
                showControllerNotInitializedWarning();
             }
            addSampleData();
        }
        */

        // --- LÓGICA TEMPORÁRIA COM DADOS DE EXEMPLO ---
        System.out.println("Usando dados de exemplo para agendamentos.");
        addSampleData();
        if (agendamentoController == null && isVisible()) { // Avisa apenas se form visível e controller nulo
            showControllerNotInitializedWarning();
        }
        // --- FIM DA LÓGICA TEMPORÁRIA ---
    }


    private void adicionarNovoAgendamento() {
        // TODO: Abrir um ModalDialog com um JPanelCadastroAgendamento
        //      Este painel precisará de ComboBoxes para selecionar Cliente, Funcionário, Serviço
        //      e um seletor de Data/Hora (pode usar bibliotecas externas ou JSpinner)
        System.out.println("Ação: Adicionar Novo Agendamento");
        // Exemplo:
        /*
        if (controllersProntos()) { // Verifica se todos os controllers estão ok
            JPanelCadastroAgendamento panelCadastro = new JPanelCadastroAgendamento(clienteController, funcionarioController, servicoController); // Passa controllers para popular combos
            boolean ok = ModalDialog.showModal(this, panelCadastro, "Novo Agendamento");
            if (ok) {
                Agendamento novoAgendamento = panelCadastro.getAgendamento(); // Método para pegar o agendamento do painel
                try {
                    agendamentoController.agendar(novoAgendamento);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Agendamento realizado com sucesso!");
                    loadAgendamentos(); // Recarrega
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao salvar agendamento: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao salvar agendamento: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
             showControllerNotInitializedWarning();
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Agendar ainda não implementada.");
    }

    private void editarAgendamentoSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um agendamento para editar.");
            return;
        }

        int agendamentoId = (int) tableModel.getValueAt(selectedRow, 0);
        System.out.println("Ação: Editar Agendamento ID: " + agendamentoId);

        // TODO: Buscar o agendamento completo pelo ID (agendamentoController.buscarPorId) - Precisa Criar
        // TODO: Criar e preencher um JPanelCadastroAgendamento
        // TODO: Abrir o ModalDialog com o painel preenchido
        // TODO: No "Salvar", chamar agendamentoController.atualizarAgendamento - Precisa Criar
        // TODO: Recarregar a tabela

        // Exemplo:
        /*
        if (controllersProntos()) {
             try {
                Agendamento agParaEditar = agendamentoController.buscarAgendamentoPorId(agendamentoId); // Precisa implementar
                if (agParaEditar != null) {
                    JPanelCadastroAgendamento panelEdicao = new JPanelCadastroAgendamento(clienteController, funcionarioController, servicoController);
                    panelEdicao.setAgendamento(agParaEditar); // Método para preencher o painel

                    boolean ok = ModalDialog.showModal(this, panelEdicao, "Editar Agendamento");
                    if (ok) {
                        Agendamento agEditado = panelEdicao.getAgendamento();
                        agendamentoController.atualizarAgendamento(agEditado); // Precisa implementar
                        Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Agendamento atualizado com sucesso!");
                        loadAgendamentos();
                    }
                } else {
                     Toast.show(this.getRootPane(), Toast.Type.ERROR, "Agendamento não encontrado para edição.");
                }
            } catch (SQLException | IllegalArgumentException ex) {
                showDatabaseOperationError("Erro ao buscar ou atualizar agendamento: " + ex.getMessage());
            } catch (Exception ex) {
                 showUnexpectedError("Erro inesperado ao editar agendamento: " + ex.getMessage());
                 ex.printStackTrace();
            }
        } else {
            showControllerNotInitializedWarning();
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Editar ainda não implementada.");
    }

    private void excluirAgendamentoSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um agendamento para excluir.");
            return;
        }

        int agendamentoId = (int) tableModel.getValueAt(selectedRow, 0);
        String clienteNome = (String) tableModel.getValueAt(selectedRow, 1); // Pegar nome do cliente da tabela
        String dataHora = (String) tableModel.getValueAt(selectedRow, 4); // Pegar data/hora da tabela
        System.out.println("Ação: Excluir Agendamento ID: " + agendamentoId);

        // Mostra confirmação
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o agendamento do cliente '" + clienteNome + "'\npara " + dataHora + " (ID: " + agendamentoId + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Chamar agendamentoController.excluirAgendamento(agendamentoId)
            // TODO: Recarregar a tabela

            // Exemplo:
             /*
             if (agendamentoController != null) {
                try {
                    agendamentoController.excluirAgendamento(agendamentoId);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Agendamento excluído com sucesso!");
                    loadAgendamentos();
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao excluir agendamento: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao excluir agendamento: " + ex.getMessage());
                    ex.printStackTrace();
                }
             } else {
                showControllerNotInitializedWarning();
             }
             */
            Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Excluir (lógica do backend) ainda não implementada.");
        }
    }

    // --- Métodos Auxiliares ---

    // Verifica se todos os controllers necessários estão inicializados
    private boolean controllersProntos() {
        return agendamentoController != null && clienteController != null && funcionarioController != null && servicoController != null;
    }

    // --- Métodos Auxiliares de Feedback (Iguais aos outros Forms) ---

    private void showDatabaseConnectionError(String message) {
        JOptionPane.showMessageDialog(this, "Não foi possível conectar ao banco de dados.\n" + message + "\nVerifique as configurações e o servidor.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        cmdAdicionar.setEnabled(false);
        cmdEditar.setEnabled(false);
        cmdExcluir.setEnabled(false);
    }

    private void showDatabaseOperationError(String message) {
        Toast.show(this.getRootPane(), Toast.Type.ERROR, message);
    }

    private void showUnexpectedError(String message) {
        JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado:\n" + message, "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
    }

    private void showControllerNotInitializedWarning() {
        Toast.show(this.getRootPane(), Toast.Type.WARNING, "Operação não disponível (sem conexão com banco). Usando dados de exemplo.");
    }


    // --- Método para Dados de Exemplo (Temporário) ---
    private void addSampleData() {
        // IDs fictícios para Cliente, Funcionario, Servico
        // No mundo real, viriam dos objetos Agendamento
        tableModel.addRow(new Object[]{
                1, "João Silva", "Ana Souza", "Impressão A4 P/B",
                LocalDateTime.of(2025, 10, 28, 15, 30).format(dateTimeFormatter),
                currencyFormatter.format(10.50), "10 cópias coloridas + 1 P/B"
        });
        tableModel.addRow(new Object[]{
                2, "Maria Oliveira", "Bruno Costa", "Encadernação Espiral",
                LocalDateTime.of(2025, 10, 29, 10, 00).format(dateTimeFormatter),
                currencyFormatter.format(7.00), "Trabalho escolar"
        });
        tableModel.addRow(new Object[]{
                3, "Carlos Pereira", "Carla Dias", "Plastificação RG",
                LocalDateTime.of(2025, 10, 29, 11, 15).format(dateTimeFormatter),
                currencyFormatter.format(3.00), ""
        });
    }
}