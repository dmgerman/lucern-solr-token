begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
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
name|Tokenizer
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
name|TypeAttribute
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
comment|/**  * Tokenizes input text into sentences.  *<p>  * The output tokens can then be broken into words with {@link WordTokenFilter}  *</p>  * @lucene.experimental  * @deprecated Use {@link HMMChineseTokenizer} instead  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|SentenceTokenizer
specifier|public
specifier|final
class|class
name|SentenceTokenizer
extends|extends
name|Tokenizer
block|{
comment|/**    * End of sentence punctuation: ãï¼ï¼ï¼ï¼,!?;    */
DECL|field|PUNCTION
specifier|private
specifier|final
specifier|static
name|String
name|PUNCTION
init|=
literal|"ãï¼ï¼ï¼ï¼,!?;"
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|tokenStart
DECL|field|tokenEnd
specifier|private
name|int
name|tokenStart
init|=
literal|0
decl_stmt|,
name|tokenEnd
init|=
literal|0
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
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SentenceTokenizer
specifier|public
name|SentenceTokenizer
parameter_list|()
block|{   }
DECL|method|SentenceTokenizer
specifier|public
name|SentenceTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|ci
decl_stmt|;
name|char
name|ch
decl_stmt|,
name|pch
decl_stmt|;
name|boolean
name|atBegin
init|=
literal|true
decl_stmt|;
name|tokenStart
operator|=
name|tokenEnd
expr_stmt|;
name|ci
operator|=
name|input
operator|.
name|read
argument_list|()
expr_stmt|;
name|ch
operator|=
operator|(
name|char
operator|)
name|ci
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|ci
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|PUNCTION
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// End of a sentence
name|buffer
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|tokenEnd
operator|++
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|atBegin
operator|&&
name|Utility
operator|.
name|SPACES
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|tokenStart
operator|++
expr_stmt|;
name|tokenEnd
operator|++
expr_stmt|;
name|ci
operator|=
name|input
operator|.
name|read
argument_list|()
expr_stmt|;
name|ch
operator|=
operator|(
name|char
operator|)
name|ci
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|atBegin
operator|=
literal|false
expr_stmt|;
name|tokenEnd
operator|++
expr_stmt|;
name|pch
operator|=
name|ch
expr_stmt|;
name|ci
operator|=
name|input
operator|.
name|read
argument_list|()
expr_stmt|;
name|ch
operator|=
operator|(
name|char
operator|)
name|ci
expr_stmt|;
comment|// Two spaces, such as CR, LF
if|if
condition|(
name|Utility
operator|.
name|SPACES
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|!=
operator|-
literal|1
operator|&&
name|Utility
operator|.
name|SPACES
operator|.
name|indexOf
argument_list|(
name|pch
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// buffer.append(ch);
name|tokenEnd
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|false
return|;
else|else
block|{
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|tokenStart
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|tokenEnd
argument_list|)
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"sentence"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tokenStart
operator|=
name|tokenEnd
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// set final offset
specifier|final
name|int
name|finalOffset
init|=
name|correctOffset
argument_list|(
name|tokenEnd
argument_list|)
decl_stmt|;
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
block|}
end_class
end_unit
