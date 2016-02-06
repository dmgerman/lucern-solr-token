begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
package|;
end_package
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|CharacterIterator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UCharacter
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedBreakIterator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|UTF16
import|;
end_import
begin_comment
comment|/**  * Contain all the issues surrounding BreakIterators in ICU in one place.  * Basically this boils down to the fact that they aren't very friendly to any  * sort of OO design.  *<p>  * http://bugs.icu-project.org/trac/ticket/5901: RBBI.getRuleStatus(), hoist to  * BreakIterator from RuleBasedBreakIterator  *<p>  * DictionaryBasedBreakIterator is a subclass of RuleBasedBreakIterator, but  * doesn't actually behave as a subclass: it always returns 0 for  * getRuleStatus():   * http://bugs.icu-project.org/trac/ticket/4730: Thai RBBI, no boundary type  * tags  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BreakIteratorWrapper
specifier|abstract
class|class
name|BreakIteratorWrapper
block|{
DECL|field|textIterator
specifier|protected
specifier|final
name|CharArrayIterator
name|textIterator
init|=
operator|new
name|CharArrayIterator
argument_list|()
decl_stmt|;
DECL|field|text
specifier|protected
name|char
name|text
index|[]
decl_stmt|;
DECL|field|start
specifier|protected
name|int
name|start
decl_stmt|;
DECL|field|length
specifier|protected
name|int
name|length
decl_stmt|;
DECL|method|next
specifier|abstract
name|int
name|next
parameter_list|()
function_decl|;
DECL|method|current
specifier|abstract
name|int
name|current
parameter_list|()
function_decl|;
DECL|method|getRuleStatus
specifier|abstract
name|int
name|getRuleStatus
parameter_list|()
function_decl|;
DECL|method|setText
specifier|abstract
name|void
name|setText
parameter_list|(
name|CharacterIterator
name|text
parameter_list|)
function_decl|;
DECL|method|setText
name|void
name|setText
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|textIterator
operator|.
name|setText
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|textIterator
argument_list|)
expr_stmt|;
block|}
comment|/**    * If it's a RuleBasedBreakIterator, the rule status can be used for token type. If it's    * any other BreakIterator, the rulestatus method is not available, so treat    * it like a generic BreakIterator.    */
DECL|method|wrap
specifier|static
name|BreakIteratorWrapper
name|wrap
parameter_list|(
name|BreakIterator
name|breakIterator
parameter_list|)
block|{
if|if
condition|(
name|breakIterator
operator|instanceof
name|RuleBasedBreakIterator
condition|)
return|return
operator|new
name|RBBIWrapper
argument_list|(
operator|(
name|RuleBasedBreakIterator
operator|)
name|breakIterator
argument_list|)
return|;
else|else
return|return
operator|new
name|BIWrapper
argument_list|(
name|breakIterator
argument_list|)
return|;
block|}
comment|/**    * RuleBasedBreakIterator wrapper: RuleBasedBreakIterator (as long as it's not    * a DictionaryBasedBreakIterator) behaves correctly.    */
DECL|class|RBBIWrapper
specifier|static
specifier|final
class|class
name|RBBIWrapper
extends|extends
name|BreakIteratorWrapper
block|{
DECL|field|rbbi
specifier|private
specifier|final
name|RuleBasedBreakIterator
name|rbbi
decl_stmt|;
DECL|method|RBBIWrapper
name|RBBIWrapper
parameter_list|(
name|RuleBasedBreakIterator
name|rbbi
parameter_list|)
block|{
name|this
operator|.
name|rbbi
operator|=
name|rbbi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|current
name|int
name|current
parameter_list|()
block|{
return|return
name|rbbi
operator|.
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRuleStatus
name|int
name|getRuleStatus
parameter_list|()
block|{
return|return
name|rbbi
operator|.
name|getRuleStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
name|int
name|next
parameter_list|()
block|{
return|return
name|rbbi
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setText
name|void
name|setText
parameter_list|(
name|CharacterIterator
name|text
parameter_list|)
block|{
name|rbbi
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Generic BreakIterator wrapper: Either the rulestatus method is not    * available or always returns 0. Calculate a rulestatus here so it behaves    * like RuleBasedBreakIterator.    *     * Note: This is slower than RuleBasedBreakIterator.    */
DECL|class|BIWrapper
specifier|static
specifier|final
class|class
name|BIWrapper
extends|extends
name|BreakIteratorWrapper
block|{
DECL|field|bi
specifier|private
specifier|final
name|BreakIterator
name|bi
decl_stmt|;
DECL|field|status
specifier|private
name|int
name|status
decl_stmt|;
DECL|method|BIWrapper
name|BIWrapper
parameter_list|(
name|BreakIterator
name|bi
parameter_list|)
block|{
name|this
operator|.
name|bi
operator|=
name|bi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|current
name|int
name|current
parameter_list|()
block|{
return|return
name|bi
operator|.
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRuleStatus
name|int
name|getRuleStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|next
name|int
name|next
parameter_list|()
block|{
name|int
name|current
init|=
name|bi
operator|.
name|current
argument_list|()
decl_stmt|;
name|int
name|next
init|=
name|bi
operator|.
name|next
argument_list|()
decl_stmt|;
name|status
operator|=
name|calcStatus
argument_list|(
name|current
argument_list|,
name|next
argument_list|)
expr_stmt|;
return|return
name|next
return|;
block|}
DECL|method|calcStatus
specifier|private
name|int
name|calcStatus
parameter_list|(
name|int
name|current
parameter_list|,
name|int
name|next
parameter_list|)
block|{
if|if
condition|(
name|current
operator|==
name|BreakIterator
operator|.
name|DONE
operator|||
name|next
operator|==
name|BreakIterator
operator|.
name|DONE
condition|)
return|return
name|RuleBasedBreakIterator
operator|.
name|WORD_NONE
return|;
name|int
name|begin
init|=
name|start
operator|+
name|current
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|next
decl_stmt|;
name|int
name|codepoint
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|begin
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|+=
name|UTF16
operator|.
name|getCharCount
argument_list|(
name|codepoint
argument_list|)
control|)
block|{
name|codepoint
operator|=
name|UTF16
operator|.
name|charAt
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|end
argument_list|,
name|begin
argument_list|)
expr_stmt|;
if|if
condition|(
name|UCharacter
operator|.
name|isDigit
argument_list|(
name|codepoint
argument_list|)
condition|)
return|return
name|RuleBasedBreakIterator
operator|.
name|WORD_NUMBER
return|;
elseif|else
if|if
condition|(
name|UCharacter
operator|.
name|isLetter
argument_list|(
name|codepoint
argument_list|)
condition|)
block|{
comment|// TODO: try to separately specify ideographic, kana?
comment|// [currently all bundled as letter for this case]
return|return
name|RuleBasedBreakIterator
operator|.
name|WORD_LETTER
return|;
block|}
block|}
return|return
name|RuleBasedBreakIterator
operator|.
name|WORD_NONE
return|;
block|}
annotation|@
name|Override
DECL|method|setText
name|void
name|setText
parameter_list|(
name|CharacterIterator
name|text
parameter_list|)
block|{
name|bi
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|status
operator|=
name|RuleBasedBreakIterator
operator|.
name|WORD_NONE
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
