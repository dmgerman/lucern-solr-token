begin_unit
begin_package
DECL|package|org.apache.lucene.facet.example.merge
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|example
operator|.
name|merge
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
name|List
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
name|example
operator|.
name|ExampleUtils
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
name|index
operator|.
name|OrdinalMappingAtomicReader
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
operator|.
name|DiskOrdinalMap
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
operator|.
name|MemoryOrdinalMap
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
operator|.
name|OrdinalMap
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
name|AtomicReader
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
name|AtomicReaderContext
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
name|MultiReader
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TaxonomyMergeUtils
specifier|public
class|class
name|TaxonomyMergeUtils
block|{
comment|/**    * Merges the given taxonomy and index directories. Note that this method    * opens {@link DirectoryTaxonomyWriter} and {@link IndexWriter} on the    * respective destination indexes. Therefore if you have a writer open on any    * of them, it should be closed, or you should use    * {@link #merge(Directory, Directory, IndexWriter, DirectoryTaxonomyWriter)}    * instead.    *     * @see #merge(Directory, Directory, IndexWriter, DirectoryTaxonomyWriter)    */
DECL|method|merge
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|Directory
name|srcIndexDir
parameter_list|,
name|Directory
name|srcTaxDir
parameter_list|,
name|Directory
name|destIndexDir
parameter_list|,
name|Directory
name|destTaxDir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|destIndexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|destIndexDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|ExampleUtils
operator|.
name|EXAMPLE_VER
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|destTaxWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|destTaxDir
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|srcIndexDir
argument_list|,
name|srcTaxDir
argument_list|,
operator|new
name|MemoryOrdinalMap
argument_list|()
argument_list|,
name|destIndexWriter
argument_list|,
name|destTaxWriter
argument_list|)
expr_stmt|;
name|destTaxWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|destIndexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Merges the given taxonomy and index directories and commits the changes to    * the given writers. This method uses {@link MemoryOrdinalMap} to store the    * mapped ordinals. If you cannot afford the memory, you can use    * {@link #merge(Directory, Directory, DirectoryTaxonomyWriter.OrdinalMap, IndexWriter, DirectoryTaxonomyWriter)}    * by passing {@link DiskOrdinalMap}.    *     * @see #merge(Directory, Directory, DirectoryTaxonomyWriter.OrdinalMap, IndexWriter, DirectoryTaxonomyWriter)    */
DECL|method|merge
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|Directory
name|srcIndexDir
parameter_list|,
name|Directory
name|srcTaxDir
parameter_list|,
name|IndexWriter
name|destIndexWriter
parameter_list|,
name|DirectoryTaxonomyWriter
name|destTaxWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|merge
argument_list|(
name|srcIndexDir
argument_list|,
name|srcTaxDir
argument_list|,
operator|new
name|MemoryOrdinalMap
argument_list|()
argument_list|,
name|destIndexWriter
argument_list|,
name|destTaxWriter
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the given taxonomy and index directories and commits the changes to    * the given writers.    */
DECL|method|merge
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|Directory
name|srcIndexDir
parameter_list|,
name|Directory
name|srcTaxDir
parameter_list|,
name|OrdinalMap
name|map
parameter_list|,
name|IndexWriter
name|destIndexWriter
parameter_list|,
name|DirectoryTaxonomyWriter
name|destTaxWriter
parameter_list|)
throws|throws
name|IOException
block|{
comment|// merge the taxonomies
name|destTaxWriter
operator|.
name|addTaxonomy
argument_list|(
name|srcTaxDir
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|int
name|ordinalMap
index|[]
init|=
name|map
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|FacetIndexingParams
name|params
init|=
name|FacetIndexingParams
operator|.
name|ALL_PARENTS
decl_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|srcIndexDir
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|int
name|numReaders
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
name|AtomicReader
name|wrappedLeaves
index|[]
init|=
operator|new
name|AtomicReader
index|[
name|numReaders
index|]
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
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
name|wrappedLeaves
index|[
name|i
index|]
operator|=
operator|new
name|OrdinalMappingAtomicReader
argument_list|(
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
argument_list|,
name|ordinalMap
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|destIndexWriter
operator|.
name|addIndexes
argument_list|(
operator|new
name|MultiReader
argument_list|(
name|wrappedLeaves
argument_list|)
argument_list|)
expr_stmt|;
comment|// commit changes to taxonomy and index respectively.
name|destTaxWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|destIndexWriter
operator|.
name|commit
argument_list|()
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
block|}
block|}
end_class
end_unit
