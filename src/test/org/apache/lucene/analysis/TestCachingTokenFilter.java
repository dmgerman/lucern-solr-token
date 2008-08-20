begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|document
operator|.
name|Field
operator|.
name|TermVector
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
name|TermPositions
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
name|RAMDirectory
import|;
end_import
begin_class
DECL|class|TestCachingTokenFilter
specifier|public
class|class
name|TestCachingTokenFilter
extends|extends
name|LuceneTestCase
block|{
DECL|field|tokens
specifier|private
name|String
index|[]
name|tokens
init|=
operator|new
name|String
index|[]
block|{
literal|"term1"
block|,
literal|"term2"
block|,
literal|"term3"
block|,
literal|"term2"
block|}
decl_stmt|;
DECL|method|testCaching
specifier|public
name|void
name|testCaching
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
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
name|dir
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
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
name|TokenStream
name|stream
init|=
operator|new
name|TokenStream
argument_list|()
block|{
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
if|if
condition|(
name|index
operator|==
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|reusableToken
operator|.
name|reinit
argument_list|(
name|tokens
index|[
name|index
operator|++
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|stream
operator|=
operator|new
name|CachingTokenFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"preanalyzed"
argument_list|,
name|stream
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1) we consume all tokens twice before we add the doc to the index
name|checkTokens
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|checkTokens
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|// 2) now add the document to the index and verify if all tokens are indexed
comment|//    don't reset the stream here, the DocumentWriter should do that implicitly
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TermPositions
name|termPositions
init|=
name|reader
operator|.
name|termPositions
argument_list|(
operator|new
name|Term
argument_list|(
literal|"preanalyzed"
argument_list|,
literal|"term1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|termPositions
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
literal|"preanalyzed"
argument_list|,
literal|"term2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|termPositions
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
literal|"preanalyzed"
argument_list|,
literal|"term3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// 3) reset stream and consume tokens again
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|checkTokens
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
DECL|method|checkTokens
specifier|private
name|void
name|checkTokens
parameter_list|(
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|count
operator|<
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tokens
index|[
name|count
index|]
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|tokens
operator|.
name|length
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
