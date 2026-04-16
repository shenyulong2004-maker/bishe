package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 作业
 */
@TableName("homework")
public class HomeworkEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /** 作业内容 */
    private String content;

    /** 是否永久保存 0:否 1:是 */
    private Integer isPermanent;

    /** 附件地址 */
    private String attachmentUrl;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(Integer isPermanent) {
        this.isPermanent = isPermanent;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
