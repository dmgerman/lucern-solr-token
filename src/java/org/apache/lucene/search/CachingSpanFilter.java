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
name|BitSet
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
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_comment
comment|/**  * Wraps another SpanFilter's result and caches it.  The purpose is to allow  * filters to simply filter, and then wrap with this class to add caching.  */
end_comment
begin_class
DECL|class|CachingSpanFilter
specifier|public
class|class
name|CachingSpanFilter
extends|extends
name|SpanFilter
block|{
DECL|field|filter
specifier|protected
name|SpanFilter
name|filter
decl_stmt|;
comment|/**    * A transient Filter cache.    */
DECL|field|cache
specifier|protected
specifier|transient
name|Map
name|cache
decl_stmt|;
comment|/**    * @param filter Filter to cache results of    */
DECL|method|CachingSpanFilter
specifier|public
name|CachingSpanFilter
parameter_list|(
name|SpanFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanFilterResult
name|result
init|=
name|getCachedResult
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|result
operator|!=
literal|null
condition|?
name|result
operator|.
name|getDocIdSet
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|getCachedResult
specifier|private
name|SpanFilterResult
name|getCachedResult
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanFilterResult
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
operator|new
name|WeakHashMap
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// check cache
name|result
operator|=
operator|(
name|SpanFilterResult
operator|)
name|cache
operator|.
name|get
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|filter
operator|.
name|bitSpans
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|bitSpans
specifier|public
name|SpanFilterResult
name|bitSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getCachedResult
argument_list|(
name|reader
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CachingSpanFilter("
operator|+
name|filter
operator|+
literal|")"
return|;
block|}
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
operator|!
operator|(
name|o
operator|instanceof
name|CachingSpanFilter
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|CachingSpanFilter
operator|)
name|o
operator|)
operator|.
name|filter
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|filter
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x1117BF25
return|;
block|}
block|}
end_class
end_unit
