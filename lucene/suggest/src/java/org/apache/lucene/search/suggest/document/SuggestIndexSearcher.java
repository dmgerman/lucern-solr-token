begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|LeafReader
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
name|LeafReaderContext
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
name|Terms
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
name|DocIdSet
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
name|Filter
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
name|IndexSearcher
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
name|automaton
operator|.
name|Automaton
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionFieldsProducer
operator|.
name|CompletionTerms
import|;
end_import
begin_comment
comment|/**  * Adds document suggest capabilities to IndexSearcher  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SuggestIndexSearcher
specifier|public
class|class
name|SuggestIndexSearcher
extends|extends
name|IndexSearcher
block|{
DECL|field|queryAnalyzer
specifier|private
specifier|final
name|Analyzer
name|queryAnalyzer
decl_stmt|;
comment|/**    * Creates a searcher with document suggest capabilities    * for<code>reader</code>.    *<p>    * Suggestion<code>key</code> is analyzed with<code>queryAnalyzer</code>    */
DECL|method|SuggestIndexSearcher
specifier|public
name|SuggestIndexSearcher
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryAnalyzer
operator|=
name|queryAnalyzer
expr_stmt|;
block|}
comment|/**    * Calls {@link #suggest(String, CharSequence, int, Filter)}    * with no document filter    */
DECL|method|suggest
specifier|public
name|TopSuggestDocs
name|suggest
parameter_list|(
name|String
name|field
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|suggest
argument_list|(
name|field
argument_list|,
name|key
argument_list|,
name|num
argument_list|,
operator|(
name|Filter
operator|)
literal|null
argument_list|)
return|;
block|}
comment|/**    * Calls {@link #suggest(String, CharSequence, int, Filter, TopSuggestDocsCollector)}    * with no document filter    */
DECL|method|suggest
specifier|public
name|void
name|suggest
parameter_list|(
name|String
name|field
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|int
name|num
parameter_list|,
name|TopSuggestDocsCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|suggest
argument_list|(
name|field
argument_list|,
name|key
argument_list|,
name|num
argument_list|,
literal|null
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Suggests at most<code>num</code> documents filtered by<code>filter</code>    * that completes to<code>key</code> for a suggest<code>field</code>    *<p>    * Returns at most Top<code>num</code> document ids with corresponding completion and weight pair    *    * @throws java.lang.IllegalArgumentException if<code>filter</code> does not provide a random access    *                                            interface or if<code>field</code> is not a {@link SuggestField}    */
DECL|method|suggest
specifier|public
name|TopSuggestDocs
name|suggest
parameter_list|(
name|String
name|field
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|int
name|num
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|TopSuggestDocsCollector
name|collector
init|=
operator|new
name|TopSuggestDocsCollector
argument_list|(
name|num
argument_list|)
decl_stmt|;
name|suggest
argument_list|(
name|field
argument_list|,
name|key
argument_list|,
name|num
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|collector
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Suggests at most<code>num</code> documents filtered by<code>filter</code>    * that completes to<code>key</code> for a suggest<code>field</code>    *<p>    * Collect completions with {@link TopSuggestDocsCollector}    * The completions are collected in order of the suggest<code>field</code> weight.    * There can be more than one collection of the same document, if the<code>key</code>    * matches multiple<code>field</code> values of the same document    *    * @throws java.lang.IllegalArgumentException if<code>filter</code> does not provide a random access    *                                            interface or if<code>field</code> is not a {@link SuggestField}    */
DECL|method|suggest
specifier|public
name|void
name|suggest
parameter_list|(
name|String
name|field
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|int
name|num
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|TopSuggestDocsCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
comment|// verify input
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'field' can not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|num
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'num' should be> 0"
argument_list|)
throw|;
block|}
if|if
condition|(
name|collector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'collector' can not be null"
argument_list|)
throw|;
block|}
comment|// build query automaton
name|CompletionAnalyzer
name|analyzer
decl_stmt|;
if|if
condition|(
name|queryAnalyzer
operator|instanceof
name|CompletionAnalyzer
condition|)
block|{
name|analyzer
operator|=
operator|(
name|CompletionAnalyzer
operator|)
name|queryAnalyzer
expr_stmt|;
block|}
else|else
block|{
name|analyzer
operator|=
operator|new
name|CompletionAnalyzer
argument_list|(
name|queryAnalyzer
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Automaton
name|automaton
init|=
name|analyzer
operator|.
name|toAutomaton
argument_list|(
name|field
argument_list|,
name|key
argument_list|)
decl_stmt|;
comment|// collect results
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
control|)
block|{
name|TopSuggestDocsCollector
name|leafCollector
init|=
operator|(
name|TopSuggestDocsCollector
operator|)
name|collector
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|NRTSuggester
name|suggester
decl_stmt|;
if|if
condition|(
name|terms
operator|instanceof
name|CompletionTerms
condition|)
block|{
name|CompletionTerms
name|completionTerms
init|=
operator|(
name|CompletionTerms
operator|)
name|terms
decl_stmt|;
name|suggester
operator|=
name|completionTerms
operator|.
name|suggester
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|field
operator|+
literal|" is not a SuggestField"
argument_list|)
throw|;
block|}
if|if
condition|(
name|suggester
operator|==
literal|null
condition|)
block|{
comment|// a segment can have a null suggester
comment|// i.e. no FST was built
continue|continue;
block|}
name|DocIdSet
name|docIdSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|docIdSet
operator|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
comment|// filter matches no docs in current leave
continue|continue;
block|}
block|}
name|suggester
operator|.
name|lookup
argument_list|(
name|reader
argument_list|,
name|automaton
argument_list|,
name|num
argument_list|,
name|docIdSet
argument_list|,
name|leafCollector
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
