package eu.interedition.collatex.matching;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import eu.interedition.collatex.collation.alignment.Alignment;
import eu.interedition.collatex.collation.alignment.Match;
import eu.interedition.collatex.collation.alignment.Matcher;
import eu.interedition.collatex.input.Witness;
import eu.interedition.collatex.input.WitnessBuilder;
import eu.interedition.collatex.input.Word;

public class PossibleMatchesTest {
  @Test
  public void testPossibleMatchesAsAMap() {
    WitnessBuilder builder = new WitnessBuilder();
    Witness a = builder.build("zijn hond liep aan zijn hand");
    Witness b = builder.build("op zijn pad liep zijn hond aan zijn hand");
    Alignment result = Matcher.align(a, b);
    Alignment firstAlignment = result.getPrevious().getPrevious().getPrevious().getPrevious();
    Word zijn = a.getWordOnPosition(1);
    Collection<Match> linked = firstAlignment.getMatchesThatLinkFrom(zijn);
    Assert.assertEquals("[(1->2), (1->5), (1->8)]", linked.toString());

    Word zijnB = b.getWordOnPosition(2);
    Collection<Match> links = firstAlignment.getMatchesThatLinkTo(zijnB);
    Assert.assertEquals("[(1->2), (5->2)]", links.toString());
  }
}