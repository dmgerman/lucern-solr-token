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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Removes stop words from a token stream.  */
end_comment
begin_class
DECL|class|StopFilter
specifier|public
specifier|final
class|class
name|StopFilter
extends|extends
name|TokenFilter
block|{
DECL|field|stopWords
specifier|private
name|Set
name|stopWords
decl_stmt|;
comment|/**    * Constructs a filter which removes words from the input    * TokenStream that are named in the array of words.    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|makeStopSet
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a filter which removes words from the input    * TokenStream that are named in the Hashtable.    *    * @deprecated Use {@link #StopFilter(TokenStream, Set)} instead    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Hashtable
name|stopTable
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stopWords
operator|=
operator|new
name|HashSet
argument_list|(
name|stopTable
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a filter which removes words from the input    * TokenStream that are named in the Set.    * It is crucial that an efficient Set implementation is used    * for maximum performance.    *    * @see #makeStopSet(java.lang.String[])    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Set
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
block|}
comment|/**    * Builds a Hashtable from an array of stop words,    * appropriate for passing into the StopFilter constructor.    * This permits this table construction to be cached once when    * an Analyzer is constructed.    *    * @deprecated Use {@link #makeStopSet(String[])} instead.    */
DECL|method|makeStopTable
specifier|public
specifier|static
specifier|final
name|Hashtable
name|makeStopTable
parameter_list|(
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|Hashtable
name|stopTable
init|=
operator|new
name|Hashtable
argument_list|(
name|stopWords
operator|.
name|length
argument_list|)
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
name|stopWords
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|stopTable
operator|.
name|put
argument_list|(
name|stopWords
index|[
name|i
index|]
argument_list|,
name|stopWords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|stopTable
return|;
block|}
comment|/**    * Builds a Set from an array of stop words,    * appropriate for passing into the StopFilter constructor.    * This permits this stopWords construction to be cached once when    * an Analyzer is constructed.    */
DECL|method|makeStopSet
specifier|public
specifier|static
specifier|final
name|Set
name|makeStopSet
parameter_list|(
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|HashSet
name|stopTable
init|=
operator|new
name|HashSet
argument_list|(
name|stopWords
operator|.
name|length
argument_list|)
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
name|stopWords
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|stopTable
operator|.
name|add
argument_list|(
name|stopWords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|stopTable
return|;
block|}
comment|/**    * Returns the next input Token whose termText() is not a stop word.    */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// return the first non-stop word found
for|for
control|(
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
init|;
name|token
operator|!=
literal|null
condition|;
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
control|)
if|if
condition|(
operator|!
name|stopWords
operator|.
name|contains
argument_list|(
name|token
operator|.
name|termText
argument_list|)
condition|)
return|return
name|token
return|;
comment|// reached EOS -- return null
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
