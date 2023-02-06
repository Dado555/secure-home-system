package rs.securehome.devicesim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DevicesimApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevicesimApplication.class, args);
	}

}
