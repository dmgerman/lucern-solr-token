begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell2
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell2
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
begin_class
DECL|class|TestStemmer
specifier|public
class|class
name|TestStemmer
extends|extends
name|LuceneTestCase
block|{
DECL|field|stemmer
specifier|private
specifier|static
name|Stemmer
name|stemmer
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|affixStream
init|=
name|TestStemmer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.aff"
argument_list|)
init|;
name|InputStream
name|dictStream
operator|=
name|TestStemmer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.dic"
argument_list|)
init|)
block|{
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
decl_stmt|;
name|stemmer
operator|=
operator|new
name|Stemmer
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|stemmer
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSimpleSuffix
specifier|public
name|void
name|testSimpleSuffix
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"lucene"
argument_list|,
literal|"lucene"
argument_list|,
literal|"lucen"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mahoute"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimplePrefix
specifier|public
name|void
name|testSimplePrefix
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"solr"
argument_list|,
literal|"olr"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursiveSuffix
specifier|public
name|void
name|testRecursiveSuffix
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"abcd"
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
block|}
comment|// all forms unmunched from dictionary
DECL|method|testAllStems
specifier|public
name|void
name|testAllStems
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"ab"
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"abc"
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"apach"
argument_list|,
literal|"apach"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"apache"
argument_list|,
literal|"apach"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"foo"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"food"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"foos"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"lucen"
argument_list|,
literal|"lucen"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"lucene"
argument_list|,
literal|"lucen"
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mahout"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mahoute"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"moo"
argument_list|,
literal|"moo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mood"
argument_list|,
literal|"moo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"olr"
argument_list|,
literal|"olr"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"solr"
argument_list|,
literal|"olr"
argument_list|)
expr_stmt|;
block|}
comment|// some bogus stuff that should not stem (empty lists)!
DECL|method|testBogusStems
specifier|public
name|void
name|testBogusStems
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"abs"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"abe"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sab"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sapach"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sapache"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"apachee"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sfoo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sfoos"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fooss"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"lucenee"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"solre"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertStemsTo
specifier|private
name|void
name|assertStemsTo
parameter_list|(
name|String
name|s
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|String
name|actual
index|[]
init|=
operator|new
name|String
index|[
name|stems
operator|.
name|size
argument_list|()
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
name|actual
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|actual
index|[
name|i
index|]
operator|=
name|stems
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getStemString
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
