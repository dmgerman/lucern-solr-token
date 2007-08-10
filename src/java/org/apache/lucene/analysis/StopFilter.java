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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
specifier|final
name|CharArraySet
name|stopWords
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
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
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|makeStopCharArraySet
argument_list|(
name|stopWords
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a token stream filtering the given input.    * @param input    * @param stopWords The set of Stop Words, as Strings.  If ignoreCase is true, all strings should be lower cased    * @param ignoreCase -Ignore case when stopping.  The stopWords set must be setup to contain only lower case words     */
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
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
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
name|Iterator
name|it
init|=
name|stopWords
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
name|this
operator|.
name|stopWords
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|it
operator|.
name|next
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
comment|/**    *      * @param stopWords    * @param ignoreCase If true, all words are lower cased first.      * @return a Set containing the words    */
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
name|ignoreCase
condition|?
name|stopWords
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
else|:
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
DECL|method|makeStopCharArraySet
specifier|private
specifier|static
specifier|final
name|CharArraySet
name|makeStopCharArraySet
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
name|stopSet
operator|.
name|add
argument_list|(
name|ignoreCase
condition|?
name|stopWords
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
else|:
name|stopWords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|stopSet
return|;
block|}
comment|/**    * Returns the next input Token whose termText() is not a stop word.    */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
name|Token
name|result
parameter_list|)
throws|throws
name|IOException
block|{
comment|// return the first non-stop word found
while|while
condition|(
operator|(
name|result
operator|=
name|input
operator|.
name|next
argument_list|(
name|result
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|stopWords
operator|.
name|contains
argument_list|(
name|result
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|result
operator|.
name|termLength
argument_list|)
condition|)
return|return
name|result
return|;
block|}
comment|// reached EOS -- return null
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
