begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
comment|/**  * Lightweight class to hold term, weight, and positions used for scoring this  * term.  */
end_comment
begin_class
DECL|class|WeightedSpanTerm
specifier|public
class|class
name|WeightedSpanTerm
extends|extends
name|WeightedTerm
block|{
DECL|field|positionSensitive
name|boolean
name|positionSensitive
decl_stmt|;
DECL|field|positionSpans
specifier|private
name|List
name|positionSpans
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/**    * @param weight    * @param term    */
DECL|method|WeightedSpanTerm
specifier|public
name|WeightedSpanTerm
parameter_list|(
name|float
name|weight
parameter_list|,
name|String
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|positionSpans
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
comment|/**    * @param weight    * @param term    * @param positionSensitive    */
DECL|method|WeightedSpanTerm
specifier|public
name|WeightedSpanTerm
parameter_list|(
name|float
name|weight
parameter_list|,
name|String
name|term
parameter_list|,
name|boolean
name|positionSensitive
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|positionSensitive
operator|=
name|positionSensitive
expr_stmt|;
block|}
comment|/**    * Checks to see if this term is valid at<code>position</code>.    *    * @param position    *            to check against valid term postions    * @return true iff this term is a hit at this position    */
DECL|method|checkPosition
specifier|public
name|boolean
name|checkPosition
parameter_list|(
name|int
name|position
parameter_list|)
block|{
comment|// There would probably be a slight speed improvement if PositionSpans
comment|// where kept in some sort of priority queue - that way this method
comment|// could
comment|// bail early without checking each PositionSpan.
name|Iterator
name|positionSpanIt
init|=
name|positionSpans
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|positionSpanIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PositionSpan
name|posSpan
init|=
operator|(
name|PositionSpan
operator|)
name|positionSpanIt
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|(
name|position
operator|>=
name|posSpan
operator|.
name|start
operator|)
operator|&&
operator|(
name|position
operator|<=
name|posSpan
operator|.
name|end
operator|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|addPositionSpans
specifier|public
name|void
name|addPositionSpans
parameter_list|(
name|List
name|positionSpans
parameter_list|)
block|{
name|this
operator|.
name|positionSpans
operator|.
name|addAll
argument_list|(
name|positionSpans
argument_list|)
expr_stmt|;
block|}
DECL|method|isPositionSensitive
specifier|public
name|boolean
name|isPositionSensitive
parameter_list|()
block|{
return|return
name|positionSensitive
return|;
block|}
DECL|method|setPositionSensitive
specifier|public
name|void
name|setPositionSensitive
parameter_list|(
name|boolean
name|positionSensitive
parameter_list|)
block|{
name|this
operator|.
name|positionSensitive
operator|=
name|positionSensitive
expr_stmt|;
block|}
DECL|method|getPositionSpans
specifier|public
name|List
name|getPositionSpans
parameter_list|()
block|{
return|return
name|positionSpans
return|;
block|}
block|}
end_class
begin_comment
comment|// Utility class to store a Span
end_comment
begin_class
DECL|class|PositionSpan
class|class
name|PositionSpan
block|{
DECL|field|start
name|int
name|start
decl_stmt|;
DECL|field|end
name|int
name|end
decl_stmt|;
DECL|method|PositionSpan
specifier|public
name|PositionSpan
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
block|}
end_class
end_unit
