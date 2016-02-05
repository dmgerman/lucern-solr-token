begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.classification.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|document
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
comment|/**  * Tests for {@link org.apache.lucene.classification.KNearestNeighborClassifier}  */
end_comment
begin_class
DECL|class|KNearestNeighborDocumentClassifierTest
specifier|public
class|class
name|KNearestNeighborDocumentClassifierTest
extends|extends
name|DocumentClassificationTestBase
argument_list|<
name|BytesRef
argument_list|>
block|{
annotation|@
name|Test
DECL|method|testBasicDocumentClassification
specifier|public
name|void
name|testBasicDocumentClassification
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Document
name|videoGameDocument
init|=
name|getVideoGameDocument
argument_list|()
decl_stmt|;
name|Document
name|batmanDocument
init|=
name|getBatmanDocument
argument_list|()
decl_stmt|;
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|videoGameDocument
argument_list|,
name|VIDEOGAME_RESULT
argument_list|)
expr_stmt|;
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|batmanDocument
argument_list|,
name|BATMAN_RESULT
argument_list|)
expr_stmt|;
comment|// considering only the text we have wrong classification because the text was ambiguos on purpose
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|}
argument_list|)
argument_list|,
name|videoGameDocument
argument_list|,
name|BATMAN_RESULT
argument_list|)
expr_stmt|;
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|}
argument_list|)
argument_list|,
name|batmanDocument
argument_list|,
name|VIDEOGAME_RESULT
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
DECL|method|testBasicDocumentClassificationScore
specifier|public
name|void
name|testBasicDocumentClassificationScore
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Document
name|videoGameDocument
init|=
name|getVideoGameDocument
argument_list|()
decl_stmt|;
name|Document
name|batmanDocument
init|=
name|getBatmanDocument
argument_list|()
decl_stmt|;
name|double
name|score1
init|=
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|videoGameDocument
argument_list|,
name|VIDEOGAME_RESULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|score1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|double
name|score2
init|=
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|batmanDocument
argument_list|,
name|BATMAN_RESULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|score2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// considering only the text we have wrong classification because the text was ambiguos on purpose
name|double
name|score3
init|=
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|}
argument_list|)
argument_list|,
name|videoGameDocument
argument_list|,
name|BATMAN_RESULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|score3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|double
name|score4
init|=
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|}
argument_list|)
argument_list|,
name|batmanDocument
argument_list|,
name|VIDEOGAME_RESULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|score4
argument_list|,
literal|0
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
DECL|method|testBoostedDocumentClassification
specifier|public
name|void
name|testBoostedDocumentClassification
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
operator|+
literal|"^100"
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|getBatmanAmbiguosDocument
argument_list|()
argument_list|,
name|BATMAN_RESULT
argument_list|)
expr_stmt|;
comment|// considering without boost wrong classification will appear
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
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
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|getBatmanAmbiguosDocument
argument_list|()
argument_list|,
name|VIDEOGAME_RESULT
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
DECL|method|testBasicDocumentClassificationWithQuery
specifier|public
name|void
name|testBasicDocumentClassificationWithQuery
parameter_list|()
throws|throws
name|Exception
block|{
try|try
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
name|authorFieldName
argument_list|,
literal|"ign"
argument_list|)
argument_list|)
decl_stmt|;
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|query
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|categoryFieldName
argument_list|,
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|getVideoGameDocument
argument_list|()
argument_list|,
name|VIDEOGAME_RESULT
argument_list|)
expr_stmt|;
name|checkCorrectDocumentClassification
argument_list|(
operator|new
name|KNearestNeighborDocumentClassifier
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|query
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|categoryFieldName
argument_list|,
name|field2analyzer
argument_list|,
operator|new
name|String
index|[]
block|{
name|textFieldName
block|,
name|titleFieldName
block|,
name|authorFieldName
block|}
argument_list|)
argument_list|,
name|getBatmanDocument
argument_list|()
argument_list|,
name|VIDEOGAME_RESULT
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
block|}
end_class
end_unit
