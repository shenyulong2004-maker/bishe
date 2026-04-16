package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线家长会/会议预约
 */
@TableName("parent_meeting")
public class ParentMeetingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /** 会议类型：1家长一对一预约 2教师群体家长会 */
    private Integer meetingType;

    /** 会议标题 */
    private String title;

    /** 创建者角色：parent/teacher */
    private String creatorRole;

    /** 创建者ID */
    private Long creatorId;

    /** 家长ID（单聊） */
    private Long parentId;

    /** 教师ID */
    private Long teacherId;

    /** 会议时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date meetingTime;

    /** 状态：0待确认 1已确认 2已完成 3已取消 */
    private Integer status;

    /** 会议室ID */
    private String roomId;

    /** 范围：single/group */
    private String scope;

    /** 创建时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date createTime;

    /** 当前登录家长的参会状态：0待确认 1已确认 2已拒绝 */
    @TableField(exist = false)
    private Integer mineJoinStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMeetingType() { return meetingType; }
    public void setMeetingType(Integer meetingType) { this.meetingType = meetingType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCreatorRole() { return creatorRole; }
    public void setCreatorRole(String creatorRole) { this.creatorRole = creatorRole; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Date getMeetingTime() { return meetingTime; }
    public void setMeetingTime(Date meetingTime) { this.meetingTime = meetingTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Integer getMineJoinStatus() { return mineJoinStatus; }
    public void setMineJoinStatus(Integer mineJoinStatus) { this.mineJoinStatus = mineJoinStatus; }
}
