begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight.custom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
operator|.
name|custom
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
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
name|analysis
operator|.
name|MockAnalyzer
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
name|analysis
operator|.
name|MockTokenFilter
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
name|analysis
operator|.
name|MockTokenizer
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
name|analysis
operator|.
name|TokenStream
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
name|Query
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
name|TermQuery
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
name|highlight
operator|.
name|Highlighter
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
name|highlight
operator|.
name|InvalidTokenOffsetsException
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
name|highlight
operator|.
name|QueryScorer
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
name|highlight
operator|.
name|SimpleFragmenter
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
name|highlight
operator|.
name|SimpleHTMLFormatter
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
name|highlight
operator|.
name|WeightedSpanTerm
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
name|highlight
operator|.
name|WeightedSpanTermExtractor
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Tests the extensibility of {@link WeightedSpanTermExtractor} and  * {@link QueryScorer} in a user defined package  */
end_comment
begin_class
DECL|class|HighlightCustomQueryTest
specifier|public
class|class
name|HighlightCustomQueryTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"contents"
decl_stmt|;
DECL|method|testHighlightCustomQuery
specifier|public
name|void
name|testHighlightCustomQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
name|String
name|s1
init|=
literal|"I call our world Flatland, not because we call it so,"
decl_stmt|;
comment|// Verify that a query against the default field results in text being
comment|// highlighted
comment|// regardless of the field name.
name|CustomQuery
name|q
init|=
operator|new
name|CustomQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
literal|"world"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|expected
init|=
literal|"I call our<B>world</B> Flatland, not because we call it so,"
decl_stmt|;
name|String
name|observed
init|=
name|highlightField
argument_list|(
name|q
argument_list|,
literal|"SOME_FIELD_NAME"
argument_list|,
name|s1
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Expected: \""
operator|+
name|expected
operator|+
literal|"\n"
operator|+
literal|"Observed: \""
operator|+
name|observed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Query in the default field results in text for *ANY* field being highlighted"
argument_list|,
name|expected
argument_list|,
name|observed
argument_list|)
expr_stmt|;
comment|// Verify that a query against a named field does not result in any
comment|// highlighting
comment|// when the query field name differs from the name of the field being
comment|// highlighted,
comment|// which in this example happens to be the default field name.
name|q
operator|=
operator|new
name|CustomQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"world"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|s1
expr_stmt|;
name|observed
operator|=
name|highlightField
argument_list|(
name|q
argument_list|,
name|FIELD_NAME
argument_list|,
name|s1
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Expected: \""
operator|+
name|expected
operator|+
literal|"\n"
operator|+
literal|"Observed: \""
operator|+
name|observed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Query in a named field does not result in highlighting when that field isn't in the query"
argument_list|,
name|s1
argument_list|,
name|highlightField
argument_list|(
name|q
argument_list|,
name|FIELD_NAME
argument_list|,
name|s1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method intended for use with    *<tt>testHighlightingWithDefaultField()</tt>    */
DECL|method|highlightField
specifier|private
name|String
name|highlightField
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
name|TokenStream
name|tokenStream
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|text
argument_list|)
decl_stmt|;
comment|// Assuming "<B>", "</B>" used to highlight
name|SimpleHTMLFormatter
name|formatter
init|=
operator|new
name|SimpleHTMLFormatter
argument_list|()
decl_stmt|;
name|MyQueryScorer
name|scorer
init|=
operator|new
name|MyQueryScorer
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|FIELD_NAME
argument_list|)
decl_stmt|;
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
name|formatter
argument_list|,
name|scorer
argument_list|)
decl_stmt|;
name|highlighter
operator|.
name|setTextFragmenter
argument_list|(
operator|new
name|SimpleFragmenter
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rv
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|tokenStream
argument_list|,
name|text
argument_list|,
literal|1
argument_list|,
literal|"(FIELD TEXT TRUNCATED)"
argument_list|)
decl_stmt|;
return|return
name|rv
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
name|text
else|:
name|rv
return|;
block|}
DECL|class|MyWeightedSpanTermExtractor
specifier|public
specifier|static
class|class
name|MyWeightedSpanTermExtractor
extends|extends
name|WeightedSpanTermExtractor
block|{
DECL|method|MyWeightedSpanTermExtractor
specifier|public
name|MyWeightedSpanTermExtractor
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|MyWeightedSpanTermExtractor
specifier|public
name|MyWeightedSpanTermExtractor
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|defaultField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractUnknownQuery
specifier|protected
name|void
name|extractUnknownQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|WeightedSpanTerm
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|instanceof
name|CustomQuery
condition|)
block|{
name|extractWeightedTerms
argument_list|(
name|terms
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|(
operator|(
name|CustomQuery
operator|)
name|query
operator|)
operator|.
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|MyQueryScorer
specifier|public
specifier|static
class|class
name|MyQueryScorer
extends|extends
name|QueryScorer
block|{
DECL|method|MyQueryScorer
specifier|public
name|MyQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermExtractor
specifier|protected
name|WeightedSpanTermExtractor
name|newTermExtractor
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
return|return
name|defaultField
operator|==
literal|null
condition|?
operator|new
name|MyWeightedSpanTermExtractor
argument_list|()
else|:
operator|new
name|MyWeightedSpanTermExtractor
argument_list|(
name|defaultField
argument_list|)
return|;
block|}
block|}
DECL|class|CustomQuery
specifier|public
specifier|static
class|class
name|CustomQuery
extends|extends
name|Query
block|{
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|method|CustomQuery
specifier|public
name|CustomQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
annotation|@
name|Override
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
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
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
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
return|;
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|term
operator|.
name|hashCode
argument_list|()
operator|)
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
name|CustomQuery
name|other
init|=
operator|(
name|CustomQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|term
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
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
block|}
end_class
end_unit
