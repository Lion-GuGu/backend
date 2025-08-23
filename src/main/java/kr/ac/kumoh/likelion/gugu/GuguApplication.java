package kr.ac.kumoh.likelion.gugu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "kr.ac.kumoh.likelion.gugu")
public class GuguApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuguApplication.class, args);
	}

}
