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
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
DECL|class|TestByteSlices
specifier|public
class|class
name|TestByteSlices
extends|extends
name|LuceneTestCase
block|{
DECL|class|ByteBlockAllocator
specifier|private
specifier|static
class|class
name|ByteBlockAllocator
extends|extends
name|ByteBlockPool
operator|.
name|Allocator
block|{
DECL|field|freeByteBlocks
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|freeByteBlocks
init|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|/* Allocate another byte[] from the shared pool */
annotation|@
name|Override
DECL|method|getByteBlock
specifier|synchronized
name|byte
index|[]
name|getByteBlock
parameter_list|()
block|{
specifier|final
name|int
name|size
init|=
name|freeByteBlocks
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|size
condition|)
name|b
operator|=
operator|new
name|byte
index|[
name|DocumentsWriterRAMAllocator
operator|.
name|BYTE_BLOCK_SIZE
index|]
expr_stmt|;
else|else
name|b
operator|=
name|freeByteBlocks
operator|.
name|remove
argument_list|(
name|size
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/* Return a byte[] to the pool */
annotation|@
name|Override
DECL|method|recycleByteBlocks
specifier|synchronized
name|void
name|recycleByteBlocks
parameter_list|(
name|byte
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|freeByteBlocks
operator|.
name|add
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recycleByteBlocks
specifier|synchronized
name|void
name|recycleByteBlocks
parameter_list|(
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|blocks
operator|.
name|size
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
name|size
condition|;
name|i
operator|++
control|)
name|freeByteBlocks
operator|.
name|add
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Throwable
block|{
name|ByteBlockPool
name|pool
init|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockAllocator
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_STREAM
init|=
literal|25
decl_stmt|;
name|ByteSliceWriter
name|writer
init|=
operator|new
name|ByteSliceWriter
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|NUM_STREAM
index|]
decl_stmt|;
name|int
index|[]
name|uptos
init|=
operator|new
name|int
index|[
name|NUM_STREAM
index|]
decl_stmt|;
name|int
index|[]
name|counters
init|=
operator|new
name|int
index|[
name|NUM_STREAM
index|]
decl_stmt|;
name|Random
name|r
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|ByteSliceReader
name|reader
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ti
init|=
literal|0
init|;
name|ti
operator|<
literal|100
condition|;
name|ti
operator|++
control|)
block|{
for|for
control|(
name|int
name|stream
init|=
literal|0
init|;
name|stream
operator|<
name|NUM_STREAM
condition|;
name|stream
operator|++
control|)
block|{
name|starts
index|[
name|stream
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|counters
index|[
name|stream
index|]
operator|=
literal|0
expr_stmt|;
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10000
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|stream
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|NUM_STREAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"write stream="
operator|+
name|stream
argument_list|)
expr_stmt|;
if|if
condition|(
name|starts
index|[
name|stream
index|]
operator|==
operator|-
literal|1
condition|)
block|{
specifier|final
name|int
name|spot
init|=
name|pool
operator|.
name|newSlice
argument_list|(
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|)
decl_stmt|;
name|starts
index|[
name|stream
index|]
operator|=
name|uptos
index|[
name|stream
index|]
operator|=
name|spot
operator|+
name|pool
operator|.
name|byteOffset
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  init to "
operator|+
name|starts
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|init
argument_list|(
name|uptos
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
name|int
name|numValue
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
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
name|numValue
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    write "
operator|+
operator|(
name|counters
index|[
name|stream
index|]
operator|+
name|j
operator|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeVInt
argument_list|(
name|counters
index|[
name|stream
index|]
operator|+
name|j
argument_list|)
expr_stmt|;
comment|//writer.writeVInt(ti);
block|}
name|counters
index|[
name|stream
index|]
operator|+=
name|numValue
expr_stmt|;
name|uptos
index|[
name|stream
index|]
operator|=
name|writer
operator|.
name|getAddress
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    addr now "
operator|+
name|uptos
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|stream
init|=
literal|0
init|;
name|stream
operator|<
name|NUM_STREAM
condition|;
name|stream
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  stream="
operator|+
name|stream
operator|+
literal|" count="
operator|+
name|counters
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|starts
index|[
name|stream
index|]
operator|!=
name|uptos
index|[
name|stream
index|]
condition|)
block|{
name|reader
operator|.
name|init
argument_list|(
name|pool
argument_list|,
name|starts
index|[
name|stream
index|]
argument_list|,
name|uptos
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|counters
index|[
name|stream
index|]
condition|;
name|j
operator|++
control|)
name|assertEquals
argument_list|(
name|j
argument_list|,
name|reader
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(ti, reader.readVInt());
block|}
block|}
name|pool
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
