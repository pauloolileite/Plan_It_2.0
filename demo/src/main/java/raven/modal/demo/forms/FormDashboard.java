package raven.modal.demo.forms;

// --- Imports (Mantidos e Adicionados) ---
import Controller.AgendamentoController;
import Controller.ClienteController;
// Adicionar imports para buscar nomes relacionados quando conectar ao banco
import Controller.FuncionarioController;
import Controller.ServicoController;
import Model.Agendamento; // Para popular a tabela
import Utils.DatabaseConnection;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
// Remover imports de gráficos não usados (Candlestick, Bar, Spider, Pie)
// import org.jfree.chart.renderer.xy.CandlestickRenderer;
import raven.modal.demo.component.ToolBarSelection;
import raven.modal.demo.component.chart.*; // Manter por enquanto, TimeSeries pode ser usado depois
// import raven.modal.demo.component.chart.renderer.other.ChartCandlestickRenderer; // Remover
import raven.modal.demo.component.chart.themes.ColorThemes;
import raven.modal.demo.component.chart.themes.DefaultChartTheme;
// import raven.modal.demo.component.chart.utils.ToolBarCategoryOrientation; // Remover
import raven.modal.demo.component.chart.utils.ToolBarTimeSeriesChartRenderer; // Manter por enquanto
import raven.modal.demo.component.dashboard.CardBox;
import raven.modal.demo.sample.SampleData; // Usado para TimeSeries (por enquanto)
import raven.modal.demo.system.Form;
import raven.modal.demo.utils.SystemForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Para a nova tabela
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime; // Para formatar hora
import java.time.YearMonth;
import java.time.format.DateTimeFormatter; // Para formatar hora
import java.util.List; // Para agendamentos
import java.util.Locale;

@SystemForm(name = "Dashboard", description = "dashboard form display some details")
public class FormDashboard extends Form {

    // --- Variáveis ---
    private JPanel panelLayout;
    private CardBox cardBox;
    private TimeSeriesChart timeSeriesChart; // Mantido para Gráfico Recente (futuro)
    // Gráficos removidos:
    // private CandlestickChart candlestickChart;
    // private BarChart barChart;
    // private SpiderChart spiderChart;
    // private PieChart pieChart;

    // Tabela da Agenda
    private JTable tableAgendaDia;
    private DefaultTableModel tableModelAgendaDia;

    // --- Controllers ---
    private ClienteController clienteController;
    private AgendamentoController agendamentoController;
    // Adicionar FuncionarioController e ServicoController quando for buscar nomes
    private FuncionarioController funcionarioController;
    private ServicoController servicoController;


    // --- Formatadores ---
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // Formato de hora

    public FormDashboard() {
        init();
    }

    private void init() {
        // --- Inicialização dos Controllers (COMENTADO) ---
        /*
        try {
            Connection conexao = DatabaseConnection.getConnection();
            if (conexao == null) { throw new SQLException("Falha ao obter conexão."); }
            this.clienteController = new ClienteController(conexao);
            this.agendamentoController = new AgendamentoController(conexao);
            this.funcionarioController = new FuncionarioController(conexao); // Inicializar
            this.servicoController = new ServicoController(conexao);       // Inicializar
        } catch (SQLException e) { showDatabaseConnectionError(e.getMessage());
        } catch (Exception e) { showUnexpectedError(e.getMessage()); e.printStackTrace(); }
        */

        setLayout(new MigLayout("wrap,fill", "[fill]", "[grow 0][fill]"));
        createTitle();
        createPanelLayout(); // Cria panelLayout com DashboardLayout
        createCard();        // Adiciona o CardBox ao panelLayout

        // --- ADAPTADO: Chama o método renomeado ---
        createAgendaDoDiaPanel(); // Adiciona o painel da Agenda ao panelLayout

        // --- REMOVIDO: Não chama mais createOtherChart() ---
        // createOtherChart();

        // TODO: Adicionar painel "Próximos Agendamentos" e "Gráfico Recente" depois, ajustando o layout principal
    }

    @Override
    public void formInit() {
        loadData();
    }

