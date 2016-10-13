package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("junit.xml")
public class JUnitTest {
	@Autowired
	ApplicationContext context;
	
	//static JUnitTest testObject;
	static Set<JUnitTest> testObjects = new HashSet<>();
	static ApplicationContext contextObject = null;
	
	@Test
	public void test1() {
		/*assertThat(this, is(not((sameInstance(testObject)))));
		testObject = this;*/
		assertThat(testObjects, not(hasItem(this)));	// Set<>testObjects가 현재 객체를 값으로 가지고 있지 않은경우 테스트 통과 
		testObjects.add(this);
		
		assertThat(contextObject == null || contextObject == this.context, is(true));	// 둘 중 하나가 true로 나오면 통과!!
		// contextObject 에 값을 할당하지 않았으므로 null 값이 나와 테스트 통과!!
		contextObject = this.context;
	}
	
	@Test
	public void test2() {
		/*assertThat(this, is(not((sameInstance(testObject)))));
		testObject = this;*/
		assertThat(testObjects.size(), is(1));
		assertThat(testObjects, not(hasItem(this)));	// testObjects는 공유되지만 현재 객체는 새로 생성되었기 때문에 테스트 통과된다!!
		testObjects.add(this);
		
		assertTrue(contextObject == null || contextObject == this.context);	// 둘중 하나가 true로 나오면 통과!!	
		// contextObject가 현재 객체가 참조하는 ApplicationContext와 일치하면 통과!!
		contextObject = this.context;
	}
	
	@Test
	public void test3() {
		/*assertThat(this, is(not((sameInstance(testObject)))));
		testObject = this;*/
		assertThat(testObjects.size(), is(2));
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(contextObject, either(is(nullValue())).or(is(this.context)));	// contextObject가 null이거나, 현재 객체가 참조하는 ApplicationContext와 일치하면 통과!!
	}
}
