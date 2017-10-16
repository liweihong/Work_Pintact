package com.pintact.data;

public class DeviceRegistrationRequest {

		public enum DeviceType {
			  
			  APPLE,
			  
			  ANDROID;
	
			}
	
	  private Long userId;
	  
	  String deviceId;
	  
	  String clientInfo;
	  
	  String staticId;
	  
	  DeviceType deviceType;
	  
	  boolean userPreference;
	  
	  public Long getUserId() {
	    return userId;
	  }
	  
	  public String getStaticId() {
		    return staticId;
	  }	  
	  
	  public void setStaticId(String staticId) {
		   this.staticId = staticId;
	  }
	  
	  public void setUserId(Long userId) {
	    this.userId = userId;
	  }
	  
	  public String getDeviceId() {
	    return deviceId;
	  }
	  
	  public void setDeviceId(String deviceId) {
	    this.deviceId = deviceId;
	  }
	  
	  public String getClientInfo() {
		    return clientInfo;
	  }
		  
	  public void setClientInfo(String clientInfo) {
		   this.clientInfo = clientInfo;
	  }
		  
	  public DeviceType getDeviceType() {
	    return deviceType;
	  }
	  
	  public void setDeviceType(DeviceType deviceType) {
	    this.deviceType = deviceType;
	  }
	  
	  public boolean getUserPreference() {
	    return userPreference;
	  }
	  
	  public void setUserPreference(boolean userPreference) {
	    this.userPreference = userPreference;
	  }
	
	
}
