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
name|List
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|QueryMaker
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
name|DirectoryReader
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
name|IndexableField
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
name|MultiFields
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
name|Collector
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
name|TopDocs
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
name|MultiTermQuery
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
name|TopFieldCollector
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
name|ScoreDoc
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
name|TopScoreDocCollector
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
name|Sort
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
name|store
operator|.
name|Directory
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
name|Bits
import|;
end_import
begin_comment
comment|/**  * Read index (abstract) task.  * Sub classes implement withSearch(), withWarm(), withTraverse() and withRetrieve()  * methods to configure the actual action.  *<p/>  *<p>Note: All ReadTasks reuse the reader if it is already open.  * Otherwise a reader is opened at start and closed at the end.  *<p>  * The<code>search.num.hits</code> config parameter sets  * the top number of hits to collect during searching.  If  *<code>print.hits.field</code> is set, then each hit is  * printed along with the value of that field.</p>  *  *<p>Other side effects: none.  */
end_comment
begin_class
DECL|class|ReadTask
specifier|public
specifier|abstract
class|class
name|ReadTask
extends|extends
name|PerfTask
block|{
DECL|field|queryMaker
specifier|private
specifier|final
name|QueryMaker
name|queryMaker
decl_stmt|;
DECL|method|ReadTask
specifier|public
name|ReadTask
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
if|if
condition|(
name|withSearch
argument_list|()
condition|)
block|{
name|queryMaker
operator|=
name|getQueryMaker
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queryMaker
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|res
init|=
literal|0
decl_stmt|;
comment|// open reader or use existing one
name|IndexSearcher
name|searcher
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexSearcher
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
decl_stmt|;
specifier|final
name|boolean
name|closeSearcher
decl_stmt|;
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
comment|// open our own reader
name|Directory
name|dir
init|=
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|closeSearcher
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// use existing one; this passes +1 ref to us
name|reader
operator|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
name|closeSearcher
operator|=
literal|false
expr_stmt|;
block|}
comment|// optionally warm and add num docs traversed to count
if|if
condition|(
name|withWarm
argument_list|()
condition|)
block|{
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|m
operator|++
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|liveDocs
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|m
argument_list|)
condition|)
block|{
name|doc
operator|=
name|reader
operator|.
name|document
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|res
operator|+=
operator|(
name|doc
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|withSearch
argument_list|()
condition|)
block|{
name|res
operator|++
expr_stmt|;
name|Query
name|q
init|=
name|queryMaker
operator|.
name|makeQuery
argument_list|()
decl_stmt|;
name|Sort
name|sort
init|=
name|getSort
argument_list|()
decl_stmt|;
name|TopDocs
name|hits
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numHits
init|=
name|numHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|numHits
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|withCollector
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
comment|// TODO: instead of always passing false we
comment|// should detect based on the query; if we make
comment|// the IndexSearcher search methods that take
comment|// Weight public again, we can go back to
comment|// pulling the Weight ourselves:
name|TopFieldCollector
name|collector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
literal|true
argument_list|,
name|withScore
argument_list|()
argument_list|,
name|withMaxScore
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|hits
operator|=
name|collector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Collector
name|collector
init|=
name|createCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|collector
argument_list|)
expr_stmt|;
comment|//hits = collector.topDocs();
block|}
specifier|final
name|String
name|printHitsField
init|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"print.hits.field"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|hits
operator|!=
literal|null
operator|&&
name|printHitsField
operator|!=
literal|null
operator|&&
name|printHitsField
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"totalHits = "
operator|+
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"maxDoc()  = "
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"numDocs() = "
operator|+
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|docID
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|i
operator|+
literal|": doc="
operator|+
name|docID
operator|+
literal|" score="
operator|+
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|" "
operator|+
name|printHitsField
operator|+
literal|" ="
operator|+
name|doc
operator|.
name|get
argument_list|(
name|printHitsField
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|withTraverse
argument_list|()
condition|)
block|{
specifier|final
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|hits
operator|.
name|scoreDocs
decl_stmt|;
name|int
name|traversalSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|scoreDocs
operator|.
name|length
argument_list|,
name|traversalSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|traversalSize
operator|>
literal|0
condition|)
block|{
name|boolean
name|retrieve
init|=
name|withRetrieve
argument_list|()
decl_stmt|;
name|int
name|numHighlight
init|=
name|Math
operator|.
name|min
argument_list|(
name|numToHighlight
argument_list|()
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|getRunData
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|BenchmarkHighlighter
name|highlighter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|numHighlight
operator|>
literal|0
condition|)
block|{
name|highlighter
operator|=
name|getBenchmarkHighlighter
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|traversalSize
condition|;
name|m
operator|++
control|)
block|{
name|int
name|id
init|=
name|scoreDocs
index|[
name|m
index|]
operator|.
name|doc
decl_stmt|;
name|res
operator|++
expr_stmt|;
if|if
condition|(
name|retrieve
condition|)
block|{
name|Document
name|document
init|=
name|retrieveDoc
argument_list|(
name|reader
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|res
operator|+=
name|document
operator|!=
literal|null
condition|?
literal|1
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|numHighlight
operator|>
literal|0
operator|&&
name|m
operator|<
name|numHighlight
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|fieldsToHighlight
init|=
name|getFieldsToHighlight
argument_list|(
name|document
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|field
range|:
name|fieldsToHighlight
control|)
block|{
name|String
name|text
init|=
name|document
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|res
operator|+=
name|highlighter
operator|.
name|doHighlight
argument_list|(
name|reader
argument_list|,
name|id
argument_list|,
name|field
argument_list|,
name|document
argument_list|,
name|analyzer
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|closeSearcher
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Release our +1 ref from above
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|createCollector
specifier|protected
name|Collector
name|createCollector
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numHits
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|retrieveDoc
specifier|protected
name|Document
name|retrieveDoc
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Return query maker used for this task.    */
DECL|method|getQueryMaker
specifier|public
specifier|abstract
name|QueryMaker
name|getQueryMaker
parameter_list|()
function_decl|;
comment|/**    * Return true if search should be performed.    */
DECL|method|withSearch
specifier|public
specifier|abstract
name|boolean
name|withSearch
parameter_list|()
function_decl|;
DECL|method|withCollector
specifier|public
name|boolean
name|withCollector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Return true if warming should be performed.    */
DECL|method|withWarm
specifier|public
specifier|abstract
name|boolean
name|withWarm
parameter_list|()
function_decl|;
comment|/**    * Return true if, with search, results should be traversed.    */
DECL|method|withTraverse
specifier|public
specifier|abstract
name|boolean
name|withTraverse
parameter_list|()
function_decl|;
comment|/** Whether scores should be computed (only useful with    *  field sort) */
DECL|method|withScore
specifier|public
name|boolean
name|withScore
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Whether maxScores should be computed (only useful with    *  field sort) */
DECL|method|withMaxScore
specifier|public
name|boolean
name|withMaxScore
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Specify the number of hits to traverse.  Tasks should override this if they want to restrict the number    * of hits that are traversed when {@link #withTraverse()} is true. Must be greater than 0.    *<p/>    * Read task calculates the traversal as: Math.min(hits.length(), traversalSize())    *    * @return Integer.MAX_VALUE    */
DECL|method|traversalSize
specifier|public
name|int
name|traversalSize
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
DECL|field|DEFAULT_SEARCH_NUM_HITS
specifier|static
specifier|final
name|int
name|DEFAULT_SEARCH_NUM_HITS
init|=
literal|10
decl_stmt|;
DECL|field|numHits
specifier|private
name|int
name|numHits
decl_stmt|;
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
name|numHits
operator|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"search.num.hits"
argument_list|,
name|DEFAULT_SEARCH_NUM_HITS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Specify the number of hits to retrieve.  Tasks should override this if they want to restrict the number    * of hits that are collected during searching. Must be greater than 0.    *    * @return 10 by default, or search.num.hits config if set.    */
DECL|method|numHits
specifier|public
name|int
name|numHits
parameter_list|()
block|{
return|return
name|numHits
return|;
block|}
comment|/**    * Return true if, with search& results traversing, docs should be retrieved.    */
DECL|method|withRetrieve
specifier|public
specifier|abstract
name|boolean
name|withRetrieve
parameter_list|()
function_decl|;
comment|/**    * Set to the number of documents to highlight.    *    * @return The number of the results to highlight.  O means no docs will be highlighted.    */
DECL|method|numToHighlight
specifier|public
name|int
name|numToHighlight
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Return an appropriate highlighter to be used with    * highlighting tasks    */
DECL|method|getBenchmarkHighlighter
specifier|protected
name|BenchmarkHighlighter
name|getBenchmarkHighlighter
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|getSort
specifier|protected
name|Sort
name|getSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Define the fields to highlight.  Base implementation returns all fields    * @param document The Document    * @return A Collection of Field names (Strings)    */
DECL|method|getFieldsToHighlight
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldsToHighlight
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
init|=
name|document
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|IndexableField
name|f
range|:
name|fields
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
