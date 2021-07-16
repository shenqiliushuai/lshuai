package com.les.ls.utils;


import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Email 附件下载工具类
 *
 * @author lshuai
 */
@Slf4j
public abstract class EmailAttachmentDownloadUtil {
    /**
     * 使用的连接协议
     */
    public static String receive_protocol;
    /**
     * POP3协议
     */
    public static final String PROTOCOL_POP3 = "pop3";
    /**
     * IMAP协议
     */
    public static final String PROTOCOL_IMAP = "imap";
    /**
     * 附件下载本地路径
     */
    public static String localPath;
    /**
     * POP3接收邮件服务器主机地址
     */
    public static String receive_pop3_host;
    /**
     * POP3接收邮件服务器端口
     */
    public static Integer receive_pop3_port;
    /**
     * IMAP接收邮件服务器主机地址
     */
    public static String receive_imap_host;
    /**
     * IMAP接收邮件服务器主机端口
     */
    public static Integer receive_imap_port;
    /**
     * 邮箱用户名
     */
    public static String username;
    /**
     * 邮箱密码或授权码
     */
    public static String password;
    /**
     * pop3邮箱消息存储器对象，需要关闭的资源
     */
    private POP3Store pop3Store;
    /**
     * pop3邮箱目录对象，需要关闭的资源
     */
    private POP3Folder pop3Folder;
    /**
     * imap邮箱消息存储器对象，需要关闭的资源
     */
    private IMAPStore imapStore;
    /**
     * imap邮箱目录对象，需要关闭的资源
     */
    private IMAPFolder imapFolder;

    /**
     * 通过指定协议获得邮件消息
     *
     * @param protocol 邮件协议
     * @return 邮件消息数组
     * @throws Exception 可能发生的消息异常
     */
    private Message[] getMessages(String protocol) throws Exception {
        switch (protocol) {
            case PROTOCOL_POP3:
                return getPopMessages();
            case PROTOCOL_IMAP:
                return getImapMessage();
            default:
                log.warn("未知协议类型！请使用内置协议protocal->{}", protocol);
                return new Message[]{};
        }
    }


    /**
     * 通过pop3协议获取邮件信息
     * pop3协议下只能通过sentDate来查询
     * receivedDate都为null
     *
     * @return Message[] 邮件数组
     * @throws Exception 异常消息
     */
    public Message[] getPopMessages() throws Exception {
        Properties props = new Properties();
        // pop3服务器主机
        props.setProperty("mail.pop3.host", receive_pop3_host);
        // pop3邮件服务器端口
        props.setProperty("mail.pop3.port", receive_pop3_port.toString());
        // 使用pop3协议
        props.setProperty("mail.imapStore.protocol", receive_protocol);
        Session session = Session.getInstance(props);
        pop3Store = (POP3Store) session.getStore(receive_protocol);
        pop3Store.connect(receive_pop3_host, receive_pop3_port, username, password);
        //获得收件箱
        pop3Folder = (POP3Folder) pop3Store.getFolder("INBOX");
        //打开收件箱
        pop3Folder.open(Folder.READ_ONLY);
        //搜索收件箱中的所有符合条件的邮件,并解析
        return pop3Folder.getMessages();
    }

    /**
     * 通过IMAP协议获取邮件信息
     *
     * @return Message[]
     * @throws Exception 异常消息
     */
    public Message[] getImapMessage() throws Exception {
        Properties props = new Properties();
        // imap服务器主机
        props.setProperty("mail.imap.host", receive_imap_host);
        // imap邮件服务器端口
        props.setProperty("mail.imap.port", receive_imap_port.toString());
        // 使用imap协议
        props.setProperty("mail.imapStore.protocol", receive_protocol);
        Session session = Session.getInstance(props);
        imapStore = (IMAPStore) session.getStore(receive_protocol);
        imapStore.connect(receive_imap_host, receive_imap_port, username, password);
        //获得收件箱
        imapFolder = (IMAPFolder) imapStore.getFolder("INBOX");
        //打开收件箱
        imapFolder.open(Folder.READ_ONLY);
        //搜索收件箱中的所有符合条件的邮件,并解析
        return imapFolder.getMessages();
    }


