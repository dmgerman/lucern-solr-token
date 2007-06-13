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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * The TermVectorOffsetInfo class holds information pertaining to a Term in a {@link org.apache.lucene.index.TermPositionVector}'s  * offset information.  This offset information is the character offset as set during the Analysis phase (and thus may not be the actual offset in the  * original content).  */
end_comment
begin_class
DECL|class|TermVectorOffsetInfo
specifier|public
class|class
name|TermVectorOffsetInfo
block|{
comment|/**    * Convenience declaration when creating a {@link org.apache.lucene.index.TermPositionVector} that stores only position information.    */
DECL|field|EMPTY_OFFSET_INFO
specifier|public
specifier|static
specifier|final
name|TermVectorOffsetInfo
index|[]
name|EMPTY_OFFSET_INFO
init|=
operator|new
name|TermVectorOffsetInfo
index|[
literal|0
index|]
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|int
name|endOffset
decl_stmt|;
DECL|method|TermVectorOffsetInfo
specifier|public
name|TermVectorOffsetInfo
parameter_list|()
block|{   }
DECL|method|TermVectorOffsetInfo
specifier|public
name|TermVectorOffsetInfo
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
block|}
comment|/**    * The accessor for the ending offset for the term    * @return The offset    */
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
DECL|method|setEndOffset
specifier|public
name|void
name|setEndOffset
parameter_list|(
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
comment|/**    * The accessor for the starting offset of the term.    *    * @return The offset    */
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|setStartOffset
specifier|public
name|void
name|setStartOffset
parameter_list|(
name|int
name|startOffset
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
block|}
comment|/**    * Two TermVectorOffsetInfos are equals if both the start and end offsets are the same    * @param o The comparison Object    * @return true if both {@link #getStartOffset()} and {@link #getEndOffset()} are the same for both objects.    */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|TermVectorOffsetInfo
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|TermVectorOffsetInfo
name|termVectorOffsetInfo
init|=
operator|(
name|TermVectorOffsetInfo
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|endOffset
operator|!=
name|termVectorOffsetInfo
operator|.
name|endOffset
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|startOffset
operator|!=
name|termVectorOffsetInfo
operator|.
name|startOffset
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|result
operator|=
name|startOffset
expr_stmt|;
name|result
operator|=
literal|29
operator|*
name|result
operator|+
name|endOffset
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
