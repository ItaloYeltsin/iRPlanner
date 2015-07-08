package jmetal.util;
import java.io.File;
import java.io.ObjectInputStream.GetField;
import java.util.Random;

public class InstanceGeneratorTest {
	public static void main(String[] args){
		
		InstanceGenerator test = new InstanceGenerator(6, 100, 5, 10, 100, 10);
		test.generateInstance();
	
	}	
}