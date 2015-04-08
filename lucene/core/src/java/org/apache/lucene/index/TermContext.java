begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|BytesRef
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Maintains a {@link IndexReader} {@link TermState} view over  * {@link IndexReader} instances containing a single term. The  * {@link TermContext} doesn't track if the given {@link TermState}  * objects are valid, neither if the {@link TermState} instances refer to the  * same terms in the associated readers.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermContext
specifier|public
specifier|final
class|class
name|TermContext
block|{
comment|/** Holds the {@link IndexReaderContext} of the top-level    *  {@link IndexReader}, used internally only for    *  asserting.    *    *  @lucene.internal */
DECL|field|topReaderContext
specifier|public
specifier|final
name|IndexReaderContext
name|topReaderContext
decl_stmt|;
DECL|field|states
specifier|private
specifier|final
name|TermState
index|[]
name|states
decl_stmt|;
DECL|field|docFreq
specifier|private
name|int
name|docFreq
decl_stmt|;
DECL|field|totalTermFreq
specifier|private
name|long
name|totalTermFreq
decl_stmt|;
comment|//public static boolean DEBUG = BlockTreeTermsWriter.DEBUG;
comment|/**    * Creates an empty {@link TermContext} from a {@link IndexReaderContext}    */
DECL|method|TermContext
specifier|public
name|TermContext
parameter_list|(
name|IndexReaderContext
name|context
parameter_list|)
block|{
assert|assert
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|isTopLevel
assert|;
name|topReaderContext
operator|=
name|context
expr_stmt|;
name|docFreq
operator|=
literal|0
expr_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|leaves
argument_list|()
operator|==
literal|null
condition|)
block|{
name|len
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|len
operator|=
name|context
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|states
operator|=
operator|new
name|TermState
index|[
name|len
index|]
expr_stmt|;
block|}
comment|/**    * Creates a {@link TermContext} with an initial {@link TermState},    * {@link IndexReader} pair.    */
DECL|method|TermContext
specifier|public
name|TermContext
parameter_list|(
name|IndexReaderContext
name|context
parameter_list|,
name|TermState
name|state
parameter_list|,
name|int
name|ord
parameter_list|,
name|int
name|docFreq
parameter_list|,
name|long
name|totalTermFreq
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|state
argument_list|,
name|ord
argument_list|,
name|docFreq
argument_list|,
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link TermContext} from a top-level {@link IndexReaderContext} and the    * given {@link Term}. This method will lookup the given term in all context's leaf readers     * and register each of the readers containing the term in the returned {@link TermContext}    * using the leaf reader's ordinal.    *<p>    * Note: the given context must be a top-level context.    */
DECL|method|build
specifier|public
specifier|static
name|TermContext
name|build
parameter_list|(
name|IndexReaderContext
name|context
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|isTopLevel
assert|;
specifier|final
name|String
name|field
init|=
name|term
operator|.
name|field
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|term
operator|.
name|bytes
argument_list|()
decl_stmt|;
specifier|final
name|TermContext
name|perReaderTermState
init|=
operator|new
name|TermContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|//if (DEBUG) System.out.println("prts.build term=" + term);
for|for
control|(
specifier|final
name|LeafReaderContext
name|ctx
range|:
name|context
operator|.
name|leaves
argument_list|()
control|)
block|{
comment|//if (DEBUG) System.out.println("  r=" + leaves[i].reader);
specifier|final
name|Terms
name|terms
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|bytes
argument_list|)
condition|)
block|{
specifier|final
name|TermState
name|termState
init|=
name|termsEnum
operator|.
name|termState
argument_list|()
decl_stmt|;
comment|//if (DEBUG) System.out.println("    found");
name|perReaderTermState
operator|.
name|register
argument_list|(
name|termState
argument_list|,
name|ctx
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|perReaderTermState
return|;
block|}
comment|/**    * Clears the {@link TermContext} internal state and removes all    * registered {@link TermState}s    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|docFreq
operator|=
literal|0
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|states
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Registers and associates a {@link TermState} with an leaf ordinal. The leaf ordinal    * should be derived from a {@link IndexReaderContext}'s leaf ord.    */
DECL|method|register
specifier|public
name|void
name|register
parameter_list|(
name|TermState
name|state
parameter_list|,
specifier|final
name|int
name|ord
parameter_list|,
specifier|final
name|int
name|docFreq
parameter_list|,
specifier|final
name|long
name|totalTermFreq
parameter_list|)
block|{
assert|assert
name|state
operator|!=
literal|null
operator|:
literal|"state must not be null"
assert|;
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|states
operator|.
name|length
assert|;
assert|assert
name|states
index|[
name|ord
index|]
operator|==
literal|null
operator|:
literal|"state for ord: "
operator|+
name|ord
operator|+
literal|" already registered"
assert|;
name|this
operator|.
name|docFreq
operator|+=
name|docFreq
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|totalTermFreq
operator|>=
literal|0
operator|&&
name|totalTermFreq
operator|>=
literal|0
condition|)
name|this
operator|.
name|totalTermFreq
operator|+=
name|totalTermFreq
expr_stmt|;
else|else
name|this
operator|.
name|totalTermFreq
operator|=
operator|-
literal|1
expr_stmt|;
name|states
index|[
name|ord
index|]
operator|=
name|state
expr_stmt|;
block|}
comment|/**    * Returns the {@link TermState} for an leaf ordinal or<code>null</code> if no    * {@link TermState} for the ordinal was registered.    *     * @param ord    *          the readers leaf ordinal to get the {@link TermState} for.    * @return the {@link TermState} for the given readers ord or<code>null</code> if no    *         {@link TermState} for the reader was registered    */
DECL|method|get
specifier|public
name|TermState
name|get
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|states
operator|.
name|length
assert|;
return|return
name|states
index|[
name|ord
index|]
return|;
block|}
comment|/**    *  Returns the accumulated document frequency of all {@link TermState}    *         instances passed to {@link #register(TermState, int, int, long)}.    * @return the accumulated document frequency of all {@link TermState}    *         instances passed to {@link #register(TermState, int, int, long)}.    */
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|docFreq
return|;
block|}
comment|/**    *  Returns the accumulated term frequency of all {@link TermState}    *         instances passed to {@link #register(TermState, int, int, long)}.    * @return the accumulated term frequency of all {@link TermState}    *         instances passed to {@link #register(TermState, int, int, long)}.    */
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
return|return
name|totalTermFreq
return|;
block|}
comment|/** expert: only available for queries that want to lie about docfreq    * @lucene.internal */
DECL|method|setDocFreq
specifier|public
name|void
name|setDocFreq
parameter_list|(
name|int
name|docFreq
parameter_list|)
block|{
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
block|}
comment|/** Returns true if all terms stored here are real (e.g., not auto-prefix terms).    *    *  @lucene.internal */
DECL|method|hasOnlyRealTerms
specifier|public
name|boolean
name|hasOnlyRealTerms
parameter_list|()
block|{
for|for
control|(
name|TermState
name|termState
range|:
name|states
control|)
block|{
if|if
condition|(
name|termState
operator|instanceof
name|BlockTermState
operator|&&
operator|(
operator|(
name|BlockTermState
operator|)
name|termState
operator|)
operator|.
name|isRealTerm
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"TermContext\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|TermState
name|termState
range|:
name|states
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  state="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|termState
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
