begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
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
name|feeds
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
operator|.
name|Occur
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
name|BooleanQuery
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
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
operator|.
name|NewAnalyzerTask
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
name|Version
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
begin_comment
comment|/**  * A QueryMaker that makes queries for a collection created   * using {@link org.apache.lucene.benchmark.byTask.feeds.SingleDocSource}.  */
end_comment
begin_class
DECL|class|SimpleQueryMaker
specifier|public
class|class
name|SimpleQueryMaker
extends|extends
name|AbstractQueryMaker
implements|implements
name|QueryMaker
block|{
comment|/**    * Prepare the queries for this test.    * Extending classes can override this method for preparing different queries.     * @return prepared queries.    * @throws Exception if cannot prepare the queries.    */
annotation|@
name|Override
DECL|method|prepareQueries
specifier|protected
name|Query
index|[]
name|prepareQueries
parameter_list|()
throws|throws
name|Exception
block|{
comment|// analyzer (default is standard analyzer)
name|Analyzer
name|anlzr
init|=
name|NewAnalyzerTask
operator|.
name|createAnalyzer
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"analyzer"
argument_list|,
literal|"org.apache.lucene.analysis.standard.StandardAnalyzer"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|anlzr
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Query
argument_list|>
name|qq
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|Query
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|DocMaker
operator|.
name|ID_FIELD
argument_list|,
literal|"doc2"
argument_list|)
argument_list|)
decl_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|q1
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|,
literal|"simple"
argument_list|)
argument_list|)
decl_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|q2
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|bq
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"synthetic body"
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"\"synthetic body\""
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"synthetic text"
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"\"synthetic text\""
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"\"synthetic text\"~3"
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"zoom*"
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"synth*"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|qq
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class
end_unit
