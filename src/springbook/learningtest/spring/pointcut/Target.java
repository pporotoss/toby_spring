package springbook.learningtest.spring.pointcut;


public class Target implements TargetInterface{

	@Override
	public void hello() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hello(String a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int plus(int a, int b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int minus(int a, int b) throws RuntimeException{
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void method() {}
	
}
