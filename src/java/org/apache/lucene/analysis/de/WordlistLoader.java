begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package
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
name|Hashtable
import|;
end_import
begin_comment
comment|/**  * Loads a textfile and adds every entry to a Hashtable. If a file is not found  * or on any error, an empty table is returned.  *  * @author    Gerhard Schwarz  * @version   $Id$  */
end_comment
begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
comment|/** 	 * @param path      Path to the wordlist. 	 * @param wordfile  Name of the wordlist. 	 */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
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
name|Hashtable
argument_list|()
return|;
block|}
name|File
name|absoluteName
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|wordfile
argument_list|)
decl_stmt|;
return|return
name|getWordtable
argument_list|(
name|absoluteName
argument_list|)
return|;
block|}
comment|/** 	 * @param wordfile  Complete path to the wordlist 	 */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
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
name|Hashtable
argument_list|()
return|;
block|}
name|File
name|absoluteName
init|=
operator|new
name|File
argument_list|(
name|wordfile
argument_list|)
decl_stmt|;
return|return
name|getWordtable
argument_list|(
name|absoluteName
argument_list|)
return|;
block|}
comment|/** 	 * @param wordfile  File containing the wordlist. 	 */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
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
name|Hashtable
argument_list|()
return|;
block|}
name|Hashtable
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
comment|// On error, use an empty table.
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|result
operator|=
operator|new
name|Hashtable
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Builds the wordlist table. 	 * 	 * @param words   Word that where read. 	 * @param length  Amount of words that where read into<tt>words</tt>. 	 */
DECL|method|makeWordTable
specifier|private
specifier|static
name|Hashtable
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
name|Hashtable
name|table
init|=
operator|new
name|Hashtable
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
