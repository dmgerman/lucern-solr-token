begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|DataInput
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
name|DataOutput
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
DECL|class|TestPagedBytes
specifier|public
class|class
name|TestPagedBytes
extends|extends
name|LuceneTestCase
block|{
comment|// Writes random byte/s to "normal" file in dir, then
comment|// copies into PagedBytes and verifies with
comment|// PagedBytes.Reader:
DECL|method|testDataInputOutput
specifier|public
name|void
name|testDataInputOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testOverflow"
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
specifier|final
name|int
name|blockBits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
literal|1
operator|<<
name|blockBits
decl_stmt|;
specifier|final
name|PagedBytes
name|p
init|=
operator|new
name|PagedBytes
argument_list|(
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|out
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
specifier|final
name|int
name|numBytes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|10000000
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|answer
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|int
name|written
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|written
operator|<
name|numBytes
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|answer
index|[
name|written
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|numBytes
operator|-
name|written
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|answer
argument_list|,
name|written
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|written
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
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
specifier|final
name|DataInput
name|in
init|=
name|input
operator|.
name|clone
argument_list|()
decl_stmt|;
name|p
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
specifier|final
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|p
operator|.
name|freeze
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|verify
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|read
operator|<
name|numBytes
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|verify
index|[
name|read
operator|++
index|]
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|numBytes
operator|-
name|read
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|verify
argument_list|,
name|read
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|read
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|answer
argument_list|,
name|verify
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|slice
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter2
init|=
literal|0
init|;
name|iter2
operator|<
literal|100
condition|;
name|iter2
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numBytes
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|blockSize
operator|+
literal|1
argument_list|,
name|numBytes
operator|-
name|pos
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|fillSlice
argument_list|(
name|slice
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteUpto
init|=
literal|0
init|;
name|byteUpto
operator|<
name|len
condition|;
name|byteUpto
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|answer
index|[
name|pos
operator|+
name|byteUpto
index|]
argument_list|,
name|slice
operator|.
name|bytes
index|[
name|slice
operator|.
name|offset
operator|+
name|byteUpto
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|input
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
block|}
comment|// Writes random byte/s into PagedBytes via
comment|// .getDataOutput(), then verifies with
comment|// PagedBytes.getDataInput():
DECL|method|testDataInputOutput2
specifier|public
name|void
name|testDataInputOutput2
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|blockBits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
literal|1
operator|<<
name|blockBits
decl_stmt|;
specifier|final
name|PagedBytes
name|p
init|=
operator|new
name|PagedBytes
argument_list|(
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|DataOutput
name|out
init|=
name|p
operator|.
name|getDataOutput
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numBytes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|answer
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|int
name|written
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|written
operator|<
name|numBytes
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|answer
index|[
name|written
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|numBytes
operator|-
name|written
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|answer
argument_list|,
name|written
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|written
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
specifier|final
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|p
operator|.
name|freeze
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DataInput
name|in
init|=
name|p
operator|.
name|getDataInput
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|verify
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|read
operator|<
name|numBytes
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|verify
index|[
name|read
operator|++
index|]
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|numBytes
operator|-
name|read
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|verify
argument_list|,
name|read
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|read
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|answer
argument_list|,
name|verify
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|slice
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter2
init|=
literal|0
init|;
name|iter2
operator|<
literal|100
condition|;
name|iter2
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numBytes
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|blockSize
operator|+
literal|1
argument_list|,
name|numBytes
operator|-
name|pos
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|fillSlice
argument_list|(
name|slice
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteUpto
init|=
literal|0
init|;
name|byteUpto
operator|<
name|len
condition|;
name|byteUpto
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|answer
index|[
name|pos
operator|+
name|byteUpto
index|]
argument_list|,
name|slice
operator|.
name|bytes
index|[
name|slice
operator|.
name|offset
operator|+
name|byteUpto
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Ignore
comment|// memory hole
DECL|method|testOverflow
specifier|public
name|void
name|testOverflow
parameter_list|()
throws|throws
name|IOException
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testOverflow"
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
specifier|final
name|int
name|blockBits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|14
argument_list|,
literal|28
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
literal|1
operator|<<
name|blockBits
decl_stmt|;
name|byte
index|[]
name|arr
init|=
operator|new
name|byte
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|blockSize
operator|/
literal|2
argument_list|,
name|blockSize
operator|*
literal|2
argument_list|)
index|]
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
name|arr
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
specifier|final
name|long
name|numBytes
init|=
operator|(
literal|1L
operator|<<
literal|31
operator|)
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|blockSize
operator|*
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|PagedBytes
name|p
init|=
operator|new
name|PagedBytes
argument_list|(
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|out
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
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBytes
condition|;
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|arr
operator|.
name|length
argument_list|,
name|numBytes
operator|-
name|i
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|arr
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|i
operator|+=
name|len
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numBytes
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexInput
name|in
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
name|p
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
specifier|final
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|p
operator|.
name|freeze
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|offset
range|:
operator|new
name|long
index|[]
block|{
literal|0L
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
name|numBytes
operator|-
literal|1
block|,
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numBytes
operator|-
literal|2
argument_list|)
block|}
control|)
block|{
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|reader
operator|.
name|fillSlice
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|arr
index|[
call|(
name|int
call|)
argument_list|(
name|offset
operator|%
name|arr
operator|.
name|length
argument_list|)
index|]
argument_list|,
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
name|in
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
block|}
end_class
end_unit
