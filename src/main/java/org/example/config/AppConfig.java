package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.example.service.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ReportService reportService() {
        return new ReportService();
    }

    @Bean
    public OpenAPI missionArchiveOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mission Archive API")
                        .description("Архивный хаб миссий колледжа. Позволяет загружать миссии, " +
                                "просматривать архив и генерировать отчёты.")
                        .version("1.0"));
    }
}
