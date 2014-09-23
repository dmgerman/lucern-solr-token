begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene410
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene410
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|DocValuesFormat
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|asserting
operator|.
name|AssertingCodec
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
name|codecs
operator|.
name|blocktreeords
operator|.
name|Ords41PostingsFormat
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
name|codecs
operator|.
name|lucene41ords
operator|.
name|Lucene41WithOrds
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
name|codecs
operator|.
name|memory
operator|.
name|FSTOrdPostingsFormat
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
name|document
operator|.
name|SortedSetDocValuesField
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
name|index
operator|.
name|AtomicReader
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
name|index
operator|.
name|BaseCompressingDocValuesFormatTestCase
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
name|DirectoryReader
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
name|index
operator|.
name|SerialMergeScheduler
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
name|index
operator|.
name|TermsEnum
operator|.
name|SeekStatus
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests Lucene410DocValuesFormat  */
end_comment
begin_class
DECL|class|TestLucene410DocValuesFormat
specifier|public
class|class
name|TestLucene410DocValuesFormat
extends|extends
name|BaseCompressingDocValuesFormatTestCase
block|{
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
init|=
name|TestUtil
operator|.
name|alwaysDocValuesFormat
argument_list|(
operator|new
name|Lucene410DocValuesFormat
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|// TODO: these big methods can easily blow up some of the other ram-hungry codecs...
comment|// for now just keep them here, as we want to test this for this format.
DECL|method|testSortedSetVariableLengthBigVsStoredFields
specifier|public
name|void
name|testSortedSetVariableLengthBigVsStoredFields
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestSortedSetVsStoredFields
argument_list|(
name|atLeast
argument_list|(
literal|300
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|32766
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nightly
DECL|method|testSortedSetVariableLengthManyVsStoredFields
specifier|public
name|void
name|testSortedSetVariableLengthManyVsStoredFields
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestSortedSetVsStoredFields
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1024
argument_list|,
literal|2049
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortedVariableLengthBigVsStoredFields
specifier|public
name|void
name|testSortedVariableLengthBigVsStoredFields
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestSortedVsStoredFields
argument_list|(
name|atLeast
argument_list|(
literal|300
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|32766
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nightly
DECL|method|testSortedVariableLengthManyVsStoredFields
specifier|public
name|void
name|testSortedVariableLengthManyVsStoredFields
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestSortedVsStoredFields
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1024
argument_list|,
literal|2049
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTermsEnumFixedWidth
specifier|public
name|void
name|testTermsEnumFixedWidth
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestTermsEnumRandom
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1025
argument_list|,
literal|5121
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTermsEnumVariableWidth
specifier|public
name|void
name|testTermsEnumVariableWidth
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestTermsEnumRandom
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1025
argument_list|,
literal|5121
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nightly
DECL|method|testTermsEnumRandomMany
specifier|public
name|void
name|testTermsEnumRandomMany
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestTermsEnumRandom
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1025
argument_list|,
literal|8121
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: try to refactor this and some termsenum tests into the base class.
comment|// to do this we need to fix the test class to get a DVF not a Codec so we can setup
comment|// the postings format correctly.
DECL|method|doTestTermsEnumRandom
specifier|private
name|void
name|doTestTermsEnumRandom
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|int
name|minLength
parameter_list|,
name|int
name|maxLength
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
comment|// set to duel against a codec which has ordinals:
specifier|final
name|PostingsFormat
name|pf
decl_stmt|;
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|pf
operator|=
operator|new
name|Lucene41WithOrds
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|pf
operator|=
operator|new
name|Ords41PostingsFormat
argument_list|()
expr_stmt|;
break|break;
comment|// TODO: these don't actually support ords!
comment|//case 2: pf = new FSTOrdPostingsFormat();
comment|//        break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|final
name|DocValuesFormat
name|dv
init|=
operator|new
name|Lucene410DocValuesFormat
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
operator|new
name|AssertingCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|pf
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|dv
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// index some docs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
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
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
specifier|final
name|int
name|length
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minLength
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
decl_stmt|;
comment|// create a random list of strings
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|v
init|=
literal|0
init|;
name|v
operator|<
name|numValues
condition|;
name|v
operator|++
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|minLength
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add in any order to the indexed field
name|ArrayList
argument_list|<
name|String
argument_list|>
name|unordered
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|unordered
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"indexed"
argument_list|,
name|v
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add in any order to the dv field
name|ArrayList
argument_list|<
name|String
argument_list|>
name|unordered2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|unordered2
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|v
range|:
name|unordered2
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|31
argument_list|)
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|// delete some docs
name|int
name|numDeletions
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
operator|/
literal|10
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
name|numDeletions
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// compare per-segment
name|DirectoryReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|ir
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|r
operator|.
name|terms
argument_list|(
literal|"indexed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|r
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"dv"
argument_list|)
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|TermsEnum
name|expected
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|TermsEnum
name|actual
init|=
name|r
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"dv"
argument_list|)
operator|.
name|termsEnum
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// now compare again after the merge
name|ir
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|AtomicReader
name|ar
init|=
name|getOnlySegmentReader
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|ar
operator|.
name|terms
argument_list|(
literal|"indexed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"dv"
argument_list|)
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|TermsEnum
name|expected
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|TermsEnum
name|actual
init|=
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"dv"
argument_list|)
operator|.
name|termsEnum
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertEquals
specifier|private
name|void
name|assertEquals
parameter_list|(
name|long
name|numOrds
parameter_list|,
name|TermsEnum
name|expected
parameter_list|,
name|TermsEnum
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
name|BytesRef
name|ref
decl_stmt|;
comment|// sequential next() through all terms
while|while
condition|(
operator|(
name|ref
operator|=
name|expected
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|ref
argument_list|,
name|actual
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|actual
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// sequential seekExact(ord) through all terms
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
name|expected
operator|.
name|seekExact
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|actual
operator|.
name|seekExact
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sequential seekExact(BytesRef) through all terms
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
name|expected
operator|.
name|seekExact
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|seekExact
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sequential seekCeil(BytesRef) through all terms
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
name|expected
operator|.
name|seekExact
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|actual
operator|.
name|seekCeil
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// random seekExact(ord)
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
name|long
name|randomOrd
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|numOrds
operator|-
literal|1
argument_list|)
decl_stmt|;
name|expected
operator|.
name|seekExact
argument_list|(
name|randomOrd
argument_list|)
expr_stmt|;
name|actual
operator|.
name|seekExact
argument_list|(
name|randomOrd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// random seekExact(BytesRef)
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
name|long
name|randomOrd
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|numOrds
operator|-
literal|1
argument_list|)
decl_stmt|;
name|expected
operator|.
name|seekExact
argument_list|(
name|randomOrd
argument_list|)
expr_stmt|;
name|actual
operator|.
name|seekExact
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// random seekCeil(BytesRef)
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|target
init|=
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SeekStatus
name|expectedStatus
init|=
name|expected
operator|.
name|seekCeil
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedStatus
argument_list|,
name|actual
operator|.
name|seekCeil
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedStatus
operator|!=
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|ord
argument_list|()
argument_list|,
name|actual
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|term
argument_list|()
argument_list|,
name|actual
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
