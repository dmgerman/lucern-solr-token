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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
comment|/** Implements the fuzzy search query. The similiarity measurement  * is based on the Levenshtein (edit distance) algorithm.  */
end_comment
begin_class
DECL|class|FuzzyQuery
specifier|public
specifier|final
class|class
name|FuzzyQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|minimumSimilarity
specifier|private
name|float
name|minimumSimilarity
decl_stmt|;
comment|/**    * Create a new FuzzyQuery that will match terms with a similarity     * of at least<code>minimumSimilarity</code> to<code>term</code>.    *     * @param term the term to search for    * @param minimumSimilarity a value between 0 and 1 to set the required similarity    *  between the query term and the matching terms. For example, for a    *<code>minimumSimilarity</code> of<code>0.5</code> a term of the same length    *  as the query term is considered similar to the query term if the edit distance    *  between both terms is less than<code>length(term)*0.5</code>.    * @throws IllegalArgumentException if minimumSimilarity is&gt; 1 or&lt; 0    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|minimumSimilarity
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|minimumSimilarity
operator|>
literal|1.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity> 1"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|minimumSimilarity
operator|<
literal|0.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity< 0"
argument_list|)
throw|;
name|this
operator|.
name|minimumSimilarity
operator|=
name|minimumSimilarity
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, float) FuzzyQuery(term, 0.5f)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
block|}
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FuzzyTermEnum
argument_list|(
name|reader
argument_list|,
name|getTerm
argument_list|()
argument_list|,
name|minimumSimilarity
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|super
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|'~'
return|;
block|}
block|}
end_class
end_unit
