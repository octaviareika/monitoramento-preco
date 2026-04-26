package monitoramento.preco.Worker;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import monitoramento.preco.Repository.MonitoramentoRepository;
import monitoramento.preco.monitoramentoPreco.MonitoramentoPreco;

@Component
public class PrecoWorker {
    
    @Autowired
    private MonitoramentoRepository monitoramentoRepository;

    @Autowired // api do spring para enviar email
    private JavaMailSender javaMailSender;


    @RabbitListener(queues = "fila_precos")
    public void processarMonitoramentoPreco(Long id) {
        MonitoramentoPreco item  = monitoramentoRepository.findById(id).orElse(null);

        if (item != null) {
            try {
                Document doc = Jsoup.connect(item.getUrl()).
                userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept-Language", "pt-BR,pt;q=0.9")
                .get();

                String precoTexto = doc.select("span.a-offscreen").first().text(); // pega a tag

                if (precoTexto != null && !precoTexto.isEmpty()){
                    // limpa o texto-- remove "R$", espaços, pontos de milhar e troca a vírgula por ponto
                    String limpo = precoTexto.replaceAll("[^0-9,]", "").replace(",", ".");
                    Double precoAtual = Double.parseDouble(limpo);

                    System.out.println("Produto: " + id + " | Preço na Amazon: " + precoAtual);

                    if (precoAtual <= item.getPrecoAlvo()) {
                        enviarEmail(item, precoAtual); // caso o preco atual seja menor ou igual ao preço alvo, envia email para o usuário
                    }
                    

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void enviarEmail(MonitoramentoPreco item, Double preco) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com");
        message.setTo(item.getEmailUsuario());
        message.setSubject("ALERTA DE PREÇO: " + item.getUrl());
        message.setText("O preço caiu para R$ " + preco + "! Aproveite agora.");
        javaMailSender.send(message);
    }

}
