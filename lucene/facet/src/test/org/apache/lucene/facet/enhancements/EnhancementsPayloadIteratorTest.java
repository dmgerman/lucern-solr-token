begin_unit
begin_package
DECL|package|org.apache.lucene.facet.enhancements
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
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
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|LuceneTestCase
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
name|enhancements
operator|.
name|EnhancementsPayloadIterator
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
name|enhancements
operator|.
name|association
operator|.
name|AssociationEnhancement
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
name|enhancements
operator|.
name|params
operator|.
name|EnhancementsIndexingParams
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
name|association
operator|.
name|AssociationIndexer
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
name|association
operator|.
name|AssociationUtils
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
name|search
operator|.
name|DrillDown
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
name|CategoryPath
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|EnhancementsPayloadIteratorTest
specifier|public
class|class
name|EnhancementsPayloadIteratorTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|indexDir
specifier|private
specifier|static
name|Directory
name|indexDir
decl_stmt|;
DECL|field|taxoDir
specifier|private
specifier|static
name|Directory
name|taxoDir
decl_stmt|;
DECL|field|indexingParams
specifier|private
specifier|static
name|EnhancementsIndexingParams
name|indexingParams
decl_stmt|;
DECL|field|associationEnhancement
specifier|private
specifier|static
name|AssociationEnhancement
name|associationEnhancement
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|buildAssociationIndex
specifier|public
specifier|static
name|void
name|buildAssociationIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create Directories for the search index and for the taxonomy index
name|indexDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
comment|// index the sample documents
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
literal|"index the sample documents..."
argument_list|)
expr_stmt|;
block|}
name|AssociationIndexer
operator|.
name|index
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
name|indexingParams
operator|=
name|AssociationUtils
operator|.
name|assocIndexingParams
expr_stmt|;
name|associationEnhancement
operator|=
operator|(
name|AssociationEnhancement
operator|)
name|indexingParams
operator|.
name|getCategoryEnhancements
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFullIterator
specifier|public
name|void
name|testFullIterator
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
name|DrillDown
operator|.
name|term
argument_list|(
name|indexingParams
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"tags"
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
decl_stmt|;
name|EnhancementsPayloadIterator
name|iterator
init|=
operator|new
name|EnhancementsPayloadIterator
argument_list|(
name|indexingParams
operator|.
name|getCategoryEnhancements
argument_list|()
argument_list|,
name|indexReader
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected failure of init()"
argument_list|,
name|iterator
operator|.
name|init
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing instance of tags/lucene in doc 0"
argument_list|,
name|iterator
operator|.
name|setdoc
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|assoc
init|=
operator|(
name|Integer
operator|)
name|iterator
operator|.
name|getCategoryData
argument_list|(
name|associationEnhancement
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected association value for tags/lucene in doc 0"
argument_list|,
literal|3
argument_list|,
name|assoc
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing instance of tags/lucene in doc 1"
argument_list|,
name|iterator
operator|.
name|setdoc
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assoc
operator|=
operator|(
name|Integer
operator|)
name|iterator
operator|.
name|getCategoryData
argument_list|(
name|associationEnhancement
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected association value for tags/lucene in doc 1"
argument_list|,
literal|1
argument_list|,
name|assoc
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyIterator
specifier|public
name|void
name|testEmptyIterator
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
name|DrillDown
operator|.
name|term
argument_list|(
name|indexingParams
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f2"
argument_list|)
argument_list|)
decl_stmt|;
name|EnhancementsPayloadIterator
name|iterator
init|=
operator|new
name|EnhancementsPayloadIterator
argument_list|(
name|indexingParams
operator|.
name|getCategoryEnhancements
argument_list|()
argument_list|,
name|indexReader
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected failure of init()"
argument_list|,
name|iterator
operator|.
name|init
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected payload for root/a/f2 in doc 0"
argument_list|,
name|iterator
operator|.
name|setdoc
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected instance of root/a/f2 in doc 1"
argument_list|,
name|iterator
operator|.
name|setdoc
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartialIterator
specifier|public
name|void
name|testPartialIterator
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
name|DrillDown
operator|.
name|term
argument_list|(
name|indexingParams
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"genre"
argument_list|,
literal|"software"
argument_list|)
argument_list|)
decl_stmt|;
name|EnhancementsPayloadIterator
name|iterator
init|=
operator|new
name|EnhancementsPayloadIterator
argument_list|(
name|indexingParams
operator|.
name|getCategoryEnhancements
argument_list|()
argument_list|,
name|indexReader
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected failure of init()"
argument_list|,
name|iterator
operator|.
name|init
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected payload for genre/computing in doc 0"
argument_list|,
name|iterator
operator|.
name|setdoc
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing instance of genre/computing in doc 1"
argument_list|,
name|iterator
operator|.
name|setdoc
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|assoc
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|Integer
operator|)
name|iterator
operator|.
name|getCategoryData
argument_list|(
name|associationEnhancement
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected association value for genre/computing in doc 1"
argument_list|,
literal|0.34f
argument_list|,
name|assoc
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|closeDirectories
specifier|public
specifier|static
name|void
name|closeDirectories
parameter_list|()
throws|throws
name|IOException
block|{
name|indexDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDir
operator|=
literal|null
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
