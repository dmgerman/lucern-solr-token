begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.nl
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|nl
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|LineNumberReader
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
begin_comment
comment|/**  *<p/>  *         Loads a text file and adds every line as an entry to a Hashtable. Every line  *         should contain only one word. If the file is not found or on any error, an  *         empty table is returned.  *           * @author Gerhard Schwarz  * @deprecated use {@link org.apache.lucene.analysis.WordlistLoader} instead  */
end_comment
begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
comment|/**    * @param path     Path to the wordlist    * @param wordfile Name of the wordlist    * @deprecated use {@link org.apache.lucene.analysis.WordlistLoader#getWordSet(File)} instead    */
DECL|method|getWordtable
specifier|public
specifier|static
name|HashMap
name|getWordtable
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|wordfile
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|wordfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
return|return
name|getWordtable
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|wordfile
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param wordfile Complete path to the wordlist    * @deprecated use {@link org.apache.lucene.analysis.WordlistLoader#getWordSet(File)} instead    */
DECL|method|getWordtable
specifier|public
specifier|static
name|HashMap
name|getWordtable
parameter_list|(
name|String
name|wordfile
parameter_list|)
block|{
if|if
condition|(
name|wordfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
return|return
name|getWordtable
argument_list|(
operator|new
name|File
argument_list|(
name|wordfile
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Reads a stemsdictionary. Each line contains:    * word \t stem    * i.e. tab seperated)    *    * @return Stem dictionary that overrules, the stemming algorithm    * @deprecated use {@link org.apache.lucene.analysis.WordlistLoader#getStemDict(File)} instead    */
DECL|method|getStemDict
specifier|public
specifier|static
name|HashMap
name|getStemDict
parameter_list|(
name|File
name|wordstemfile
parameter_list|)
block|{
if|if
condition|(
name|wordstemfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
name|HashMap
name|result
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
try|try
block|{
name|LineNumberReader
name|lnr
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|wordstemfile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|String
index|[]
name|wordstem
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|lnr
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|wordstem
operator|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|wordstem
index|[
literal|0
index|]
argument_list|,
name|wordstem
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
return|return
name|result
return|;
block|}
comment|/**    * @param wordfile File containing the wordlist    * @deprecated use {@link org.apache.lucene.analysis.WordlistLoader#getWordSet(File)} instead    */
DECL|method|getWordtable
specifier|public
specifier|static
name|HashMap
name|getWordtable
parameter_list|(
name|File
name|wordfile
parameter_list|)
block|{
if|if
condition|(
name|wordfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
name|HashMap
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LineNumberReader
name|lnr
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|wordfile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|word
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|stopwords
init|=
operator|new
name|String
index|[
literal|100
index|]
decl_stmt|;
name|int
name|wordcount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|lnr
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|wordcount
operator|++
expr_stmt|;
if|if
condition|(
name|wordcount
operator|==
name|stopwords
operator|.
name|length
condition|)
block|{
name|String
index|[]
name|tmp
init|=
operator|new
name|String
index|[
name|stopwords
operator|.
name|length
operator|+
literal|50
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|stopwords
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|wordcount
argument_list|)
expr_stmt|;
name|stopwords
operator|=
name|tmp
expr_stmt|;
block|}
name|stopwords
index|[
name|wordcount
operator|-
literal|1
index|]
operator|=
name|word
expr_stmt|;
block|}
name|result
operator|=
name|makeWordTable
argument_list|(
name|stopwords
argument_list|,
name|wordcount
argument_list|)
expr_stmt|;
block|}
comment|// On error, use an empty table
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|result
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Builds the wordlist table.    *    * @param words  Word that where read    * @param length Amount of words that where read into<tt>words</tt>    */
DECL|method|makeWordTable
specifier|private
specifier|static
name|HashMap
name|makeWordTable
parameter_list|(
name|String
index|[]
name|words
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|HashMap
name|table
init|=
operator|new
name|HashMap
argument_list|(
name|length
argument_list|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|table
operator|.
name|put
argument_list|(
name|words
index|[
name|i
index|]
argument_list|,
name|words
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class
end_unit
