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
name|util
operator|.
name|Arrays
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
DECL|field|ENABLE_POSITION_INCREMENTS_DEFAULT
specifier|private
specifier|static
name|boolean
name|ENABLE_POSITION_INCREMENTS_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|stopWords
specifier|private
specifier|final
name|CharArraySet
name|stopWords
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
init|=
name|ENABLE_POSITION_INCREMENTS_DEFAULT
decl_stmt|;
comment|/**    * Construct a token stream filtering the given input.    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|boolean
name|ignoreCase
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
operator|(
name|CharArraySet
operator|)
name|makeStopSet
argument_list|(
name|stopWords
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a token stream filtering the given input.    * If<code>stopWords</code> is an instance of {@link CharArraySet} (true if    *<code>makeStopSet()</code> was used to construct the set) it will be directly used    * and<code>ignoreCase</code> will be ignored since<code>CharArraySet</code>    * directly controls case sensitivity.    *<p/>    * If<code>stopWords</code> is not an instance of {@link CharArraySet},    * a new CharArraySet will be constructed and<code>ignoreCase</code> will be    * used to specify the case sensitivity of that set.    *    * @param input    * @param stopWords The set of Stop Words.    * @param ignoreCase -Ignore case when stopping.    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
name|stopWords
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopWords
operator|instanceof
name|CharArraySet
condition|)
block|{
name|this
operator|.
name|stopWords
operator|=
operator|(
name|CharArraySet
operator|)
name|stopWords
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|stopWords
operator|=
operator|new
name|CharArraySet
argument_list|(
name|stopWords
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|this
operator|.
name|stopWords
operator|.
name|addAll
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Constructs a filter which removes words from the input    * TokenStream that are named in the Set.    *    * @see #makeStopSet(java.lang.String[])    */
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
name|this
argument_list|(
name|in
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds a Set from an array of stop words,    * appropriate for passing into the StopFilter constructor.    * This permits this stopWords construction to be cached once when    * an Analyzer is constructed.    *     * @see #makeStopSet(java.lang.String[], boolean) passing false to ignoreCase    */
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
return|return
name|makeStopSet
argument_list|(
name|stopWords
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    *     * @param stopWords    * @param ignoreCase If true, all words are lower cased first.      * @return a Set containing the words    */
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
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|CharArraySet
name|stopSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|stopWords
operator|.
name|length
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|stopSet
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|stopWords
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stopSet
return|;
block|}
comment|/**    * Returns the next input Token whose term() is not a stop word.    */
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
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
comment|// return the first non-stop word found
name|int
name|skippedPositions
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|stopWords
operator|.
name|contains
argument_list|(
name|nextToken
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|nextToken
operator|.
name|termLength
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|nextToken
operator|.
name|setPositionIncrement
argument_list|(
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
operator|+
name|skippedPositions
argument_list|)
expr_stmt|;
block|}
return|return
name|nextToken
return|;
block|}
name|skippedPositions
operator|+=
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
comment|// reached EOS -- return null
return|return
literal|null
return|;
block|}
comment|/**    * @see #setEnablePositionIncrementsDefault(boolean).     */
DECL|method|getEnablePositionIncrementsDefault
specifier|public
specifier|static
name|boolean
name|getEnablePositionIncrementsDefault
parameter_list|()
block|{
return|return
name|ENABLE_POSITION_INCREMENTS_DEFAULT
return|;
block|}
comment|/**    * Set the default position increments behavior of every StopFilter created from now on.    *<p>    * Note: behavior of a single StopFilter instance can be modified     * with {@link #setEnablePositionIncrements(boolean)}.    * This static method allows control over behavior of classes using StopFilters internally,     * for example {@link org.apache.lucene.analysis.standard.StandardAnalyzer StandardAnalyzer}.     *<p>    * Default : false.    * @see #setEnablePositionIncrements(boolean).    */
DECL|method|setEnablePositionIncrementsDefault
specifier|public
specifier|static
name|void
name|setEnablePositionIncrementsDefault
parameter_list|(
name|boolean
name|defaultValue
parameter_list|)
block|{
name|ENABLE_POSITION_INCREMENTS_DEFAULT
operator|=
name|defaultValue
expr_stmt|;
block|}
comment|/**    * @see #setEnablePositionIncrements(boolean).     */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
comment|/**    * Set to<code>true</code> to make<b>this</b> StopFilter enable position increments to result tokens.    *<p>    * When set, when a token is stopped (omitted), the position increment of     * the following token is incremented.      *<p>    * Default: see {@link #setEnablePositionIncrementsDefault(boolean)}.    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enable
expr_stmt|;
block|}
block|}
end_class
end_unit
