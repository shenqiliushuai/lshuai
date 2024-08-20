package com.les.ls.service.impl;

import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 邮箱方式下载器
 *
 * @author levis
 */
public class EmailDownloader extends AbstractDownloader {

    EmailDownloader(JfSetting setting) {
        initDownloader(setting);
    }

    /**
     * 邮件目录，资源需释放
     */
    private Folder folder;
    /**
     * 收件箱，资源需释放
     */
    private Store store;

    /**
     * 下载方法
     *
     * @param placeholder 占位符对象
     * @return 下载结果
     */
    @Override
    public DownloadResult download(Placeholder placeholder) {
        //删除掉历史文件
        clearHistoryData();
        DownloadResult downloadResult = new DownloadResult();
        openConnect();
        List<String> list = new ArrayList<>();
        try {
            //搜索收件箱中的所有符合条件的邮件
            Message[] messages = folder.search(emailFilter(placeholder));
            //下载文件个数限制，压缩文件解压后产生多个文件，不做控制，默认10个
            Integer downloadNum = setting.getDownloadConfig().getInteger("downloadNum");
            downloadNum = downloadNum == null ? 10 : downloadNum;
            for (Message message : messages) {
                if (hasAttachment(message)) {
                    if (list.size() < downloadNum) {
                        List<MailAttachment> mailAttachments = new ArrayList<>();
                        //获取附件
                        getAttachment(message, mailAttachments);
                        //保存附件到本地
                        List<MailAttachment> downloadFiles = saveAttachment(mailAttachments);
                        //解压附件
                        decompressionAttachment(downloadFiles, list);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("邮件下载异常！", e);
            return downloadResult;
        } finally {
            closeConnect();
        }
        downloadResult.setType(DownloadResult.TYPE_FILE_LIST);
        downloadResult.setFileList(list);
        return downloadResult;
    }

    @Override
    public Boolean testConnect() {
        openConnect();
        if (store != null && store.isConnected() && folder.isOpen()) {
            closeConnect();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开邮箱服务连接
     */
    private void openConnect() {
        String protocol = setting.getDownloadConfig().getString("emailType");
        String server = setting.getDownloadConfig().getString("emailServer");
        String port = setting.getDownloadConfig().getString("emailPort");
        String username = setting.getDownloadConfig().getString("targetAddress");
        String password = setting.getDownloadConfig().getString("password");
        String ssl = setting.getDownloadConfig().getString("isSSL");
        //连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", protocol);
        switch (protocol) {
            case "pop3":
                props.setProperty("mail.pop3.host", server);
                props.setProperty("mail.pop3.port", port);
                if ("1".equals(ssl)) {
                    Security.addProvider(new BouncyCastleProvider());
                    props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.setProperty("mail.pop3.socketFactory.fallback", "false");
                    props.setProperty("mail.pop3.socketFactory.port", port);
                }
                break;
            case "imap":
                props.setProperty("mail.imap.host", server);
                props.setProperty("mail.imap.port", port);
                if ("1".equals(ssl)) {
                    props.setProperty("mail.imap.ssl.enable", "true");
                    props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                }
                break;
            default:
                logger.warn("未知协议类型 protocol->{}", protocol);
                break;
        }
        try {
            // 创建Session实例对象
            Session session = Session.getInstance(props);
            // 加载对应协议的Store对象
            store = session.getStore(protocol);
            // 连接邮件服务器
            store.connect(username, password);
            // 获得收件箱
            folder = store.getFolder("INBOX");
            //打开收件箱，只读打开
            folder.open(Folder.READ_ONLY);
        } catch (Exception e) {
            logger.error("邮件服务连接异常！", e);
        }
    }

    /**
     * 关闭邮箱服务连接
     */
    private void closeConnect() {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(true);
            } catch (MessagingException e) {
                logger.error("邮箱资源释放异常！");
            }
        }
        if (store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException e) {
                logger.error("邮箱资源释放异常！");
            }
        }
    }

    /**
     * email搜索条件
     *
     * @param placeholder 占位符对象
     * @return 搜索词组
     */
    private SearchTerm emailFilter(Placeholder placeholder) {
        List<SearchTerm> searchTerms = new ArrayList<>();
        if (placeholder != null && StringUtils.isNotEmpty(placeholder.getDate())) {
            Date startDate = parseDate(placeholder.getDate() + "00:00:00");
            Date endDate = parseDate(placeholder.getDate() + "23:59:59");
            //大于等于
            SearchTerm startTerm = new SentDateTerm(ComparisonTerm.GE, startDate);
            //小于等于
            SearchTerm endTerm = new SentDateTerm(ComparisonTerm.LE, endDate);
            searchTerms.add(startTerm);
            searchTerms.add(endTerm);
        }
        //发件人搜索
        String fromEmail = setting.getDownloadConfig().getString("senderEmail");
        if (StringUtils.isNotEmpty(fromEmail)) {
            SearchTerm fromTerm = new FromStringTerm(fromEmail);
            searchTerms.add(fromTerm);
        }
        //主题搜索
        String subject = setting.getDownloadConfig().getString("billRules");
        if (StringUtils.isNotEmpty(subject)) {
            //占位符处理
            subject = disposePlaceholder(subject, placeholder);
            SearchTerm subjectTerm = new SubjectTerm(subject);
            searchTerms.add(subjectTerm);
        }
        return new AndTerm(searchTerms.toArray(new SearchTerm[0]));
    }

    /**
     * 时间转换
     *
     * @param dateStr 字符串
     * @return 时间
     */
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            return sdf.parse(dateStr);
        } catch (Exception e) {
            logger.error("日期转换异常！", e);
        }
        return new Date();
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
            //复杂体邮件
            Multipart multipart = (Multipart) part.getContent();
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                    list.add(new MailAttachment(bodyPart, billFilePath));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    getAttachment(bodyPart, list);
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            getAttachment((Part) part.getContent(), list);
        }
    }

    /**
     * 保存附件
     *
     * @param list 附件传输集合
     */
    private List<MailAttachment> saveAttachment(List<MailAttachment> list) {
        List<MailAttachment> result = new ArrayList<>();
        for (MailAttachment mailAttachment : list) {
            File file = new File(mailAttachment.getPath());
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    logger.warn("文件夹创建异常，请检查服务运行权限！path->{}", file.getPath());
                }
            }
            //文件存储路径running dir/billFile/机构ID_渠道编码_下载方式_文件名
            String outFileName = setting.getOrgCode() + "_" +
                    setting.getChannel() + "_" +
                    setting.getDownloadType() + "_" + mailAttachment.getName();
            //文件名重新命名，向下传递，解压时用到
            mailAttachment.setName(outFileName);
            try (FileOutputStream outputStream = new FileOutputStream(mailAttachment.getPath() + File.separator + outFileName);
                 InputStream inputStream = mailAttachment.getInputStream()) {
                int len;
                byte[] bytes = new byte[1024 * 1024];
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                    outputStream.flush();
                }
            } catch (IOException e) {
                logger.error("附件下载异常！", e);
            }
            mailAttachment.setInputStream(null);
            result.add(mailAttachment);
        }
        return result;
    }

    /**
     * 解压zip附件
     *
     * @param attachmentList 附件容器对象集合
     * @param fileList       文件集合
     */
    private void decompressionAttachment(List<MailAttachment> attachmentList, List<String> fileList) {
        for (MailAttachment attachment : attachmentList) {
            if (attachment.getName().endsWith(".zip")) {
                //zip解压到当前目录，zip不返回给接口
                boolean unzipResult = unzip(new File(attachment.getPath() + File.separator + attachment.getName()),
                        attachment.getPath());
                if (!unzipResult) {
                    logger.warn("zip解压失败！zipFile->{}", attachment.getPath() + File.separator + attachment.getName());
                }
                File file = new File(attachment.getPath());
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String fileName = setting.getOrgCode() + "_" +
                                setting.getChannel() + "_" +
                                setting.getDownloadType() + "_";
                        if (f.getName().startsWith(fileName) && !f.getName().endsWith(".zip")) {
                            fileList.add(f.getPath());
                        }
                    }
                }
            } else {
                fileList.add(attachment.getPath() + File.separator + attachment.getName());
            }
        }
    }

    /**
     * zip解压
     *
     * @param srcFile     源zip文件
     * @param destDirPath 解压目录
     * @return 解压结果
     */
    public boolean unzip(File srcFile, String destDirPath) {
        final int BUFFER_SIZE = 1024;
        try (ZipFile zipFile = getZipFile(srcFile)) {
            Enumeration<?> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                String fileName = destDirPath + File.separator +
                        setting.getOrgCode() + "_" +
                        setting.getChannel() + "_" +
                        setting.getDownloadType() + "_"
                        + zipEntry.getName();
                File file = new File(fileName);
                if (zipEntry.isDirectory()) {
                    if (!file.mkdirs()) {
                        logger.warn("解压时创建文件夹失败！");
                    }
                } else {
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            logger.warn("解压时创建文件夹失败！");
                            continue;
                        }
                    }
                    if (!file.createNewFile()) {
                        logger.warn("解压时创建文件失败！file->{}", file.getName());
                        continue;
                    }
                    try (InputStream is = zipFile.getInputStream(zipEntry);
                         FileOutputStream fos = new FileOutputStream(file)) {
                        int len;
                        byte[] buf = new byte[BUFFER_SIZE];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    } catch (IOException e) {
                        logger.warn("解压文件时发生了异常！", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("文件解压失败！path->{}", srcFile.getPath(), e);
            return false;
        }
        return true;
    }

    private ZipFile getZipFile(File zipFile) throws Exception {
        ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8);
        Enumeration<?> entries = zip.entries();
        if (entries.hasMoreElements()) {
            try {
                entries.nextElement();
                zip.close();
                zip = new ZipFile(zipFile, StandardCharsets.UTF_8);
                return zip;
            } catch (Exception e) {
                zip.close();
                zip = new ZipFile(zipFile, Charset.forName("GBK"));
                return zip;
            }
        }
        return zip;
    }

    /**
     * 邮件附件类-作为传递信息的容器类
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
