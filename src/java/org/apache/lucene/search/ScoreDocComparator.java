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
begin_comment
comment|/**  * Expert: Compares two ScoreDoc objects for sorting.  *  *<p>Created: Feb 3, 2004 9:00:16 AM   *  * @since   lucene 1.4  * @version $Id$  * @deprecated use {@link FieldComparator}  */
end_comment
begin_interface
DECL|interface|ScoreDocComparator
specifier|public
interface|interface
name|ScoreDocComparator
block|{
comment|/** Special comparator for sorting hits according to computed relevance (document score). */
DECL|field|RELEVANCE
specifier|static
specifier|final
name|ScoreDocComparator
name|RELEVANCE
init|=
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
if|if
condition|(
name|i
operator|.
name|score
operator|>
name|j
operator|.
name|score
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|i
operator|.
name|score
operator|<
name|j
operator|.
name|score
condition|)
return|return
literal|1
return|;
return|return
literal|0
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
operator|new
name|Float
argument_list|(
name|i
operator|.
name|score
argument_list|)
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
name|SCORE
return|;
block|}
block|}
decl_stmt|;
comment|/** Special comparator for sorting hits according to index order (document number). */
DECL|field|INDEXORDER
specifier|static
specifier|final
name|ScoreDocComparator
name|INDEXORDER
init|=
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
if|if
condition|(
name|i
operator|.
name|doc
operator|<
name|j
operator|.
name|doc
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|i
operator|.
name|doc
operator|>
name|j
operator|.
name|doc
condition|)
return|return
literal|1
return|;
return|return
literal|0
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
operator|new
name|Integer
argument_list|(
name|i
operator|.
name|doc
argument_list|)
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
name|DOC
return|;
block|}
block|}
decl_stmt|;
comment|/** 	 * Compares two ScoreDoc objects and returns a result indicating their 	 * sort order. 	 * @param i First ScoreDoc 	 * @param j Second ScoreDoc 	 * @return a negative integer if<code>i</code> should come before<code>j</code><br>      *         a positive integer if<code>i</code> should come after<code>j</code><br>      *<code>0</code> if they are equal 	 * @see java.util.Comparator 	 */
DECL|method|compare
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|i
parameter_list|,
name|ScoreDoc
name|j
parameter_list|)
function_decl|;
comment|/** 	 * Returns the value used to sort the given document.  The 	 * object returned must implement the java.io.Serializable 	 * interface.  This is used by multisearchers to determine how      * to collate results from their searchers. 	 * @see FieldDoc 	 * @param i Document 	 * @return Serializable object 	 */
DECL|method|sortValue
name|Comparable
name|sortValue
parameter_list|(
name|ScoreDoc
name|i
parameter_list|)
function_decl|;
comment|/** 	 * Returns the type of sort.  Should return<code>SortField.SCORE</code>,      *<code>SortField.DOC</code>,<code>SortField.STRING</code>,      *<code>SortField.INTEGER</code>,<code>SortField.FLOAT</code> or      *<code>SortField.CUSTOM</code>.  It is not valid to return      *<code>SortField.AUTO</code>.      * This is used by multisearchers to determine how to collate results      * from their searchers. 	 * @return One of the constants in SortField. 	 * @see SortField 	 */
DECL|method|sortType
name|int
name|sortType
parameter_list|()
function_decl|;
block|}
end_interface
end_unit
