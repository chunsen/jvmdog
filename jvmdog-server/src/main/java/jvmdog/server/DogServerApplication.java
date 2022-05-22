package jvmdog.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "jvmdog"
})
public class DogServerApplication {

    public static void main(String[] args){
        SpringApplication.run(DogServerApplication.class, args);
    }
}
