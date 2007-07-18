begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BitSet
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
begin_comment
comment|/**  *  The results of a SpanQueryFilter.  Wraps the BitSet and the position infomration from the SpanQuery  *  *<p/>  * NOTE: This API is still experimental and subject to change.   *  **/
end_comment
begin_class
DECL|class|SpanFilterResult
specifier|public
class|class
name|SpanFilterResult
block|{
DECL|field|bits
specifier|private
name|BitSet
name|bits
decl_stmt|;
DECL|field|positions
specifier|private
name|List
name|positions
decl_stmt|;
comment|//Spans spans;
comment|/**    *    * @param bits The bits for the Filter    * @param positions A List of {@link org.apache.lucene.search.SpanFilterResult.PositionInfo} objects    */
DECL|method|SpanFilterResult
specifier|public
name|SpanFilterResult
parameter_list|(
name|BitSet
name|bits
parameter_list|,
name|List
name|positions
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
block|}
comment|/**    * The first entry in the array corresponds to the first "on" bit.    * Entries are increasing by document order    * @return A List of PositionInfo objects    */
DECL|method|getPositions
specifier|public
name|List
name|getPositions
parameter_list|()
block|{
return|return
name|positions
return|;
block|}
DECL|method|getBits
specifier|public
name|BitSet
name|getBits
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
DECL|class|PositionInfo
specifier|public
specifier|static
class|class
name|PositionInfo
block|{
DECL|field|doc
specifier|private
name|int
name|doc
decl_stmt|;
DECL|field|positions
specifier|private
name|List
name|positions
decl_stmt|;
DECL|method|PositionInfo
specifier|public
name|PositionInfo
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|positions
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|positions
operator|.
name|add
argument_list|(
operator|new
name|StartEnd
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getDoc
specifier|public
name|int
name|getDoc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/**      *      * @return A List of {@link org.apache.lucene.search.SpanFilterResult.StartEnd} objects      */
DECL|method|getPositions
specifier|public
name|List
name|getPositions
parameter_list|()
block|{
return|return
name|positions
return|;
block|}
block|}
DECL|class|StartEnd
specifier|public
specifier|static
class|class
name|StartEnd
block|{
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
DECL|method|StartEnd
specifier|public
name|StartEnd
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
comment|/**      *      * @return The end position of this match      */
DECL|method|getEnd
specifier|public
name|int
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
comment|/**      * The Start position      * @return The start position of this match      */
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
block|}
block|}
end_class
end_unit
