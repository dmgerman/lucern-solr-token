begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Random
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
operator|.
name|OpenMode
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
name|LuceneTestCase
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
name|_TestUtil
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
DECL|class|BaseTestRangeFilter
specifier|public
class|class
name|BaseTestRangeFilter
extends|extends
name|LuceneTestCase
block|{
DECL|field|F
specifier|public
specifier|static
specifier|final
name|boolean
name|F
init|=
literal|false
decl_stmt|;
DECL|field|T
specifier|public
specifier|static
specifier|final
name|boolean
name|T
init|=
literal|true
decl_stmt|;
comment|/**    * Collation interacts badly with hyphens -- collation produces different    * ordering than Unicode code-point ordering -- so two indexes are created:    * one which can't have negative random integers, for testing collated ranges,    * and the other which can have negative random integers, for all other tests.    */
DECL|class|TestIndex
specifier|static
class|class
name|TestIndex
block|{
DECL|field|maxR
name|int
name|maxR
decl_stmt|;
DECL|field|minR
name|int
name|minR
decl_stmt|;
DECL|field|allowNegativeRandomInts
name|boolean
name|allowNegativeRandomInts
decl_stmt|;
DECL|field|index
name|Directory
name|index
decl_stmt|;
DECL|method|TestIndex
name|TestIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|minR
parameter_list|,
name|int
name|maxR
parameter_list|,
name|boolean
name|allowNegativeRandomInts
parameter_list|)
block|{
name|this
operator|.
name|minR
operator|=
name|minR
expr_stmt|;
name|this
operator|.
name|maxR
operator|=
name|maxR
expr_stmt|;
name|this
operator|.
name|allowNegativeRandomInts
operator|=
name|allowNegativeRandomInts
expr_stmt|;
try|try
block|{
name|index
operator|=
name|newDirectory
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|signedIndexReader
specifier|static
name|IndexReader
name|signedIndexReader
decl_stmt|;
DECL|field|unsignedIndexReader
specifier|static
name|IndexReader
name|unsignedIndexReader
decl_stmt|;
DECL|field|signedIndexDir
specifier|static
name|TestIndex
name|signedIndexDir
decl_stmt|;
DECL|field|unsignedIndexDir
specifier|static
name|TestIndex
name|unsignedIndexDir
decl_stmt|;
DECL|field|minId
specifier|static
name|int
name|minId
init|=
literal|0
decl_stmt|;
DECL|field|maxId
specifier|static
name|int
name|maxId
decl_stmt|;
DECL|field|intLength
specifier|static
specifier|final
name|int
name|intLength
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
comment|/**    * a simple padding function that should work with any int    */
DECL|method|pad
specifier|public
specifier|static
name|String
name|pad
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|40
argument_list|)
decl_stmt|;
name|String
name|p
init|=
literal|"0"
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
name|p
operator|=
literal|"-"
expr_stmt|;
name|n
operator|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
name|n
operator|+
literal|1
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|s
operator|.
name|length
argument_list|()
init|;
name|i
operator|<=
name|intLength
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClassBaseTestRangeFilter
specifier|public
specifier|static
name|void
name|beforeClassBaseTestRangeFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|maxId
operator|=
name|atLeast
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|signedIndexDir
operator|=
operator|new
name|TestIndex
argument_list|(
name|random
argument_list|()
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|unsignedIndexDir
operator|=
operator|new
name|TestIndex
argument_list|(
name|random
argument_list|()
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|signedIndexReader
operator|=
name|build
argument_list|(
name|random
argument_list|()
argument_list|,
name|signedIndexDir
argument_list|)
expr_stmt|;
name|unsignedIndexReader
operator|=
name|build
argument_list|(
name|random
argument_list|()
argument_list|,
name|unsignedIndexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassBaseTestRangeFilter
specifier|public
specifier|static
name|void
name|afterClassBaseTestRangeFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|signedIndexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|unsignedIndexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|signedIndexDir
operator|.
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
name|unsignedIndexDir
operator|.
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
name|signedIndexReader
operator|=
literal|null
expr_stmt|;
name|unsignedIndexReader
operator|=
literal|null
expr_stmt|;
name|signedIndexDir
operator|=
literal|null
expr_stmt|;
name|unsignedIndexDir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|build
specifier|private
specifier|static
name|IndexReader
name|build
parameter_list|(
name|Random
name|random
parameter_list|,
name|TestIndex
name|index
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* build an index */
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
name|newStringField
argument_list|(
name|random
argument_list|,
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|randField
init|=
name|newStringField
argument_list|(
name|random
argument_list|,
literal|"rand"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|bodyField
init|=
name|newStringField
argument_list|(
name|random
argument_list|,
literal|"body"
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
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|randField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bodyField
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|index
operator|.
name|index
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|50
argument_list|,
literal|1000
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|_TestUtil
operator|.
name|reduceOpenFiles
argument_list|(
name|writer
operator|.
name|w
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|minCount
init|=
literal|0
decl_stmt|;
name|int
name|maxCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
name|minId
init|;
name|d
operator|<=
name|maxId
condition|;
name|d
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|pad
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|r
init|=
name|index
operator|.
name|allowNegativeRandomInts
condition|?
name|random
operator|.
name|nextInt
argument_list|()
else|:
name|random
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|.
name|maxR
operator|<
name|r
condition|)
block|{
name|index
operator|.
name|maxR
operator|=
name|r
expr_stmt|;
name|maxCount
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|.
name|maxR
operator|==
name|r
condition|)
block|{
name|maxCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|<
name|index
operator|.
name|minR
condition|)
block|{
name|index
operator|.
name|minR
operator|=
name|r
expr_stmt|;
name|minCount
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|==
name|index
operator|.
name|minR
condition|)
block|{
name|minCount
operator|++
expr_stmt|;
block|}
name|randField
operator|.
name|setStringValue
argument_list|(
name|pad
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|bodyField
operator|.
name|setStringValue
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minCount
operator|==
literal|1
operator|&&
name|maxCount
operator|==
literal|1
condition|)
block|{
comment|// our subclasses rely on only 1 doc having the min or
comment|// max, so, we loop until we satisfy that.  it should be
comment|// exceedingly rare (Yonik calculates 1 in ~429,000)
comment|// times) that this loop requires more than one try:
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|ir
return|;
block|}
comment|// try again
name|writer
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPad
specifier|public
name|void
name|testPad
parameter_list|()
block|{
name|int
index|[]
name|tests
init|=
operator|new
name|int
index|[]
block|{
operator|-
literal|9999999
block|,
operator|-
literal|99560
block|,
operator|-
literal|100
block|,
operator|-
literal|3
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|9
block|,
literal|10
block|,
literal|1000
block|,
literal|999999999
block|}
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
name|tests
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|int
name|a
init|=
name|tests
index|[
name|i
index|]
decl_stmt|;
name|int
name|b
init|=
name|tests
index|[
name|i
operator|+
literal|1
index|]
decl_stmt|;
name|String
name|aa
init|=
name|pad
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|String
name|bb
init|=
name|pad
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|String
name|label
init|=
name|a
operator|+
literal|":"
operator|+
name|aa
operator|+
literal|" vs "
operator|+
name|b
operator|+
literal|":"
operator|+
name|bb
decl_stmt|;
name|assertEquals
argument_list|(
literal|"length of "
operator|+
name|label
argument_list|,
name|aa
operator|.
name|length
argument_list|()
argument_list|,
name|bb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"compare less than "
operator|+
name|label
argument_list|,
name|aa
operator|.
name|compareTo
argument_list|(
name|bb
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
