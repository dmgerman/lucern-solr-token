begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.quality.trec
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
operator|.
name|trec
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|quality
operator|.
name|QualityQuery
import|;
end_import
begin_comment
comment|/**  * Read TREC topics.  *<p>  * Expects this topic format -  *<pre>  *&lt;top&gt;  *&lt;num&gt; Number: nnn  *       *&lt;title&gt; title of the topic  *       *&lt;desc&gt; Description:  *   description of the topic  *       *&lt;narr&gt; Narrative:  *   "story" composed by assessors.  *      *&lt;/top&gt;  *</pre>  * Comment lines starting with '#' are ignored.  */
end_comment
begin_class
DECL|class|TrecTopicsReader
specifier|public
class|class
name|TrecTopicsReader
block|{
DECL|field|newline
specifier|private
specifier|static
specifier|final
name|String
name|newline
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|/**    *  Constructor for Trec's TopicsReader    */
DECL|method|TrecTopicsReader
specifier|public
name|TrecTopicsReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Read quality queries from trec format topics file.    * @param reader where queries are read from.    * @return the result quality queries.    * @throws IOException if cannot read the queries.    */
DECL|method|readQueries
specifier|public
name|QualityQuery
index|[]
name|readQueries
parameter_list|(
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|QualityQuery
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|QualityQuery
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuffer
name|sb
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|null
operator|!=
operator|(
name|sb
operator|=
name|read
argument_list|(
name|reader
argument_list|,
literal|"<top>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|)
condition|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// id
name|sb
operator|=
name|read
argument_list|(
name|reader
argument_list|,
literal|"<num>"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|k
init|=
name|sb
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|sb
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// title
name|sb
operator|=
name|read
argument_list|(
name|reader
argument_list|,
literal|"<title>"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|k
operator|=
name|sb
operator|.
name|indexOf
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|String
name|title
init|=
name|sb
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// description
name|sb
operator|=
name|read
argument_list|(
name|reader
argument_list|,
literal|"<desc>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sb
operator|=
name|read
argument_list|(
name|reader
argument_list|,
literal|"<narr>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|descripion
init|=
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// we got a topic!
name|fields
operator|.
name|put
argument_list|(
literal|"title"
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
literal|"description"
argument_list|,
name|descripion
argument_list|)
expr_stmt|;
name|QualityQuery
name|topic
init|=
operator|new
name|QualityQuery
argument_list|(
name|id
argument_list|,
name|fields
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|topic
argument_list|)
expr_stmt|;
comment|// skip narrative, get to end of doc
name|read
argument_list|(
name|reader
argument_list|,
literal|"</top>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
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
comment|// sort result array (by ID)
name|QualityQuery
name|qq
index|[]
init|=
name|res
operator|.
name|toArray
argument_list|(
operator|new
name|QualityQuery
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|qq
argument_list|)
expr_stmt|;
return|return
name|qq
return|;
block|}
comment|// read until finding a line that starts with the specified prefix
DECL|method|read
specifier|private
name|StringBuffer
name|read
parameter_list|(
name|BufferedReader
name|reader
parameter_list|,
name|String
name|prefix
parameter_list|,
name|StringBuffer
name|sb
parameter_list|,
name|boolean
name|collectMatchLine
parameter_list|,
name|boolean
name|collectAll
parameter_list|)
throws|throws
name|IOException
block|{
name|sb
operator|=
operator|(
name|sb
operator|==
literal|null
condition|?
operator|new
name|StringBuffer
argument_list|()
else|:
name|sb
operator|)
expr_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
if|if
condition|(
name|collectMatchLine
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
operator|+
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|newline
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|collectAll
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
operator|+
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|newline
expr_stmt|;
block|}
block|}
comment|//System.out.println("read: "+sb);
return|return
name|sb
return|;
block|}
block|}
end_class
end_unit
