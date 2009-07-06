begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|InputStreamReader
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
name|util
operator|.
name|ArrayList
import|;
end_import
begin_class
DECL|class|TestRussianStem
specifier|public
class|class
name|TestRussianStem
extends|extends
name|TestCase
block|{
DECL|field|words
specifier|private
name|ArrayList
name|words
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|stems
specifier|private
name|ArrayList
name|stems
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|method|TestRussianStem
specifier|public
name|TestRussianStem
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
comment|/**      * @see TestCase#setUp()      */
DECL|method|setUp
specifier|protected
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
comment|//System.out.println(new java.util.Date());
name|String
name|str
decl_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
decl_stmt|;
comment|// open and read words into an array list
name|BufferedReader
name|inWords
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
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/wordsUTF8.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|str
operator|=
name|inWords
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|words
operator|.
name|add
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
name|inWords
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// open and read stems into an array list
name|BufferedReader
name|inStems
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
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/stemsUTF8.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|str
operator|=
name|inStems
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
name|inStems
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see TestCase#tearDown()      */
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testStem
specifier|public
name|void
name|testStem
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|words
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//if ( (i % 100) == 0 ) System.err.println(i);
name|String
name|realStem
init|=
name|RussianStemmer
operator|.
name|stem
argument_list|(
operator|(
name|String
operator|)
name|words
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"unicode"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|realStem
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
