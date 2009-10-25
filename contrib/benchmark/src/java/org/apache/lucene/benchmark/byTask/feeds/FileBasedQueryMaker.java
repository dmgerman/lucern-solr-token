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
name|queryParser
operator|.
name|QueryParser
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
name|queryParser
operator|.
name|ParseException
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
name|Query
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
name|NewAnalyzerTask
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
name|Version
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
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Create queries from a FileReader.  One per line, pass them through the  * QueryParser.  Lines beginning with # are treated as comments  *  * File can be specified as a absolute, relative or resource.  * Two properties can be set:  * file.query.maker.file=&lt;Full path to file containing queries&gt;  *<br/>  * file.query.maker.default.field=&lt;Name of default field - Default value is "body"&gt;  *  * Example:  * file.query.maker.file=c:/myqueries.txt  * file.query.maker.default.field=body  */
end_comment
begin_class
DECL|class|FileBasedQueryMaker
specifier|public
class|class
name|FileBasedQueryMaker
extends|extends
name|AbstractQueryMaker
implements|implements
name|QueryMaker
block|{
DECL|method|prepareQueries
specifier|protected
name|Query
index|[]
name|prepareQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|anlzr
init|=
name|NewAnalyzerTask
operator|.
name|createAnalyzer
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"analyzer"
argument_list|,
literal|"org.apache.lucene.analysis.standard.StandardAnalyzer"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|defaultField
init|=
name|config
operator|.
name|get
argument_list|(
literal|"file.query.maker.default.field"
argument_list|,
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|defaultField
argument_list|,
name|anlzr
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|qq
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"file.query.maker.file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//see if we can find it as a resource
name|InputStream
name|asStream
init|=
name|FileBasedQueryMaker
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|asStream
operator|!=
literal|null
condition|)
block|{
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|asStream
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|BufferedReader
name|buffered
init|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|int
name|lineNum
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|buffered
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|line
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
try|try
block|{
name|query
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" occurred while parsing line: "
operator|+
name|lineNum
operator|+
literal|" Text: "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
name|qq
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
name|lineNum
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"No Reader available for: "
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|qq
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
name|qq
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class
end_unit
