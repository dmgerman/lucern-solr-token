begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|tasks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|compress
operator|.
name|compressors
operator|.
name|CompressorStreamFactory
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
name|PerfRunData
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
name|feeds
operator|.
name|DocMaker
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
begin_comment
comment|/**  * A task which writes documents, one line per document. Each line is in the  * following format: title&lt;TAB&gt; date&lt;TAB&gt; body. The output of this  * taske can be consumed by  * {@link org.apache.lucene.benchmark.byTask.feeds.LineDocMaker} and is intended  * to save the IO overhead of opening a file per doument to be indexed.<br>  * Supports the following parameters:  *<ul>  *<li>line.file.out - the name of the file to write the output to. That  * parameter is mandatory.<b>NOTE:</b> the file is re-created.  *<li>bzip.compression - whether the output should be bzip-compressed. This is  * recommended when the output file is expected to be large. (optional, default:  * false).  *</ul>  *<b>NOTE:</b> this class is not thread-safe and if used by multiple threads the  * output is unspecified (as all will write to the same ouput file in a  * non-synchronized way).  */
end_comment
begin_class
DECL|class|WriteLineDocTask
specifier|public
class|class
name|WriteLineDocTask
extends|extends
name|PerfTask
block|{
DECL|field|SEP
specifier|public
specifier|final
specifier|static
name|char
name|SEP
init|=
literal|'\t'
decl_stmt|;
DECL|field|NORMALIZER
specifier|private
specifier|static
specifier|final
name|Matcher
name|NORMALIZER
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\t\r\n]+"
argument_list|)
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|docSize
specifier|private
name|int
name|docSize
init|=
literal|0
decl_stmt|;
DECL|field|lineFileOut
specifier|private
name|BufferedWriter
name|lineFileOut
init|=
literal|null
decl_stmt|;
DECL|field|docMaker
specifier|private
name|DocMaker
name|docMaker
decl_stmt|;
DECL|method|WriteLineDocTask
specifier|public
name|WriteLineDocTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"line.file.out"
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
literal|"line.file.out must be set"
argument_list|)
throw|;
block|}
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|boolean
name|doBzipCompression
init|=
literal|false
decl_stmt|;
name|String
name|doBZCompress
init|=
name|config
operator|.
name|get
argument_list|(
literal|"bzip.compression"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|doBZCompress
operator|!=
literal|null
condition|)
block|{
comment|// Property was set, use the value.
name|doBzipCompression
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|doBZCompress
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Property was not set, attempt to detect based on file's extension
name|doBzipCompression
operator|=
name|fileName
operator|.
name|endsWith
argument_list|(
literal|"bz2"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doBzipCompression
condition|)
block|{
comment|// Wrap with BOS since BZip2CompressorOutputStream calls out.write(int)
comment|// and does not use the write(byte[]) version. This proved to speed the
comment|// compression process by 70% !
name|out
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|out
argument_list|,
literal|1
operator|<<
literal|16
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|CompressorStreamFactory
argument_list|()
operator|.
name|createCompressorOutputStream
argument_list|(
literal|"bzip2"
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|lineFileOut
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|1
operator|<<
literal|16
argument_list|)
expr_stmt|;
name|docMaker
operator|=
name|runData
operator|.
name|getDocMaker
argument_list|()
expr_stmt|;
block|}
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
return|return
literal|"Wrote "
operator|+
name|recsCount
operator|+
literal|" line docs"
return|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|docSize
operator|>
literal|0
condition|?
name|docMaker
operator|.
name|makeDocument
argument_list|(
name|docSize
argument_list|)
else|:
name|docMaker
operator|.
name|makeDocument
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
decl_stmt|;
name|String
name|body
init|=
name|f
operator|!=
literal|null
condition|?
name|NORMALIZER
operator|.
name|reset
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|)
else|:
literal|""
decl_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
expr_stmt|;
name|String
name|title
init|=
name|f
operator|!=
literal|null
condition|?
name|NORMALIZER
operator|.
name|reset
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|)
else|:
literal|""
decl_stmt|;
if|if
condition|(
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|||
name|title
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|DATE_FIELD
argument_list|)
expr_stmt|;
name|String
name|date
init|=
name|f
operator|!=
literal|null
condition|?
name|NORMALIZER
operator|.
name|reset
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|)
else|:
literal|""
decl_stmt|;
name|lineFileOut
operator|.
name|write
argument_list|(
name|title
argument_list|,
literal|0
argument_list|,
name|title
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|lineFileOut
operator|.
name|write
argument_list|(
name|SEP
argument_list|)
expr_stmt|;
name|lineFileOut
operator|.
name|write
argument_list|(
name|date
argument_list|,
literal|0
argument_list|,
name|date
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|lineFileOut
operator|.
name|write
argument_list|(
name|SEP
argument_list|)
expr_stmt|;
name|lineFileOut
operator|.
name|write
argument_list|(
name|body
argument_list|,
literal|0
argument_list|,
name|body
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|lineFileOut
operator|.
name|newLine
argument_list|()
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|lineFileOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the params (docSize only)    * @param params docSize, or 0 for no limit.    */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|supportsParams
argument_list|()
condition|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
name|docSize
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