    @Override
    public void formRefresh() {
        loadData();
    }

    private void loadData() {
        // --- 1. Atualiza os Cards (Lógica da resposta anterior mantida) ---
        System.out.println("Atualizando cards do dashboard com dados de exemplo.");
        int totalClientes = 42;
        int agendamentosHoje = 5;
        double faturamentoMes = 1234.50;
        String descClientes = "+10 na semana";
        String descAgendamentos = "";
        String descFaturamento = "+R$ 200 vs Mês Ant.";
        boolean clientesUp = true;
        boolean agendamentosUp = true;
        boolean faturamentoUp = true;

        if ((clienteController == null || agendamentoController == null) && isVisible()) {
            showControllerNotInitializedWarning();
        }
        // Lógica real comentada (igual anterior) ...

        cardBox.setValueAt(0, String.valueOf(totalClientes), descClientes, null, clientesUp);
        cardBox.setValueAt(1, String.valueOf(agendamentosHoje), descAgendamentos, null, agendamentosUp);
        cardBox.setValueAt(2, currencyFormatter.format(faturamentoMes), descFaturamento, null, faturamentoUp);


        // --- 2. Atualiza a Tabela da Agenda do Dia ---
        if (tableModelAgendaDia != null) {
            tableModelAgendaDia.setRowCount(0); // Limpa a tabela

            // --- LÓGICA TEMPORÁRIA COM DADOS DE EXEMPLO ---
            System.out.println("Populando Agenda do Dia com dados de exemplo.");
            tableModelAgendaDia.addRow(new Object[]{"09:00", "Maria Oliveira", "Corte Simples", "Ana Souza", "Agendado"});
            tableModelAgendaDia.addRow(new Object[]{"10:30", "João Silva", "Impressão Colorida", "Bruno Costa", "Agendado"});
            tableModelAgendaDia.addRow(new Object[]{"14:00", "Carlos Pereira", "Encadernação", "Carla Dias", "Agendado"});
            // --- FIM LÓGICA TEMPORÁRIA ---


            // --- LÓGICA REAL (COMENTADA) ---
            /*
            if (agendamentoController != null && clienteController != null && funcionarioController != null && servicoController != null) {
                try {
                    List<Agendamento> agendamentos = agendamentoController.listarAgendamentosDeHoje(); // Precisa criar (ORDER BY data_hora)
                    if (agendamentos != null) {
                        for (Agendamento ag : agendamentos) {
                            String nomeCliente = buscarNomeCliente(ag.getClienteId()); // Usar método auxiliar
                            String nomeFunc = buscarNomeFuncionario(ag.getFuncionarioId()); // Usar método auxiliar
                            String nomeServ = buscarNomeServico(ag.getServicoId()); // Usar método auxiliar
                            String status = "Agendado"; // TODO: Adicionar status ao Model/DAO
                            tableModelAgendaDia.addRow(new Object[]{
                                    ag.getDataHora().format(timeFormatter), // Apenas Hora:Minuto
                                    nomeCliente,
                                    nomeServ,
                                    nomeFunc,
                                    status
                            });
                        }
                    }
                } catch (SQLException e) {
                    showDatabaseOperationError("Erro ao carregar agenda do dia: " + e.getMessage()); // Precisa criar
                }
            }
            */
            // --- FIM LÓGICA REAL (COMENTADA) ---
        }


        // --- 3. Atualiza Gráficos (REMOVIDO ou COMENTADO) ---
        // timeSeriesChart.setDataset(SampleData.getTimeSeriesDataset()); // Manter se for usar para faturamento
        // candlestickChart.setDataset(SampleData.getOhlcDataset()); // REMOVER
        // barChart.setDataset(SampleData.getCategoryDataset()); // REMOVER
        // spiderChart.setDataset(SampleData.getCategoryDataset()); // REMOVER
        // pieChart.setDataset(SampleData.getPieDataset()); // REMOVER
    }

    // --- createTitle() ADAPTADO ---
    private void createTitle() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[]push[][]"));
        JLabel title = new JLabel("Dashboard Plan It");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +3");

