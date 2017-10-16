package com.pintact.data;

import com.pintact.data.EventType;

public class NotificationDTO {

  public static final String PROFILES_KEY ="profiles";
  public static final String FULL_PROFILES_KEY = "fullProfiles";
  public static final String SENDER_KEY = "sender";
  public static final String INTRODUCING_USER_KEY = "introducingUser";
  public static final String OTHER_DATA_KEY = "other";

  private Long notificationId;
  private EventType eventType;
  private NotifyMaps data;
  public String topic;
  public NotificationDTO(){
    data = new NotifyMaps();
  }
  
  public void setNotificationId(Long notificationId) {
    this.notificationId = notificationId;
  }
  
  public Long getNotificationId() {
    return notificationId;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public NotifyMaps getData() {
    return data;
  }

  public void setData(NotifyMaps data) {
    this.data = data;
  }

}
