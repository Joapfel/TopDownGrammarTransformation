package de.ws1617.pccl.transformation;

import java.util.ArrayList;
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

		for (NonTerminal nt : grammar.getNonTerminals()) {
			

			for (ArrayList<Symbol> list : grammar.getRuleForLHS(nt)) {
					
				// unit
				if (list.size() == 1) {
					
					Stack<NonTerminal> subsequentRules = new Stack<>();
					HashSet<NonTerminal> unitLhs = new HashSet<>();

					// add lhs to agenda
					subsequentRules.push(nt);

					// while agenda is not empty process it
					while (!subsequentRules.isEmpty()) {

						NonTerminal top = (NonTerminal) subsequentRules.pop();

						if (!unitLhs.contains(top)) {
							
							//add it to the set
							unitLhs.add(top);
							
							// get rhs of stack top symbol
							for (ArrayList<Symbol> rhs : grammar.getRuleForLHS(top)) {

								// if unit rule
								if (rhs.size() == 1) {
									//push it back on the stack
									//to go deeper in to the structure
									NonTerminal unit = (NonTerminal) rhs.get(0);
									subsequentRules.push(unit);
									
									//if not a unit rule
								} else {
									// add to all Nonterminals from the Hashset
									for (NonTerminal units : unitLhs) {
										transformed.addRule(units, rhs);
									}
								}

							}
							// for lexicon
							for (ArrayList<Terminal> rhs : lexicon.getRules(top)) {

								for (NonTerminal units : unitLhs) {
									lex.addRule(units, rhs);
								}

							}
						}
					}
					// add grammar if not unit
				} else {
					transformed.addRule(nt, list);
				}

			}
		}
		// add remaining lexicon rules
		for (NonTerminal nt : lexicon.getNonTerminals()) {

			for (ArrayList<Terminal> terms : lexicon.getRules(nt)) {

				lex.addRule(nt, terms);

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
			for (ArrayList<Symbol> rhs : this.grammar.getRuleForLHS(tmp)) {

				for (Symbol s : rhs) {

					// reachable Nonterminal
					NonTerminal nt = (NonTerminal) s;
					// add to the set
					reachable.add(nt);
					// add to the stack for further processing
					agenda.push(nt);

				}

			}
		}

		// Loop over all rules in the original Grammar/Lexicon and do
		// not add rules to the new Grammar/Lexicon that have a
		// Nonterminal on the left hand side which is not in the Set
		for (NonTerminal nt : this.grammar.getNonTerminals()) {

			for (ArrayList<Symbol> rhs : this.grammar.getRuleForLHS(nt)) {

				if (reachable.contains(nt)) {
					grammarReachable.addRule(nt, rhs);
				}

			}

		}

		for (NonTerminal nt : this.lexicon.getNonTerminals()) {

			for (ArrayList<Terminal> rhs : this.lexicon.getRules(nt)) {

				if (reachable.contains(nt)) {
					lexiconReachable.addRule(nt, rhs);
				}

			}

		}

		this.grammar = grammarReachable;
		this.lexicon = lexiconReachable;

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
