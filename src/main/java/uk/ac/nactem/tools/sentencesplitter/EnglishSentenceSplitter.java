/**
 * Sentence Splitter - Sentence splitter with output compatible with Scott Piao's version
 * Copyright © 2017 The National Centre for Text Mining (NaCTeM), University of
							Manchester (jacob.carter@manchester.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.nactem.tools.sentencesplitter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author William Black and Adam Funk
 */
public class EnglishSentenceSplitter {

	/**
	 * $1 = Possible non-word char before token starts $2 = Beginning of first
	 * sentence: At least one non-space, a punctuation character, optionally
	 * closing quotes and brackets $3 = Inter-sentence space. $4 = Possible
	 * beginning of next sentence $5 = Rest of paragraph The subsequent tests
	 * are generally on $2$3$4. The . in this regexp needs to be able to match
	 * newlines.
	 */
	private static final Pattern CANDIDATE = Pattern.compile(
			// 11111
			"(.*?)"
					// 22222222222222222222222222222222222222222222222222222
					+ "([\\S&&[^-:=+'\"\\(\\[\\{]]+[\\.!?][\"\'\\)\\]\\}>]*)"
					// 3333334444445555
					+ "(\\s+)(\\S+)(.*)",
			Pattern.DOTALL);

	/** Split after [.?!] followed by any right bracketing */
	private static final Pattern RULE0 = Pattern.compile("\\S+[\\.!?][\"\'\\)\\]\\}>]+\\s+\\S+");

	/** Split after [?!] followed by whitespace */
	private static final Pattern RULE1 = Pattern.compile("\\S+[!?]\\s+\\S+");

	/**
	 * Don't split if next nonwhite is lower-case unless it's in _lowerCaseTerms
	 */
	private static final Pattern RULE2 = Pattern.compile("\\S+\\.\\s+\\p{Ll}\\S*");

	/** Splitting is possible with eWords, e.g. eScience */
	private static final Pattern EWORDRULE = Pattern.compile("[eim]\\p{Upper}\\p{Alpha}+");

