package fr.eni.maximot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Maximot {
	private static final Scanner SCANNER = new Scanner(System.in);
	private static final Random RANDOM = new Random();
	private static final int MAX_TRIES = 5;

	private static int numberOfPoints = 50;
	private static String selectedWord;

	private static List<String> loadWords(String filePath) {
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			return stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private static char[] prepareWord(List<String> words) {
		char[] randomWord = pickRandomWord(words);
		selectedWord = new String(randomWord);
		return shuffleWord(randomWord);
	}

	private static char[] pickRandomWord(List<String> words) {
		String randomWord = words.get(RANDOM.nextInt(words.size()));
		return randomWord.toCharArray();
	}

	private static char[] shuffleWord(char[] word) {
		for (int i = word.length - 1; i > 0; i--) {
			int index = RANDOM.nextInt(i + 1);
			char temp = word[index];
			word[index] = word[i];
			word[i] = temp;
		}
		return word;
	}

	private static void playGame(List<String> words) {
		do {
			boolean won = initializeGame(words);
			if (numberOfPoints <= 0) {
				System.out.println("Game over! You've run out of points.");
				break;
			}
			if (!won) {
				System.out.println("You didn't guess the word. Let's try another word!");
			}
		} while (askToPlayAgain() && numberOfPoints > 0);
	}

	private static boolean initializeGame(List<String> words) {
		char[] shuffledWord = prepareWord(words);

		System.out.printf("Guess the word: %s\n", new String(shuffledWord));
		displayPoints();
		boolean guessedCorrectly = false;

		for (int numberOfTries = MAX_TRIES; numberOfTries > 0 && numberOfPoints > 0; numberOfTries--) {
			String guess = SCANNER.nextLine();
			guessedCorrectly = evaluateGuess(guess, numberOfTries - 1);
			if (guessedCorrectly) {
				break;
			}

		}

		return guessedCorrectly;
	}

	private static boolean evaluateGuess(String guess, int numberOfTries) {
		if (selectedWord.equalsIgnoreCase(guess)) {
			System.out.printf("Great job! You guessed the word: %s\n", selectedWord);
			addPoints(selectedWord.length());
			return true;
		} else {
			System.out.printf("Incorrect! %d tries remaining.\n", numberOfTries);
			subtractPoints(selectedWord.length());
			return false;
		}
	}

	private static void addPoints(int points) {
		numberOfPoints += points;
		System.out.println("Points earned: " + points + ". Total points: " + numberOfPoints);
	}

	private static void subtractPoints(int points) {
		numberOfPoints -= points;
		System.out.println("Points lost: " + points + ". Total points: " + numberOfPoints);
	}

	private static void displayPoints() {
		System.out.println("Current points: " + numberOfPoints);
	}

	private static boolean askToPlayAgain() {
		while (true) {
			System.out.println("Wanna play again? (type 'yes' or 'no')");
			String userInput = SCANNER.nextLine().trim().toLowerCase();
			switch (userInput) {
			case "yes":
				return true;
			case "no":
				return false;
			default:
				System.out.println("Invalid input. Please type 'yes' or 'no'.");
			}
		}
	}

	public static void main(String[] args) {
		List<String> words = loadWords("dictionnaire.txt");
		playGame(words);
		SCANNER.close();
	}
}
