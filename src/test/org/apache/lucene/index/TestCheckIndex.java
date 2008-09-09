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
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
begin_class
DECL|class|TestCheckIndex
specifier|public
class|class
name|TestCheckIndex
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDeletedDocs
specifier|public
name|void
name|testDeletedDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|MockRAMDirectory
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
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
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
literal|"field"
argument_list|,
literal|"aaa"
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
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
literal|19
condition|;
name|i
operator|++
control|)
block|{
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
name|dir
argument_list|)
decl_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|CheckIndex
operator|.
name|out
operator|=
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|)
expr_stmt|;
name|CheckIndexStatus
name|indexStatus
init|=
name|CheckIndex
operator|.
name|check
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexStatus
operator|.
name|clean
operator|==
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CheckIndex failed"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|bos
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
name|onlySegments
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|onlySegments
operator|.
name|add
argument_list|(
literal|"_0"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CheckIndex
operator|.
name|check
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
name|onlySegments
argument_list|)
operator|.
name|clean
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
