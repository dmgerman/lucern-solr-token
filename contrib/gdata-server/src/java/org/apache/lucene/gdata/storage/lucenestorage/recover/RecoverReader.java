begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage.recover
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|recover
package|;
end_package
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
name|IOException
import|;
end_import
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
name|StringTokenizer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|server
operator|.
name|GDataEntityBuilder
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedService
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
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageEntryWrapper
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
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageEntryWrapper
operator|.
name|StorageOperation
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|ParseException
import|;
end_import
begin_comment
comment|/**  * Recovers the written object from the harddisc  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|RecoverReader
specifier|public
class|class
name|RecoverReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RecoverReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|strategy
specifier|private
name|RecoverStrategy
name|strategy
decl_stmt|;
DECL|method|RecoverReader
specifier|protected
name|RecoverReader
parameter_list|()
block|{
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecoverStrategy
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param reader      * @return      * @throws IOException      */
DECL|method|recoverEntries
specifier|public
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|recoverEntries
parameter_list|(
specifier|final
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|actionList
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageEntryWrapper
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecoverStrategy
argument_list|()
expr_stmt|;
name|String
name|input
init|=
literal|null
decl_stmt|;
name|String
name|metaData
init|=
literal|null
decl_stmt|;
name|String
name|entryData
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|input
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
if|if
condition|(
name|metaData
operator|==
literal|null
condition|)
block|{
name|metaData
operator|=
name|input
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|input
operator|.
name|equals
argument_list|(
name|RecoverWriter
operator|.
name|STORAGE_OPERATION_SEPARATOR
argument_list|)
condition|)
block|{
try|try
block|{
name|actionList
operator|.
name|add
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|recover
argument_list|(
name|metaData
argument_list|,
name|entryData
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RecoverException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Skipping recover entry for metadata: "
operator|+
name|metaData
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecoverStrategy
argument_list|()
expr_stmt|;
name|metaData
operator|=
literal|null
expr_stmt|;
name|entryData
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|entryData
operator|==
literal|null
condition|)
block|{
name|entryData
operator|=
name|input
expr_stmt|;
block|}
block|}
return|return
name|actionList
return|;
block|}
DECL|class|RecoverStrategy
specifier|private
specifier|static
class|class
name|RecoverStrategy
block|{
DECL|field|operation
specifier|private
name|StorageOperation
name|operation
decl_stmt|;
DECL|field|config
specifier|private
name|ProvidedService
name|config
decl_stmt|;
DECL|field|feedId
specifier|private
name|String
name|feedId
decl_stmt|;
DECL|field|entryId
specifier|private
name|String
name|entryId
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
comment|/**          * @param metaData          * @param entry          * @return          * @throws RecoverException          */
DECL|method|recover
specifier|public
name|StorageEntryWrapper
name|recover
parameter_list|(
name|String
name|metaData
parameter_list|,
name|String
name|entry
parameter_list|)
throws|throws
name|RecoverException
block|{
name|fillMetaData
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|ServerBaseEntry
name|retVal
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
operator|&&
name|this
operator|.
name|operation
operator|==
name|StorageOperation
operator|.
name|DELETE
condition|)
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can not recover -- Delete operation has entry part"
argument_list|)
throw|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
try|try
block|{
name|retVal
operator|=
operator|new
name|ServerBaseEntry
argument_list|(
name|buildEntry
argument_list|(
name|entry
argument_list|,
name|this
operator|.
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Exception occured while building entry -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
name|retVal
operator|=
operator|new
name|ServerBaseEntry
argument_list|()
expr_stmt|;
name|retVal
operator|.
name|setId
argument_list|(
name|this
operator|.
name|entryId
argument_list|)
expr_stmt|;
name|retVal
operator|.
name|setFeedId
argument_list|(
name|this
operator|.
name|feedId
argument_list|)
expr_stmt|;
name|retVal
operator|.
name|setServiceConfig
argument_list|(
name|this
operator|.
name|config
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|StorageEntryWrapper
argument_list|(
name|retVal
argument_list|,
name|this
operator|.
name|operation
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can't create StorageWrapper -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|fillMetaData
specifier|private
name|void
name|fillMetaData
parameter_list|(
name|String
name|recoverString
parameter_list|)
throws|throws
name|RecoverException
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|recoverString
argument_list|,
name|RecoverWriter
operator|.
name|META_DATA_SEPARATOR
argument_list|)
decl_stmt|;
name|String
name|temp
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"D"
argument_list|)
condition|)
name|this
operator|.
name|operation
operator|=
name|StorageOperation
operator|.
name|DELETE
expr_stmt|;
elseif|else
if|if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"U"
argument_list|)
condition|)
name|this
operator|.
name|operation
operator|=
name|StorageOperation
operator|.
name|UPDATE
expr_stmt|;
elseif|else
if|if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"I"
argument_list|)
condition|)
name|this
operator|.
name|operation
operator|=
name|StorageOperation
operator|.
name|INSERT
expr_stmt|;
else|else
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Illegal metadata --- "
operator|+
name|recoverString
argument_list|)
throw|;
name|temp
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|temp
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can't recover feed Id -- is null"
argument_list|)
throw|;
name|this
operator|.
name|feedId
operator|=
name|temp
expr_stmt|;
name|temp
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|temp
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can't recover entry Id -- is null"
argument_list|)
throw|;
name|this
operator|.
name|entryId
operator|=
name|temp
expr_stmt|;
name|temp
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|timestamp
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can't recover timestamp -- is null"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|operation
operator|!=
name|StorageOperation
operator|.
name|DELETE
condition|)
block|{
name|temp
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|temp
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can't recover service -- is null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|isServiceRegistered
argument_list|(
name|temp
argument_list|)
condition|)
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Service in recover metadata is not registered  - "
operator|+
name|temp
argument_list|)
throw|;
name|this
operator|.
name|config
operator|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|getProvidedService
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildEntry
specifier|private
name|BaseEntry
name|buildEntry
parameter_list|(
name|String
name|entry
parameter_list|,
name|ProvidedService
name|serviceConfig
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|entry
argument_list|)
decl_stmt|;
return|return
name|GDataEntityBuilder
operator|.
name|buildEntry
argument_list|(
name|reader
argument_list|,
name|serviceConfig
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
