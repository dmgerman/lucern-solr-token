begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.grouping.term
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
operator|.
name|term
package|;
end_package
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
name|LeafReaderContext
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
name|DocValues
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
name|SortedDocValues
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
name|grouping
operator|.
name|AbstractAllGroupsCollector
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
name|SentinelIntSet
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
comment|/**  * A collector that collects all groups that match the  * query. Only the group value is collected, and the order  * is undefined.  This collector does not determine  * the most relevant document of a group.  *<p>  * Implementation detail: an int hash set (SentinelIntSet)  * is used to detect if a group is already added to the  * total count.  For each segment the int set is cleared and filled  * with previous counted groups that occur in the new  * segment.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermAllGroupsCollector
specifier|public
class|class
name|TermAllGroupsCollector
extends|extends
name|AbstractAllGroupsCollector
argument_list|<
name|BytesRef
argument_list|>
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
DECL|field|index
specifier|private
name|SortedDocValues
name|index
decl_stmt|;
comment|/**    * Expert: Constructs a {@link AbstractAllGroupsCollector}    *    * @param groupField  The field to group by    * @param initialSize The initial allocation size of the    *                    internal int set and group list    *                    which should roughly match the total    *                    number of expected unique groups. Be aware that the    *                    heap usage is 4 bytes * initialSize.    */
DECL|method|TermAllGroupsCollector
specifier|public
name|TermAllGroupsCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|ordSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|initialSize
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|groups
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
block|}
comment|/**    * Constructs a {@link AbstractAllGroupsCollector}. This sets the    * initial allocation size for the internal int set and group    * list to 128.    *    * @param groupField The field to group by    */
DECL|method|TermAllGroupsCollector
specifier|public
name|TermAllGroupsCollector
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
annotation|@
name|Override
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
specifier|final
name|BytesRef
name|term
decl_stmt|;
if|if
condition|(
name|key
operator|==
operator|-
literal|1
condition|)
block|{
name|term
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|index
operator|.
name|lookupOrd
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|groups
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
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
if|if
condition|(
name|countedGroup
operator|==
literal|null
condition|)
block|{
name|ordSet
operator|.
name|put
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|ord
init|=
name|index
operator|.
name|lookupTerm
argument_list|(
name|countedGroup
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
block|}
block|}
end_class
end_unit
