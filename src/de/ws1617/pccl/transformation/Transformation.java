package de.ws1617.pccl.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import de.ws1617.pccl.grammar.*;

public class Transformation {

	Grammar grammar;
	Lexicon lexicon;

	public Transformation(Grammar grammar, Lexicon lexicon) {

		this.grammar = grammar;
		this.lexicon = lexicon;
	}

	// methods
	/**
	 * 
	 */
	public void rmUnitRules() {

		Grammar transformed = new Grammar();
		Lexicon lex = new Lexicon();

		for (NonTerminal nt : this.grammar.getNonTerminals()) {

			for (ArrayList<Symbol> list : this.grammar.getRuleForLHS(nt)) {

				// unit
				if (list.size() == 1) {

					Stack<ArrayList<NonTerminal>> subsequentRules = new Stack<>();
					Stack<NonTerminal> visited = new Stack<>();
					// HashSet<NonTerminal> unitLhs = new HashSet<>();

					// add lhs to agenda
					ArrayList<NonTerminal> initial = new ArrayList<>();
					initial.add(nt);
					subsequentRules.push(initial);

					// while agenda is not empty process it
					while (!subsequentRules.isEmpty()) {

						// get the first element
						NonTerminal top = subsequentRules.peek().get(0);

						if (!visited.contains(top)) {

							// add it to the set
							visited.push(top);

							ArrayList<NonTerminal> tmp = new ArrayList<>();
							subsequentRules.push(tmp);

							// get rhs of stack top symbol
							for (ArrayList<Symbol> rhs : this.grammar.getRuleForLHS(top)) {

								// if not a unit rule
								if (rhs.size() > 1) {

									// add to all Nonterminals from the Hashset
									// as Rules
									for (NonTerminal units : visited) {
										transformed.addRule(units, rhs);
									}

									// if unit rule or empty
								} else if (rhs.size() == 1) {

									NonTerminal unit = (NonTerminal) rhs.get(0);
									if (!visited.contains(unit)) {
										subsequentRules.peek().add(unit);
									}

								}

							}
							// for lexicon
							for (ArrayList<Terminal> rhs : this.lexicon.getRules(top)) {

								for (NonTerminal units : visited) {
									lex.addRule(units, rhs);
								}

							}

							// if the tmp ArrayList is empty it means there were
							// no subsequent rules
							if (subsequentRules.peek().isEmpty()) {
								subsequentRules.pop();
							}
							// if the top non terminal is already in the visited
							// stack
						} else {

							// if there is only one element which is already in
							// the
							// visited stack than it is already processed as
							// well and we can
							// pop it
							if (subsequentRules.peek().size() == 1) {
								subsequentRules.pop();
							} else if (subsequentRules.peek().size() > 1) {
								subsequentRules.peek().remove(0);
							}

							// In either case, pop the top element of the
							// visited stack
							// There are no right hand sides left to which this
							// NonTerminal could expand
							visited.pop();
						}
					}
					// add grammar if not unit
				} else {
					transformed.addRule(nt, list);
				}

			}
		}
		// add remaining lexicon rules
		for (NonTerminal lhs : this.lexicon.getNonTerminals()) {

			for (ArrayList<Terminal> rhs : lexicon.getRules(lhs)) {

				lex.addRule(lhs, rhs);

			}

		}

		this.grammar = transformed;
		this.lexicon = lex;

	}

	/**
	 * 
	 * @param startSymbol
	 */
	public void rmUnreachables(NonTerminal startSymbol) {

		Grammar grammarReachable = new Grammar();
		Lexicon lexiconReachable = new Lexicon();

		// store all Nonterminals we come allong
		HashSet<NonTerminal> reachable = new HashSet<>();
		// stack to process the nonterminals starting with SIGMA
		Stack<NonTerminal> agenda = new Stack<>();
		agenda.push(startSymbol);

		// process the stack
		while (!agenda.isEmpty()) {

			NonTerminal tmp = agenda.pop();

			if (!reachable.contains(tmp)) {
				
				// add to the set
				reachable.add(tmp);

				for (ArrayList<Symbol> rhs : this.grammar.getRuleForLHS(tmp)) {

					for (Symbol s : rhs) {

						// reachable Nonterminal
						NonTerminal nt = (NonTerminal) s;
						
						// add to the stack for further processing
						agenda.push(nt);

					}

				}
			}
		}

		// Loop over all rules in the original Grammar/Lexicon and do
		// not add rules to the new Grammar/Lexicon that have a
		// Nonterminal on the left hand side which is not in the Set
		for (NonTerminal nt : this.grammar.getNonTerminals()) {

			if (reachable.contains(nt)) {

				for (ArrayList<Symbol> rhs : this.grammar.getRuleForLHS(nt)) {

					grammarReachable.addRule(nt, rhs);
				}

			}

		}

		for (NonTerminal nt : this.lexicon.getNonTerminals()) {
			
			if (reachable.contains(nt)) {
			
				for (ArrayList<Terminal> rhs : this.lexicon.getRules(nt)) {

					lexiconReachable.addRule(nt, rhs);
				}

			}

		}

		this.grammar = grammarReachable;
		this.lexicon = lexiconReachable;

	}
	
	/**
	 * Sorts Grammar Rules by the length of their right hand sides
	 * @return  HashMap<NonTerminal, ArrayList<HashSet<ArrayList<Symbol>>>> containing the sorted rules
	 */
	public HashMap<NonTerminal, ArrayList<HashSet<ArrayList<Symbol>>>> sortRulesByRightHandSide(){

		HashMap<NonTerminal,ArrayList<HashSet<ArrayList<Symbol>>>> sortedRules = new HashMap<>();

		//Determine the max size of a rightHandSide for a NonTerminal n 
		for(NonTerminal n : this.grammar.getNonTerminals()){

			HashSet<ArrayList<Symbol>> rightHandSides = this.grammar.getRuleForLHS(n);
			
			int largestRightHandSide = 0;
			for(ArrayList<Symbol> rightHandSide : rightHandSides){
				
				int rightHandSize = rightHandSide.size();
				
				if(largestRightHandSide > rightHandSize){
					largestRightHandSide = rightHandSize;
				}
				
			}
			
			//Create the ArrayList of that size
			ArrayList<HashSet<ArrayList<Symbol>>> sortedRightHandSides = new ArrayList<>(largestRightHandSide + 1);

			//Fill the Array with the rules 
			for(ArrayList<Symbol> rightHandSide : rightHandSides){

				int rightHandSideSize = rightHandSide.size();

				if(sortedRightHandSides.get(rightHandSideSize) == null){

					sortedRightHandSides.add(rightHandSideSize, new HashSet<ArrayList<Symbol>>());

					sortedRightHandSides.get(rightHandSideSize).add(rightHandSide);
				}
				else{
					sortedRightHandSides.get(rightHandSideSize).add(rightHandSide);
				}
			}

			//Store the sorted rightHandSides under the NonTerminal in question
			sortedRules.put(n, sortedRightHandSides);
		}

		return sortedRules;
	}

	// getters setters.....
	public Grammar getGrammar() {
		return grammar;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public Lexicon getLexicon() {
		return lexicon;
	}

	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}

	@Override
	public String toString() {
		return "Transformation [grammar=" + grammar + ", lexicon=" + lexicon + "]";
	}

}
