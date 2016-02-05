begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|TaxonomyWriterCache
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
name|index
operator|.
name|IndexWriter
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
name|IndexWriterConfig
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|SnapshotDeletionPolicy
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
import|;
end_import
begin_comment
comment|/**  * A {@link Revision} of a single index and taxonomy index files which comprises  * the list of files from both indexes. This revision should be used whenever a  * pair of search and taxonomy indexes need to be replicated together to  * guarantee consistency of both on the replicating (client) side.  *   * @see IndexRevision  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|IndexAndTaxonomyRevision
specifier|public
class|class
name|IndexAndTaxonomyRevision
implements|implements
name|Revision
block|{
comment|/**    * A {@link DirectoryTaxonomyWriter} which sets the underlying    * {@link IndexWriter}'s {@link IndexDeletionPolicy} to    * {@link SnapshotDeletionPolicy}.    */
DECL|class|SnapshotDirectoryTaxonomyWriter
specifier|public
specifier|static
specifier|final
class|class
name|SnapshotDirectoryTaxonomyWriter
extends|extends
name|DirectoryTaxonomyWriter
block|{
DECL|field|sdp
specifier|private
name|SnapshotDeletionPolicy
name|sdp
decl_stmt|;
DECL|field|writer
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
comment|/**      * @see DirectoryTaxonomyWriter#DirectoryTaxonomyWriter(Directory,      *      IndexWriterConfig.OpenMode, TaxonomyWriterCache)      */
DECL|method|SnapshotDirectoryTaxonomyWriter
specifier|public
name|SnapshotDirectoryTaxonomyWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|OpenMode
name|openMode
parameter_list|,
name|TaxonomyWriterCache
name|cache
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|openMode
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
comment|/** @see DirectoryTaxonomyWriter#DirectoryTaxonomyWriter(Directory, IndexWriterConfig.OpenMode) */
DECL|method|SnapshotDirectoryTaxonomyWriter
specifier|public
name|SnapshotDirectoryTaxonomyWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|OpenMode
name|openMode
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|openMode
argument_list|)
expr_stmt|;
block|}
comment|/** @see DirectoryTaxonomyWriter#DirectoryTaxonomyWriter(Directory) */
DECL|method|SnapshotDirectoryTaxonomyWriter
specifier|public
name|SnapshotDirectoryTaxonomyWriter
parameter_list|(
name|Directory
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIndexWriterConfig
specifier|protected
name|IndexWriterConfig
name|createIndexWriterConfig
parameter_list|(
name|OpenMode
name|openMode
parameter_list|)
block|{
name|IndexWriterConfig
name|conf
init|=
name|super
operator|.
name|createIndexWriterConfig
argument_list|(
name|openMode
argument_list|)
decl_stmt|;
name|sdp
operator|=
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|sdp
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|openIndexWriter
specifier|protected
name|IndexWriter
name|openIndexWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexWriterConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|=
name|super
operator|.
name|openIndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
comment|/** Returns the {@link SnapshotDeletionPolicy} used by the underlying {@link IndexWriter}. */
DECL|method|getDeletionPolicy
specifier|public
name|SnapshotDeletionPolicy
name|getDeletionPolicy
parameter_list|()
block|{
return|return
name|sdp
return|;
block|}
comment|/** Returns the {@link IndexWriter} used by this {@link DirectoryTaxonomyWriter}. */
DECL|method|getIndexWriter
specifier|public
name|IndexWriter
name|getIndexWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
block|}
DECL|field|RADIX
specifier|private
specifier|static
specifier|final
name|int
name|RADIX
init|=
literal|16
decl_stmt|;
DECL|field|INDEX_SOURCE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_SOURCE
init|=
literal|"index"
decl_stmt|;
DECL|field|TAXONOMY_SOURCE
specifier|public
specifier|static
specifier|final
name|String
name|TAXONOMY_SOURCE
init|=
literal|"taxo"
decl_stmt|;
DECL|field|indexWriter
specifier|private
specifier|final
name|IndexWriter
name|indexWriter
decl_stmt|;
DECL|field|taxoWriter
specifier|private
specifier|final
name|SnapshotDirectoryTaxonomyWriter
name|taxoWriter
decl_stmt|;
DECL|field|indexCommit
DECL|field|taxoCommit
specifier|private
specifier|final
name|IndexCommit
name|indexCommit
decl_stmt|,
name|taxoCommit
decl_stmt|;
DECL|field|indexSDP
DECL|field|taxoSDP
specifier|private
specifier|final
name|SnapshotDeletionPolicy
name|indexSDP
decl_stmt|,
name|taxoSDP
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|String
name|version
decl_stmt|;
DECL|field|sourceFiles
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|sourceFiles
decl_stmt|;
comment|/** Returns a singleton map of the revision files from the given {@link IndexCommit}. */
DECL|method|revisionFiles
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|revisionFiles
parameter_list|(
name|IndexCommit
name|indexCommit
parameter_list|,
name|IndexCommit
name|taxoCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|files
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|files
operator|.
name|put
argument_list|(
name|INDEX_SOURCE
argument_list|,
name|IndexRevision
operator|.
name|revisionFiles
argument_list|(
name|indexCommit
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|files
operator|.
name|put
argument_list|(
name|TAXONOMY_SOURCE
argument_list|,
name|IndexRevision
operator|.
name|revisionFiles
argument_list|(
name|taxoCommit
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|files
return|;
block|}
comment|/**    * Returns a String representation of a revision's version from the given    * {@link IndexCommit}s of the search and taxonomy indexes.    */
DECL|method|revisionVersion
specifier|public
specifier|static
name|String
name|revisionVersion
parameter_list|(
name|IndexCommit
name|indexCommit
parameter_list|,
name|IndexCommit
name|taxoCommit
parameter_list|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|RADIX
argument_list|)
operator|+
literal|":"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|taxoCommit
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|RADIX
argument_list|)
return|;
block|}
comment|/**    * Constructor over the given {@link IndexWriter}. Uses the last    * {@link IndexCommit} found in the {@link Directory} managed by the given    * writer.    */
DECL|method|IndexAndTaxonomyRevision
specifier|public
name|IndexAndTaxonomyRevision
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|SnapshotDirectoryTaxonomyWriter
name|taxoWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexDeletionPolicy
name|delPolicy
init|=
name|indexWriter
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|delPolicy
operator|instanceof
name|SnapshotDeletionPolicy
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"IndexWriter must be created with SnapshotDeletionPolicy"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexWriter
operator|=
name|indexWriter
expr_stmt|;
name|this
operator|.
name|taxoWriter
operator|=
name|taxoWriter
expr_stmt|;
name|this
operator|.
name|indexSDP
operator|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|delPolicy
expr_stmt|;
name|this
operator|.
name|taxoSDP
operator|=
name|taxoWriter
operator|.
name|getDeletionPolicy
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexCommit
operator|=
name|indexSDP
operator|.
name|snapshot
argument_list|()
expr_stmt|;
name|this
operator|.
name|taxoCommit
operator|=
name|taxoSDP
operator|.
name|snapshot
argument_list|()
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|revisionVersion
argument_list|(
name|indexCommit
argument_list|,
name|taxoCommit
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceFiles
operator|=
name|revisionFiles
argument_list|(
name|indexCommit
argument_list|,
name|taxoCommit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|String
name|version
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|parts
init|=
name|version
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|indexGen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|RADIX
argument_list|)
decl_stmt|;
specifier|final
name|long
name|taxoGen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|,
name|RADIX
argument_list|)
decl_stmt|;
specifier|final
name|long
name|indexCommitGen
init|=
name|indexCommit
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
specifier|final
name|long
name|taxoCommitGen
init|=
name|taxoCommit
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
comment|// if the index generation is not the same as this commit's generation,
comment|// compare by it. Otherwise, compare by the taxonomy generation.
if|if
condition|(
name|indexCommitGen
operator|<
name|indexGen
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|indexCommitGen
operator|>
name|indexGen
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|taxoCommitGen
operator|<
name|taxoGen
condition|?
operator|-
literal|1
else|:
operator|(
name|taxoCommitGen
operator|>
name|taxoGen
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Revision
name|o
parameter_list|)
block|{
name|IndexAndTaxonomyRevision
name|other
init|=
operator|(
name|IndexAndTaxonomyRevision
operator|)
name|o
decl_stmt|;
name|int
name|cmp
init|=
name|indexCommit
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|indexCommit
argument_list|)
decl_stmt|;
return|return
name|cmp
operator|!=
literal|0
condition|?
name|cmp
else|:
name|taxoCommit
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|taxoCommit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceFiles
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|getSourceFiles
parameter_list|()
block|{
return|return
name|sourceFiles
return|;
block|}
annotation|@
name|Override
DECL|method|open
specifier|public
name|InputStream
name|open
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|source
operator|.
name|equals
argument_list|(
name|INDEX_SOURCE
argument_list|)
operator|||
name|source
operator|.
name|equals
argument_list|(
name|TAXONOMY_SOURCE
argument_list|)
operator|:
literal|"invalid source; expected=("
operator|+
name|INDEX_SOURCE
operator|+
literal|" or "
operator|+
name|TAXONOMY_SOURCE
operator|+
literal|") got="
operator|+
name|source
assert|;
name|IndexCommit
name|ic
init|=
name|source
operator|.
name|equals
argument_list|(
name|INDEX_SOURCE
argument_list|)
condition|?
name|indexCommit
else|:
name|taxoCommit
decl_stmt|;
return|return
operator|new
name|IndexInputInputStream
argument_list|(
name|ic
operator|.
name|getDirectory
argument_list|()
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|indexSDP
operator|.
name|release
argument_list|(
name|indexCommit
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|taxoSDP
operator|.
name|release
argument_list|(
name|taxoCommit
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|indexWriter
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|taxoWriter
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"IndexAndTaxonomyRevision version="
operator|+
name|version
operator|+
literal|" files="
operator|+
name|sourceFiles
return|;
block|}
block|}
end_class
end_unit
