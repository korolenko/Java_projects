import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MyFrame extends JFrame implements ActionListener {
    //размеры окна приложения
    private final short FrameWidth = 1000;
    private final short FrameHeight = 650;
    //размеры модального окна
    private final short dialogFrameWidth = 200;
    private final short dialogFrameHeight = 150;

    //текстовые обозначения
    private final String btnText = "Посмотреть";
    private final String lblText = "Статус работы тасков фреймворка";
    private final String settingsText = "Настройки";
    private final String item1Text = "О программе";
    private final String item2Text = "Настройки подключения";
    private final String versionText = "Текущая версия - 0.6";
    private final String refreshActionText = "Обновить";

    //инициализация используемых графических объектов
    //основное окно
    private JLabel labelTop = new JLabel(lblText);
    private JTextArea logTextArea = new JTextArea(4,1);
    private JButton btn = new JButton(btnText);
    private MenuBar menuBar = new MenuBar();
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneLog;
    private JTable JDatatable;
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem refreshAction = new JMenuItem(refreshActionText);
    private Menu settings = new Menu(settingsText);
    private MenuItem item1 = new MenuItem(item1Text);
    private MenuItem item2 = new MenuItem(item2Text);
    private JPanel SouthJPannel;
    // модальное окно
    private JTextArea DialogLogin = new JTextArea(1,10);
    private JPasswordField  DialogPassword = new JPasswordField (20);
    private JLabel labelDialogLogin = new JLabel("Логин:");
    private JLabel labelDialogPassword = new JLabel("Пароль:");
    private JButton jDialogButton = new JButton("Применить");
    private JPanel dialogpanel = new JPanel();

    private Dimension dim;
    private Table dataTable = new Table();
    private PostgreConnector postgreConnector = new PostgreConnector();
    private DefaultTableModel model = new DefaultTableModel();

    private void setFrameBoundsByCenter(JFrame jFrame){
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setBounds(dim.width / 2 - FrameWidth/ 2, dim.height / 2 - FrameHeight / 2, FrameWidth, FrameHeight);
    }

    MyFrame(String title) {

        super(title);
        this.getContentPane().setLayout(new BorderLayout());
        setFrameBoundsByCenter(this);
        this.setSize(FrameWidth, FrameHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //строки меню
        this.setMenuBar(menuBar);
        settings.add(item2);
        settings.add(item1);
        menuBar.add(settings);

        //обработчик меню
        item1.addActionListener(this);
        item2.addActionListener(this);

        //текстовое поле заголовок
        labelTop.setHorizontalAlignment(JLabel.CENTER);
        labelTop.setBounds(1, 1, FrameWidth, 100);
        this.add(labelTop, BorderLayout.NORTH);

        //таблица
        //JTable с запретом редактирования ячеек
        JDatatable = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JDatatable.setModel(model);
        scrollPane = new JScrollPane(JDatatable);
        scrollPane.setPreferredSize(new Dimension(FrameWidth, FrameHeight));
        this.add(scrollPane, BorderLayout.CENTER);
        JDatatable.setPreferredScrollableViewportSize(JDatatable.getPreferredSize());

        //нажатие на таблицу на лямбде
        refreshAction.addActionListener(e -> {

            NewTheread newTheread = new NewTheread("Поток чтения данных из базы");
            newTheread.run(dataTable,JDatatable,model,logTextArea,postgreConnector);
            //postgreConnector.getDataFromDB(dataTable,JDatatable,model,logTextArea);
            FrameLog.setLog(logTextArea, "Data have been updated");
        });

        popupMenu.add(refreshAction);
        JDatatable.setComponentPopupMenu(popupMenu);

        //форматирование таблицы (цвет строк)
        /*JDatatable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                LocalDate lastDate = (LocalDate) JDatatable.getModel().getValueAt(row, 10);
                LocalDate nowDate = LocalDateTime.now().toLocalDate();
                Period period = Period.between(lastDate,nowDate);
                int yearValue = (int) JDatatable.getModel().getValueAt(row, 10);
                c.setBackground(yearValue <= 3 ? Color.RED : Color.ORANGE);
                return c;
            }
        });*/

        //для добавления в South часть BorderLayout`а больше одного объекта
        SouthJPannel = new JPanel();
        SouthJPannel.setLayout(new BorderLayout());

        //кнопка
        btn.setVerticalAlignment(JLabel.BOTTOM);
        btn.setHorizontalAlignment(JLabel.CENTER);
        btn.addActionListener(this);

        //текстовое поле логи
        logTextArea.setEditable(false);
        scrollPaneLog = new JScrollPane(logTextArea);

        SouthJPannel.add(scrollPaneLog,BorderLayout.CENTER);
        SouthJPannel.add(btn,BorderLayout.SOUTH);
        this.add(SouthJPannel,BorderLayout.SOUTH);

        //компоновка объектов на экране
        //MyFrame.pack();
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String arg = actionEvent.getActionCommand();

        //нажатие кнопки
        if (arg.equals(btnText)) {
            NewTheread newTheread = new NewTheread("Поток чтения данных из базы");
            newTheread.run(dataTable,JDatatable,model,logTextArea,postgreConnector);
            //postgreConnector.getDataFromDB(dataTable,JDatatable,model,logTextArea);
        }
        //о программе
        else if(arg.equals(item1Text)){
            JOptionPane.showMessageDialog(this, versionText,item1Text,JOptionPane.INFORMATION_MESSAGE);
        }
        //настройки подключения
        else if(arg.equals(item2Text)){
            createModalFrame();
        }
        // диалоговое окно
        else if(arg.equals("Применить")){
            postgreConnector.setPASS(DialogPassword);
            postgreConnector.setUSER(DialogLogin);
            JOptionPane.showMessageDialog(this, "Данные сохранены!","Успех",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createModalFrame(){
        final JDialog frame = new JDialog(this, "Настройки", true);
        frame.setBounds(dim.width / 2 - dialogFrameWidth/ 2, dim.height / 2 - dialogFrameHeight / 2, dialogFrameWidth, dialogFrameHeight);
        frame.setContentPane(getJContentPane(null,dialogpanel));

        //setBoundsByCenter(frame);
        dialogpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.setVisible(true);
    }

    // настройка компоновки объектов модального окна
    JPanel getJContentPane(JPanel jContentPane, JPanel panel){
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);

            panel.setBounds(1, 1, dialogFrameWidth-20, dialogFrameHeight-43);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            jContentPane.add(panel);

            panel.add(labelDialogLogin);
            labelDialogLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(DialogLogin);
            DialogLogin.setText(postgreConnector.getUSER());
            DialogLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(labelDialogPassword);

            panel.add(DialogPassword);
            DialogPassword.setText(postgreConnector.getPASS());

            panel.add(jDialogButton);
            jDialogButton.addActionListener(this);
        }
        return jContentPane;
    }
}
