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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_comment
comment|/** An abstract base class for simple, character-oriented tokenizers.*/
end_comment
begin_class
DECL|class|CharTokenizer
specifier|public
specifier|abstract
class|class
name|CharTokenizer
extends|extends
name|Tokenizer
block|{
DECL|method|CharTokenizer
specifier|public
name|CharTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|field|offset
DECL|field|bufferIndex
DECL|field|dataLen
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|,
name|bufferIndex
init|=
literal|0
decl_stmt|,
name|dataLen
init|=
literal|0
decl_stmt|;
DECL|field|MAX_WORD_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WORD_LEN
init|=
literal|255
decl_stmt|;
DECL|field|IO_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|IO_BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|MAX_WORD_LEN
index|]
decl_stmt|;
DECL|field|ioBuffer
specifier|private
specifier|final
name|char
index|[]
name|ioBuffer
init|=
operator|new
name|char
index|[
name|IO_BUFFER_SIZE
index|]
decl_stmt|;
comment|/** Returns true iff a character should be included in a token.  This    * tokenizer generates as tokens adjacent sequences of characters which    * satisfy this predicate.  Characters for which this is false are used to    * define token boundaries and are not included in tokens. */
DECL|method|isTokenChar
specifier|protected
specifier|abstract
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
function_decl|;
comment|/** Called on each token character to normalize it before it is added to the    * token.  The default implementation does nothing.  Subclasses may use this    * to, e.g., lowercase tokens. */
DECL|method|normalize
specifier|protected
name|char
name|normalize
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|c
return|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|int
name|length
init|=
literal|0
decl_stmt|;
name|int
name|start
init|=
name|offset
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|char
name|c
decl_stmt|;
name|offset
operator|++
expr_stmt|;
if|if
condition|(
name|bufferIndex
operator|>=
name|dataLen
condition|)
block|{
name|dataLen
operator|=
name|input
operator|.
name|read
argument_list|(
name|ioBuffer
argument_list|)
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
empty_stmt|;
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
break|break;
else|else
return|return
literal|null
return|;
block|}
else|else
name|c
operator|=
operator|(
name|char
operator|)
name|ioBuffer
index|[
name|bufferIndex
operator|++
index|]
expr_stmt|;
if|if
condition|(
name|isTokenChar
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// if it's a token char
if|if
condition|(
name|length
operator|==
literal|0
condition|)
comment|// start of token
name|start
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|normalize
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// buffer it, normalized
if|if
condition|(
name|length
operator|==
name|MAX_WORD_LEN
condition|)
comment|// buffer overflow!
break|break;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
comment|// at non-Letter w/ chars
break|break;
comment|// return 'em
block|}
return|return
operator|new
name|Token
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|length
argument_list|)
return|;
block|}
block|}
end_class
end_unit
