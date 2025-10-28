package raven.modal.demo.forms; // Ou o pacote que você escolheu

import Controller.ServicoController; // Importa o Controller
import Model.Servico; // Importa o Model
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
import java.util.List;
import java.util.Locale; // Para formatação de moeda

public class FormServicos extends Form {

    private JTable table;
    private DefaultTableModel tableModel;
    private ServicoController servicoController; // Controller para a lógica de negócio
    private JButton cmdAdicionar;
    private JButton cmdEditar;
    private JButton cmdExcluir;

    // Formatador para Valor (Moeda Local - Brasil)
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public FormServicos() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        // --- Inicialização do Controller (COMENTADO POR ENQUANTO) ---
        /*
        try {
            Connection conexao = DatabaseConnection.getConnection();
            if (conexao == null) {
                throw new SQLException("Falha ao obter conexão com o banco de dados.");
            }
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
        cmdAdicionar = new JButton("Adicionar Novo");
        cmdEditar = new JButton("Editar Selecionado");
        cmdExcluir = new JButton("Excluir Selecionado");

        // Estilo moderno para botões (igual aos outros Forms)
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
                // Colunas baseadas no Model.Servico
                new String[]{"ID", "Nome", "Valor", "Descrição"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição direta na tabela
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha

        // Adiciona a tabela com barra de rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Carrega os dados na tabela
        loadServicos();

        // --- Ações dos Botões (Placeholder) ---
        cmdAdicionar.addActionListener(e -> adicionarNovoServico());
        cmdEditar.addActionListener(e -> editarServicoSelecionado());
        cmdExcluir.addActionListener(e -> excluirServicoSelecionado());

        // Desabilita botões de edição/exclusão inicialmente
        cmdEditar.setEnabled(false);
        cmdExcluir.setEnabled(false);

        // Habilita/desabilita botões Editar/Excluir ao selecionar linha na tabela
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = table.getSelectedRow() != -1;
            cmdEditar.setEnabled(rowSelected);
            cmdExcluir.setEnabled(rowSelected);
        });

    }

    // --- Métodos de Ação ---

    private void loadServicos() {
        // Limpa a tabela antes de carregar
        tableModel.setRowCount(0);

        // --- LÓGICA REAL (COMENTADA POR ENQUANTO) ---
        /*
        if (servicoController != null) {
            try {
                List<Servico> servicos = servicoController.listarServicos();
                if (servicos != null) {
                    for (Servico serv : servicos) {
                        tableModel.addRow(new Object[]{
                                serv.getId(),
                                serv.getNome(),
                                currencyFormatter.format(serv.getValor()), // Formata valor
                                serv.getDescricao()
                        });
                    }
                }
            } catch (SQLException e) {
                showDatabaseOperationError("Erro ao carregar serviços: " + e.getMessage());
            } catch (Exception e) {
                showUnexpectedError("Erro inesperado ao carregar serviços: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
             // Avisa apenas se form visível e controller nulo
            if (isVisible()) {
                showControllerNotInitializedWarning();
            }
            // Adiciona dados de exemplo se o controller não foi inicializado
            addSampleData();
        }
        */

