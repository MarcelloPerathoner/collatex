/*
 * Copyright (c) 2015 The Interedition Development Group.
 *
 * This file is part of CollateX.
 *
 * CollateX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CollateX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CollateX.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * {@link java.util.Comparator Comparators} for matching tokens.
 * <p/>
 * Implementation base the equality of tokens on strict or on approximate equality of their respective textual contents.
 *
 * @see eu.interedition.collatex.matching.StrictEqualityTokenComparator
 * @see eu.interedition.collatex.matching.EditDistanceTokenComparator
 *
 */
package eu.interedition.collatex.matching;