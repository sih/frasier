package eu.waldonia.labs.frasier;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author sih
 */
@SpringBootApplication
@Configuration
@ComponentScan({"eu.waldonia.labs.frasier","com.acrcloud.utils"})
public class FrasierApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(FrasierApplication.class)
                .web(true)
                .run(args);
    }


}
