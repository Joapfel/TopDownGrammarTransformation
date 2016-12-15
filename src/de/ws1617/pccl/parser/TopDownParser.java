package de.ws1617.pccl.parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

import org.omg.PortableInterceptor.NON_EXISTENT;

import de.ws1617.pccl.grammar.*;
import de.ws1617.pccl.search.*;

public class TopDownParser {

	// the current parser prediction stack rule RHSs are added in reverse order
	private Stack<Symbol> predictions;

	// the rules applied so far
	private Stack<Rule> analysis;

	// the input to parse
	private Terminal[] input;

	// the current position in the input
	private int inputIndex;

	// the grammar for parsing
	private Grammar grammar;

	// the lexicon for parsing
	private Lexicon lexicon;

	// constructor
	public TopDownParser(Stack<Symbol> predictions, Terminal[] input, Grammar grammar, Lexicon lexicon) {
		super();
		this.predictions = predictions;
		this.input = input;
		this.grammar = grammar;
		this.lexicon = lexicon;
		this.inputIndex = 0;
		analysis = new Stack<>();
	}

	// constructor for cloning
	public TopDownParser() {

	}

	// methods

	/**
	 * check whether it is possible to generate successors (is there input to
	 * process, is the prediction stack not empty); add a check to avoid left
	 * recursion (slide 47)
	 * 
	 * @return
	 */
	public ArrayList<TopDownParser> successors() {

		ArrayList<TopDownParser> successors = new ArrayList<>();

		if (input[inputIndex] != null && (!predictions.isEmpty())
				&& !(predictions.size() > input.length - inputIndex)) {

			if (predictions.peek() instanceof NonTerminal) {

				// add all predicted successors
				successors.addAll(predict());

			} else if (predictions.peek() instanceof Terminal) {

				if (match()) {

					this.inputIndex++;
					this.predictions.pop();
					successors.add(this);

				}

			}

		}

		return successors;
	}

	/**
	 * try to pop the top symbol (terminal) from the prediction stack and match
	 * with next input word
	 * 
	 * @return
	 */
	public boolean match() {

		Terminal comp = (Terminal) predictions.peek();
		return inputIndex < input.length && comp.equals(input[inputIndex]);
	}

	/**
	 * try to replace the top NonTerminal on the prediction stack with each of
	 * the rule right hand sides in the lexicon and grammar; record the action
	 * on the analysis stack
	 * 
	 * @return
	 */
	public ArrayList<TopDownParser> predict() {

		// return value
		ArrayList<TopDownParser> predict = new ArrayList<>();

		// look at the first element on the stack
		// pop it only in the clone configuration for having better overview
		NonTerminal top = (NonTerminal) predictions.peek();

		// for all rules in the grammar
		for (ArrayList<Symbol> tmp : grammar.getRuleForLHS(top)) {
			if (!(Grammar.isUnitRecursive(top, tmp))) {

				TopDownParser clone = clone();

				clone.predictions.pop();

				// for all NonTerminals
				for (int i = tmp.size() - 1; i >= 0; i--) {
					// reverse
					clone.predictions.push(tmp.get(i));
				}
				// add rule to analysis stack
				clone.analysis.push(new Rule(top, tmp));
				// add clone to the list
				predict.add(clone);
			}

		}

		// for all rules in the lexicon
		for (ArrayList<Terminal> tmp : lexicon.getRules(top)) {

			TopDownParser clone = clone();

			clone.predictions.pop();

			// since the lexicon is not allowed to have more than one symbol on
			// rhs
			clone.predictions.push(tmp.get(0));
			// add rule to the analysis stack
			ArrayList<Symbol> listForRule = new ArrayList<>(tmp.size());// dont
																		// need
																		// to
																		// resize
			listForRule.addAll(tmp);
			clone.analysis.push(new Rule(top, listForRule));
			// add clone to the list
			predict.add(clone);

		}

		// return all the clones
		return predict;
	}

	/**
	 * checks if the parser fully processed all input
	 * 
	 * @return
	 */
	public boolean isGoal() {
		return predictions.isEmpty() && input.length == inputIndex;
	}

	/**
	 * returns a lone of the current configuration grammar, lexicon and input
	 * can be passed over
	 * 
	 * @return
	 */
	public TopDownParser clone() {

		TopDownParser clone = new TopDownParser();
		// set all the values
		clone.setInputIndex(this.inputIndex);
		clone.setInput(this.input);
		clone.setGrammar(this.grammar);
		clone.setLexicon(this.lexicon);

		Stack<Symbol> clonePrediction = new Stack<>();
		clonePrediction.addAll(this.predictions);
		clone.setPredictions(clonePrediction);

		Stack<Rule> cloneAnalysis = new Stack<>();
		cloneAnalysis.addAll(this.analysis);
		clone.setAnalysis(cloneAnalysis);

		return clone;
	}

	// getters and setters

	public Stack<Symbol> getPredictions() {
		return predictions;
	}

	public void setPredictions(Stack<Symbol> predictions) {
		this.predictions = predictions;
	}

	public Stack<Rule> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Stack<Rule> analysis) {
		this.analysis = analysis;
	}

	public Terminal[] getInput() {
		return input;
	}

	public void setInput(Terminal[] input) {
		this.input = input;
	}

	public int getInputIndex() {
		return inputIndex;
	}

	public void setInputIndex(int inputIndex) {
		this.inputIndex = inputIndex;
	}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((analysis == null) ? 0 : analysis.hashCode());
		result = prime * result + Arrays.hashCode(input);
		result = prime * result + inputIndex;
		result = prime * result + ((predictions == null) ? 0 : predictions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopDownParser other = (TopDownParser) obj;
		if (analysis == null) {
			if (other.analysis != null)
				return false;
		} else if (!analysis.equals(other.analysis))
			return false;
		if (!Arrays.equals(input, other.input))
			return false;
		if (inputIndex != other.inputIndex)
			return false;
		if (predictions == null) {
			if (other.predictions != null)
				return false;
		} else if (!predictions.equals(other.predictions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TopDownParser [predictions=" + predictions + ", analysis=" + analysis + ", input="
				+ Arrays.toString(input) + ", inputIndex=" + inputIndex + "]";
	}

}
