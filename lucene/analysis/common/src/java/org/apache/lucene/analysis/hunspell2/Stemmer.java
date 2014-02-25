begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell2
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell2
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Stemmer uses the affix rules declared in the Dictionary to generate one or more stems for a word.  It  * conforms to the algorithm in the original hunspell algorithm, including recursive suffix stripping.  */
end_comment
begin_class
DECL|class|Stemmer
specifier|final
class|class
name|Stemmer
block|{
DECL|field|recursionCap
specifier|private
specifier|final
name|int
name|recursionCap
decl_stmt|;
DECL|field|dictionary
specifier|private
specifier|final
name|Dictionary
name|dictionary
decl_stmt|;
DECL|field|scratch
specifier|private
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|StringBuilder
name|segment
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|/**    * Constructs a new Stemmer which will use the provided Dictionary to create its stems. Uses the     * default recursion cap of<code>2</code> (based on Hunspell documentation).     *    * @param dictionary Dictionary that will be used to create the stems    */
DECL|method|Stemmer
specifier|public
name|Stemmer
parameter_list|(
name|Dictionary
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|dictionary
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new Stemmer which will use the provided Dictionary to create its stems.    *    * @param dictionary Dictionary that will be used to create the stems    * @param recursionCap maximum level of recursion stemmer can go into    */
DECL|method|Stemmer
specifier|public
name|Stemmer
parameter_list|(
name|Dictionary
name|dictionary
parameter_list|,
name|int
name|recursionCap
parameter_list|)
block|{
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
name|this
operator|.
name|recursionCap
operator|=
name|recursionCap
expr_stmt|;
block|}
comment|/**    * Find the stem(s) of the provided word.    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|stem
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|stem
parameter_list|(
name|String
name|word
parameter_list|)
block|{
return|return
name|stem
argument_list|(
name|word
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|word
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Find the stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|stem
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|stem
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|scratch
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
operator|new
name|Stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stems
return|;
block|}
comment|/**    * Find the unique stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|uniqueStems
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|uniqueStems
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
name|CharArraySet
name|terms
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|scratch
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
operator|new
name|Stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Stem
argument_list|>
name|otherStems
init|=
name|stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Stem
name|s
range|:
name|otherStems
control|)
block|{
if|if
condition|(
operator|!
name|terms
operator|.
name|contains
argument_list|(
name|s
operator|.
name|stem
argument_list|)
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|s
operator|.
name|stem
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|stems
return|;
block|}
comment|// ================================================= Helper Methods ================================================
comment|/**    * Generates a list of stems for the provided word    *    * @param word Word to generate the stems for    * @param flags Flags from a previous stemming step that need to be cross-checked with any affixes in this recursive step    * @param recursionDepth Level of recursion this stemming step is at    * @return List of stems, or empty list if no stems are found    */
DECL|method|stem
specifier|private
name|List
argument_list|<
name|Stem
argument_list|>
name|stem
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|char
index|[]
name|flags
parameter_list|,
name|int
name|recursionDepth
parameter_list|)
block|{
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Affix
argument_list|>
name|suffixes
init|=
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
name|word
argument_list|,
name|i
argument_list|,
name|length
operator|-
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffixes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|Affix
name|suffix
range|:
name|suffixes
control|)
block|{
if|if
condition|(
name|hasCrossCheckedFlag
argument_list|(
name|suffix
operator|.
name|getFlag
argument_list|()
argument_list|,
name|flags
argument_list|)
condition|)
block|{
name|int
name|appendLength
init|=
name|length
operator|-
name|i
decl_stmt|;
name|int
name|deAffixedLength
init|=
name|length
operator|-
name|appendLength
decl_stmt|;
comment|// TODO: can we do this in-place?
name|String
name|strippedWord
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|deAffixedLength
argument_list|)
operator|.
name|append
argument_list|(
name|suffix
operator|.
name|getStrip
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Stem
argument_list|>
name|stemList
init|=
name|applyAffix
argument_list|(
name|strippedWord
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|()
argument_list|,
name|suffix
argument_list|,
name|recursionDepth
argument_list|)
decl_stmt|;
for|for
control|(
name|Stem
name|stem
range|:
name|stemList
control|)
block|{
name|stem
operator|.
name|addSuffix
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stemList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|List
argument_list|<
name|Affix
argument_list|>
name|prefixes
init|=
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|Affix
name|prefix
range|:
name|prefixes
control|)
block|{
if|if
condition|(
name|hasCrossCheckedFlag
argument_list|(
name|prefix
operator|.
name|getFlag
argument_list|()
argument_list|,
name|flags
argument_list|)
condition|)
block|{
name|int
name|deAffixedStart
init|=
name|i
decl_stmt|;
name|int
name|deAffixedLength
init|=
name|length
operator|-
name|deAffixedStart
decl_stmt|;
name|String
name|strippedWord
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|getStrip
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|word
argument_list|,
name|deAffixedStart
argument_list|,
name|deAffixedLength
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Stem
argument_list|>
name|stemList
init|=
name|applyAffix
argument_list|(
name|strippedWord
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|()
argument_list|,
name|prefix
argument_list|,
name|recursionDepth
argument_list|)
decl_stmt|;
for|for
control|(
name|Stem
name|stem
range|:
name|stemList
control|)
block|{
name|stem
operator|.
name|addPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stemList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|stems
return|;
block|}
comment|/**    * Applies the affix rule to the given word, producing a list of stems if any are found    *    * @param strippedWord Word the affix has been removed and the strip added    * @param affix HunspellAffix representing the affix rule itself    * @param recursionDepth Level of recursion this stemming step is at    * @return List of stems for the word, or an empty list if none are found    */
DECL|method|applyAffix
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|applyAffix
parameter_list|(
name|char
name|strippedWord
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|Affix
name|affix
parameter_list|,
name|int
name|recursionDepth
parameter_list|)
block|{
name|segment
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|segment
operator|.
name|append
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|affix
operator|.
name|checkCondition
argument_list|(
name|segment
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
name|char
name|wordFlags
index|[]
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordFlags
operator|!=
literal|null
operator|&&
name|Dictionary
operator|.
name|hasFlag
argument_list|(
name|wordFlags
argument_list|,
name|affix
operator|.
name|getFlag
argument_list|()
argument_list|)
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
operator|new
name|Stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|affix
operator|.
name|isCrossProduct
argument_list|()
operator|&&
name|recursionDepth
operator|<
name|recursionCap
condition|)
block|{
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|,
name|affix
operator|.
name|getAppendFlags
argument_list|()
argument_list|,
operator|++
name|recursionDepth
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stems
return|;
block|}
comment|/**    * Checks if the given flag cross checks with the given array of flags    *    * @param flag Flag to cross check with the array of flags    * @param flags Array of flags to cross check against.  Can be {@code null}    * @return {@code true} if the flag is found in the array or the array is {@code null}, {@code false} otherwise    */
DECL|method|hasCrossCheckedFlag
specifier|private
name|boolean
name|hasCrossCheckedFlag
parameter_list|(
name|char
name|flag
parameter_list|,
name|char
index|[]
name|flags
parameter_list|)
block|{
return|return
name|flags
operator|==
literal|null
operator|||
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|flags
argument_list|,
name|flag
argument_list|)
operator|>=
literal|0
return|;
block|}
block|}
end_class
end_unit
