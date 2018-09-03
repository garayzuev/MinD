package com.horizon.mind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@SpringBootApplication(scanBasePackages = "com.horizon.mind")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
