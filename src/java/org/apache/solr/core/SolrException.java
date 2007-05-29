begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|CharArrayWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrException
specifier|public
class|class
name|SolrException
extends|extends
name|RuntimeException
block|{
comment|/**    * @since solr 1.2    */
DECL|enum|ErrorCode
specifier|public
enum|enum
name|ErrorCode
block|{
DECL|enum constant|BAD_REQUEST
name|BAD_REQUEST
argument_list|(
literal|400
argument_list|)
block|,
DECL|enum constant|NOT_FOUND
name|NOT_FOUND
argument_list|(
literal|404
argument_list|)
block|,
DECL|enum constant|SERVER_ERROR
name|SERVER_ERROR
argument_list|(
literal|500
argument_list|)
block|,
DECL|enum constant|SERVICE_UNAVAILABLE
name|SERVICE_UNAVAILABLE
argument_list|(
literal|503
argument_list|)
block|;
DECL|field|code
specifier|final
name|int
name|code
decl_stmt|;
DECL|method|ErrorCode
specifier|private
name|ErrorCode
parameter_list|(
name|int
name|c
parameter_list|)
block|{
name|code
operator|=
name|c
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|field|logged
specifier|public
name|boolean
name|logged
init|=
literal|false
decl_stmt|;
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
operator|.
name|code
expr_stmt|;
block|}
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|msg
parameter_list|,
name|boolean
name|alreadyLogged
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
operator|.
name|code
expr_stmt|;
name|this
operator|.
name|logged
operator|=
name|alreadyLogged
expr_stmt|;
block|}
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|,
name|boolean
name|alreadyLogged
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|th
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
operator|.
name|code
expr_stmt|;
name|logged
operator|=
name|alreadyLogged
expr_stmt|;
block|}
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{
name|this
argument_list|(
name|code
argument_list|,
name|msg
argument_list|,
name|th
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{
name|super
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
operator|.
name|code
expr_stmt|;
name|logged
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|msg
parameter_list|,
name|boolean
name|alreadyLogged
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|logged
operator|=
name|alreadyLogged
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|,
name|boolean
name|alreadyLogged
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|th
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|logged
operator|=
name|alreadyLogged
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{
name|this
argument_list|(
name|code
argument_list|,
name|msg
argument_list|,
name|th
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|SolrException
specifier|public
name|SolrException
parameter_list|(
name|int
name|code
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{
name|super
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|logged
operator|=
literal|true
expr_stmt|;
block|}
DECL|field|code
name|int
name|code
init|=
literal|0
decl_stmt|;
DECL|method|code
specifier|public
name|int
name|code
parameter_list|()
block|{
return|return
name|code
return|;
block|}
DECL|method|log
specifier|public
name|void
name|log
parameter_list|(
name|Logger
name|log
parameter_list|)
block|{
name|log
argument_list|(
name|log
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|log
specifier|public
specifier|static
name|void
name|log
parameter_list|(
name|Logger
name|log
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|severe
argument_list|(
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|logged
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|log
specifier|public
specifier|static
name|void
name|log
parameter_list|(
name|Logger
name|log
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|severe
argument_list|(
name|msg
operator|+
literal|':'
operator|+
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|logged
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|logOnce
specifier|public
specifier|static
name|void
name|logOnce
parameter_list|(
name|Logger
name|log
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|logged
condition|)
return|return;
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
name|log
argument_list|(
name|log
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
else|else
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// public String toString() { return toStr(this); }  // oops, inf loop
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toStr
specifier|public
specifier|static
name|String
name|toStr
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|CharArrayWriter
name|cw
init|=
operator|new
name|CharArrayWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|cw
argument_list|)
decl_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|cw
operator|.
name|toString
argument_list|()
return|;
comment|/** This doesn't work for some reason!!!!!     StringWriter sw = new StringWriter();     PrintWriter pw = new PrintWriter(sw);     e.printStackTrace(pw);     pw.flush();     System.out.println("The STRING:" + sw.toString());     return sw.toString(); **/
block|}
block|}
end_class
end_unit
