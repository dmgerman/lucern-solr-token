begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|*
import|;
end_import
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
specifier|private
name|RandomIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|indexSearcher
specifier|private
name|IndexSearcher
name|indexSearcher
decl_stmt|;
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
name|indexWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|)
expr_stmt|;
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
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
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
