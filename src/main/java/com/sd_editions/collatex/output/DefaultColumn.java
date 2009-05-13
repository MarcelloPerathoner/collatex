package com.sd_editions.collatex.output;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sd_editions.collatex.permutations.Witness;
import com.sd_editions.collatex.permutations.Word;

public class DefaultColumn extends Column {
  Map<String, Word> wordsProWitness;

  // NOTE: how to handle multiple words in a reading
  // of an app? we used to use phrases for that
  // maybe we should remember start position of the column
  // in the table and list of words here in the map?
  // but a list of words is a phrase
  // or maybe we should do the columns later?
  public DefaultColumn(Word word) {
    wordsProWitness = Maps.newHashMap();
    wordsProWitness.put(word.getWitnessId(), word);
  }

  @Override
  public void toXML(StringBuilder builder) {
  // TODO Auto-generated method stub

  }

  // NOTE: maybe method name is not the best choice!
  @Override
  public String toString() {
    Collection<Word> values = wordsProWitness.values();
    String result = "";
    String delim = "";
    for (Word word : values) {
      result += delim + word.original;
      delim += " ";
    }
    return result;
  }

  // TODO: think about empty columns here!

  @Override
  public Word getWord(Witness witness) {
    // Note: Change map to <Witness, Word> ?
    Word result = wordsProWitness.get(witness.id);
    return result;
  }

}