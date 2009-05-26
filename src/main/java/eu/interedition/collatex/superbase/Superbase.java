package eu.interedition.collatex.superbase;

import java.util.List;

import com.google.common.collect.Lists;

import eu.interedition.collatex.input.Witness;
import eu.interedition.collatex.input.Word;

public class Superbase extends Witness {
  private final List<Column> columnForEachWord;

  public Superbase() {
    this.columnForEachWord = Lists.newArrayList();
  }

  public void addWord(Word word, Column column) {
    Word newWord = new Word("sb", word.original, getWords().size() + 1);
    getWords().add(newWord);
    columnForEachWord.add(column);
  }

  public Column getColumnFor(Word word) {
    int indexOf = getWords().indexOf(word);
    if (indexOf == -1) {
      throw new RuntimeException("Unexpected error: no column found for word: " + word);
    }
    Column column = columnForEachWord.get(indexOf);
    if (column == null) {
      throw new RuntimeException(word.toString() + " not in alignment table!");
    }
    return column;
  }

  //  public void setColumn(int position, Column column) {
  //    columnForEachWord.set(position - 1, column);
  //  }
}