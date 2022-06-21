package com.assignmentjava.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class DotEnvService {

    public String getDotEnvValue(String keyName){
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
        return dotenv.get(keyName);
    }
}
