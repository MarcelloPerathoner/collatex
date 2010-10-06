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

package eu.interedition.collatex2.implementation.alignmenttable;

import java.util.List;

import com.google.common.collect.Lists;

import eu.interedition.collatex2.interfaces.IInternalColumn;
import eu.interedition.collatex2.interfaces.IColumns;
import eu.interedition.collatex2.interfaces.INormalizedToken;
import eu.interedition.collatex2.interfaces.IPhrase;

public class Columns implements IColumns {
  private final List<IInternalColumn> columns;

  public Columns(final List<IInternalColumn> columns1) {
    this.columns = columns1;
  }

  public Columns() {
    this.columns = Lists.newArrayList();
  }

  //NOTE: for now we assume that phraseA is longer than phraseB!
  //NOTE: this method is only for matches!
  public void addMatchPhrase(final IPhrase phraseB) {
    if (phraseB.size() > columns.size()) {
      // System.out.println(columns.size());
      // System.out.println(phraseB.size());
      throw new RuntimeException("The phrase to be placed in the table is longer than columns!");
    }
    final List<INormalizedToken> tokens = phraseB.getTokens();
    for (int i = 0; i < phraseB.size(); i++) {
      final IInternalColumn column = columns.get(i);
      final INormalizedToken token = tokens.get(i);
      column.addMatch(token);
    }
  }

  //NOTE: for now we assume that phraseA is longer than phraseB!
  //NOTE: this method is only for variants!
  public void addVariantPhrase(final IPhrase phraseB) {
    if (phraseB.size() > columns.size()) {
      // System.out.println("!!" + toString() + ":" + phraseB.toString());
      // System.out.println(columns.size());
      // System.out.println(phraseB.size());
      throw new RuntimeException("The phrase to be placed in the table is longer than columns!");
    }
    final List<INormalizedToken> tokens = phraseB.getTokens();
    for (int i = 0; i < phraseB.size(); i++) {
      final IInternalColumn column = columns.get(i);
      final INormalizedToken token = tokens.get(i);
      column.addVariant(token);
    }
  }

  @Override
  public int getBeginPosition() {
    return getFirstColumn().getPosition();
  }

  @Override
  public int getEndPosition() {
    return getLastColumn().getPosition();
  }

  @Override
  public IInternalColumn getFirstColumn() {
    if (isEmpty()) {
      throw new RuntimeException("Columns are empty!");
    }
    return columns.get(0);
  }

  public IInternalColumn getLastColumn() {
    if (isEmpty()) {
      throw new RuntimeException("Columns are empty!");
    }
    return columns.get(columns.size() - 1);
  }

  @Override
  public boolean isEmpty() {
    return columns.isEmpty();
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    String splitter="";
    for (IInternalColumn column : columns) {
      buffer.append(splitter);
      buffer.append(column.toString());
      splitter = " ";
    }
    return buffer.toString();
  }
//    if (isEmpty()) {
//      return "Columns: EMPTY";
//    }
//    return "Columns: " + getBeginPosition() + "-" + getEndPosition();
//  }

  @Override
  public int size() {
    return columns.size();
  }

  @Override
  public List<IInternalColumn> getColumns() {
    return columns;
  }

}
