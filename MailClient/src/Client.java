import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

class MailInformation {
    int id;
    int size;
    String content = "";

    public MailInformation(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public MailInformation(int id, int size, String content) {
        this.id = id;
        this.size = size;
        this.content = content;
    }
}

public class Client {
    private Socket socket;
    private Scanner socketReader;
    private PrintWriter socketWriter;
    public Message[] messages;

    private int unread = 0;
    private int total = 0;
    public Client(String server, int port) throws Exception {
        try {
            socket = new Socket(server, port);
            socketReader = new Scanner(socket.getInputStream());
            socketWriter = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            throw new IOException("连接服务器出现错误");
        }
    }

    private void sendCommand(String command) {
        socketWriter.println(command);
    }

    private String getResponse() {
        String response = socketReader.nextLine();
        return response;
    }

    public boolean isValid() {
        return getResponse().startsWith("+OK");
    }

    public boolean login(String username, String password) {
        String duankou = "110";  // 端口号
        String servicePath = "pop3.163.com";   // 服务器地址


        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "pop3");       // 使用pop3协议
        props.setProperty("mail.pop3.port", duankou);           // 端口
        props.setProperty("mail.pop3.host", servicePath);       // pop3服务器

        // 创建Session实例对象
       try{
           Session session = Session.getInstance(props);
           Store store = session.getStore("pop3");
           store.connect("13013550115@163.com", "Caozixuan98724"); //163邮箱程序登录属于第三方登录所以这里的密码是163给的授权密码而并非普通的登录密码
           // 获得收件箱
           Folder folder = store.getFolder("INBOX");
           /* Folder.READ_ONLY：只读权限
            * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
            */
           folder.open(Folder.READ_WRITE); //打开收件箱

           // 由于POP3协议无法获知邮件的状态,所以getUnreadMessageCount得到的是收件箱的邮件总数
           System.out.println("未读邮件数: " + folder.getUnreadMessageCount());
           unread = folder.getUnreadMessageCount();
           // 获得收件箱中的邮件总数
           System.out.println("邮件总数: " + folder.getMessageCount());
           total = folder.getMessageCount();
           // 得到收件箱中的所有邮件,并解析
           messages = folder.getMessages();
       }catch (Exception e)
       {

       }





        sendCommand("USER " + username);
        if (!isValid()) {
            System.out.println("用户名错误");
            return false;
        }
        sendCommand("PASS " + password);
        if (!isValid()) {
            System.out.println("密码错误");
            return false;
        }
        return true;
    }

    public String getMail(int msg) {
        sendCommand("RETR " + msg);
        if (!isValid()) {
            System.out.println("获取邮件失败");
            return "";
        }
        String result = getResponse();
        String tmp = getResponse();
        while (!tmp.equals(".")) {
            result = result + tmp;
            tmp = getResponse();
        }
        return result;
    }

    public MailInformation stat() {
        MailInformation m = new MailInformation(-1, -1);
        sendCommand("STAT");
        if (!isValid()) {
            System.out.println("获取邮箱信息失败");
        }
        String[] strings = getResponse().split(" ");

        m.id = Integer.valueOf(strings[1]);
        m.size = Integer.valueOf(strings[2]);
        return m;
    }

    public ArrayList<MailInformation> list() {
        sendCommand("LIST");
        if(!isValid())
            return null;
        ArrayList<MailInformation> results = new ArrayList<MailInformation>();
        getResponse();
        String responseLine = getResponse();
        String tmp = getResponse();
        while(!tmp.equals("."))
        {
            String[] strings = responseLine.split(" ");
            int id = Integer.valueOf(strings[0]);
            int num = Integer.valueOf(strings[1]);
            MailInformation m = new MailInformation(id,num);
            results.add(m);
            responseLine = tmp;
            tmp = getResponse();
            System.out.println(tmp);
        }
        return results;
    }

    public boolean delete(int msg)
    {
        sendCommand("DELE "+msg);
        if(isValid())
            return true;
        return false;
    }

    public ArrayList<MailInformation> updateAllInformation()
    {
        ArrayList<MailInformation> mails = list();
        for(MailInformation m:mails)
        {
            int msg = m.id;
            String content = getMail(msg);
            System.out.println(content);
            m.content = content;
        }
        return mails;
    }

    public static void main(String[]args)
    {
        try{
            Client c = new Client("pop3.163.com",110);
            c.login("13013550115","Caozixuan98724");
            ArrayList<MailInformation> mails = c.updateAllInformation();
            for(MailInformation m:mails)
            {
                System.out.println(m.size);
            }
        }catch (Exception e)
        {

        }

    }


    /**
     * 获得邮件主题
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    public String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件发件人
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();
        if (froms.length < 1)
            throw new MessagingException("没有发件人!");

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "<" + address.getAddress() + ">";

        return from;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     * @param msg 邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    public String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }

        if (addresss == null || addresss.length < 1)
            throw new MessagingException("没有收件人!");
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress)address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length()-1); //删除最后一个逗号

        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    public String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null)
            return "";

        if (pattern == null || "".equals(pattern))
            pattern = "yyyy年MM月dd日 E HH:mm ";

        return new SimpleDateFormat(pattern).format(receivedDate);
    }


    public boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part)part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     * @param msg 邮件内容
     * @return 如果邮件已读返回true,否则返回false
     * @throws MessagingException
     */
    public boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     * @param msg 邮件内容
     * @return 需要回执返回true,否则返回false
     * @throws MessagingException
     */
    public boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null)
            replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "紧急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     * @param part 邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part)part.getContent(),content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart,content);
            }
        }

    }



}
