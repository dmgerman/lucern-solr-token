begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
import|;
end_import
begin_comment
comment|/**  * Removes elisions from a {@link TokenStream}. For example, "l'avion" (the plane) will be  * tokenized as "avion" (plane).  *   * @see<a href="http://fr.wikipedia.org/wiki/%C3%89lision">Elision in Wikipedia</a>  */
end_comment
begin_class
DECL|class|ElisionFilter
specifier|public
specifier|final
class|class
name|ElisionFilter
extends|extends
name|TokenFilter
block|{
DECL|field|articles
specifier|private
specifier|final
name|CharArraySet
name|articles
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Constructs an elision filter with a Set of stop words    * @param input the source {@link TokenStream}    * @param articles a set of stopword articles    */
DECL|method|ElisionFilter
specifier|public
name|ElisionFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|CharArraySet
name|articles
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|articles
operator|=
name|articles
expr_stmt|;
block|}
comment|/**    * Increments the {@link TokenStream} with a {@link CharTermAttribute} without elisioned start    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|termLength
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|index
init|=
operator|-
literal|1
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
name|termLength
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|termBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\''
operator|||
name|ch
operator|==
literal|'\u2019'
condition|)
block|{
name|index
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
comment|// An apostrophe has been found. If the prefix is an article strip it off.
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
name|articles
operator|.
name|contains
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|index
argument_list|)
condition|)
block|{
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|termBuffer
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|termLength
operator|-
operator|(
name|index
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class
end_unit
