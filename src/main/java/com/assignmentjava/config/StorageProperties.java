package com.assignmentjava.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

//Config đơn thuộc tính

@ConfigurationProperties("storage")
@Data
public class StorageProperties {
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StorageProperties(){
        this.setLocation("upload/images");
    }

}
