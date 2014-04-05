begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|TokenStream
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
name|core
operator|.
name|KeywordTokenizerFactory
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
name|ngram
operator|.
name|NGramFilterFactory
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
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
operator|.
name|LukeRequestHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|FacetComponent
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|JSONResponseWriter
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
name|FileFilter
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
name|InputStream
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
name|CharacterCodingException
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
import|;
end_import
begin_class
DECL|class|ResourceLoaderTest
specifier|public
class|class
name|ResourceLoaderTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testInstanceDir
specifier|public
name|void
name|testInstanceDir
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|instDir
init|=
name|loader
operator|.
name|getInstanceDir
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|instDir
operator|+
literal|" is not equal to "
operator|+
literal|"solr/"
argument_list|,
name|instDir
operator|.
name|equals
argument_list|(
literal|"solr/"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr"
argument_list|)
expr_stmt|;
name|instDir
operator|=
name|loader
operator|.
name|getInstanceDir
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|instDir
operator|+
literal|" is not equal to "
operator|+
literal|"solr/"
argument_list|,
name|instDir
operator|.
name|equals
argument_list|(
literal|"solr"
operator|+
name|File
operator|.
name|separator
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEscapeInstanceDir
specifier|public
name|void
name|testEscapeInstanceDir
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|temp
init|=
name|createTempDir
argument_list|(
literal|"testEscapeInstanceDir"
argument_list|)
decl_stmt|;
try|try
block|{
name|temp
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|temp
argument_list|,
literal|"dummy.txt"
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|File
name|instanceDir
init|=
operator|new
name|File
argument_list|(
name|temp
argument_list|,
literal|"instance"
argument_list|)
decl_stmt|;
name|instanceDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
literal|"conf"
argument_list|)
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|instanceDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|loader
operator|.
name|openResource
argument_list|(
literal|"../../dummy.txt"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"For security reasons, SolrResourceLoader"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|TestUtil
operator|.
name|rm
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAwareCompatibility
specifier|public
name|void
name|testAwareCompatibility
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|ResourceLoaderAware
operator|.
name|class
decl_stmt|;
comment|// Check ResourceLoaderAware valid objects
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|NGramFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|KeywordTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure it throws an error for invalid objects
name|Object
index|[]
name|invalid
init|=
operator|new
name|Object
index|[]
block|{
comment|// new NGramTokenFilter( null ),
literal|"hello"
block|,
operator|new
name|Float
argument_list|(
literal|12.3f
argument_list|)
block|,
operator|new
name|LukeRequestHandler
argument_list|()
block|,
operator|new
name|JSONResponseWriter
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|invalid
control|)
block|{
try|try
block|{
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
name|obj
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should be invalid class: "
operator|+
name|obj
operator|+
literal|" FOR "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// OK
block|}
name|clazz
operator|=
name|SolrCoreAware
operator|.
name|class
expr_stmt|;
comment|// Check ResourceLoaderAware valid objects
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|LukeRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|FacetComponent
argument_list|()
argument_list|)
expr_stmt|;
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|JSONResponseWriter
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure it throws an error for invalid objects
name|invalid
operator|=
operator|new
name|Object
index|[]
block|{
operator|new
name|NGramFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
block|,
literal|"hello"
block|,
operator|new
name|Float
argument_list|(
literal|12.3f
argument_list|)
block|,
operator|new
name|KeywordTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
block|}
expr_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|invalid
control|)
block|{
try|try
block|{
name|loader
operator|.
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
name|obj
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should be invalid class: "
operator|+
name|obj
operator|+
literal|" FOR "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// OK
block|}
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testBOMMarkers
specifier|public
name|void
name|testBOMMarkers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|fileWithBom
init|=
literal|"stopwithbom.txt"
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
decl_stmt|;
comment|// preliminary sanity check
name|InputStream
name|bomStream
init|=
name|loader
operator|.
name|openResource
argument_list|(
name|fileWithBom
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|bomExpected
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|17
block|,
operator|-
literal|69
block|,
operator|-
literal|65
block|}
decl_stmt|;
specifier|final
name|byte
index|[]
name|firstBytes
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have been able to read 3 bytes from bomStream"
argument_list|,
literal|3
argument_list|,
name|bomStream
operator|.
name|read
argument_list|(
name|firstBytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"This test only works if "
operator|+
name|fileWithBom
operator|+
literal|" contains a BOM -- it appears someone removed it."
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|bomExpected
argument_list|,
name|firstBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|bomStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* IGNORE */
block|}
block|}
comment|// now make sure getLines skips the BOM...
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|fileWithBom
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BOMsAreEvil"
argument_list|,
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testWrongEncoding
specifier|public
name|void
name|testWrongEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|wrongEncoding
init|=
literal|"stopwordsWrongEncoding.txt"
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
decl_stmt|;
comment|// ensure we get our exception
try|try
block|{
name|loader
operator|.
name|getLines
argument_list|(
name|wrongEncoding
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CharacterCodingException
argument_list|)
expr_stmt|;
block|}
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testClassLoaderLibs
specifier|public
name|void
name|testClassLoaderLibs
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpRoot
init|=
name|createTempDir
argument_list|(
literal|"testClassLoaderLibs"
argument_list|)
decl_stmt|;
name|File
name|lib
init|=
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"lib"
argument_list|)
decl_stmt|;
name|lib
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar1
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|lib
argument_list|,
literal|"jar1.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar1
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"aLibFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar1
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar1
operator|.
name|close
argument_list|()
expr_stmt|;
name|File
name|otherLib
init|=
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"otherLib"
argument_list|)
decl_stmt|;
name|otherLib
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar2
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|otherLib
argument_list|,
literal|"jar2.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar2
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"explicitFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar2
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar2
operator|.
name|close
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar3
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|otherLib
argument_list|,
literal|"jar3.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar3
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"otherFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar3
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar3
operator|.
name|close
argument_list|()
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
comment|// ./lib is accessible by default
name|assertNotNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"aLibFile"
argument_list|)
argument_list|)
expr_stmt|;
comment|// file filter works (and doesn't add other files in the same dir)
specifier|final
name|File
name|explicitFileJar
init|=
operator|new
name|File
argument_list|(
name|otherLib
argument_list|,
literal|"jar2.jar"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|loader
operator|.
name|addToClassLoader
argument_list|(
literal|"otherLib"
argument_list|,
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
return|return
name|pathname
operator|.
name|equals
argument_list|(
name|explicitFileJar
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"explicitFile"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"otherFile"
argument_list|)
argument_list|)
expr_stmt|;
comment|// null file filter means accept all (making otherFile accessible)
name|loader
operator|.
name|addToClassLoader
argument_list|(
literal|"otherLib"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"otherFile"
argument_list|)
argument_list|)
expr_stmt|;
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|class|DeprecatedTokenFilterFactory
specifier|public
specifier|static
specifier|final
class|class
name|DeprecatedTokenFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|method|DeprecatedTokenFilterFactory
specifier|public
name|DeprecatedTokenFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|testLoadDeprecatedFactory
specifier|public
name|void
name|testLoadDeprecatedFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
decl_stmt|;
comment|// ensure we get our exception
name|loader
operator|.
name|newInstance
argument_list|(
name|DeprecatedTokenFilterFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TokenFilterFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Map
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|}
argument_list|)
expr_stmt|;
comment|// TODO: How to check that a warning was printed to log file?
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
