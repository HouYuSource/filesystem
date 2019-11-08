package cn.shaines.filesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.shaines"})
public class FilesystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilesystemApplication.class, args);
    }

}
