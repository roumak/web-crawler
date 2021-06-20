package org.rc.webcrawler.app.distributed;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

@Configuration
class MQConfig {

    @Value("${mq.brokerUrl}")
    private String hostUrl;

    @Bean
    public Queue queue(){
        return new ActiveMQQueue("urlQueue");
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(hostUrl);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory factory){
       return   new JmsTemplate(factory);
    }

}
