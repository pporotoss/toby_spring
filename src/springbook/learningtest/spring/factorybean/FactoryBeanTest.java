package springbook.learningtest.spring.factorybean;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration	// 값을 설정하지 않으면 같은폴더에서 클래스이름+"-context.xml"을 기본값으로 찾는다.
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;
	
	@Test
	public void getMessageFromFactory() {
		Object message = context.getBean("message");
		assertThat(message, is(instanceOf(Message.class)));
		assertThat(((Message)message).getText(), is("Factory Bean"));
	}
	
	@Test
	public void getFactoryBean() {
		Object factory = context.getBean("&message");	// &을 앞에 붙여주면 getObject()메서드의 반환값이 아닌, FactoryBean 자체를 가져온다.
		assertThat(factory, is(instanceOf(MessageFactoryBean.class)));
	}
}
