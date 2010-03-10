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
name|store
operator|.
name|RAMDirectory
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_class
DECL|class|TestSegmentTermDocs
specifier|public
class|class
name|TestSegmentTermDocs
extends|extends
name|LuceneTestCase
block|{
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|info
specifier|private
name|SegmentInfo
name|info
decl_stmt|;
DECL|method|TestSegmentTermDocs
specifier|public
name|TestSegmentTermDocs
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|info
operator|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|testDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermDocs
specifier|public
name|void
name|testTermDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|testTermDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermDocs
specifier|public
name|void
name|testTermDocs
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|indexDivisor
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexDivisor
argument_list|,
name|reader
operator|.
name|getTermInfosIndexDivisor
argument_list|()
argument_list|)
expr_stmt|;
name|SegmentTermDocs
name|segTermDocs
init|=
operator|new
name|SegmentTermDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|segTermDocs
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|,
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|segTermDocs
operator|.
name|next
argument_list|()
operator|==
literal|true
condition|)
block|{
name|int
name|docId
init|=
name|segTermDocs
operator|.
name|doc
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docId
operator|==
literal|0
argument_list|)
expr_stmt|;
name|int
name|freq
init|=
name|segTermDocs
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testBadSeek
specifier|public
name|void
name|testBadSeek
parameter_list|()
throws|throws
name|IOException
block|{
name|testBadSeek
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadSeek
specifier|public
name|void
name|testBadSeek
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
block|{
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|indexDivisor
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|SegmentTermDocs
name|segTermDocs
init|=
operator|new
name|SegmentTermDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|segTermDocs
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
literal|"textField2"
argument_list|,
literal|"bad"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|segTermDocs
operator|.
name|next
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|indexDivisor
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|SegmentTermDocs
name|segTermDocs
init|=
operator|new
name|SegmentTermDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|segTermDocs
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
literal|"junk"
argument_list|,
literal|"bad"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|segTermDocs
operator|.
name|next
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSkipTo
specifier|public
name|void
name|testSkipTo
parameter_list|()
throws|throws
name|IOException
block|{
name|testSkipTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testSkipTo
specifier|public
name|void
name|testSkipTo
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|Term
name|ta
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
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
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa aaa aaa aaa"
argument_list|)
expr_stmt|;
name|Term
name|tb
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
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
literal|16
condition|;
name|i
operator|++
control|)
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"bbb bbb bbb bbb"
argument_list|)
expr_stmt|;
name|Term
name|tc
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
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
literal|50
condition|;
name|i
operator|++
control|)
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"ccc ccc ccc ccc"
argument_list|)
expr_stmt|;
comment|// assure that we deal with a single segment
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
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|indexDivisor
argument_list|)
decl_stmt|;
name|TermDocs
name|tdocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
comment|// without optimization (assumption skipInterval == 16)
comment|// with next
name|tdocs
operator|.
name|seek
argument_list|(
name|ta
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// without next
name|tdocs
operator|.
name|seek
argument_list|(
name|ta
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// exactly skipInterval documents and therefore with optimization
comment|// with next
name|tdocs
operator|.
name|seek
argument_list|(
name|tb
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|24
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|25
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|26
argument_list|)
argument_list|)
expr_stmt|;
comment|// without next
name|tdocs
operator|.
name|seek
argument_list|(
name|tb
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|24
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|25
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|26
argument_list|)
argument_list|)
expr_stmt|;
comment|// much more than skipInterval documents and therefore with optimization
comment|// with next
name|tdocs
operator|.
name|seek
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|26
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|27
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|40
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|57
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|57
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|74
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|75
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|76
argument_list|)
argument_list|)
expr_stmt|;
comment|//without next
name|tdocs
operator|.
name|seek
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|26
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|40
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|57
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|57
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|74
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|75
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|skipTo
argument_list|(
literal|76
argument_list|)
argument_list|)
expr_stmt|;
name|tdocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|testIndexDivisor
specifier|public
name|void
name|testIndexDivisor
parameter_list|()
throws|throws
name|IOException
block|{
name|dir
operator|=
operator|new
name|MockRAMDirectory
argument_list|()
expr_stmt|;
name|testDoc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|testDoc
argument_list|)
expr_stmt|;
name|testTermDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|testBadSeek
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|testSkipTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
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
literal|"content"
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
block|}
end_class
end_unit
