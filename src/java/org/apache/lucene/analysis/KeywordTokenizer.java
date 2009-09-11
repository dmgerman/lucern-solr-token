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
name|io
operator|.
name|Reader
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
name|OffsetAttribute
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
name|TermAttribute
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
name|AttributeSource
import|;
end_import
begin_comment
comment|/**  * Emits the entire input as a single token.  */
end_comment
begin_class
DECL|class|KeywordTokenizer
specifier|public
class|class
name|KeywordTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|256
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
decl_stmt|;
DECL|field|finalOffset
specifier|private
name|int
name|finalOffset
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|method|KeywordTokenizer
specifier|public
name|KeywordTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|KeywordTokenizer
specifier|public
name|KeywordTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|KeywordTokenizer
specifier|public
name|KeywordTokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|KeywordTokenizer
specifier|public
name|KeywordTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|done
operator|=
literal|false
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
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
operator|!
name|done
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|length
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|upto
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|upto
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
break|break;
name|upto
operator|+=
name|length
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|buffer
operator|.
name|length
condition|)
name|buffer
operator|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
literal|1
operator|+
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
return|;
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|done
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class
end_unit
