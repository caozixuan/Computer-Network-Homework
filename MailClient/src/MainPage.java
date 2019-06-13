import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainPage {
    private JFrame frame;
    private JScrollPane currentScrollPane;
    private Client c;
    private JTable table;

    public MainPage(Client c) {
        this.c = c;
        initialize();
    }

    public JFrame getFrame()
    {
        return frame;
    }
    private void initialize() {
        this.frame = new JFrame();
        this.frame.setTitle("主页面");
        this.frame.setBounds(100, 100, 630, 470);
        this.frame.setDefaultCloseOperation(3);
        this.frame.getContentPane().setLayout((LayoutManager)null);
        JButton login = new JButton("查看邮件");
        login.setFont(new Font("宋体", 0, 12));
        login.setBackground(UIManager.getColor("Button.highlight"));
        login.setBounds(110, 20, 100, 30);
        this.frame.getContentPane().add(login);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = table.getRowCount();
                Message[] messages = c.messages;
                MimeMessage m = (MimeMessage) messages[c.messages.length-index];
                new MailDetailPage(m,c);
            }
        });

        JButton refresh = new JButton("发送邮件");
        refresh.setFont(new Font("宋体", 0, 12));
        refresh.setBackground(UIManager.getColor("Button.highlight"));
        refresh.setBounds(210, 20, 100, 30);
        this.frame.getContentPane().add(refresh);
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EmailSendPage().setVisible(true);
            }
        });


        JButton download = new JButton("删除邮件");
        download.setFont(new Font("宋体", 0, 12));
        download.setBackground(UIManager.getColor("Button.highlight"));
        download.setBounds(310, 20, 100, 30);
        this.frame.getContentPane().add(download);
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = c.messages.length-table.getRowCount();
                c.delete(index);
            }
        });



        JButton pause = new JButton("刷新信息");
        pause.setFont(new Font("宋体", 0, 12));
        pause.setBackground(UIManager.getColor("Button.highlight"));
        pause.setBounds(410, 20, 100, 30);
        this.frame.getContentPane().add(pause);
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTableInfor();
            }
        });


        this.frame.setVisible(true);

        setTableInfor();

    }


    public void setTableInfor(){
        ArrayList<MailInformation> infors =  c.updateAllInformation();
        Message[] ms = c.messages;
        String[][] data = new String[infors.size()][2];
        for(int i=0;i<infors.size();i++)
        {
            try{
                MimeMessage msg = (MimeMessage) ms[i];
                data[i][0] = c.getSubject(msg);
                data[i][1] = c.getSentDate(msg,null);
                //data[i][0] = String.valueOf(infors.get(i).id);
                //data[i][1] = String.valueOf(infors.get(i).size);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        String[] columnNames = new String[]{"主题", "日期"};

        DefaultTableModel model = new DefaultTableModel();
        model.setDataVector(data, columnNames);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(32, 80, 570, 300);
        // this.frame.setContentPane(scrollPane);
        this.frame.getContentPane().invalidate();
        if(this.currentScrollPane != null) this.frame.getContentPane().remove(currentScrollPane);
        this.currentScrollPane = scrollPane;
        this.frame.getContentPane().add(scrollPane);
        this.frame.getContentPane().validate();

//        this.frame.getContentPane().removeAll();
//        this.frame.getContentPane().add(scrollPane);

        // this.table.removeAll();
        this.table = new JTable(model);
        scrollPane.setViewportView(this.table);
        this.table.setColumnSelectionAllowed(true);
        this.table.setCellSelectionEnabled(true);
        this.table.setFont(new Font("微软雅黑", 0, 12));
        this.table.setBorder(new LineBorder(new Color(0, 0, 0)));
    }
}
