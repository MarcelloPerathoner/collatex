package eu.interedition.collatex2.implementation.indexing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import eu.interedition.collatex2.implementation.Factory;
import eu.interedition.collatex2.implementation.alignmenttable.AlignmentTableCreator3;
import eu.interedition.collatex2.interfaces.IAlignment;
import eu.interedition.collatex2.interfaces.IAlignmentTable;
import eu.interedition.collatex2.interfaces.ICallback;
import eu.interedition.collatex2.interfaces.IWitness;

public class AlignmentTableIndexTest {
  private static final ICallback CALLBACK = new ICallback() {
    @Override
    public void alignment(final IAlignment alignment) {
      System.out.println(alignment.getMatches());
    }
  };
  private Factory factory;

  @Before
  public void setup() {
    factory = new Factory();
  }

  //Note: there are more valid combinations!
  @Ignore
  @Test
  public void test() {
    final IWitness witnessA = factory.createWitness("A", "the big black cat and the big black rat");
    final IAlignmentTable table = AlignmentTableCreator3.createAlignmentTable(Lists.newArrayList(witnessA), CALLBACK);
    final AlignmentTableIndex index = new AlignmentTableIndex(table);
    assertTrue(index.containsNormalizedPhrase("# the big black"));
    assertTrue(index.containsNormalizedPhrase("the big black cat"));
    assertTrue(index.containsNormalizedPhrase("and"));
    assertTrue(index.containsNormalizedPhrase("and the big black"));
    assertTrue(index.containsNormalizedPhrase("the big black rat"));
    assertEquals(5, index.size());
  }
}