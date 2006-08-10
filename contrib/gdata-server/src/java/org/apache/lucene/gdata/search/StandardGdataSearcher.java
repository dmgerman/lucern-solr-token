begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
package|;
end_package
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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|gdata
operator|.
name|search
operator|.
name|index
operator|.
name|IndexDocument
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
name|gdata
operator|.
name|utils
operator|.
name|ReferenceCounter
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
name|Term
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
name|search
operator|.
name|Hits
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|QueryFilter
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
name|search
operator|.
name|TermQuery
import|;
end_import
begin_comment
comment|/**  * Standard implementation of  * {@link org.apache.lucene.gdata.search.GDataSearcher}  *   * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|StandardGdataSearcher
specifier|public
class|class
name|StandardGdataSearcher
implements|implements
name|GDataSearcher
argument_list|<
name|String
argument_list|>
block|{
DECL|field|isClosed
specifier|private
specifier|final
name|AtomicBoolean
name|isClosed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|ReferenceCounter
argument_list|<
name|IndexSearcher
argument_list|>
name|searcher
decl_stmt|;
DECL|field|queryFilterCache
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|QueryFilter
argument_list|>
name|queryFilterCache
init|=
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|QueryFilter
argument_list|>
argument_list|()
decl_stmt|;
comment|/** constructs a new GdataSearcher      * @param searcher - the current lucene searcher instance      */
DECL|method|StandardGdataSearcher
specifier|public
name|StandardGdataSearcher
parameter_list|(
name|ReferenceCounter
argument_list|<
name|IndexSearcher
argument_list|>
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"searcher must not be null"
argument_list|)
throw|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.GDataSearcher#search(org.apache.lucene.search.Query,      *      int, int, java.lang.String)      */
DECL|method|search
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|hitcount
parameter_list|,
name|int
name|offset
parameter_list|,
name|String
name|feedId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hitcount
operator|<
literal|0
operator|||
name|offset
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"hit count and offset must not be less than 0"
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|isClosed
operator|.
name|get
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Searcher is closed"
argument_list|)
throw|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"query is null can not apply search"
argument_list|)
throw|;
if|if
condition|(
name|feedId
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"feed id must not be null"
argument_list|)
throw|;
name|QueryFilter
name|filter
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|queryFilterCache
init|)
block|{
name|filter
operator|=
name|queryFilterCache
operator|.
name|get
argument_list|(
name|feedId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
name|filter
operator|=
operator|new
name|QueryFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|IndexDocument
operator|.
name|FIELD_FEED_ID
argument_list|,
name|feedId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|this
operator|.
name|searcher
operator|.
name|get
argument_list|()
decl_stmt|;
name|Hits
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|queryFilterCache
init|)
block|{
name|queryFilterCache
operator|.
name|put
argument_list|(
name|feedId
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
return|return
name|collectHits
argument_list|(
name|hits
argument_list|,
name|hitcount
argument_list|,
name|offset
argument_list|)
return|;
block|}
DECL|method|collectHits
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|collectHits
parameter_list|(
name|Hits
name|hits
parameter_list|,
name|int
name|hitcount
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|hitLength
init|=
name|hits
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|hitLength
operator|<
name|offset
operator|||
name|hitLength
operator|==
literal|0
condition|)
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
return|;
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
operator|--
name|offset
expr_stmt|;
comment|/*          * include the offset          */
name|int
name|remainingHits
init|=
name|hitLength
operator|-
name|offset
decl_stmt|;
name|int
name|returnSize
init|=
name|remainingHits
operator|>
name|hitcount
condition|?
name|hitcount
else|:
name|remainingHits
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|retVal
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|returnSize
argument_list|)
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
name|returnSize
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|offset
operator|++
argument_list|)
decl_stmt|;
comment|/*              * the entry id is sufficient to retrieve the entry from the              * storage. the result will be ordered by score (default)              */
name|Field
name|field
init|=
name|doc
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
decl_stmt|;
name|retVal
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.GDataSearcher#close()      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|this
operator|.
name|isClosed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|searcher
operator|.
name|decrementRef
argument_list|()
expr_stmt|;
block|}
DECL|method|flushFilterCache
specifier|static
name|void
name|flushFilterCache
parameter_list|()
block|{
name|queryFilterCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
