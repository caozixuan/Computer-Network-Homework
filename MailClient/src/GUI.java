import com.sun.org.apache.bcel.internal.generic.InstructionConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private JFrame frame = new JFrame("登录");
    private Container c = frame.getContentPane();
    private JTextField username = new JTextField();
    private JPasswordField password = new JPasswordField();
    private JButton ok = new JButton("确定");
    private JButton cancel = new JButton("取消");
    public GUI(){
        frame.setSize(300,200);
        c.setLayout(new BorderLayout());
        initFrame();
        frame.setVisible(true);
    }
    private void initFrame(){
//顶部
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(new JLabel("163邮件系统"));
        c.add(titlePanel,"North");

//中部表单
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(null);
        JLabel a1 = new JLabel("用户名:");
        a1.setBounds(50,20,50,20);
        JLabel a2 = new JLabel("密  码:");
        a2.setBounds(50,60,50,20);
        fieldPanel.add(a1);
        fieldPanel.add(a2);
        username.setBounds(110,20,120,20);
        password.setBounds(110,60,120,20);
        fieldPanel.add(username);
        fieldPanel.add(password);
        c.add(fieldPanel,"Center");

//底部按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        c.add(buttonPanel,"South");
        ok.addActionListener(new LoginListerner());
        username.setText("13013550115");
        password.setText("Caozixuan98724");

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 消息对话框无返回, 仅做通知作用
                JOptionPane.showMessageDialog(
                        frame,
                        "用户名或密码错误",
                        "登陆失败",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    private class LoginListerner implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String name = username.getText();
            String pass = new String(password.getPassword());
            Client c = null;
            try{
                c = new Client("pop3.163.com",110);
                c.login(name,pass);
            }catch (Exception ee)
            {

            }

            MainPage main =  new MainPage(c);

        }
    }


    public static void main(String[] args){
        new GUI();
    }

}
