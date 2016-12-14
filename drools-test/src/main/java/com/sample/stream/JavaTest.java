package com.sample.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author zhengbo
 * @date 2016年10月8日
 * 
 */
public class JavaTest {
	public static long loopTime = 0;

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Stream<List<Integer>> inputStream = Stream.of(Arrays.asList(1), Arrays.asList(2, 3), Arrays.asList(4, 5, 6));
		Stream<Integer> outputStream = inputStream.flatMap((childList) -> childList.stream());
		outputStream.filter(p -> p>3).forEach(p -> System.out.println(p));
		
		ConvertCase();

		long startTime = System.currentTimeMillis();

		List<Person> people = new ArrayList<>();
		for (int i = 0; i < loopTime; i++) {
			people.add(new Person(nameGen(), ageGen()));
		}

		Predicate<Person> pred = (p) -> p.getAge() > 65;
		System.out.println("Ready time:" + (System.currentTimeMillis() - startTime));
		/***************************** 2 Serial ***************************/
		Thread.sleep(5000);
		startTime = System.currentTimeMillis();
		displayPeople1(people, pred);
		System.out.println("Serial time:" + (System.currentTimeMillis() - startTime));
		/***************************** 2 Stream ***************************/
		Thread.sleep(5000);
		startTime = System.currentTimeMillis();
		displayPeople2(people, pred);
		System.out.println("sum time:" + people.stream().parallel().mapToInt(p -> p.getAge()).sum());
		System.out.println("average time:" + people.stream().parallel().mapToInt(p -> p.getAge()).average());
		System.out.println("Stream time:" + (System.currentTimeMillis() - startTime));
		/***************************** 2 ParallelStream *******************/
		Thread.sleep(5000);
		startTime = System.currentTimeMillis();
		displayPeople3(people, pred);
		System.out.println("Parallel time:" + (System.currentTimeMillis() - startTime));
	}

	private static void ConvertCase() {
		long startTime = 0;
		String input = "Wo Ai Ni ZhongGuo.";
		/****************************** 1 Serial *****************************/
		startTime = System.currentTimeMillis();
		String[] inputs = input.split(" ");
		for (int i = 0; i < loopTime; i++) {
			for (String string : inputs)
				// System.out.println(string.toUpperCase());
				string.toUpperCase();
		}
		System.out.println("Serial time:" + (System.currentTimeMillis() - startTime));
		/*******************************
		 * 1 Parallel
		 *****************************/
		startTime = System.currentTimeMillis();
		for (int i = 0; i < loopTime; i++) {
			upcase(input.split(" "));
		}
		System.out.println("Parallel time:" + (System.currentTimeMillis() - startTime));
	}

	private static String[] upcase(String[] input) {
		return Arrays.stream(input).map(String::toUpperCase).toArray(String[]::new);
	}

	private static void displayPeople1(List<Person> people, Predicate<Person> pred) {
		System.out.println("Selected:");
		people.forEach(p -> {
			if (pred.test(p)) {
				System.out.println("displayPeople1-" + p.getName());
			}
		});
	}

	private static void displayPeople2(List<Person> people, Predicate<Person> pred) {
		people.stream().filter(pred).forEach(p -> System.out.println("displayPeople2-" + p.getName()));
	}

	private static void displayPeople3(List<Person> people, Predicate<Person> pred) {
		people.stream().parallel().filter(pred).forEach(p -> System.out.println("displayPeople3-" + p.getName()));
	}

	public static class Person {
		private String name;
		private int age;

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

	/**
	 * 产生随机字符串
	 */
	private static Random randGen = null;
	private static Random lengthGen = new Random();
	private static char[] numbersAndLetters = null;

	public static final String nameGen() {
		int length = lengthGen.nextInt(10);
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			randGen = new Random();
			numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
					.toCharArray();
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	public static final int ageGen() {
		return lengthGen.nextInt(100);
	}
}