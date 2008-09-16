package com.sd_editions.collatex.spike2;

import java.util.List;

import junit.framework.TestCase;

import com.sd_editions.collatex.spike2.collate.Transposition;

public class TranspositionTest extends TestCase {
  public void testTransposition1() {
    Colors colors = new Colors("a b c d e", "a c d b e");
    Modifications modifications = colors.detectTranspositions(1, 2);
    List<Transposition> transpositions = modifications.getTranspositions();
    assertEquals("transposition: b switches position with c d", transpositions.get(0).toString());
  }

  public void testTransposition2() {
    Colors colors = new Colors("a b x c d ", "a c d x b");
    Modifications modifications = colors.detectTranspositions(1, 2);
    List<Transposition> transpositions = modifications.getTranspositions();
    assertEquals("transposition: b switches position with c d", transpositions.get(0).toString());
  }

  public void testTransposition3() {
    Colors colors = new Colors("a b x c d ", "c d x a b");
    Modifications modifications = colors.detectTranspositions(1, 2);
    List<Transposition> transpositions = modifications.getTranspositions();
    assertEquals("transposition: a b switches position with c d", transpositions.get(0).toString());
  }

  //Note: this test should NOT lead to a transposition
  public void testComplex() {
    String base = "The black dog chases a red cat.";
    String witness = "A red cat chases the yellow dog";
    Colors colors = new Colors(base, witness);
    Modifications modifications = colors.detectTranspositions(1, 2);
    List<Transposition> transpositions = modifications.getTranspositions();
    assertEquals(0, transpositions.size());
  }

  //

  //  public void testPhrases1() {
  //    Colors colors = new Colors("a b c d e", "a c d b e");
  //    TranspositionDetection detection = colors.detectTranspositions(1, 2);
  //    List<Phrase> phrases = detection.getPhrases();
  //    assertEquals(4, phrases.size());
  //    assertEquals("a", phrases.get(0).toString());
  //    assertEquals("c d", phrases.get(1).toString());
  //    assertEquals("b", phrases.get(2).toString());
  //    assertEquals("e", phrases.get(3).toString());
  //    
  //  }
  //
  //  public void testPhrases2() {
  //    Colors colors = new Colors("a b x c d ", "a c d x b");
  //    TranspositionDetection detection = colors.detectTranspositions(1, 2);
  //    List<Phrase> phrases = detection.getPhrases();
  //    assertEquals(4, phrases.size());
  //    assertEquals("a", phrases.get(0).toString());
  //    assertEquals("c d", phrases.get(1).toString());
  //    assertEquals("x", phrases.get(2).toString());
  //    assertEquals("b", phrases.get(3).toString());
  //    //    assertEquals("transposition: b switches position with c d", modifications.get(0).toString());
  //  }
  //
  //  public void testPhrases3() {
  //    Colors colors = new Colors("a b x c d ", "c d x a b");
  //    TranspositionDetection detection = colors.detectTranspositions(1, 2);
  //    List<Phrase> phrases = detection.getPhrases();
  //    assertEquals(3, phrases.size());
  //    assertEquals("c d", phrases.get(0).toString());
  //    assertEquals("x", phrases.get(1).toString());
  //    assertEquals("a b", phrases.get(2).toString());
  //    //    assertEquals("transposition: a b switches position with c d", modifications.get(0).toString());
  //  }

}