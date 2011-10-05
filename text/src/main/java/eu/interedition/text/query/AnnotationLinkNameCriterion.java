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
package eu.interedition.text.query;

import com.google.common.base.Function;
import eu.interedition.text.QName;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class AnnotationLinkNameCriterion implements Criterion {
  private final QName name;

  AnnotationLinkNameCriterion(QName name) {
    this.name = name;
  }

  public QName getName() {
    return name;
  }

  public static Function<AnnotationLinkNameCriterion, QName> TO_NAME = new Function<AnnotationLinkNameCriterion, QName>() {
    public QName apply(AnnotationLinkNameCriterion input) {
      return input.getName();
    }
  };
}