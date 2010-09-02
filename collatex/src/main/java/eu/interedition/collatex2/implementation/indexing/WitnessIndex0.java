/**
 * CollateX - a Java library for collating textual sources,
 * for example, to produce an apparatus.
 *
 * Copyright (C) 2010 ESF COST Action "Interedition".
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.interedition.collatex2.implementation.indexing;

import static com.google.common.collect.Iterables.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import eu.interedition.collatex2.input.Phrase;
import eu.interedition.collatex2.interfaces.INormalizedToken;
import eu.interedition.collatex2.interfaces.IPhrase;
import eu.interedition.collatex2.interfaces.IWitness;
import eu.interedition.collatex2.interfaces.IWitnessIndex;

public class WitnessIndex0 implements IWitnessIndex {
  private Logger log = LoggerFactory.getLogger(WitnessIndex0.class); 
  Multiset<IPhrase> phraseBag = TreeMultiset.create();
  
  public WitnessIndex0(final Multiset<IPhrase> _phraseBag) {
    this.phraseBag = _phraseBag;
  }

  public WitnessIndex0(final IWitness witness, final Collection<String> repeatingTokens) {
    final List<INormalizedToken> tokens = witness.getTokens();
    final Multimap<String, IPhrase> seedlings = seed(tokens);
    final Map<String, IPhrase> crop = grow(seedlings, tokens, repeatingTokens);
    phraseBag.addAll(harvest(crop));
  }

  public static final Function<IPhrase, String> PHRASE_TO_NORMALIZED = new Function<IPhrase, String>() {
    @Override
    public String apply(final IPhrase phrase) {
      return phrase.getNormalized();
    }
  };

  @Override
  public boolean contains(final String normalizedPhrase) {
    final List<String> phrasesInIndex = Lists.newArrayList(transform(phraseBag.elementSet(), PHRASE_TO_NORMALIZED));
    log.debug(phrasesInIndex.toString());
    return phrasesInIndex.contains(normalizedPhrase);
  }

  @Override
  public int size() {
    return phraseBag.size();
  }

  private Multimap<String, IPhrase> seed(final List<INormalizedToken> tokens) {
    final Multimap<String, IPhrase> phraseMap = HashMultimap.create();
    for (final INormalizedToken token : tokens) {
      phraseMap.put(token.getNormalized(), new Phrase(Lists.newArrayList(token)));
    }
    return phraseMap;
  }

  private Map<String, IPhrase> grow(final Multimap<String, IPhrase> seed, final List<INormalizedToken> tokens, final Collection<String> repeatingTokens) {
    Multimap<String, IPhrase> phraseMap = seed;

    do {
      final Multimap<String, IPhrase> newPhraseMap = HashMultimap.create();
      for (final String phraseId : phraseMap.keySet()) {
        final Collection<IPhrase> phrases = phraseMap.get(phraseId);
        if (phrases.size() == 1 && !repeatingTokens.contains(phraseId)) {
          final IPhrase phrase = phrases.iterator().next();
          newPhraseMap.put(phraseId, phrase);
        } else {
          addExpandedPhrases(newPhraseMap, phrases, tokens, repeatingTokens);
        }
      }
      phraseMap = newPhraseMap;
    } while (phraseMap.entries().size() > phraseMap.keySet().size());

    final Map<String, IPhrase> crop = Maps.newHashMap();
    for (final Entry<String, IPhrase> entry : phraseMap.entries()) {
      crop.put(entry.getKey(), entry.getValue());
    }

    return crop;
  }

  private void addExpandedPhrases(final Multimap<String, IPhrase> newPhraseMap, final Collection<IPhrase> phrases, final List<INormalizedToken> tokens, final Collection<String> repeatingTokens) {
    for (final IPhrase phrase : phrases) {
      final int beforePosition = phrase.getBeginPosition() - 1;
      final int afterPosition = phrase.getEndPosition();

      final INormalizedToken beforeToken = (beforePosition > 0) ? tokens.get(beforePosition - 1) : new NullToken(phrase.getBeginPosition(), phrase.getSigil());
      final INormalizedToken afterToken = (afterPosition < tokens.size()) ? tokens.get(afterPosition) : new NullToken(phrase.getEndPosition(), phrase.getSigil());

      final ArrayList<INormalizedToken> leftExpandedTokenList = Lists.newArrayList(beforeToken);
      leftExpandedTokenList.addAll(phrase.getTokens());
      final IPhrase leftExpandedPhrase = new Phrase(leftExpandedTokenList);

      final ArrayList<INormalizedToken> rightExpandedTokenList = Lists.newArrayList(phrase.getTokens());
      rightExpandedTokenList.add(afterToken);
      final IPhrase rightExpandedPhrase = new Phrase(rightExpandedTokenList);

      final String leftPhraseId = leftExpandedPhrase.getNormalized();
      newPhraseMap.put(leftPhraseId, leftExpandedPhrase);

      final String rightPhraseId = rightExpandedPhrase.getNormalized();
      newPhraseMap.put(rightPhraseId, rightExpandedPhrase);
    }
  }

  private List<IPhrase> harvest(final Map<String, IPhrase> phraseMap) {
    final List<IPhrase> values = Lists.newArrayList(phraseMap.values());
    Collections.sort(values, Phrase.PHRASECOMPARATOR);
    return values;
  }

  @Override
  public IPhrase getPhrase(String key) {
    throw new RuntimeException("NOT IMPLEMENTED!");
  }

  @Override
  public Set<String> keys() {
    throw new RuntimeException("NOT IMPLEMENTED!");
  }

}
