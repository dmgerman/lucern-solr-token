begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|Iterator
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
comment|/**  * A SinkTokenizer can be used to cache Tokens for use in an Analyzer  *  * @see TeeTokenFilter  *  **/
end_comment
begin_class
DECL|class|SinkTokenizer
specifier|public
class|class
name|SinkTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|lst
specifier|protected
name|List
comment|/*<Token>*/
name|lst
init|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
decl_stmt|;
DECL|field|iter
specifier|protected
name|Iterator
comment|/*<Token>*/
name|iter
decl_stmt|;
DECL|method|SinkTokenizer
specifier|public
name|SinkTokenizer
parameter_list|(
name|List
comment|/*<Token>*/
name|input
parameter_list|)
block|{
name|this
operator|.
name|lst
operator|=
name|input
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|lst
operator|==
literal|null
condition|)
name|this
operator|.
name|lst
operator|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
expr_stmt|;
block|}
DECL|method|SinkTokenizer
specifier|public
name|SinkTokenizer
parameter_list|()
block|{
name|this
operator|.
name|lst
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
DECL|method|SinkTokenizer
specifier|public
name|SinkTokenizer
parameter_list|(
name|int
name|initCap
parameter_list|)
block|{
name|this
operator|.
name|lst
operator|=
operator|new
name|ArrayList
argument_list|(
name|initCap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the tokens in the internal List.    *<p/>    * WARNING: Adding tokens to this list requires the {@link #reset()} method to be called in order for them    * to be made available.  Also, this Tokenizer does nothing to protect against {@link java.util.ConcurrentModificationException}s    * in the case of adds happening while {@link #next(org.apache.lucene.analysis.Token)} is being called.    *    * @return A List of {@link org.apache.lucene.analysis.Token}s    */
DECL|method|getTokens
specifier|public
name|List
comment|/*<Token>*/
name|getTokens
parameter_list|()
block|{
return|return
name|lst
return|;
block|}
comment|/**    * Ignores the input result Token and returns the next token out of the list of cached tokens    * @param result The input token    * @return The next {@link org.apache.lucene.analysis.Token} in the Sink.    * @throws IOException    */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
name|Token
name|result
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
name|iter
operator|=
name|lst
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
name|iter
operator|.
name|hasNext
argument_list|()
condition|?
operator|(
name|Token
operator|)
name|iter
operator|.
name|next
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**    * Override this method to cache only certain tokens, or new tokens based    * on the old tokens.    *    * @param t The {@link org.apache.lucene.analysis.Token} to add to the sink    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return;
name|lst
operator|.
name|add
argument_list|(
operator|(
name|Token
operator|)
name|t
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reset the internal data structures to the start at the front of the list of tokens.  Should be called    * if tokens were added to the list after an invocation of {@link #next(Token)}    * @throws IOException    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|iter
operator|=
name|lst
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
