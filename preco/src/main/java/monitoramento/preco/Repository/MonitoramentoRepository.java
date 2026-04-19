package monitoramento.preco.Repository;

import monitoramento.preco.monitoramentoPreco.MonitoramentoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoramentoRepository extends JpaRepository<MonitoramentoPreco, Long> {

    
}