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
name|CannedTokenStream
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
name|Token
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
begin_comment
comment|// LUCENE-6382
end_comment
begin_class
DECL|class|TestMaxPosition
specifier|public
class|class
name|TestMaxPosition
extends|extends
name|LuceneTestCase
block|{
DECL|method|testTooBigPosition
specifier|public
name|void
name|testTooBigPosition
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// This is at position 1:
name|Token
name|t1
init|=
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|t1
operator|.
name|setPositionIncrement
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|t1
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x1
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Token
name|t2
init|=
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
decl_stmt|;
comment|// This should overflow max:
name|t2
operator|.
name|setPositionIncrement
argument_list|(
name|IndexWriter
operator|.
name|MAX_POSITION
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|t2
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x1
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
index|[]
block|{
name|t1
block|,
name|t2
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
comment|// Document should not be visible:
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
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
DECL|method|testMaxPosition
specifier|public
name|void
name|testMaxPosition
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// This is at position 0:
name|Token
name|t1
init|=
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|t1
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x1
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Token
name|t2
init|=
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|t2
operator|.
name|setPositionIncrement
argument_list|(
name|IndexWriter
operator|.
name|MAX_POSITION
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|t2
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x1
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
index|[]
block|{
name|t1
block|,
name|t2
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// Document should be visible:
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|PostingsEnum
name|postings
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
comment|// "foo" appears in docID=0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// "foo" appears 2 times in the doc
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postings
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
comment|// first at pos=0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postings
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|// next at pos=MAX
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_POSITION
argument_list|,
name|postings
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
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
block|}
end_class
end_unit
