begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
name|SolrInputDocument
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|CommitUpdateCommand
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
name|update
operator|.
name|DeleteUpdateCommand
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
name|update
operator|.
name|RollbackUpdateCommand
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
begin_comment
comment|/**  *<p> Writes documents to SOLR as well as provides methods for loading and persisting last index time.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrWriter
specifier|public
class|class
name|SolrWriter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|IMPORTER_PROPERTIES
specifier|static
specifier|final
name|String
name|IMPORTER_PROPERTIES
init|=
literal|"dataimport.properties"
decl_stmt|;
DECL|field|LAST_INDEX_KEY
specifier|static
specifier|final
name|String
name|LAST_INDEX_KEY
init|=
literal|"last_index_time"
decl_stmt|;
DECL|field|processor
specifier|private
specifier|final
name|UpdateRequestProcessor
name|processor
decl_stmt|;
DECL|field|configDir
specifier|private
specifier|final
name|String
name|configDir
decl_stmt|;
DECL|field|persistFilename
specifier|private
name|String
name|persistFilename
init|=
name|IMPORTER_PROPERTIES
decl_stmt|;
DECL|field|debugLogger
name|DebugLogger
name|debugLogger
decl_stmt|;
DECL|method|SolrWriter
specifier|public
name|SolrWriter
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|String
name|confDir
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|configDir
operator|=
name|confDir
expr_stmt|;
block|}
DECL|method|SolrWriter
specifier|public
name|SolrWriter
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|String
name|confDir
parameter_list|,
name|String
name|filePrefix
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|configDir
operator|=
name|confDir
expr_stmt|;
if|if
condition|(
name|filePrefix
operator|!=
literal|null
condition|)
block|{
name|persistFilename
operator|=
name|filePrefix
operator|+
literal|".properties"
expr_stmt|;
block|}
block|}
DECL|method|upload
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrInputDocument
name|d
parameter_list|)
block|{
try|try
block|{
name|AddUpdateCommand
name|command
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|command
operator|.
name|solrDoc
operator|=
name|d
expr_stmt|;
name|command
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
name|command
operator|.
name|overwritePending
operator|=
literal|true
expr_stmt|;
name|command
operator|.
name|overwriteCommitted
operator|=
literal|true
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error creating document : "
operator|+
name|d
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|deleteDoc
specifier|public
name|void
name|deleteDoc
parameter_list|(
name|Object
name|id
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting document: "
operator|+
name|id
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|delCmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
name|delCmd
operator|.
name|id
operator|=
name|id
operator|.
name|toString
argument_list|()
expr_stmt|;
name|delCmd
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|delCmd
operator|.
name|fromCommitted
operator|=
literal|true
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|delCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while deleteing: "
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|persist
name|void
name|persist
parameter_list|(
name|Properties
name|p
parameter_list|)
block|{
name|OutputStream
name|propOutput
init|=
literal|null
decl_stmt|;
name|Properties
name|props
init|=
name|readIndexerProperties
argument_list|()
decl_stmt|;
try|try
block|{
name|props
operator|.
name|putAll
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|String
name|filePath
init|=
name|configDir
decl_stmt|;
if|if
condition|(
name|configDir
operator|!=
literal|null
operator|&&
operator|!
name|configDir
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
name|filePath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|filePath
operator|+=
name|persistFilename
expr_stmt|;
name|propOutput
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|props
operator|.
name|store
argument_list|(
name|propOutput
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Wrote last indexed time to "
operator|+
name|persistFilename
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to persist Index Start Time"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to persist Index Start Time"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|propOutput
operator|!=
literal|null
condition|)
name|propOutput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|propOutput
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|readIndexerProperties
name|Properties
name|readIndexerProperties
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStream
name|propInput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|propInput
operator|=
operator|new
name|FileInputStream
argument_list|(
name|configDir
operator|+
name|persistFilename
argument_list|)
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
name|propInput
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Read "
operator|+
name|persistFilename
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to read: "
operator|+
name|persistFilename
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|propInput
operator|!=
literal|null
condition|)
name|propInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|propInput
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|props
return|;
block|}
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting documents from Solr with query: "
operator|+
name|query
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|delCmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
name|delCmd
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|delCmd
operator|.
name|fromCommitted
operator|=
literal|true
expr_stmt|;
name|delCmd
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|delCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while deleting by query: "
operator|+
name|query
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|optimize
parameter_list|)
block|{
try|try
block|{
name|CommitUpdateCommand
name|commit
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|optimize
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processCommit
argument_list|(
name|commit
argument_list|)
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
literal|"Exception while solr commit."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
block|{
try|try
block|{
name|RollbackUpdateCommand
name|rollback
init|=
operator|new
name|RollbackUpdateCommand
argument_list|()
decl_stmt|;
name|processor
operator|.
name|processRollback
argument_list|(
name|rollback
argument_list|)
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
literal|"Exception while solr rollback."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doDeleteAll
specifier|public
name|void
name|doDeleteAll
parameter_list|()
block|{
try|try
block|{
name|DeleteUpdateCommand
name|deleteCommand
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
name|deleteCommand
operator|.
name|query
operator|=
literal|"*:*"
expr_stmt|;
name|deleteCommand
operator|.
name|fromCommitted
operator|=
literal|true
expr_stmt|;
name|deleteCommand
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|deleteCommand
argument_list|)
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
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Exception in full dump while deleting all documents."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getResourceAsString
specifier|static
name|String
name|getResourceAsString
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|sz
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|sz
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|sz
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|in
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
block|{        }
block|}
return|return
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|method|getDocCount
specifier|static
name|String
name|getDocCount
parameter_list|()
block|{
if|if
condition|(
name|DocBuilder
operator|.
name|INSTANCE
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|""
operator|+
operator|(
name|DocBuilder
operator|.
name|INSTANCE
operator|.
name|get
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
operator|+
literal|1
operator|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|loadIndexStartTime
specifier|public
name|Date
name|loadIndexStartTime
parameter_list|()
block|{
name|Properties
name|props
decl_stmt|;
name|props
operator|=
name|readIndexerProperties
argument_list|()
expr_stmt|;
name|String
name|result
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|SolrWriter
operator|.
name|LAST_INDEX_KEY
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
name|DataImporter
operator|.
name|DATE_TIME_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|result
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
literal|"Unable to read last indexed time from: "
operator|+
name|persistFilename
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getDebugLogger
specifier|public
name|DebugLogger
name|getDebugLogger
parameter_list|()
block|{
if|if
condition|(
name|debugLogger
operator|==
literal|null
condition|)
block|{
name|debugLogger
operator|=
operator|new
name|DebugLogger
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|debugLogger
return|;
block|}
comment|/**    * This method is used for verbose debugging    *    * @param event The event name start.entity ,end.entity ,transformer.row    * @param name  Name of the entity/transformer    * @param row   The actual data . Can be a Map<String,object> or a List<Map<String,object>>    */
DECL|method|log
specifier|public
name|void
name|log
parameter_list|(
name|int
name|event
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|row
parameter_list|)
block|{
name|getDebugLogger
argument_list|()
operator|.
name|log
argument_list|(
name|event
argument_list|,
name|name
argument_list|,
name|row
argument_list|)
expr_stmt|;
block|}
DECL|field|START_ENTITY
DECL|field|END_ENTITY
specifier|public
specifier|static
specifier|final
name|int
name|START_ENTITY
init|=
literal|1
decl_stmt|,
name|END_ENTITY
init|=
literal|2
decl_stmt|,
DECL|field|TRANSFORMED_ROW
DECL|field|ENTITY_META
DECL|field|PRE_TRANSFORMER_ROW
name|TRANSFORMED_ROW
init|=
literal|3
decl_stmt|,
name|ENTITY_META
init|=
literal|4
decl_stmt|,
name|PRE_TRANSFORMER_ROW
init|=
literal|5
decl_stmt|,
DECL|field|START_DOC
DECL|field|END_DOC
DECL|field|ENTITY_OUT
DECL|field|ROW_END
name|START_DOC
init|=
literal|6
decl_stmt|,
name|END_DOC
init|=
literal|7
decl_stmt|,
name|ENTITY_OUT
init|=
literal|8
decl_stmt|,
name|ROW_END
init|=
literal|9
decl_stmt|,
DECL|field|TRANSFORMER_EXCEPTION
DECL|field|ENTITY_EXCEPTION
DECL|field|DISABLE_LOGGING
name|TRANSFORMER_EXCEPTION
init|=
literal|10
decl_stmt|,
name|ENTITY_EXCEPTION
init|=
literal|11
decl_stmt|,
name|DISABLE_LOGGING
init|=
literal|12
decl_stmt|,
DECL|field|ENABLE_LOGGING
name|ENABLE_LOGGING
init|=
literal|13
decl_stmt|;
block|}
end_class
end_unit
