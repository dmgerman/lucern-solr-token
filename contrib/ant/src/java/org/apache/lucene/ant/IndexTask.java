begin_unit
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Analyzer
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
name|StopAnalyzer
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|standard
operator|.
name|StandardAnalyzer
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
name|DateTools
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
name|search
operator|.
name|Hits
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
name|search
operator|.
name|Searcher
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
name|search
operator|.
name|TermQuery
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DirectoryScanner
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DynamicConfigurator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Task
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|FileSet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|EnumeratedAttribute
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|text
operator|.
name|ParseException
import|;
end_import
begin_comment
comment|/**  *  Ant task to index files with Lucene  *  *@author Erik Hatcher  */
end_comment
begin_class
DECL|class|IndexTask
specifier|public
class|class
name|IndexTask
extends|extends
name|Task
block|{
comment|/**    *  file list    */
DECL|field|filesets
specifier|private
name|ArrayList
name|filesets
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/**    *  overwrite index?    */
DECL|field|overwrite
specifier|private
name|boolean
name|overwrite
init|=
literal|false
decl_stmt|;
comment|/**    *  index path    */
DECL|field|indexDir
specifier|private
name|File
name|indexDir
decl_stmt|;
comment|/**    *  document handler classname    */
DECL|field|handlerClassName
specifier|private
name|String
name|handlerClassName
init|=
name|FileExtensionDocumentHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    *  document handler instance    */
DECL|field|handler
specifier|private
name|DocumentHandler
name|handler
decl_stmt|;
comment|/**    *    */
DECL|field|analyzerClassName
specifier|private
name|String
name|analyzerClassName
init|=
name|StandardAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    *  analyzer instance    */
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
comment|/**    *  Lucene merge factor    */
DECL|field|mergeFactor
specifier|private
name|int
name|mergeFactor
init|=
literal|20
decl_stmt|;
DECL|field|handlerConfig
specifier|private
name|HandlerConfig
name|handlerConfig
decl_stmt|;
DECL|field|useCompoundIndex
specifier|private
name|boolean
name|useCompoundIndex
init|=
literal|true
decl_stmt|;
comment|/**    *  Creates new instance    */
DECL|method|IndexTask
specifier|public
name|IndexTask
parameter_list|()
block|{   }
comment|/**    *  Specifies the directory where the index will be stored    */
DECL|method|setIndex
specifier|public
name|void
name|setIndex
parameter_list|(
name|File
name|indexDir
parameter_list|)
block|{
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
block|}
comment|/**    *  Sets the mergeFactor attribute of the IndexTask object    *    *@param  mergeFactor  The new mergeFactor value    */
DECL|method|setMergeFactor
specifier|public
name|void
name|setMergeFactor
parameter_list|(
name|int
name|mergeFactor
parameter_list|)
block|{
name|this
operator|.
name|mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
block|}
comment|/**    *  Sets the overwrite attribute of the IndexTask object    *    *@param  overwrite  The new overwrite value    */
DECL|method|setOverwrite
specifier|public
name|void
name|setOverwrite
parameter_list|(
name|boolean
name|overwrite
parameter_list|)
block|{
name|this
operator|.
name|overwrite
operator|=
name|overwrite
expr_stmt|;
block|}
comment|/**    * If creating a new index and this is set to true, the    * index will be created in compound format.    */
DECL|method|setUseCompoundIndex
specifier|public
name|void
name|setUseCompoundIndex
parameter_list|(
name|boolean
name|useCompoundIndex
parameter_list|)
block|{
name|this
operator|.
name|useCompoundIndex
operator|=
name|useCompoundIndex
expr_stmt|;
block|}
comment|/**    *  Sets the documentHandler attribute of the IndexTask object    *    *@param  classname  The new documentHandler value    */
DECL|method|setDocumentHandler
specifier|public
name|void
name|setDocumentHandler
parameter_list|(
name|String
name|classname
parameter_list|)
block|{
name|handlerClassName
operator|=
name|classname
expr_stmt|;
block|}
comment|/**    * Sets the analyzer based on the builtin Lucene analyzer types.    *    * @todo Enforce analyzer and analyzerClassName to be mutually exclusive    */
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|AnalyzerType
name|type
parameter_list|)
block|{
name|analyzerClassName
operator|=
name|type
operator|.
name|getClassname
argument_list|()
expr_stmt|;
block|}
DECL|method|setAnalyzerClassName
specifier|public
name|void
name|setAnalyzerClassName
parameter_list|(
name|String
name|classname
parameter_list|)
block|{
name|analyzerClassName
operator|=
name|classname
expr_stmt|;
block|}
comment|/**    *  Adds a set of files (nested fileset attribute).    *    *@param  set  FileSet to be added    */
DECL|method|addFileset
specifier|public
name|void
name|addFileset
parameter_list|(
name|FileSet
name|set
parameter_list|)
block|{
name|filesets
operator|.
name|add
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets custom properties for a configurable document handler.    */
DECL|method|addConfig
specifier|public
name|void
name|addConfig
parameter_list|(
name|HandlerConfig
name|config
parameter_list|)
throws|throws
name|BuildException
block|{
if|if
condition|(
name|handlerConfig
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Only one config element allowed"
argument_list|)
throw|;
block|}
name|handlerConfig
operator|=
name|config
expr_stmt|;
block|}
comment|/**    *  Begins the indexing    *    *@exception  BuildException  If an error occurs indexing the    *      fileset    */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
comment|// construct handler and analyzer dynamically
try|try
block|{
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|handlerClassName
argument_list|)
decl_stmt|;
name|handler
operator|=
operator|(
name|DocumentHandler
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|analyzerClassName
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|(
name|Analyzer
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|cnfe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|iae
argument_list|)
throw|;
block|}
name|log
argument_list|(
literal|"Document handler = "
operator|+
name|handler
operator|.
name|getClass
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_VERBOSE
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Analyzer = "
operator|+
name|analyzer
operator|.
name|getClass
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_VERBOSE
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|ConfigurableDocumentHandler
condition|)
block|{
operator|(
operator|(
name|ConfigurableDocumentHandler
operator|)
name|handler
operator|)
operator|.
name|configure
argument_list|(
name|handlerConfig
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|indexDocs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Index the fileset.    *    *@exception  IOException if Lucene I/O exception    *@todo refactor!!!!!    */
DECL|method|indexDocs
specifier|private
name|void
name|indexDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|boolean
name|create
init|=
name|overwrite
decl_stmt|;
comment|// If the index directory doesn't exist,
comment|// create it and force create mode
if|if
condition|(
name|indexDir
operator|.
name|mkdirs
argument_list|()
operator|&&
operator|!
name|overwrite
condition|)
block|{
name|create
operator|=
literal|true
expr_stmt|;
block|}
name|Searcher
name|searcher
init|=
literal|null
decl_stmt|;
name|boolean
name|checkLastModified
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|create
condition|)
block|{
try|try
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|checkLastModified
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
argument_list|(
literal|"IOException: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// Empty - ignore, which indicates to index all
comment|// documents
block|}
block|}
name|log
argument_list|(
literal|"checkLastModified = "
operator|+
name|checkLastModified
argument_list|,
name|Project
operator|.
name|MSG_VERBOSE
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundIndex
argument_list|)
expr_stmt|;
name|int
name|totalFiles
init|=
literal|0
decl_stmt|;
name|int
name|totalIndexed
init|=
literal|0
decl_stmt|;
name|int
name|totalIgnored
init|=
literal|0
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
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
name|filesets
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FileSet
name|fs
init|=
operator|(
name|FileSet
operator|)
name|filesets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|DirectoryScanner
name|ds
init|=
name|fs
operator|.
name|getDirectoryScanner
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|dsfiles
init|=
name|ds
operator|.
name|getIncludedFiles
argument_list|()
decl_stmt|;
name|File
name|baseDir
init|=
name|ds
operator|.
name|getBasedir
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
name|dsfiles
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|dsfiles
index|[
name|j
index|]
argument_list|)
decl_stmt|;
name|totalFiles
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"File \""
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\" does not exist or is not readable."
argument_list|)
throw|;
block|}
name|boolean
name|indexIt
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|checkLastModified
condition|)
block|{
name|Term
name|pathTerm
init|=
operator|new
name|Term
argument_list|(
literal|"path"
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
name|pathTerm
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// if document is found, compare the
comment|// indexed last modified time with the
comment|// current file
comment|// - don't index if up to date
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|indexModified
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"modified"
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexModified
operator|!=
literal|null
condition|)
block|{
name|long
name|lastModified
init|=
literal|0
decl_stmt|;
try|try
block|{
name|lastModified
operator|=
name|DateTools
operator|.
name|stringToTime
argument_list|(
name|indexModified
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// if modified time is not parsable, skip
block|}
if|if
condition|(
name|lastModified
operator|==
name|file
operator|.
name|lastModified
argument_list|()
condition|)
block|{
comment|// TODO: remove existing document
name|indexIt
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|indexIt
condition|)
block|{
try|try
block|{
name|log
argument_list|(
literal|"Indexing "
operator|+
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_VERBOSE
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|handler
operator|.
name|getDocument
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|totalIgnored
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// Add the path of the file as a field named "path".  Use a Keyword field, so
comment|// that the index stores the path, and so that the path is searchable
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"path"
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the last modified date of the file a field named "modified".  Use a
comment|// Keyword field, so that it's searchable, but so that no attempt is made
comment|// to tokenize the field into words.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"modified"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|file
operator|.
name|lastModified
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|totalIndexed
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DocumentHandlerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// for j
block|}
comment|// if (fs != null)
block|}
comment|// for i
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
comment|//try
finally|finally
block|{
comment|// always make sure everything gets closed,
comment|// no matter how we exit.
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|log
argument_list|(
name|totalIndexed
operator|+
literal|" out of "
operator|+
name|totalFiles
operator|+
literal|" indexed ("
operator|+
name|totalIgnored
operator|+
literal|" ignored) in "
operator|+
operator|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
operator|)
operator|+
literal|" milliseconds"
argument_list|)
expr_stmt|;
block|}
DECL|class|HandlerConfig
specifier|public
specifier|static
class|class
name|HandlerConfig
implements|implements
name|DynamicConfigurator
block|{
DECL|field|props
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|method|setDynamicAttribute
specifier|public
name|void
name|setDynamicAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|BuildException
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|attributeName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|createDynamicElement
specifier|public
name|Object
name|createDynamicElement
parameter_list|(
name|String
name|elementName
parameter_list|)
throws|throws
name|BuildException
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Sub elements not supported"
argument_list|)
throw|;
block|}
DECL|method|getProperties
specifier|public
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|props
return|;
block|}
block|}
DECL|class|AnalyzerType
specifier|public
specifier|static
class|class
name|AnalyzerType
extends|extends
name|EnumeratedAttribute
block|{
DECL|field|analyzerLookup
specifier|private
specifier|static
name|Map
name|analyzerLookup
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
static|static
block|{
name|analyzerLookup
operator|.
name|put
argument_list|(
literal|"simple"
argument_list|,
name|SimpleAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|analyzerLookup
operator|.
name|put
argument_list|(
literal|"standard"
argument_list|,
name|StandardAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|analyzerLookup
operator|.
name|put
argument_list|(
literal|"stop"
argument_list|,
name|StopAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|analyzerLookup
operator|.
name|put
argument_list|(
literal|"whitespace"
argument_list|,
name|WhitespaceAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see EnumeratedAttribute#getValues      */
DECL|method|getValues
specifier|public
name|String
index|[]
name|getValues
parameter_list|()
block|{
name|Set
name|keys
init|=
name|analyzerLookup
operator|.
name|keySet
argument_list|()
decl_stmt|;
return|return
operator|(
name|String
index|[]
operator|)
name|keys
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|getClassname
specifier|public
name|String
name|getClassname
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|analyzerLookup
operator|.
name|get
argument_list|(
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
