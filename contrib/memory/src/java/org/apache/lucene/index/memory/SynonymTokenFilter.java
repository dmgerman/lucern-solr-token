begin_unit
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
begin_comment
comment|/**  * Injects additional tokens for synonyms of token terms fetched from the  * underlying child stream; the child stream must deliver lowercase tokens  * for synonyms to be found.  *   * @author whoschek.AT.lbl.DOT.gov  */
end_comment
begin_class
DECL|class|SynonymTokenFilter
specifier|public
class|class
name|SynonymTokenFilter
extends|extends
name|TokenFilter
block|{
comment|/** The Token.type used to indicate a synonym to higher level filters. */
DECL|field|SYNONYM_TOKEN_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|SYNONYM_TOKEN_TYPE
init|=
literal|"SYNONYM"
decl_stmt|;
DECL|field|synonyms
specifier|private
specifier|final
name|SynonymMap
name|synonyms
decl_stmt|;
DECL|field|maxSynonyms
specifier|private
specifier|final
name|int
name|maxSynonyms
decl_stmt|;
DECL|field|stack
specifier|private
name|String
index|[]
name|stack
init|=
literal|null
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
DECL|field|current
specifier|private
name|Token
name|current
init|=
literal|null
decl_stmt|;
DECL|field|todo
specifier|private
name|int
name|todo
init|=
literal|0
decl_stmt|;
comment|/**    * Creates an instance for the given underlying stream and synonym table.    *     * @param input    *            the underlying child token stream    * @param synonyms    *            the map used to extract synonyms for terms    * @param maxSynonyms    *            the maximum number of synonym tokens to return per underlying    *            token word (a value of Integer.MAX_VALUE indicates unlimited)    */
DECL|method|SynonymTokenFilter
specifier|public
name|SynonymTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|SynonymMap
name|synonyms
parameter_list|,
name|int
name|maxSynonyms
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|input
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"input must not be null"
argument_list|)
throw|;
if|if
condition|(
name|synonyms
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"synonyms must not be null"
argument_list|)
throw|;
if|if
condition|(
name|maxSynonyms
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxSynonyms must not be negative"
argument_list|)
throw|;
name|this
operator|.
name|synonyms
operator|=
name|synonyms
expr_stmt|;
name|this
operator|.
name|maxSynonyms
operator|=
name|maxSynonyms
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
DECL|method|next
specifier|public
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
while|while
condition|(
name|todo
operator|>
literal|0
operator|&&
name|index
operator|<
name|stack
operator|.
name|length
condition|)
block|{
comment|// pop from stack
name|Token
name|nextToken
init|=
name|createToken
argument_list|(
name|stack
index|[
name|index
operator|++
index|]
argument_list|,
name|current
argument_list|,
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|!=
literal|null
condition|)
block|{
name|todo
operator|--
expr_stmt|;
return|return
name|nextToken
return|;
block|}
block|}
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// EOS; iterator exhausted
name|stack
operator|=
name|synonyms
operator|.
name|getSynonyms
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
comment|// push onto stack
if|if
condition|(
name|stack
operator|.
name|length
operator|>
name|maxSynonyms
condition|)
name|randomize
argument_list|(
name|stack
argument_list|)
expr_stmt|;
name|index
operator|=
literal|0
expr_stmt|;
name|current
operator|=
operator|(
name|Token
operator|)
name|nextToken
operator|.
name|clone
argument_list|()
expr_stmt|;
name|todo
operator|=
name|maxSynonyms
expr_stmt|;
return|return
name|nextToken
return|;
block|}
comment|/**    * Creates and returns a token for the given synonym of the current input    * token; Override for custom (stateless or stateful) behavior, if desired.    *     * @param synonym     *            a synonym for the current token's term    * @param current    *            the current token from the underlying child stream    * @param reusableToken    *            the token to reuse    * @return a new token, or null to indicate that the given synonym should be    *         ignored    */
DECL|method|createToken
specifier|protected
name|Token
name|createToken
parameter_list|(
name|String
name|synonym
parameter_list|,
name|Token
name|current
parameter_list|,
specifier|final
name|Token
name|reusableToken
parameter_list|)
block|{
name|reusableToken
operator|.
name|reinit
argument_list|(
name|current
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setTermBuffer
argument_list|(
name|synonym
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setType
argument_list|(
name|SYNONYM_TOKEN_TYPE
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|reusableToken
return|;
block|}
comment|/**    * Randomize synonyms to later sample a subset. Uses constant random seed    * for reproducibility. Uses "DRand", a simple, fast, uniform pseudo-random    * number generator with medium statistical quality (multiplicative    * congruential method), producing integers in the range [Integer.MIN_VALUE,    * Integer.MAX_VALUE].    */
DECL|method|randomize
specifier|private
specifier|static
name|void
name|randomize
parameter_list|(
name|Object
index|[]
name|arr
parameter_list|)
block|{
name|int
name|seed
init|=
literal|1234567
decl_stmt|;
comment|// constant
name|int
name|randomState
init|=
literal|4
operator|*
name|seed
operator|+
literal|1
decl_stmt|;
comment|//    Random random = new Random(seed); // unnecessary overhead
name|int
name|len
init|=
name|arr
operator|.
name|length
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
name|len
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|randomState
operator|*=
literal|0x278DDE6D
expr_stmt|;
comment|// z(i+1)=a*z(i) (mod 2**32)
name|int
name|r
init|=
name|randomState
operator|%
operator|(
name|len
operator|-
name|i
operator|)
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|0
condition|)
name|r
operator|=
operator|-
name|r
expr_stmt|;
comment|// e.g. -9 % 2 == -1
comment|//      int r = random.nextInt(len-i);
comment|// swap arr[i, i+r]
name|Object
name|tmp
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|arr
index|[
name|i
operator|+
name|r
index|]
expr_stmt|;
name|arr
index|[
name|i
operator|+
name|r
index|]
operator|=
name|tmp
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
