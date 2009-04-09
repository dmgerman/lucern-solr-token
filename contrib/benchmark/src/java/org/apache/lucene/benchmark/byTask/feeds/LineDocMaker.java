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
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
operator|.
name|WriteLineDocTask
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
name|FileInputStream
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
name|util
operator|.
name|Random
import|;
end_import
begin_comment
comment|/**  * A DocMaker reading one line at a time as a Document from  * a single file.  This saves IO cost (over DirDocMaker) of  * recursing through a directory and opening a new file for  * every document.  It also re-uses its Document and Field  * instance to improve indexing speed.  *  * Config properties:  * docs.file=&lt;path to the file%gt;  * doc.reuse.fields=true|false (default true)  * doc.random.id.limit=N (default -1) -- create random  *   docid in the range 0..N; this is useful  *   with UpdateDoc to test updating random documents; if  *   this is unspecified or -1, then docid is sequentially  *   assigned  */
end_comment
begin_class
DECL|class|LineDocMaker
specifier|public
class|class
name|LineDocMaker
extends|extends
name|BasicDocMaker
block|{
DECL|field|fileIS
name|FileInputStream
name|fileIS
decl_stmt|;
DECL|field|fileIn
name|BufferedReader
name|fileIn
decl_stmt|;
DECL|field|docState
name|ThreadLocal
name|docState
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
DECL|field|READER_BUFFER_BYTES
specifier|private
specifier|static
name|int
name|READER_BUFFER_BYTES
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|localDocState
specifier|private
specifier|final
name|DocState
name|localDocState
init|=
operator|new
name|DocState
argument_list|()
decl_stmt|;
DECL|field|doReuseFields
specifier|private
name|boolean
name|doReuseFields
init|=
literal|true
decl_stmt|;
DECL|field|r
specifier|private
name|Random
name|r
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|class|DocState
class|class
name|DocState
block|{
DECL|field|doc
name|Document
name|doc
decl_stmt|;
DECL|field|bodyField
name|Field
name|bodyField
decl_stmt|;
DECL|field|titleField
name|Field
name|titleField
decl_stmt|;
DECL|field|dateField
name|Field
name|dateField
decl_stmt|;
DECL|field|idField
name|Field
name|idField
decl_stmt|;
DECL|method|DocState
specifier|public
name|DocState
parameter_list|()
block|{
name|bodyField
operator|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|BODY_FIELD
argument_list|,
literal|""
argument_list|,
name|storeVal
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|termVecVal
argument_list|)
expr_stmt|;
name|titleField
operator|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|TITLE_FIELD
argument_list|,
literal|""
argument_list|,
name|storeVal
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|termVecVal
argument_list|)
expr_stmt|;
name|dateField
operator|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|DATE_FIELD
argument_list|,
literal|""
argument_list|,
name|storeVal
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|termVecVal
argument_list|)
expr_stmt|;
name|idField
operator|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|ID_FIELD
argument_list|,
literal|""
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
name|NOT_ANALYZED_NO_NORMS
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bodyField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dateField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
block|}
DECL|field|SEP
specifier|final
specifier|static
name|String
name|SEP
init|=
name|WriteLineDocTask
operator|.
name|SEP
decl_stmt|;
DECL|field|numDocsCreated
specifier|private
name|int
name|numDocsCreated
decl_stmt|;
DECL|method|incrNumDocsCreated
specifier|private
specifier|synchronized
name|int
name|incrNumDocsCreated
parameter_list|()
block|{
return|return
name|numDocsCreated
operator|++
return|;
block|}
DECL|method|setFields
specifier|public
name|Document
name|setFields
parameter_list|(
name|String
name|line
parameter_list|)
block|{
comment|// title<TAB> date<TAB> body<NEWLINE>
specifier|final
name|String
name|title
decl_stmt|,
name|date
decl_stmt|,
name|body
decl_stmt|;
name|int
name|spot
init|=
name|line
operator|.
name|indexOf
argument_list|(
name|SEP
argument_list|)
decl_stmt|;
if|if
condition|(
name|spot
operator|!=
operator|-
literal|1
condition|)
block|{
name|title
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|spot
argument_list|)
expr_stmt|;
name|int
name|spot2
init|=
name|line
operator|.
name|indexOf
argument_list|(
name|SEP
argument_list|,
literal|1
operator|+
name|spot
argument_list|)
decl_stmt|;
if|if
condition|(
name|spot2
operator|!=
operator|-
literal|1
condition|)
block|{
name|date
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|spot
argument_list|,
name|spot2
argument_list|)
expr_stmt|;
name|body
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|spot2
argument_list|,
name|line
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
name|date
operator|=
name|body
operator|=
literal|""
expr_stmt|;
block|}
else|else
name|title
operator|=
name|date
operator|=
name|body
operator|=
literal|""
expr_stmt|;
specifier|final
name|String
name|docID
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|docID
operator|=
literal|"doc"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docID
operator|=
literal|"doc"
operator|+
name|incrNumDocsCreated
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doReuseFields
condition|)
block|{
name|idField
operator|.
name|setValue
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|titleField
operator|.
name|setValue
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|dateField
operator|.
name|setValue
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|bodyField
operator|.
name|setValue
argument_list|(
name|body
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
else|else
block|{
name|Field
name|localIDField
init|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|ID_FIELD
argument_list|,
name|docID
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
name|NOT_ANALYZED_NO_NORMS
argument_list|)
decl_stmt|;
name|Field
name|localTitleField
init|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|TITLE_FIELD
argument_list|,
name|title
argument_list|,
name|storeVal
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|Field
name|localBodyField
init|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|body
argument_list|,
name|storeVal
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|Field
name|localDateField
init|=
operator|new
name|Field
argument_list|(
name|BasicDocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|date
argument_list|,
name|storeVal
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|Document
name|localDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|localDoc
operator|.
name|add
argument_list|(
name|localIDField
argument_list|)
expr_stmt|;
name|localDoc
operator|.
name|add
argument_list|(
name|localBodyField
argument_list|)
expr_stmt|;
name|localDoc
operator|.
name|add
argument_list|(
name|localTitleField
argument_list|)
expr_stmt|;
name|localDoc
operator|.
name|add
argument_list|(
name|localDateField
argument_list|)
expr_stmt|;
return|return
name|localDoc
return|;
block|}
block|}
block|}
DECL|method|getNextDocData
specifier|protected
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
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
name|line
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|line
operator|=
name|fileIn
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
comment|// Reset the file
name|openFile
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|forever
condition|)
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|doReuseFields
condition|)
return|return
name|getDocState
argument_list|()
operator|.
name|setFields
argument_list|(
name|line
argument_list|)
return|;
else|else
return|return
name|localDocState
operator|.
name|setFields
argument_list|(
name|line
argument_list|)
return|;
block|}
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot change document size with LineDocMaker; please use DirDocMaker instead"
argument_list|)
throw|;
block|}
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|fileName
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"docs.file"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"docs.file must be set"
argument_list|)
throw|;
name|openFile
argument_list|()
expr_stmt|;
block|}
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|doReuseFields
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.reuse.fields"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|numDocs
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.random.id.limit"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDocs
operator|!=
operator|-
literal|1
condition|)
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
literal|179
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|openFile
specifier|synchronized
name|void
name|openFile
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|fileIn
operator|!=
literal|null
condition|)
name|fileIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileIS
operator|=
operator|new
name|FileInputStream
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|fileIn
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fileIS
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
name|READER_BUFFER_BYTES
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|numUniqueTexts
specifier|public
name|int
name|numUniqueTexts
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class
end_unit
