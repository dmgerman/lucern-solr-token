begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.path
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|path
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
name|StringReader
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
name|BaseTokenStreamTestCase
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
name|CharStream
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
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
import|;
end_import
begin_class
DECL|class|TestPathHierarchyTokenizer
specifier|public
class|class
name|TestPathHierarchyTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEndOfDelimiter
specifier|public
name|void
name|testEndOfDelimiter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c/"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/c"
block|,
literal|"/a/b/c/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfChar
specifier|public
name|void
name|testStartOfChar
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"a/b"
block|,
literal|"a/b/c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharEndOfDelimiter
specifier|public
name|void
name|testStartOfCharEndOfDelimiter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c/"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"a/b"
block|,
literal|"a/b/c"
block|,
literal|"a/b/c/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|5
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimiter
specifier|public
name|void
name|testOnlyDelimiter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimiters
specifier|public
name|void
name|testOnlyDelimiters
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"//"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|,
literal|"//"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplace
specifier|public
name|void
name|testReplace
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|'/'
argument_list|,
literal|'\\'
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\\a"
block|,
literal|"\\a\\b"
block|,
literal|"\\a\\b\\c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWindowsPath
specifier|public
name|void
name|testWindowsPath
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"c:\\a\\b\\c"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|'\\'
argument_list|,
literal|'\\'
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c:"
block|,
literal|"c:\\a"
block|,
literal|"c:\\a\\b"
block|,
literal|"c:\\a\\b\\c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNormalizeWinDelimToLinuxDelim
specifier|public
name|void
name|testNormalizeWinDelimToLinuxDelim
parameter_list|()
throws|throws
name|Exception
block|{
name|NormalizeCharMap
name|normMap
init|=
operator|new
name|NormalizeCharMap
argument_list|()
decl_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"c:\\a\\b\\c"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
name|cs
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c:"
block|,
literal|"c:/a"
block|,
literal|"c:/a/b"
block|,
literal|"c:/a/b/c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasicSkip
specifier|public
name|void
name|testBasicSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/b"
block|,
literal|"/b/c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEndOfDelimiterSkip
specifier|public
name|void
name|testEndOfDelimiterSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c/"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/b"
block|,
literal|"/b/c"
block|,
literal|"/b/c/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|6
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharSkip
specifier|public
name|void
name|testStartOfCharSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/b"
block|,
literal|"/b/c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharEndOfDelimiterSkip
specifier|public
name|void
name|testStartOfCharEndOfDelimiterSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c/"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/b"
block|,
literal|"/b/c"
block|,
literal|"/b/c/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|5
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimiterSkip
specifier|public
name|void
name|testOnlyDelimiterSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimitersSkip
specifier|public
name|void
name|testOnlyDelimitersSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"//"
decl_stmt|;
name|PathHierarchyTokenizer
name|t
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
