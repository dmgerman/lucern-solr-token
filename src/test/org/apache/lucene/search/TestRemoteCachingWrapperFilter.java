begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|Naming
import|;
end_import
begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|registry
operator|.
name|LocateRegistry
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
name|analysis
operator|.
name|SimpleAnalyzer
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
name|RAMDirectory
import|;
end_import
begin_comment
comment|/**  * Tests that the index is cached on the searcher side of things.  * NOTE: This is copied from TestRemoteSearchable since it already had a remote index set up.  * @author Matt Ericson  */
end_comment
begin_class
DECL|class|TestRemoteCachingWrapperFilter
specifier|public
class|class
name|TestRemoteCachingWrapperFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestRemoteCachingWrapperFilter
specifier|public
name|TestRemoteCachingWrapperFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getRemote
specifier|private
specifier|static
name|Searchable
name|getRemote
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
return|return
name|lookupRemote
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|startServer
argument_list|()
expr_stmt|;
return|return
name|lookupRemote
argument_list|()
return|;
block|}
block|}
DECL|method|lookupRemote
specifier|private
specifier|static
name|Searchable
name|lookupRemote
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|Searchable
operator|)
name|Naming
operator|.
name|lookup
argument_list|(
literal|"//localhost/Searchable"
argument_list|)
return|;
block|}
DECL|method|startServer
specifier|private
specifier|static
name|void
name|startServer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// construct an index
name|RAMDirectory
name|indexStore
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
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
name|Field
argument_list|(
literal|"test"
argument_list|,
literal|"test text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"type"
argument_list|,
literal|"A"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"other"
argument_list|,
literal|"other test text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//Need a second document to search for
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"test"
argument_list|,
literal|"test text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"type"
argument_list|,
literal|"B"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"other"
argument_list|,
literal|"other test text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// publish it
name|LocateRegistry
operator|.
name|createRegistry
argument_list|(
literal|1099
argument_list|)
expr_stmt|;
name|Searchable
name|local
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
name|RemoteSearchable
name|impl
init|=
operator|new
name|RemoteSearchable
argument_list|(
name|local
argument_list|)
decl_stmt|;
name|Naming
operator|.
name|rebind
argument_list|(
literal|"//localhost/Searchable"
argument_list|,
name|impl
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|private
specifier|static
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|hitNumber
parameter_list|,
name|String
name|typeValue
parameter_list|)
throws|throws
name|Exception
block|{
name|Searchable
index|[]
name|searchables
init|=
block|{
name|getRemote
argument_list|()
block|}
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchables
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|result
index|[
name|hitNumber
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|typeValue
argument_list|,
name|document
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|3
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermRemoteFilter
specifier|public
name|void
name|testTermRemoteFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|CachingWrapperFilterHelper
name|cwfh
init|=
operator|new
name|CachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// This is what we are fixing - if one uses a CachingWrapperFilter(Helper) it will never
comment|// cache the filter on the remote site
name|cwfh
operator|.
name|setShouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|cwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|cwfh
operator|.
name|setShouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|cwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// This is how we fix caching - we wrap a Filter in the RemoteCachingWrapperFilter(Handler - for testing)
comment|// to cache the Filter on the searcher (remote) side
name|RemoteCachingWrapperFilterHelper
name|rcwfh
init|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
name|cwfh
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// 2nd time we do the search, we should be using the cached Filter
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// assert that we get the same cached Filter, even if we create a new instance of RemoteCachingWrapperFilter(Helper)
comment|// this should pass because the Filter parameters are the same, and the cache uses Filter's hashCode() as cache keys,
comment|// and Filters' hashCode() builds on Filter parameters, not the Filter instance itself
name|rcwfh
operator|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|rcwfh
operator|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// assert that we get a non-cached version of the Filter because this is a new Query (type:b)
name|rcwfh
operator|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
