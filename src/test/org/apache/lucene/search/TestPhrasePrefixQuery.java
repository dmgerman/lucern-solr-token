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
name|TermEnum
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
name|LinkedList
import|;
end_import
begin_comment
comment|/**  * This class tests PhrasePrefixQuery class.  *  * @author Otis Gospodnetic  * @version $Id$  */
end_comment
begin_class
DECL|class|TestPhrasePrefixQuery
specifier|public
class|class
name|TestPhrasePrefixQuery
extends|extends
name|TestCase
block|{
DECL|method|TestPhrasePrefixQuery
specifier|public
name|TestPhrasePrefixQuery
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
comment|/**      *      */
DECL|method|testPhrasePrefix
specifier|public
name|void
name|testPhrasePrefix
parameter_list|()
throws|throws
name|IOException
block|{
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
name|doc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry pie"
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry pizza"
argument_list|)
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry chewing gum"
argument_list|)
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"body"
argument_list|,
literal|"picadelly circus"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc4
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
name|PhrasePrefixQuery
name|query1
init|=
operator|new
name|PhrasePrefixQuery
argument_list|()
decl_stmt|;
name|PhrasePrefixQuery
name|query2
init|=
operator|new
name|PhrasePrefixQuery
argument_list|()
decl_stmt|;
name|query1
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry"
argument_list|)
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"strawberry"
argument_list|)
argument_list|)
expr_stmt|;
name|LinkedList
name|termsWithPrefix
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
comment|// this TermEnum gives "picadelly", "pie" and "pizza".
name|TermEnum
name|te
init|=
name|ir
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"pi*"
argument_list|)
argument_list|)
decl_stmt|;
do|do
block|{
name|termsWithPrefix
operator|.
name|add
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
condition|)
do|;
name|query1
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|result
decl_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
