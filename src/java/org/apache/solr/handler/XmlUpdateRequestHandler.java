begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
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
name|Reader
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
name|logging
operator|.
name|Logger
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
name|SolrCore
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
name|request
operator|.
name|ContentStream
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryResponse
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
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
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
name|servlet
operator|.
name|SolrServlet
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
name|DocumentBuilder
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
name|UpdateHandler
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
name|NamedList
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
name|StrUtils
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
name|XML
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xmlpull
operator|.
name|v1
operator|.
name|XmlPullParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xmlpull
operator|.
name|v1
operator|.
name|XmlPullParserException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xmlpull
operator|.
name|v1
operator|.
name|XmlPullParserFactory
import|;
end_import
begin_class
DECL|class|XmlUpdateRequestHandler
specifier|public
class|class
name|XmlUpdateRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|factory
specifier|private
name|XmlPullParserFactory
name|factory
decl_stmt|;
comment|// This must be called AFTER solrCore has initalized!
comment|// otherwise you get a big bad error loop
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|=
name|XmlPullParserFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlPullParserException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// TODO - this should be a general utility in another class
DECL|method|getCharsetFromContentType
specifier|public
specifier|static
name|String
name|getCharsetFromContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|int
name|idx
init|=
name|contentType
operator|.
name|toLowerCase
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"charset="
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
return|return
name|contentType
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|"charset="
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"missing content stream"
argument_list|)
throw|;
block|}
comment|// Cycle through each stream
for|for
control|(
name|ContentStream
name|stream
range|:
name|req
operator|.
name|getContentStreams
argument_list|()
control|)
block|{
name|String
name|charset
init|=
name|getCharsetFromContentType
argument_list|(
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|charset
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"update"
argument_list|,
name|this
operator|.
name|update
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure its closed
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
block|}
DECL|method|update
specifier|public
name|NamedList
name|update
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updateHandler
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
comment|// TODO: What results should be returned?
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|XmlPullParser
name|xpp
init|=
name|factory
operator|.
name|newPullParser
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|xpp
operator|.
name|setInput
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|xpp
operator|.
name|nextTag
argument_list|()
expr_stmt|;
name|String
name|currTag
init|=
name|xpp
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"add"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"SolrCore.update(add)"
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
comment|// the default
name|int
name|status
init|=
literal|0
decl_stmt|;
name|boolean
name|pendingAttr
init|=
literal|false
decl_stmt|,
name|committedAttr
init|=
literal|false
decl_stmt|;
name|int
name|attrcount
init|=
name|xpp
operator|.
name|getAttributeCount
argument_list|()
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
name|attrcount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|xpp
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|xpp
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"allowDups"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|allowDups
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"overwritePending"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|overwritePending
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|pendingAttr
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"overwriteCommitted"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|overwriteCommitted
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|committedAttr
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown attribute id in add:"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|//set defaults for committed and pending based on allowDups value
if|if
condition|(
operator|!
name|pendingAttr
condition|)
name|cmd
operator|.
name|overwritePending
operator|=
operator|!
name|cmd
operator|.
name|allowDups
expr_stmt|;
if|if
condition|(
operator|!
name|committedAttr
condition|)
name|cmd
operator|.
name|overwriteCommitted
operator|=
operator|!
name|cmd
operator|.
name|allowDups
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
name|SchemaField
name|uniqueKeyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|int
name|eventType
init|=
literal|0
decl_stmt|;
comment|// accumulate responses
name|List
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// this may be our second time through the loop in the case
comment|// that there are multiple docs in the add... so make sure that
comment|// objects can handle that.
name|cmd
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
comment|// reset the id for this add
if|if
condition|(
name|eventType
operator|!=
literal|0
condition|)
block|{
name|eventType
operator|=
name|xpp
operator|.
name|getEventType
argument_list|()
expr_stmt|;
if|if
condition|(
name|eventType
operator|==
name|XmlPullParser
operator|.
name|END_DOCUMENT
condition|)
break|break;
block|}
comment|// eventType = xpp.next();
name|eventType
operator|=
name|xpp
operator|.
name|nextTag
argument_list|()
expr_stmt|;
if|if
condition|(
name|eventType
operator|==
name|XmlPullParser
operator|.
name|END_TAG
operator|||
name|eventType
operator|==
name|XmlPullParser
operator|.
name|END_DOCUMENT
condition|)
break|break;
comment|// should match</add>
name|readDoc
argument_list|(
name|builder
argument_list|,
name|xpp
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endDoc
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|=
name|builder
operator|.
name|getDoc
argument_list|()
expr_stmt|;
name|log
operator|.
name|finest
argument_list|(
literal|"adding doc..."
argument_list|)
expr_stmt|;
name|updateHandler
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|String
name|docId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|uniqueKeyField
operator|!=
literal|null
condition|)
name|docId
operator|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
name|added
operator|.
name|add
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
comment|// end while
comment|// write log and result
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|docId
range|:
name|added
control|)
if|if
condition|(
name|docId
operator|!=
literal|null
condition|)
name|out
operator|.
name|append
argument_list|(
name|docId
operator|+
literal|","
argument_list|)
expr_stmt|;
name|String
name|outMsg
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|outMsg
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|outMsg
operator|=
name|outMsg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|outMsg
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"added id={"
operator|+
name|outMsg
operator|+
literal|"} in "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
comment|// Add output
name|res
operator|.
name|add
argument_list|(
literal|"added"
argument_list|,
name|outMsg
argument_list|)
expr_stmt|;
block|}
comment|// end add
elseif|else
if|if
condition|(
literal|"commit"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
operator|||
literal|"optimize"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"parsing "
operator|+
name|currTag
argument_list|)
expr_stmt|;
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|"optimize"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|sawWaitSearcher
init|=
literal|false
decl_stmt|,
name|sawWaitFlush
init|=
literal|false
decl_stmt|;
name|int
name|attrcount
init|=
name|xpp
operator|.
name|getAttributeCount
argument_list|()
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
name|attrcount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|xpp
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|xpp
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"waitFlush"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitFlush
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|sawWaitFlush
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"waitSearcher"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|sawWaitSearcher
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected attribute commit/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If waitFlush is specified and waitSearcher wasn't, then
comment|// clear waitSearcher.
if|if
condition|(
name|sawWaitFlush
operator|&&
operator|!
name|sawWaitSearcher
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
literal|false
expr_stmt|;
block|}
name|updateHandler
operator|.
name|commit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"optimize"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"optimize 0 "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"commit 0 "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|eventType
init|=
name|xpp
operator|.
name|nextTag
argument_list|()
decl_stmt|;
if|if
condition|(
name|eventType
operator|==
name|XmlPullParser
operator|.
name|END_TAG
condition|)
break|break;
comment|// match</commit>
block|}
comment|// add debug output
name|res
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|optimize
condition|?
literal|"optimize"
else|:
literal|"commit"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// end commit
elseif|else
if|if
condition|(
literal|"delete"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"parsing delete"
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|fromCommitted
operator|=
literal|true
expr_stmt|;
name|int
name|attrcount
init|=
name|xpp
operator|.
name|getAttributeCount
argument_list|()
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
name|attrcount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|xpp
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|xpp
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"fromPending"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|fromPending
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"fromCommitted"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|fromCommitted
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected attribute delete/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|eventType
init|=
name|xpp
operator|.
name|nextTag
argument_list|()
decl_stmt|;
name|currTag
operator|=
name|xpp
operator|.
name|getName
argument_list|()
expr_stmt|;
name|String
name|val
init|=
name|xpp
operator|.
name|nextText
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|id
operator|=
name|val
expr_stmt|;
name|updateHandler
operator|.
name|delete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"delete(id "
operator|+
name|val
operator|+
literal|") 0 "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|query
operator|=
name|val
expr_stmt|;
name|updateHandler
operator|.
name|deleteByQuery
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"deleteByQuery(query "
operator|+
name|val
operator|+
literal|") 0 "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected XML tag /delete/"
operator|+
name|currTag
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unexpected XML tag /delete/"
operator|+
name|currTag
argument_list|)
throw|;
block|}
name|res
operator|.
name|add
argument_list|(
literal|"delete"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
while|while
condition|(
name|xpp
operator|.
name|nextTag
argument_list|()
operator|!=
name|XmlPullParser
operator|.
name|END_TAG
condition|)
empty_stmt|;
block|}
comment|// end delete
return|return
name|res
return|;
block|}
DECL|method|readDoc
specifier|private
name|void
name|readDoc
parameter_list|(
name|DocumentBuilder
name|builder
parameter_list|,
name|XmlPullParser
name|xpp
parameter_list|)
throws|throws
name|IOException
throws|,
name|XmlPullParserException
block|{
comment|// xpp should be at<doc> at this point
name|builder
operator|.
name|startDoc
argument_list|()
expr_stmt|;
name|int
name|attrcount
init|=
name|xpp
operator|.
name|getAttributeCount
argument_list|()
decl_stmt|;
name|float
name|docBoost
init|=
literal|1.0f
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
name|attrcount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|xpp
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|xpp
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|docBoost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setBoost
argument_list|(
name|docBoost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown attribute doc/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docBoost
operator|!=
literal|1.0f
condition|)
name|builder
operator|.
name|setBoost
argument_list|(
name|docBoost
argument_list|)
expr_stmt|;
comment|// while (findNextTag(xpp,"field") != XmlPullParser.END_DOCUMENT) {
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|eventType
init|=
name|xpp
operator|.
name|nextTag
argument_list|()
decl_stmt|;
if|if
condition|(
name|eventType
operator|==
name|XmlPullParser
operator|.
name|END_TAG
condition|)
break|break;
comment|//</doc>
name|String
name|tname
init|=
name|xpp
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// System.out.println("FIELD READER AT TAG " + tname);
if|if
condition|(
operator|!
literal|"field"
operator|.
name|equals
argument_list|(
name|tname
argument_list|)
condition|)
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected XML tag doc/"
operator|+
name|tname
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unexpected XML tag doc/"
operator|+
name|tname
argument_list|)
throw|;
block|}
comment|//
comment|// get field name and parse field attributes
comment|//
name|attrcount
operator|=
name|xpp
operator|.
name|getAttributeCount
argument_list|()
expr_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|boolean
name|isNull
init|=
literal|false
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
name|attrcount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|xpp
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|xpp
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|name
operator|=
name|attrVal
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|isNull
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown attribute doc/field/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now get the field value
name|String
name|val
init|=
name|xpp
operator|.
name|nextText
argument_list|()
decl_stmt|;
comment|// todo... text event for<field></field>???
comment|// need this line for isNull???
comment|// Don't add fields marked as null (for now at least)
if|if
condition|(
operator|!
name|isNull
condition|)
block|{
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
block|{
name|builder
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|// do I have to do a nextTag here to read the end_tag?
block|}
comment|// end field loop
block|}
comment|/**    * A Convinince method for getting back a simple XML string indicating    * successs of failure from an XML formated Update (from the Reader)    */
DECL|method|doLegacyUpdate
specifier|public
name|void
name|doLegacyUpdate
parameter_list|(
name|Reader
name|input
parameter_list|,
name|Writer
name|output
parameter_list|)
block|{
try|try
block|{
name|NamedList
name|ignored
init|=
name|this
operator|.
name|update
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|output
operator|.
name|write
argument_list|(
literal|"<result status=\"0\"></result>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|output
argument_list|,
literal|"result"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|ex
argument_list|)
argument_list|,
literal|"status"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ee
parameter_list|)
block|{
name|log
operator|.
name|severe
argument_list|(
literal|"Error writing to output stream: "
operator|+
name|ee
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add documents with XML"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision:$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id:$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL:$"
return|;
block|}
block|}
end_class
end_unit
