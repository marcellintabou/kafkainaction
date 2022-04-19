package com.marcode.domainprocessor;

import java.util.function.Function;
import com.marcode.domaincrawler.entities.Domain;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainKafkaProcessor {

    @Bean
    public Function<KStream<String, Domain>, KStream<String, Domain>> domainProcessor() {

        return kstream -> kstream.filter((key, domain) -> {
            if (domain.isDead()) {
                System.out.println("Inactive domain: " + domain.getDomain());
            } else {
                System.out.println("Active domain: " + domain.getDomain());
            }
            return !domain.isDead();
        });
    }
}
