begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|List
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
name|cn
operator|.
name|smart
operator|.
name|Utility
import|;
end_import
begin_comment
comment|/**  * Graph representing possible token pairs (bigrams) at each start offset in the sentence.  *<p>  * For each start offset, a list of possible token pairs is stored.  *</p>  *<p><font color="#FF0000">  * WARNING: The status of the analyzers/smartcn<b>analysis.cn.smart</b> package is experimental.   * The APIs and file formats introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *</p>  */
end_comment
begin_class
DECL|class|BiSegGraph
class|class
name|BiSegGraph
block|{
DECL|field|tokenPairListTable
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|SegTokenPair
argument_list|>
argument_list|>
name|tokenPairListTable
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|SegTokenPair
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|segTokenList
specifier|private
name|List
argument_list|<
name|SegToken
argument_list|>
name|segTokenList
decl_stmt|;
DECL|field|bigramDict
specifier|private
specifier|static
name|BigramDictionary
name|bigramDict
init|=
name|BigramDictionary
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|method|BiSegGraph
specifier|public
name|BiSegGraph
parameter_list|(
name|SegGraph
name|segGraph
parameter_list|)
block|{
name|segTokenList
operator|=
name|segGraph
operator|.
name|makeIndex
argument_list|()
expr_stmt|;
name|generateBiSegGraph
argument_list|(
name|segGraph
argument_list|)
expr_stmt|;
block|}
comment|/*    * Generate a BiSegGraph based upon a SegGraph    */
DECL|method|generateBiSegGraph
specifier|private
name|void
name|generateBiSegGraph
parameter_list|(
name|SegGraph
name|segGraph
parameter_list|)
block|{
name|double
name|smooth
init|=
literal|0.1
decl_stmt|;
name|int
name|wordPairFreq
init|=
literal|0
decl_stmt|;
name|int
name|maxStart
init|=
name|segGraph
operator|.
name|getMaxStart
argument_list|()
decl_stmt|;
name|double
name|oneWordFreq
decl_stmt|,
name|weight
decl_stmt|,
name|tinyDouble
init|=
literal|1.0
operator|/
name|Utility
operator|.
name|MAX_FREQUENCE
decl_stmt|;
name|int
name|next
decl_stmt|;
name|char
index|[]
name|idBuffer
decl_stmt|;
comment|// get the list of tokens ordered and indexed
name|segTokenList
operator|=
name|segGraph
operator|.
name|makeIndex
argument_list|()
expr_stmt|;
comment|// Because the beginning position of startToken is -1, therefore startToken can be obtained when key = -1
name|int
name|key
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|SegToken
argument_list|>
name|nextTokens
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|key
operator|<
name|maxStart
condition|)
block|{
if|if
condition|(
name|segGraph
operator|.
name|isStartExist
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|SegToken
argument_list|>
name|tokenList
init|=
name|segGraph
operator|.
name|getStartList
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Calculate all tokens for a given key.
for|for
control|(
name|SegToken
name|t1
range|:
name|tokenList
control|)
block|{
name|oneWordFreq
operator|=
name|t1
operator|.
name|weight
expr_stmt|;
name|next
operator|=
name|t1
operator|.
name|endOffset
expr_stmt|;
name|nextTokens
operator|=
literal|null
expr_stmt|;
comment|// Find the next corresponding Token.
comment|// For example: "Sunny seashore", the present Token is "sunny", next one should be "sea" or "seashore".
comment|// If we cannot find the next Token, then go to the end and repeat the same cycle.
while|while
condition|(
name|next
operator|<=
name|maxStart
condition|)
block|{
comment|// Because the beginning position of endToken is sentenceLen, so equal to sentenceLen can find endToken.
if|if
condition|(
name|segGraph
operator|.
name|isStartExist
argument_list|(
name|next
argument_list|)
condition|)
block|{
name|nextTokens
operator|=
name|segGraph
operator|.
name|getStartList
argument_list|(
name|next
argument_list|)
expr_stmt|;
break|break;
block|}
name|next
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|nextTokens
operator|==
literal|null
condition|)
block|{
break|break;
block|}
for|for
control|(
name|SegToken
name|t2
range|:
name|nextTokens
control|)
block|{
name|idBuffer
operator|=
operator|new
name|char
index|[
name|t1
operator|.
name|charArray
operator|.
name|length
operator|+
name|t2
operator|.
name|charArray
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|t1
operator|.
name|charArray
argument_list|,
literal|0
argument_list|,
name|idBuffer
argument_list|,
literal|0
argument_list|,
name|t1
operator|.
name|charArray
operator|.
name|length
argument_list|)
expr_stmt|;
name|idBuffer
index|[
name|t1
operator|.
name|charArray
operator|.
name|length
index|]
operator|=
name|BigramDictionary
operator|.
name|WORD_SEGMENT_CHAR
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|t2
operator|.
name|charArray
argument_list|,
literal|0
argument_list|,
name|idBuffer
argument_list|,
name|t1
operator|.
name|charArray
operator|.
name|length
operator|+
literal|1
argument_list|,
name|t2
operator|.
name|charArray
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Two linked Words frequency
name|wordPairFreq
operator|=
name|bigramDict
operator|.
name|getFrequency
argument_list|(
name|idBuffer
argument_list|)
expr_stmt|;
comment|// Smoothing
comment|// -log{a*P(Ci-1)+(1-a)P(Ci|Ci-1)} Note 0<a<1
name|weight
operator|=
operator|-
name|Math
operator|.
name|log
argument_list|(
name|smooth
operator|*
operator|(
literal|1.0
operator|+
name|oneWordFreq
operator|)
operator|/
operator|(
name|Utility
operator|.
name|MAX_FREQUENCE
operator|+
literal|0.0
operator|)
operator|+
operator|(
literal|1.0
operator|-
name|smooth
operator|)
operator|*
operator|(
operator|(
literal|1.0
operator|-
name|tinyDouble
operator|)
operator|*
name|wordPairFreq
operator|/
operator|(
literal|1.0
operator|+
name|oneWordFreq
operator|)
operator|+
name|tinyDouble
operator|)
argument_list|)
expr_stmt|;
name|SegTokenPair
name|tokenPair
init|=
operator|new
name|SegTokenPair
argument_list|(
name|idBuffer
argument_list|,
name|t1
operator|.
name|index
argument_list|,
name|t2
operator|.
name|index
argument_list|,
name|weight
argument_list|)
decl_stmt|;
name|this
operator|.
name|addSegTokenPair
argument_list|(
name|tokenPair
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|key
operator|++
expr_stmt|;
block|}
block|}
comment|/**    * Returns true if their is a list of token pairs at this offset (index of the second token)    *     * @param to index of the second token in the token pair    * @return true if a token pair exists    */
DECL|method|isToExist
specifier|public
name|boolean
name|isToExist
parameter_list|(
name|int
name|to
parameter_list|)
block|{
return|return
name|tokenPairListTable
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|to
argument_list|)
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Return a {@link List} of all token pairs at this offset (index of the second token)    *     * @param to index of the second token in the token pair    * @return {@link List} of token pairs.    */
DECL|method|getToList
specifier|public
name|List
argument_list|<
name|SegTokenPair
argument_list|>
name|getToList
parameter_list|(
name|int
name|to
parameter_list|)
block|{
return|return
name|tokenPairListTable
operator|.
name|get
argument_list|(
name|to
argument_list|)
return|;
block|}
comment|/**    * Add a {@link SegTokenPair}    *     * @param tokenPair {@link SegTokenPair}    */
DECL|method|addSegTokenPair
specifier|public
name|void
name|addSegTokenPair
parameter_list|(
name|SegTokenPair
name|tokenPair
parameter_list|)
block|{
name|int
name|to
init|=
name|tokenPair
operator|.
name|to
decl_stmt|;
if|if
condition|(
operator|!
name|isToExist
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|ArrayList
argument_list|<
name|SegTokenPair
argument_list|>
name|newlist
init|=
operator|new
name|ArrayList
argument_list|<
name|SegTokenPair
argument_list|>
argument_list|()
decl_stmt|;
name|newlist
operator|.
name|add
argument_list|(
name|tokenPair
argument_list|)
expr_stmt|;
name|tokenPairListTable
operator|.
name|put
argument_list|(
name|to
argument_list|,
name|newlist
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|SegTokenPair
argument_list|>
name|tokenPairList
init|=
name|tokenPairListTable
operator|.
name|get
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|tokenPairList
operator|.
name|add
argument_list|(
name|tokenPair
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the number of {@link SegTokenPair} entries in the table.    * @return number of {@link SegTokenPair} entries    */
DECL|method|getToCount
specifier|public
name|int
name|getToCount
parameter_list|()
block|{
return|return
name|tokenPairListTable
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Find the shortest path with the Viterbi algorithm.    * @return {@link List}    */
DECL|method|getShortPath
specifier|public
name|List
argument_list|<
name|SegToken
argument_list|>
name|getShortPath
parameter_list|()
block|{
name|int
name|current
decl_stmt|;
name|int
name|nodeCount
init|=
name|getToCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PathNode
argument_list|>
name|path
init|=
operator|new
name|ArrayList
argument_list|<
name|PathNode
argument_list|>
argument_list|()
decl_stmt|;
name|PathNode
name|zeroPath
init|=
operator|new
name|PathNode
argument_list|()
decl_stmt|;
name|zeroPath
operator|.
name|weight
operator|=
literal|0
expr_stmt|;
name|zeroPath
operator|.
name|preNode
operator|=
literal|0
expr_stmt|;
name|path
operator|.
name|add
argument_list|(
name|zeroPath
argument_list|)
expr_stmt|;
for|for
control|(
name|current
operator|=
literal|1
init|;
name|current
operator|<=
name|nodeCount
condition|;
name|current
operator|++
control|)
block|{
name|double
name|weight
decl_stmt|;
name|List
argument_list|<
name|SegTokenPair
argument_list|>
name|edges
init|=
name|getToList
argument_list|(
name|current
argument_list|)
decl_stmt|;
name|double
name|minWeight
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
name|SegTokenPair
name|minEdge
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SegTokenPair
name|edge
range|:
name|edges
control|)
block|{
name|weight
operator|=
name|edge
operator|.
name|weight
expr_stmt|;
name|PathNode
name|preNode
init|=
name|path
operator|.
name|get
argument_list|(
name|edge
operator|.
name|from
argument_list|)
decl_stmt|;
if|if
condition|(
name|preNode
operator|.
name|weight
operator|+
name|weight
operator|<
name|minWeight
condition|)
block|{
name|minWeight
operator|=
name|preNode
operator|.
name|weight
operator|+
name|weight
expr_stmt|;
name|minEdge
operator|=
name|edge
expr_stmt|;
block|}
block|}
name|PathNode
name|newNode
init|=
operator|new
name|PathNode
argument_list|()
decl_stmt|;
name|newNode
operator|.
name|weight
operator|=
name|minWeight
expr_stmt|;
name|newNode
operator|.
name|preNode
operator|=
name|minEdge
operator|.
name|from
expr_stmt|;
name|path
operator|.
name|add
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
block|}
comment|// Calculate PathNodes
name|int
name|preNode
decl_stmt|,
name|lastNode
decl_stmt|;
name|lastNode
operator|=
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
name|current
operator|=
name|lastNode
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|rpath
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SegToken
argument_list|>
name|resultPath
init|=
operator|new
name|ArrayList
argument_list|<
name|SegToken
argument_list|>
argument_list|()
decl_stmt|;
name|rpath
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
while|while
condition|(
name|current
operator|!=
literal|0
condition|)
block|{
name|PathNode
name|currentPathNode
init|=
operator|(
name|PathNode
operator|)
name|path
operator|.
name|get
argument_list|(
name|current
argument_list|)
decl_stmt|;
name|preNode
operator|=
name|currentPathNode
operator|.
name|preNode
expr_stmt|;
name|rpath
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|preNode
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|=
name|preNode
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
name|rpath
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|--
control|)
block|{
name|Integer
name|idInteger
init|=
operator|(
name|Integer
operator|)
name|rpath
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|int
name|id
init|=
name|idInteger
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|SegToken
name|t
init|=
name|segTokenList
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|resultPath
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|resultPath
return|;
block|}
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
name|Collection
argument_list|<
name|ArrayList
argument_list|<
name|SegTokenPair
argument_list|>
argument_list|>
name|values
init|=
name|tokenPairListTable
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|ArrayList
argument_list|<
name|SegTokenPair
argument_list|>
name|segList
range|:
name|values
control|)
block|{
for|for
control|(
name|SegTokenPair
name|pair
range|:
name|segList
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|pair
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
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
