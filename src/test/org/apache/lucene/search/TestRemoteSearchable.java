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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
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
name|store
operator|.
name|RAMDirectory
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
begin_class
DECL|class|TestRemoteSearchable
specifier|public
class|class
name|TestRemoteSearchable
extends|extends
name|TestCase
block|{
DECL|method|TestRemoteSearchable
specifier|public
name|TestRemoteSearchable
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
name|Field
operator|.
name|Text
argument_list|(
literal|"test"
argument_list|,
literal|"test text"
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
parameter_list|)
throws|throws
name|Exception
block|{
comment|// try to search the published index
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
name|Hits
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test text"
argument_list|,
name|result
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermQuery
specifier|public
name|void
name|testTermQuery
parameter_list|()
throws|throws
name|Exception
block|{
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
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanQuery
specifier|public
name|void
name|testBooleanQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
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
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|testPhraseQuery
specifier|public
name|void
name|testPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|// Tests bug fix at http://nagoya.apache.org/bugzilla/show_bug.cgi?id=20290
DECL|method|testQueryFilter
specifier|public
name|void
name|testQueryFilter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// try to search the published index
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
name|Hits
name|hits
init|=
name|searcher
operator|.
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
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|QueryFilter
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
argument_list|)
argument_list|)
decl_stmt|;
name|Hits
name|nohits
init|=
name|searcher
operator|.
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
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|QueryFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"non-existent-term"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nohits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
