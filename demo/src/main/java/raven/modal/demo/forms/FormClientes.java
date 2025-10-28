package raven.modal.demo.forms; // Ou o pacote que você escolheu (ex: com.planit.forms)

import Controller.ClienteController; // Importa o Controller
import Model.Cliente; // Importa o Model
import Utils.DatabaseConnection; // Importa a classe de conexão
import com.formdev.flatlaf.FlatClientProperties;
// import raven.modal.demo.sample.SampleData; // Removido, dados de exemplo colocados diretamente
import raven.modal.demo.system.Form;
import raven.modal.ModalDialog; // Para futuros diálogos de cadastro/edição
import raven.modal.Toast; // <<< --- CORREÇÃO APLICADA AQUI ---

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList; // Para a lista de exemplo

public class FormClientes extends Form {

    private JTable table;
    private DefaultTableModel tableModel;
    private ClienteController clienteController; // Controller para a lógica de negócio
    private JButton cmdAdicionar;
    private JButton cmdEditar;
    private JButton cmdExcluir;

    public FormClientes() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        // --- Inicialização do Controller (COMENTADO POR ENQUANTO) ---

        try {
            Connection conexao = DatabaseConnection.getConnection();
            if (conexao == null) {
                throw new SQLException("Falha ao obter conexão com o banco de dados.");
            }
            this.clienteController = new ClienteController(conexao);
        } catch (SQLException e) {
            // Exibe um erro se não conseguir conectar ao banco
            showDatabaseConnectionError(e.getMessage());
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            showUnexpectedError(e.getMessage());
            e.printStackTrace();
        }


        // Cria o painel principal com padding
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(panel, BorderLayout.CENTER);

        // Cria o painel de botões
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cmdAdicionar = new JButton("Adicionar Novo");
        cmdEditar = new JButton("Editar Selecionado");
        cmdExcluir = new JButton("Excluir Selecionado");

