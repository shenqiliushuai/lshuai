package com.les.ls.service.impl;

import com.les.ls.utils.DateUtils;
import com.les.ls.utils.EmailAttachmentDownloadUtil;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.search.*;
import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * Email 附件Excel下载实现
 *
 * @author lshuai
 */
@Slf4j
public class EmailParseImpl extends EmailAttachmentDownloadUtil {
    /**
     * 下载邮件的时间
     */
    private static Date date;

    public static void main(String[] args) throws Exception {
        //不用网易的狗屎邮箱就好了
        date = new Date();
        EmailParseImpl emailDownload = new EmailParseImpl();
        /*receive_protocol = "pop3";
        receive_pop3_host = "pop.163.com";
        receive_pop3_port = 110;*/
        receive_protocol = EmailAttachmentDownloadUtil.PROTOCOL_IMAP;
        receive_imap_host = "imap.163.com";
        receive_imap_port = 143;
        localPath = "C:\\Users\\Administrator\\Desktop\\test";
        username = "Les_liushuai@163.com";
        password = "KRZNDMXZKTBCTWGC";
        List<File> fileList = emailDownload.emailExcelAttachmentsDownload();
        fileList.forEach(file -> log.info(file.getName()));
    }

    /**
     * 只要前一天的邮件
     *
     * @return 邮件搜索器
     */
    @Override
    public SearchTerm emailBeforeFilter() {
        //大于等于
        SearchTerm startTerm = new SentDateTerm(ComparisonTerm.GE, new Date(DateUtils.minDateTimeOfTimestamp(date.toInstant().getEpochSecond())));
        //小于等于
        SearchTerm endTerm = new SentDateTerm(ComparisonTerm.LE, new Date(DateUtils.maxDateTimeOfTimestamp(date.toInstant().getEpochSecond())));
        SearchTerm formTerm = new FromStringTerm("2298262418@qq.com");
        return new AndTerm(new SearchTerm[]{startTerm, endTerm, formTerm});
    }

    /**
     * 只要指定主体的邮件
     *
     * @param message 邮件体
     * @return 过滤规则，true放行，false丢弃
     * @throws Exception 消息体获取时可能发生的异常
     */
    @Override
    public boolean emailAfterFilter(Message message) throws Exception {
        return "测试邮件".equals(message.getSubject());
    }

    /**
     * 过滤掉不要的邮件附件，修改邮件下载地址
     *
     * @param mailAttachment 附件传输实体
     * @return 过滤规则，true放行，false丢弃
     */
    @Override
    public boolean emailAttachmentAfterFilter(EmailAttachmentDownloadUtil.MailAttachment mailAttachment) {
        //以结尾作为判断条件
        if (!mailAttachment.getPath().endsWith(File.separator)) {
            String oldPath = mailAttachment.getPath();
            ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
            String newPath = oldPath + File.separator + username
                    + File.separator + zonedDateTime.getYear()
                    + File.separator + zonedDateTime.getMonth()
                    + File.separator + zonedDateTime.getDayOfMonth()
                    + File.separator;
            mailAttachment.setPath(newPath);
        }
        return mailAttachment.getName().endsWith(".xls") || mailAttachment.getName().endsWith("xlsx") || mailAttachment.getName().endsWith(".zip");
    }
}
