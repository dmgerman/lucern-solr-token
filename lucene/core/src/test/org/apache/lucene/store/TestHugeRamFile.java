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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
begin_comment
comment|/** Test huge RAMFile with more than Integer.MAX_VALUE bytes. */
end_comment
begin_class
DECL|class|TestHugeRamFile
specifier|public
class|class
name|TestHugeRamFile
extends|extends
name|LuceneTestCase
block|{
DECL|field|MAX_VALUE
specifier|private
specifier|static
specifier|final
name|long
name|MAX_VALUE
init|=
operator|(
name|long
operator|)
literal|2
operator|*
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** Fake a huge ram file by using the same byte buffer for all     * buffers under maxint. */
DECL|class|DenseRAMFile
specifier|private
specifier|static
class|class
name|DenseRAMFile
extends|extends
name|RAMFile
block|{
DECL|field|capacity
specifier|private
name|long
name|capacity
init|=
literal|0
decl_stmt|;
DECL|field|singleBuffers
specifier|private
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|byte
index|[]
argument_list|>
name|singleBuffers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|newBuffer
specifier|protected
name|byte
index|[]
name|newBuffer
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|capacity
operator|+=
name|size
expr_stmt|;
if|if
condition|(
name|capacity
operator|<=
name|MAX_VALUE
condition|)
block|{
comment|// below maxint we reuse buffers
name|byte
name|buf
index|[]
init|=
name|singleBuffers
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
name|buf
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
comment|//System.out.println("allocate: "+size);
name|singleBuffers
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
return|;
block|}
comment|//System.out.println("allocate: "+size); System.out.flush();
return|return
operator|new
name|byte
index|[
name|size
index|]
return|;
block|}
block|}
comment|/** Test huge RAMFile with more than Integer.MAX_VALUE bytes. (LUCENE-957) */
DECL|method|testHugeFile
specifier|public
name|void
name|testHugeFile
parameter_list|()
throws|throws
name|IOException
block|{
name|DenseRAMFile
name|f
init|=
operator|new
name|DenseRAMFile
argument_list|()
decl_stmt|;
comment|// output part
name|RAMOutputStream
name|out
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|byte
name|b1
index|[]
init|=
operator|new
name|byte
index|[
name|RAMOutputStream
operator|.
name|BUFFER_SIZE
index|]
decl_stmt|;
name|byte
name|b2
index|[]
init|=
operator|new
name|byte
index|[
name|RAMOutputStream
operator|.
name|BUFFER_SIZE
operator|/
literal|3
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
name|b1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b1
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x0007F
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b2
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x0003F
argument_list|)
expr_stmt|;
block|}
name|long
name|n
init|=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
literal|"output length must match"
argument_list|,
name|n
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|n
operator|<=
name|MAX_VALUE
operator|-
name|b1
operator|.
name|length
condition|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|b1
argument_list|,
literal|0
argument_list|,
name|b1
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|n
operator|+=
name|b1
operator|.
name|length
expr_stmt|;
name|assertEquals
argument_list|(
literal|"output length must match"
argument_list|,
name|n
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("after writing b1's, length = "+out.length()+" (MAX_VALUE="+MAX_VALUE+")");
name|int
name|m
init|=
name|b2
operator|.
name|length
decl_stmt|;
name|long
name|L
init|=
literal|12
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|L
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b2
index|[
name|i
index|]
operator|++
expr_stmt|;
block|}
name|out
operator|.
name|writeBytes
argument_list|(
name|b2
argument_list|,
literal|0
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|n
operator|+=
name|m
expr_stmt|;
name|assertEquals
argument_list|(
literal|"output length must match"
argument_list|,
name|n
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// input part
name|RAMInputStream
name|in
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"testcase"
argument_list|,
name|f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"input length must match"
argument_list|,
name|n
argument_list|,
name|in
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("input length = "+in.length()+" % 1024 = "+in.length()%1024);
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|L
condition|;
name|j
operator|++
control|)
block|{
name|long
name|loc
init|=
name|n
operator|-
operator|(
name|L
operator|-
name|j
operator|)
operator|*
name|m
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|loc
operator|/
literal|3
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|loc
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
name|m
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|bt
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|byte
name|expected
init|=
call|(
name|byte
call|)
argument_list|(
literal|1
operator|+
name|j
operator|+
operator|(
name|i
operator|&
literal|0x0003F
operator|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"must read same value that was written! j="
operator|+
name|j
operator|+
literal|" i="
operator|+
name|i
argument_list|,
name|expected
argument_list|,
name|bt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
