begin_unit
begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
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
name|Tokenizer
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
name|core
operator|.
name|KeywordTokenizer
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
name|ngram
operator|.
name|EdgeNGramTokenFilter
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
name|reverse
operator|.
name|ReverseStringFilter
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
name|classification
operator|.
name|utils
operator|.
name|ConfusionMatrixGenerator
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
name|util
operator|.
name|BytesRef
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
begin_comment
comment|/**  * Testcase for {@link org.apache.lucene.classification.CachingNaiveBayesClassifier}  */
end_comment
begin_class
DECL|class|CachingNaiveBayesClassifierTest
specifier|public
class|class
name|CachingNaiveBayesClassifierTest
extends|extends
name|ClassificationTestBase
argument_list|<
name|BytesRef
argument_list|>
block|{
annotation|@
name|Test
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|leafReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|leafReader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
operator|new
name|CachingNaiveBayesClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
operator|new
name|CachingNaiveBayesClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
argument_list|,
name|POLITICS_INPUT
argument_list|,
name|POLITICS_RESULT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|leafReader
operator|!=
literal|null
condition|)
block|{
name|leafReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBasicUsageWithQuery
specifier|public
name|void
name|testBasicUsageWithQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|leafReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|leafReader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|textFieldName
argument_list|,
literal|"it"
argument_list|)
argument_list|)
decl_stmt|;
name|checkCorrectClassification
argument_list|(
operator|new
name|CachingNaiveBayesClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
name|query
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|leafReader
operator|!=
literal|null
condition|)
block|{
name|leafReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testNGramUsage
specifier|public
name|void
name|testNGramUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|leafReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|NGramAnalyzer
name|analyzer
init|=
operator|new
name|NGramAnalyzer
argument_list|()
decl_stmt|;
name|leafReader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
operator|new
name|CachingNaiveBayesClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|leafReader
operator|!=
literal|null
condition|)
block|{
name|leafReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|NGramAnalyzer
specifier|private
class|class
name|NGramAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ReverseStringFilter
argument_list|(
operator|new
name|EdgeNGramTokenFilter
argument_list|(
operator|new
name|ReverseStringFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPerformance
specifier|public
name|void
name|testPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|LeafReader
name|leafReader
init|=
name|getRandomIndex
argument_list|(
name|analyzer
argument_list|,
literal|100
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|trainStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|CachingNaiveBayesClassifier
name|simpleNaiveBayesClassifier
init|=
operator|new
name|CachingNaiveBayesClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|long
name|trainEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|trainTime
init|=
name|trainEnd
operator|-
name|trainStart
decl_stmt|;
name|assertTrue
argument_list|(
literal|"training took more than 10s: "
operator|+
name|trainTime
operator|/
literal|1000
operator|+
literal|"s"
argument_list|,
name|trainTime
operator|<
literal|10000
argument_list|)
expr_stmt|;
name|long
name|evaluationStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|leafReader
argument_list|,
name|simpleNaiveBayesClassifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
name|long
name|evaluationEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|evaluationTime
init|=
name|evaluationEnd
operator|-
name|evaluationStart
decl_stmt|;
name|assertTrue
argument_list|(
literal|"evaluation took more than 1m: "
operator|+
name|evaluationTime
operator|/
literal|1000
operator|+
literal|"s"
argument_list|,
name|evaluationTime
operator|<
literal|60000
argument_list|)
expr_stmt|;
name|double
name|avgClassificationTime
init|=
name|confusionMatrix
operator|.
name|getAvgClassificationTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|5000
operator|>
name|avgClassificationTime
argument_list|)
expr_stmt|;
name|double
name|accuracy
init|=
name|confusionMatrix
operator|.
name|getAccuracy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|accuracy
operator|>
literal|0d
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|leafReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
