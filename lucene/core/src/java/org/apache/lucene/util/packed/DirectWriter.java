begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
begin_comment
comment|/**   * Class for writing packed integers to be directly read from Directory.  * Integers can be read on-the-fly via {@link DirectReader}.  *<p>  * Unlike PackedInts, it optimizes for read i/o operations and supports&gt; 2B values.  * Example usage:  *<pre class="prettyprint">  *   int bitsPerValue = DirectWriter.bitsRequired(100); // values up to and including 100  *   IndexOutput output = dir.createOutput("packed", IOContext.DEFAULT);  *   DirectWriter writer = DirectWriter.getInstance(output, numberOfValues, bitsPerValue);  *   for (int i = 0; i&lt; numberOfValues; i++) {  *     writer.add(value);  *   }  *   writer.finish();  *   output.close();  *</pre>  * @see DirectReader  */
end_comment
begin_class
DECL|class|DirectWriter
specifier|public
specifier|final
class|class
name|DirectWriter
block|{
DECL|field|bitsPerValue
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|numValues
specifier|final
name|long
name|numValues
decl_stmt|;
DECL|field|output
specifier|final
name|IndexOutput
name|output
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|finished
name|boolean
name|finished
decl_stmt|;
comment|// for now, just use the existing writer under the hood
DECL|field|off
name|int
name|off
decl_stmt|;
DECL|field|nextBlocks
specifier|final
name|byte
index|[]
name|nextBlocks
decl_stmt|;
DECL|field|nextValues
specifier|final
name|long
index|[]
name|nextValues
decl_stmt|;
DECL|field|encoder
specifier|final
name|BulkOperation
name|encoder
decl_stmt|;
DECL|field|iterations
specifier|final
name|int
name|iterations
decl_stmt|;
DECL|method|DirectWriter
name|DirectWriter
parameter_list|(
name|IndexOutput
name|output
parameter_list|,
name|long
name|numValues
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
name|this
operator|.
name|numValues
operator|=
name|numValues
expr_stmt|;
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
name|encoder
operator|=
name|BulkOperation
operator|.
name|of
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|iterations
operator|=
name|encoder
operator|.
name|computeIterations
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|numValues
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|nextBlocks
operator|=
operator|new
name|byte
index|[
name|iterations
operator|*
name|encoder
operator|.
name|byteBlockCount
argument_list|()
index|]
expr_stmt|;
name|nextValues
operator|=
operator|new
name|long
index|[
name|iterations
operator|*
name|encoder
operator|.
name|byteValueCount
argument_list|()
index|]
expr_stmt|;
block|}
comment|/** Adds a value to this writer */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bitsPerValue
operator|==
literal|64
operator|||
operator|(
name|l
operator|>=
literal|0
operator|&&
name|l
operator|<=
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
operator|)
operator|:
name|bitsPerValue
assert|;
assert|assert
operator|!
name|finished
assert|;
if|if
condition|(
name|count
operator|>=
name|numValues
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Writing past end of stream"
argument_list|)
throw|;
block|}
name|nextValues
index|[
name|off
operator|++
index|]
operator|=
name|l
expr_stmt|;
if|if
condition|(
name|off
operator|==
name|nextValues
operator|.
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
DECL|method|flush
specifier|private
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|encoder
operator|.
name|encode
argument_list|(
name|nextValues
argument_list|,
literal|0
argument_list|,
name|nextBlocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
specifier|final
name|int
name|blockCount
init|=
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|off
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|nextBlocks
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|nextValues
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|off
operator|=
literal|0
expr_stmt|;
block|}
comment|/** finishes writing */
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|!=
name|numValues
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Wrong number of values added, expected: "
operator|+
name|numValues
operator|+
literal|", got: "
operator|+
name|count
argument_list|)
throw|;
block|}
assert|assert
operator|!
name|finished
assert|;
name|flush
argument_list|()
expr_stmt|;
comment|// pad for fast io: we actually only need this for certain BPV, but its just 3 bytes...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Returns an instance suitable for encoding {@code numValues} using {@code bitsPerValue} */
DECL|method|getInstance
specifier|public
specifier|static
name|DirectWriter
name|getInstance
parameter_list|(
name|IndexOutput
name|output
parameter_list|,
name|long
name|numValues
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
if|if
condition|(
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|SUPPORTED_BITS_PER_VALUE
argument_list|,
name|bitsPerValue
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported bitsPerValue "
operator|+
name|bitsPerValue
operator|+
literal|". Did you use bitsRequired?"
argument_list|)
throw|;
block|}
return|return
operator|new
name|DirectWriter
argument_list|(
name|output
argument_list|,
name|numValues
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
comment|/**     * Round a number of bits per value to the next amount of bits per value that    * is supported by this writer.    *     * @param bitsRequired the amount of bits required    * @return the next number of bits per value that is gte the provided value    *         and supported by this writer    */
DECL|method|roundBits
specifier|private
specifier|static
name|int
name|roundBits
parameter_list|(
name|int
name|bitsRequired
parameter_list|)
block|{
name|int
name|index
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|SUPPORTED_BITS_PER_VALUE
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
name|SUPPORTED_BITS_PER_VALUE
index|[
operator|-
name|index
operator|-
literal|1
index|]
return|;
block|}
else|else
block|{
return|return
name|bitsRequired
return|;
block|}
block|}
comment|/**    * Returns how many bits are required to hold values up    * to and including maxValue    *    * @param maxValue the maximum value that should be representable.    * @return the amount of bits needed to represent values from 0 to maxValue.    * @see PackedInts#bitsRequired(long)    */
DECL|method|bitsRequired
specifier|public
specifier|static
name|int
name|bitsRequired
parameter_list|(
name|long
name|maxValue
parameter_list|)
block|{
return|return
name|roundBits
argument_list|(
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxValue
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns how many bits are required to hold values up    * to and including maxValue, interpreted as an unsigned value.    *    * @param maxValue the maximum value that should be representable.    * @return the amount of bits needed to represent values from 0 to maxValue.    * @see PackedInts#unsignedBitsRequired(long)    */
DECL|method|unsignedBitsRequired
specifier|public
specifier|static
name|int
name|unsignedBitsRequired
parameter_list|(
name|long
name|maxValue
parameter_list|)
block|{
return|return
name|roundBits
argument_list|(
name|PackedInts
operator|.
name|unsignedBitsRequired
argument_list|(
name|maxValue
argument_list|)
argument_list|)
return|;
block|}
DECL|field|SUPPORTED_BITS_PER_VALUE
specifier|final
specifier|static
name|int
name|SUPPORTED_BITS_PER_VALUE
index|[]
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|12
block|,
literal|16
block|,
literal|20
block|,
literal|24
block|,
literal|28
block|,
literal|32
block|,
literal|40
block|,
literal|48
block|,
literal|56
block|,
literal|64
block|}
decl_stmt|;
block|}
end_class
end_unit
