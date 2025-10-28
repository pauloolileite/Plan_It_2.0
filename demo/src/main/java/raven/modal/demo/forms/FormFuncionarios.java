package raven.modal.demo.forms; // Ou o pacote que você escolheu

import Controller.FuncionarioController; // Importa o Controller
import Model.Funcionario; // Importa o Model
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
import java.text.NumberFormat; // Para formatar salário
import java.time.LocalDate; // Para data de contratação
import java.time.format.DateTimeFormatter; // Para formatar data
import java.util.List;
import java.util.Locale; // Para formatação de moeda

public class FormFuncionarios extends Form {

    private JTable table;
    private DefaultTableModel tableModel;
    private FuncionarioController funcionarioController; // Controller para a lógica de negócio
    private JButton cmdAdicionar;
    private JButton cmdEditar;
    private JButton cmdExcluir;

    // Formatadores para Salário (Moeda Local - Brasil) e Data
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FormFuncionarios() {
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
            this.funcionarioController = new FuncionarioController(conexao);
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

        // Estilo moderno para botões (igual ao FormClientes)
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
                // Colunas baseadas no Model.Funcionario
                new String[]{"ID", "Nome", "CPF", "Telefone", "Endereço", "Cargo", "Salário", "Data Contratação"}
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
        loadFuncionarios();

        // --- Ações dos Botões (Placeholder) ---
        cmdAdicionar.addActionListener(e -> adicionarNovoFuncionario());
        cmdEditar.addActionListener(e -> editarFuncionarioSelecionado());
        cmdExcluir.addActionListener(e -> excluirFuncionarioSelecionado());

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

    private void loadFuncionarios() {
        // Limpa a tabela antes de carregar
        tableModel.setRowCount(0);

        // --- LÓGICA REAL (COMENTADA POR ENQUANTO) ---
        /*
        if (funcionarioController != null) {
            try {
                List<Funcionario> funcionarios = funcionarioController.listarFuncionarios();
                if (funcionarios != null) {
                    for (Funcionario func : funcionarios) {
                        tableModel.addRow(new Object[]{
                                func.getId(),
                                func.getNome(),
                                func.getCpf(),
                                func.getTelefone(),
                                func.getEndereco(),
                                func.getCargo(),
                                currencyFormatter.format(func.getSalario()), // Formata salário
                                func.getDataContratacao().format(dateFormatter) // Formata data
                        });
                    }
                }
            } catch (SQLException e) {
                showDatabaseOperationError("Erro ao carregar funcionários: " + e.getMessage());
            } catch (Exception e) {
                showUnexpectedError("Erro inesperado ao carregar funcionários: " + e.getMessage());
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
        System.out.println("Usando dados de exemplo para funcionários.");
        addSampleData();
        if (funcionarioController == null && isVisible()) { // Avisa apenas se form visível e controller nulo
            showControllerNotInitializedWarning();
        }
        // --- FIM DA LÓGICA TEMPORÁRIA ---
    }


    private void adicionarNovoFuncionario() {
        // TODO: Abrir um ModalDialog com um JPanelCadastroFuncionario
        System.out.println("Ação: Adicionar Novo Funcionário");
        // Exemplo:
        /*
        JPanelCadastroFuncionario panelCadastro = new JPanelCadastroFuncionario();
        boolean ok = ModalDialog.showModal(this, panelCadastro, "Cadastrar Novo Funcionário");
        if (ok) {
            Funcionario novoFuncionario = panelCadastro.getFuncionario(); // Método para pegar o func do painel
            if (funcionarioController != null) {
                try {
                    funcionarioController.cadastrarFuncionario(novoFuncionario);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Funcionário cadastrado com sucesso!");
                    loadFuncionarios(); // Recarrega
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao cadastrar funcionário: " + ex.getMessage());
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

    private void editarFuncionarioSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um funcionário para editar.");
            return;
        }

        int funcionarioId = (int) tableModel.getValueAt(selectedRow, 0);
        System.out.println("Ação: Editar Funcionário ID: " + funcionarioId);

        // TODO: Buscar o funcionário completo pelo ID (funcController.buscarFuncionarioPorId)
        // TODO: Criar e preencher um JPanelCadastroFuncionario
        // TODO: Abrir o ModalDialog com o painel preenchido
        // TODO: No "Salvar", chamar funcController.atualizarFuncionario
        // TODO: Recarregar a tabela

        // Exemplo:
        /*
        if (funcionarioController != null) {
            try {
                Funcionario funcParaEditar = funcionarioController.buscarFuncionarioPorId(funcionarioId); // Precisa implementar no Controller/DAO
                if (funcParaEditar != null) {
                    JPanelCadastroFuncionario panelEdicao = new JPanelCadastroFuncionario();
                    panelEdicao.setFuncionario(funcParaEditar); // Método para preencher o painel

                    boolean ok = ModalDialog.showModal(this, panelEdicao, "Editar Funcionário");
                    if (ok) {
                        Funcionario funcEditado = panelEdicao.getFuncionario();
                        funcionarioController.atualizarFuncionario(funcEditado);
                        Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Funcionário atualizado com sucesso!");
                        loadFuncionarios();
                    }
                } else {
                     Toast.show(this.getRootPane(), Toast.Type.ERROR, "Funcionário não encontrado para edição.");
                }
            } catch (SQLException | IllegalArgumentException ex) {
                showDatabaseOperationError("Erro ao buscar ou atualizar funcionário: " + ex.getMessage());
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

    private void excluirFuncionarioSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um funcionário para excluir.");
            return;
        }

        int funcionarioId = (int) tableModel.getValueAt(selectedRow, 0);
        String funcionarioNome = (String) tableModel.getValueAt(selectedRow, 1);
        System.out.println("Ação: Excluir Funcionário ID: " + funcionarioId);

        // Mostra confirmação
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o funcionário '" + funcionarioNome + "' (ID: " + funcionarioId + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Chamar funcionarioController.excluirFuncionario(funcionarioId)
            // TODO: Recarregar a tabela

            // Exemplo:
             /*
             if (funcionarioController != null) {
                try {
                    funcionarioController.excluirFuncionario(funcionarioId);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Funcionário excluído com sucesso!");
                    loadFuncionarios();
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao excluir funcionário: " + ex.getMessage());
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


    // --- Métodos Auxiliares de Feedback (Iguais ao FormClientes) ---

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
        tableModel.addRow(new Object[]{1, "Ana Souza", "123.456.789-00", "(75) 91111-1111", "Rua A, 10", "Recepcionista", currencyFormatter.format(1800.00), LocalDate.of(2023, 1, 15).format(dateFormatter)});
        tableModel.addRow(new Object[]{2, "Bruno Costa", "987.654.321-11", "(75) 92222-2222", "Rua B, 20", "Designer Gráfico", currencyFormatter.format(2500.50), LocalDate.of(2022, 5, 20).format(dateFormatter)});
        tableModel.addRow(new Object[]{3, "Carla Dias", "111.333.555-77", "(75) 93333-3333", "Rua C, 30", "Atendente", currencyFormatter.format(1750.00), LocalDate.of(2024, 2, 1).format(dateFormatter)});
    }
}