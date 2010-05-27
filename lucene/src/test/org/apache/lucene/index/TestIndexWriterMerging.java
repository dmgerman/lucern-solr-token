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
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|LuceneTestCase
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
DECL|class|TestIndexWriterMerging
specifier|public
class|class
name|TestIndexWriterMerging
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Tests that index merging (specifically addIndexes(Directory...)) doesn't    * change the index order of documents.    */
DECL|method|testLucene
specifier|public
name|void
name|testLucene
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|num
init|=
literal|100
decl_stmt|;
name|Directory
name|indexA
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|Directory
name|indexB
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|fillIndex
argument_list|(
name|indexA
argument_list|,
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|boolean
name|fail
init|=
name|verifyIndex
argument_list|(
name|indexA
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|fail
argument_list|(
literal|"Index a is invalid"
argument_list|)
expr_stmt|;
block|}
name|fillIndex
argument_list|(
name|indexB
argument_list|,
name|num
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|fail
operator|=
name|verifyIndex
argument_list|(
name|indexB
argument_list|,
name|num
argument_list|)
expr_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|fail
argument_list|(
literal|"Index b is invalid"
argument_list|)
expr_stmt|;
block|}
name|Directory
name|merged
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
name|merged
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
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
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
operator|new
name|Directory
index|[]
block|{
name|indexA
block|,
name|indexB
block|}
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
name|fail
operator|=
name|verifyIndex
argument_list|(
name|merged
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|merged
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The merged index is invalid"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyIndex
specifier|private
name|boolean
name|verifyIndex
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|int
name|startAt
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
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
name|int
name|max
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|temp
init|=
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//System.out.println("doc "+i+"="+temp.getField("count").stringValue());
comment|//compare the index doc number to the value that it should be
if|if
condition|(
operator|!
name|temp
operator|.
name|getField
argument_list|(
literal|"count"
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
name|i
operator|+
name|startAt
operator|)
operator|+
literal|""
argument_list|)
condition|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Document "
operator|+
operator|(
name|i
operator|+
name|startAt
operator|)
operator|+
literal|" is returning document "
operator|+
name|temp
operator|.
name|getField
argument_list|(
literal|"count"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|fail
return|;
block|}
DECL|method|fillIndex
specifier|private
name|void
name|fillIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
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
literal|2
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
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
operator|(
name|start
operator|+
name|numDocs
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|temp
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|temp
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"count"
argument_list|,
operator|(
literal|""
operator|+
name|i
operator|)
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
