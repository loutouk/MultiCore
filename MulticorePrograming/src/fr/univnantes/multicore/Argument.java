package fr.univnantes.multicore;

import javax.swing.JOptionPane;

/**
 * A simple tool used to simplify the passing of arguments to programs executed in Eclipse 
 * @author Matthieu Perrin
 */
public class Argument {

	/**
	 * Get the argument passed as input of the program, or prompt one if it is missing
	 * @param args the array of strings obtained as input of Function main()
	 * @return either the given argument if it exists, or a value given by the user
	 */
	public static int get(String args[]) {
		return get(args, "", 0);
	}

	/**
	 * Get the argument passed as input of the program, or prompt one if it is missing
	 * @param args the array of strings obtained as input of Function main()
	 * @param message the message displayed if the argument is missing
	 * @return either the given argument if it exists, or a value given by the user
	 */
	public static int get(String args[], String message) {
		return get(args, message, 0);
	}

	/**
	 * Get the argument passed as input of the program, or prompt one if it is missing
	 * @param args the array of strings obtained as input of Function main()
	 * @param index the index of the searched for argument in the array
	 * @return either the given argument if it exists, or a value given by the user
	 */
	public static int get(String args[], String message, int index) {
		int argument;
		if(args.length > index) {
			argument = Integer.parseInt(args[index]);
		} else {
			argument = Integer.parseInt(JOptionPane.showInputDialog("Missing argument:" + message));
		}
		return argument;
	}
}