        ToolBarSelection<ColorThemes> toolBarSelection = new ToolBarSelection<>(ColorThemes.values(), colorThemes -> {
            if (DefaultChartTheme.setChartColors(colorThemes)) {
                // Aplica tema apenas aos gráficos que sobraram (nenhum por enquanto, exceto TimeSeries se mantido)
                if(timeSeriesChart != null) DefaultChartTheme.applyTheme(timeSeriesChart.getFreeChart());
                // Remover aplicação aos gráficos removidos
                // DefaultChartTheme.applyTheme(candlestickChart.getFreeChart());
                // DefaultChartTheme.applyTheme(barChart.getFreeChart());
                // DefaultChartTheme.applyTheme(pieChart.getFreeChart());
                // DefaultChartTheme.applyTheme(spiderChart.getFreeChart());

                // Atualiza cores dos 3 cards
                cardBox.setCardIconColor(0, DefaultChartTheme.getColor(0));
                cardBox.setCardIconColor(1, DefaultChartTheme.getColor(1));
                cardBox.setCardIconColor(2, DefaultChartTheme.getColor(2));
            }
        });
        panel.add(title);
        panel.add(toolBarSelection);
        add(panel, "wrap"); // Adiciona ao layout principal e quebra linha
    }

    // --- createPanelLayout() Original Mantido ---
    private void createPanelLayout() {
        panelLayout = new JPanel(new DashboardLayout());
        JScrollPane scrollPane = new JScrollPane(panelLayout);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "width:5;trackArc:$ScrollBar.thumbArc;trackInsets:0,0,0,0;thumbInsets:0,0,0,0;");
        add(scrollPane, "grow"); // Adiciona ao layout principal, ocupando o espaço restante
    }

    // --- createCard() Original Mantido (adaptado na resposta anterior) ---
    private void createCard() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[fill]"));
        cardBox = new CardBox();
        cardBox.addCardItem(createIcon("raven/modal/demo/icons/dashboard/customer.svg", DefaultChartTheme.getColor(0)), "Total de Clientes");
        cardBox.addCardItem(createIcon("raven/modal/demo/drawer/icon/calendar.svg", DefaultChartTheme.getColor(1)), "Agendamentos Hoje");
        cardBox.addCardItem(createIcon("raven/modal/demo/icons/dashboard/income.svg", DefaultChartTheme.getColor(2)), "Faturamento Mês");
        panel.add(cardBox);
        panelLayout.add(panel); // Adiciona ao panelLayout (com DashboardLayout)
    }


    // --- MÉTODO createChart() RENOMEADO e ADAPTADO para Agenda do Dia ---
    private void createAgendaDoDiaPanel() {
        JPanel panel = new JPanel(new MigLayout("fill", "[fill]", "[grow 0][fill, grow]")); // Layout para Título + Tabela
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:10"); // Borda arredondada

        // Título da Seção
        JLabel lbTitulo = new JLabel("Agenda do Dia");
        lbTitulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
        panel.add(lbTitulo, "wrap, gapy 5 10"); // Adiciona título com espaço abaixo

        // Cria a Tabela da Agenda
        tableModelAgendaDia = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Horário", "Cliente", "Serviço", "Funcionário", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAgendaDia = new JTable(tableModelAgendaDia);
        // Configurações básicas da tabela (opcional)
        tableAgendaDia.setRowHeight(30);
        tableAgendaDia.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height 30");
        // Ajustar largura das colunas se necessário (ex: tableAgendaDia.getColumnModel().getColumn(0).setPreferredWidth(60); )

        JScrollPane scrollPane = new JScrollPane(tableAgendaDia);
        // Estilo da barra de rolagem (opcional, similar ao scroll principal)
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "width:5;trackArc:$ScrollBar.thumbArc;");
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "height:5;trackArc:$ScrollBar.thumbArc;");

        panel.add(scrollPane, "grow"); // Adiciona a tabela com scroll, fazendo-a crescer

        panelLayout.add(panel); // Adiciona este painel da agenda ao panelLayout principal
    }

    // --- MÉTODO createOtherChart() REMOVIDO ---
    /*
    private void createOtherChart() { ... }
    */

    // --- createIcon() Original Mantido ---
    private Icon createIcon(String path, Color color) {
        return new FlatSVGIcon(path, 0.4f).setColorFilter(new FlatSVGIcon.ColorFilter(color1 -> color));
    }

    // --- Métodos Auxiliares para buscar nomes (usados na lógica real comentada) ---
    private String buscarNomeCliente(int id) {
        // Lógica real comentada
        /*
        if (clienteController != null) {
            try {
                Model.Cliente c = clienteController.buscarClientePorId(id);
                return (c != null) ? c.getNome() : "ID: " + id;
            } catch (SQLException e) { System.err.println("Erro buscando cliente "+id+": "+e.getMessage()); }
        } */
        return "Cliente " + id; // Placeholder
    }
    private String buscarNomeFuncionario(int id) {
        // Lógica real comentada
        /*
        if (funcionarioController != null) {
             try {
                Model.Funcionario f = funcionarioController.buscarFuncionarioPorId(id); // Precisa criar
                return (f != null) ? f.getNome() : "ID: " + id;
            } catch (SQLException e) { System.err.println("Erro buscando func "+id+": "+e.getMessage()); }
        }*/
        return "Func. " + id; // Placeholder
    }
    private String buscarNomeServico(int id) {
        // Lógica real comentada
         /*
         if (servicoController != null) {
            try {
                Model.Servico s = servicoController.buscarServicoPorId(id); // Precisa criar
                return (s != null) ? s.getNome() : "ID: " + id;
            } catch (SQLException e) { System.err.println("Erro buscando serv "+id+": "+e.getMessage()); }
         }*/
        return "Serviço " + id; // Placeholder
    }


    // --- DashboardLayout Original Mantido ---
    private class DashboardLayout implements LayoutManager {
        // ... (código do layout inalterado) ...
        private int gap = UIScale.scale(14);

        @Override
        public void addLayoutComponent(String name, Component comp) {}

        @Override
        public void removeLayoutComponent(Component comp) {}

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int width = parent.getWidth() > 0 ? parent.getWidth() : 1000;
                width -= (insets.left + insets.right);
                int height = insets.top + insets.bottom;
                int count = parent.getComponentCount();
                int panelWidth = width;

                for (int i = 0; i < count; i++) {
                    Component com = parent.getComponent(i);
                    Dimension size = com.getPreferredSize();
                    com.setSize(panelWidth, size.height);
                    size = com.getPreferredSize();
                    height += size.height;
                }
                if (count > 0) {
                    height += (count - 1) * gap;
                }
                return new Dimension(width + insets.left + insets.right, height);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(UIScale.scale(10), UIScale.scale(10));
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);

                int count = parent.getComponentCount();
                for (int i = 0; i < count; i++) {
                    Component com = parent.getComponent(i);
                    Dimension size = com.getPreferredSize();
                    com.setSize(width, size.height);
                    size = com.getPreferredSize();
                    int componentHeight = size.height;

                    com.setBounds(x, y, width, componentHeight);
                    y += componentHeight + gap;
                }
            }
        }
    }

    // --- Métodos de Feedback (Adicionados) ---
    private void showDatabaseConnectionError(String message) {
        JOptionPane.showMessageDialog(this, "Não foi possível conectar ao banco de dados.\n" + message + "\nVerifique as configurações e o servidor.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
    }
    private void showDatabaseOperationError(String message) { // Adicionado
        if (getRootPane() != null) SwingUtilities.invokeLater(() -> raven.modal.Toast.show(this.getRootPane(), raven.modal.Toast.Type.ERROR, message));
        else System.err.println(message);
    }
    private void showUnexpectedError(String message) {
        JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado:\n" + message, "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
    }
    private void showControllerNotInitializedWarning() {
        if (getRootPane() != null) {
            SwingUtilities.invokeLater(() ->
                    raven.modal.Toast.show(this.getRootPane(), raven.modal.Toast.Type.WARNING, "Dashboard usando dados de exemplo (sem conexão com banco).")
            );
        } else {
            System.out.println("Dashboard usando dados de exemplo (sem conexão com banco).");
        }
    }
}