begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexCommit
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
name|IndexDeletionPolicy
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
name|store
operator|.
name|Directory
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
name|*
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
name|ConcurrentHashMap
import|;
end_import
begin_comment
comment|/**  * A wrapper for an IndexDeletionPolicy instance.  *<p/>  * Provides features for looking up IndexCommit given a version. Allows reserving index  * commit points for certain amounts of time to support features such as index replication  * or snapshooting directly out of a live index directory.  *  * @version $Id$  * @see org.apache.lucene.index.IndexDeletionPolicy  */
end_comment
begin_class
DECL|class|IndexDeletionPolicyWrapper
specifier|public
class|class
name|IndexDeletionPolicyWrapper
implements|implements
name|IndexDeletionPolicy
block|{
DECL|field|deletionPolicy
specifier|private
specifier|final
name|IndexDeletionPolicy
name|deletionPolicy
decl_stmt|;
DECL|field|solrVersionVsCommits
specifier|private
specifier|volatile
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|solrVersionVsCommits
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reserves
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|reserves
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|latestCommit
specifier|private
specifier|volatile
name|IndexCommit
name|latestCommit
decl_stmt|;
DECL|method|IndexDeletionPolicyWrapper
specifier|public
name|IndexDeletionPolicyWrapper
parameter_list|(
name|IndexDeletionPolicy
name|deletionPolicy
parameter_list|)
block|{
name|this
operator|.
name|deletionPolicy
operator|=
name|deletionPolicy
expr_stmt|;
block|}
comment|/**    * Gets the most recent commit point    *<p/>    * It is recommended to reserve a commit point for the duration of usage so that    * it is not deleted by the underlying deletion policy    *    * @return the most recent commit point    */
DECL|method|getLatestCommit
specifier|public
name|IndexCommit
name|getLatestCommit
parameter_list|()
block|{
return|return
name|latestCommit
return|;
block|}
DECL|method|getWrappedDeletionPolicy
specifier|public
name|IndexDeletionPolicy
name|getWrappedDeletionPolicy
parameter_list|()
block|{
return|return
name|deletionPolicy
return|;
block|}
comment|/**    * Set the duration for which commit point is to be reserved by the deletion policy.    *    * @param indexVersion version of the commit point to be reserved    * @param reserveTime  time in milliseconds for which the commit point is to be reserved    */
DECL|method|setReserveDuration
specifier|public
name|void
name|setReserveDuration
parameter_list|(
name|Long
name|indexVersion
parameter_list|,
name|long
name|reserveTime
parameter_list|)
block|{
name|long
name|timeToSet
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|reserveTime
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Long
name|previousTime
init|=
name|reserves
operator|.
name|put
argument_list|(
name|indexVersion
argument_list|,
name|timeToSet
argument_list|)
decl_stmt|;
comment|// this is the common success case: the older time didn't exist, or
comment|// came before the new time.
if|if
condition|(
name|previousTime
operator|==
literal|null
operator|||
name|previousTime
operator|<=
name|timeToSet
condition|)
break|break;
comment|// At this point, we overwrote a longer reservation, so we want to restore the older one.
comment|// the problem is that an even longer reservation may come in concurrently
comment|// and we don't want to overwrite that one too.  We simply keep retrying in a loop
comment|// with the maximum time value we have seen.
name|timeToSet
operator|=
name|previousTime
expr_stmt|;
block|}
block|}
DECL|method|cleanReserves
specifier|private
name|void
name|cleanReserves
parameter_list|()
block|{
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|reserves
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|<
name|currentTime
condition|)
block|{
name|reserves
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|wrap
specifier|private
name|List
argument_list|<
name|IndexCommitWrapper
argument_list|>
name|wrap
parameter_list|(
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|list
parameter_list|)
block|{
name|List
argument_list|<
name|IndexCommitWrapper
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexCommitWrapper
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexCommit
name|indexCommit
range|:
name|list
control|)
name|result
operator|.
name|add
argument_list|(
operator|new
name|IndexCommitWrapper
argument_list|(
name|indexCommit
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Internal use for Lucene... do not explicitly call.    */
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
name|list
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|IndexCommitWrapper
argument_list|>
name|wrapperList
init|=
name|wrap
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|deletionPolicy
operator|.
name|onInit
argument_list|(
name|wrapperList
argument_list|)
expr_stmt|;
name|updateCommitPoints
argument_list|(
name|wrapperList
argument_list|)
expr_stmt|;
name|cleanReserves
argument_list|()
expr_stmt|;
block|}
comment|/**    * Internal use for Lucene... do not explicitly call.    */
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
name|list
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|IndexCommitWrapper
argument_list|>
name|wrapperList
init|=
name|wrap
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|deletionPolicy
operator|.
name|onCommit
argument_list|(
name|wrapperList
argument_list|)
expr_stmt|;
name|updateCommitPoints
argument_list|(
name|wrapperList
argument_list|)
expr_stmt|;
name|cleanReserves
argument_list|()
expr_stmt|;
block|}
DECL|class|IndexCommitWrapper
specifier|private
class|class
name|IndexCommitWrapper
extends|extends
name|IndexCommit
block|{
DECL|field|delegate
name|IndexCommit
name|delegate
decl_stmt|;
DECL|method|IndexCommitWrapper
name|IndexCommitWrapper
parameter_list|(
name|IndexCommit
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSegmentsFileName
argument_list|()
return|;
block|}
DECL|method|getFileNames
specifier|public
name|Collection
name|getFileNames
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getFileNames
argument_list|()
return|;
block|}
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getDirectory
argument_list|()
return|;
block|}
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|Long
name|reserve
init|=
name|reserves
operator|.
name|get
argument_list|(
name|delegate
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|reserve
operator|!=
literal|null
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|reserve
condition|)
return|return;
name|delegate
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isOptimized
argument_list|()
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
return|return
name|delegate
operator|.
name|equals
argument_list|(
name|o
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
name|delegate
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getVersion
argument_list|()
return|;
block|}
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getGeneration
argument_list|()
return|;
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isDeleted
argument_list|()
return|;
block|}
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getTimestamp
argument_list|()
return|;
block|}
block|}
comment|/**    * @param version the version of the commit point    * @return a commit point corresponding to the given version    */
DECL|method|getCommitPoint
specifier|public
name|IndexCommit
name|getCommitPoint
parameter_list|(
name|Long
name|version
parameter_list|)
block|{
return|return
name|solrVersionVsCommits
operator|.
name|get
argument_list|(
name|version
argument_list|)
return|;
block|}
comment|/**    * Gets the commit points for the index.    * This map instance may change between commits and commit points may be deleted.    * It is recommended to reserve a commit point for the duration of usage    *    * @return a Map of version to commit points    */
DECL|method|getCommits
specifier|public
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|getCommits
parameter_list|()
block|{
return|return
name|solrVersionVsCommits
return|;
block|}
DECL|method|updateCommitPoints
specifier|private
name|void
name|updateCommitPoints
parameter_list|(
name|List
argument_list|<
name|IndexCommitWrapper
argument_list|>
name|list
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexCommitWrapper
name|wrapper
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|wrapper
operator|.
name|isDeleted
argument_list|()
condition|)
name|map
operator|.
name|put
argument_list|(
name|wrapper
operator|.
name|getVersion
argument_list|()
argument_list|,
name|wrapper
operator|.
name|delegate
argument_list|)
expr_stmt|;
block|}
name|solrVersionVsCommits
operator|=
name|map
expr_stmt|;
name|latestCommit
operator|=
operator|(
operator|(
name|list
operator|.
name|get
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|)
operator|.
name|delegate
operator|)
expr_stmt|;
block|}
block|}
end_class
end_unit
