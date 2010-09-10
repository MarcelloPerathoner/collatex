package eu.interedition.collatex2.implementation.containers.jgraph;

import java.util.Set;

import com.google.common.collect.Sets;

import eu.interedition.collatex2.interfaces.IJVariantGraphEdge;
import eu.interedition.collatex2.interfaces.IJVariantGraphVertex;
import eu.interedition.collatex2.interfaces.IWitness;

public class JVariantGraphEdge implements IJVariantGraphEdge {
  private final Set<IWitness> witnesses;
  private final IJVariantGraphVertex beginVertex;
  private final IJVariantGraphVertex endVertex;

  public JVariantGraphEdge(IJVariantGraphVertex beginVertex, IJVariantGraphVertex endVertex) {
    this.beginVertex = beginVertex;
    this.endVertex = endVertex;
    Set<IWitness> beginWitnesses = beginVertex.getWitnesses();
    Set<IWitness> endWitnesses = endVertex.getWitnesses();
    if (beginWitnesses.isEmpty()) {
      witnesses = Sets.newHashSet(endWitnesses);
    } else {
      witnesses = Sets.newHashSet(beginWitnesses);
      if (!endWitnesses.isEmpty()) {
        witnesses.retainAll(endVertex.getWitnesses());
      }
    }
  }

  @Override
  public Set<IWitness> getWitnesses() {
    return witnesses;
  }

  @Override
  public String toString() {
    return beginVertex + " --{" + witnesses + "}-> " + endVertex;
  }

}
