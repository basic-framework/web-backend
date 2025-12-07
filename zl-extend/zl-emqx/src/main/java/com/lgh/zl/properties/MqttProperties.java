package com.lgh.zl.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

 /**
 * MQTT Properties
 * @Author GuihaoLv
 */
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
    public String getBrokerUrl() {
        return brokerUrl;
    }
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "MqttProperties{" +
                "brokerUrl='" + brokerUrl + '\'' +
                ", clientId='" + clientId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}