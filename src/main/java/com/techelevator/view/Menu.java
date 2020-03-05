package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

	private PrintWriter out;
	private Scanner in;
	private boolean includeQuit;
	
	private static final String QUIT_OPTION = "Q) Quit";

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		return getChoiceFromOptions(options, false);
	}
	
	public Object getChoiceFromOptions(Object[] options, boolean includeQuit) {
		this.includeQuit = includeQuit;
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		if (choice == QUIT_OPTION) {
			choice = null;
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		if (userInput.toUpperCase().equals("Q")) {
			choice = QUIT_OPTION;
		} else {
			try {
				int selectedOption = Integer.valueOf(userInput);
				if (selectedOption > 0 && selectedOption <= options.length) {
					choice = options[selectedOption - 1];
				}
			} catch (NumberFormatException e) {
				// eat the exception, an error message will be displayed below since choice will
				// be null
			}
			if (choice == null) {
				out.println("\n*** " + userInput + " is not a valid option ***\n");
				out.flush();
			}
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		if (includeQuit) {
			out.println(QUIT_OPTION);
		} 
	
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
}
