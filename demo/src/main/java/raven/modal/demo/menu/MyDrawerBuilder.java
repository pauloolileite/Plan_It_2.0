package raven.modal.demo.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.extras.AvatarIcon;
import raven.modal.demo.Demo;
import raven.modal.demo.forms.*;
import raven.modal.demo.model.ModelUser;
import raven.modal.demo.system.AllForms;
import raven.modal.demo.system.Form;
import raven.modal.demo.system.FormManager;
import raven.modal.drawer.DrawerPanel;
import raven.modal.drawer.item.Item; // Import correto
import raven.modal.drawer.item.MenuItem; // Import correto
import raven.modal.drawer.menu.MenuAction;
import raven.modal.drawer.menu.MenuEvent;
import raven.modal.drawer.menu.MenuOption;
import raven.modal.drawer.menu.MenuStyle;
import raven.modal.drawer.renderer.DrawerStraightDotLineStyle;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.footer.LightDarkButtonFooter;
import raven.modal.drawer.simple.footer.SimpleFooterData;
import raven.modal.drawer.simple.header.SimpleHeader;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import raven.modal.option.Option;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

// A classe permanece exatamente como a sua, apenas o createSimpleMenuOption será modificado
public class MyDrawerBuilder extends SimpleDrawerBuilder {

    private static MyDrawerBuilder instance;
    private ModelUser user;

    public static MyDrawerBuilder getInstance() {
        if (instance == null) {
            instance = new MyDrawerBuilder();
        }
        return instance;
    }

    public ModelUser getUser() {
        return user;
    }

    // Este método setUser está correto e já lida com a atualização do menu
    public void setUser(ModelUser user) {
        boolean updateMenuItem = this.user == null || this.user.getRole() != user.getRole();

        this.user = user;

        // set user to menu validation
        MyMenuValidation.setUser(user);

        // setup drawer header
        SimpleHeader header = (SimpleHeader) getHeader();
        SimpleHeaderData data = header.getSimpleHeaderData();
        AvatarIcon icon = (AvatarIcon) data.getIcon();
        // Lógica de ícone baseada no Role (conforme seu original)
        String iconName = user.getRole() == ModelUser.Role.ADMIN ? "avatar_male.svg" : "avatar_female.svg";

        icon.setIcon(new FlatSVGIcon("raven/modal/demo/drawer/image/" + iconName, 100, 100));
        data.setTitle(user.getUserName());
        data.setDescription(user.getMail()); // Corrigido para getMail() do seu ModelUser
        header.setSimpleHeaderData(data);

        if (updateMenuItem) {
            rebuildMenu();
        }
    }

    private final int SHADOW_SIZE = 12;

