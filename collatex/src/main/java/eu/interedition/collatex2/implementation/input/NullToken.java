package eu.interedition.collatex2.implementation.input;

import eu.interedition.collatex2.interfaces.INormalizedToken;

public class NullToken implements INormalizedToken {

  @Override
  public String getNormalized() {
    return "#";
  }

  @Override
  public String getSigil() {
    throw new RuntimeException("DO NOT CALL THIS METHOD!");
  }

  @Override
  public String getContent() {
    return "";
  }
}