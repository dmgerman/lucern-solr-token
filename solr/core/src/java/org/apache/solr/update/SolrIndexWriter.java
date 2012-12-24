begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
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
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|index
operator|.
name|IndexDeletionPolicy
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
name|InfoStream
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
name|PrintStreamInfoStream
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
name|ThreadInterruptedException
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
name|core
operator|.
name|DirectoryFactory
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * An IndexWriter that is configured via Solr config mechanisms.  *  * @since solr 0.9  */
end_comment
begin_class
DECL|class|SolrIndexWriter
specifier|public
class|class
name|SolrIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrIndexWriter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// These should *only* be used for debugging or monitoring purposes
DECL|field|numOpens
specifier|public
specifier|static
specifier|final
name|AtomicLong
name|numOpens
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numCloses
specifier|public
specifier|static
specifier|final
name|AtomicLong
name|numCloses
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/** Stored into each Lucene commit to record the    *  System.currentTimeMillis() when commit was called. */
DECL|field|COMMIT_TIME_MSEC_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_TIME_MSEC_KEY
init|=
literal|"commitTimeMSec"
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|directoryFactory
specifier|private
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
DECL|method|create
specifier|public
specifier|static
name|SolrIndexWriter
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|directoryFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|,
name|IndexDeletionPolicy
name|delPolicy
parameter_list|,
name|Codec
name|codec
parameter_list|,
name|boolean
name|forceNewDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrIndexWriter
name|w
init|=
literal|null
decl_stmt|;
specifier|final
name|Directory
name|d
init|=
name|directoryFactory
operator|.
name|get
argument_list|(
name|path
argument_list|,
name|config
operator|.
name|lockType
argument_list|,
name|forceNewDirectory
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|=
operator|new
name|SolrIndexWriter
argument_list|(
name|name
argument_list|,
name|path
argument_list|,
name|d
argument_list|,
name|create
argument_list|,
name|schema
argument_list|,
name|config
argument_list|,
name|delPolicy
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|w
operator|.
name|setDirectoryFactory
argument_list|(
name|directoryFactory
argument_list|)
expr_stmt|;
return|return
name|w
return|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|==
name|w
operator|&&
literal|null
operator|!=
name|d
condition|)
block|{
name|directoryFactory
operator|.
name|doneWithDirectory
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|directoryFactory
operator|.
name|release
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|SolrIndexWriter
specifier|private
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|,
name|IndexDeletionPolicy
name|delPolicy
parameter_list|,
name|Codec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|config
operator|.
name|toIndexWriterConfig
argument_list|(
name|schema
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|create
condition|?
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
else|:
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|delPolicy
argument_list|)
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
operator|.
name|setInfoStream
argument_list|(
name|toInfoStream
argument_list|(
name|config
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Opened Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|numOpens
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|setDirectoryFactory
specifier|private
name|void
name|setDirectoryFactory
parameter_list|(
name|DirectoryFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|directoryFactory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|toInfoStream
specifier|private
specifier|static
name|InfoStream
name|toInfoStream
parameter_list|(
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|infoStreamFile
init|=
name|config
operator|.
name|infoStreamFile
decl_stmt|;
if|if
condition|(
name|infoStreamFile
operator|!=
literal|null
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|infoStreamFile
argument_list|)
decl_stmt|;
name|File
name|parent
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|parent
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrintStreamInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|fos
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|InfoStream
operator|.
name|NO_OUTPUT
return|;
block|}
block|}
comment|/**    * use DocumentBuilder now...    * private final void addField(Document doc, String name, String val) {    * SchemaField ftype = schema.getField(name);    *<p/>    * // we don't check for a null val ourselves because a solr.FieldType    * // might actually want to map it to something.  If createField()    * // returns null, then we don't store the field.    *<p/>    * Field field = ftype.createField(val, boost);    * if (field != null) doc.add(field);    * }    *<p/>    *<p/>    * public void addRecord(String[] fieldNames, String[] fieldValues) throws IOException {    * Document doc = new Document();    * for (int i=0; i<fieldNames.length; i++) {    * String name = fieldNames[i];    * String val = fieldNames[i];    *<p/>    * // first null is end of list.  client can reuse arrays if they want    * // and just write a single null if there is unused space.    * if (name==null) break;    *<p/>    * addField(doc,name,val);    * }    * addDocument(doc);    * }    * ****    */
DECL|field|isClosed
specifier|private
specifier|volatile
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|Directory
name|directory
init|=
name|getDirectory
argument_list|()
decl_stmt|;
specifier|final
name|InfoStream
name|infoStream
init|=
name|isClosed
condition|?
literal|null
else|:
name|getConfig
argument_list|()
operator|.
name|getInfoStream
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ThreadInterruptedException
name|e
parameter_list|)
block|{
comment|// don't allow interruption
continue|continue;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error closing IndexWriter, trying rollback"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|super
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writeLock
operator|!=
literal|null
operator|&&
name|writeLock
operator|.
name|isLocked
argument_list|()
condition|)
block|{
try|try
block|{
comment|// we go right to the lock because
comment|// IndexWriter#unlock(Directory) does
comment|// not work well in this case with
comment|// native locks
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Coud not unlock directory after seemingly failed IndexWriter#close()"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|isClosed
operator|=
literal|true
expr_stmt|;
name|directoryFactory
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|numCloses
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|super
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ThreadInterruptedException
name|e
parameter_list|)
block|{
comment|// don't allow interruption
continue|continue;
block|}
break|break;
block|}
block|}
finally|finally
block|{
name|isClosed
operator|=
literal|true
expr_stmt|;
name|directoryFactory
operator|.
name|release
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|numCloses
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
operator|!
name|isClosed
condition|)
block|{
assert|assert
literal|false
operator|:
literal|"SolrIndexWriter was not closed prior to finalize()"
assert|;
name|log
operator|.
name|error
argument_list|(
literal|"SolrIndexWriter was not closed prior to finalize(), indicates a bug -- POSSIBLE RESOURCE LEAK!!!"
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
