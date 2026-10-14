package com.typerush.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Model representing an authentication session / login event record.
 * Tracks user identification, timestamp, client workstation, and security status.
 * Owned by: Prashant Soni (Member 5 - Authentication & Roles) & Mukesh (Member 4 - JDBC)
 */
public class LoginRecord {

    private int id;
    private int userId;
    private String userName;
    private String email;
    private String role;
    private String deviceName;
    private String ipAddress;
    private String status; // "SUCCESS", "FAILED"
    private Timestamp loginTime;

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss");

    public LoginRecord() {
        this.loginTime = new Timestamp(System.currentTimeMillis());
        this.status = "SUCCESS";
    }

    public LoginRecord(int id, int userId, String userName, String email, String role,
                       String deviceName, String ipAddress, String status, Timestamp loginTime) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = (role != null) ? role : "USER";
        this.deviceName = (deviceName != null) ? deviceName : "LocalHost";
        this.ipAddress = (ipAddress != null) ? ipAddress : "127.0.0.1";
        this.status = (status != null) ? status : "SUCCESS";
        this.loginTime = (loginTime != null) ? loginTime : new Timestamp(System.currentTimeMillis());
    }

    public LoginRecord(int userId, String userName, String email, String role,
                       String deviceName, String ipAddress, String status) {
        this(0, userId, userName, email, role, deviceName, ipAddress, status, new Timestamp(System.currentTimeMillis()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String getFormattedTime() {
        if (loginTime == null) return "N/A";
        return DATE_FORMATTER.format(loginTime);
    }

    public String getFormattedTimestamp() {
        return getFormattedTime();
    }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }
}
