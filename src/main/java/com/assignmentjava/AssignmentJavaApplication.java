package com.assignmentjava;

import com.assignmentjava.config.MailConfig;
import com.assignmentjava.config.StorageProperties;
import com.assignmentjava.services.FileSystemStorageServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({
        StorageProperties.class
//        MailConfig.class
})
public class AssignmentJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentJavaApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FileSystemStorageServices storageService) {
        return (args -> {
            storageService.init();
        });
    }

//    @Bean
//    CommandLineRunner commandLineRunner(){
//        return new MyCommandLineRunner();
//    }
//
//    public class MyCommandLineRunner implements CommandLineRunner {
//
//        @Autowired
//        FileSystemStorageServices storageService;
//
//        @Override
//        public void run(String... args) throws Exception {
//            storageService.init();
//        }
//    }
}
