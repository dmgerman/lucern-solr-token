begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|PhraseQuery
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
name|Searcher
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
name|store
operator|.
name|IndexInput
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
name|MockRAMDirectory
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * Tests lazy skipping on the proximity file.  *  */
end_comment
begin_class
DECL|class|TestLazyProxSkipping
specifier|public
class|class
name|TestLazyProxSkipping
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|seeksCounter
specifier|private
name|int
name|seeksCounter
init|=
literal|0
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
init|=
literal|"tokens"
decl_stmt|;
DECL|field|term1
specifier|private
name|String
name|term1
init|=
literal|"xx"
decl_stmt|;
DECL|field|term2
specifier|private
name|String
name|term2
init|=
literal|"yy"
decl_stmt|;
DECL|field|term3
specifier|private
name|String
name|term3
init|=
literal|"zz"
decl_stmt|;
DECL|class|SeekCountingDirectory
specifier|private
class|class
name|SeekCountingDirectory
extends|extends
name|MockRAMDirectory
block|{
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|ii
init|=
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".prx"
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|".pos"
argument_list|)
condition|)
block|{
comment|// we decorate the proxStream with a wrapper class that allows to count the number of calls of seek()
name|ii
operator|=
operator|new
name|SeeksCountingStream
argument_list|(
name|ii
argument_list|)
expr_stmt|;
block|}
return|return
name|ii
return|;
block|}
block|}
DECL|method|createIndex
specifier|private
name|void
name|createIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|numHits
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numDocs
init|=
literal|500
decl_stmt|;
name|Directory
name|directory
init|=
operator|new
name|SeekCountingDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setUseCompoundDocStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|String
name|content
decl_stmt|;
if|if
condition|(
name|i
operator|%
operator|(
name|numDocs
operator|/
name|numHits
operator|)
operator|==
literal|0
condition|)
block|{
comment|// add a document that matches the query "term1 term2"
name|content
operator|=
name|this
operator|.
name|term1
operator|+
literal|" "
operator|+
name|this
operator|.
name|term2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|%
literal|15
operator|==
literal|0
condition|)
block|{
comment|// add a document that only contains term1
name|content
operator|=
name|this
operator|.
name|term1
operator|+
literal|" "
operator|+
name|this
operator|.
name|term1
expr_stmt|;
block|}
else|else
block|{
comment|// add a document that contains term2 but not term 1
name|content
operator|=
name|this
operator|.
name|term3
operator|+
literal|" "
operator|+
name|this
operator|.
name|term2
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
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
comment|// make sure the index has only a single segment
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|getOnlySegmentReader
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|this
operator|.
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|private
name|ScoreDoc
index|[]
name|search
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create PhraseQuery "term1 term2" and search
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|this
operator|.
name|term1
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|this
operator|.
name|term2
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|searcher
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
return|;
block|}
DECL|method|performTest
specifier|private
name|void
name|performTest
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|numHits
parameter_list|)
throws|throws
name|IOException
block|{
name|createIndex
argument_list|(
name|random
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|this
operator|.
name|seeksCounter
operator|=
literal|0
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|search
argument_list|()
decl_stmt|;
comment|// verify that the right number of docs was found
name|assertEquals
argument_list|(
name|numHits
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check if the number of calls of seek() does not exceed the number of hits
name|assertTrue
argument_list|(
name|this
operator|.
name|seeksCounter
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"seeksCounter="
operator|+
name|this
operator|.
name|seeksCounter
operator|+
literal|" numHits="
operator|+
name|numHits
argument_list|,
name|this
operator|.
name|seeksCounter
operator|<=
name|numHits
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testLazySkipping
specifier|public
name|void
name|testLazySkipping
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test whether only the minimum amount of seeks() are performed
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|performTest
argument_list|(
name|random
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|performTest
argument_list|(
name|random
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|testSeek
specifier|public
name|void
name|testSeek
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|Directory
name|directory
init|=
name|newDirectory
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
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
literal|10
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|this
operator|.
name|field
argument_list|,
literal|"a b"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|tp
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
operator|.
name|field
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|tp
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|docID
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|tp
operator|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
operator|.
name|field
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
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
name|tp
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|docID
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Simply extends IndexInput in a way that we are able to count the number
comment|// of invocations of seek()
DECL|class|SeeksCountingStream
class|class
name|SeeksCountingStream
extends|extends
name|IndexInput
block|{
DECL|field|input
specifier|private
name|IndexInput
name|input
decl_stmt|;
DECL|method|SeeksCountingStream
name|SeeksCountingStream
parameter_list|(
name|IndexInput
name|input
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|input
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|this
operator|.
name|input
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|TestLazyProxSkipping
operator|.
name|this
operator|.
name|seeksCounter
operator|++
expr_stmt|;
name|this
operator|.
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|input
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SeeksCountingStream
argument_list|(
operator|(
name|IndexInput
operator|)
name|this
operator|.
name|input
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
