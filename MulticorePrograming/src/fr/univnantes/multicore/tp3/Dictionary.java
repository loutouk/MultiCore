package fr.univnantes.multicore.tp3;

/*
 * TODO: this class is not thread-safe. Make the method add linearizable and wait-free!
 */

/**
 * An implementation of a set of strings based on a dictionary. 
 * The strings of the set are kept sorted according to their lexicographic ordering and common prefixes of two strings in the set are only encoded once.
 * @author Matthieu Perrin
 */
public class Dictionary {

	/**
	 * A node of the dictionary data structure, representing one character.
	 * As a dictionary is a tree, a node can be only accessed by following one path from the root.
	 * The succession of the characters encoded by the nodes in the path leading to a node, including the node itself, forms a string, 
	 * that is considered present in the set if, and only if, the member "present" is set to true.
	 * 
	 * More formally, the path leading to a node is defined as such:
	 *   - the path leading to the first node is path(start) = "\0";
	 *   - if path(n) = s + n.character, then path(n.suffix) = s + n.character + n.suffix.character
	 *   - if path(n) = s + n.character, then path(n.next) = s + n.suffix.character
	 *   
	 * A word s is contained in the dictionary if there is a node n whose path is s
	 */
	private static class Node {

		// The character of the string encoded in this node of the dictionary
		char character;
		// True if the string leading to this node has already been inserted, false otherwise
		boolean absent = true;
		// Encodes the set of strings starting with the string leading to this word, 
		// including the character encoded by this node
		Node suffix = null;
		// Encodes the set of strings starting with the string leading to this word, 
		// excluding the character encoded by this node, 
		// and whose next character is strictly greater than the character encoded by this node
		Node next;

		Node(char character, Node next) { 
			this.character = character; 
			this.next = next; 
		}

		/**
		 * Adds the specified string to this set if it is not already present. 
		 * More formally, adds the specified string s to this set if the set contains no element s2 such that s.equals(s2). 
		 * If this set already contains the element, the call leaves the set unchanged and returns false. 
		 * @param s The string that is being inserted in the set
		 * @param depth The number of time the pointer "suffix" has been followed
		 * @return true if s was not already inserted, false otherwise
		 */
		boolean add(String s, int depth) {

			// First case: we are at the end of the string and this is the correct node
			if(depth >= s.length() || (s.charAt(depth) == character) && depth == s.length() - 1) {
				boolean result = absent;
				absent = false;
				return result;
			}

			// Second case: the next character in the string was found, but this is not the end of the string
			// We continue in member "suffix"
			if(s.charAt(depth) == character) {
				if (suffix == null || suffix.character > s.charAt(depth+1)) 
					suffix = new Node(s.charAt(depth+1), suffix);
				return suffix.add(s, depth+1);
			}

			// Third case: the next character in the string was not found
			// We continue in member "next"
			// To maintain the order, we may have to add a new node before "next" first
			if (next == null || next.character > s.charAt(depth))
				next = new Node(s.charAt(depth), next);
	
			return next.add(s, depth);
		}
	
	}

	// We start with a first node, to simplify the algorithm, that encodes the smallest non-empty string "\0".
	private Node start = new Node('\0', null);
	// The empty string is stored separately
	private boolean emptyAbsent = true;

	/**
	 * Adds the specified string to this set if it is not already present. 
	 * More formally, adds the specified string s to this set if the set contains no element s2 such that s.equals(s2). 
	 * If this set already contains the element, the call leaves the set unchanged and returns false. 
	 * @param s The string that is being inserted in the set
	 * @return true if s was not already inserted, false otherwise
	 */
	public boolean add(String s) {
		if (s != "") return start.add(s, 0);
		boolean result = emptyAbsent;
		emptyAbsent = false;
		return result;
	}
	
}