        // Estilo moderno para botões (opcional)
        cmdAdicionar.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:tint(@background,50%);" +
                "[dark]background:tint(@background,50%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:10;"); // Botão Novo com destaque
        cmdEditar.putClientProperty(FlatClientProperties.STYLE, "" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:10;");
        cmdExcluir.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:tint(#f44336, 90%);foreground:tint(#f44336, 10%);" +
                "[dark]background:tint(#f44336, 30%);foreground:tint(#f44336, 90%);" + // Estilo para botão Excluir
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:10;");


        commandPanel.add(cmdAdicionar);
        commandPanel.add(cmdEditar);
        commandPanel.add(cmdExcluir);

        // Adiciona painel de botões no topo
        panel.add(commandPanel, BorderLayout.NORTH);

        // Cria a tabela
        tableModel = new DefaultTableModel(
                new Object[][]{}, // Dados iniciais vazios
                new String[]{"ID", "Nome", "CPF", "Telefone", "Endereço"} // Colunas
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
        loadClientes();

        // --- Ações dos Botões (Placeholder) ---
        cmdAdicionar.addActionListener(e -> adicionarNovoCliente());
        cmdEditar.addActionListener(e -> editarClienteSelecionado());
        cmdExcluir.addActionListener(e -> excluirClienteSelecionado());

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

    private void loadClientes() {
        // Limpa a tabela antes de carregar
        tableModel.setRowCount(0);

        // --- LÓGICA REAL (COMENTADA POR ENQUANTO) ---
        /*
        if (clienteController != null) {
            try {
                List<Cliente> clientes = clienteController.listarClientes();
                if (clientes != null) {
                    for (Cliente cliente : clientes) {
                        tableModel.addRow(new Object[]{
                                cliente.getId(),
                                cliente.getNome(),
                                cliente.getCpf(),
                                cliente.getTelefone(),
                                cliente.getEndereco()
                        });
                    }
                }
            } catch (SQLException e) {
                // Exibe um erro se não conseguir buscar os clientes
                showDatabaseOperationError("Erro ao carregar clientes: " + e.getMessage());
            } catch (Exception e) {
                // Captura outras exceções inesperadas
                showUnexpectedError("Erro inesperado ao carregar clientes: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
             // Apenas mostra a mensagem se a conexão já não falhou antes
             //if (this.isVisible() && !databaseConnectionFailed) { // Evita mostrar antes do form ser visível ou se conexão já falhou
             //   showControllerNotInitializedWarning();
             //}
            // Adiciona dados de exemplo se o controller não foi inicializado
            addSampleData();
        }
        */

        // --- LÓGICA TEMPORÁRIA COM DADOS DE EXEMPLO ---
        System.out.println("Usando dados de exemplo para clientes.");
        addSampleData();
        if (clienteController == null && isVisible()) { // Avisa apenas se form visível e controller nulo
            showControllerNotInitializedWarning();
        }
        // --- FIM DA LÓGICA TEMPORÁRIA ---
    }


    private void adicionarNovoCliente() {
        // TODO: Abrir um ModalDialog com um JPanel para inserir os dados do novo cliente
        System.out.println("Ação: Adicionar Novo Cliente");
        // Exemplo de como seria (precisa criar JPanelCadastroCliente):
        /*
        JPanelCadastroCliente panelCadastro = new JPanelCadastroCliente();
        boolean ok = ModalDialog.showModal(this, panelCadastro, "Cadastrar Novo Cliente");
        if (ok) {
            Cliente novoCliente = panelCadastro.getCliente();
            if (clienteController != null) {
                try {
                    clienteController.cadastrarCliente(novoCliente);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Cliente cadastrado com sucesso!"); // Usar getRootPane() para Toast
                    loadClientes(); // Recarrega a tabela
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao cadastrar cliente: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao cadastrar: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showControllerNotInitializedWarning();
            }
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Adicionar ainda não implementada."); // Usar getRootPane() para Toast

    }

    private void editarClienteSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um cliente para editar."); // Usar getRootPane() para Toast
            return;
        }

        // Obtém o ID do cliente da linha selecionada (assumindo que ID é a primeira coluna)
        int clienteId = (int) tableModel.getValueAt(selectedRow, 0);
        System.out.println("Ação: Editar Cliente ID: " + clienteId);

        // TODO: Buscar o cliente completo pelo ID usando clienteController.buscarClientePorId(clienteId)
        // TODO: Abrir um ModalDialog (JPanelCadastroCliente) preenchido com os dados do cliente
        // TODO: No "Salvar" do modal, chamar clienteController.atualizarCliente(clienteEditado)
        // TODO: Recarregar a tabela (loadClientes())

        // Exemplo de como seria (precisa criar JPanelCadastroCliente e buscarClientePorId):
        /*
        if (clienteController != null) {
            try {
                Cliente clienteParaEditar = clienteController.buscarClientePorId(clienteId);
                if (clienteParaEditar != null) {
                    JPanelCadastroCliente panelEdicao = new JPanelCadastroCliente();
                    panelEdicao.setCliente(clienteParaEditar); // Método para preencher o painel

                    boolean ok = ModalDialog.showModal(this, panelEdicao, "Editar Cliente");
                    if (ok) {
                        Cliente clienteEditado = panelEdicao.getCliente();
                        clienteController.atualizarCliente(clienteEditado);
                        Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Cliente atualizado com sucesso!"); // Usar getRootPane() para Toast
                        loadClientes(); // Recarrega a tabela
                    }
                } else {
                     Toast.show(this.getRootPane(), Toast.Type.ERROR, "Cliente não encontrado para edição."); // Usar getRootPane() para Toast
                }
            } catch (SQLException | IllegalArgumentException ex) {
                showDatabaseOperationError("Erro ao buscar ou atualizar cliente: " + ex.getMessage());
            } catch (Exception ex) {
                 showUnexpectedError("Erro inesperado ao editar: " + ex.getMessage());
                 ex.printStackTrace();
            }
        } else {
            showControllerNotInitializedWarning();
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Editar ainda não implementada."); // Usar getRootPane() para Toast
    }

    private void excluirClienteSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um cliente para excluir."); // Usar getRootPane() para Toast
            return;
        }

        // Obtém o ID e o nome para a mensagem de confirmação
        int clienteId = (int) tableModel.getValueAt(selectedRow, 0);
        String clienteNome = (String) tableModel.getValueAt(selectedRow, 1);
        System.out.println("Ação: Excluir Cliente ID: " + clienteId);


        // Mostra um JOptionPane de confirmação
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cliente '" + clienteNome + "' (ID: " + clienteId + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Chamar clienteController.excluirCliente(clienteId)
            // TODO: Recarregar a tabela (loadClientes())

            // Exemplo de como seria:
             /*
             if (clienteController != null) {
                try {
                    clienteController.excluirCliente(clienteId);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Cliente excluído com sucesso!"); // Usar getRootPane() para Toast
                    loadClientes(); // Recarrega a tabela
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao excluir cliente: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao excluir: " + ex.getMessage());
                    ex.printStackTrace();
                }
             } else {
                showControllerNotInitializedWarning();
             }
             */
            Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Excluir (lógica do backend) ainda não implementada."); // Usar getRootPane() para Toast

        }
    }


    // --- Métodos Auxiliares de Feedback ---

    private void showDatabaseConnectionError(String message) {
        JOptionPane.showMessageDialog(this,
                "Não foi possível conectar ao banco de dados.\n" + message +
                        "\nVerifique se o servidor MySQL está rodando e as configurações estão corretas.",
                "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        // Desabilita botões se a conexão falhar
        cmdAdicionar.setEnabled(false);
        cmdEditar.setEnabled(false);
        cmdExcluir.setEnabled(false);
        //databaseConnectionFailed = true; // Marca que a conexão falhou
    }

    private void showDatabaseOperationError(String message) {
        Toast.show(this.getRootPane(), Toast.Type.ERROR, message); // Usar getRootPane() para Toast
        // Ou usar JOptionPane se preferir um diálogo bloqueante:
        // JOptionPane.showMessageDialog(this, message, "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
    }

    private void showUnexpectedError(String message) {
        JOptionPane.showMessageDialog(this,
                "Ocorreu um erro inesperado:\n" + message,
                "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
    }

    private void showControllerNotInitializedWarning() {
        Toast.show(this.getRootPane(), Toast.Type.WARNING, "Operação não disponível (sem conexão com banco). Usando dados de exemplo."); // Usar getRootPane() para Toast
        // JOptionPane.showMessageDialog(this, "A conexão com o banco de dados não foi estabelecida.\nNão é possível realizar esta operação.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }


    // --- Método para Dados de Exemplo (Temporário) ---
    private void addSampleData() {
        // Adiciona algumas linhas de exemplo
        tableModel.addRow(new Object[]{1, "João Silva", "111.222.333-44", "(75) 98888-1111", "Rua das Flores, 123"});
        tableModel.addRow(new Object[]{2, "Maria Oliveira", "555.666.777-88", "(75) 99999-2222", "Avenida Principal, 456"});
        tableModel.addRow(new Object[]{3, "Carlos Pereira", "999.888.777-66", "(75) 97777-3333", "Travessa dos Sonhos, 789"});
    }

}