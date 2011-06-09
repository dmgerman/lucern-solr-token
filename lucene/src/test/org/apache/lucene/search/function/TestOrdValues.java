begin_unit
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|index
operator|.
name|CorruptIndexException
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
operator|.
name|AtomicReaderContext
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
name|*
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
name|ReaderUtil
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
begin_comment
comment|/**  * Test search based on OrdFieldSource and ReverseOrdFieldSource.  *<p/>  * Tests here create an index with a few documents, each having  * an indexed "id" field.  * The ord values of this field are later used for scoring.  *<p/>  * The order tests use Hits to verify that docs are ordered as expected.  *<p/>  * The exact score tests use TopDocs top to verify the exact score.  */
end_comment
begin_class
DECL|class|TestOrdValues
specifier|public
class|class
name|TestOrdValues
extends|extends
name|FunctionTestSetup
block|{
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
name|createIndex
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test OrdFieldSource    */
annotation|@
name|Test
DECL|method|testOrdFieldRank
specifier|public
name|void
name|testOrdFieldRank
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestRank
argument_list|(
name|ID_FIELD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test ReverseOrdFieldSource    */
annotation|@
name|Test
DECL|method|testReverseOrdFieldRank
specifier|public
name|void
name|testReverseOrdFieldRank
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestRank
argument_list|(
name|ID_FIELD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Test that queries based on reverse/ordFieldScore scores correctly
DECL|method|doTestRank
specifier|private
name|void
name|doTestRank
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ValueSource
name|vs
decl_stmt|;
if|if
condition|(
name|inOrder
condition|)
block|{
name|vs
operator|=
operator|new
name|MultiValueSource
argument_list|(
operator|new
name|OrdFieldSource
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|MultiValueSource
argument_list|(
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
init|=
operator|new
name|ValueSourceQuery
argument_list|(
name|vs
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"test: "
operator|+
name|q
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|q
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|prevID
init|=
name|inOrder
condition|?
literal|"IE"
comment|// greater than all ids of docs in this test ("ID0001", etc.)
else|:
literal|"IC"
decl_stmt|;
comment|// smaller than all ids of docs in this test ("ID0001", etc.)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|h
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|resID
init|=
name|s
operator|.
name|doc
argument_list|(
name|h
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|log
argument_list|(
name|i
operator|+
literal|".   score="
operator|+
name|h
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|"  -  "
operator|+
name|resID
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|h
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|inOrder
condition|)
block|{
name|assertTrue
argument_list|(
literal|"res id "
operator|+
name|resID
operator|+
literal|" should be< prev res id "
operator|+
name|prevID
argument_list|,
name|resID
operator|.
name|compareTo
argument_list|(
name|prevID
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"res id "
operator|+
name|resID
operator|+
literal|" should be> prev res id "
operator|+
name|prevID
argument_list|,
name|resID
operator|.
name|compareTo
argument_list|(
name|prevID
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|prevID
operator|=
name|resID
expr_stmt|;
block|}
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test exact score for OrdFieldSource    */
annotation|@
name|Test
DECL|method|testOrdFieldExactScore
specifier|public
name|void
name|testOrdFieldExactScore
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestExactScore
argument_list|(
name|ID_FIELD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test exact score for ReverseOrdFieldSource    */
annotation|@
name|Test
DECL|method|testReverseOrdFieldExactScore
specifier|public
name|void
name|testReverseOrdFieldExactScore
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestExactScore
argument_list|(
name|ID_FIELD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Test that queries based on reverse/ordFieldScore returns docs with expected score.
DECL|method|doTestExactScore
specifier|private
name|void
name|doTestExactScore
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ValueSource
name|vs
decl_stmt|;
if|if
condition|(
name|inOrder
condition|)
block|{
name|vs
operator|=
operator|new
name|OrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
init|=
operator|new
name|ValueSourceQuery
argument_list|(
name|vs
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ScoreDoc
name|sd
index|[]
init|=
name|td
operator|.
name|scoreDocs
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
name|float
name|score
init|=
name|sd
index|[
name|i
index|]
operator|.
name|score
decl_stmt|;
name|String
name|id
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|document
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"-------- "
operator|+
name|i
operator|+
literal|". Explain doc "
operator|+
name|id
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|expectedScore
init|=
name|N_DOCS
operator|-
name|i
decl_stmt|;
name|assertEquals
argument_list|(
literal|"score of result "
operator|+
name|i
operator|+
literal|" shuould be "
operator|+
name|expectedScore
operator|+
literal|" != "
operator|+
name|score
argument_list|,
name|expectedScore
argument_list|,
name|score
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
name|String
name|expectedId
init|=
name|inOrder
condition|?
name|id2String
argument_list|(
name|N_DOCS
operator|-
name|i
argument_list|)
comment|// in-order ==> larger  values first
else|:
name|id2String
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// reverse  ==> smaller values first
name|assertTrue
argument_list|(
literal|"id of result "
operator|+
name|i
operator|+
literal|" shuould be "
operator|+
name|expectedId
operator|+
literal|" != "
operator|+
name|score
argument_list|,
name|expectedId
operator|.
name|equals
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test caching OrdFieldSource    */
annotation|@
name|Test
DECL|method|testCachingOrd
specifier|public
name|void
name|testCachingOrd
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestCaching
argument_list|(
name|ID_FIELD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test caching for ReverseOrdFieldSource    */
annotation|@
name|Test
DECL|method|testCachingReverseOrd
specifier|public
name|void
name|testCachingReverseOrd
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestCaching
argument_list|(
name|ID_FIELD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Test that values loaded for FieldScoreQuery are cached properly and consumes the proper RAM resources.
DECL|method|doTestCaching
specifier|private
name|void
name|doTestCaching
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Object
name|innerArray
init|=
literal|null
decl_stmt|;
name|boolean
name|warned
init|=
literal|false
decl_stmt|;
comment|// print warning once
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|ValueSource
name|vs
decl_stmt|;
if|if
condition|(
name|inOrder
condition|)
block|{
name|vs
operator|=
operator|new
name|OrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|ValueSourceQuery
name|q
init|=
operator|new
name|ValueSourceQuery
argument_list|(
name|vs
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|s
operator|.
name|getTopReaderContext
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|innerArray
operator|=
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
argument_list|(
name|i
operator|+
literal|".  compare: "
operator|+
name|innerArray
operator|+
literal|" to "
operator|+
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"field values should be cached and reused!"
argument_list|,
name|innerArray
argument_list|,
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|warned
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|testName
argument_list|()
operator|+
literal|" cannot fully test values of "
operator|+
name|q
argument_list|)
expr_stmt|;
name|warned
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|ValueSource
name|vs
decl_stmt|;
name|ValueSourceQuery
name|q
decl_stmt|;
name|ScoreDoc
index|[]
name|h
decl_stmt|;
comment|// verify that different values are loaded for a different field
name|String
name|field2
init|=
name|INT_FIELD
decl_stmt|;
name|assertFalse
argument_list|(
name|field
operator|.
name|equals
argument_list|(
name|field2
argument_list|)
argument_list|)
expr_stmt|;
comment|// otherwise this test is meaningless.
if|if
condition|(
name|inOrder
condition|)
block|{
name|vs
operator|=
operator|new
name|OrdFieldSource
argument_list|(
name|field2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field2
argument_list|)
expr_stmt|;
block|}
name|q
operator|=
operator|new
name|ValueSourceQuery
argument_list|(
name|vs
argument_list|)
expr_stmt|;
name|h
operator|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|s
operator|.
name|getTopReaderContext
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
try|try
block|{
name|log
argument_list|(
literal|"compare (should differ): "
operator|+
name|innerArray
operator|+
literal|" to "
operator|+
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"different values should be loaded for a different field!"
argument_list|,
name|innerArray
argument_list|,
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|warned
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|testName
argument_list|()
operator|+
literal|" cannot fully test values of "
operator|+
name|q
argument_list|)
expr_stmt|;
name|warned
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify new values are reloaded (not reused) for a new reader
name|s
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|inOrder
condition|)
block|{
name|vs
operator|=
operator|new
name|OrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|q
operator|=
operator|new
name|ValueSourceQuery
argument_list|(
name|vs
argument_list|)
expr_stmt|;
name|h
operator|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|leaves
operator|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|s
operator|.
name|getTopReaderContext
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AtomicReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
try|try
block|{
name|log
argument_list|(
literal|"compare (should differ): "
operator|+
name|innerArray
operator|+
literal|" to "
operator|+
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"cached field values should not be reused if reader as changed!"
argument_list|,
name|innerArray
argument_list|,
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|leaf
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|warned
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|testName
argument_list|()
operator|+
literal|" cannot fully test values of "
operator|+
name|q
argument_list|)
expr_stmt|;
name|warned
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testName
specifier|private
name|String
name|testName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
comment|// LUCENE-1250
DECL|method|testEqualsNull
specifier|public
name|void
name|testEqualsNull
parameter_list|()
throws|throws
name|Exception
block|{
name|OrdFieldSource
name|ofs
init|=
operator|new
name|OrdFieldSource
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ofs
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|ReverseOrdFieldSource
name|rofs
init|=
operator|new
name|ReverseOrdFieldSource
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rofs
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
