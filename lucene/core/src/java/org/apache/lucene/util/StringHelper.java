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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Properties
import|;
end_import
begin_comment
comment|/**  * Methods for manipulating strings.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|StringHelper
specifier|public
specifier|abstract
class|class
name|StringHelper
block|{
comment|/**    * Compares two {@link BytesRef}, element by element, and returns the    * number of elements common to both arrays (from the start of each).    *    * @param left The first {@link BytesRef} to compare    * @param right The second {@link BytesRef} to compare    * @return The number of common elements (from the start of each).    */
DECL|method|bytesDifference
specifier|public
specifier|static
name|int
name|bytesDifference
parameter_list|(
name|BytesRef
name|left
parameter_list|,
name|BytesRef
name|right
parameter_list|)
block|{
name|int
name|len
init|=
name|left
operator|.
name|length
operator|<
name|right
operator|.
name|length
condition|?
name|left
operator|.
name|length
else|:
name|right
operator|.
name|length
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytesLeft
init|=
name|left
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|offLeft
init|=
name|left
operator|.
name|offset
decl_stmt|;
name|byte
index|[]
name|bytesRight
init|=
name|right
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|offRight
init|=
name|right
operator|.
name|offset
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
name|len
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|bytesLeft
index|[
name|i
operator|+
name|offLeft
index|]
operator|!=
name|bytesRight
index|[
name|i
operator|+
name|offRight
index|]
condition|)
return|return
name|i
return|;
return|return
name|len
return|;
block|}
comment|/**     * Returns the length of {@code currentTerm} needed for use as a sort key.    * so that {@link BytesRef#compareTo(BytesRef)} still returns the same result.    * This method assumes currentTerm comes after priorTerm.    */
DECL|method|sortKeyLength
specifier|public
specifier|static
name|int
name|sortKeyLength
parameter_list|(
specifier|final
name|BytesRef
name|priorTerm
parameter_list|,
specifier|final
name|BytesRef
name|currentTerm
parameter_list|)
block|{
specifier|final
name|int
name|currentTermOffset
init|=
name|currentTerm
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|priorTermOffset
init|=
name|priorTerm
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|priorTerm
operator|.
name|length
argument_list|,
name|currentTerm
operator|.
name|length
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|priorTerm
operator|.
name|bytes
index|[
name|priorTermOffset
operator|+
name|i
index|]
operator|!=
name|currentTerm
operator|.
name|bytes
index|[
name|currentTermOffset
operator|+
name|i
index|]
condition|)
block|{
return|return
name|i
operator|+
literal|1
return|;
block|}
block|}
return|return
name|Math
operator|.
name|min
argument_list|(
literal|1
operator|+
name|priorTerm
operator|.
name|length
argument_list|,
name|currentTerm
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|StringHelper
specifier|private
name|StringHelper
parameter_list|()
block|{   }
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
return|return
name|s2
operator|==
literal|null
return|;
block|}
else|else
block|{
return|return
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns<code>true</code> iff the ref starts with the given prefix.    * Otherwise<code>false</code>.    *     * @param ref    *         the {@code byte[]} to test    * @param prefix    *         the expected prefix    * @return Returns<code>true</code> iff the ref starts with the given prefix.    *         Otherwise<code>false</code>.    */
DECL|method|startsWith
specifier|public
specifier|static
name|boolean
name|startsWith
parameter_list|(
name|byte
index|[]
name|ref
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|.
name|length
operator|<
name|prefix
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
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
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ref
index|[
name|i
index|]
operator|!=
name|prefix
operator|.
name|bytes
index|[
name|prefix
operator|.
name|offset
operator|+
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns<code>true</code> iff the ref starts with the given prefix.    * Otherwise<code>false</code>.    *     * @param ref    *          the {@link BytesRef} to test    * @param prefix    *          the expected prefix    * @return Returns<code>true</code> iff the ref starts with the given prefix.    *         Otherwise<code>false</code>.    */
DECL|method|startsWith
specifier|public
specifier|static
name|boolean
name|startsWith
parameter_list|(
name|BytesRef
name|ref
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
name|sliceEquals
argument_list|(
name|ref
argument_list|,
name|prefix
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Returns<code>true</code> iff the ref ends with the given suffix. Otherwise    *<code>false</code>.    *     * @param ref    *          the {@link BytesRef} to test    * @param suffix    *          the expected suffix    * @return Returns<code>true</code> iff the ref ends with the given suffix.    *         Otherwise<code>false</code>.    */
DECL|method|endsWith
specifier|public
specifier|static
name|boolean
name|endsWith
parameter_list|(
name|BytesRef
name|ref
parameter_list|,
name|BytesRef
name|suffix
parameter_list|)
block|{
return|return
name|sliceEquals
argument_list|(
name|ref
argument_list|,
name|suffix
argument_list|,
name|ref
operator|.
name|length
operator|-
name|suffix
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|sliceEquals
specifier|private
specifier|static
name|boolean
name|sliceEquals
parameter_list|(
name|BytesRef
name|sliceToTest
parameter_list|,
name|BytesRef
name|other
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|sliceToTest
operator|.
name|length
operator|-
name|pos
operator|<
name|other
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
name|sliceToTest
operator|.
name|offset
operator|+
name|pos
decl_stmt|;
name|int
name|j
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|k
init|=
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|k
condition|)
block|{
if|if
condition|(
name|sliceToTest
operator|.
name|bytes
index|[
name|i
operator|++
index|]
operator|!=
name|other
operator|.
name|bytes
index|[
name|j
operator|++
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** Pass this as the seed to {@link #murmurhash3_x86_32}. */
comment|// Poached from Guava: set a different salt/seed
comment|// for each JVM instance, to frustrate hash key collision
comment|// denial of service attacks, and to catch any places that
comment|// somehow rely on hash function/order across JVM
comment|// instances:
DECL|field|GOOD_FAST_HASH_SEED
specifier|public
specifier|static
specifier|final
name|int
name|GOOD_FAST_HASH_SEED
decl_stmt|;
static|static
block|{
name|String
name|prop
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.seed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
comment|// So if there is a test failure that relied on hash
comment|// order, we remain reproducible based on the test seed:
name|GOOD_FAST_HASH_SEED
operator|=
name|prop
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|GOOD_FAST_HASH_SEED
operator|=
operator|(
name|int
operator|)
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the MurmurHash3_x86_32 hash.    * Original source/tests at https://github.com/yonik/java_util/    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|murmurhash3_x86_32
specifier|public
specifier|static
name|int
name|murmurhash3_x86_32
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
specifier|final
name|int
name|c1
init|=
literal|0xcc9e2d51
decl_stmt|;
specifier|final
name|int
name|c2
init|=
literal|0x1b873593
decl_stmt|;
name|int
name|h1
init|=
name|seed
decl_stmt|;
name|int
name|roundedEnd
init|=
name|offset
operator|+
operator|(
name|len
operator|&
literal|0xfffffffc
operator|)
decl_stmt|;
comment|// round down to 4 byte block
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|roundedEnd
condition|;
name|i
operator|+=
literal|4
control|)
block|{
comment|// little endian load order
name|int
name|k1
init|=
operator|(
name|data
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|i
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|i
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
name|data
index|[
name|i
operator|+
literal|3
index|]
operator|<<
literal|24
operator|)
decl_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|k1
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
name|h1
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h1
argument_list|,
literal|13
argument_list|)
expr_stmt|;
name|h1
operator|=
name|h1
operator|*
literal|5
operator|+
literal|0xe6546b64
expr_stmt|;
block|}
comment|// tail
name|int
name|k1
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|len
operator|&
literal|0x03
condition|)
block|{
case|case
literal|3
case|:
name|k1
operator|=
operator|(
name|data
index|[
name|roundedEnd
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
expr_stmt|;
comment|// fallthrough
case|case
literal|2
case|:
name|k1
operator||=
operator|(
name|data
index|[
name|roundedEnd
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
expr_stmt|;
comment|// fallthrough
case|case
literal|1
case|:
name|k1
operator||=
operator|(
name|data
index|[
name|roundedEnd
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|k1
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
block|}
comment|// finalization
name|h1
operator|^=
name|len
expr_stmt|;
comment|// fmix(h1);
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
name|h1
operator|*=
literal|0x85ebca6b
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|13
expr_stmt|;
name|h1
operator|*=
literal|0xc2b2ae35
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
return|return
name|h1
return|;
block|}
DECL|method|murmurhash3_x86_32
specifier|public
specifier|static
name|int
name|murmurhash3_x86_32
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
return|return
name|murmurhash3_x86_32
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|seed
argument_list|)
return|;
block|}
comment|// Holds 128 bit unsigned value:
DECL|field|nextId
specifier|private
specifier|static
name|BigInteger
name|nextId
decl_stmt|;
DECL|field|mask128
specifier|private
specifier|static
specifier|final
name|BigInteger
name|mask128
decl_stmt|;
DECL|field|idLock
specifier|private
specifier|static
specifier|final
name|Object
name|idLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
static|static
block|{
comment|// 128 bit unsigned mask
name|byte
index|[]
name|maskBytes128
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|maskBytes128
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|mask128
operator|=
operator|new
name|BigInteger
argument_list|(
literal|1
argument_list|,
name|maskBytes128
argument_list|)
expr_stmt|;
name|String
name|prop
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.seed"
argument_list|)
decl_stmt|;
comment|// State for xorshift128:
name|long
name|x0
decl_stmt|;
name|long
name|x1
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
comment|// So if there is a test failure that somehow relied on this id,
comment|// we remain reproducible based on the test seed:
if|if
condition|(
name|prop
operator|.
name|length
argument_list|()
operator|>
literal|8
condition|)
block|{
name|prop
operator|=
name|prop
operator|.
name|substring
argument_list|(
name|prop
operator|.
name|length
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
block|}
name|x0
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|prop
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|x1
operator|=
name|x0
expr_stmt|;
block|}
else|else
block|{
comment|// seed from /dev/urandom, if its available
try|try
init|(
name|DataInputStream
name|is
init|=
operator|new
name|DataInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"/dev/urandom"
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|x0
operator|=
name|is
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|x1
operator|=
name|is
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|unavailable
parameter_list|)
block|{
comment|// may not be available on this platform
comment|// fall back to lower quality randomness from 3 different sources:
name|x0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|x1
operator|=
name|StringHelper
operator|.
name|class
operator|.
name|hashCode
argument_list|()
operator|<<
literal|32
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Properties can vary across JVM instances:
try|try
block|{
name|Properties
name|p
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|p
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|p
operator|.
name|getProperty
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|x1
operator||=
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|notallowed
parameter_list|)
block|{
comment|// getting Properties requires wildcard read-write: may not be allowed
name|x1
operator||=
name|StringBuffer
operator|.
name|class
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Use a few iterations of xorshift128 to scatter the seed
comment|// in case multiple Lucene instances starting up "near" the same
comment|// nanoTime, since we use ++ (mod 2^128) for full period cycle:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|long
name|s1
init|=
name|x0
decl_stmt|;
name|long
name|s0
init|=
name|x1
decl_stmt|;
name|x0
operator|=
name|s0
expr_stmt|;
name|s1
operator|^=
name|s1
operator|<<
literal|23
expr_stmt|;
comment|// a
name|x1
operator|=
name|s1
operator|^
name|s0
operator|^
operator|(
name|s1
operator|>>>
literal|17
operator|)
operator|^
operator|(
name|s0
operator|>>>
literal|26
operator|)
expr_stmt|;
comment|// b, c
block|}
comment|// 64-bit unsigned mask
name|byte
index|[]
name|maskBytes64
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|maskBytes64
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|BigInteger
name|mask64
init|=
operator|new
name|BigInteger
argument_list|(
literal|1
argument_list|,
name|maskBytes64
argument_list|)
decl_stmt|;
comment|// First make unsigned versions of x0, x1:
name|BigInteger
name|unsignedX0
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|x0
argument_list|)
operator|.
name|and
argument_list|(
name|mask64
argument_list|)
decl_stmt|;
name|BigInteger
name|unsignedX1
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|x1
argument_list|)
operator|.
name|and
argument_list|(
name|mask64
argument_list|)
decl_stmt|;
comment|// Concatentate bits of x0 and x1, as unsigned 128 bit integer:
name|nextId
operator|=
name|unsignedX0
operator|.
name|shiftLeft
argument_list|(
literal|64
argument_list|)
operator|.
name|or
argument_list|(
name|unsignedX1
argument_list|)
expr_stmt|;
block|}
comment|/** length in bytes of an ID */
DECL|field|ID_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|ID_LENGTH
init|=
literal|16
decl_stmt|;
comment|/** Generates a non-cryptographic globally unique id. */
DECL|method|randomId
specifier|public
specifier|static
name|byte
index|[]
name|randomId
parameter_list|()
block|{
comment|// NOTE: we don't use Java's UUID.randomUUID() implementation here because:
comment|//
comment|//   * It's overkill for our usage: it tries to be cryptographically
comment|//     secure, whereas for this use we don't care if someone can
comment|//     guess the IDs.
comment|//
comment|//   * It uses SecureRandom, which on Linux can easily take a long time
comment|//     (I saw ~ 10 seconds just running a Lucene test) when entropy
comment|//     harvesting is falling behind.
comment|//
comment|//   * It loses a few (6) bits to version and variant and it's not clear
comment|//     what impact that has on the period, whereas the simple ++ (mod 2^128)
comment|//     we use here is guaranteed to have the full period.
name|byte
name|bits
index|[]
decl_stmt|;
synchronized|synchronized
init|(
name|idLock
init|)
block|{
name|bits
operator|=
name|nextId
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|nextId
operator|=
name|nextId
operator|.
name|add
argument_list|(
name|BigInteger
operator|.
name|ONE
argument_list|)
operator|.
name|and
argument_list|(
name|mask128
argument_list|)
expr_stmt|;
block|}
comment|// toByteArray() always returns a sign bit, so it may require an extra byte (always zero)
if|if
condition|(
name|bits
operator|.
name|length
operator|>
name|ID_LENGTH
condition|)
block|{
assert|assert
name|bits
operator|.
name|length
operator|==
name|ID_LENGTH
operator|+
literal|1
assert|;
assert|assert
name|bits
index|[
literal|0
index|]
operator|==
literal|0
assert|;
return|return
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|bits
argument_list|,
literal|1
argument_list|,
name|bits
operator|.
name|length
argument_list|)
return|;
block|}
else|else
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|ID_LENGTH
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|result
operator|.
name|length
operator|-
name|bits
operator|.
name|length
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|/**     * Helper method to render an ID as a string, for debugging    *<p>    * Returns the string {@code (null)} if the id is null.    * Otherwise, returns a string representation for debugging.    * Never throws an exception. The returned string may    * indicate if the id is definitely invalid.    */
DECL|method|idToString
specifier|public
specifier|static
name|String
name|idToString
parameter_list|(
name|byte
name|id
index|[]
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
literal|"(null)"
return|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|new
name|BigInteger
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
operator|.
name|toString
argument_list|(
name|Character
operator|.
name|MAX_RADIX
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|length
operator|!=
name|ID_LENGTH
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" (INVALID FORMAT)"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** Just converts each int in the incoming {@link IntsRef} to each byte    *  in the returned {@link BytesRef}, throwing {@code IllegalArgumentException}    *  if any int value is out of bounds for a byte. */
DECL|method|intsRefToBytesRef
specifier|public
specifier|static
name|BytesRef
name|intsRefToBytesRef
parameter_list|(
name|IntsRef
name|ints
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|ints
operator|.
name|length
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|x
init|=
name|ints
operator|.
name|ints
index|[
name|ints
operator|.
name|offset
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|x
argument_list|<
literal|0
operator|||
name|x
argument_list|>
literal|255
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"int at pos="
operator|+
name|i
operator|+
literal|" with value="
operator|+
name|x
operator|+
literal|" is out-of-bounds for byte"
argument_list|)
throw|;
block|}
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|x
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|/** Compares a fixed length slice of two byte arrays interpreted as    *  big-endian unsigned values.  Returns positive int if a&gt; b,    *  negative int if a&lt; b and 0 if a == b */
DECL|method|compare
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|int
name|count
parameter_list|,
name|byte
index|[]
name|a
parameter_list|,
name|int
name|aOffset
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|bOffset
parameter_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|cmp
init|=
operator|(
name|a
index|[
name|aOffset
operator|+
name|i
index|]
operator|&
literal|0xff
operator|)
operator|-
operator|(
name|b
index|[
name|bOffset
operator|+
name|i
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
end_class
end_unit
