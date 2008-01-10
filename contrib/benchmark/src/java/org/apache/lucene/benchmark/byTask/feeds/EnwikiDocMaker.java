begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|XMLReaderFactory
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
name|FileInputStream
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
begin_comment
comment|/**  * A LineDocMaker which reads the uncompressed english wikipedia dump.  */
end_comment
begin_class
DECL|class|EnwikiDocMaker
specifier|public
class|class
name|EnwikiDocMaker
extends|extends
name|LineDocMaker
block|{
DECL|field|TITLE
specifier|static
specifier|final
name|int
name|TITLE
init|=
literal|0
decl_stmt|;
DECL|field|DATE
specifier|static
specifier|final
name|int
name|DATE
init|=
name|TITLE
operator|+
literal|1
decl_stmt|;
DECL|field|BODY
specifier|static
specifier|final
name|int
name|BODY
init|=
name|DATE
operator|+
literal|1
decl_stmt|;
DECL|field|ID
specifier|static
specifier|final
name|int
name|ID
init|=
name|BODY
operator|+
literal|1
decl_stmt|;
DECL|field|LENGTH
specifier|static
specifier|final
name|int
name|LENGTH
init|=
name|ID
operator|+
literal|1
decl_stmt|;
DECL|field|months
specifier|static
specifier|final
name|String
index|[]
name|months
init|=
block|{
literal|"JAN"
block|,
literal|"FEB"
block|,
literal|"MAR"
block|,
literal|"APR"
block|,
literal|"MAY"
block|,
literal|"JUN"
block|,
literal|"JUL"
block|,
literal|"AUG"
block|,
literal|"SEP"
block|,
literal|"OCT"
block|,
literal|"NOV"
block|,
literal|"DEC"
block|}
decl_stmt|;
DECL|class|Parser
class|class
name|Parser
extends|extends
name|DefaultHandler
implements|implements
name|Runnable
block|{
DECL|field|t
name|Thread
name|t
decl_stmt|;
DECL|field|threadDone
name|boolean
name|threadDone
decl_stmt|;
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|XMLReader
name|reader
init|=
name|XMLReaderFactory
operator|.
name|createXMLReader
argument_list|(
literal|"org.apache.xerces.parsers.SAXParser"
argument_list|)
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setErrorHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|FileInputStream
name|localFileIS
init|=
name|fileIS
decl_stmt|;
try|try
block|{
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|localFileIS
argument_list|)
decl_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
synchronized|synchronized
init|(
name|EnwikiDocMaker
operator|.
name|this
init|)
block|{
if|if
condition|(
name|localFileIS
operator|!=
name|fileIS
condition|)
block|{
comment|// fileIS was closed on us, so, just fall
comment|// through
block|}
else|else
comment|// Exception is real
throw|throw
name|ioe
throw|;
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|forever
condition|)
block|{
name|nmde
operator|=
operator|new
name|NoMoreDataException
argument_list|()
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|localFileIS
operator|==
name|fileIS
condition|)
block|{
comment|// If file is not already re-opened then
comment|// re-open it now
name|openFile
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|sae
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|sae
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|threadDone
operator|=
literal|true
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|tuple
name|String
index|[]
name|tuple
decl_stmt|;
DECL|field|nmde
name|NoMoreDataException
name|nmde
decl_stmt|;
DECL|method|next
name|String
index|[]
name|next
parameter_list|()
throws|throws
name|NoMoreDataException
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|threadDone
operator|=
literal|false
expr_stmt|;
name|t
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|result
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
name|tuple
operator|==
literal|null
operator|&&
name|nmde
operator|==
literal|null
operator|&&
operator|!
name|threadDone
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{           }
block|}
if|if
condition|(
name|nmde
operator|!=
literal|null
condition|)
block|{
comment|// Set to null so we will re-start thread in case
comment|// we are re-used:
name|t
operator|=
literal|null
expr_stmt|;
throw|throw
name|nmde
throw|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|threadDone
condition|)
comment|// The thread has exited yet did not hit end of
comment|// data, so this means it hit an exception.  We
comment|// throw NoMorDataException here to force
comment|// benchmark to stop the current alg:
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
name|result
operator|=
name|tuple
expr_stmt|;
name|tuple
operator|=
literal|null
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|field|contents
name|StringBuffer
name|contents
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
DECL|method|characters
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|contents
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|field|title
name|String
name|title
decl_stmt|;
DECL|field|body
name|String
name|body
decl_stmt|;
DECL|field|time
name|String
name|time
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|simple
parameter_list|,
name|String
name|qualified
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"page"
argument_list|)
condition|)
block|{
name|title
operator|=
literal|null
expr_stmt|;
name|body
operator|=
literal|null
expr_stmt|;
name|time
operator|=
literal|null
expr_stmt|;
name|id
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"text"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"timestamp"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"title"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|time
name|String
name|time
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|8
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|months
index|[
name|Integer
operator|.
name|valueOf
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|11
argument_list|,
literal|19
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|".000"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|time
parameter_list|,
name|String
name|body
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|String
index|[]
name|t
init|=
operator|new
name|String
index|[
name|LENGTH
index|]
decl_stmt|;
name|t
index|[
name|TITLE
index|]
operator|=
name|title
operator|.
name|replace
argument_list|(
literal|'\t'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|t
index|[
name|DATE
index|]
operator|=
name|time
operator|.
name|replace
argument_list|(
literal|'\t'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|t
index|[
name|BODY
index|]
operator|=
name|body
operator|.
name|replaceAll
argument_list|(
literal|"[\t\n]"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|t
index|[
name|ID
index|]
operator|=
name|id
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
name|tuple
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{           }
block|}
name|tuple
operator|=
name|t
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|simple
parameter_list|,
name|String
name|qualified
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"title"
argument_list|)
condition|)
block|{
name|title
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"text"
argument_list|)
condition|)
block|{
name|body
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|body
operator|.
name|startsWith
argument_list|(
literal|"#REDIRECT"
argument_list|)
operator|||
name|body
operator|.
name|startsWith
argument_list|(
literal|"#redirect"
argument_list|)
condition|)
block|{
name|body
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"timestamp"
argument_list|)
condition|)
block|{
name|time
operator|=
name|time
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
operator|&&
name|id
operator|==
literal|null
condition|)
block|{
comment|//just get the first id
name|id
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"page"
argument_list|)
condition|)
block|{
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|create
argument_list|(
name|title
argument_list|,
name|time
argument_list|,
name|body
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|field|parser
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|()
decl_stmt|;
DECL|class|DocState
class|class
name|DocState
extends|extends
name|LineDocMaker
operator|.
name|DocState
block|{
DECL|method|setFields
specifier|public
name|Document
name|setFields
parameter_list|(
name|String
index|[]
name|tuple
parameter_list|)
block|{
name|titleField
operator|.
name|setValue
argument_list|(
name|tuple
index|[
name|TITLE
index|]
argument_list|)
expr_stmt|;
name|dateField
operator|.
name|setValue
argument_list|(
name|tuple
index|[
name|DATE
index|]
argument_list|)
expr_stmt|;
name|bodyField
operator|.
name|setValue
argument_list|(
name|tuple
index|[
name|BODY
index|]
argument_list|)
expr_stmt|;
name|idField
operator|.
name|setValue
argument_list|(
name|tuple
index|[
name|ID
index|]
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
DECL|method|getDocState
specifier|private
name|DocState
name|getDocState
parameter_list|()
block|{
name|DocState
name|ds
init|=
operator|(
name|DocState
operator|)
name|docState
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|==
literal|null
condition|)
block|{
name|ds
operator|=
operator|new
name|DocState
argument_list|()
expr_stmt|;
name|docState
operator|.
name|set
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
return|return
name|ds
return|;
block|}
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|tuple
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|getDocState
argument_list|()
operator|.
name|setFields
argument_list|(
name|tuple
argument_list|)
return|;
block|}
block|}
end_class
end_unit
