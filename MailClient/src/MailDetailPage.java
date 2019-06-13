import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;

public class MailDetailPage {
    private JFrame frame;

    private String subject;
    private String sender;
    private String date;
    private String content;


    MailDetailPage(MimeMessage msg, Client c)
    {
        try{
            StringBuffer con = new StringBuffer(30);
            subject = "标题:"+c.getSubject(msg);
            sender = "发件人："+c.getFrom(msg);
            date = "日期：" + c.getSentDate(msg,null);
            c.getMailTextContent(msg,con);
            content = con.toString();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        init();
    }

    public void init()
    {
        this.frame = new JFrame();
        this.frame.setTitle("邮件详情");
        this.frame.setBounds(100, 100, 630, 470);
        this.frame.setDefaultCloseOperation(3);
        this.frame.getContentPane().setLayout((LayoutManager)null);
        JLabel lblNewLabel = new JLabel(subject);
        lblNewLabel.setBounds(32, 15, 600, 30);
        this.frame.getContentPane().add(lblNewLabel);
        JLabel lblNewLabel_1 = new JLabel(sender);
        lblNewLabel_1.setBounds(32, 45, 600, 30);
        this.frame.getContentPane().add(lblNewLabel_1);
        JLabel lblNewLabel_2 = new JLabel(date);
        lblNewLabel_2.setBounds(32, 75, 600, 30);
        this.frame.getContentPane().add(lblNewLabel_2);
        JPanel panel = new JPanel();

        // 创建一个 5 行 10 列的文本区域
        final JTextArea textArea = new JTextArea(5, 10);
        textArea.setBounds(32,120,600,100);
        // 设置自动换行
        textArea.setLineWrap(true);
        // 添加到内容面板
        panel.add(textArea);
        this.frame.getContentPane().add(textArea);
        textArea.setText(content);

        frame.setVisible(true);
    }
}