        // --- LÓGICA TEMPORÁRIA COM DADOS DE EXEMPLO ---
        System.out.println("Usando dados de exemplo para serviços.");
        addSampleData();
        if (servicoController == null && isVisible()) { // Avisa apenas se form visível e controller nulo
            showControllerNotInitializedWarning();
        }
        // --- FIM DA LÓGICA TEMPORÁRIA ---
    }


    private void adicionarNovoServico() {
        // TODO: Abrir um ModalDialog com um JPanelCadastroServico
        System.out.println("Ação: Adicionar Novo Serviço");
        // Exemplo:
        /*
        JPanelCadastroServico panelCadastro = new JPanelCadastroServico();
        boolean ok = ModalDialog.showModal(this, panelCadastro, "Cadastrar Novo Serviço");
        if (ok) {
            Servico novoServico = panelCadastro.getServico(); // Método para pegar o serviço do painel
            if (servicoController != null) {
                try {
                    servicoController.cadastrarServico(novoServico);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Serviço cadastrado com sucesso!");
                    loadServicos(); // Recarrega
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao cadastrar serviço: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao cadastrar: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showControllerNotInitializedWarning();
            }
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Adicionar ainda não implementada.");

    }

    private void editarServicoSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um serviço para editar.");
            return;
        }

        int servicoId = (int) tableModel.getValueAt(selectedRow, 0);
        System.out.println("Ação: Editar Serviço ID: " + servicoId);

        // TODO: Buscar o serviço completo pelo ID (servController.buscarServicoPorId) - Precisa Criar
        // TODO: Criar e preencher um JPanelCadastroServico
        // TODO: Abrir o ModalDialog com o painel preenchido
        // TODO: No "Salvar", chamar servController.atualizarServico
        // TODO: Recarregar a tabela

        // Exemplo:
        /*
        if (servicoController != null) {
            try {
                Servico servParaEditar = servicoController.buscarServicoPorId(servicoId); // Precisa implementar no Controller/DAO
                if (servParaEditar != null) {
                    JPanelCadastroServico panelEdicao = new JPanelCadastroServico();
                    panelEdicao.setServico(servParaEditar); // Método para preencher o painel

                    boolean ok = ModalDialog.showModal(this, panelEdicao, "Editar Serviço");
                    if (ok) {
                        Servico servEditado = panelEdicao.getServico();
                        servicoController.atualizarServico(servEditado);
                        Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Serviço atualizado com sucesso!");
                        loadServicos();
                    }
                } else {
                     Toast.show(this.getRootPane(), Toast.Type.ERROR, "Serviço não encontrado para edição.");
                }
            } catch (SQLException | IllegalArgumentException ex) {
                showDatabaseOperationError("Erro ao buscar ou atualizar serviço: " + ex.getMessage());
            } catch (Exception ex) {
                 showUnexpectedError("Erro inesperado ao editar: " + ex.getMessage());
                 ex.printStackTrace();
            }
        } else {
            showControllerNotInitializedWarning();
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Editar ainda não implementada.");
    }

    private void excluirServicoSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um serviço para excluir.");
            return;
        }

        int servicoId = (int) tableModel.getValueAt(selectedRow, 0);
        String servicoNome = (String) tableModel.getValueAt(selectedRow, 1);
        System.out.println("Ação: Excluir Serviço ID: " + servicoId);

        // Mostra confirmação
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o serviço '" + servicoNome + "' (ID: " + servicoId + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Chamar servicoController.excluirServico(servicoId)
            // TODO: Recarregar a tabela

            // Exemplo:
             /*
             if (servicoController != null) {
                try {
                    servicoController.excluirServico(servicoId);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Serviço excluído com sucesso!");
                    loadServicos();
                } catch (SQLException | IllegalArgumentException ex) {
                    // Tratar erro específico de FK aqui se necessário (ex: serviço usado em agendamento)
                    showDatabaseOperationError("Erro ao excluir serviço: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao excluir: " + ex.getMessage());
                    ex.printStackTrace();
                }
             } else {
                showControllerNotInitializedWarning();
             }
             */
            Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Excluir (lógica do backend) ainda não implementada.");
        }
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
        tableModel.addRow(new Object[]{1, "Impressão A4 Preto/Branco", currencyFormatter.format(0.50), "Impressão simples em papel A4"});
        tableModel.addRow(new Object[]{2, "Impressão A4 Colorida", currencyFormatter.format(1.50), "Impressão colorida em papel A4"});
        tableModel.addRow(new Object[]{3, "Encadernação Espiral", currencyFormatter.format(5.00), "Encadernação com capa plástica e espiral"});
        tableModel.addRow(new Object[]{4, "Plastificação RG", currencyFormatter.format(3.00), "Plastificação de documento tamanho RG"});
    }
}