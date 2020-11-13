package shopee2.pormotionService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages="shopee2.controller")
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
