begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|compound
operator|.
name|hyphenation
operator|.
name|Hyphenation
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
name|compound
operator|.
name|hyphenation
operator|.
name|HyphenationTree
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
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_comment
comment|/**  * A {@link TokenFilter} that decomposes compound words found in many Germanic languages,  * using pre-4.4 behavior.  *  * @deprecated Use {@link HyphenationCompoundWordTokenFilter}.  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene43HyphenationCompoundWordTokenFilter
specifier|public
class|class
name|Lucene43HyphenationCompoundWordTokenFilter
extends|extends
name|Lucene43CompoundWordTokenFilterBase
block|{
DECL|field|hyphenator
specifier|private
name|HyphenationTree
name|hyphenator
decl_stmt|;
comment|/**    * Creates a new {@link Lucene43HyphenationCompoundWordTokenFilter} instance.    *    * @param input    *          the {@link TokenStream} to process    * @param hyphenator    *          the hyphenation pattern tree to use for hyphenation    * @param dictionary    *          the word dictionary to match against.    */
DECL|method|Lucene43HyphenationCompoundWordTokenFilter
specifier|public
name|Lucene43HyphenationCompoundWordTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|HyphenationTree
name|hyphenator
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|hyphenator
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link Lucene43HyphenationCompoundWordTokenFilter} instance.    *    * @param input    *          the {@link TokenStream} to process    * @param hyphenator    *          the hyphenation pattern tree to use for hyphenation    * @param dictionary    *          the word dictionary to match against.    * @param minWordSize    *          only words longer than this get processed    * @param minSubwordSize    *          only subwords longer than this get to the output stream    * @param maxSubwordSize    *          only subwords shorter than this get to the output stream    * @param onlyLongestMatch    *          Add only the longest matching subword to the stream    */
DECL|method|Lucene43HyphenationCompoundWordTokenFilter
specifier|public
name|Lucene43HyphenationCompoundWordTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|HyphenationTree
name|hyphenator
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|dictionary
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
name|this
operator|.
name|hyphenator
operator|=
name|hyphenator
expr_stmt|;
block|}
comment|/**    * Create a HyphenationCompoundWordTokenFilter with no dictionary.    *<p>    * Calls {@link #Lucene43HyphenationCompoundWordTokenFilter(TokenStream, HyphenationTree, CharArraySet, int, int, int, boolean)    * HyphenationCompoundWordTokenFilter(matchVersion, input, hyphenator,    * null, minWordSize, minSubwordSize, maxSubwordSize }    */
DECL|method|Lucene43HyphenationCompoundWordTokenFilter
specifier|public
name|Lucene43HyphenationCompoundWordTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|HyphenationTree
name|hyphenator
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|hyphenator
argument_list|,
literal|null
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a HyphenationCompoundWordTokenFilter with no dictionary.    *<p>    * Calls {@link #Lucene43HyphenationCompoundWordTokenFilter(TokenStream, HyphenationTree, int, int, int)    * HyphenationCompoundWordTokenFilter(matchVersion, input, hyphenator,     * DEFAULT_MIN_WORD_SIZE, DEFAULT_MIN_SUBWORD_SIZE, DEFAULT_MAX_SUBWORD_SIZE }    */
DECL|method|Lucene43HyphenationCompoundWordTokenFilter
specifier|public
name|Lucene43HyphenationCompoundWordTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|HyphenationTree
name|hyphenator
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|hyphenator
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a hyphenator tree    *     * @param hyphenationFilename the filename of the XML grammar to load    * @return An object representing the hyphenation patterns    * @throws IOException If there is a low-level I/O error.    */
DECL|method|getHyphenationTree
specifier|public
specifier|static
name|HyphenationTree
name|getHyphenationTree
parameter_list|(
name|String
name|hyphenationFilename
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getHyphenationTree
argument_list|(
operator|new
name|InputSource
argument_list|(
name|hyphenationFilename
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create a hyphenator tree    *     * @param hyphenationSource the InputSource pointing to the XML grammar    * @return An object representing the hyphenation patterns    * @throws IOException If there is a low-level I/O error.    */
DECL|method|getHyphenationTree
specifier|public
specifier|static
name|HyphenationTree
name|getHyphenationTree
parameter_list|(
name|InputSource
name|hyphenationSource
parameter_list|)
throws|throws
name|IOException
block|{
name|HyphenationTree
name|tree
init|=
operator|new
name|HyphenationTree
argument_list|()
decl_stmt|;
name|tree
operator|.
name|loadPatterns
argument_list|(
name|hyphenationSource
argument_list|)
expr_stmt|;
return|return
name|tree
return|;
block|}
annotation|@
name|Override
DECL|method|decompose
specifier|protected
name|void
name|decompose
parameter_list|()
block|{
comment|// get the hyphenation points
name|Hyphenation
name|hyphens
init|=
name|hyphenator
operator|.
name|hyphenate
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// No hyphen points found -> exit
if|if
condition|(
name|hyphens
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|int
index|[]
name|hyp
init|=
name|hyphens
operator|.
name|getHyphenationPoints
argument_list|()
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
name|hyp
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
name|remaining
init|=
name|hyp
operator|.
name|length
operator|-
name|i
decl_stmt|;
name|int
name|start
init|=
name|hyp
index|[
name|i
index|]
decl_stmt|;
name|CompoundToken
name|longestMatchToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|remaining
condition|;
name|j
operator|++
control|)
block|{
name|int
name|partLength
init|=
name|hyp
index|[
name|i
operator|+
name|j
index|]
operator|-
name|start
decl_stmt|;
comment|// if the part is longer than maxSubwordSize we
comment|// are done with this round
if|if
condition|(
name|partLength
operator|>
name|this
operator|.
name|maxSubwordSize
condition|)
block|{
break|break;
block|}
comment|// we only put subwords to the token stream
comment|// that are longer than minPartSize
if|if
condition|(
name|partLength
operator|<
name|this
operator|.
name|minSubwordSize
condition|)
block|{
comment|// BOGUS/BROKEN/FUNKY/WACKO: somehow we have negative 'parts' according to the
comment|// calculation above, and we rely upon minSubwordSize being>=0 to filter them out...
continue|continue;
block|}
comment|// check the dictionary
if|if
condition|(
name|dictionary
operator|==
literal|null
operator|||
name|dictionary
operator|.
name|contains
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|start
argument_list|,
name|partLength
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|onlyLongestMatch
condition|)
block|{
if|if
condition|(
name|longestMatchToken
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|longestMatchToken
operator|.
name|txt
operator|.
name|length
argument_list|()
operator|<
name|partLength
condition|)
block|{
name|longestMatchToken
operator|=
operator|new
name|CompoundToken
argument_list|(
name|start
argument_list|,
name|partLength
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|longestMatchToken
operator|=
operator|new
name|CompoundToken
argument_list|(
name|start
argument_list|,
name|partLength
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|CompoundToken
argument_list|(
name|start
argument_list|,
name|partLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|dictionary
operator|.
name|contains
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|start
argument_list|,
name|partLength
operator|-
literal|1
argument_list|)
condition|)
block|{
comment|// check the dictionary again with a word that is one character
comment|// shorter
comment|// to avoid problems with genitive 's characters and other binding
comment|// characters
if|if
condition|(
name|this
operator|.
name|onlyLongestMatch
condition|)
block|{
if|if
condition|(
name|longestMatchToken
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|longestMatchToken
operator|.
name|txt
operator|.
name|length
argument_list|()
operator|<
name|partLength
operator|-
literal|1
condition|)
block|{
name|longestMatchToken
operator|=
operator|new
name|CompoundToken
argument_list|(
name|start
argument_list|,
name|partLength
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|longestMatchToken
operator|=
operator|new
name|CompoundToken
argument_list|(
name|start
argument_list|,
name|partLength
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|CompoundToken
argument_list|(
name|start
argument_list|,
name|partLength
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|this
operator|.
name|onlyLongestMatch
operator|&&
name|longestMatchToken
operator|!=
literal|null
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|longestMatchToken
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
