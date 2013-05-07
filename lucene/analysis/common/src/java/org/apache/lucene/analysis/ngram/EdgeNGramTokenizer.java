begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
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
name|PositionIncrementAttribute
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
name|ArrayUtil
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Tokenizes the input from an edge into n-grams of given size(s).  *<p>  * This {@link Tokenizer} create n-grams from the beginning edge of a input token.  */
end_comment
begin_class
DECL|class|EdgeNGramTokenizer
specifier|public
specifier|final
class|class
name|EdgeNGramTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_MAX_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_GRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MIN_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_GRAM_SIZE
init|=
literal|1
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
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|minGram
specifier|private
name|int
name|minGram
decl_stmt|;
DECL|field|maxGram
specifier|private
name|int
name|maxGram
decl_stmt|;
DECL|field|gramSize
specifier|private
name|int
name|gramSize
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
decl_stmt|;
DECL|field|inLen
specifier|private
name|int
name|inLen
decl_stmt|;
comment|// length of the input AFTER trim()
DECL|field|charsRead
specifier|private
name|int
name|charsRead
decl_stmt|;
comment|// length of the input
DECL|field|inStr
specifier|private
name|String
name|inStr
decl_stmt|;
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param version the Lucene match version    * @param input {@link Reader} holding the input to be tokenized    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|,
name|Reader
name|input
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|version
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param version the Lucene match version    * @param factory {@link org.apache.lucene.util.AttributeSource.AttributeFactory} to use    * @param input {@link Reader} holding the input to be tokenized    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
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
name|version
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Version
name|version
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"version must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minGram
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must be greater than zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minGram
operator|>
name|maxGram
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must not be greater than maxGram"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minGram
operator|=
name|minGram
expr_stmt|;
name|this
operator|.
name|maxGram
operator|=
name|maxGram
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
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
comment|// if we are just starting, read the whole input
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|started
operator|=
literal|true
expr_stmt|;
name|gramSize
operator|=
name|minGram
expr_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|Math
operator|.
name|min
argument_list|(
literal|1024
argument_list|,
name|maxGram
argument_list|)
index|]
decl_stmt|;
name|charsRead
operator|=
literal|0
expr_stmt|;
comment|// TODO: refactor to a shared readFully somewhere:
name|boolean
name|exhausted
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|charsRead
operator|<
name|maxGram
condition|)
block|{
specifier|final
name|int
name|inc
init|=
name|input
operator|.
name|read
argument_list|(
name|chars
argument_list|,
name|charsRead
argument_list|,
name|chars
operator|.
name|length
operator|-
name|charsRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|inc
operator|==
operator|-
literal|1
condition|)
block|{
name|exhausted
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|charsRead
operator|+=
name|inc
expr_stmt|;
if|if
condition|(
name|charsRead
operator|==
name|chars
operator|.
name|length
operator|&&
name|charsRead
operator|<
name|maxGram
condition|)
block|{
name|chars
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|chars
argument_list|)
expr_stmt|;
block|}
block|}
name|inStr
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|charsRead
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|exhausted
condition|)
block|{
comment|// Read extra throwaway chars so that on end() we
comment|// report the correct offset:
name|char
index|[]
name|throwaway
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|inc
init|=
name|input
operator|.
name|read
argument_list|(
name|throwaway
argument_list|,
literal|0
argument_list|,
name|throwaway
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|inc
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|charsRead
operator|+=
name|inc
expr_stmt|;
block|}
block|}
name|inLen
operator|=
name|inStr
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|inLen
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// if the remaining input is too short, we can't generate any n-grams
if|if
condition|(
name|gramSize
operator|>
name|inLen
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// if we have hit the end of our n-gram size range, quit
if|if
condition|(
name|gramSize
operator|>
name|maxGram
operator|||
name|gramSize
operator|>
name|inLen
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// grab gramSize chars from front or back
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|inStr
argument_list|,
literal|0
argument_list|,
name|gramSize
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
name|correctOffset
argument_list|(
name|gramSize
argument_list|)
argument_list|)
expr_stmt|;
name|gramSize
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
specifier|final
name|int
name|finalOffset
init|=
name|correctOffset
argument_list|(
name|charsRead
argument_list|)
decl_stmt|;
name|this
operator|.
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
name|started
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class
end_unit
