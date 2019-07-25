package com.pluralsight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FundamentalsApplication  {

//	private static final Logger log = LoggerFactory.getLogger(FundamentalsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FundamentalsApplication.class, args);
	}

	/*@Bean
	public CommandLineRunner demo(ApplicationRepository repository) {
		return (args) -> {
			repository.save(new Application("Tracking", "To track the applications", "David.Sam"));
			repository.save(new Application("Eating", "To eat", "Abc.def"));
			repository.save(new Application("Music", "To listen to music", "ghi.jkl"));
			for (Application app : repository.findAll()) {
				log.info("The application" + app);
			}
		};

	}
*/
}
