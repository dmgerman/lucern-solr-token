begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|en
operator|.
name|EnglishAnalyzer
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
name|index
operator|.
name|TermsEnum
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
name|similarities
operator|.
name|BM25Similarity
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
name|similarities
operator|.
name|LMDirichletSimilarity
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
comment|/**  * Testcase for {@link KNearestNeighborClassifier}  */
end_comment
begin_class
DECL|class|KNearestNeighborClassifierTest
specifier|public
class|class
name|KNearestNeighborClassifierTest
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
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
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
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
operator|new
name|LMDirichletSimilarity
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
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
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|resultDS
init|=
name|checkCorrectClassification
argument_list|(
operator|new
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
operator|new
name|BM25Similarity
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|,
literal|1
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
decl_stmt|;
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|resultLMS
init|=
name|checkCorrectClassification
argument_list|(
operator|new
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
operator|new
name|LMDirichletSimilarity
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|,
literal|1
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
decl_stmt|;
name|assertTrue
argument_list|(
name|resultDS
operator|.
name|getScore
argument_list|()
operator|!=
name|resultLMS
operator|.
name|getScore
argument_list|()
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
comment|/**    * This test is for the scenario where in the first topK results from the MLT query, we have the same number of results per class.    * But the results for a class have a better ranking in comparison with the results of the second class.    * So we would expect a greater score for the best ranked class.    *    * @throws Exception if any error happens    */
annotation|@
name|Test
DECL|method|testRankedClasses
specifier|public
name|void
name|testRankedClasses
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
name|Analyzer
name|analyzer
init|=
operator|new
name|EnglishAnalyzer
argument_list|()
decl_stmt|;
name|leafReader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|KNearestNeighborClassifier
name|knnClassifier
init|=
operator|new
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|classes
init|=
name|knnClassifier
operator|.
name|getClasses
argument_list|(
name|STRONG_TECHNOLOGY_INPUT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|classes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getScore
argument_list|()
operator|>
name|classes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getScore
argument_list|()
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
name|knnClassifier
argument_list|,
name|STRONG_TECHNOLOGY_INPUT
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
comment|/**    * This test is for the scenario where in the first topK results from the MLT query, we have less results    * for the expected class than the results for the bad class.    * But the results for the expected class have a better score in comparison with the results of the second class.    * So we would expect a greater score for the best ranked class.    *    * @throws Exception if any error happens    */
annotation|@
name|Test
DECL|method|testUnbalancedClasses
specifier|public
name|void
name|testUnbalancedClasses
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
name|Analyzer
name|analyzer
init|=
operator|new
name|EnglishAnalyzer
argument_list|()
decl_stmt|;
name|leafReader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|KNearestNeighborClassifier
name|knnClassifier
init|=
operator|new
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|classes
init|=
name|knnClassifier
operator|.
name|getClasses
argument_list|(
name|SUPER_STRONG_TECHNOLOGY_INPUT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|classes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getScore
argument_list|()
operator|>
name|classes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getScore
argument_list|()
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
name|knnClassifier
argument_list|,
name|SUPER_STRONG_TECHNOLOGY_INPUT
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
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
name|query
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
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
name|KNearestNeighborClassifier
name|kNearestNeighborClassifier
init|=
operator|new
name|KNearestNeighborClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
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
name|kNearestNeighborClassifier
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
literal|"evaluation took more than 2m: "
operator|+
name|evaluationTime
operator|/
literal|1000
operator|+
literal|"s"
argument_list|,
name|evaluationTime
operator|<
literal|120000
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
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|accuracy
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|recall
init|=
name|confusionMatrix
operator|.
name|getRecall
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|precision
init|=
name|confusionMatrix
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|categoryFieldName
argument_list|)
decl_stmt|;
name|TermsEnum
name|iterator
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|s
init|=
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|recall
operator|=
name|confusionMatrix
operator|.
name|getRecall
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|precision
operator|=
name|confusionMatrix
operator|.
name|getPrecision
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|f1Measure
init|=
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f1Measure
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f1Measure
operator|<=
literal|1d
argument_list|)
expr_stmt|;
block|}
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
