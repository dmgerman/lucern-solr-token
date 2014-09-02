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
name|compressing
operator|.
name|CompressingStoredFieldsWriter
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
name|TestUtil
import|;
end_import
begin_class
DECL|class|TestFileSwitchDirectory
specifier|public
class|class
name|TestFileSwitchDirectory
extends|extends
name|BaseDirectoryTestCase
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
argument_list|<>
argument_list|()
decl_stmt|;
name|fileExtensions
operator|.
name|add
argument_list|(
name|CompressingStoredFieldsWriter
operator|.
name|FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|fileExtensions
operator|.
name|add
argument_list|(
name|CompressingStoredFieldsWriter
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
comment|// for now we wire Lucene410Codec because we rely upon its specific impl
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
literal|"Lucene410"
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
name|createTempDir
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|File
name|secondDir
init|=
name|createTempDir
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
return|return
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
name|createTempDir
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|File
name|secondDir
init|=
name|createTempDir
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|rm
argument_list|(
name|primDir
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|rm
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
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|extensions
operator|.
name|add
argument_list|(
literal|"cfs"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
literal|"prx"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
literal|"frq"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
literal|"tip"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
literal|"tim"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
literal|"del"
argument_list|)
expr_stmt|;
block|}
return|return
name|newFSSwitchDirectory
argument_list|(
name|extensions
argument_list|)
return|;
block|}
block|}
end_class
end_unit
