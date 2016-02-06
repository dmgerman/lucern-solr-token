begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|LevensteinDistance
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
name|spell
operator|.
name|NGramDistance
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
name|spell
operator|.
name|StringDistance
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|ConjunctionSolrSpellCheckerTest
specifier|public
class|class
name|ConjunctionSolrSpellCheckerTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|ConjunctionSolrSpellChecker
name|cssc
init|=
operator|new
name|ConjunctionSolrSpellChecker
argument_list|()
decl_stmt|;
name|MockSolrSpellChecker
name|levenstein1
init|=
operator|new
name|MockSolrSpellChecker
argument_list|(
operator|new
name|LevensteinDistance
argument_list|()
argument_list|)
decl_stmt|;
name|MockSolrSpellChecker
name|levenstein2
init|=
operator|new
name|MockSolrSpellChecker
argument_list|(
operator|new
name|LevensteinDistance
argument_list|()
argument_list|)
decl_stmt|;
name|MockSolrSpellChecker
name|ngram
init|=
operator|new
name|MockSolrSpellChecker
argument_list|(
operator|new
name|NGramDistance
argument_list|()
argument_list|)
decl_stmt|;
name|cssc
operator|.
name|addChecker
argument_list|(
name|levenstein1
argument_list|)
expr_stmt|;
name|cssc
operator|.
name|addChecker
argument_list|(
name|levenstein2
argument_list|)
expr_stmt|;
try|try
block|{
name|cssc
operator|.
name|addChecker
argument_list|(
name|ngram
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ConjunctionSolrSpellChecker should have thrown an exception about non-identical StringDistances."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// correct behavior
block|}
block|}
DECL|class|MockSolrSpellChecker
class|class
name|MockSolrSpellChecker
extends|extends
name|SolrSpellChecker
block|{
DECL|field|sd
specifier|final
name|StringDistance
name|sd
decl_stmt|;
DECL|method|MockSolrSpellChecker
name|MockSolrSpellChecker
parameter_list|(
name|StringDistance
name|sd
parameter_list|)
block|{
name|this
operator|.
name|sd
operator|=
name|sd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStringDistance
specifier|protected
name|StringDistance
name|getStringDistance
parameter_list|()
block|{
return|return
name|sd
return|;
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|getSuggestions
specifier|public
name|SpellingResult
name|getSuggestions
parameter_list|(
name|SpellingOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
