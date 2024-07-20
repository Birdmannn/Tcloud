package sia.tcloud3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableFeignClients
@SpringBootApplication
public class Tcloud3Application {

    public static void main(String[] args) {
        SpringApplication.run(Tcloud3Application.class, args);
    }

}
