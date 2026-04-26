package monitoramento.preco.RabbitConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public org.springframework.amqp.core.Queue filaPrecos() {
        return new org.springframework.amqp.core.Queue("fila_precos", true);
    }
    
}
