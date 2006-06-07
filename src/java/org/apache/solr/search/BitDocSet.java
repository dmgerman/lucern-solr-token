begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/**  *<code>BitDocSet</code> represents an unordered set of Lucene Document Ids  * using a BitSet.  A set bit represents inclusion in the set for that document.  *  * @author yonik  * @version $Id$  * @since solr 0.9  */
end_comment
begin_class
DECL|class|BitDocSet
specifier|public
class|class
name|BitDocSet
extends|extends
name|DocSetBase
block|{
DECL|field|bits
specifier|final
name|BitSet
name|bits
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
comment|// number of docs in the set (cached for perf)
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|()
block|{
name|bits
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
block|}
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|(
name|BitSet
name|bits
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|(
name|BitSet
name|bits
parameter_list|,
name|int
name|size
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
name|size
operator|=
name|size
expr_stmt|;
block|}
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIterator
argument_list|()
block|{
name|int
name|pos
init|=
name|bits
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|>=
literal|0
return|;
block|}
specifier|public
name|Integer
name|next
parameter_list|()
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|bits
operator|.
name|clear
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|int
name|old
init|=
name|pos
decl_stmt|;
name|pos
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|old
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
literal|0.0f
return|;
block|}
block|}
return|;
block|}
comment|/**    *    * @return the<b>internal</b> BitSet that should<b>not</b> be modified.    */
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
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate size
block|}
DECL|method|addUnique
specifier|public
name|void
name|addUnique
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate size
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
return|return
name|size
return|;
return|return
name|size
operator|=
name|bits
operator|.
name|cardinality
argument_list|()
return|;
block|}
comment|/**    * The number of set bits - size - is cached.  If the bitset is changed externally,    * this method should be used to invalidate the previously cached size.    */
DECL|method|invalidateSize
specifier|public
name|void
name|invalidateSize
parameter_list|()
block|{
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|memSize
specifier|public
name|long
name|memSize
parameter_list|()
block|{
return|return
operator|(
name|bits
operator|.
name|size
argument_list|()
operator|>>
literal|3
operator|)
operator|+
literal|16
return|;
block|}
block|}
end_class
end_unit
