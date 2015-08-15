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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|InputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Properties
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
name|utils
operator|.
name|StreamUtils
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
begin_comment
comment|/**  * A {@link ContentSource} reading one line at a time as a  * {@link org.apache.lucene.document.Document} from a single file. This saves IO  * cost (over DirContentSource) of recursing through a directory and opening a  * new file for every document.<br>  * The expected format of each line is (arguments are separated by&lt;TAB&gt;):  *<i>title, date, body</i>. If a line is read in a different format, a  * {@link RuntimeException} will be thrown. In general, you should use this  * content source for files that were created with {@link WriteLineDocTask}.<br>  *<br>  * Config properties:  *<ul>  *<li>docs.file=&lt;path to the file&gt;  *<li>content.source.encoding - default to UTF-8.  *<li>line.parser - default to {@link HeaderLineParser} if a header line exists which differs   *     from {@link WriteLineDocTask#DEFAULT_FIELDS} and to {@link SimpleLineParser} otherwise.  *</ul>  */
end_comment
begin_class
DECL|class|LineDocSource
specifier|public
class|class
name|LineDocSource
extends|extends
name|ContentSource
block|{
comment|/** Reader of a single input line into {@link DocData}. */
DECL|class|LineParser
specifier|public
specifier|static
specifier|abstract
class|class
name|LineParser
block|{
DECL|field|header
specifier|protected
specifier|final
name|String
index|[]
name|header
decl_stmt|;
comment|/** Construct with the header       * @param header header line found in the input file, or null if none      */
DECL|method|LineParser
specifier|public
name|LineParser
parameter_list|(
name|String
index|[]
name|header
parameter_list|)
block|{
name|this
operator|.
name|header
operator|=
name|header
expr_stmt|;
block|}
comment|/** parse an input line and fill doc data appropriately */
DECL|method|parseLine
specifier|public
specifier|abstract
name|void
name|parseLine
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|line
parameter_list|)
function_decl|;
block|}
comment|/**     * {@link LineParser} which ignores the header passed to its constructor    * and assumes simply that field names and their order are the same     * as in {@link WriteLineDocTask#DEFAULT_FIELDS}     */
DECL|class|SimpleLineParser
specifier|public
specifier|static
class|class
name|SimpleLineParser
extends|extends
name|LineParser
block|{
DECL|method|SimpleLineParser
specifier|public
name|SimpleLineParser
parameter_list|(
name|String
index|[]
name|header
parameter_list|)
block|{
name|super
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseLine
specifier|public
name|void
name|parseLine
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|line
parameter_list|)
block|{
name|int
name|k1
init|=
literal|0
decl_stmt|;
name|int
name|k2
init|=
name|line
operator|.
name|indexOf
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|,
name|k1
argument_list|)
decl_stmt|;
if|if
condition|(
name|k2
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"line: ["
operator|+
name|line
operator|+
literal|"] is in an invalid format (missing: separator title::date)!"
argument_list|)
throw|;
block|}
name|docData
operator|.
name|setTitle
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|k1
argument_list|,
name|k2
argument_list|)
argument_list|)
expr_stmt|;
name|k1
operator|=
name|k2
operator|+
literal|1
expr_stmt|;
name|k2
operator|=
name|line
operator|.
name|indexOf
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|,
name|k1
argument_list|)
expr_stmt|;
if|if
condition|(
name|k2
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"line: ["
operator|+
name|line
operator|+
literal|"] is in an invalid format (missing: separator date::body)!"
argument_list|)
throw|;
block|}
name|docData
operator|.
name|setDate
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|k1
argument_list|,
name|k2
argument_list|)
argument_list|)
expr_stmt|;
name|k1
operator|=
name|k2
operator|+
literal|1
expr_stmt|;
name|k2
operator|=
name|line
operator|.
name|indexOf
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|,
name|k1
argument_list|)
expr_stmt|;
if|if
condition|(
name|k2
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"line: ["
operator|+
name|line
operator|+
literal|"] is in an invalid format (too many separators)!"
argument_list|)
throw|;
block|}
comment|// last one
name|docData
operator|.
name|setBody
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|k1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * {@link LineParser} which sets field names and order by     * the header - any header - of the lines file.    * It is less efficient than {@link SimpleLineParser} but more powerful.    */
DECL|class|HeaderLineParser
specifier|public
specifier|static
class|class
name|HeaderLineParser
extends|extends
name|LineParser
block|{
DECL|enum|FieldName
DECL|enum constant|NAME
DECL|enum constant|TITLE
DECL|enum constant|DATE
DECL|enum constant|BODY
DECL|enum constant|PROP
specifier|private
enum|enum
name|FieldName
block|{
name|NAME
block|,
name|TITLE
block|,
name|DATE
block|,
name|BODY
block|,
name|PROP
block|}
DECL|field|posToF
specifier|private
specifier|final
name|FieldName
index|[]
name|posToF
decl_stmt|;
DECL|method|HeaderLineParser
specifier|public
name|HeaderLineParser
parameter_list|(
name|String
index|[]
name|header
parameter_list|)
block|{
name|super
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|posToF
operator|=
operator|new
name|FieldName
index|[
name|header
operator|.
name|length
index|]
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
name|header
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|f
init|=
name|header
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|DocMaker
operator|.
name|NAME_FIELD
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|posToF
index|[
name|i
index|]
operator|=
name|FieldName
operator|.
name|NAME
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DocMaker
operator|.
name|TITLE_FIELD
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|posToF
index|[
name|i
index|]
operator|=
name|FieldName
operator|.
name|TITLE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DocMaker
operator|.
name|DATE_FIELD
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|posToF
index|[
name|i
index|]
operator|=
name|FieldName
operator|.
name|DATE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DocMaker
operator|.
name|BODY_FIELD
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|posToF
index|[
name|i
index|]
operator|=
name|FieldName
operator|.
name|BODY
expr_stmt|;
block|}
else|else
block|{
name|posToF
index|[
name|i
index|]
operator|=
name|FieldName
operator|.
name|PROP
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|parseLine
specifier|public
name|void
name|parseLine
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|line
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|int
name|k1
init|=
literal|0
decl_stmt|;
name|int
name|k2
decl_stmt|;
while|while
condition|(
operator|(
name|k2
operator|=
name|line
operator|.
name|indexOf
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|,
name|k1
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|>=
name|header
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"input line has invalid format: "
operator|+
operator|(
name|n
operator|+
literal|1
operator|)
operator|+
literal|" fields instead of "
operator|+
name|header
operator|.
name|length
operator|+
literal|" :: ["
operator|+
name|line
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|setDocDataField
argument_list|(
name|docData
argument_list|,
name|n
argument_list|,
name|line
operator|.
name|substring
argument_list|(
name|k1
argument_list|,
name|k2
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|n
expr_stmt|;
name|k1
operator|=
name|k2
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|!=
name|header
operator|.
name|length
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"input line has invalid format: "
operator|+
operator|(
name|n
operator|+
literal|1
operator|)
operator|+
literal|" fields instead of "
operator|+
name|header
operator|.
name|length
operator|+
literal|" :: ["
operator|+
name|line
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// last one
name|setDocDataField
argument_list|(
name|docData
argument_list|,
name|n
argument_list|,
name|line
operator|.
name|substring
argument_list|(
name|k1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setDocDataField
specifier|private
name|void
name|setDocDataField
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|int
name|position
parameter_list|,
name|String
name|text
parameter_list|)
block|{
switch|switch
condition|(
name|posToF
index|[
name|position
index|]
condition|)
block|{
case|case
name|NAME
case|:
name|docData
operator|.
name|setName
argument_list|(
name|text
argument_list|)
expr_stmt|;
break|break;
case|case
name|TITLE
case|:
name|docData
operator|.
name|setTitle
argument_list|(
name|text
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|docData
operator|.
name|setDate
argument_list|(
name|text
argument_list|)
expr_stmt|;
break|break;
case|case
name|BODY
case|:
name|docData
operator|.
name|setBody
argument_list|(
name|text
argument_list|)
expr_stmt|;
break|break;
case|case
name|PROP
case|:
name|Properties
name|p
init|=
name|docData
operator|.
name|getProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setProps
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|setProperty
argument_list|(
name|header
index|[
name|position
index|]
argument_list|,
name|text
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|field|file
specifier|private
name|Path
name|file
decl_stmt|;
DECL|field|reader
specifier|private
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|readCount
specifier|private
name|int
name|readCount
decl_stmt|;
DECL|field|docDataLineReader
specifier|private
name|LineParser
name|docDataLineReader
init|=
literal|null
decl_stmt|;
DECL|field|skipHeaderLine
specifier|private
name|boolean
name|skipHeaderLine
init|=
literal|false
decl_stmt|;
DECL|method|openFile
specifier|private
specifier|synchronized
name|void
name|openFile
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|InputStream
name|is
init|=
name|StreamUtils
operator|.
name|inputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|encoding
argument_list|)
argument_list|,
name|StreamUtils
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipHeaderLine
condition|)
block|{
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
comment|// skip one line - the header line - already handled that info
block|}
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
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
specifier|final
name|String
name|line
decl_stmt|;
specifier|final
name|int
name|myID
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|line
operator|=
name|reader
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
if|if
condition|(
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
comment|// Reset the file
name|openFile
argument_list|()
expr_stmt|;
return|return
name|getNextDocData
argument_list|(
name|docData
argument_list|)
return|;
block|}
if|if
condition|(
name|docDataLineReader
operator|==
literal|null
condition|)
block|{
comment|// first line ever, one time initialization,
name|docDataLineReader
operator|=
name|createDocDataLineReader
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipHeaderLine
condition|)
block|{
return|return
name|getNextDocData
argument_list|(
name|docData
argument_list|)
return|;
block|}
block|}
comment|// increment IDS only once...
name|myID
operator|=
name|readCount
operator|++
expr_stmt|;
block|}
comment|// The date String was written in the format of DateTools.dateToString.
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setID
argument_list|(
name|myID
argument_list|)
expr_stmt|;
name|docDataLineReader
operator|.
name|parseLine
argument_list|(
name|docData
argument_list|,
name|line
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
DECL|method|createDocDataLineReader
specifier|private
name|LineParser
name|createDocDataLineReader
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|String
index|[]
name|header
decl_stmt|;
name|String
name|headIndicator
init|=
name|WriteLineDocTask
operator|.
name|FIELDS_HEADER_INDICATOR
operator|+
name|WriteLineDocTask
operator|.
name|SEP
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|headIndicator
argument_list|)
condition|)
block|{
name|header
operator|=
name|line
operator|.
name|substring
argument_list|(
name|headIndicator
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|split
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
argument_list|)
expr_stmt|;
name|skipHeaderLine
operator|=
literal|true
expr_stmt|;
comment|// mark to skip the header line when input file is reopened
block|}
else|else
block|{
name|header
operator|=
name|WriteLineDocTask
operator|.
name|DEFAULT_FIELDS
expr_stmt|;
block|}
comment|// if a specific DocDataLineReader was configured, must respect it
name|String
name|docDataLineReaderClassName
init|=
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"line.parser"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|docDataLineReaderClassName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|LineParser
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|docDataLineReaderClassName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|LineParser
operator|.
name|class
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|LineParser
argument_list|>
name|cnstr
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|String
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
return|return
name|cnstr
operator|.
name|newInstance
argument_list|(
operator|(
name|Object
operator|)
name|header
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to instantiate "
operator|+
name|docDataLineReaderClassName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// if this the simple case,
if|if
condition|(
name|Arrays
operator|.
name|deepEquals
argument_list|(
name|header
argument_list|,
name|WriteLineDocTask
operator|.
name|DEFAULT_FIELDS
argument_list|)
condition|)
block|{
return|return
operator|new
name|SimpleLineParser
argument_list|(
name|header
argument_list|)
return|;
block|}
return|return
operator|new
name|HeaderLineParser
argument_list|(
name|header
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|openFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|String
name|fileName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"docs.file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docs.file must be set"
argument_list|)
throw|;
block|}
name|file
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
expr_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
name|encoding
operator|=
name|IOUtils
operator|.
name|UTF_8
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
