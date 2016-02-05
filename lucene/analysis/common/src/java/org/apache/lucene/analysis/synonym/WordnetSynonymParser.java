begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
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
name|LineNumberReader
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
name|Arrays
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
name|util
operator|.
name|CharsRef
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
name|CharsRefBuilder
import|;
end_import
begin_comment
comment|/**  * Parser for wordnet prolog format  *<p>  * See http://wordnet.princeton.edu/man/prologdb.5WN.html for a description of the format.  * @lucene.experimental  */
end_comment
begin_comment
comment|// TODO: allow you to specify syntactic categories (e.g. just nouns, etc)
end_comment
begin_class
DECL|class|WordnetSynonymParser
specifier|public
class|class
name|WordnetSynonymParser
extends|extends
name|SynonymMap
operator|.
name|Parser
block|{
DECL|field|expand
specifier|private
specifier|final
name|boolean
name|expand
decl_stmt|;
DECL|method|WordnetSynonymParser
specifier|public
name|WordnetSynonymParser
parameter_list|(
name|boolean
name|dedup
parameter_list|,
name|boolean
name|expand
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
name|dedup
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|expand
operator|=
name|expand
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|Reader
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|LineNumberReader
name|br
init|=
operator|new
name|LineNumberReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
name|String
name|lastSynSetID
init|=
literal|""
decl_stmt|;
name|CharsRef
name|synset
index|[]
init|=
operator|new
name|CharsRef
index|[
literal|8
index|]
decl_stmt|;
name|int
name|synsetSize
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|synSetID
init|=
name|line
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|11
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|synSetID
operator|.
name|equals
argument_list|(
name|lastSynSetID
argument_list|)
condition|)
block|{
name|addInternal
argument_list|(
name|synset
argument_list|,
name|synsetSize
argument_list|)
expr_stmt|;
name|synsetSize
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|synset
operator|.
name|length
operator|<=
name|synsetSize
operator|+
literal|1
condition|)
block|{
name|synset
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|synset
argument_list|,
name|synset
operator|.
name|length
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
name|synset
index|[
name|synsetSize
index|]
operator|=
name|parseSynonym
argument_list|(
name|line
argument_list|,
operator|new
name|CharsRefBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|synsetSize
operator|++
expr_stmt|;
name|lastSynSetID
operator|=
name|synSetID
expr_stmt|;
block|}
comment|// final synset in the file
name|addInternal
argument_list|(
name|synset
argument_list|,
name|synsetSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|ParseException
name|ex
init|=
operator|new
name|ParseException
argument_list|(
literal|"Invalid synonym rule at line "
operator|+
name|br
operator|.
name|getLineNumber
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|parseSynonym
specifier|private
name|CharsRef
name|parseSynonym
parameter_list|(
name|String
name|line
parameter_list|,
name|CharsRefBuilder
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
condition|)
block|{
name|reuse
operator|=
operator|new
name|CharsRefBuilder
argument_list|()
expr_stmt|;
block|}
name|int
name|start
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'\''
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|end
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|'\''
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|line
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|replace
argument_list|(
literal|"''"
argument_list|,
literal|"'"
argument_list|)
decl_stmt|;
return|return
name|analyze
argument_list|(
name|text
argument_list|,
name|reuse
argument_list|)
return|;
block|}
DECL|method|addInternal
specifier|private
name|void
name|addInternal
parameter_list|(
name|CharsRef
name|synset
index|[]
parameter_list|,
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|1
condition|)
block|{
return|return;
comment|// nothing to do
block|}
if|if
condition|(
name|expand
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|size
condition|;
name|j
operator|++
control|)
block|{
name|add
argument_list|(
name|synset
index|[
name|i
index|]
argument_list|,
name|synset
index|[
name|j
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|synset
index|[
name|i
index|]
argument_list|,
name|synset
index|[
literal|0
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
