begin_unit
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
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
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import
begin_class
DECL|class|TestSerialization
specifier|public
class|class
name|TestSerialization
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
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
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
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
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar rab abr bra rba"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"moo"
argument_list|,
literal|"bar rab abr bra rba"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|ObjectOutputStream
name|oos
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|oos
operator|.
name|writeObject
argument_list|(
name|ii
argument_list|)
expr_stmt|;
name|oos
operator|.
name|close
argument_list|()
expr_stmt|;
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
