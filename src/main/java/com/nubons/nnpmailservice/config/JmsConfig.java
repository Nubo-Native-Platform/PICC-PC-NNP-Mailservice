package com.nubons.nnpmailservice.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
@Configuration
public class JmsConfig {

	@Value("${spring.activemq.broker-url}")
	private String brokerUrl;

	@Value("${spring.activemq.user}")
	private String user;

	@Value("${spring.activemq.password}")
	private String password;

	public static final String REQUEST_QUEUE = "nnp::nnp-sendmail";
	
	@Bean
	public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
		return new ActiveMQConnectionFactory(brokerUrl, user, password);
	}
	
	@Bean
	public CachingConnectionFactory cachingConnectionFactory() {
		return new CachingConnectionFactory(receiverActiveMQConnectionFactory());
	}

	
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(receiverActiveMQConnectionFactory());
		factory.setConcurrency("3-10");
		return factory;
	}
	
	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate template =  new JmsTemplate(cachingConnectionFactory());
		return template;
	}

}