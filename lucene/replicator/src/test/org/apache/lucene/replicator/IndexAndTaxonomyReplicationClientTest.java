begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HashMap
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
name|Callable
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
name|AtomicInteger
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
name|facet
operator|.
name|DrillDownQuery
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
name|FacetField
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
name|Facets
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
name|FacetsCollector
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
name|FacetsConfig
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
name|FastTaxonomyFacetCounts
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
name|TaxonomyReader
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
name|TaxonomyWriter
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
name|DirectoryTaxonomyReader
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
name|index
operator|.
name|DirectoryReader
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
name|replicator
operator|.
name|IndexAndTaxonomyRevision
operator|.
name|SnapshotDirectoryTaxonomyWriter
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
name|replicator
operator|.
name|ReplicationClient
operator|.
name|ReplicationHandler
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
name|replicator
operator|.
name|ReplicationClient
operator|.
name|SourceDirectoryFactory
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
name|MatchAllDocsQuery
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
name|TopDocs
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
name|MockDirectoryWrapper
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
name|IOUtils
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
name|TestUtil
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
name|ThreadInterruptedException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|IndexAndTaxonomyReplicationClientTest
specifier|public
class|class
name|IndexAndTaxonomyReplicationClientTest
extends|extends
name|ReplicatorTestCase
block|{
DECL|class|IndexAndTaxonomyReadyCallback
specifier|private
specifier|static
class|class
name|IndexAndTaxonomyReadyCallback
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|indexDir
DECL|field|taxoDir
specifier|private
specifier|final
name|Directory
name|indexDir
decl_stmt|,
name|taxoDir
decl_stmt|;
DECL|field|indexReader
specifier|private
name|DirectoryReader
name|indexReader
decl_stmt|;
DECL|field|taxoReader
specifier|private
name|DirectoryTaxonomyReader
name|taxoReader
decl_stmt|;
DECL|field|config
specifier|private
name|FacetsConfig
name|config
decl_stmt|;
DECL|field|lastIndexGeneration
specifier|private
name|long
name|lastIndexGeneration
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|IndexAndTaxonomyReadyCallback
specifier|public
name|IndexAndTaxonomyReadyCallback
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
name|this
operator|.
name|taxoDir
operator|=
name|taxoDir
expr_stmt|;
name|config
operator|=
operator|new
name|FacetsConfig
argument_list|()
expr_stmt|;
name|config
operator|.
name|setHierarchical
argument_list|(
literal|"A"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|indexDir
argument_list|)
condition|)
block|{
name|indexReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|lastIndexGeneration
operator|=
name|indexReader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getGeneration
argument_list|()
expr_stmt|;
name|taxoReader
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|call
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|indexReader
operator|==
literal|null
condition|)
block|{
name|indexReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|lastIndexGeneration
operator|=
name|indexReader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getGeneration
argument_list|()
expr_stmt|;
name|taxoReader
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// verify search index
name|DirectoryReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"should not have reached here if no changes were made to the index"
argument_list|,
name|newReader
argument_list|)
expr_stmt|;
name|long
name|newGeneration
init|=
name|newReader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected newer generation; current="
operator|+
name|lastIndexGeneration
operator|+
literal|" new="
operator|+
name|newGeneration
argument_list|,
name|newGeneration
operator|>
name|lastIndexGeneration
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
name|newReader
expr_stmt|;
name|lastIndexGeneration
operator|=
name|newGeneration
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
comment|// verify taxonomy index
name|DirectoryTaxonomyReader
name|newTaxoReader
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|taxoReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newTaxoReader
operator|!=
literal|null
condition|)
block|{
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
operator|=
name|newTaxoReader
expr_stmt|;
block|}
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
comment|// verify faceted search
name|int
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|indexReader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|VERSION_ID
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"A"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|,
literal|16
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|DrillDownQuery
name|drillDown
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|drillDown
operator|.
name|add
argument_list|(
literal|"A"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|drillDown
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|publishIndexDir
DECL|field|publishTaxoDir
specifier|private
name|Directory
name|publishIndexDir
decl_stmt|,
name|publishTaxoDir
decl_stmt|;
DECL|field|handlerIndexDir
DECL|field|handlerTaxoDir
specifier|private
name|MockDirectoryWrapper
name|handlerIndexDir
decl_stmt|,
name|handlerTaxoDir
decl_stmt|;
DECL|field|replicator
specifier|private
name|Replicator
name|replicator
decl_stmt|;
DECL|field|sourceDirFactory
specifier|private
name|SourceDirectoryFactory
name|sourceDirFactory
decl_stmt|;
DECL|field|client
specifier|private
name|ReplicationClient
name|client
decl_stmt|;
DECL|field|handler
specifier|private
name|ReplicationHandler
name|handler
decl_stmt|;
DECL|field|publishIndexWriter
specifier|private
name|IndexWriter
name|publishIndexWriter
decl_stmt|;
DECL|field|publishTaxoWriter
specifier|private
name|SnapshotDirectoryTaxonomyWriter
name|publishTaxoWriter
decl_stmt|;
DECL|field|config
specifier|private
name|FacetsConfig
name|config
decl_stmt|;
DECL|field|callback
specifier|private
name|IndexAndTaxonomyReadyCallback
name|callback
decl_stmt|;
DECL|field|clientWorkDir
specifier|private
name|File
name|clientWorkDir
decl_stmt|;
DECL|field|VERSION_ID
specifier|private
specifier|static
specifier|final
name|String
name|VERSION_ID
init|=
literal|"version"
decl_stmt|;
DECL|method|assertHandlerRevision
specifier|private
name|void
name|assertHandlerRevision
parameter_list|(
name|int
name|expectedID
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// loop as long as client is alive. test-framework will terminate us if
comment|// there's a serious bug, e.g. client doesn't really update. otherwise,
comment|// introducing timeouts is not good, can easily lead to false positives.
while|while
condition|(
name|client
operator|.
name|isUpdateThreadAlive
argument_list|()
condition|)
block|{
comment|// give client a chance to update
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|handlerID
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|VERSION_ID
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedID
operator|==
name|handlerID
condition|)
block|{
return|return;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// we can hit IndexNotFoundException or e.g. EOFException (on
comment|// segments_N) because it is being copied at the same time it is read by
comment|// DirectoryReader.open().
block|}
block|}
block|}
DECL|method|createRevision
specifier|private
name|Revision
name|createRevision
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|publishIndexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|publishTaxoWriter
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|publishIndexWriter
operator|.
name|setCommitData
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|VERSION_ID
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|publishIndexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|publishTaxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
operator|new
name|IndexAndTaxonomyRevision
argument_list|(
name|publishIndexWriter
argument_list|,
name|publishTaxoWriter
argument_list|)
return|;
block|}
DECL|method|newDocument
specifier|private
name|Document
name|newDocument
parameter_list|(
name|TaxonomyWriter
name|taxoWriter
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"A"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|config
operator|.
name|build
argument_list|(
name|publishTaxoWriter
argument_list|,
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|publishIndexDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|publishTaxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|handlerIndexDir
operator|=
name|newMockDirectory
argument_list|()
expr_stmt|;
name|handlerTaxoDir
operator|=
name|newMockDirectory
argument_list|()
expr_stmt|;
name|clientWorkDir
operator|=
name|createTempDir
argument_list|(
literal|"replicationClientTest"
argument_list|)
expr_stmt|;
name|sourceDirFactory
operator|=
operator|new
name|PerSessionDirectoryFactory
argument_list|(
name|clientWorkDir
argument_list|)
expr_stmt|;
name|replicator
operator|=
operator|new
name|LocalReplicator
argument_list|()
expr_stmt|;
name|callback
operator|=
operator|new
name|IndexAndTaxonomyReadyCallback
argument_list|(
name|handlerIndexDir
argument_list|,
name|handlerTaxoDir
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|IndexAndTaxonomyReplicationHandler
argument_list|(
name|handlerIndexDir
argument_list|,
name|handlerTaxoDir
argument_list|,
name|callback
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|ReplicationClient
argument_list|(
name|replicator
argument_list|,
name|handler
argument_list|,
name|sourceDirFactory
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|publishIndexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|publishIndexDir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|publishTaxoWriter
operator|=
operator|new
name|SnapshotDirectoryTaxonomyWriter
argument_list|(
name|publishTaxoDir
argument_list|)
expr_stmt|;
name|config
operator|=
operator|new
name|FacetsConfig
argument_list|()
expr_stmt|;
name|config
operator|.
name|setHierarchical
argument_list|(
literal|"A"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|publishIndexWriter
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|client
argument_list|,
name|callback
argument_list|,
name|publishTaxoWriter
argument_list|,
name|replicator
argument_list|,
name|publishIndexDir
argument_list|,
name|publishTaxoDir
argument_list|,
name|handlerIndexDir
argument_list|,
name|handlerTaxoDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoUpdateThread
specifier|public
name|void
name|testNoUpdateThread
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
literal|"no version expected at start"
argument_list|,
name|handler
operator|.
name|currentVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Callback validates the replicated index
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
comment|// make sure updating twice, when in fact there's nothing to update, works
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
comment|// Publish two revisions without update, handler should be upgraded to latest
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestart
specifier|public
name|void
name|testRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|client
operator|.
name|stopUpdateThread
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|ReplicationClient
argument_list|(
name|replicator
argument_list|,
name|handler
argument_list|,
name|sourceDirFactory
argument_list|)
expr_stmt|;
comment|// Publish two revisions without update, handler should be upgraded to latest
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateThread
specifier|public
name|void
name|testUpdateThread
parameter_list|()
throws|throws
name|Exception
block|{
name|client
operator|.
name|startUpdateThread
argument_list|(
literal|10
argument_list|,
literal|"indexTaxo"
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertHandlerRevision
argument_list|(
literal|1
argument_list|,
name|handlerIndexDir
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertHandlerRevision
argument_list|(
literal|2
argument_list|,
name|handlerIndexDir
argument_list|)
expr_stmt|;
comment|// Publish two revisions without update, handler should be upgraded to latest
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertHandlerRevision
argument_list|(
literal|4
argument_list|,
name|handlerIndexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecreateTaxonomy
specifier|public
name|void
name|testRecreateTaxonomy
parameter_list|()
throws|throws
name|Exception
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
comment|// recreate index and taxonomy
name|Directory
name|newTaxo
init|=
name|newDirectory
argument_list|()
decl_stmt|;
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|newTaxo
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|publishTaxoWriter
operator|.
name|replaceTaxonomy
argument_list|(
name|newTaxo
argument_list|)
expr_stmt|;
name|publishIndexWriter
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|newTaxo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*    * This test verifies that the client and handler do not end up in a corrupt    * index if exceptions are thrown at any point during replication. Either when    * a client copies files from the server to the temporary space, or when the    * handler copies them to the index directory.    */
annotation|@
name|Test
DECL|method|testConsistencyOnExceptions
specifier|public
name|void
name|testConsistencyOnExceptions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// so the handler's index isn't empty
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|callback
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Replicator violates write-once policy. It may be that the
comment|// handler copies files to the index dir, then fails to copy a
comment|// file and reverts the copy operation. On the next attempt, it
comment|// will copy the same file again. There is nothing wrong with this
comment|// in a real system, but it does violate write-once, and MDW
comment|// doesn't like it. Disabling it means that we won't catch cases
comment|// where the handler overwrites an existing index file, but
comment|// there's nothing currently we can do about it, unless we don't
comment|// use MDW.
name|handlerIndexDir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// wrap sourceDirFactory to return a MockDirWrapper so we can simulate errors
specifier|final
name|SourceDirectoryFactory
name|in
init|=
name|sourceDirFactory
decl_stmt|;
specifier|final
name|AtomicInteger
name|failures
init|=
operator|new
name|AtomicInteger
argument_list|(
name|atLeast
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|sourceDirFactory
operator|=
operator|new
name|SourceDirectoryFactory
argument_list|()
block|{
specifier|private
name|long
name|clientMaxSize
init|=
literal|100
decl_stmt|,
name|handlerIndexMaxSize
init|=
literal|100
decl_stmt|,
name|handlerTaxoMaxSize
init|=
literal|100
decl_stmt|;
specifier|private
name|double
name|clientExRate
init|=
literal|1.0
decl_stmt|,
name|handlerIndexExRate
init|=
literal|1.0
decl_stmt|,
name|handlerTaxoExRate
init|=
literal|1.0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|cleanupSession
parameter_list|(
name|String
name|sessionID
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|cleanupSession
argument_list|(
name|sessionID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"synthetic-access"
argument_list|)
annotation|@
name|Override
specifier|public
name|Directory
name|getDirectory
parameter_list|(
name|String
name|sessionID
parameter_list|,
name|String
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|in
operator|.
name|getDirectory
argument_list|(
name|sessionID
argument_list|,
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|failures
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// client should fail, return wrapped dir
name|MockDirectoryWrapper
name|mdw
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|mdw
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|clientExRate
argument_list|)
expr_stmt|;
name|mdw
operator|.
name|setMaxSizeInBytes
argument_list|(
name|clientMaxSize
argument_list|)
expr_stmt|;
name|mdw
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|clientExRate
argument_list|)
expr_stmt|;
name|mdw
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|clientMaxSize
operator|*=
literal|2
expr_stmt|;
name|clientExRate
operator|/=
literal|2
expr_stmt|;
return|return
name|mdw
return|;
block|}
if|if
condition|(
name|failures
operator|.
name|get
argument_list|()
operator|>
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// handler should fail
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// index dir fail
name|handlerIndexDir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|handlerIndexMaxSize
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|handlerIndexExRate
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|handlerIndexExRate
argument_list|)
expr_stmt|;
name|handlerIndexMaxSize
operator|*=
literal|2
expr_stmt|;
name|handlerIndexExRate
operator|/=
literal|2
expr_stmt|;
block|}
else|else
block|{
comment|// taxo dir fail
name|handlerTaxoDir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|handlerTaxoMaxSize
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|handlerTaxoExRate
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|handlerTaxoExRate
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|handlerTaxoMaxSize
operator|*=
literal|2
expr_stmt|;
name|handlerTaxoExRate
operator|/=
literal|2
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// disable all errors
name|handlerIndexDir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
block|}
expr_stmt|;
name|handler
operator|=
operator|new
name|IndexAndTaxonomyReplicationHandler
argument_list|(
name|handlerIndexDir
argument_list|,
name|handlerTaxoDir
argument_list|,
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.2
operator|&&
name|failures
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"random exception from callback"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// wrap handleUpdateException so we can act on the thrown exception
name|client
operator|=
operator|new
name|ReplicationClient
argument_list|(
name|replicator
argument_list|,
name|handler
argument_list|,
name|sourceDirFactory
argument_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"synthetic-access"
argument_list|)
annotation|@
name|Override
specifier|protected
name|void
name|handleUpdateException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|IOException
condition|)
block|{
try|try
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"hit exception during update: "
operator|+
name|t
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|// test that the index can be read and also some basic statistics
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|handlerIndexDir
operator|.
name|getDelegate
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|int
name|version
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|VERSION_ID
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// verify index is fully consistent
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|handlerIndexDir
operator|.
name|getDelegate
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify taxonomy index is fully consistent (since we only add one
comment|// category to all documents, there's nothing much more to validate
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|handlerTaxoDir
operator|.
name|getDelegate
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// count-down number of failures
name|failures
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
assert|assert
name|failures
operator|.
name|get
argument_list|()
operator|>=
literal|0
operator|:
literal|"handler failed too many times: "
operator|+
name|failures
operator|.
name|get
argument_list|()
assert|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
if|if
condition|(
name|failures
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"no more failures expected"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"num failures left: "
operator|+
name|failures
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|t
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|t
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|client
operator|.
name|startUpdateThread
argument_list|(
literal|10
argument_list|,
literal|"indexAndTaxo"
argument_list|)
expr_stmt|;
specifier|final
name|Directory
name|baseHandlerIndexDir
init|=
name|handlerIndexDir
operator|.
name|getDelegate
argument_list|()
decl_stmt|;
name|int
name|numRevisions
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
operator|+
literal|2
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|numRevisions
condition|;
name|i
operator|++
control|)
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertHandlerRevision
argument_list|(
name|i
argument_list|,
name|baseHandlerIndexDir
argument_list|)
expr_stmt|;
block|}
comment|// disable errors -- maybe randomness didn't exhaust all allowed failures,
comment|// and we don't want e.g. CheckIndex to hit false errors.
name|handlerIndexDir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|handlerTaxoDir
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
