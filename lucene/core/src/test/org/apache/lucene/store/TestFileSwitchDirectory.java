begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|Arrays
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|lucene40
operator|.
name|Lucene40StoredFieldsWriter
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
name|TestIndexWriterReader
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
DECL|class|TestFileSwitchDirectory
specifier|public
class|class
name|TestFileSwitchDirectory
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Test if writing doc stores to disk and everything else to ram works.    */
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fileExtensions
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|fileExtensions
operator|.
name|add
argument_list|(
name|Lucene40StoredFieldsWriter
operator|.
name|FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|fileExtensions
operator|.
name|add
argument_list|(
name|Lucene40StoredFieldsWriter
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|MockDirectoryWrapper
name|primaryDir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|primaryDir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// only part of an index
name|MockDirectoryWrapper
name|secondaryDir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|secondaryDir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// only part of an index
name|FileSwitchDirectory
name|fsd
init|=
operator|new
name|FileSwitchDirectory
argument_list|(
name|fileExtensions
argument_list|,
name|primaryDir
argument_list|,
name|secondaryDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// for now we wire Lucene40Codec because we rely upon its specific impl
name|boolean
name|oldValue
init|=
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
decl_stmt|;
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
operator|=
literal|true
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|fsd
argument_list|,
operator|new
name|IndexWriterConfig
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
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene40"
argument_list|)
argument_list|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TestIndexWriterReader
operator|.
name|createIndexNoClose
argument_list|(
literal|true
argument_list|,
literal|"ram"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// we should see only fdx,fdt files here
name|String
index|[]
name|files
init|=
name|primaryDir
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|files
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|files
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|String
name|ext
init|=
name|FileSwitchDirectory
operator|.
name|getExtension
argument_list|(
name|files
index|[
name|x
index|]
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fileExtensions
operator|.
name|contains
argument_list|(
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|files
operator|=
name|secondaryDir
operator|.
name|listAll
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|files
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// we should not see fdx,fdt files here
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|files
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|String
name|ext
init|=
name|FileSwitchDirectory
operator|.
name|getExtension
argument_list|(
name|files
index|[
name|x
index|]
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fileExtensions
operator|.
name|contains
argument_list|(
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|files
operator|=
name|fsd
operator|.
name|listAll
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|fsd
operator|.
name|close
argument_list|()
expr_stmt|;
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
operator|=
name|oldValue
expr_stmt|;
block|}
DECL|method|newFSSwitchDirectory
specifier|private
name|Directory
name|newFSSwitchDirectory
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|primaryExtensions
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|primDir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|File
name|secondDir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
return|return
name|newFSSwitchDirectory
argument_list|(
name|primDir
argument_list|,
name|secondDir
argument_list|,
name|primaryExtensions
argument_list|)
return|;
block|}
DECL|method|newFSSwitchDirectory
specifier|private
name|Directory
name|newFSSwitchDirectory
parameter_list|(
name|File
name|aDir
parameter_list|,
name|File
name|bDir
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|primaryExtensions
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|a
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|aDir
argument_list|)
decl_stmt|;
name|Directory
name|b
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|bDir
argument_list|)
decl_stmt|;
name|FileSwitchDirectory
name|switchDir
init|=
operator|new
name|FileSwitchDirectory
argument_list|(
name|primaryExtensions
argument_list|,
name|a
argument_list|,
name|b
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
name|switchDir
argument_list|)
return|;
block|}
comment|// LUCENE-3380 -- make sure we get exception if the directory really does not exist.
DECL|method|testNoDir
specifier|public
name|void
name|testNoDir
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|primDir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|File
name|secondDir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|primDir
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|secondDir
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newFSSwitchDirectory
argument_list|(
name|primDir
argument_list|,
name|secondDir
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchDirectoryException
name|nsde
parameter_list|)
block|{
comment|// expected
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3380 test that we can add a file, and then when we call list() we get it back
DECL|method|testDirectoryFilter
specifier|public
name|void
name|testDirectoryFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newFSSwitchDirectory
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|name
init|=
literal|"file"
decl_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-3380 test that delegate compound files correctly.
DECL|method|testCompoundFileAppendTwice
specifier|public
name|void
name|testCompoundFileAppendTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|newDir
init|=
name|newFSSwitchDirectory
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"cfs"
argument_list|)
argument_list|)
decl_stmt|;
name|CompoundFileDirectory
name|csw
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|createSequenceFile
argument_list|(
name|newDir
argument_list|,
literal|"d1"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|csw
operator|.
name|createOutput
argument_list|(
literal|"d.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|newDir
operator|.
name|copy
argument_list|(
name|csw
argument_list|,
literal|"d1"
argument_list|,
literal|"d1"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"file does already exist"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|csw
operator|.
name|close
argument_list|()
expr_stmt|;
name|CompoundFileDirectory
name|cfr
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|cfr
operator|.
name|close
argument_list|()
expr_stmt|;
name|newDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a file of the specified size with sequential data. The first    *  byte is written as the start byte provided. All subsequent bytes are    *  computed as start + offset where offset is the number of the byte.    */
DECL|method|createSequenceFile
specifier|private
name|void
name|createSequenceFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|byte
name|start
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|os
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|os
operator|.
name|writeByte
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|start
operator|++
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
