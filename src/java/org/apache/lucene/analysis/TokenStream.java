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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Payload
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/** A TokenStream enumerates the sequence of tokens, either from   fields of a document or from query text.<p>   This is an abstract class.  Concrete subclasses are:<ul><li>{@link Tokenizer}, a TokenStream   whose input is a Reader; and<li>{@link TokenFilter}, a TokenStream   whose input is another TokenStream.</ul>   NOTE: subclasses must override at least one of {@link   #next()} or {@link #next(Token)}.   */
end_comment
begin_class
DECL|class|TokenStream
specifier|public
specifier|abstract
class|class
name|TokenStream
block|{
comment|/** Returns the next token in the stream, or null at EOS.    *  The returned Token is a "full private copy" (not    *  re-used across calls to next()) but will be slower    *  than calling {@link #next(Token)} instead.. */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|result
init|=
name|next
argument_list|(
operator|new
name|Token
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|Payload
name|p
init|=
name|result
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|setPayload
argument_list|(
operator|(
name|Payload
operator|)
name|p
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/** Returns the next token in the stream, or null at EOS.    *  When possible, the input Token should be used as the    *  returned Token (this gives fastest tokenization    *  performance), but this is not required and a new Token    *  may be returned. Callers may re-use a single Token    *  instance for successive calls to this method.    *<p>    *  This implicitly defines a "contract" between     *  consumers (callers of this method) and     *  producers (implementations of this method     *  that are the source for tokens):    *<ul>    *<li>A consumer must fully consume the previously     *       returned Token before calling this method again.</li>    *<li>A producer must call {@link Token#clear()}    *       before setting the fields in it& returning it</li>    *</ul>    *  Note that a {@link TokenFilter} is considered a consumer.    *  @param result a Token that may or may not be used to return    *  @return next token in the stream or null if end-of-stream was hit    */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
name|Token
name|result
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
argument_list|()
return|;
block|}
comment|/** Resets this stream to the beginning. This is an    *  optional operation, so subclasses may or may not    *  implement this method. Reset() is not needed for    *  the standard indexing process. However, if the Tokens     *  of a TokenStream are intended to be consumed more than     *  once, it is necessary to implement reset().     */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/** Releases resources associated with this stream. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class
end_unit