	private static final Set<String> ABBREVIATIONS = new HashSet<String>();
	static {
		// Civilian titles
		ABBREVIATIONS.add("Dr.");
		ABBREVIATIONS.add("Ph.D.");
		ABBREVIATIONS.add("Ph.");
		ABBREVIATIONS.add("Mr.");
		ABBREVIATIONS.add("Mrs.");
		ABBREVIATIONS.add("Ms.");
		ABBREVIATIONS.add("Prof.");
		ABBREVIATIONS.add("Esq.");
		// Military ranks
		ABBREVIATIONS.add("Maj.");
		ABBREVIATIONS.add("Gen.");
		ABBREVIATIONS.add("Adm.");
		ABBREVIATIONS.add("Lieut.");
		ABBREVIATIONS.add("Lt.");
		ABBREVIATIONS.add("Col.");
		ABBREVIATIONS.add("Sgt.");
		ABBREVIATIONS.add("Cpl.");
		ABBREVIATIONS.add("Pte.");
		ABBREVIATIONS.add("Cap.");
		ABBREVIATIONS.add("Capt.");
		// Political titles
		ABBREVIATIONS.add("Sen.");
		ABBREVIATIONS.add("Pres.");
		ABBREVIATIONS.add("Rep.");
		// Religious titles
		ABBREVIATIONS.add("St.");
		ABBREVIATIONS.add("Rev.");
		// Geographical and addresses
		ABBREVIATIONS.add("Mt.");
		ABBREVIATIONS.add("Rd.");
		ABBREVIATIONS.add("Cres.");
		ABBREVIATIONS.add("Ln.");
		ABBREVIATIONS.add("Ave.");
		ABBREVIATIONS.add("Av.");
		ABBREVIATIONS.add("Bd.");
		ABBREVIATIONS.add("Blvd.");
		ABBREVIATIONS.add("Co.");
		ABBREVIATIONS.add("co.");
		// Commercial
		ABBREVIATIONS.add("Ltd.");
		ABBREVIATIONS.add("Plc.");
		ABBREVIATIONS.add("PLC.");
		ABBREVIATIONS.add("Inc.");
		ABBREVIATIONS.add("Pty.");
		ABBREVIATIONS.add("Corp.");
		ABBREVIATIONS.add("Co.");
		// Academic
		ABBREVIATIONS.add("et.");
		ABBREVIATIONS.add("al.");
		ABBREVIATIONS.add("ed.");
		ABBREVIATIONS.add("eds.");
		ABBREVIATIONS.add("Ed.");
		ABBREVIATIONS.add("Eds.");
		ABBREVIATIONS.add("Fig.");
		ABBREVIATIONS.add("fig.");
		ABBREVIATIONS.add("Ref.");
		ABBREVIATIONS.add("ref.");
		// General
		ABBREVIATIONS.add("etc.");
		ABBREVIATIONS.add("usu.");
		ABBREVIATIONS.add("e.g.");
		ABBREVIATIONS.add("pp.");
		ABBREVIATIONS.add("vs.");
		ABBREVIATIONS.add("v.");
		// Measures
		ABBREVIATIONS.add("yr.");
		ABBREVIATIONS.add("yrs.");
		ABBREVIATIONS.add("g.");
		ABBREVIATIONS.add("mg.");
		ABBREVIATIONS.add("kg.");
		ABBREVIATIONS.add("gr.");
		ABBREVIATIONS.add("lb.");
		ABBREVIATIONS.add("lbs.");
		ABBREVIATIONS.add("oz.");
		ABBREVIATIONS.add("in.");
		ABBREVIATIONS.add("mi.");
		ABBREVIATIONS.add("m.");
		ABBREVIATIONS.add("M.");
		ABBREVIATIONS.add("mt.");
		ABBREVIATIONS.add("mtr.");
		ABBREVIATIONS.add("ft.");
		ABBREVIATIONS.add("max.");
		ABBREVIATIONS.add("min.");
		ABBREVIATIONS.add("Max.");
		ABBREVIATIONS.add("Min.");
		ABBREVIATIONS.add("inc.");
		ABBREVIATIONS.add("exc.");
		// Single letter abbreviations
		ABBREVIATIONS.add("A.");
		ABBREVIATIONS.add("B.");
		ABBREVIATIONS.add("C.");
		ABBREVIATIONS.add("D.");
		ABBREVIATIONS.add("E.");
		ABBREVIATIONS.add("F.");
		ABBREVIATIONS.add("G.");
		ABBREVIATIONS.add("H.");
		ABBREVIATIONS.add("I.");
		ABBREVIATIONS.add("J.");
		ABBREVIATIONS.add("K.");
		ABBREVIATIONS.add("L.");
		ABBREVIATIONS.add("M.");
		ABBREVIATIONS.add("N.");
		ABBREVIATIONS.add("O.");
		ABBREVIATIONS.add("P.");
		ABBREVIATIONS.add("Q.");
		ABBREVIATIONS.add("R.");
		ABBREVIATIONS.add("S.");
		ABBREVIATIONS.add("T.");
		ABBREVIATIONS.add("U.");
		ABBREVIATIONS.add("V.");
		ABBREVIATIONS.add("W.");
		ABBREVIATIONS.add("X.");
		ABBREVIATIONS.add("Y.");
		ABBREVIATIONS.add("Z.");
		ABBREVIATIONS.add("a.");
		ABBREVIATIONS.add("b.");
		ABBREVIATIONS.add("c.");
		ABBREVIATIONS.add("d.");
		ABBREVIATIONS.add("e.");
		ABBREVIATIONS.add("f.");
		ABBREVIATIONS.add("g.");
		ABBREVIATIONS.add("h.");
		ABBREVIATIONS.add("i.");
		ABBREVIATIONS.add("j.");
		ABBREVIATIONS.add("k.");
		ABBREVIATIONS.add("l.");
		ABBREVIATIONS.add("m.");
		ABBREVIATIONS.add("n.");
		ABBREVIATIONS.add("o.");
		ABBREVIATIONS.add("p.");
		ABBREVIATIONS.add("q.");
		ABBREVIATIONS.add("r.");
		ABBREVIATIONS.add("s.");
		ABBREVIATIONS.add("t.");
		ABBREVIATIONS.add("u.");
		ABBREVIATIONS.add("v.");
		ABBREVIATIONS.add("w.");
		ABBREVIATIONS.add("x.");
		ABBREVIATIONS.add("y.");
		ABBREVIATIONS.add("z.");
		// Temporal
		ABBREVIATIONS.add("Jan.");
		ABBREVIATIONS.add("Feb.");
		ABBREVIATIONS.add("Mar.");
		ABBREVIATIONS.add("Apr.");
		ABBREVIATIONS.add("Jun.");
		ABBREVIATIONS.add("Jul.");
		ABBREVIATIONS.add("Aug.");
		ABBREVIATIONS.add("Sep.");
		ABBREVIATIONS.add("Sept.");
		ABBREVIATIONS.add("Oct.");
		ABBREVIATIONS.add("Nov.");
		ABBREVIATIONS.add("Dec.");
		ABBREVIATIONS.add("Mon.");
		ABBREVIATIONS.add("Tue.");
		ABBREVIATIONS.add("Wed.");
		ABBREVIATIONS.add("Thu.");
		ABBREVIATIONS.add("Fri.");
		ABBREVIATIONS.add("Sat.");
		ABBREVIATIONS.add("Sun.");
	}

