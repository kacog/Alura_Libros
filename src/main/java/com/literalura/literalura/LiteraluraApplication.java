package com.literalura.literalura;


import com.literalura.literalura.principal.Principal;
import com.literalura.literalura.repository.AutoresRepository;
import com.literalura.literalura.repository.LibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
	@Autowired
	private AutoresRepository autoresRepository;

	@Autowired
	private LibrosRepository librosRepository;



	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal=new Principal(autoresRepository,librosRepository);
		principal.muestraElMenu();

	}
}
