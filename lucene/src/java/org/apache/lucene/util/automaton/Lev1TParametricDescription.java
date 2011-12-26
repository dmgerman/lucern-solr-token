begin_unit
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|// The following code was generated with the moman/finenight pkg
end_comment
begin_comment
comment|// This package is available under the MIT License, see NOTICE.txt
end_comment
begin_comment
comment|// for more details.
end_comment
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
name|automaton
operator|.
name|LevenshteinAutomata
operator|.
name|ParametricDescription
import|;
end_import
begin_comment
comment|/** Parametric description for generating a Levenshtein automaton of degree 1,      with transpositions as primitive edits */
end_comment
begin_class
DECL|class|Lev1TParametricDescription
class|class
name|Lev1TParametricDescription
extends|extends
name|ParametricDescription
block|{
annotation|@
name|Override
DECL|method|transition
name|int
name|transition
parameter_list|(
name|int
name|absState
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|vector
parameter_list|)
block|{
comment|// null absState should never be passed in
assert|assert
name|absState
operator|!=
operator|-
literal|1
assert|;
comment|// decode absState -> state, offset
name|int
name|state
init|=
name|absState
operator|/
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|offset
init|=
name|absState
operator|%
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
assert|assert
name|offset
operator|>=
literal|0
assert|;
if|if
condition|(
name|position
operator|==
name|w
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|2
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|2
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs0
argument_list|,
name|loc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates0
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|3
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|3
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs1
argument_list|,
name|loc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates1
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|2
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|6
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|6
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs2
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates2
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|state
operator|<
literal|6
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|6
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs3
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates3
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|state
operator|==
operator|-
literal|1
condition|)
block|{
comment|// null state
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
comment|// translate back to abs
return|return
name|state
operator|*
operator|(
name|w
operator|+
literal|1
operator|)
operator|+
name|offset
return|;
block|}
block|}
comment|// 1 vectors; 2 states per vector; array length = 2
DECL|field|toStates0
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates0
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x2L
block|}
decl_stmt|;
DECL|field|offsetIncrs0
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs0
init|=
operator|new
name|long
index|[]
comment|/*1 bits per value */
block|{
literal|0x0L
block|}
decl_stmt|;
comment|// 2 vectors; 3 states per vector; array length = 6
DECL|field|toStates1
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates1
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0xa43L
block|}
decl_stmt|;
DECL|field|offsetIncrs1
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs1
init|=
operator|new
name|long
index|[]
comment|/*1 bits per value */
block|{
literal|0x38L
block|}
decl_stmt|;
comment|// 4 vectors; 6 states per vector; array length = 24
DECL|field|toStates2
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates2
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0x3453491482140003L
block|,
literal|0x6dL
block|}
decl_stmt|;
DECL|field|offsetIncrs2
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs2
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x555555a20000L
block|}
decl_stmt|;
comment|// 8 vectors; 6 states per vector; array length = 48
DECL|field|toStates3
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates3
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0x21520854900c0003L
block|,
literal|0x5b4d19a24534916dL
block|,
literal|0xda34L
block|}
decl_stmt|;
DECL|field|offsetIncrs3
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs3
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x5555ae0a20fc0000L
block|,
literal|0x55555555L
block|}
decl_stmt|;
comment|// state map
comment|//   0 -> [(0, 0)]
comment|//   1 -> [(0, 1)]
comment|//   2 -> [(0, 1), (1, 1)]
comment|//   3 -> [(0, 1), (2, 1)]
comment|//   4 -> [t(0, 1), (0, 1), (1, 1), (2, 1)]
comment|//   5 -> [(0, 1), (1, 1), (2, 1)]
DECL|method|Lev1TParametricDescription
specifier|public
name|Lev1TParametricDescription
parameter_list|(
name|int
name|w
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|,
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|0
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