	private static final Set<String> LOWERCASETERMS = new HashSet<String>();
	static {
		LOWERCASETERMS.add("mRNA");
		LOWERCASETERMS.add("tRNA");
		LOWERCASETERMS.add("cDNA");
		LOWERCASETERMS.add("iPad");
		LOWERCASETERMS.add("iPod");
		LOWERCASETERMS.add("iPhone");
		LOWERCASETERMS.add("iCloud");
		LOWERCASETERMS.add("iMac");
		LOWERCASETERMS.add("eCommerce");
		LOWERCASETERMS.add("eBusiness");
		LOWERCASETERMS.add("mCommerce");
		LOWERCASETERMS.add("alpha");
		LOWERCASETERMS.add("beta");
		LOWERCASETERMS.add("gamma");
		LOWERCASETERMS.add("delta");
		LOWERCASETERMS.add("c");
		LOWERCASETERMS.add("i");
		LOWERCASETERMS.add("ii");
		LOWERCASETERMS.add("iii");
		LOWERCASETERMS.add("iv");
		LOWERCASETERMS.add("v");
		LOWERCASETERMS.add("vi");
		LOWERCASETERMS.add("vii");
		LOWERCASETERMS.add("viii");
		LOWERCASETERMS.add("ix");
		LOWERCASETERMS.add("x");
	}

  public void addAbbreviation(String abbrev) {
    ABBREVIATIONS.add(abbrev);
  }

  public void addLowerCaseTerm(String term) {
    ABBREVIATIONS.add(term);
  }

	/**
	 * To give this a compatible interface to Piao's SentParDetector
	 */
	public ArrayList<int[]> markupRawText(String input) {
		List<String> sentenceList = this.splitParagraph(input);
		ArrayList<int[]> toReturn = new ArrayList<int[]>();
		int begin = 0, end = 0;
		int sentCount = 0;
		int parCount = 0;
		boolean newPar = true;
		for (int i = 0; i < sentenceList.size(); i++) {
			String sent = sentenceList.get(i);
			if (sent.equals("")) {
				newPar = true;
				parCount = (i == 0 ? 0 : parCount++);
			} else {
				begin = this.getStartOfSentRobustly(sent, input, end);
				end = this.getEndOfSentRobustly(sent, input, begin);
				if (newPar) {
					newPar = false;
				}
				int[] sentData = { parCount, sentCount, begin, end };
				toReturn.add(sentData);
				sentCount++;
			}
		}
		return toReturn;
	}

