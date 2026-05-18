package br.com.estacionamento;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "The Best Parking API", version = "1.0", description = "Sistema de gestao de estacionamento"))
public class EstacionamentoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstacionamentoApplication.class, args);
    }
}
