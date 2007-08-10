begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A simple class that can store& retrieve char[]'s in a  * hash table.  Note that this is not a general purpose  * class.  For example, it cannot remove char[]'s from the  * set, nor does it resize its hash table to be smaller,  * etc.  It is designed for use with StopFilter to enable  * quick filtering based on the char[] termBuffer in a  * Token.  */
end_comment
begin_class
DECL|class|CharArraySet
specifier|final
class|class
name|CharArraySet
block|{
DECL|field|INIT_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|INIT_SIZE
init|=
literal|8
decl_stmt|;
DECL|field|MAX_LOAD_FACTOR
specifier|private
specifier|final
specifier|static
name|double
name|MAX_LOAD_FACTOR
init|=
literal|0.75
decl_stmt|;
DECL|field|mask
specifier|private
name|int
name|mask
decl_stmt|;
DECL|field|entries
specifier|private
name|char
index|[]
index|[]
name|entries
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
comment|/** Create set with enough capacity to hold startSize    *  terms */
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|int
name|startSize
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|int
name|size
init|=
name|INIT_SIZE
decl_stmt|;
while|while
condition|(
operator|(
operator|(
name|double
operator|)
name|startSize
operator|)
operator|/
name|size
operator|>=
name|MAX_LOAD_FACTOR
condition|)
name|size
operator|*=
literal|2
expr_stmt|;
name|mask
operator|=
name|size
operator|-
literal|1
expr_stmt|;
name|entries
operator|=
operator|new
name|char
index|[
name|size
index|]
index|[]
expr_stmt|;
block|}
comment|/** Returns true if the characters in text up to length    *  len is present in the set. */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|code
init|=
name|getHashCode
argument_list|(
name|text
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|code
operator|&
name|mask
decl_stmt|;
name|char
index|[]
name|text2
init|=
name|entries
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|text2
operator|!=
literal|null
operator|&&
operator|!
name|equals
argument_list|(
name|text
argument_list|,
name|len
argument_list|,
name|text2
argument_list|)
condition|)
block|{
specifier|final
name|int
name|inc
init|=
name|code
operator|*
literal|1347
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|pos
operator|=
name|code
operator|&
name|mask
expr_stmt|;
name|text2
operator|=
name|entries
index|[
name|pos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|text2
operator|!=
literal|null
operator|&&
operator|!
name|equals
argument_list|(
name|text
argument_list|,
name|len
argument_list|,
name|text2
argument_list|)
condition|)
do|;
block|}
return|return
name|text2
operator|!=
literal|null
return|;
block|}
comment|/** Add this String into the set */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|add
argument_list|(
name|text
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Add this text into the set */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|char
index|[]
name|text
parameter_list|)
block|{
if|if
condition|(
name|ignoreCase
condition|)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|text
index|[
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|text
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|int
name|code
init|=
name|getHashCode
argument_list|(
name|text
argument_list|,
name|text
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|code
operator|&
name|mask
decl_stmt|;
name|char
index|[]
name|text2
init|=
name|entries
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|text2
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|inc
init|=
name|code
operator|*
literal|1347
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|pos
operator|=
name|code
operator|&
name|mask
expr_stmt|;
name|text2
operator|=
name|entries
index|[
name|pos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|text2
operator|!=
literal|null
condition|)
do|;
block|}
name|entries
index|[
name|pos
index|]
operator|=
name|text
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
operator|(
operator|(
name|double
operator|)
name|count
operator|)
operator|/
name|entries
operator|.
name|length
operator|>
name|MAX_LOAD_FACTOR
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|equals
specifier|private
name|boolean
name|equals
parameter_list|(
name|char
index|[]
name|text1
parameter_list|,
name|int
name|len
parameter_list|,
name|char
index|[]
name|text2
parameter_list|)
block|{
if|if
condition|(
name|len
operator|!=
name|text2
operator|.
name|length
condition|)
return|return
literal|false
return|;
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
block|{
if|if
condition|(
name|ignoreCase
condition|)
block|{
if|if
condition|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|text1
index|[
name|i
index|]
argument_list|)
operator|!=
name|text2
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|text1
index|[
name|i
index|]
operator|!=
name|text2
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
block|{
specifier|final
name|int
name|newSize
init|=
literal|2
operator|*
name|count
decl_stmt|;
name|mask
operator|=
name|newSize
operator|-
literal|1
expr_stmt|;
name|char
index|[]
index|[]
name|newEntries
init|=
operator|new
name|char
index|[
name|newSize
index|]
index|[]
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
name|entries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
index|[]
name|text
init|=
name|entries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|int
name|code
init|=
name|getHashCode
argument_list|(
name|text
argument_list|,
name|text
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|code
operator|&
name|mask
decl_stmt|;
if|if
condition|(
name|newEntries
index|[
name|pos
index|]
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|inc
init|=
name|code
operator|*
literal|1347
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|pos
operator|=
name|code
operator|&
name|mask
expr_stmt|;
block|}
do|while
condition|(
name|newEntries
index|[
name|pos
index|]
operator|!=
literal|null
condition|)
do|;
block|}
name|newEntries
index|[
name|pos
index|]
operator|=
name|text
expr_stmt|;
block|}
block|}
name|entries
operator|=
name|newEntries
expr_stmt|;
block|}
DECL|method|getHashCode
specifier|private
name|int
name|getHashCode
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|downto
init|=
name|len
decl_stmt|;
name|int
name|code
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|downto
operator|>
literal|0
condition|)
block|{
specifier|final
name|char
name|c
decl_stmt|;
if|if
condition|(
name|ignoreCase
condition|)
name|c
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|text
index|[
operator|--
name|downto
index|]
argument_list|)
expr_stmt|;
else|else
name|c
operator|=
name|text
index|[
operator|--
name|downto
index|]
expr_stmt|;
name|code
operator|=
operator|(
name|code
operator|*
literal|31
operator|)
operator|+
name|c
expr_stmt|;
block|}
return|return
name|code
return|;
block|}
block|}
end_class
end_unit
