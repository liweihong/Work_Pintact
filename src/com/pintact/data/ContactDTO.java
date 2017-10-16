package com.pintact.data;

import java.util.List;

public class ContactDTO {
  private Long userId;
  private UserDTO contactUser;
  private List<ProfileDTO> sharedProfiles;
  private List<String> labels;
  private String contactNote;
  private Long groupingId;
  private boolean isAContact;
  public  boolean isLocalContact;

  public boolean isAContact() {
    return isAContact;
  }

  public void setAContact(boolean isAContact) {
    this.isAContact = isAContact;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  public List<ProfileDTO> getSharedProfiles() {
    return sharedProfiles;
  }

  public void setSharedProfiles(List<ProfileDTO> sharedProfiles) {
    this.sharedProfiles = sharedProfiles;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public UserDTO getContactUser() {
    return contactUser;
  }

  public void setContactUser(UserDTO contactUser) {
    this.contactUser = contactUser;
  }
  
  public String getContactNote() {
    return contactNote;
  }
  
  public void setContactNote(String contactNote) {
    this.contactNote = contactNote;
  }
  
  public Long getGroupingId() {
    return groupingId;
  }
  
  public void setGroupingId(Long groupingId) {
    this.groupingId = groupingId;
  }
}
