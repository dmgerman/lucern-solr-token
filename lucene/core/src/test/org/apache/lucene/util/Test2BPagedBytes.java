begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|store
operator|.
name|MockDirectoryWrapper
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_class
annotation|@
name|Ignore
argument_list|(
literal|"You must increase heap to> 2 G to run this"
argument_list|)
DECL|class|Test2BPagedBytes
specifier|public
class|class
name|Test2BPagedBytes
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
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"test2BPagedBytes"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|PagedBytes
name|pb
init|=
operator|new
name|PagedBytes
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|IndexOutput
name|dataOutput
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|netBytes
init|=
literal|0
decl_stmt|;
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|long
name|lastFP
init|=
literal|0
decl_stmt|;
name|Random
name|r2
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
while|while
condition|(
name|netBytes
operator|<
literal|1.1
operator|*
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|int
name|numBytes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r2
argument_list|,
literal|1
argument_list|,
literal|32768
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|r2
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|dataOutput
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|long
name|fp
init|=
name|dataOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
assert|assert
name|fp
operator|==
name|lastFP
operator|+
name|numBytes
assert|;
name|lastFP
operator|=
name|fp
expr_stmt|;
name|netBytes
operator|+=
name|numBytes
expr_stmt|;
block|}
name|dataOutput
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|pb
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|pb
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|r2
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|netBytes
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|netBytes
operator|<
literal|1.1
operator|*
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|int
name|numBytes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r2
argument_list|,
literal|1
argument_list|,
literal|32768
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|r2
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|reader
operator|.
name|fillSlice
argument_list|(
name|actual
argument_list|,
name|netBytes
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|netBytes
operator|+=
name|numBytes
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