    /**
     * 下载Email Excel附件信息
     *
     * @return 附件文件集合，已保留在本地
     * @throws Exception 异常消息
     */
    public List<File> emailExcelAttachmentsDownload() throws Exception {
        Instant start = Instant.now();
        List<File> fileList = new ArrayList<>();
        Message[] messages = getMessages(receive_protocol);
        try {
            for (Message message : messages) {
                if (emailAfterFilter(message)) {
                    if (hasAttachment(message)) {
                        List<MailAttachment> mailAttachments = new ArrayList<>();
                        //获取附件
                        getAttachment(message, mailAttachments);
                        //保存附件到本地
                        List<MailAttachment> downloadFiles = saveAttachment(mailAttachments);
                        //解压附件
                        decompressionAttachment(downloadFiles, fileList);
                    }
                }
            }
            releaseResource();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Instant end = Instant.now();
        log.info("解析邮件附件共耗时：{}毫秒！", Duration.between(start, end).toMillis());
        return fileList;
    }

    /**
     * 解压zip附件，只做一层解压
     *
     * @param attachmentList 附件容器对象集合
     * @param fileList       文件集合
     */
    public void decompressionAttachment(List<MailAttachment> attachmentList, List<File> fileList) {
        for (MailAttachment attachment : attachmentList) {
            if (attachment.getName().endsWith(".zip")) {
                boolean unzipResult = ZipUtils.unzip(new File(attachment.getPath() + attachment.getName()), attachment.getPath());
                if (unzipResult) {
                    File file = new File(attachment.getPath());
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (emailAttachmentAfterFilter(attachment)) {
                                fileList.add(f);
                            }
                        }
                    }
                } else {
                    log.warn("附件为zip解压失败！fileName->{}", attachment.getName());
                }
            } else if (emailAttachmentAfterFilter(attachment)) {
                fileList.add(new File(attachment.getPath() + attachment.getName()));
            } else {
                log.warn("未知的压缩文件！File Name->{}", attachment.getName());
            }
        }
    }

    /**
     * Email前置过滤器，过滤掉不想要的邮件
     *
     * @return Email搜索器
     * @throws Exception 异常消息
     */
    public abstract SearchTerm emailBeforeFilter() throws Exception;

    /**
     * Email后置过滤器，过滤掉不想要的邮件
     *
     * @param message 邮件体
     * @return true：保存，false：丢弃
     * @throws Exception 邮件消息异常传递基类
     */
    public abstract boolean emailAfterFilter(Message message) throws Exception;

    /**
     * Email附件后置过滤器，过滤掉不想要的附件，可以在这里更改路径和下载的文件名称
     *
     * @param mailAttachment 附件传输实体
     * @return true：保留，false：丢弃
     */
    public abstract boolean emailAttachmentAfterFilter(MailAttachment mailAttachment);

    /**
     * 保存附件
     *
     * @param list 附件传输集合
     */
    private List<MailAttachment> saveAttachment(List<MailAttachment> list) {
        List<MailAttachment> result = new ArrayList<>();
        for (MailAttachment mailAttachment : list) {
            if (emailAttachmentAfterFilter(mailAttachment)) {
                try (FileOutputStream outputStream = new FileOutputStream(new File(mailAttachment.getPath() + mailAttachment.getName()));
                     InputStream inputStream = mailAttachment.getInputStream()) {
                    int len;
                    byte[] bytes = new byte[1024 * 1024];
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    log.error("附件下载异常！", e);
                }
                mailAttachment.setInputStream(null);
                result.add(mailAttachment);
            }
        }
        return result;
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @return 邮件中是否存在附件
     * @throws MessagingException 邮件消息异常传递基类
     * @throws IOException        IO异常
     */
    public boolean hasAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = hasAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }
                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }
                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = hasAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 获取附件，邮件内容里的附件（图片等）忽略
     *
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param list 附件容器
     * @throws Exception 异常消息
     */
    public void getAttachment(Part part, List<MailAttachment> list) throws Exception {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                    list.add(new MailAttachment(bodyPart, localPath));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    getAttachment(bodyPart, list);
                } else {
                    String contentType = bodyPart.getContentType();
                    log.warn("位置的内容类型！contentType->{}", contentType);
                    /*if (contentType.contains("name") || contentType.contains("application")) {
                        //todo 这个地方可以为空
                        getAttachment(bodyPart, list);
                    }*/
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            getAttachment((Part) part.getContent(), list);
        }
    }

    /**
     * 释放邮箱资源
     *
     * @throws MessagingException 可能发生的邮件消息异常基类
     */
    private void releaseResource() throws MessagingException {
        if (pop3Folder != null) {
            pop3Folder.close(true);
        }
        if (pop3Store != null) {
            pop3Store.close();
        }
        if (imapFolder != null) {
            imapFolder.close();
        }
        if (imapStore != null) {
            imapStore.close();
        }
    }


    /**
     * 邮件附件类-作为传递信息的容器类
     *
     * @author lshuai
     */
    public static class MailAttachment {
        private String name;
        private String path;
        private InputStream inputStream;

        public MailAttachment(BodyPart bodyPart, String path) throws Exception {
            // 附件名通过MimeUtility解码，否则是乱码
            this.name = MimeUtility.decodeText(bodyPart.getFileName());
            this.path = path;
            this.inputStream = bodyPart.getInputStream();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }
}
