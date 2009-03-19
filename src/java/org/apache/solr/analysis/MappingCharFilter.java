begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|LinkedList
import|;
end_import
begin_comment
comment|/**  *  * @version $Id$  * @since Solr 1.4  *  */
end_comment
begin_class
DECL|class|MappingCharFilter
specifier|public
class|class
name|MappingCharFilter
extends|extends
name|BaseCharFilter
block|{
DECL|field|normMap
specifier|private
specifier|final
name|NormalizeMap
name|normMap
decl_stmt|;
DECL|field|buffer
specifier|private
name|LinkedList
argument_list|<
name|Character
argument_list|>
name|buffer
decl_stmt|;
DECL|field|replacement
specifier|private
name|String
name|replacement
decl_stmt|;
DECL|field|charPointer
specifier|private
name|int
name|charPointer
decl_stmt|;
DECL|field|nextCharCounter
specifier|private
name|int
name|nextCharCounter
decl_stmt|;
DECL|method|MappingCharFilter
specifier|public
name|MappingCharFilter
parameter_list|(
name|NormalizeMap
name|normMap
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|normMap
operator|=
name|normMap
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|replacement
operator|!=
literal|null
operator|&&
name|charPointer
operator|<
name|replacement
operator|.
name|length
argument_list|()
condition|)
return|return
name|replacement
operator|.
name|charAt
argument_list|(
name|charPointer
operator|++
argument_list|)
return|;
name|int
name|firstChar
init|=
name|nextChar
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstChar
operator|==
operator|-
literal|1
condition|)
return|return
operator|-
literal|1
return|;
name|NormalizeMap
name|nm
init|=
name|normMap
operator|.
name|submap
operator|!=
literal|null
condition|?
name|normMap
operator|.
name|submap
operator|.
name|get
argument_list|(
operator|(
name|char
operator|)
name|firstChar
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|nm
operator|==
literal|null
condition|)
return|return
name|firstChar
return|;
name|NormalizeMap
name|result
init|=
name|match
argument_list|(
name|nm
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
return|return
name|firstChar
return|;
name|replacement
operator|=
name|result
operator|.
name|normStr
expr_stmt|;
name|charPointer
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|diff
operator|!=
literal|0
condition|)
block|{
name|int
name|prevCumulativeDiff
init|=
name|getLastCumulativeDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|diff
operator|<
literal|0
condition|)
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
operator|-
name|result
operator|.
name|diff
condition|;
name|i
operator|++
control|)
name|addOffCorrectMap
argument_list|(
name|nextCharCounter
operator|+
name|i
operator|-
name|prevCumulativeDiff
argument_list|,
name|prevCumulativeDiff
operator|-
literal|1
operator|-
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addOffCorrectMap
argument_list|(
name|nextCharCounter
operator|-
name|result
operator|.
name|diff
operator|-
name|prevCumulativeDiff
argument_list|,
name|prevCumulativeDiff
operator|+
name|result
operator|.
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|nextChar
specifier|private
name|int
name|nextChar
parameter_list|()
throws|throws
name|IOException
block|{
name|nextCharCounter
operator|++
expr_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
operator|&&
operator|!
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|buffer
operator|.
name|removeFirst
argument_list|()
return|;
return|return
name|input
operator|.
name|read
argument_list|()
return|;
block|}
DECL|method|pushChar
specifier|private
name|void
name|pushChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
name|nextCharCounter
operator|--
expr_stmt|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
name|buffer
operator|=
operator|new
name|LinkedList
argument_list|<
name|Character
argument_list|>
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|addFirst
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|pushLastChar
specifier|private
name|void
name|pushLastChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
name|buffer
operator|=
operator|new
name|LinkedList
argument_list|<
name|Character
argument_list|>
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|addLast
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|match
specifier|private
name|NormalizeMap
name|match
parameter_list|(
name|NormalizeMap
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|NormalizeMap
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|submap
operator|!=
literal|null
condition|)
block|{
name|int
name|chr
init|=
name|nextChar
argument_list|()
decl_stmt|;
if|if
condition|(
name|chr
operator|!=
operator|-
literal|1
condition|)
block|{
name|NormalizeMap
name|subMap
init|=
name|map
operator|.
name|submap
operator|.
name|get
argument_list|(
operator|(
name|char
operator|)
name|chr
argument_list|)
decl_stmt|;
if|if
condition|(
name|subMap
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|match
argument_list|(
name|subMap
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
name|pushChar
argument_list|(
name|chr
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|&&
name|map
operator|.
name|normStr
operator|!=
literal|null
condition|)
name|result
operator|=
name|map
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|char
index|[]
name|tmp
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
name|int
name|l
init|=
name|input
operator|.
name|read
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
operator|-
literal|1
condition|)
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
name|l
condition|;
name|i
operator|++
control|)
name|pushLastChar
argument_list|(
name|tmp
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|l
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off
operator|+
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
break|break;
name|cbuf
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
name|l
operator|++
expr_stmt|;
block|}
return|return
name|l
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|l
return|;
block|}
DECL|method|markSupported
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readAheadLimit
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mark/reset not supported"
argument_list|)
throw|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mark/reset not supported"
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
