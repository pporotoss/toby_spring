package springbook.learningtest.spring.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message>{
	
	private String text;
	
	public void setText(String text) {		// Message 인스턴스 생성시에 필요한 매개변수를 대신 주입받는다.
		this.text = text;
	}
	
	@Override
	public Message getObject() throws Exception {
		return Message.newMessage(text);
	}

	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
	
}
