begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
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
name|index
operator|.
name|IndexReader
import|;
end_import
begin_comment
comment|/** Removes matches which overlap with another SpanQuery. */
end_comment
begin_class
DECL|class|SpanNotQuery
specifier|public
class|class
name|SpanNotQuery
extends|extends
name|SpanQuery
block|{
DECL|field|include
specifier|private
name|SpanQuery
name|include
decl_stmt|;
DECL|field|exclude
specifier|private
name|SpanQuery
name|exclude
decl_stmt|;
comment|/** Construct a SpanNotQuery matching spans from<code>include</code> which    * have no overlap with spans from<code>exclude</code>.*/
DECL|method|SpanNotQuery
specifier|public
name|SpanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|)
block|{
name|this
operator|.
name|include
operator|=
name|include
expr_stmt|;
name|this
operator|.
name|exclude
operator|=
name|exclude
expr_stmt|;
if|if
condition|(
operator|!
name|include
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|exclude
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Clauses must have same field."
argument_list|)
throw|;
block|}
comment|/** Return the SpanQuery whose matches are filtered. */
DECL|method|getInclude
specifier|public
name|SpanQuery
name|getInclude
parameter_list|()
block|{
return|return
name|include
return|;
block|}
comment|/** Return the SpanQuery whose matches must not overlap those returned. */
DECL|method|getExclude
specifier|public
name|SpanQuery
name|getExclude
parameter_list|()
block|{
return|return
name|exclude
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|include
operator|.
name|getField
argument_list|()
return|;
block|}
DECL|method|getTerms
specifier|public
name|Collection
name|getTerms
parameter_list|()
block|{
return|return
name|include
operator|.
name|getTerms
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanNot("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|include
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|exclude
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Spans
argument_list|()
block|{
specifier|private
name|Spans
name|includeSpans
init|=
name|include
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|moreInclude
init|=
literal|true
decl_stmt|;
specifier|private
name|Spans
name|excludeSpans
init|=
name|exclude
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|moreExclude
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|moreInclude
condition|)
comment|// move to next include
name|moreInclude
operator|=
name|includeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|moreInclude
operator|&&
name|moreExclude
condition|)
block|{
if|if
condition|(
name|includeSpans
operator|.
name|doc
argument_list|()
operator|>
name|excludeSpans
operator|.
name|doc
argument_list|()
condition|)
comment|// skip exclude
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|skipTo
argument_list|(
name|includeSpans
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|moreExclude
comment|// while exclude is before
operator|&&
name|includeSpans
operator|.
name|doc
argument_list|()
operator|==
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|&&
name|excludeSpans
operator|.
name|end
argument_list|()
operator|<=
name|includeSpans
operator|.
name|start
argument_list|()
condition|)
block|{
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// increment exclude
block|}
if|if
condition|(
operator|!
name|moreExclude
comment|// if no intersection
operator|||
name|includeSpans
operator|.
name|doc
argument_list|()
operator|!=
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|||
name|includeSpans
operator|.
name|end
argument_list|()
operator|<=
name|excludeSpans
operator|.
name|start
argument_list|()
condition|)
break|break;
comment|// we found a match
name|moreInclude
operator|=
name|includeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// intersected: keep scanning
block|}
return|return
name|moreInclude
return|;
block|}
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|moreInclude
condition|)
comment|// skip include
name|moreInclude
operator|=
name|includeSpans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|moreInclude
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|moreExclude
comment|// skip exclude
operator|&&
name|includeSpans
operator|.
name|doc
argument_list|()
operator|>
name|excludeSpans
operator|.
name|doc
argument_list|()
condition|)
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|skipTo
argument_list|(
name|includeSpans
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|moreExclude
comment|// while exclude is before
operator|&&
name|includeSpans
operator|.
name|doc
argument_list|()
operator|==
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|&&
name|excludeSpans
operator|.
name|end
argument_list|()
operator|<=
name|includeSpans
operator|.
name|start
argument_list|()
condition|)
block|{
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// increment exclude
block|}
if|if
condition|(
operator|!
name|moreExclude
comment|// if no intersection
operator|||
name|includeSpans
operator|.
name|doc
argument_list|()
operator|!=
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|||
name|includeSpans
operator|.
name|end
argument_list|()
operator|<=
name|excludeSpans
operator|.
name|start
argument_list|()
condition|)
return|return
literal|true
return|;
comment|// we found a match
return|return
name|next
argument_list|()
return|;
comment|// scan to next match
block|}
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|doc
argument_list|()
return|;
block|}
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|start
argument_list|()
return|;
block|}
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|end
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|SpanNotQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
