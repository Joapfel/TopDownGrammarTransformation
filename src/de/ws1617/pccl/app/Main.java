package de.ws1617.pccl.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.plaf.TreeUI;

import de.ws1617.pccl.grammar.*;
import de.ws1617.pccl.parser.TopDownParser;
import de.ws1617.pccl.search.BFS_TopDown;
import de.ws1617.pccl.transformation.Transformation;
import de.ws1617.pccl.tree.TreeUtils;

public class Main {

	public static void main(String[] args) {

		try {

			// argument check
			if (args.length < 4) {
				System.err.println("required arguments: grammar-file lexicon-file start-symbol input-sentence");
				return;
			}

			Grammar grammar = GrammarUtils.readGrammar(args[0]);
			Lexicon lexicon = GrammarUtils.readLexicon(args[1]);
			NonTerminal startSymbol = new NonTerminal(args[2]);
			String input = args[3];
			

			// split by whitespace
			String[] splt = input.split("\\s+");
			// convert to terminals
			Terminal[] terminals = new Terminal[splt.length];
			// add the split strings to the terminal array
			for (int i = 0; i < splt.length; i++) {

				Terminal tmp = new Terminal(splt[i]);
				terminals[i] = tmp;

			}
			
			Transformation tr = new Transformation(grammar, lexicon);
			tr.rmUnitRules();
			tr.rmUnreachables(startSymbol);
			
			Grammar grTransformed = tr.getGrammar();
			Lexicon lexTransformed = tr.getLexicon();
			
			BFS_TopDown search = new BFS_TopDown();
			HashSet<TopDownParser> goalConfigurations = search.search(terminals, grTransformed, lexTransformed, startSymbol);
			//int filenumber = 0;

			for (TopDownParser tD : goalConfigurations) {

				System.out.println(tD.toString());
			}

			// TODO implement me !

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
