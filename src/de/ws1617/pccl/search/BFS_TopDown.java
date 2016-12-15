package de.ws1617.pccl.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import de.ws1617.pccl.grammar.*;
import de.ws1617.pccl.parser.*;

public class BFS_TopDown {

	/**
	 * 
	 * 
	 * @param input
	 * @param grammar
	 * @param lexicon
	 * @param startSymbol
	 * @return
	 */
	public HashSet<TopDownParser> search(Terminal[] input, Grammar grammar, Lexicon lexicon, NonTerminal startSymbol) {
		Queue<TopDownParser> agenda = new LinkedList<>();
		// initialize with start symbol
		HashSet<TopDownParser> results = new HashSet<>();
		HashSet<TopDownParser> init = initialize(grammar, lexicon, startSymbol, input);
		for (TopDownParser initial : init) {
			agenda.add(initial);
		}

		// do actual search
		while (!agenda.isEmpty()) {
			TopDownParser top = agenda.peek();
			if (top.isGoal()) {
				results.add(top);
			} else {
				for (TopDownParser succ : top.successors()) {
					if (succ.isGoal()) {
						results.add(succ);
					} else {
						agenda.add(succ);
					}
				}
			}
			agenda.poll();
		}

		return results;
	}

	/**
	 * Expands the start symbol for initializing the top-down parser.
	 * 
	 * @param grammar
	 * @param lexicon
	 * @param startSymbol
	 * @param input
	 * @return
	 */
	private HashSet<TopDownParser> initialize(Grammar grammar, Lexicon lexicon, NonTerminal startSymbol, Terminal[] input) {
		HashSet<TopDownParser> results = new HashSet<>();

		Stack<Symbol> predictionStack = new Stack<>();
		predictionStack.push(startSymbol);
		// create initial TopDownParser
		TopDownParser init = new TopDownParser(predictionStack, input, grammar, lexicon);

		// add initial TopDownParser to HashSet
		results.add(init);
		return results;
	}

}