	private int getEndOfSentRobustly(String sent, String wholeDoc, int begin) {
		// align with source document
		String[] wordsOfSent = sent.split(" ");
		int start = begin - 1, end = start;
		for (int i = 0; i < wordsOfSent.length; i++) {
			String thisTok = wordsOfSent[i];
			int startOfTok = wholeDoc.indexOf(thisTok, end);
			end = startOfTok > -1 ? startOfTok + thisTok.length() : end + thisTok.length();
		}
		return end;
	}

	private int getStartOfSentRobustly(String sent, String wholeDoc, int lastend) {
		// align with source document
		String[] wordsOfSent = sent.split(" ");
		int begin = wholeDoc.indexOf(wordsOfSent[0], lastend);
		return begin == -1 ? lastend + 1 : begin;
	}

	public List<String> splitParagraph(String paragraph) {
		List<String> result = new ArrayList<String>();
		String remainder = new String(paragraph); /* copy to mess with */
		StringBuffer accumulator = new StringBuffer("");
		Matcher m;
		String test;

		while (remainder.length() > 0) {
			m = CANDIDATE.matcher(remainder);

			if (m.matches()) {
				accumulator.append(m.group(1));
				accumulator.append(m.group(2));
				test = m.group(2) + m.group(3) + m.group(4);
				remainder = m.group(3) + m.group(4) + m.group(5);

				/* Split if $4 is a lower-case term (e.g. "mRNA") */
				/* Split if _rule0 */
				/* Split if _rule1 */
				if (LOWERCASETERMS.contains(m.group(4)) || RULE0.matcher(test).matches()
						|| RULE1.matcher(test).matches() || EWORDRULE.matcher(m.group(4)).matches()) {
					result.add(accumulator.toString());
					accumulator.setLength(0);
				}

				/* Don't split if _rule2 */
				/* Don't split if $2 is in _abbreviations */
				/* Otherwise split */
				else if ((!RULE2.matcher(test).matches()) && (!ABBREVIATIONS.contains(m.group(2).toLowerCase()))
						&& (!ABBREVIATIONS.contains(m.group(2)))) {
					result.add(accumulator.toString().trim());
					accumulator.setLength(0);
				}

			}

			else { /* Out of stops: finish off. */
				accumulator.append(remainder);
				break;
			}
		}

		/* Flush the accumulator */
		if (accumulator.length() > 0) {
			result.add(accumulator.toString().trim());
		}

		return result;
	}

	public static void main(String[] argv) throws Exception {
		String TEST_PAR = "Wot about Fig. 2 and (Fig. 3)? We created a myosinII-responsive FA interactome from proteins in the expected FA list by color-coding proteins according to MDR magnitude (Supplemental Fig. S4 and Table 7, http://dir.nhlbi.nih.gov/papers/lctm/focaladhesion/Home/index.html). The interactome illustrates the full range of MDR values, including proteins exhibiting minor/low confidence changes. This interactome suggests how myosinII activity may collectively modulate FA abundance of groups of proteins mediating distinct pathways.";
		TEST_PAR = "The development coincided with a warning issued in London by the Bosnian Foreign Minister, Irfan Ljubijankic, that the region was \"dangerously close to a resumption of all-out war.\" He added, \"At the moment we have a diplomatic vacuum.\"\nIn the latest of a series of inconclusive Western moves to avert a renewed Balkan flareup, the American envoy, Assistant Secretary of State Richard C. Holbrooke, met with President Franjo Tudjman at the Presidential Palace in the hills above Zagreb tonight. But the meeting lasted less than 40 minutes and Mr. Holbrooke refused to answer reporters' questions when he left.";
		EnglishSentenceSplitter splitter = new EnglishSentenceSplitter();
		ArrayList<int[]> output = splitter.markupRawText(TEST_PAR);
		for (int[] o : output) {
			System.out.println(o[2] + "--" + o[3] + ": " + TEST_PAR.substring(o[2], o[3]));
		}
	}
}
