package com.marcode.domaincrawler.service;

import com.marcode.domaincrawler.entities.Domain;
import com.marcode.domaincrawler.entities.DomainList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class DomainCrawlerService {

    @Autowired
    private KafkaTemplate<String, Domain> kafkaTemplate;
    private final String KAFKA_TOPIC = "web-domains";
    private  final String DOMAINS_URL = "https://api.domainsdb.info/v1/domains/search?domain=";
    public void crawl(String name) {
        //"https://api.domainsdb.info/v1/domains/search?domain=" + name + "&zone=com"
        Mono<DomainList> domainListMono = WebClient.create()
                .get()
                .uri(DOMAINS_URL + name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DomainList.class);

        domainListMono.subscribe(domainList -> {
            domainList.getDomains()
                .forEach(domain -> {
                    kafkaTemplate.send(KAFKA_TOPIC, domain);
                    System.out.println("Domain message " + domain.getDomain());
                });
        });

        // return "Domain crawler has scrapped your data";

    }

}
