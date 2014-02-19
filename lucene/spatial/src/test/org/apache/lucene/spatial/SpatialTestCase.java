begin_unit
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
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
name|analysis
operator|.
name|MockAnalyzer
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
name|codecs
operator|.
name|lucene45
operator|.
name|Lucene45DocValuesFormat
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
name|RandomIndexWriter
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
name|StoredDocument
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
name|ScoreDoc
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
name|util
operator|.
name|TestUtil
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
name|Random
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomGaussian
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomIntBetween
import|;
end_import
begin_comment
comment|/** A base test class for spatial lucene. It's mostly Lucene generic. */
end_comment
begin_class
DECL|class|SpatialTestCase
specifier|public
specifier|abstract
class|class
name|SpatialTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|indexReader
specifier|private
name|DirectoryReader
name|indexReader
decl_stmt|;
DECL|field|indexWriter
specifier|protected
name|RandomIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|indexSearcher
specifier|protected
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
comment|//subclass must initialize
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|indexWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|indexReader
operator|=
name|indexWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|indexSearcher
operator|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
DECL|method|newIndexWriterConfig
specifier|protected
name|IndexWriterConfig
name|newIndexWriterConfig
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
specifier|final
name|IndexWriterConfig
name|indexWriterConfig
init|=
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
comment|//TODO can we randomly choose a doc-values supported format?
if|if
condition|(
name|needsDocValues
argument_list|()
condition|)
name|indexWriterConfig
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysDocValuesFormat
argument_list|(
operator|new
name|Lucene45DocValuesFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
empty_stmt|;
return|return
name|indexWriterConfig
return|;
block|}
DECL|method|needsDocValues
specifier|protected
name|boolean
name|needsDocValues
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|indexWriter
argument_list|,
name|indexReader
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|// ================================================= Helper Methods ================================================
DECL|method|addDocument
specifier|protected
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocumentsAndCommit
specifier|protected
name|void
name|addDocumentsAndCommit
parameter_list|(
name|List
argument_list|<
name|Document
argument_list|>
name|documents
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Document
name|document
range|:
name|documents
control|)
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteAll
specifier|protected
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|commit
specifier|protected
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
name|indexReader
operator|=
name|indexWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|indexSearcher
operator|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyDocumentsIndexed
specifier|protected
name|void
name|verifyDocumentsIndexed
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|executeQuery
specifier|protected
name|SearchResults
name|executeQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
try|try
block|{
name|TopDocs
name|topDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SearchResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|SearchResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
name|results
operator|.
name|add
argument_list|(
operator|new
name|SearchResult
argument_list|(
name|scoreDoc
operator|.
name|score
argument_list|,
name|indexSearcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SearchResults
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|results
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IOException thrown while executing query"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|randomPoint
specifier|protected
name|Point
name|randomPoint
parameter_list|()
block|{
specifier|final
name|Rectangle
name|WB
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
return|return
name|ctx
operator|.
name|makePoint
argument_list|(
name|randomIntBetween
argument_list|(
operator|(
name|int
operator|)
name|WB
operator|.
name|getMinX
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|WB
operator|.
name|getMaxX
argument_list|()
argument_list|)
argument_list|,
name|randomIntBetween
argument_list|(
operator|(
name|int
operator|)
name|WB
operator|.
name|getMinY
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|WB
operator|.
name|getMaxY
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomRectangle
specifier|protected
name|Rectangle
name|randomRectangle
parameter_list|()
block|{
specifier|final
name|Rectangle
name|WB
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
name|int
name|rW
init|=
operator|(
name|int
operator|)
name|randomGaussianMeanMax
argument_list|(
literal|10
argument_list|,
name|WB
operator|.
name|getWidth
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|xMin
init|=
name|randomIntBetween
argument_list|(
operator|(
name|int
operator|)
name|WB
operator|.
name|getMinX
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|WB
operator|.
name|getMaxX
argument_list|()
operator|-
name|rW
argument_list|)
decl_stmt|;
name|double
name|xMax
init|=
name|xMin
operator|+
name|rW
decl_stmt|;
name|int
name|yH
init|=
operator|(
name|int
operator|)
name|randomGaussianMeanMax
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|rW
argument_list|,
name|WB
operator|.
name|getHeight
argument_list|()
argument_list|)
argument_list|,
name|WB
operator|.
name|getHeight
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|yMin
init|=
name|randomIntBetween
argument_list|(
operator|(
name|int
operator|)
name|WB
operator|.
name|getMinY
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|WB
operator|.
name|getMaxY
argument_list|()
operator|-
name|yH
argument_list|)
decl_stmt|;
name|double
name|yMax
init|=
name|yMin
operator|+
name|yH
decl_stmt|;
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|xMin
argument_list|,
name|xMax
argument_list|,
name|yMin
argument_list|,
name|yMax
argument_list|)
return|;
block|}
DECL|method|randomGaussianMinMeanMax
specifier|private
name|double
name|randomGaussianMinMeanMax
parameter_list|(
name|double
name|min
parameter_list|,
name|double
name|mean
parameter_list|,
name|double
name|max
parameter_list|)
block|{
assert|assert
name|mean
operator|>
name|min
assert|;
return|return
name|randomGaussianMeanMax
argument_list|(
name|mean
operator|-
name|min
argument_list|,
name|max
operator|-
name|min
argument_list|)
operator|+
name|min
return|;
block|}
comment|/**    * Within one standard deviation (68% of the time) the result is "close" to    * mean. By "close": when greater than mean, it's the lesser of 2*mean or half    * way to max, when lesser than mean, it's the greater of max-2*mean or half    * way to 0. The other 32% of the time it's in the rest of the range, touching    * either 0 or max but never exceeding.    */
DECL|method|randomGaussianMeanMax
specifier|private
name|double
name|randomGaussianMeanMax
parameter_list|(
name|double
name|mean
parameter_list|,
name|double
name|max
parameter_list|)
block|{
comment|// DWS: I verified the results empirically
assert|assert
name|mean
operator|<=
name|max
operator|&&
name|mean
operator|>=
literal|0
assert|;
name|double
name|g
init|=
name|randomGaussian
argument_list|()
decl_stmt|;
name|double
name|mean2
init|=
name|mean
decl_stmt|;
name|double
name|flip
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|g
operator|<
literal|0
condition|)
block|{
name|mean2
operator|=
name|max
operator|-
name|mean
expr_stmt|;
name|flip
operator|=
operator|-
literal|1
expr_stmt|;
name|g
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
comment|// pivot is the distance from mean2 towards max where the boundary of
comment|// 1 standard deviation alters the calculation
name|double
name|pivotMax
init|=
name|max
operator|-
name|mean2
decl_stmt|;
name|double
name|pivot
init|=
name|Math
operator|.
name|min
argument_list|(
name|mean2
argument_list|,
name|pivotMax
operator|/
literal|2
argument_list|)
decl_stmt|;
comment|//from 0 to max-mean2
assert|assert
name|pivot
operator|>=
literal|0
operator|&&
name|pivotMax
operator|>=
name|pivot
operator|&&
name|g
operator|>=
literal|0
assert|;
name|double
name|pivotResult
decl_stmt|;
if|if
condition|(
name|g
operator|<=
literal|1
condition|)
name|pivotResult
operator|=
name|pivot
operator|*
name|g
expr_stmt|;
else|else
name|pivotResult
operator|=
name|Math
operator|.
name|min
argument_list|(
name|pivotMax
argument_list|,
operator|(
name|g
operator|-
literal|1
operator|)
operator|*
operator|(
name|pivotMax
operator|-
name|pivot
operator|)
operator|+
name|pivot
argument_list|)
expr_stmt|;
return|return
name|mean
operator|+
name|flip
operator|*
name|pivotResult
return|;
block|}
comment|// ================================================= Inner Classes =================================================
DECL|class|SearchResults
specifier|protected
specifier|static
class|class
name|SearchResults
block|{
DECL|field|numFound
specifier|public
name|int
name|numFound
decl_stmt|;
DECL|field|results
specifier|public
name|List
argument_list|<
name|SearchResult
argument_list|>
name|results
decl_stmt|;
DECL|method|SearchResults
specifier|public
name|SearchResults
parameter_list|(
name|int
name|numFound
parameter_list|,
name|List
argument_list|<
name|SearchResult
argument_list|>
name|results
parameter_list|)
block|{
name|this
operator|.
name|numFound
operator|=
name|numFound
expr_stmt|;
name|this
operator|.
name|results
operator|=
name|results
expr_stmt|;
block|}
DECL|method|toDebugString
specifier|public
name|StringBuilder
name|toDebugString
parameter_list|()
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|"found: "
argument_list|)
operator|.
name|append
argument_list|(
name|numFound
argument_list|)
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchResult
name|r
range|:
name|results
control|)
block|{
name|String
name|id
init|=
name|r
operator|.
name|getId
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|str
return|;
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
literal|"[found:"
operator|+
name|numFound
operator|+
literal|" "
operator|+
name|results
operator|+
literal|"]"
return|;
block|}
block|}
DECL|class|SearchResult
specifier|protected
specifier|static
class|class
name|SearchResult
block|{
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
DECL|field|document
specifier|public
name|StoredDocument
name|document
decl_stmt|;
DECL|method|SearchResult
specifier|public
name|SearchResult
parameter_list|(
name|float
name|score
parameter_list|,
name|StoredDocument
name|storedDocument
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|storedDocument
expr_stmt|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
return|;
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
literal|"["
operator|+
name|score
operator|+
literal|"="
operator|+
name|document
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class
end_unit
