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
name|java
operator|.
name|io
operator|.
name|File
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
begin_import
import|import
name|java
operator|.
name|util
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
name|DocumentStoredFieldVisitor
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|BaseDirectory
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
name|BufferedIndexInput
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
name|IOContext
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
name|IndexOutput
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
name|TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
begin_class
DECL|class|TestFieldsReader
specifier|public
class|class
name|TestFieldsReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|testDoc
specifier|private
specifier|static
name|Document
name|testDoc
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|static
name|FieldInfos
operator|.
name|Builder
name|fieldInfos
init|=
literal|null
decl_stmt|;
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
name|testDoc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|testDoc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|fieldInfos
operator|.
name|addOrUpdate
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|fieldType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|getMergePolicy
argument_list|()
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|FaultyIndexInput
operator|.
name|doFail
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|fieldInfos
operator|=
literal|null
expr_stmt|;
name|testDoc
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|StoredDocument
name|doc
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
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|field
operator|=
operator|(
name|Field
operator|)
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|field
operator|=
operator|(
name|Field
operator|)
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|NO_TF_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
decl_stmt|;
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|StorableField
argument_list|>
name|fields
init|=
name|visitor
operator|.
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|,
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|FaultyFSDirectory
specifier|public
specifier|static
class|class
name|FaultyFSDirectory
extends|extends
name|BaseDirectory
block|{
DECL|field|fsDir
name|Directory
name|fsDir
decl_stmt|;
DECL|method|FaultyFSDirectory
specifier|public
name|FaultyFSDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|fsDir
operator|=
name|newFSDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|lockFactory
operator|=
name|fsDir
operator|.
name|getLockFactory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FaultyIndexInput
argument_list|(
name|fsDir
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fsDir
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|fsDir
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fsDir
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fsDir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|fsDir
operator|.
name|sync
argument_list|(
name|names
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
name|fsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|FaultyIndexInput
specifier|private
specifier|static
class|class
name|FaultyIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|field|delegate
name|IndexInput
name|delegate
decl_stmt|;
DECL|field|doFail
specifier|static
name|boolean
name|doFail
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|method|FaultyIndexInput
specifier|private
name|FaultyIndexInput
parameter_list|(
name|IndexInput
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
literal|"FaultyIndexInput("
operator|+
name|delegate
operator|+
literal|")"
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|simOutage
specifier|private
name|void
name|simOutage
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doFail
operator|&&
name|count
operator|++
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Simulated network outage"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readInternal
specifier|public
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|simOutage
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|seek
argument_list|(
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekInternal
specifier|public
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|length
argument_list|()
return|;
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
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|FaultyIndexInput
name|clone
parameter_list|()
block|{
name|FaultyIndexInput
name|i
init|=
operator|new
name|FaultyIndexInput
argument_list|(
name|delegate
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
comment|// seek the clone to our current position
try|try
block|{
name|i
operator|.
name|seek
argument_list|(
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
return|return
name|i
return|;
block|}
block|}
comment|// LUCENE-1262
DECL|method|testExceptions
specifier|public
name|void
name|testExceptions
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|indexDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testfieldswriterexceptions"
argument_list|)
decl_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
operator|new
name|FaultyFSDirectory
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
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
literal|2
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
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
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|FaultyIndexInput
operator|.
name|doFail
operator|=
literal|true
expr_stmt|;
name|boolean
name|exc
init|=
literal|false
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// expected
name|exc
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// expected
name|exc
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|exc
argument_list|)
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
finally|finally
block|{
name|TestUtil
operator|.
name|rmDir
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
