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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestDirectoryReader
specifier|public
class|class
name|TestDirectoryReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|protected
name|Directory
name|dir
decl_stmt|;
DECL|field|doc1
specifier|private
name|Document
name|doc1
decl_stmt|;
DECL|field|doc2
specifier|private
name|Document
name|doc2
decl_stmt|;
DECL|field|readers
specifier|protected
name|SegmentReader
index|[]
name|readers
init|=
operator|new
name|SegmentReader
index|[
literal|2
index|]
decl_stmt|;
DECL|field|sis
specifier|protected
name|SegmentInfos
name|sis
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
name|dir
operator|=
name|createDirectory
argument_list|()
expr_stmt|;
name|doc1
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc2
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|sis
operator|=
operator|new
name|SegmentInfos
argument_list|()
expr_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|readers
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
name|readers
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|readers
index|[
literal|1
index|]
operator|!=
literal|null
condition|)
name|readers
index|[
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|createDirectory
specifier|protected
name|Directory
name|createDirectory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|newDirectory
argument_list|()
return|;
block|}
DECL|method|openReader
specifier|protected
name|IndexReader
name|openReader
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
decl_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|instanceof
name|DirectoryReader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sis
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
DECL|method|testDocument
specifier|public
name|void
name|testDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|openReader
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Document
name|newDoc1
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc1
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc1
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc1
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|newDoc2
init|=
name|reader
operator|.
name|document
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc2
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc2
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Terms
name|vector
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
literal|0
argument_list|)
operator|.
name|terms
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
comment|// TODO: pretty sure this check makes zero sense TestSegmentReader.checkNorms(reader);
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testIsCurrent
specifier|public
name|void
name|testIsCurrent
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|ramDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|random
argument_list|,
name|ramDir
argument_list|,
literal|"test foo"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|ramDir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// just opened, must be current
name|addDoc
argument_list|(
name|random
argument_list|,
name|ramDir
argument_list|,
literal|"more text"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|reader
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// has been modified, not current anymore
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMultiTermDocs
specifier|public
name|void
name|testMultiTermDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|ramDir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|random
argument_list|,
name|ramDir1
argument_list|,
literal|"test foo"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|ramDir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|random
argument_list|,
name|ramDir2
argument_list|,
literal|"test blah"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|ramDir3
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|random
argument_list|,
name|ramDir3
argument_list|,
literal|"test wow"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers1
init|=
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir1
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir3
argument_list|)
block|}
decl_stmt|;
name|IndexReader
index|[]
name|readers2
init|=
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir1
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir2
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir3
argument_list|)
block|}
decl_stmt|;
name|MultiReader
name|mr2
init|=
operator|new
name|MultiReader
argument_list|(
name|readers1
argument_list|)
decl_stmt|;
name|MultiReader
name|mr3
init|=
operator|new
name|MultiReader
argument_list|(
name|readers2
argument_list|)
decl_stmt|;
comment|// test mixing up TermDocs and TermEnums from different readers.
name|TermsEnum
name|te2
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|mr2
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|te2
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"wow"
argument_list|)
argument_list|)
expr_stmt|;
name|DocsEnum
name|td
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|,
name|mr2
argument_list|,
literal|"body"
argument_list|,
name|te2
operator|.
name|term
argument_list|()
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|mr2
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TermsEnum
name|te3
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|mr3
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|te3
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"wow"
argument_list|)
argument_list|)
expr_stmt|;
name|td
operator|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|,
name|te3
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|mr3
argument_list|)
argument_list|,
name|td
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
comment|// This should blow up if we forget to check that the TermEnum is from the same
comment|// reader as the TermDocs.
while|while
condition|(
name|td
operator|.
name|nextDoc
argument_list|()
operator|!=
name|td
operator|.
name|NO_MORE_DOCS
condition|)
name|ret
operator|+=
name|td
operator|.
name|docID
argument_list|()
expr_stmt|;
comment|// really a dummy assert to ensure that we got some docs and to ensure that
comment|// nothing is eliminated by hotspot
name|assertTrue
argument_list|(
name|ret
operator|>
literal|0
argument_list|)
expr_stmt|;
name|readers1
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|readers1
index|[
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|readers2
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|readers2
index|[
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|readers2
index|[
literal|2
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir3
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
name|Random
name|random
parameter_list|,
name|Directory
name|ramDir1
parameter_list|,
name|String
name|s
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramDir1
argument_list|,
name|newIndexWriterConfig
argument_list|(
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
name|create
condition|?
name|OpenMode
operator|.
name|CREATE
else|:
name|OpenMode
operator|.
name|APPEND
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
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"body"
argument_list|,
name|s
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
