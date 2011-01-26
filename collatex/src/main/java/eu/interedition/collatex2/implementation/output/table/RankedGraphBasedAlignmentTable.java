package eu.interedition.collatex2.implementation.output.table;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import eu.interedition.collatex2.implementation.output.rankedgraph.IRankedVariantGraphVertex;
import eu.interedition.collatex2.implementation.output.rankedgraph.VariantGraphRanker;
import eu.interedition.collatex2.implementation.output.segmented_graph.ISegmentedVariantGraph;
import eu.interedition.collatex2.implementation.output.segmented_graph.NonSegmentedGraphConverter;
import eu.interedition.collatex2.interfaces.IAlignmentTable;
import eu.interedition.collatex2.interfaces.IColumn;
import eu.interedition.collatex2.interfaces.IVariantGraph;
import eu.interedition.collatex2.interfaces.IVariantGraphVertex;
import eu.interedition.collatex2.interfaces.IWitness;

//TODO: remove explicit dependency on rankedgraph implementation classes!
//TODO: The maximum dependency is implementation classes of the same package!
//TODO: make base alignment table abstract and so on!
public class RankedGraphBasedAlignmentTable extends BaseAlignmentTable implements IAlignmentTable {
  private final IVariantGraph graph;

  public RankedGraphBasedAlignmentTable(IVariantGraph graph) {
    this.graph = graph;
    init();
  }

  private void init() {
    NonSegmentedGraphConverter converter = new NonSegmentedGraphConverter();
    ISegmentedVariantGraph segmentedVariantGraph = converter.convertGraph(graph);
    VariantGraphRanker ranker = new VariantGraphRanker(segmentedVariantGraph);
    Iterator<IRankedVariantGraphVertex> iterator = ranker.iterator();
    Iterator<IVariantGraphVertex> vertexIterator = graph.iterator();
    //skip startVertex
    vertexIterator.next();
    while(iterator.hasNext()) {
      //nextVertex is a IRankedVariantGraphVertex which is not the 
      //same as a real vertex!
      IRankedVariantGraphVertex nextVertex = iterator.next();
      IVariantGraphVertex next = vertexIterator.next();
      if (next.equals(graph.getEndVertex())) {
        continue;
      }
      int rank = nextVertex.getRank();
//      System.out.println("DEBUG: "+nextVertex.getNormalized()+":"+nextVertex.getRank());
      if (rank>columns.size()) {
        addNewColumn(next, nextVertex.getRank());
      } else {
        columns.get(rank-1).addVertex(next);
      }
    }
//    System.out.println("TOTAL number of columns: "+columns.size());
  }

  private IColumn addNewColumn(IVariantGraphVertex vertex, int rank) {
    final IColumn column = new VariantGraphBasedColumn(vertex, rank);
    columns.add(column);
    return column;
  }
  
  @Override
  public final List<String> getSigla() {
    List<IWitness> witnesses = graph.getWitnesses();
    List<String> sigla = Lists.newArrayList();
    for (IWitness witness : witnesses) {
      sigla.add(witness.getSigil());
    }
    return sigla;
  }

}
