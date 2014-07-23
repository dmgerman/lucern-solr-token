begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.blocktreeords
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktreeords
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
name|Collections
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
name|Codec
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
name|BasePostingsFormatTestCase
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
name|MultiFields
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
name|TermsEnum
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
name|BytesRef
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
begin_class
DECL|class|TestOrdsBlockTree
specifier|public
class|class
name|TestOrdsBlockTree
extends|extends
name|BasePostingsFormatTestCase
block|{
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
init|=
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Ords41PostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"a b c"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// Test next()
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test seekExact by term
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test seekExact by ord
name|te
operator|.
name|seekExact
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTwoBlocks
specifier|public
name|void
name|testTwoBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|"m"
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"mo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|27
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|54
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"s"
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|terms
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|te
operator|.
name|seekExact
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|ord
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
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
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|" seek to ord="
operator|+
name|ord
operator|+
literal|" of "
operator|+
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|te
operator|.
name|seekExact
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|" seek to term="
operator|+
name|terms
operator|.
name|get
argument_list|(
name|ord
argument_list|)
operator|+
literal|" ord="
operator|+
name|ord
operator|+
literal|" of "
operator|+
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|te
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ord
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|term
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testThreeBlocks
specifier|public
name|void
name|testThreeBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|"m"
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|"mo"
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TERM: "
operator|+
name|te
operator|.
name|ord
argument_list|()
operator|+
literal|" "
operator|+
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"mo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|27
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|90
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"s"
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|testEnum
argument_list|(
name|te
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEnum
specifier|private
name|void
name|testEnum
parameter_list|(
name|TermsEnum
name|te
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|terms
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
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
literal|"TEST: seek to ord="
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|te
operator|.
name|seekExact
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|ord
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|te
operator|.
name|seekExact
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ord
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFloorBlocks
specifier|public
name|void
name|testFloorBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
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
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|""
operator|+
operator|(
name|char
operator|)
name|i
decl_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
operator|+
literal|" bytes="
operator|+
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|te
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|te
operator|.
name|ord
argument_list|()
operator|+
literal|": "
operator|+
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|97
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|98
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|122
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNonRootFloorBlocks
specifier|public
name|void
name|testNonRootFloorBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|"m"
operator|+
operator|(
name|char
operator|)
name|i
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"i="
operator|+
name|i
operator|+
literal|" term="
operator|+
name|term
operator|+
literal|" bytes="
operator|+
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|te
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
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
literal|"TEST: "
operator|+
name|te
operator|.
name|ord
argument_list|()
operator|+
literal|": "
operator|+
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ord
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|ord
operator|++
expr_stmt|;
block|}
name|testEnum
argument_list|(
name|te
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSeveralNonRootBlocks
specifier|public
name|void
name|testSeveralNonRootBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|30
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|term
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|j
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
literal|"term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|30
condition|;
name|j
operator|++
control|)
block|{
name|String
name|term
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|j
argument_list|)
decl_stmt|;
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
literal|"TEST: check term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|term
argument_list|,
name|te
operator|.
name|next
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
operator|*
name|i
operator|+
name|j
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|testEnum
argument_list|(
name|te
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aa"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSeekCeilNotFound
specifier|public
name|void
name|testSeekCeilNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Get empty string in there!
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|String
name|term
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|String
name|term2
init|=
literal|"a"
operator|+
call|(
name|char
call|)
argument_list|(
literal|97
operator|+
name|i
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|term
operator|+
literal|" "
operator|+
name|term2
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x22
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|te
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit