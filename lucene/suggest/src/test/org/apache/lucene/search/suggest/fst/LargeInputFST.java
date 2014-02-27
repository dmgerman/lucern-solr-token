begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
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
name|BufferedReader
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|OfflineSorter
import|;
end_import
begin_comment
comment|/**  * Try to build a suggester from a large data set. The input is a simple text  * file, newline-delimited.  */
end_comment
begin_class
DECL|class|LargeInputFST
specifier|public
class|class
name|LargeInputFST
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|input
init|=
operator|new
name|File
argument_list|(
literal|"/home/dweiss/tmp/shuffled.dict"
argument_list|)
decl_stmt|;
name|int
name|buckets
init|=
literal|20
decl_stmt|;
name|int
name|shareMaxTail
init|=
literal|10
decl_stmt|;
name|ExternalRefSorter
name|sorter
init|=
operator|new
name|ExternalRefSorter
argument_list|(
operator|new
name|OfflineSorter
argument_list|()
argument_list|)
decl_stmt|;
name|FSTCompletionBuilder
name|builder
init|=
operator|new
name|FSTCompletionBuilder
argument_list|(
name|buckets
argument_list|,
name|sorter
argument_list|,
name|shareMaxTail
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|input
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|scratch
operator|.
name|copyChars
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratch
argument_list|,
name|count
operator|%
name|buckets
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|count
operator|++
operator|%
literal|100000
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Line: "
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Building FSTCompletion."
argument_list|)
expr_stmt|;
name|FSTCompletion
name|completion
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|File
name|fstFile
init|=
operator|new
name|File
argument_list|(
literal|"completion.fst"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done. Writing automaton: "
operator|+
name|fstFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|completion
operator|.
name|getFST
argument_list|()
operator|.
name|save
argument_list|(
name|fstFile
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
