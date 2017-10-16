package com.pintact.data;

public class ContactShareRequest {

  private Long sourceUserId;

  private String destinationPin;

  private Long destinationUserId;
  
  private String note;

  private Long[] userProfileIdsShared;
  
  private String groupName;

  private String expiryTimeInUTC;

  public String getExpiryTimeInUTC() {
    return expiryTimeInUTC;
  }

  public void setExpiryTimeInUTC(String expiryTimeInUTC) {
    this.expiryTimeInUTC = expiryTimeInUTC;
  }

  public String getDestinationPin() {
    return destinationPin;
  }

  public void setDestinationPin(String destinationPin) {
    this.destinationPin = destinationPin;
  }

  public Long getSourceUserId() {
    return sourceUserId;
  }

  public void setSourceUserId(Long sourceUserId) {
    this.sourceUserId = sourceUserId;
  }

  public Long getDestinationUserId() {
    return destinationUserId;
  }

  public void setDestinationUserId(Long destinationUserId) {
    this.destinationUserId = destinationUserId;
  }

  public Long[] getUserProfileIdsShared() {
    return userProfileIdsShared;
  }

  public void setUserProfileIdsShared(Long[] userProfileIdsShared) {
    this.userProfileIdsShared = userProfileIdsShared;
  }
  
  public String getNote() {
    return note;
  }
  
  public void setNote(String note) {
    this.note = note;
  }
  
  public String getGroupName() {
    return groupName;
  }
  
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  
}
