begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Abstract base class for sorting hits returned by a Query.  *  *<p>This class should only be used if the other SortField  * types (SCORE, DOC, STRING, INT, FLOAT) do not provide an  * adequate sorting.  It maintains an internal cache of values which  * could be quite large.  The cache is an array of Comparable,  * one for each document in the index.  There is a distinct  * Comparable for each unique term in the field - if  * some documents have the same term in the field, the cache  * array will have entries which reference the same Comparable.  *  *<p>Created: Apr 21, 2004 5:08:38 PM  *  *  * @version $Id$  * @since   1.4  * @deprecated Please use {@link FieldComparatorSource} instead.  */
end_comment
begin_class
DECL|class|SortComparator
specifier|public
specifier|abstract
class|class
name|SortComparator
implements|implements
name|SortComparatorSource
block|{
comment|// inherit javadocs
DECL|method|newComparator
specifier|public
name|ScoreDocComparator
name|newComparator
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|Comparable
index|[]
name|cachedValues
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getCustom
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|SortComparator
operator|.
name|this
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|i
parameter_list|,
name|ScoreDoc
name|j
parameter_list|)
block|{
return|return
name|cachedValues
index|[
name|i
operator|.
name|doc
index|]
operator|.
name|compareTo
argument_list|(
name|cachedValues
index|[
name|j
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|cachedValues
index|[
name|i
operator|.
name|doc
index|]
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|CUSTOM
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an object which, when sorted according to natural order,    * will order the Term values in the correct order.    *<p>For example, if the Terms contained integer values, this method    * would return<code>new Integer(termtext)</code>.  Note that this    * might not always be the most efficient implementation - for this    * particular example, a better implementation might be to make a    * ScoreDocLookupComparator that uses an internal lookup table of int.    * @param termtext The textual value of the term.    * @return An object representing<code>termtext</code> that sorts according to the natural order of<code>termtext</code>.    * @see Comparable    * @see ScoreDocComparator    */
DECL|method|getComparable
specifier|protected
specifier|abstract
name|Comparable
name|getComparable
parameter_list|(
name|String
name|termtext
parameter_list|)
function_decl|;
block|}
end_class
end_unit
