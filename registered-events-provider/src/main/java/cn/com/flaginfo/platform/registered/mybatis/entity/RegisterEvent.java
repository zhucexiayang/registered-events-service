package cn.com.flaginfo.platform.registered.mybatis.entity;

import cn.com.flaginfo.platform.registered.mybatis.base.BaseEntity;

public class RegisterEvent extends BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column register_event.app_id
     *
     * @mbggenerated
     */
    private Long appId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column register_event.event_type
     *
     * @mbggenerated
     */
    private String eventType;


    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column register_event.app_id
     *
     * @return the value of register_event.app_id
     *
     * @mbggenerated
     */
    public Long getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column register_event.app_id
     *
     * @param appId the value for register_event.app_id
     *
     * @mbggenerated
     */
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column register_event.event_type
     *
     * @return the value of register_event.event_type
     *
     * @mbggenerated
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column register_event.event_type
     *
     * @param eventType the value for register_event.event_type
     *
     * @mbggenerated
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    @Override
    public String toString() {
        return "RegisterEvent{" +
                "appId=" + appId +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}