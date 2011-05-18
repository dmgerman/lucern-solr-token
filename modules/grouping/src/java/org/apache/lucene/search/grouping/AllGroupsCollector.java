begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Collector
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
name|FieldCache
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
name|Scorer
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
name|Collection
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
comment|/**  * A collector that collects all groups that match the  * query. Only the group value is collected, and the order  * is undefined.  This collector does not determine  * the most relevant document of a group.  *  *<p/>  * Implementation detail: an int hash set (SentinelIntSet)  * is used to detect if a group is already added to the  * total count.  For each segment the int set is cleared and filled  * with previous counted groups that occur in the new  * segment.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AllGroupsCollector
specifier|public
class|class
name|AllGroupsCollector
extends|extends
name|Collector
block|{
DECL|field|DEFAULT_INITIAL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_INITIAL_SIZE
init|=
literal|128
decl_stmt|;
DECL|field|groupField
specifier|private
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|ordSet
specifier|private
specifier|final
name|SentinelIntSet
name|ordSet
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|groups
decl_stmt|;
DECL|field|spareBytesRef
specifier|private
specifier|final
name|BytesRef
name|spareBytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|index
specifier|private
name|FieldCache
operator|.
name|DocTermsIndex
name|index
decl_stmt|;
comment|/**    * Expert: Constructs a {@link AllGroupsCollector}    *    * @param groupField  The field to group by    * @param initialSize The initial allocation size of the    * internal int set and group list    * which should roughly match the total    * number of expected unique groups. Be aware that the    * heap usage is 4 bytes * initialSize.    */
DECL|method|AllGroupsCollector
specifier|public
name|AllGroupsCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|ordSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|initialSize
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|groups
operator|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a {@link AllGroupsCollector}. This sets the    * initial allocation size for the internal int set and group    * list to 128.    *    * @param groupField The field to group by    */
DECL|method|AllGroupsCollector
specifier|public
name|AllGroupsCollector
parameter_list|(
name|String
name|groupField
parameter_list|)
block|{
name|this
argument_list|(
name|groupField
argument_list|,
name|DEFAULT_INITIAL_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{   }
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|key
init|=
name|index
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ordSet
operator|.
name|exists
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|ordSet
operator|.
name|put
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|BytesRef
name|term
init|=
name|key
operator|==
literal|0
condition|?
literal|null
else|:
name|index
operator|.
name|getTerm
argument_list|(
name|doc
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the total number of groups for the executed search.    * This is a convenience method. The following code snippet has the same effect:<pre>getGroups().size()</pre>    *    * @return The total number of groups for the executed search    */
DECL|method|getGroupCount
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
return|return
name|groups
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns the group values    *<p/>    * This is an unordered collections of group values. For each group that matched the query there is a {@link BytesRef}    * representing a group value.    *    * @return the group values    */
DECL|method|getGroups
specifier|public
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
operator|.
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|context
operator|.
name|reader
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
comment|// Clear ordSet and fill it with previous encountered groups that can occur in the current segment.
name|ordSet
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|BytesRef
name|countedGroup
range|:
name|groups
control|)
block|{
name|int
name|ord
init|=
name|index
operator|.
name|binarySearchLookup
argument_list|(
name|countedGroup
argument_list|,
name|spareBytesRef
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|ordSet
operator|.
name|put
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
