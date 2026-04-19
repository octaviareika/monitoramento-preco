package monitoramento.preco.Scheduler;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import monitoramento.preco.Repository.MonitoramentoRepository;

@Component
public class PrecoScheduler {

    // injecao de dependencias
    @Autowired
    private MonitoramentoRepository monitoramentoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 60000) // executa a cada 60 segundos
    public void verificarPrecos() {
        monitoramentoRepository.findAll().forEach(item -> {
            rabbitTemplate.convertAndSend("precoQueue", item.getUrl()); // envia a URL para a fila do RabbitMQ
        });
    }
}
