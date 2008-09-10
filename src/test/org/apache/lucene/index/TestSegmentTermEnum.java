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
name|IndexWriter
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
name|TermEnum
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
begin_comment
comment|/**  * @author goller  */
end_comment
begin_class
DECL|class|TestSegmentTermEnum
specifier|public
class|class
name|TestSegmentTermEnum
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|method|testTermEnum
specifier|public
name|void
name|testTermEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
comment|// ADD 100 documents with term : aaa
comment|// add 100 documents with terms: aaa bbb
comment|// Therefore, term 'aaa' has document frequency of 200 and term 'bbb' 100
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa bbb"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify document frequency of terms in an unoptimized index
name|verifyDocFreq
argument_list|()
expr_stmt|;
comment|// merge segments by optimizing the index
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
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
comment|// verify document frequency of terms in an optimized index
name|verifyDocFreq
argument_list|()
expr_stmt|;
block|}
DECL|method|testPrevTermAtEnd
specifier|public
name|void
name|testPrevTermAtEnd
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
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
argument_list|()
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
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa bbb"
argument_list|)
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
argument_list|)
decl_stmt|;
name|SegmentTermEnum
name|termEnum
init|=
operator|(
name|SegmentTermEnum
operator|)
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|termEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|termEnum
operator|.
name|prev
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|termEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|termEnum
operator|.
name|prev
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyDocFreq
specifier|private
name|void
name|verifyDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TermEnum
name|termEnum
init|=
literal|null
decl_stmt|;
comment|// create enumeration of all terms
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|()
expr_stmt|;
comment|// go to the first term (aaa)
name|termEnum
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// assert that term is 'aaa'
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|termEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
comment|// go to the second term (bbb)
name|termEnum
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// assert that term is 'bbb'
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|termEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// create enumeration of terms after term 'aaa', including 'aaa'
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
comment|// assert that term is 'aaa'
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|termEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
comment|// go to term 'bbb'
name|termEnum
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// assert that term is 'bbb'
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|termEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
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
