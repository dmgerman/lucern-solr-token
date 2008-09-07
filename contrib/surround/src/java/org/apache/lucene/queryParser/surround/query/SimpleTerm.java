begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|Query
import|;
end_import
begin_class
DECL|class|SimpleTerm
specifier|public
specifier|abstract
class|class
name|SimpleTerm
extends|extends
name|SrndQuery
implements|implements
name|DistanceSubQuery
implements|,
name|Comparable
block|{
DECL|method|SimpleTerm
specifier|public
name|SimpleTerm
parameter_list|(
name|boolean
name|q
parameter_list|)
block|{
name|quoted
operator|=
name|q
expr_stmt|;
block|}
DECL|field|quoted
specifier|private
name|boolean
name|quoted
decl_stmt|;
DECL|method|isQuoted
name|boolean
name|isQuoted
parameter_list|()
block|{
return|return
name|quoted
return|;
block|}
DECL|method|getQuote
specifier|public
name|String
name|getQuote
parameter_list|()
block|{
return|return
literal|"\""
return|;
block|}
DECL|method|getFieldOperator
specifier|public
name|String
name|getFieldOperator
parameter_list|()
block|{
return|return
literal|"/"
return|;
block|}
DECL|method|toStringUnquoted
specifier|public
specifier|abstract
name|String
name|toStringUnquoted
parameter_list|()
function_decl|;
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
comment|/* for ordering terms and prefixes before using an index, not used */
name|SimpleTerm
name|ost
init|=
operator|(
name|SimpleTerm
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|toStringUnquoted
argument_list|()
operator|.
name|compareTo
argument_list|(
name|ost
operator|.
name|toStringUnquoted
argument_list|()
argument_list|)
return|;
block|}
DECL|method|suffixToString
specifier|protected
name|void
name|suffixToString
parameter_list|(
name|StringBuffer
name|r
parameter_list|)
block|{
empty_stmt|;
block|}
comment|/* override for prefix query */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|r
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|isQuoted
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|getQuote
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|toStringUnquoted
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isQuoted
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|getQuote
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|suffixToString
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|weightToString
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|visitMatchingTerms
specifier|public
specifier|abstract
name|void
name|visitMatchingTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|MatchingTermVisitor
name|mtv
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|interface|MatchingTermVisitor
specifier|public
interface|interface
name|MatchingTermVisitor
block|{
DECL|method|visitMatchingTerm
name|void
name|visitMatchingTerm
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|distanceSubQueryNotAllowed
specifier|public
name|String
name|distanceSubQueryNotAllowed
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|makeLuceneQueryFieldNoBoost
specifier|public
name|Query
name|makeLuceneQueryFieldNoBoost
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
return|return
operator|new
name|Query
argument_list|()
block|{
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|fn
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|fieldName
operator|+
literal|" ("
operator|+
name|fn
operator|+
literal|"?)"
return|;
block|}
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
specifier|final
name|List
name|luceneSubQueries
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|visitMatchingTerms
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
operator|new
name|MatchingTermVisitor
argument_list|()
block|{
specifier|public
name|void
name|visitMatchingTerm
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|luceneSubQueries
operator|.
name|add
argument_list|(
name|qf
operator|.
name|newTermQuery
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|(
name|luceneSubQueries
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|SrndQuery
operator|.
name|theEmptyLcnQuery
else|:
operator|(
name|luceneSubQueries
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
operator|(
name|Query
operator|)
name|luceneSubQueries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
name|SrndBooleanQuery
operator|.
name|makeBooleanQuery
argument_list|(
comment|/* luceneSubQueries all have default weight */
name|luceneSubQueries
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
return|;
comment|/* OR the subquery terms */
block|}
block|}
return|;
block|}
DECL|method|addSpanQueries
specifier|public
name|void
name|addSpanQueries
parameter_list|(
specifier|final
name|SpanNearClauseFactory
name|sncf
parameter_list|)
throws|throws
name|IOException
block|{
name|visitMatchingTerms
argument_list|(
name|sncf
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|sncf
operator|.
name|getFieldName
argument_list|()
argument_list|,
operator|new
name|MatchingTermVisitor
argument_list|()
block|{
specifier|public
name|void
name|visitMatchingTerm
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|sncf
operator|.
name|addTermWeighted
argument_list|(
name|term
argument_list|,
name|getWeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
