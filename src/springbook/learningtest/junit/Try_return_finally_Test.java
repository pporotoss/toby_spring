package springbook.learningtest.junit;

import org.junit.Test;

public class Try_return_finally_Test {
	
	@Test
	public void try_return_finally_test() {
		try {
			System.out.println("try!!");
			return;
		} finally {
			System.out.println("finally!!");
		}
	}
}
