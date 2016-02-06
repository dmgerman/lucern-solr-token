begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import
begin_class
DECL|class|AbstractTestLZ4CompressionMode
specifier|public
specifier|abstract
class|class
name|AbstractTestLZ4CompressionMode
extends|extends
name|AbstractTestCompressionMode
block|{
annotation|@
name|Override
DECL|method|test
specifier|public
name|byte
index|[]
name|test
parameter_list|(
name|byte
index|[]
name|decompressed
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|compressed
init|=
name|super
operator|.
name|test
argument_list|(
name|decompressed
argument_list|)
decl_stmt|;
name|int
name|off
init|=
literal|0
decl_stmt|;
name|int
name|decompressedOff
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|token
init|=
name|compressed
index|[
name|off
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|literalLen
init|=
name|token
operator|>>>
literal|4
decl_stmt|;
if|if
condition|(
name|literalLen
operator|==
literal|0x0F
condition|)
block|{
while|while
condition|(
name|compressed
index|[
name|off
index|]
operator|==
operator|(
name|byte
operator|)
literal|0xFF
condition|)
block|{
name|literalLen
operator|+=
literal|0xFF
expr_stmt|;
operator|++
name|off
expr_stmt|;
block|}
name|literalLen
operator|+=
name|compressed
index|[
name|off
operator|++
index|]
operator|&
literal|0xFF
expr_stmt|;
block|}
comment|// skip literals
name|off
operator|+=
name|literalLen
expr_stmt|;
name|decompressedOff
operator|+=
name|literalLen
expr_stmt|;
comment|// check that the stream ends with literals and that there are at least
comment|// 5 of them
if|if
condition|(
name|off
operator|==
name|compressed
operator|.
name|length
condition|)
block|{
name|assertEquals
argument_list|(
name|decompressed
operator|.
name|length
argument_list|,
name|decompressedOff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"lastLiterals="
operator|+
name|literalLen
operator|+
literal|", bytes="
operator|+
name|decompressed
operator|.
name|length
argument_list|,
name|literalLen
operator|>=
name|LZ4
operator|.
name|LAST_LITERALS
operator|||
name|literalLen
operator|==
name|decompressed
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
block|}
specifier|final
name|int
name|matchDec
init|=
operator|(
name|compressed
index|[
name|off
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator||
operator|(
operator|(
name|compressed
index|[
name|off
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
decl_stmt|;
comment|// check that match dec is not 0
name|assertTrue
argument_list|(
name|matchDec
operator|+
literal|" "
operator|+
name|decompressedOff
argument_list|,
name|matchDec
operator|>
literal|0
operator|&&
name|matchDec
operator|<=
name|decompressedOff
argument_list|)
expr_stmt|;
name|int
name|matchLen
init|=
name|token
operator|&
literal|0x0F
decl_stmt|;
if|if
condition|(
name|matchLen
operator|==
literal|0x0F
condition|)
block|{
while|while
condition|(
name|compressed
index|[
name|off
index|]
operator|==
operator|(
name|byte
operator|)
literal|0xFF
condition|)
block|{
name|matchLen
operator|+=
literal|0xFF
expr_stmt|;
operator|++
name|off
expr_stmt|;
block|}
name|matchLen
operator|+=
name|compressed
index|[
name|off
operator|++
index|]
operator|&
literal|0xFF
expr_stmt|;
block|}
name|matchLen
operator|+=
name|LZ4
operator|.
name|MIN_MATCH
expr_stmt|;
comment|// if the match ends prematurely, the next sequence should not have
comment|// literals or this means we are wasting space
if|if
condition|(
name|decompressedOff
operator|+
name|matchLen
operator|<
name|decompressed
operator|.
name|length
operator|-
name|LZ4
operator|.
name|LAST_LITERALS
condition|)
block|{
specifier|final
name|boolean
name|moreCommonBytes
init|=
name|decompressed
index|[
name|decompressedOff
operator|+
name|matchLen
index|]
operator|==
name|decompressed
index|[
name|decompressedOff
operator|-
name|matchDec
operator|+
name|matchLen
index|]
decl_stmt|;
specifier|final
name|boolean
name|nextSequenceHasLiterals
init|=
operator|(
operator|(
name|compressed
index|[
name|off
index|]
operator|&
literal|0xFF
operator|)
operator|>>>
literal|4
operator|)
operator|!=
literal|0
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|moreCommonBytes
operator|||
operator|!
name|nextSequenceHasLiterals
argument_list|)
expr_stmt|;
block|}
name|decompressedOff
operator|+=
name|matchLen
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|decompressed
operator|.
name|length
argument_list|,
name|decompressedOff
argument_list|)
expr_stmt|;
return|return
name|compressed
return|;
block|}
DECL|method|testShortLiteralsAndMatchs
specifier|public
name|void
name|testShortLiteralsAndMatchs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// literals and matchs lengths<= 15
specifier|final
name|byte
index|[]
name|decompressed
init|=
literal|"1234562345673456745678910123"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongMatchs
specifier|public
name|void
name|testLongMatchs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// match length>= 20
specifier|final
name|byte
index|[]
name|decompressed
init|=
operator|new
name|byte
index|[
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|300
argument_list|,
literal|1024
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
name|decompressed
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|decompressed
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
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongLiterals
specifier|public
name|void
name|testLongLiterals
parameter_list|()
throws|throws
name|IOException
block|{
comment|// long literals (length>= 16) which are not the last literals
specifier|final
name|byte
index|[]
name|decompressed
init|=
name|randomArray
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|400
argument_list|,
literal|1024
argument_list|)
argument_list|,
literal|256
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchRef
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchOff
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|decompressed
operator|.
name|length
operator|-
literal|40
argument_list|,
name|decompressed
operator|.
name|length
operator|-
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchLength
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|decompressed
argument_list|,
name|matchRef
argument_list|,
name|decompressed
argument_list|,
name|matchOff
argument_list|,
name|matchLength
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
DECL|method|testMatchRightBeforeLastLiterals
specifier|public
name|void
name|testMatchRightBeforeLastLiterals
parameter_list|()
throws|throws
name|IOException
block|{
name|test
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
