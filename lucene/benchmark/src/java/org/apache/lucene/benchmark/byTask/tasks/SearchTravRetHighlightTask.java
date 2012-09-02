begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|document
operator|.
name|Document
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
name|StoredDocument
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
name|TextFragment
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
name|TokenSources
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_comment
comment|/**  * Search and Traverse and Retrieve docs task.  Highlight the fields in the retrieved documents.  *  * Uses the {@link org.apache.lucene.search.highlight.SimpleHTMLFormatter} for formatting.  *  *<p>Note: This task reuses the reader if it is already open.  * Otherwise a reader is opened at start and closed at the end.  *</p>  *  *<p>Takes optional multivalued, comma separated param string as: size[&lt;traversal size&gt;],highlight[&lt;int&gt;],maxFrags[&lt;int&gt;],mergeContiguous[&lt;boolean&gt;],fields[name1;name2;...]</p>  *<ul>  *<li>traversal size - The number of hits to traverse, otherwise all will be traversed</li>  *<li>highlight - The number of the hits to highlight.  Will always be less than or equal to traversal size.  Default is Integer.MAX_VALUE (i.e. hits.length())</li>  *<li>maxFrags - The maximum number of fragments to score by the highlighter</li>  *<li>mergeContiguous - true if contiguous fragments should be merged.</li>  *<li>fields - The fields to highlight.  If not specified all fields will be highlighted (or at least attempted)</li>  *</ul>  * Example:  *<pre>"SearchHlgtSameRdr" SearchTravRetHighlight(size[10],highlight[10],mergeContiguous[true],maxFrags[3],fields[body])> : 1000  *</pre>  *  * Documents must be stored in order for this task to work.  Additionally, term vector positions can be used as well.  *  *<p>Other side effects: counts additional 1 (record) for each traversed hit,  * and 1 more for each retrieved (non null) document and 1 for each fragment returned.</p>  */
end_comment
begin_class
DECL|class|SearchTravRetHighlightTask
specifier|public
class|class
name|SearchTravRetHighlightTask
extends|extends
name|SearchTravTask
block|{
DECL|field|numToHighlight
specifier|protected
name|int
name|numToHighlight
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|mergeContiguous
specifier|protected
name|boolean
name|mergeContiguous
decl_stmt|;
DECL|field|maxFrags
specifier|protected
name|int
name|maxFrags
init|=
literal|2
decl_stmt|;
DECL|field|paramFields
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|paramFields
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
DECL|field|highlighter
specifier|protected
name|Highlighter
name|highlighter
decl_stmt|;
DECL|field|maxDocCharsToAnalyze
specifier|protected
name|int
name|maxDocCharsToAnalyze
decl_stmt|;
DECL|method|SearchTravRetHighlightTask
specifier|public
name|SearchTravRetHighlightTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|//check to make sure either the doc is being stored
name|PerfRunData
name|data
init|=
name|getRunData
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"doc.stored"
argument_list|,
literal|false
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"doc.stored must be set to true"
argument_list|)
throw|;
block|}
name|maxDocCharsToAnalyze
operator|=
name|data
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"highlighter.maxDocCharsToAnalyze"
argument_list|,
name|Highlighter
operator|.
name|DEFAULT_MAX_CHARS_TO_ANALYZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|withRetrieve
specifier|public
name|boolean
name|withRetrieve
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|numToHighlight
specifier|public
name|int
name|numToHighlight
parameter_list|()
block|{
return|return
name|numToHighlight
return|;
block|}
annotation|@
name|Override
DECL|method|getBenchmarkHighlighter
specifier|protected
name|BenchmarkHighlighter
name|getBenchmarkHighlighter
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|highlighter
operator|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|highlighter
operator|.
name|setMaxDocCharsToAnalyze
argument_list|(
name|maxDocCharsToAnalyze
argument_list|)
expr_stmt|;
return|return
operator|new
name|BenchmarkHighlighter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|doHighlight
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|StoredDocument
name|document
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|TokenSources
operator|.
name|getAnyTokenStream
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|,
name|field
argument_list|,
name|document
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|TextFragment
index|[]
name|frag
init|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|ts
argument_list|,
name|text
argument_list|,
name|mergeContiguous
argument_list|,
name|maxFrags
argument_list|)
decl_stmt|;
return|return
name|frag
operator|!=
literal|null
condition|?
name|frag
operator|.
name|length
else|:
literal|0
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldsToHighlight
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldsToHighlight
parameter_list|(
name|StoredDocument
name|document
parameter_list|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|result
init|=
name|super
operator|.
name|getFieldsToHighlight
argument_list|(
name|document
argument_list|)
decl_stmt|;
comment|//if stored is false, then result will be empty, in which case just get all the param fields
if|if
condition|(
name|paramFields
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
operator|&&
name|result
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|result
operator|.
name|retainAll
argument_list|(
name|paramFields
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|paramFields
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
comment|// can't call super because super doesn't understand our
comment|// params syntax
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|String
index|[]
name|splits
init|=
name|params
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|splits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|splits
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"size["
argument_list|)
operator|==
literal|true
condition|)
block|{
name|traversalSize
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|splits
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"size["
operator|.
name|length
argument_list|()
argument_list|,
name|splits
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|splits
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"highlight["
argument_list|)
operator|==
literal|true
condition|)
block|{
name|numToHighlight
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|splits
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"highlight["
operator|.
name|length
argument_list|()
argument_list|,
name|splits
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|splits
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"maxFrags["
argument_list|)
operator|==
literal|true
condition|)
block|{
name|maxFrags
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|splits
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"maxFrags["
operator|.
name|length
argument_list|()
argument_list|,
name|splits
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|splits
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"mergeContiguous["
argument_list|)
operator|==
literal|true
condition|)
block|{
name|mergeContiguous
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|splits
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"mergeContiguous["
operator|.
name|length
argument_list|()
argument_list|,
name|splits
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|splits
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"fields["
argument_list|)
operator|==
literal|true
condition|)
block|{
name|paramFields
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|String
name|fieldNames
init|=
name|splits
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"fields["
operator|.
name|length
argument_list|()
argument_list|,
name|splits
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
index|[]
name|fieldSplits
init|=
name|fieldNames
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fieldSplits
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|paramFields
operator|.
name|add
argument_list|(
name|fieldSplits
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
