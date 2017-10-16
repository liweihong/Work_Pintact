package com.pintact.data;

public enum EventType {
  // TODO extract descriptions
  PROFILE_UPDATE((byte)0, "One of your contacts has updated a profile."),
  CONTACT_INVITE((byte)1, "You have been invited to connect."),
  ADDITIONAL_PROFILE_SHARE((byte)2, "An existing contact has shared an additional profile with you."),
  PROFILE_UNSHARE((byte)3, "A profile has been unshared."),
  CONTACT_INVITE_ACCEPTED((byte) 4, "An invitation has been accepted."),
  CONTACT_INVITE_REJECTED((byte)5, "An invitation has been rejected."),
  GROUP_INVITE_ACCEPTED((byte)6, "You have a new contact via a group."),
  CONTACT_INTRODUCE((byte)7, "You have received an introduction."),
  PROFILE_CREATE((byte)8, "Profile created"),
  UPDATE_PROFILE_SHARE((byte)9, "An existing contact has altered the list of profiles shared with you.");

  private byte id;
  
  private String simpleDescription;
  
  private EventType(byte id, String simpleDescription){
    this.id = id;
  }

  public byte getId(){
    return this.id;
  }
  
  public String getSimpleDescription() {
    return this.simpleDescription;
  }
}
