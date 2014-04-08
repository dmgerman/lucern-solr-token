begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|document
operator|.
name|DoubleField
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
name|Field
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
name|FloatField
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
name|IntField
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
name|LongField
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
name|StringField
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
name|TextField
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
name|IndexWriterConfig
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
name|RandomIndexWriter
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|BytesRefFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ConstValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DivFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DocFreqValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DoubleConstValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DoubleFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|FloatFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|IDFValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|IfFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|IntFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|JoinDocFreqValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LinearFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LiteralValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LongFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|MaxDocValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|MaxFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|MinFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|NormValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|NumDocsValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|PowFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ProductFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|QueryValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|RangeMapFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ReciprocalFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ScaleFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|SumFloatFunction
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|SumTotalTermFreqValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|TFValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|TermFreqValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|TotalTermFreqValueSource
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
name|CheckHits
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
name|search
operator|.
name|SortField
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|Similarity
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
name|BytesRef
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
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|// TODO: add separate docvalues test
end_comment
begin_comment
comment|/**  * barebones tests for function queries.  */
end_comment
begin_class
DECL|class|TestValueSources
specifier|public
class|class
name|TestValueSources
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|documents
specifier|static
specifier|final
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|documents
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
index|[]
block|{
comment|/*             id,  double, float, int,  long,   string, text */
operator|new
name|String
index|[]
block|{
literal|"0"
block|,
literal|"3.63"
block|,
literal|"5.2"
block|,
literal|"35"
block|,
literal|"4343"
block|,
literal|"test"
block|,
literal|"this is a test test test"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"5.65"
block|,
literal|"9.3"
block|,
literal|"54"
block|,
literal|"1954"
block|,
literal|"bar"
block|,
literal|"second test"
block|}
block|,   }
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwConfig
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwConfig
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|Field
name|doubleField
init|=
operator|new
name|DoubleField
argument_list|(
literal|"double"
argument_list|,
literal|0d
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|doubleField
argument_list|)
expr_stmt|;
name|Field
name|floatField
init|=
operator|new
name|FloatField
argument_list|(
literal|"float"
argument_list|,
literal|0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|floatField
argument_list|)
expr_stmt|;
name|Field
name|intField
init|=
operator|new
name|IntField
argument_list|(
literal|"int"
argument_list|,
literal|0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|intField
argument_list|)
expr_stmt|;
name|Field
name|longField
init|=
operator|new
name|LongField
argument_list|(
literal|"long"
argument_list|,
literal|0L
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|longField
argument_list|)
expr_stmt|;
name|Field
name|stringField
init|=
operator|new
name|StringField
argument_list|(
literal|"string"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|stringField
argument_list|)
expr_stmt|;
name|Field
name|textField
init|=
operator|new
name|TextField
argument_list|(
literal|"text"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|textField
argument_list|)
expr_stmt|;
for|for
control|(
name|String
index|[]
name|doc
range|:
name|documents
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|doc
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|doubleField
operator|.
name|setDoubleValue
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
name|doc
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|floatField
operator|.
name|setFloatValue
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
name|doc
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|intField
operator|.
name|setIntValue
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|doc
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|longField
operator|.
name|setLongValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|doc
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|stringField
operator|.
name|setStringValue
argument_list|(
name|doc
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
name|textField
operator|.
name|setStringValue
argument_list|(
name|doc
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testConst
specifier|public
name|void
name|testConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|0.3f
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0.3f
block|,
literal|0.3f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDiv
specifier|public
name|void
name|testDiv
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|DivFloatFunction
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|10f
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|5f
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|2f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocFreq
specifier|public
name|void
name|testDocFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|DocFreqValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"text"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|2f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleConst
specifier|public
name|void
name|testDoubleConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|DoubleConstValueSource
argument_list|(
literal|0.3d
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0.3f
block|,
literal|0.3f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDouble
specifier|public
name|void
name|testDouble
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|DoubleFieldSource
argument_list|(
literal|"double"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|3.63f
block|,
literal|5.65f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloat
specifier|public
name|void
name|testFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|FloatFieldSource
argument_list|(
literal|"float"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|5.2f
block|,
literal|9.3f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testIDF
specifier|public
name|void
name|testIDF
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|saved
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
try|try
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|IDFValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"text"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0.5945349f
block|,
literal|0.5945349f
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|saved
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIf
specifier|public
name|void
name|testIf
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|IfFunction
argument_list|(
operator|new
name|BytesRefFieldSource
argument_list|(
literal|"id"
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|1.0f
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1f
block|,
literal|1f
block|}
argument_list|)
expr_stmt|;
comment|// true just if a value exists...
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|IfFunction
argument_list|(
operator|new
name|LiteralValueSource
argument_list|(
literal|"false"
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|1.0f
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1f
block|,
literal|1f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testInt
specifier|public
name|void
name|testInt
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|IntFieldSource
argument_list|(
literal|"int"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|35f
block|,
literal|54f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testJoinDocFreq
specifier|public
name|void
name|testJoinDocFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|JoinDocFreqValueSource
argument_list|(
literal|"string"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|0f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinearFloat
specifier|public
name|void
name|testLinearFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|LinearFloatFunction
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|2.0f
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|7f
block|,
literal|7f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLong
specifier|public
name|void
name|testLong
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|LongFieldSource
argument_list|(
literal|"long"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4343f
block|,
literal|1954f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxDoc
specifier|public
name|void
name|testMaxDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|MaxDocValueSource
argument_list|()
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|2f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxFloat
specifier|public
name|void
name|testMaxFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|MaxFloatFunction
argument_list|(
operator|new
name|ValueSource
index|[]
block|{
operator|new
name|ConstValueSource
argument_list|(
literal|1f
argument_list|)
block|,
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|2f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinFloat
specifier|public
name|void
name|testMinFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|MinFloatFunction
argument_list|(
operator|new
name|ValueSource
index|[]
block|{
operator|new
name|ConstValueSource
argument_list|(
literal|1f
argument_list|)
block|,
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1f
block|,
literal|1f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNorm
specifier|public
name|void
name|testNorm
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|saved
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
try|try
block|{
comment|// no norm field (so agnostic to indexed similarity)
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|NormValueSource
argument_list|(
literal|"byte"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0f
block|,
literal|0f
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|saved
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNumDocs
specifier|public
name|void
name|testNumDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|NumDocsValueSource
argument_list|()
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|2f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testPow
specifier|public
name|void
name|testPow
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|PowFloatFunction
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|3f
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|8f
block|,
literal|8f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testProduct
specifier|public
name|void
name|testProduct
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ProductFloatFunction
argument_list|(
operator|new
name|ValueSource
index|[]
block|{
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
block|,
operator|new
name|ConstValueSource
argument_list|(
literal|3f
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|6f
block|,
literal|6f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testQuery
specifier|public
name|void
name|testQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|QueryValueSource
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
argument_list|)
argument_list|,
literal|0f
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2f
block|,
literal|2f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeMap
specifier|public
name|void
name|testRangeMap
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|RangeMapFloatFunction
argument_list|(
operator|new
name|FloatFieldSource
argument_list|(
literal|"float"
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|0f
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1f
block|,
literal|0f
block|}
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|RangeMapFloatFunction
argument_list|(
operator|new
name|FloatFieldSource
argument_list|(
literal|"float"
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
operator|new
name|SumFloatFunction
argument_list|(
operator|new
name|ValueSource
index|[]
block|{
operator|new
name|ConstValueSource
argument_list|(
literal|1f
argument_list|)
block|,
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
block|}
argument_list|)
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|11f
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|3f
block|,
literal|11f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReciprocal
specifier|public
name|void
name|testReciprocal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ReciprocalFloatFunction
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0.1f
block|,
literal|0.1f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testScale
specifier|public
name|void
name|testScale
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ScaleFloatFunction
argument_list|(
operator|new
name|IntFieldSource
argument_list|(
literal|"int"
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0.0f
block|,
literal|1.0f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSumFloat
specifier|public
name|void
name|testSumFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|SumFloatFunction
argument_list|(
operator|new
name|ValueSource
index|[]
block|{
operator|new
name|ConstValueSource
argument_list|(
literal|1f
argument_list|)
block|,
operator|new
name|ConstValueSource
argument_list|(
literal|2f
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|3f
block|,
literal|3f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSumTotalTermFreq
specifier|public
name|void
name|testSumTotalTermFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|SumTotalTermFreqValueSource
argument_list|(
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|8f
block|,
literal|8f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermFreq
specifier|public
name|void
name|testTermFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|TermFreqValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"text"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|3f
block|,
literal|1f
block|}
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|TermFreqValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"string"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0f
block|,
literal|1f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTF
specifier|public
name|void
name|testTF
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|saved
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
try|try
block|{
comment|// no norm field (so agnostic to indexed similarity)
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|TFValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"text"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
literal|3d
argument_list|)
block|,
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
literal|1d
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|TFValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"string"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0f
block|,
literal|1f
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|saved
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTotalTermFreq
specifier|public
name|void
name|testTotalTermFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|TotalTermFreqValueSource
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"text"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4f
block|,
literal|4f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHits
name|void
name|assertHits
parameter_list|(
name|Query
name|q
parameter_list|,
name|float
name|scores
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|ScoreDoc
name|expected
index|[]
init|=
operator|new
name|ScoreDoc
index|[
name|scores
operator|.
name|length
index|]
decl_stmt|;
name|int
name|expectedDocs
index|[]
init|=
operator|new
name|int
index|[
name|scores
operator|.
name|length
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
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|expectedDocs
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|expected
index|[
name|i
index|]
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|i
argument_list|,
name|scores
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|documents
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|random
argument_list|()
argument_list|,
name|q
argument_list|,
literal|""
argument_list|,
name|searcher
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkHitsQuery
argument_list|(
name|q
argument_list|,
name|expected
argument_list|,
name|docs
operator|.
name|scoreDocs
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|q
argument_list|,
literal|""
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
