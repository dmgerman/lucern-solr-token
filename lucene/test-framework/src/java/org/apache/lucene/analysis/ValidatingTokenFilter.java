begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
import|;
end_import
begin_comment
comment|// TODO: rename to OffsetsXXXTF?  ie we only validate
end_comment
begin_comment
comment|// offsets (now anyway...)
end_comment
begin_comment
comment|// TODO: also make a DebuggingTokenFilter, that just prints
end_comment
begin_comment
comment|// all att values that come through it...
end_comment
begin_comment
comment|// TODO: BTSTC should just append this to the chain
end_comment
begin_comment
comment|// instead of checking itself:
end_comment
begin_comment
comment|/** A TokenFilter that checks consistency of the tokens (eg  *  offsets are consistent with one another). */
end_comment
begin_class
DECL|class|ValidatingTokenFilter
specifier|public
specifier|final
class|class
name|ValidatingTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
name|lastStartOffset
decl_stmt|;
comment|// Maps position to the start/end offset:
DECL|field|posToStartOffset
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|posToStartOffset
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|posToEndOffset
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|posToEndOffset
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posLenAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLenAtt
init|=
name|getAttribute
argument_list|(
name|PositionLengthAttribute
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
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetsAreCorrect
specifier|private
specifier|final
name|boolean
name|offsetsAreCorrect
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/** The name arg is used to identify this stage when    *  throwing exceptions (useful if you have more than one    *  instance in your chain). */
DECL|method|ValidatingTokenFilter
specifier|public
name|ValidatingTokenFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|offsetsAreCorrect
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|offsetsAreCorrect
operator|=
name|offsetsAreCorrect
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
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|startOffset
init|=
literal|0
decl_stmt|;
name|int
name|endOffset
init|=
literal|0
decl_stmt|;
name|int
name|posLen
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|posIncAtt
operator|!=
literal|null
condition|)
block|{
name|pos
operator|+=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"first posInc must be> 0"
argument_list|)
throw|;
block|}
block|}
comment|// System.out.println("  got token=" + termAtt + " pos=" + pos);
if|if
condition|(
name|offsetAtt
operator|!=
literal|null
condition|)
block|{
name|startOffset
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
if|if
condition|(
name|offsetsAreCorrect
operator|&&
name|offsetAtt
operator|.
name|startOffset
argument_list|()
operator|<
name|lastStartOffset
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|name
operator|+
literal|": offsets must not go backwards startOffset="
operator|+
name|startOffset
operator|+
literal|" is< lastStartOffset="
operator|+
name|lastStartOffset
argument_list|)
throw|;
block|}
name|lastStartOffset
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
block|}
name|posLen
operator|=
name|posLenAtt
operator|==
literal|null
condition|?
literal|1
else|:
name|posLenAtt
operator|.
name|getPositionLength
argument_list|()
expr_stmt|;
if|if
condition|(
name|offsetAtt
operator|!=
literal|null
operator|&&
name|posIncAtt
operator|!=
literal|null
operator|&&
name|offsetsAreCorrect
condition|)
block|{
if|if
condition|(
operator|!
name|posToStartOffset
operator|.
name|containsKey
argument_list|(
name|pos
argument_list|)
condition|)
block|{
comment|// First time we've seen a token leaving from this position:
name|posToStartOffset
operator|.
name|put
argument_list|(
name|pos
argument_list|,
name|startOffset
argument_list|)
expr_stmt|;
comment|//System.out.println("  + s " + pos + " -> " + startOffset);
block|}
else|else
block|{
comment|// We've seen a token leaving from this position
comment|// before; verify the startOffset is the same:
comment|//System.out.println("  + vs " + pos + " -> " + startOffset);
specifier|final
name|int
name|oldStartOffset
init|=
name|posToStartOffset
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldStartOffset
operator|!=
name|startOffset
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|name
operator|+
literal|": inconsistent startOffset at pos="
operator|+
name|pos
operator|+
literal|": "
operator|+
name|oldStartOffset
operator|+
literal|" vs "
operator|+
name|startOffset
operator|+
literal|"; token="
operator|+
name|termAtt
argument_list|)
throw|;
block|}
block|}
specifier|final
name|int
name|endPos
init|=
name|pos
operator|+
name|posLen
decl_stmt|;
if|if
condition|(
operator|!
name|posToEndOffset
operator|.
name|containsKey
argument_list|(
name|endPos
argument_list|)
condition|)
block|{
comment|// First time we've seen a token arriving to this position:
name|posToEndOffset
operator|.
name|put
argument_list|(
name|endPos
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
comment|//System.out.println("  + e " + endPos + " -> " + endOffset);
block|}
else|else
block|{
comment|// We've seen a token arriving to this position
comment|// before; verify the endOffset is the same:
comment|//System.out.println("  + ve " + endPos + " -> " + endOffset);
specifier|final
name|int
name|oldEndOffset
init|=
name|posToEndOffset
operator|.
name|get
argument_list|(
name|endPos
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldEndOffset
operator|!=
name|endOffset
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|name
operator|+
literal|": inconsistent endOffset at pos="
operator|+
name|endPos
operator|+
literal|": "
operator|+
name|oldEndOffset
operator|+
literal|" vs "
operator|+
name|endOffset
operator|+
literal|"; token="
operator|+
name|termAtt
argument_list|)
throw|;
block|}
block|}
block|}
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
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// TODO: what else to validate
comment|// TODO: check that endOffset is>= max(endOffset)
comment|// we've seen
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
name|pos
operator|=
operator|-
literal|1
expr_stmt|;
name|posToStartOffset
operator|.
name|clear
argument_list|()
expr_stmt|;
name|posToEndOffset
operator|.
name|clear
argument_list|()
expr_stmt|;
name|lastStartOffset
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class
end_unit
