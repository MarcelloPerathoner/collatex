package eu.interedition.collatex.implementation.graph.edit;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import eu.interedition.collatex.implementation.matching.Matches;
import eu.interedition.collatex.interfaces.INormalizedToken;
import eu.interedition.collatex.interfaces.ITokenLinker;
import eu.interedition.collatex.interfaces.IWitness;

public class EditGraphLinker implements ITokenLinker {

  @Override
  public Map<INormalizedToken, INormalizedToken> link(IWitness superbase, IWitness b, Comparator<INormalizedToken> comparator) {
    EditGraphCreator egCreator = new EditGraphCreator();
    EditGraph editGraph = egCreator.buildEditGraph(superbase, b, comparator);
    EditGraphVisitor visitor = new EditGraphVisitor(editGraph);
    Matches match = Matches.between(superbase, b, comparator);
    Multimap<INormalizedToken, INormalizedToken> matches = match.getAll();
    List<EditGraphEdge> shortestPath = visitor.getShortestPath(match);
    Iterator<EditGraphEdge> edges = shortestPath.iterator();
    //    for (DGEdge edge : shortestPath) {
    //      System.out.println(edge.getTargetVertex().toString());
    //    }
    //Note: This is the second time the matcher function is called
    Map<INormalizedToken, INormalizedToken> linkedTokens = Maps.newLinkedHashMap();
    List<INormalizedToken> tokens = b.getTokens();
    for (INormalizedToken token : tokens) {
      if (matches.containsKey(token)) {
        EditGraphEdge edge = edges.next();
        linkedTokens.put(token, edge.getTargetVertex().getBaseToken());
      }
    }
    return linkedTokens;
  }
}
