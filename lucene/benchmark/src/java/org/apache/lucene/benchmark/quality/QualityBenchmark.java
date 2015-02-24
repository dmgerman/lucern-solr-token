begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.quality
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
package|;
end_package
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
name|io
operator|.
name|PrintWriter
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
name|quality
operator|.
name|utils
operator|.
name|DocNameExtractor
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
name|quality
operator|.
name|utils
operator|.
name|SubmissionReport
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
name|TopDocs
import|;
end_import
begin_comment
comment|/**  * Main entry point for running a quality benchmark.  *<p>  * There are two main configurations for running a quality benchmark:<ul>  *<li>Against existing judgements.</li>  *<li>For submission (e.g. for a contest).</li>  *</ul>  * The first configuration requires a non null  * {@link org.apache.lucene.benchmark.quality.Judge Judge}.   * The second configuration requires a non null   * {@link org.apache.lucene.benchmark.quality.utils.SubmissionReport SubmissionLogger}.  */
end_comment
begin_class
DECL|class|QualityBenchmark
specifier|public
class|class
name|QualityBenchmark
block|{
comment|/** Quality Queries that this quality benchmark would execute. */
DECL|field|qualityQueries
specifier|protected
name|QualityQuery
name|qualityQueries
index|[]
decl_stmt|;
comment|/** Parser for turning QualityQueries into Lucene Queries. */
DECL|field|qqParser
specifier|protected
name|QualityQueryParser
name|qqParser
decl_stmt|;
comment|/** Index to be searched. */
DECL|field|searcher
specifier|protected
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/** index field to extract doc name for each search result; used for judging the results. */
DECL|field|docNameField
specifier|protected
name|String
name|docNameField
decl_stmt|;
comment|/** maximal number of queries that this quality benchmark runs. Default: maxint. Useful for debugging. */
DECL|field|maxQueries
specifier|private
name|int
name|maxQueries
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** maximal number of results to collect for each query. Default: 1000. */
DECL|field|maxResults
specifier|private
name|int
name|maxResults
init|=
literal|1000
decl_stmt|;
comment|/**    * Create a QualityBenchmark.    * @param qqs quality queries to run.    * @param qqParser parser for turning QualityQueries into Lucene Queries.     * @param searcher index to be searched.    * @param docNameField name of field containing the document name.    *        This allows to extract the doc name for search results,    *        and is important for judging the results.      */
DECL|method|QualityBenchmark
specifier|public
name|QualityBenchmark
parameter_list|(
name|QualityQuery
name|qqs
index|[]
parameter_list|,
name|QualityQueryParser
name|qqParser
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|String
name|docNameField
parameter_list|)
block|{
name|this
operator|.
name|qualityQueries
operator|=
name|qqs
expr_stmt|;
name|this
operator|.
name|qqParser
operator|=
name|qqParser
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|docNameField
operator|=
name|docNameField
expr_stmt|;
block|}
comment|/**    * Run the quality benchmark.    * @param judge the judge that can tell if a certain result doc is relevant for a certain quality query.     *        If null, no judgements would be made. Usually null for a submission run.     * @param submitRep submission report is created if non null.    * @param qualityLog If not null, quality run data would be printed for each query.    * @return QualityStats of each quality query that was executed.    * @throws Exception if quality benchmark failed to run.    */
DECL|method|execute
specifier|public
name|QualityStats
index|[]
name|execute
parameter_list|(
name|Judge
name|judge
parameter_list|,
name|SubmissionReport
name|submitRep
parameter_list|,
name|PrintWriter
name|qualityLog
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|nQueries
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxQueries
argument_list|,
name|qualityQueries
operator|.
name|length
argument_list|)
decl_stmt|;
name|QualityStats
name|stats
index|[]
init|=
operator|new
name|QualityStats
index|[
name|nQueries
index|]
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
name|nQueries
condition|;
name|i
operator|++
control|)
block|{
name|QualityQuery
name|qq
init|=
name|qualityQueries
index|[
name|i
index|]
decl_stmt|;
comment|// generate query
name|Query
name|q
init|=
name|qqParser
operator|.
name|parse
argument_list|(
name|qq
argument_list|)
decl_stmt|;
comment|// search with this query
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|maxResults
argument_list|)
decl_stmt|;
name|long
name|searchTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t1
decl_stmt|;
comment|//most likely we either submit or judge, but check both
if|if
condition|(
name|judge
operator|!=
literal|null
condition|)
block|{
name|stats
index|[
name|i
index|]
operator|=
name|analyzeQueryResults
argument_list|(
name|qq
argument_list|,
name|q
argument_list|,
name|td
argument_list|,
name|judge
argument_list|,
name|qualityLog
argument_list|,
name|searchTime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|submitRep
operator|!=
literal|null
condition|)
block|{
name|submitRep
operator|.
name|report
argument_list|(
name|qq
argument_list|,
name|td
argument_list|,
name|docNameField
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|submitRep
operator|!=
literal|null
condition|)
block|{
name|submitRep
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
comment|/* Analyze/judge results for a single quality query; optionally log them. */
DECL|method|analyzeQueryResults
specifier|private
name|QualityStats
name|analyzeQueryResults
parameter_list|(
name|QualityQuery
name|qq
parameter_list|,
name|Query
name|q
parameter_list|,
name|TopDocs
name|td
parameter_list|,
name|Judge
name|judge
parameter_list|,
name|PrintWriter
name|logger
parameter_list|,
name|long
name|searchTime
parameter_list|)
throws|throws
name|IOException
block|{
name|QualityStats
name|stts
init|=
operator|new
name|QualityStats
argument_list|(
name|judge
operator|.
name|maxRecall
argument_list|(
name|qq
argument_list|)
argument_list|,
name|searchTime
argument_list|)
decl_stmt|;
name|ScoreDoc
name|sd
index|[]
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// extraction of first doc name we measure also construction of doc name extractor, just in case.
name|DocNameExtractor
name|xt
init|=
operator|new
name|DocNameExtractor
argument_list|(
name|docNameField
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
name|sd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|docName
init|=
name|xt
operator|.
name|docName
argument_list|(
name|searcher
argument_list|,
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|long
name|docNameExtractTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t1
decl_stmt|;
name|t1
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|boolean
name|isRelevant
init|=
name|judge
operator|.
name|isRelevant
argument_list|(
name|docName
argument_list|,
name|qq
argument_list|)
decl_stmt|;
name|stts
operator|.
name|addResult
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|isRelevant
argument_list|,
name|docNameExtractTime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|println
argument_list|(
name|qq
operator|.
name|getQueryID
argument_list|()
operator|+
literal|"  -  "
operator|+
name|q
argument_list|)
expr_stmt|;
name|stts
operator|.
name|log
argument_list|(
name|qq
operator|.
name|getQueryID
argument_list|()
operator|+
literal|" Stats:"
argument_list|,
literal|1
argument_list|,
name|logger
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
block|}
return|return
name|stts
return|;
block|}
comment|/**    * @return the maximum number of quality queries to run. Useful at debugging.    */
DECL|method|getMaxQueries
specifier|public
name|int
name|getMaxQueries
parameter_list|()
block|{
return|return
name|maxQueries
return|;
block|}
comment|/**    * Set the maximum number of quality queries to run. Useful at debugging.    */
DECL|method|setMaxQueries
specifier|public
name|void
name|setMaxQueries
parameter_list|(
name|int
name|maxQueries
parameter_list|)
block|{
name|this
operator|.
name|maxQueries
operator|=
name|maxQueries
expr_stmt|;
block|}
comment|/**    * @return the maximum number of results to collect for each quality query.    */
DECL|method|getMaxResults
specifier|public
name|int
name|getMaxResults
parameter_list|()
block|{
return|return
name|maxResults
return|;
block|}
comment|/**    * set the maximum number of results to collect for each quality query.    */
DECL|method|setMaxResults
specifier|public
name|void
name|setMaxResults
parameter_list|(
name|int
name|maxResults
parameter_list|)
block|{
name|this
operator|.
name|maxResults
operator|=
name|maxResults
expr_stmt|;
block|}
block|}
end_class
end_unit
