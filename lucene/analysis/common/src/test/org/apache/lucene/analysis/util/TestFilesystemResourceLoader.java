begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|File
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
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|util
operator|.
name|TestUtil
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
DECL|class|TestFilesystemResourceLoader
specifier|public
class|class
name|TestFilesystemResourceLoader
extends|extends
name|LuceneTestCase
block|{
DECL|method|assertNotFound
specifier|private
name|void
name|assertNotFound
parameter_list|(
name|ResourceLoader
name|rl
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
literal|"/this-directory-really-really-really-should-not-exist/foo/bar.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The resource does not exist, should fail!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// pass
block|}
try|try
block|{
name|rl
operator|.
name|newInstance
argument_list|(
literal|"org.apache.lucene.analysis.FooBarFilterFactory"
argument_list|,
name|TokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The class does not exist, should fail!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|iae
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|assertClasspathDelegation
specifier|private
name|void
name|assertClasspathDelegation
parameter_list|(
name|ResourceLoader
name|rl
parameter_list|)
throws|throws
name|Exception
block|{
comment|// try a stopwords file from classpath
name|CharArraySet
name|set
init|=
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
literal|"org/apache/lucene/analysis/snowball/english_stop.txt"
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"you"
argument_list|)
argument_list|)
expr_stmt|;
comment|// try to load a class; we use string comparison because classloader may be different...
name|assertEquals
argument_list|(
literal|"org.apache.lucene.analysis.util.RollingCharBuffer"
argument_list|,
name|rl
operator|.
name|newInstance
argument_list|(
literal|"org.apache.lucene.analysis.util.RollingCharBuffer"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// theoretically classes should also be loadable:
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
literal|"java/lang/String.class"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBaseDir
specifier|public
name|void
name|testBaseDir
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|base
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"fsResourceLoaderBase"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
try|try
block|{
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Writer
name|os
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"template.txt"
argument_list|)
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
name|os
operator|.
name|write
argument_list|(
literal|"foobar\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
name|ResourceLoader
name|rl
init|=
operator|new
name|FilesystemResourceLoader
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|WordlistLoader
operator|.
name|getLines
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
literal|"template.txt"
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Same with full path name:
name|String
name|fullPath
init|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"template.txt"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|WordlistLoader
operator|.
name|getLines
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
name|fullPath
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertClasspathDelegation
argument_list|(
name|rl
argument_list|)
expr_stmt|;
name|assertNotFound
argument_list|(
name|rl
argument_list|)
expr_stmt|;
comment|// now use RL without base dir:
name|rl
operator|=
operator|new
name|FilesystemResourceLoader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|WordlistLoader
operator|.
name|getLines
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"template.txt"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertClasspathDelegation
argument_list|(
name|rl
argument_list|)
expr_stmt|;
name|assertNotFound
argument_list|(
name|rl
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestUtil
operator|.
name|rmDir
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDelegation
specifier|public
name|void
name|testDelegation
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceLoader
name|rl
init|=
operator|new
name|FilesystemResourceLoader
argument_list|(
literal|null
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"foobar\n"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|WordlistLoader
operator|.
name|getLines
argument_list|(
name|rl
operator|.
name|openResource
argument_list|(
literal|"template.txt"
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
