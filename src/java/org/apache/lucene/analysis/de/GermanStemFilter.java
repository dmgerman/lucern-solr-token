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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|TokenFilter
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
name|TokenStream
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
name|Hashtable
import|;
end_import
begin_comment
comment|/**  * A filter that stemms german words. It supports a table of words that should  * not be stemmed at all.  *  * @author    Gerhard Schwarz  * @version   $Id$  */
end_comment
begin_class
DECL|class|GermanStemFilter
specifier|public
specifier|final
class|class
name|GermanStemFilter
extends|extends
name|TokenFilter
block|{
comment|/** 	 * The actual token in the input stream. 	 */
DECL|field|token
specifier|private
name|Token
name|token
init|=
literal|null
decl_stmt|;
DECL|field|stemmer
specifier|private
name|GermanStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Hashtable
name|exclusions
init|=
literal|null
decl_stmt|;
DECL|method|GermanStemFilter
specifier|public
name|GermanStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|stemmer
operator|=
operator|new
name|GermanStemmer
argument_list|()
expr_stmt|;
name|input
operator|=
name|in
expr_stmt|;
block|}
comment|/** 	 * Builds a GermanStemFilter that uses an exclusiontable. 	 */
DECL|method|GermanStemFilter
specifier|public
name|GermanStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Hashtable
name|exclusiontable
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|exclusions
operator|=
name|exclusions
expr_stmt|;
block|}
comment|/** 	 * @return  Returns the next token in the stream, or null at EOS. 	 */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Check the exclusiontable.
elseif|else
if|if
condition|(
name|exclusions
operator|!=
literal|null
operator|&&
name|exclusions
operator|.
name|contains
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|token
return|;
block|}
else|else
block|{
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
decl_stmt|;
comment|// If not stemmed, dont waste the time creating a new token.
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Token
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|token
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
return|return
name|token
return|;
block|}
block|}
block|}
end_class
end_unit
