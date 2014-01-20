begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache
package|package
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
name|writercache
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
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
name|FacetTestCase
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
name|_TestUtil
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestCharBlockArray
specifier|public
class|class
name|TestCharBlockArray
extends|extends
name|FacetTestCase
block|{
DECL|method|testArray
annotation|@
name|Test
specifier|public
name|void
name|testArray
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBlockArray
name|array
init|=
operator|new
name|CharBlockArray
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
literal|100
operator|*
literal|1000
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|50
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|int
name|size
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
decl_stmt|;
comment|// This test is turning random bytes into a string,
comment|// this is asking for trouble.
name|CharsetDecoder
name|decoder
init|=
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|array
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|s
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|int
name|size
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
decl_stmt|;
comment|// This test is turning random bytes into a string,
comment|// this is asking for trouble.
name|CharsetDecoder
name|decoder
init|=
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|array
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|s
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|s
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|int
name|size
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
decl_stmt|;
comment|// This test is turning random bytes into a string,
comment|// this is asking for trouble.
name|CharsetDecoder
name|decoder
init|=
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|array
operator|.
name|append
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|assertEqualsInternal
argument_list|(
literal|"GrowingCharArray<->StringBuilder mismatch."
argument_list|,
name|builder
argument_list|,
name|array
argument_list|)
expr_stmt|;
name|File
name|tempDir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"growingchararray"
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"GrowingCharArrayTest.tmp"
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|out
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|array
operator|.
name|flush
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|BufferedInputStream
name|in
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|array
operator|=
name|CharBlockArray
operator|.
name|open
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertEqualsInternal
argument_list|(
literal|"GrowingCharArray<->StringBuilder mismatch after flush/load."
argument_list|,
name|builder
argument_list|,
name|array
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|assertEqualsInternal
specifier|private
specifier|static
name|void
name|assertEqualsInternal
parameter_list|(
name|String
name|msg
parameter_list|,
name|StringBuilder
name|expected
parameter_list|,
name|CharBlockArray
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|expected
operator|.
name|length
argument_list|()
argument_list|,
name|actual
operator|.
name|length
argument_list|()
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
name|expected
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|expected
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|actual
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit