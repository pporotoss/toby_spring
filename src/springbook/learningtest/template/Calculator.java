package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	
	public int calcSum(String filePath) throws IOException {
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value += Integer.parseInt(line);
			}
		};
		
		return lineReadTemplat(filePath, sumCallback, 0);
	}
	
	public int calcMultiply(String filePath) throws IOException {
		LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value *= Integer.parseInt(line);
			}
		};
		
		return lineReadTemplat(filePath, multiplyCallback, 1);
	}
	
	public String concatenate(String filePath) throws IOException {
		LineCallback<String> concatenateCallback = new LineCallback<String>() {
			@Override
			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}
		};
		return lineReadTemplat(filePath, concatenateCallback, "");
	}
	
	public <T> T lineReadTemplat(String filePath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(filePath));
				T result = initVal;
				String line = null;
				while ((line = br.readLine()) != null) {
					result = callback.doSomethingWithLine(line, result);
				}
				return result;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw e;
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		
	}
	
	public int fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filePath));
			int ret = callback.doSomethingWithReader(br);
			return ret;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
}
