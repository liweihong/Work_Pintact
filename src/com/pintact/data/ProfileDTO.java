package com.pintact.data;

import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;
import java.util.List;

public class ProfileDTO{

  private Long userId;
  private Long profileId;
  
  private UserProfile userProfile;
  private List<UserProfileAddress> userProfileAddresses;
  private List<UserProfileAttribute> userProfileAttributes;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getProfileId() {
    return profileId;
  }

  public void setProfileId(Long id) {
    this.profileId = id;
  }

  
  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public List<UserProfileAddress> getUserProfileAddresses() {
    return userProfileAddresses;
  }

  public void setUserProfileAddresses(List<UserProfileAddress> userProfileAddresses) {
    this.userProfileAddresses = userProfileAddresses;
  }

  public List<UserProfileAttribute> getUserProfileAttributes() {
    return userProfileAttributes;
  }

  public void setUserProfileAttributes(List<UserProfileAttribute> userProfileAttributes) {
    this.userProfileAttributes = userProfileAttributes;
  }
}
