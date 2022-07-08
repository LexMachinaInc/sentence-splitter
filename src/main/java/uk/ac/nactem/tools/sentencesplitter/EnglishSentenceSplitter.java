/*
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
import java.util.Collections;
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
          // 222222222222222222222222222222222222222222222222222222
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

  public static final Set<String> DEFAULT_ABBREVIATIONS;
  static {
    Set<String> defaultAbbreviations = new HashSet<>();
    // Civilian titles
    defaultAbbreviations.add("Dr.");
    defaultAbbreviations.add("Ph.D.");
    defaultAbbreviations.add("Ph.");
    defaultAbbreviations.add("Mr.");
    defaultAbbreviations.add("Mrs.");
    defaultAbbreviations.add("Ms.");
    defaultAbbreviations.add("Prof.");
    defaultAbbreviations.add("Esq.");
    // Military ranks
    defaultAbbreviations.add("Maj.");
    defaultAbbreviations.add("Gen.");
    defaultAbbreviations.add("Adm.");
    defaultAbbreviations.add("Lieut.");
    defaultAbbreviations.add("Lt.");
    defaultAbbreviations.add("Col.");
    defaultAbbreviations.add("Sgt.");
    defaultAbbreviations.add("Cpl.");
    defaultAbbreviations.add("Pte.");
    defaultAbbreviations.add("Cap.");
    defaultAbbreviations.add("Capt.");
    // Political titles
    defaultAbbreviations.add("Sen.");
    defaultAbbreviations.add("Pres.");
    defaultAbbreviations.add("Rep.");
    // Religious titles
    defaultAbbreviations.add("St.");
    defaultAbbreviations.add("Rev.");
    // Geographical and addresses
    defaultAbbreviations.add("Mt.");
    defaultAbbreviations.add("Rd.");
    defaultAbbreviations.add("Cres.");
    defaultAbbreviations.add("Ln.");
    defaultAbbreviations.add("Ave.");
    defaultAbbreviations.add("Av.");
    defaultAbbreviations.add("Bd.");
    defaultAbbreviations.add("Blvd.");
    defaultAbbreviations.add("Co.");
    defaultAbbreviations.add("co.");
    // Commercial
    defaultAbbreviations.add("Ltd.");
    defaultAbbreviations.add("Plc.");
    defaultAbbreviations.add("PLC.");
    defaultAbbreviations.add("Inc.");
    defaultAbbreviations.add("Pty.");
    defaultAbbreviations.add("Corp.");
    defaultAbbreviations.add("Co.");
    // Academic
    defaultAbbreviations.add("et.");
    defaultAbbreviations.add("al.");
    defaultAbbreviations.add("ed.");
    defaultAbbreviations.add("eds.");
    defaultAbbreviations.add("Ed.");
    defaultAbbreviations.add("Eds.");
    defaultAbbreviations.add("Fig.");
    defaultAbbreviations.add("fig.");
    defaultAbbreviations.add("Ref.");
    defaultAbbreviations.add("ref.");
    // General
    defaultAbbreviations.add("etc.");
    defaultAbbreviations.add("usu.");
    defaultAbbreviations.add("e.g.");
    defaultAbbreviations.add("pp.");
    defaultAbbreviations.add("vs.");
    defaultAbbreviations.add("v.");
    // Measures
    defaultAbbreviations.add("yr.");
    defaultAbbreviations.add("yrs.");
    defaultAbbreviations.add("g.");
    defaultAbbreviations.add("mg.");
    defaultAbbreviations.add("kg.");
    defaultAbbreviations.add("gr.");
    defaultAbbreviations.add("lb.");
    defaultAbbreviations.add("lbs.");
    defaultAbbreviations.add("oz.");
    defaultAbbreviations.add("in.");
    defaultAbbreviations.add("mi.");
    defaultAbbreviations.add("m.");
    defaultAbbreviations.add("M.");
    defaultAbbreviations.add("mt.");
    defaultAbbreviations.add("mtr.");
    defaultAbbreviations.add("ft.");
    defaultAbbreviations.add("max.");
    defaultAbbreviations.add("min.");
    defaultAbbreviations.add("Max.");
    defaultAbbreviations.add("Min.");
    defaultAbbreviations.add("inc.");
    defaultAbbreviations.add("exc.");
    // Single letter abbreviations
    defaultAbbreviations.add("A.");
    defaultAbbreviations.add("B.");
    defaultAbbreviations.add("C.");
    defaultAbbreviations.add("D.");
    defaultAbbreviations.add("E.");
    defaultAbbreviations.add("F.");
    defaultAbbreviations.add("G.");
    defaultAbbreviations.add("H.");
    defaultAbbreviations.add("I.");
    defaultAbbreviations.add("J.");
    defaultAbbreviations.add("K.");
    defaultAbbreviations.add("L.");
    defaultAbbreviations.add("M.");
    defaultAbbreviations.add("N.");
    defaultAbbreviations.add("O.");
    defaultAbbreviations.add("P.");
    defaultAbbreviations.add("Q.");
    defaultAbbreviations.add("R.");
    defaultAbbreviations.add("S.");
    defaultAbbreviations.add("T.");
    defaultAbbreviations.add("U.");
    defaultAbbreviations.add("V.");
    defaultAbbreviations.add("W.");
    defaultAbbreviations.add("X.");
    defaultAbbreviations.add("Y.");
    defaultAbbreviations.add("Z.");
    defaultAbbreviations.add("a.");
    defaultAbbreviations.add("b.");
    defaultAbbreviations.add("c.");
    defaultAbbreviations.add("d.");
    defaultAbbreviations.add("e.");
    defaultAbbreviations.add("f.");
    defaultAbbreviations.add("g.");
    defaultAbbreviations.add("h.");
    defaultAbbreviations.add("i.");
    defaultAbbreviations.add("j.");
    defaultAbbreviations.add("k.");
    defaultAbbreviations.add("l.");
    defaultAbbreviations.add("m.");
    defaultAbbreviations.add("n.");
    defaultAbbreviations.add("o.");
    defaultAbbreviations.add("p.");
    defaultAbbreviations.add("q.");
    defaultAbbreviations.add("r.");
    defaultAbbreviations.add("s.");
    defaultAbbreviations.add("t.");
    defaultAbbreviations.add("u.");
    defaultAbbreviations.add("v.");
    defaultAbbreviations.add("w.");
    defaultAbbreviations.add("x.");
    defaultAbbreviations.add("y.");
    defaultAbbreviations.add("z.");
    // Temporal
    defaultAbbreviations.add("Jan.");
    defaultAbbreviations.add("Feb.");
    defaultAbbreviations.add("Mar.");
    defaultAbbreviations.add("Apr.");
    defaultAbbreviations.add("Jun.");
    defaultAbbreviations.add("Jul.");
    defaultAbbreviations.add("Aug.");
    defaultAbbreviations.add("Sep.");
    defaultAbbreviations.add("Sept.");
    defaultAbbreviations.add("Oct.");
    defaultAbbreviations.add("Nov.");
    defaultAbbreviations.add("Dec.");
    defaultAbbreviations.add("Mon.");
    defaultAbbreviations.add("Tue.");
    defaultAbbreviations.add("Wed.");
    defaultAbbreviations.add("Thu.");
    defaultAbbreviations.add("Fri.");
    defaultAbbreviations.add("Sat.");
    defaultAbbreviations.add("Sun.");

    DEFAULT_ABBREVIATIONS = Collections.unmodifiableSet(defaultAbbreviations);
  }

  public static final Set<String> DEFAULT_LOWER_CASE_TERMS;
  static {
    Set<String> defaultLowerCaseTerms = new HashSet<>();

    defaultLowerCaseTerms.add("mRNA");
    defaultLowerCaseTerms.add("tRNA");
    defaultLowerCaseTerms.add("cDNA");
    defaultLowerCaseTerms.add("iPad");
    defaultLowerCaseTerms.add("iPod");
    defaultLowerCaseTerms.add("iPhone");
    defaultLowerCaseTerms.add("iCloud");
    defaultLowerCaseTerms.add("iMac");
    defaultLowerCaseTerms.add("eCommerce");
    defaultLowerCaseTerms.add("eBusiness");
    defaultLowerCaseTerms.add("mCommerce");
    defaultLowerCaseTerms.add("alpha");
    defaultLowerCaseTerms.add("beta");
    defaultLowerCaseTerms.add("gamma");
    defaultLowerCaseTerms.add("delta");
    defaultLowerCaseTerms.add("c");
    defaultLowerCaseTerms.add("i");
    defaultLowerCaseTerms.add("ii");
    defaultLowerCaseTerms.add("iii");
    defaultLowerCaseTerms.add("iv");
    defaultLowerCaseTerms.add("v");
    defaultLowerCaseTerms.add("vi");
    defaultLowerCaseTerms.add("vii");
    defaultLowerCaseTerms.add("viii");
    defaultLowerCaseTerms.add("ix");
    defaultLowerCaseTerms.add("x");

    DEFAULT_LOWER_CASE_TERMS = Collections.unmodifiableSet(defaultLowerCaseTerms);
  }

  private final Set<String> abbreviations;
  private final Set<String> lowerCaseTerms;

  public EnglishSentenceSplitter() {
    this(DEFAULT_ABBREVIATIONS, DEFAULT_LOWER_CASE_TERMS);
  }

  public EnglishSentenceSplitter(Set<String> abbreviations, Set<String> lowerCaseTerms) {
    this.abbreviations = abbreviations;
    this.lowerCaseTerms = lowerCaseTerms;
  }

  /**
   * To give this a compatible interface to Piao's SentParDetector
   */
  public List<int[]> markupRawText(String input) {
    List<String> sentenceList = this.splitParagraph(input);
    ArrayList<int[]> toReturn = new ArrayList<>();
    int begin, end = 0;
    int sentCount = 0;
    int parCount = 0;
    boolean newPar = true;
    for (int i = 0; i < sentenceList.size(); i++) {
      String sent = sentenceList.get(i);
      if (sent.equals("")) {
        newPar = true;
        parCount = (i == 0 ? 0 : parCount + 1);
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
    for (String thisTok : wordsOfSent) {
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
    List<String> result = new ArrayList<>();
    String remainder = paragraph; /* copy to mess with */
    StringBuilder accumulator = new StringBuilder();
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
        if (lowerCaseTerms.contains(m.group(4)) || RULE0.matcher(test).matches()
            || RULE1.matcher(test).matches() || EWORDRULE.matcher(m.group(4)).matches()) {
          result.add(accumulator.toString());
          accumulator.setLength(0);
        }

        /* Don't split if _rule2 */
        /* Don't split if $2 is in _abbreviations */
        /* Otherwise split */
        else if ((!RULE2.matcher(test).matches()) && (!abbreviations.contains(m.group(2).toLowerCase()))
            && (!abbreviations.contains(m.group(2)))) {
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

  public static void main(String[] argv) {
    String TEST_PAR = "Wot about Fig. 2 and (Fig. 3)? We created a myosinII-responsive FA interactome from proteins in the expected FA list by color-coding proteins according to MDR magnitude (Supplemental Fig. S4 and Table 7, http://dir.nhlbi.nih.gov/papers/lctm/focaladhesion/Home/index.html). The interactome illustrates the full range of MDR values, including proteins exhibiting minor/low confidence changes. This interactome suggests how myosinII activity may collectively modulate FA abundance of groups of proteins mediating distinct pathways.";
    TEST_PAR = "The development coincided with a warning issued in London by the Bosnian Foreign Minister, Irfan Ljubijankic, that the region was \"dangerously close to a resumption of all-out war.\" He added, \"At the moment we have a diplomatic vacuum.\"\nIn the latest of a series of inconclusive Western moves to avert a renewed Balkan flareup, the American envoy, Assistant Secretary of State Richard C. Holbrooke, met with President Franjo Tudjman at the Presidential Palace in the hills above Zagreb tonight. But the meeting lasted less than 40 minutes and Mr. Holbrooke refused to answer reporters' questions when he left.";
    EnglishSentenceSplitter splitter = new EnglishSentenceSplitter();
    List<int[]> output = splitter.markupRawText(TEST_PAR);
    for (int[] o : output) {
      System.out.println(o[2] + "--" + o[3] + ": " + TEST_PAR.substring(o[2], o[3]));
    }
  }
}
