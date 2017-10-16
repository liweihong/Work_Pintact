package com.pintact.data;

public class UserDTO extends BasicUserDTO{

  private String emailId;
  private int mobileNumber;

  public String getEmailId() {
    return emailId;
  }
  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }
  public int getMobileNumber() {
    return mobileNumber;
  }
  public void setMobileNumber(int mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

}