    private MyDrawerBuilder() {
        super(createSimpleMenuOption());
        LightDarkButtonFooter lightDarkButtonFooter = (LightDarkButtonFooter) getFooter();
        lightDarkButtonFooter.addModeChangeListener(isDarkMode -> {
            // event for light dark mode changed
        });
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        AvatarIcon icon = new AvatarIcon(new FlatSVGIcon("raven/modal/demo/drawer/image/avatar_male.svg", 100, 100), 50, 50, 3.5f);
        icon.setType(AvatarIcon.Type.MASK_SQUIRCLE);
        icon.setBorder(2, 2);

        changeAvatarIconBorderColor(icon);

        UIManager.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("lookAndFeel")) {
                changeAvatarIconBorderColor(icon);
            }
        });

        return new SimpleHeaderData()
                .setIcon(icon)
                .setTitle("Ra Ven") // Título padrão antes do login
                .setDescription("raven@gmail.com"); // Descrição padrão
    }

    private void changeAvatarIconBorderColor(AvatarIcon icon) {
        icon.setBorderColor(new AvatarIcon.BorderColor(UIManager.getColor("Component.accentColor"), 0.7f));
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        // Mantido o rodapé original do seu arquivo
        return new SimpleFooterData()
                .setTitle("Swing Modal Dialog")
                .setDescription("Version " + Demo.DEMO_VERSION);
    }

    @Override
    public Option createOption() {
        Option option = super.createOption();
        option.setOpacity(0.3f);
        option.getBorderOption()
                .setShadowSize(new Insets(0, 0, 0, SHADOW_SIZE));
        return option;
    }

    // --- ESTE É O MÉTODO MODIFICADO ---
    public static MenuOption createSimpleMenuOption() {

        // create simple menu option
        MenuOption simpleMenuOption = new MenuOption();

        // --- ARRAY DE ITENS SUBSTITUÍDO PELOS ITENS DO PLAN IT ---
        // Ícones usados são os que existem no pacote .../drawer/icon/
        MenuItem items[] = new MenuItem[]{
                new Item.Label("PRINCIPAL"), // Label (índice 0)
                new Item("Dashboard", "dashboard.svg", FormDashboard.class), // (índice 1)
                new Item("Agendamentos", "calendar.svg", FormAgendamentos.class), // (índice 2)
                new Item.Label("CADASTROS"), // Label (índice 3)
                new Item("Clientes", "components.svg", FormClientes.class), // (índice 4)
                new Item("Funcionários", "forms.svg", FormFuncionarios.class), // (índice 5)
                new Item("Serviços", "page.svg", FormServicos.class), // (índice 6)
                new Item.Label("ADMINISTRAÇÃO"), // Label (índice 7) - MyMenuValidation deve esconder isso
                new Item("Sistema", "setting.svg") // (índice 8) - MyMenuValidation deve esconder isso
                        .subMenu("Gerenciar Usuários", FormUsuarios.class) // Subitem
                        .subMenu("Relatórios", FormRelatorios.class), // Subitem
                new Item.Label("OUTROS"), // Label (índice 9)
                new Item("Configurações", "setting.svg", FormSetting.class), // (índice 10)
                new Item("Logout", "logout.svg") // (índice 11)
        };
        // --- FIM DA MODIFICAÇÃO DO ARRAY ---

        simpleMenuOption.setMenuStyle(new MenuStyle() {

            @Override
            public void styleMenuItem(JButton menu, int[] index, boolean isMainItem) {
                boolean isTopLevel = index.length == 1;
                if (isTopLevel) {
                    // adjust item menu at the top level because it's contain icon
                    menu.putClientProperty(FlatClientProperties.STYLE, "" +
                            "margin:-1,0,-1,0;");
                }
            }

            @Override
            public void styleMenu(JComponent component) {
                component.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
            }
        });

        simpleMenuOption.getMenuStyle().setDrawerLineStyleRenderer(new DrawerStraightDotLineStyle());
        simpleMenuOption.setMenuValidation(new MyMenuValidation()); // Usa a validação existente

        simpleMenuOption.addMenuEvent(new MenuEvent() {
            @Override
            public void selected(MenuAction action, int[] index) {
                System.out.println("Drawer menu selected " + Arrays.toString(index));
                Class<?> itemClass = action.getItem().getItemClass();
                int i = index[0]; // Pega o índice do item principal

                // --- LÓGICA DE CLIQUE ATUALIZADA ---
                // O item "Logout" agora está no índice 11
                if (i == 11) { // Índice do Logout
                    action.consume(); // Impede o processamento padrão
                    FormManager.logout(); // Chama o logout
                    return;
                }
                // Remover o 'About' (índice 8) que não existe mais no menu principal
                // --- FIM DA ATUALIZAÇÃO ---

                if (itemClass == null || !Form.class.isAssignableFrom(itemClass)) {
                    action.consume();
                    return;
                }
                Class<? extends Form> formClass = (Class<? extends Form>) itemClass;
                FormManager.showForm(AllForms.getForm(formClass));
            }
        });

        simpleMenuOption.setMenus(items)
                .setBaseIconPath("raven/modal/demo/drawer/icon") // Caminho base dos ícones
                .setIconScale(0.45f);

        return simpleMenuOption;
    }
    // --- FIM DO MÉTODO MODIFICADO ---


    @Override
    public int getDrawerWidth() {
        return 270 + SHADOW_SIZE;
    }

    @Override
    public int getDrawerCompactWidth() {
        return 80 + SHADOW_SIZE;
    }

    @Override
    public int getOpenDrawerAt() {
        return 1000;
    }

    @Override
    public boolean openDrawerAtScale() {
        return false;
    }

    @Override
    public void build(DrawerPanel drawerPanel) {
        drawerPanel.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
    }

    private static String getDrawerBackgroundStyle() {
        return "" +
                "[light]background:tint($Panel.background,20%);" +
                "[dark]background:tint($Panel.background,5%;";
    }
}