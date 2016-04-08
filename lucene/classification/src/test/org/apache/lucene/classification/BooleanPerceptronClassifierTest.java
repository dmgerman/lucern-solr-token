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
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  * Testcase for {@link org.apache.lucene.classification.BooleanPerceptronClassifier}  */
end_comment
begin_class
DECL|class|BooleanPerceptronClassifierTest
specifier|public
class|class
name|BooleanPerceptronClassifierTest
extends|extends
name|ClassificationTestBase
argument_list|<
name|Boolean
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
name|BooleanPerceptronClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|booleanFieldName
argument_list|,
name|textFieldName
argument_list|)
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
literal|false
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
DECL|method|testExplicitThreshold
specifier|public
name|void
name|testExplicitThreshold
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
name|BooleanPerceptronClassifier
name|classifier
init|=
operator|new
name|BooleanPerceptronClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|50d
argument_list|,
name|booleanFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|checkCorrectClassification
argument_list|(
name|classifier
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
name|classifier
argument_list|,
name|POLITICS_INPUT
argument_list|,
literal|true
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
name|BooleanPerceptronClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
name|query
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|booleanFieldName
argument_list|,
name|textFieldName
argument_list|)
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
literal|false
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
name|BooleanPerceptronClassifier
name|classifier
init|=
operator|new
name|BooleanPerceptronClassifier
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|booleanFieldName
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
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
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
comment|// accuracy check disabled until LUCENE-6853 is fixed
comment|//      double accuracy = confusionMatrix.getAccuracy();
comment|//      assertTrue(accuracy> 0d);
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
