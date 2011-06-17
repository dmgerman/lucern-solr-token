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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
begin_comment
comment|/** The abstract base class for queries.<p>Instantiable subclasses are:<ul><li> {@link TermQuery}<li> {@link MultiTermQuery}<li> {@link BooleanQuery}<li> {@link WildcardQuery}<li> {@link PhraseQuery}<li> {@link PrefixQuery}<li> {@link MultiPhraseQuery}<li> {@link FuzzyQuery}<li> {@link TermRangeQuery}<li> {@link NumericRangeQuery}<li> {@link org.apache.lucene.search.spans.SpanQuery}</ul><p>A parser for queries is contained in:<ul><li>{@link org.apache.lucene.queryParser.QueryParser QueryParser}</ul> */
end_comment
begin_class
DECL|class|Query
specifier|public
specifier|abstract
class|class
name|Query
implements|implements
name|Cloneable
block|{
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|// query boost factor
comment|/** Sets the boost for this query clause to<code>b</code>.  Documents    * matching this clause will (in addition to the normal weightings) have    * their score multiplied by<code>b</code>.    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|b
parameter_list|)
block|{
name|boost
operator|=
name|b
expr_stmt|;
block|}
comment|/** Gets the boost for this clause.  Documents matching    * this clause will (in addition to the normal weightings) have their score    * multiplied by<code>b</code>.   The boost is 1.0 by default.    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Prints a query to a string, with<code>field</code> assumed to be the     * default field and omitted.    *<p>The representation used is one that is supposed to be readable    * by {@link org.apache.lucene.queryParser.QueryParser QueryParser}. However,    * there are the following limitations:    *<ul>    *<li>If the query was created by the parser, the printed    *  representation may not be exactly what was parsed. For example,    *  characters that need to be escaped will be represented without    *  the required backslash.</li>    *<li>Some of the more complicated queries (e.g. span queries)    *  don't have a representation that can be parsed by QueryParser.</li>    *</ul>    */
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** Prints a query to a string. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/**    * Expert: Constructs an appropriate Weight implementation for this query.    *     *<p>    * Only implemented by primitive queries, which re-write to themselves.    */
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Expert: called to re-write queries into primitive queries. For example,    * a PrefixQuery will be rewritten into a BooleanQuery that consists    * of TermQuerys.    */
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
comment|/**    * Expert: adds all terms occurring in this query to the terms set. Only    * works if this query is in its {@link #rewrite rewritten} form.    *     * @throws UnsupportedOperationException if this query is not yet rewritten    */
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
comment|// needs to be implemented by query subclasses
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Returns a clone of this query. */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Clone not supported: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Query
name|other
init|=
operator|(
name|Query
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|boost
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
