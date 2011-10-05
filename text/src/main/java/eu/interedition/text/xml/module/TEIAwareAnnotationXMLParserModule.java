/*
 * #%L
 * Text: A text model with range-based markup via standoff annotations.
 * %%
 * Copyright (C) 2010 - 2011 The Interedition Development Group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.interedition.text.xml.module;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import eu.interedition.text.AnnotationRepository;
import eu.interedition.text.QName;
import eu.interedition.text.Range;
import eu.interedition.text.mem.SimpleAnnotation;
import eu.interedition.text.mem.SimpleQName;
import eu.interedition.text.xml.XMLEntity;
import eu.interedition.text.xml.XMLParserState;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import static eu.interedition.text.TextConstants.TEI_NS;
import static eu.interedition.text.TextConstants.XML_ID_ATTR_NAME;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class TEIAwareAnnotationXMLParserModule extends AbstractAnnotationXMLParserModule {
  private static final Map<QName, QName> MILESTONE_ELEMENT_UNITS = Maps.newHashMap();

  static {
    MILESTONE_ELEMENT_UNITS.put(new SimpleQName(TEI_NS, "pb"), new SimpleQName(TEI_NS, "page"));
    MILESTONE_ELEMENT_UNITS.put(new SimpleQName(TEI_NS, "lb"), new SimpleQName(TEI_NS, "line"));
    MILESTONE_ELEMENT_UNITS.put(new SimpleQName(TEI_NS, "cb"), new SimpleQName(TEI_NS, "column"));
    MILESTONE_ELEMENT_UNITS.put(new SimpleQName(TEI_NS, "gb"), new SimpleQName(TEI_NS, "gathering"));
  }

  private static final QName MILESTONE_NAME = new SimpleQName(TEI_NS, "milestone");

  private Multimap<String, SimpleAnnotation> spanning;
  private Multimap<String, Map<QName, String>> spanningAttributes;
  private Map<QName, SimpleAnnotation> milestones;
  private Map<QName, Map<QName, String>> milestoneAttributes;

  public TEIAwareAnnotationXMLParserModule(AnnotationRepository annotationRepository, int batchSize) {
    super(annotationRepository, batchSize);
  }

  @Override
  public void start(XMLParserState state) {
    super.start(state);
    this.spanning = ArrayListMultimap.create();
    this.spanningAttributes = ArrayListMultimap.create();
    this.milestones = Maps.newHashMap();
    this.milestoneAttributes = Maps.newHashMap();
  }

  @Override
  public void end(XMLParserState state) {
    final long textOffset = state.getTextOffset();
    for (QName milestoneUnit : milestones.keySet()) {
      final SimpleAnnotation last = milestones.get(milestoneUnit);
      final Map<QName, String> lastAttrs = milestoneAttributes.get(milestoneUnit);
      add(new SimpleAnnotation(last.getText(), last.getName(), new Range(last.getRange().getStart(), textOffset)), lastAttrs);
    }

    this.milestoneAttributes = null;
    this.milestones = null;
    this.spanningAttributes = null;
    this.spanning = null;

    super.end(state);
  }

  @Override
  public void start(XMLEntity entity, XMLParserState state) {
    super.start(entity, state);
    handleSpanningElements(entity, state);
    handleMilestoneElements(entity, state);
  }

  protected void handleMilestoneElements(XMLEntity entity, XMLParserState state) {
    final QName entityName = entity.getName();
    final Map<QName, String> entityAttributes = Maps.newHashMap(entity.getAttributes());

    QName milestoneUnit = null;
    if (MILESTONE_NAME.equals(entityName)) {
      for (Iterator<QName> it = entityAttributes.keySet().iterator(); it.hasNext(); ) {
        final QName attrName = it.next();
        final URI attrNameNs = attrName.getNamespaceURI();
        if ("unit".equals(attrName.getLocalName()) && (attrNameNs == null || TEI_NS.equals(attrNameNs))) {
          milestoneUnit = new SimpleQName(TEI_NS, entityAttributes.get(attrName));
          it.remove();
        }
      }
    } else if (MILESTONE_ELEMENT_UNITS.containsKey(entityName)) {
      milestoneUnit = MILESTONE_ELEMENT_UNITS.get(entityName);
    }

    if (milestoneUnit == null) {
      return;
    }

    final long textOffset = state.getTextOffset();

    final SimpleAnnotation last = milestones.get(milestoneUnit);
    final Map<QName, String> lastAttrs = milestoneAttributes.get(milestoneUnit);
    if (last != null && lastAttrs != null) {
      add(new SimpleAnnotation(last.getText(), last.getName(), new Range(last.getRange().getStart(), textOffset)), lastAttrs);
    }

    milestones.put(milestoneUnit, new SimpleAnnotation(state.getTarget(), milestoneUnit, new Range(textOffset, textOffset)));
    milestoneAttributes.put(milestoneUnit, entityAttributes);
  }

  protected void handleSpanningElements(XMLEntity entity, XMLParserState state) {
    final URI entityNs = entity.getName().getNamespaceURI();
    final Map<QName, String> entityAttributes = Maps.newHashMap(entity.getAttributes());
    String spanTo = null;
    String refId = null;
    for (Iterator<QName> it = entityAttributes.keySet().iterator(); it.hasNext(); ) {
      final QName attrName = it.next();
      if ("spanTo".equals(attrName.getLocalName())) {
        final URI attrNs = attrName.getNamespaceURI();
        if (attrNs == null || TEI_NS.equals(attrNs)) {
          spanTo = entityAttributes.get(attrName).replaceAll("^#", "");
          it.remove();
        }
      } else if (XML_ID_ATTR_NAME.equals(attrName)) {
        refId = entityAttributes.get(attrName);
      }
    }

    if (spanTo == null && refId == null) {
      return;
    }

    final long textOffset = state.getTextOffset();

    if (spanTo != null) {
      final QName name = entity.getName();
      spanning.put(spanTo, new SimpleAnnotation(
              state.getTarget(),
              new SimpleQName(name.getNamespaceURI(), name.getLocalName().replaceAll("Span$", "")),
              new Range(textOffset, textOffset)));
      spanningAttributes.put(spanTo, entityAttributes);
    }
    if (refId != null) {
      final Iterator<SimpleAnnotation> aIt = spanning.removeAll(refId).iterator();
      final Iterator<Map<QName, String>> attrIt = spanningAttributes.removeAll(refId).iterator();
      while (aIt.hasNext() && attrIt.hasNext()) {
        final SimpleAnnotation a = aIt.next();
        add(new SimpleAnnotation(a.getText(), a.getName(), new Range(a.getRange().getStart(), textOffset)), attrIt.next());
      }
    }
  }
}